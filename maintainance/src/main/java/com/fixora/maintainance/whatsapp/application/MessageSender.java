package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.whatsapp.domain.model.OutboundMessage;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Sends outbound WhatsApp messages via Twilio.
 * Supports Text, Buttons, and List.
 */
@Service
public class MessageSender {

    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);

    private final String fromWhatsApp;
    private final String accountSid;
    private final String authToken;
    private final TwilioWhatsAppContentProperties whatsAppContent;

    public MessageSender(@Value("${twilio.sender-whatsapp-number:}") String fromWhatsApp,
                         @Value("${twilio.account-sid:}") String accountSid,
                         @Value("${twilio.auth-token:}") String authToken,
                         TwilioWhatsAppContentProperties whatsAppContent) {
        this.fromWhatsApp = ensureWhatsAppPrefix(fromWhatsApp);
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.whatsAppContent = whatsAppContent;
    }

    public void sendAll(List<OutboundMessage> messages) {
        com.twilio.Twilio.init(accountSid, authToken);
        for (OutboundMessage msg : messages) {
            sendOne(msg);
        }
    }

    private void sendOne(OutboundMessage msg) {
        String to = ensureWhatsAppPrefix(msg.getTo());
        log.info("[WHATSAPP_DEBUG] outbound send type={} to={} preview={}",
                outboundType(msg), to, outboundPreview(msg));
        if (msg instanceof OutboundMessage.Text t) {
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromWhatsApp),
                    t.body()
            ).create();
        } else if (msg instanceof OutboundMessage.Buttons b) {
            sendButtons(b);
        } else if (msg instanceof OutboundMessage.ListMessage l) {
            sendList(to, l.body(), l.sections());
        }
    }

    /**
     * Real quick-reply buttons: set {@code twilio.whatsapp.content.*-buttons-sid} in config
     * to a Twilio Content template SID (Console → Content). Otherwise sends plain text fallback.
     */
    private void sendButtons(OutboundMessage.Buttons msg) {
        String to = ensureWhatsAppPrefix(msg.to());
        String contentSid = msg.twilioContentSid();
        if (contentSid != null && !contentSid.isBlank()) {
            String vars = msg.twilioContentVariables() != null ? msg.twilioContentVariables() : "{}";
            log.info("[WHATSAPP_DEBUG] outbound send type=ContentTemplate(quick-reply) to={} contentSid={}", to, contentSid.trim());
            String msid = whatsAppContent.getMessagingServiceSid();
            if (msid != null && !msid.isBlank()) {
                Message.creator(new PhoneNumber(to), msid.trim(), (String) null)
                        .setContentSid(contentSid.trim())
                        .setContentVariables(vars)
                        .create();
            } else {
                Message.creator(new PhoneNumber(to), new PhoneNumber(fromWhatsApp), (String) null)
                        .setContentSid(contentSid.trim())
                        .setContentVariables(vars)
                        .create();
            }
            return;
        }

        // MVP fallback: Twilio WhatsApp interactive buttons need Content templates; user replies with button id.
        StringBuilder sb = new StringBuilder(msg.body()).append("\n\n");
        for (OutboundMessage.Buttons.Button btn : msg.buttons()) {
            sb.append("• ").append(btn.title()).append(" - reply: ").append(btn.id()).append("\n");
        }
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromWhatsApp),
                sb.toString()
        ).create();
    }

    private void sendList(String to, String body, List<OutboundMessage.ListMessage.Section> sections) {
        // Twilio WhatsApp list requires pre-approved Content Template.
        // For MVP we send as text; user replies with row id (e.g. AC, PLUMBING).
        StringBuilder sb = new StringBuilder(body).append("\n\n");
        for (OutboundMessage.ListMessage.Section sec : sections) {
            sb.append(sec.title()).append(":\n");
            for (OutboundMessage.ListMessage.Row row : sec.rows()) {
                sb.append("• ").append(row.title());
                if (row.description() != null) sb.append(" - ").append(row.description());
                sb.append(" (reply: ").append(row.id()).append(")\n");
            }
        }
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromWhatsApp),
                sb.toString()
        ).create();
    }

    /**
     * Send template message (e.g. for technician notification).
     */
    public void sendTemplate(String to, String templateBody) {
        String toP = ensureWhatsAppPrefix(to);
        log.info("[WHATSAPP_DEBUG] outbound send type=Template to={} preview={}",
                toP, truncate(templateBody, 160));
        com.twilio.Twilio.init(accountSid, authToken);
        Message.creator(
                new PhoneNumber(toP),
                new PhoneNumber(fromWhatsApp),
                templateBody
        ).create();
    }

    private static String ensureWhatsAppPrefix(String phone) {
        if (phone == null || phone.isEmpty()) return phone;
        return phone.toLowerCase().startsWith("whatsapp:") ? phone : "whatsapp:" + phone;
    }

    private static String outboundType(OutboundMessage msg) {
        if (msg instanceof OutboundMessage.Text) return "Text";
        if (msg instanceof OutboundMessage.Buttons) return "Buttons";
        if (msg instanceof OutboundMessage.ListMessage) return "ListMessage";
        return msg.getClass().getSimpleName();
    }

    private static String outboundPreview(OutboundMessage msg) {
        if (msg instanceof OutboundMessage.Text t) {
            return truncate(t.body(), 160);
        }
        if (msg instanceof OutboundMessage.Buttons b) {
            return truncate(b.body(), 120) + " (buttons=" + b.buttons().size() + ")";
        }
        if (msg instanceof OutboundMessage.ListMessage l) {
            return truncate(l.body(), 120) + " (sections=" + l.sections().size() + ")";
        }
        return "";
    }

    private static String truncate(String s, int max) {
        if (s == null) return "null";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "…";
    }
}

