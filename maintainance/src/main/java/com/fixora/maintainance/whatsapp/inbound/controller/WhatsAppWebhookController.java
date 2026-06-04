package com.fixora.maintainance.whatsapp.inbound.controller;

import com.fixora.maintainance.whatsapp.application.MessageNormalizer;
import com.fixora.maintainance.whatsapp.application.MessageSender;
import com.fixora.maintainance.whatsapp.application.WhatsAppConversationRouter;
import com.fixora.maintainance.whatsapp.domain.model.InboundMessage;
import com.fixora.maintainance.whatsapp.domain.model.OutboundMessage;
import com.twilio.security.RequestValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * HTTP endpoint for Twilio WhatsApp inbound webhook.
 * Verifies signature, validates To, normalizes payload, calls Orchestrator, sends outbound.
 */
@RestController
@RequestMapping("/webhooks/whatsapp")
public class WhatsAppWebhookController {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    private final String authToken;
    private final String senderWhatsAppNumber;
    private final MessageNormalizer messageNormalizer;
    private final WhatsAppConversationRouter conversationRouter;
    private final MessageSender messageSender;

    public WhatsAppWebhookController(@Value("${twilio.auth-token:}") String authToken,
                                     @Value("${twilio.sender-whatsapp-number:}") String senderWhatsAppNumber,
                                     MessageNormalizer messageNormalizer,
                                     WhatsAppConversationRouter conversationRouter,
                                     MessageSender messageSender) {
        this.authToken = authToken;
        this.senderWhatsAppNumber = ensureWhatsAppPrefix(senderWhatsAppNumber);
        this.messageNormalizer = messageNormalizer;
        this.conversationRouter = conversationRouter;
        this.messageSender = messageSender;
    }

    @PostMapping("/inbound")
    public ResponseEntity<Void> handleInbound(HttpServletRequest request,
                                             @RequestParam Map<String, String> params) {
        String url = getRequestUrl(request);
        if (!verifySignature(url, params, request)) {
            log.warn("Twilio webhook signature verification failed");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String to = params.get("To");
        if (to == null || !to.trim().equalsIgnoreCase(senderWhatsAppNumber.trim())) {
            log.warn("Invalid To field: expected {}, got {}", senderWhatsAppNumber, to);
            // 200 = acknowledge webhook so Twilio does not retry; we intentionally skip processing.
            return ResponseEntity.ok().build();
        }

        try {
            log.info("[WHATSAPP_DEBUG] inbound webhook params (sorted): {}", new TreeMap<>(params));

            InboundMessage inbound = messageNormalizer.normalize(params);
            List<OutboundMessage> outbound = conversationRouter.handle(inbound);

            log.info("[WHATSAPP_DEBUG] orchestrator returned {} outbound message(s)", outbound.size());
            if (!outbound.isEmpty()) {
                messageSender.sendAll(outbound);
            }
        } catch (Exception e) {
            log.error("Error processing WhatsApp webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    private boolean verifySignature(String url, Map<String, String> params, HttpServletRequest request) {
        if (authToken == null || authToken.isEmpty()) {
            log.warn("Twilio auth token not configured - skipping signature verification");
            return true;
        }
        RequestValidator validator = new RequestValidator(authToken);
        String signature = request.getHeader("X-Twilio-Signature");
        if (signature == null || signature.isEmpty()) {
            return false;
        }
        return validator.validate(url, params, signature);
    }
// extracting the exact URL that client use to call the service
    private String getRequestUrl(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null) scheme = request.getScheme();
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null) host = request.getServerName();
        int port = request.getServerPort();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null) {
            return scheme + "://" + host + (port != 80 && port != 443 ? ":" + port : "") + path + "?" + query;
        }
        return scheme + "://" + host + (port != 80 && port != 443 ? ":" + port : "") + path;
    }

    private static String ensureWhatsAppPrefix(String phone) {
        if (phone == null || phone.isEmpty()) return phone;
        return phone.toLowerCase().startsWith("whatsapp:") ? phone : "whatsapp:" + phone;
    }
}

