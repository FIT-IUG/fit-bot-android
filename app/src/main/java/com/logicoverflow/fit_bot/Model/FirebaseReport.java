package com.logicoverflow.fit_bot.Model;

import java.io.Serializable;

public class FirebaseReport implements Serializable {
    private String userMessage;
    private String chatbotMessage;

    public FirebaseReport(String userMessage, String chatbotMessage) {
        this.userMessage = userMessage;
        this.chatbotMessage = chatbotMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getChatbotMessage() {
        return chatbotMessage;
    }

    public void setChatbotMessage(String chatbotMessage) {
        this.chatbotMessage = chatbotMessage;
    }

}
