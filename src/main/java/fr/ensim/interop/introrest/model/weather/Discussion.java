package fr.ensim.interop.introrest.model.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Discussion {

    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("text")
    private String texte;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }
}
