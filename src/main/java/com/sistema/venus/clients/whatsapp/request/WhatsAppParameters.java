package com.sistema.venus.clients.whatsapp.request;

public class WhatsAppParameters {
    private String type;
    private String text;

    public WhatsAppParameters() {
    }

    public WhatsAppParameters(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
