package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.whatsapp.domain.model.InboundMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts Twilio webhook payload into internal InboundMessage model.
 */
@Component
public class MessageNormalizer {

    private static final Logger log = LoggerFactory.getLogger(MessageNormalizer.class);

    public InboundMessage normalize(Map<String, String> twilioParams) {
        String messageSid = twilioParams.get("MessageSid");
        String from = normalizePhone(twilioParams.get("From"));
        String to = twilioParams.get("To");
        String body = twilioParams.get("Body");
        String numMedia = twilioParams.getOrDefault("NumMedia", "0");

        InboundMessage.InboundMessageBuilder builder = InboundMessage.builder()
                .providerMessageId(messageSid)
                .fromPhone(from)
                .toPhone(to);

        // Check for interactive (button/list) response
        String buttonPayload = twilioParams.get("ButtonPayload");
        String listPayload = twilioParams.get("ListReply");
        if (buttonPayload != null && !buttonPayload.isEmpty()) {
            builder.type(InboundMessage.MessageType.INTERACTIVE)
                    .interactive(InboundMessage.InteractivePayload.builder()
                            .kind("BUTTON")
                            .id(buttonPayload)
                            .title(buttonPayload)
                            .build())
                    .text(buttonPayload);
        } else if (listPayload != null && !listPayload.isEmpty()) {
            builder.type(InboundMessage.MessageType.INTERACTIVE)
                    .interactive(InboundMessage.InteractivePayload.builder()
                            .kind("LIST")
                            .id(listPayload)
                            .title(listPayload)
                            .build())
                    .text(listPayload);
        } else if (Integer.parseInt(numMedia) > 0) {
            List<InboundMessage.MediaItem> media = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(numMedia); i++) {
                String url = twilioParams.get("MediaUrl" + i);
                String contentType = twilioParams.get("MediaContentType" + i);
                if (url != null) {
                    media.add(InboundMessage.MediaItem.builder()
                            .url(url)
                            .contentType(contentType != null ? contentType : "application/octet-stream")
                            .build());
                }
            }
            builder.type(InboundMessage.MessageType.MEDIA)
                    .media(media)
                    .text(body);
        } else {
            builder.type(InboundMessage.MessageType.TEXT)
                    .text(body != null ? body.trim() : "");
        }

        InboundMessage built = builder.build();
        log.info("[WHATSAPP_DEBUG] normalized inbound: {}", summarizeInbound(built));
        return built;
    }

    private static String summarizeInbound(InboundMessage m) {
        StringBuilder sb = new StringBuilder();
        sb.append("messageSid=").append(m.getProviderMessageId());
        sb.append(", from=").append(m.getFromPhone());
        sb.append(", to=").append(m.getToPhone());
        sb.append(", type=").append(m.getType());
        sb.append(", text=").append(truncate(m.getText(), 200));
        if (m.getInteractive() != null) {
            sb.append(", interactiveKind=").append(m.getInteractive().getKind());
            sb.append(", interactiveId=").append(m.getInteractive().getId());
        }
        if (m.getMedia() != null && !m.getMedia().isEmpty()) {
            sb.append(", mediaCount=").append(m.getMedia().size());
            sb.append(", mediaUrls=").append(m.getMedia().stream()
                    .map(mi -> truncate(mi.getUrl(), 80))
                    .collect(Collectors.joining(" | ")));
        }
        return sb.toString();
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "null";
        }
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "…";
    }

    /**
     * Normalize phone: strip whatsapp: prefix, keep E.164 format for lookup.
     */
    public static String normalizePhone(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        if (s.toLowerCase().startsWith("whatsapp:")) {
            s = s.substring(9).trim();
        }
        return s;
    }
}

