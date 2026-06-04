package com.fixora.maintainance.whatsapp.domain.model;

/**
 * Stable category IDs for WhatsApp list selection.
 */
public enum TicketCategory {
    AC("AC", "Air Conditioning"),
    PLUMBING("PLUMBING", "Plumbing"),
    ELECTRICAL("ELECTRICAL", "Electrical"),
    CLEANING("CLEANING", "Cleaning"),
    OTHER("OTHER", "Other");

    private final String id;
    private final String displayName;

    TicketCategory(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TicketCategory fromId(String id) {
        if (id == null) return null;
        for (TicketCategory c : values()) {
            if (c.id.equalsIgnoreCase(id)) return c;
        }
        return null;
    }
}

