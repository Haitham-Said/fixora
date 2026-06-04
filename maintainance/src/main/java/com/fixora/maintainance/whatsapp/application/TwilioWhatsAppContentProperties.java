package com.fixora.maintainance.whatsapp.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Optional Twilio Content Template SIDs for real WhatsApp quick-reply buttons.
 * Create templates in Console → Messaging → Content → Template Builder, then paste SIDs here.
 * When a SID is blank, {@link MessageSender} falls back to plain text (MVP behavior).
 */
@ConfigurationProperties(prefix = "twilio.whatsapp.content")
public class TwilioWhatsAppContentProperties {

    /**
     * Content SID for welcome screen (Create Request / Track Request).
     */
    private String welcomeButtonsSid = "";

    /**
     * JSON for template variables, e.g. {"1":"Welcome text"} — must match your template.
     */
    private String welcomeButtonsVariables = "{}";

    private String visitDateButtonsSid = "";
    private String visitDateButtonsVariables = "{}";

    private String visitSlotButtonsSid = "";
    private String visitSlotButtonsVariables = "{}";

    /**
     * Optional. Some Twilio setups send WhatsApp templates via a Messaging Service instead of From.
     */
    private String messagingServiceSid = "";

    public String getWelcomeButtonsSid() {
        return welcomeButtonsSid;
    }

    public void setWelcomeButtonsSid(String welcomeButtonsSid) {
        this.welcomeButtonsSid = welcomeButtonsSid;
    }

    public String getWelcomeButtonsVariables() {
        return welcomeButtonsVariables;
    }

    public void setWelcomeButtonsVariables(String welcomeButtonsVariables) {
        this.welcomeButtonsVariables = welcomeButtonsVariables;
    }

    public String getVisitDateButtonsSid() {
        return visitDateButtonsSid;
    }

    public void setVisitDateButtonsSid(String visitDateButtonsSid) {
        this.visitDateButtonsSid = visitDateButtonsSid;
    }

    public String getVisitDateButtonsVariables() {
        return visitDateButtonsVariables;
    }

    public void setVisitDateButtonsVariables(String visitDateButtonsVariables) {
        this.visitDateButtonsVariables = visitDateButtonsVariables;
    }

    public String getVisitSlotButtonsSid() {
        return visitSlotButtonsSid;
    }

    public void setVisitSlotButtonsSid(String visitSlotButtonsSid) {
        this.visitSlotButtonsSid = visitSlotButtonsSid;
    }

    public String getVisitSlotButtonsVariables() {
        return visitSlotButtonsVariables;
    }

    public void setVisitSlotButtonsVariables(String visitSlotButtonsVariables) {
        this.visitSlotButtonsVariables = visitSlotButtonsVariables;
    }

    public String getMessagingServiceSid() {
        return messagingServiceSid;
    }

    public void setMessagingServiceSid(String messagingServiceSid) {
        this.messagingServiceSid = messagingServiceSid;
    }
}
