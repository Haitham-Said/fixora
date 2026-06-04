package com.fixora.maintainance.maintainancerequest.domain.model;

public enum PreferredSlot {
    MORNING("SLOT_MORNING", "Morning"),
    AFTERNOON("SLOT_AFTERNOON", "Afternoon"),
    EVENING("SLOT_EVENING", "Evening");

    private final String whatsAppId;
    private final String displayLabel;

    PreferredSlot(String whatsAppId, String displayLabel) {
        this.whatsAppId = whatsAppId;
        this.displayLabel = displayLabel;
    }

    public String getWhatsAppId() {
        return whatsAppId;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    /**
     * Maps inbound interactive / reply id (e.g. SLOT_MORNING) to domain slot.
     */
    public static PreferredSlot fromWhatsAppId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String key = raw.trim().toUpperCase();
        for (PreferredSlot s : values()) {
            if (s.whatsAppId.equals(key) || s.name().equals(key)) {
                return s;
            }
        }
        return null;
    }
}

