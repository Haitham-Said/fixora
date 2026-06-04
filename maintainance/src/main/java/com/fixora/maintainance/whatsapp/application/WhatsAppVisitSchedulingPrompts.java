package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.maintainancerequest.domain.model.PreferredSlot;
import com.fixora.maintainance.whatsapp.domain.model.OutboundMessage;
import com.fixora.maintainance.whatsapp.domain.model.PreferredVisitDateOption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Outbound prompts for preferred visit date and time slot.
 * Optional Twilio Content SIDs enable real quick-reply buttons when set in config.
 */
@Component
public class WhatsAppVisitSchedulingPrompts {

    private final TwilioWhatsAppContentProperties content;

    public WhatsAppVisitSchedulingPrompts(TwilioWhatsAppContentProperties content) {
        this.content = content;
    }

    public OutboundMessage preferredDateButtons(String to) {
        List<OutboundMessage.Buttons.Button> buttons = new ArrayList<>();
        for (PreferredVisitDateOption o : PreferredVisitDateOption.values()) {
            buttons.add(new OutboundMessage.Buttons.Button(o.getId(), o.getButtonTitle()));
        }
        return new OutboundMessage.Buttons(
                to,
                "When would you prefer us to visit? (Preference only — we will confirm assignment shortly.)",
                buttons,
                blankToNull(content.getVisitDateButtonsSid()),
                content.getVisitDateButtonsVariables()
        );
    }

    public OutboundMessage preferredSlotButtons(String to) {
        List<OutboundMessage.Buttons.Button> buttons = new ArrayList<>();
        for (PreferredSlot slot : PreferredSlot.values()) {
            buttons.add(new OutboundMessage.Buttons.Button(slot.getWhatsAppId(), slot.getDisplayLabel()));
        }
        return new OutboundMessage.Buttons(
                to,
                "Preferred time of day? (Ranges: Morning / Afternoon / Evening — not a fixed clock time.)",
                buttons,
                blankToNull(content.getVisitSlotButtonsSid()),
                content.getVisitSlotButtonsVariables()
        );
    }

    public static OutboundMessage preferredDateRetry(String to) {
        return new OutboundMessage.Text(to,
                "Please choose one of the dates below by tapping an option or replying with the code shown.");
    }

    public static OutboundMessage preferredSlotRetry(String to) {
        return new OutboundMessage.Text(to,
                "Please choose Morning, Afternoon, or Evening using the options below (or reply with SLOT_MORNING, SLOT_AFTERNOON, SLOT_EVENING).");
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
