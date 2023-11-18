package com.sistema.venus.clients.whatsapp.request;

public class WhatsAppMessageRequest {
    private String messaging_product;
    private String to;
    private String type;
    private WhatsAppMessageTemplate template;

    public WhatsAppMessageRequest() {
    }

    public WhatsAppMessageRequest(String messaging_product, String to, String type, WhatsAppMessageTemplate template) {
        this.messaging_product = messaging_product;
        this.to = to;
        this.type = type;
        this.template = template;
    }

    public String getMessaging_product() {
        return messaging_product;
    }

    public void setMessaging_product(String messaging_product) {
        this.messaging_product = messaging_product;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public WhatsAppMessageTemplate getTemplate() {
        return template;
    }

    public void setTemplate(WhatsAppMessageTemplate template) {
        this.template = template;
    }
}
