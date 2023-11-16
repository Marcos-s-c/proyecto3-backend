package com.sistema.venus.clients.whatsapp.request;

import java.util.List;

public class WhatsAppComponent {
    private String type;
    private List<WhatsAppParameters> parameters;

    public WhatsAppComponent(String type, List<WhatsAppParameters> parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public WhatsAppComponent() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WhatsAppParameters> getParameters() {
        return parameters;
    }

    public void setParameters(List<WhatsAppParameters> parameters) {
        this.parameters = parameters;
    }
}
