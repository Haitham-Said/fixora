package com.fixora.maintainance.whatsapp.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InboundMessage {
    private String providerMessageId;
    private String fromPhone;
    private String toPhone;
    private MessageType type;
    private String text;
    private InteractivePayload interactive;
    private List<MediaItem> media;

    public enum MessageType {
        TEXT,
        INTERACTIVE,
        MEDIA
    }

    @Data
    @Builder
    public static class InteractivePayload {
        private String kind; // BUTTON | LIST
        private String id;
        private String title;
    }

    @Data
    @Builder
    public static class MediaItem {
        private String url;
        private String contentType;
    }
}

