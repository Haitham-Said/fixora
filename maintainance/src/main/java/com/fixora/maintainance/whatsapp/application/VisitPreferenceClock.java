package com.fixora.maintainance.whatsapp.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Calendar "today" for tenant scheduling, using an optional configured timezone.
 * If {@code fixora.app.timezone} is blank, the JVM default zone is used.
 */
@Component
public class VisitPreferenceClock {

    private final ZoneId zoneId;

    public VisitPreferenceClock(@Value("${fixora.app.timezone:}") String timezone) {
        this.zoneId = timezone == null || timezone.isBlank()
                ? ZoneId.systemDefault()
                : ZoneId.of(timezone);
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public LocalDate today() {
        return LocalDate.now(zoneId);
    }
}
