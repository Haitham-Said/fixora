package com.fixora.maintainance.whatsapp.domain.model;

import java.util.List;

/**
 * Internal outbound message model for WhatsApp.
 * MessageSender maps these to Twilio WhatsApp API calls.
 */
public interface OutboundMessage {
    String getTo();

    record Text(String to, String body) implements OutboundMessage {
        @Override
        public String getTo() {
            return to;
        }
    }

    /**
     * @param twilioContentSid       when non-blank, {@link com.fixora.maintainance.whatsapp.application.MessageSender}
     *                               sends via Twilio Content API (real tap buttons).
     * @param twilioContentVariables JSON object string for template variables; use "{}" if none.
     */
    record Buttons(String to, String body, List<Button> buttons, String twilioContentSid, String twilioContentVariables) implements OutboundMessage {

        public Buttons(String to, String body, List<Button> buttons) {
            this(to, body, buttons, null, null);
        }

        public Buttons {
            if (twilioContentVariables == null || twilioContentVariables.isBlank()) {
                twilioContentVariables = "{}";
            }
        }

        @Override
        public String getTo() {
            return to;
        }

        public record Button(String id, String title) {}
    }

    record ListMessage(String to, String body, List<Section> sections) implements OutboundMessage {
        @Override
        public String getTo() {
            return to;
        }

        public record Section(String title, List<Row> rows) {}

        public record Row(String id, String title, String description) {}
    }
}
