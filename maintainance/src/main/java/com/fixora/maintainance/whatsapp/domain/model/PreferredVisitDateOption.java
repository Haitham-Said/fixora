package com.fixora.maintainance.whatsapp.domain.model;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Stable IDs for WhatsApp date buttons (today / tomorrow / day after tomorrow).
 */
public enum PreferredVisitDateOption {
    DATE_TODAY("DATE_TODAY", "Today"),
    DATE_TOMORROW("DATE_TOMORROW", "Tomorrow"),
    DATE_DAY_AFTER_TOMORROW("DATE_DAY_AFTER_TOMORROW", "Day After Tomorrow");

    private final String id;
    private final String buttonTitle;

    PreferredVisitDateOption(String id, String buttonTitle) {
        this.id = id;
        this.buttonTitle = buttonTitle;
    }

    public String getId() {
        return id;
    }

    public String getButtonTitle() {
        return buttonTitle;
    }

    public static PreferredVisitDateOption fromId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String key = raw.trim().toUpperCase();
        for (PreferredVisitDateOption o : values()) {
            if (o.id.equals(key)) {
                return o;
            }
        }
        return null;
    }

    public LocalDate resolveDate(ZoneId zoneId) {
        LocalDate today = LocalDate.now(zoneId);
        return switch (this) {
            case DATE_TODAY -> today;
            case DATE_TOMORROW -> today.plusDays(1);
            case DATE_DAY_AFTER_TOMORROW -> today.plusDays(2);
        };
    }
}
