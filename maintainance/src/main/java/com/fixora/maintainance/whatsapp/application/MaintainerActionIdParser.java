package com.fixora.maintainance.whatsapp.application;

import org.springframework.stereotype.Component;

/**
 * Central parser for maintainer actions.
 *
 * Supported inputs:
 * - open / my open tickets
 * - inprogress / in progress / my in progress tickets
 * - {ticketId}
 * - start:{ticketId}
 * - complete:{ticketId}
 */
@Component
public class MaintainerActionIdParser {

    public ParsedAction parse(String raw) {
        if (raw == null || raw.isBlank()) return ParsedAction.invalid();
        String normalized = raw.trim().toLowerCase();
        if ("open".equals(normalized) || "my open tickets".equals(normalized)) {
            return ParsedAction.menuOpen();
        }
        if ("inprogress".equals(normalized) || "in progress".equals(normalized) || "my in progress tickets".equals(normalized)) {
            return ParsedAction.menuInProgress();
        }
        if (normalized.matches("\\d+")) {
            try {
                return ParsedAction.ticketSelected(Long.parseLong(normalized));
            } catch (NumberFormatException ignored) {
                return ParsedAction.invalid();
            }
        }

        if (normalized.startsWith("start:")) {
            try {
                return ParsedAction.ticketStart(Long.parseLong(normalized.substring("start:".length())));
            } catch (NumberFormatException ignored) {
                return ParsedAction.invalid();
            }
        }
        if (normalized.startsWith("complete:")) {
            try {
                return ParsedAction.ticketComplete(Long.parseLong(normalized.substring("complete:".length())));
            } catch (NumberFormatException ignored) {
                return ParsedAction.invalid();
            }
        }

        return ParsedAction.invalid();
    }

    public enum Kind {
        MENU_OPEN,
        MENU_IN_PROGRESS,
        TICKET_SELECTED,
        TICKET_START,
        TICKET_COMPLETE,
        INVALID
    }

    public record ParsedAction(Kind kind, Long ticketId) {
        static ParsedAction menuOpen() { return new ParsedAction(Kind.MENU_OPEN, null); }
        static ParsedAction menuInProgress() { return new ParsedAction(Kind.MENU_IN_PROGRESS, null); }
        static ParsedAction ticketSelected(Long id) { return new ParsedAction(Kind.TICKET_SELECTED, id); }
        static ParsedAction ticketStart(Long id) { return new ParsedAction(Kind.TICKET_START, id); }
        static ParsedAction ticketComplete(Long id) { return new ParsedAction(Kind.TICKET_COMPLETE, id); }
        static ParsedAction invalid() { return new ParsedAction(Kind.INVALID, null); }
    }
}

