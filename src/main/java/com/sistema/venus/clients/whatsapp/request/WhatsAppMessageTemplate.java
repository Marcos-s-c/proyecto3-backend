package com.sistema.venus.clients.whatsapp.request;

import java.util.List;

public class WhatsAppMessageTemplate {
    private String name;
    private WhatsAppLanguage language;

    private List<WhatsAppComponent> components;

    public WhatsAppMessageTemplate() {
    }

    public WhatsAppMessageTemplate(String name, WhatsAppLanguage language, List<WhatsAppComponent> components) {
        this.name = name;
        this.language = language;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WhatsAppLanguage getLanguage() {
        return language;
    }

    public void setLanguage(WhatsAppLanguage language) {
        this.language = language;
    }

    public List<WhatsAppComponent> getComponents() {
        return components;
    }

    public void setComponents(List<WhatsAppComponent> components) {
        this.components = components;
    }
}
