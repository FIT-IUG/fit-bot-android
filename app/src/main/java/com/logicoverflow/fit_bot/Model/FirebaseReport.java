package com.logicoverflow.fit_bot.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirebaseReport implements Serializable {
    private String userMessage;
    private String chatbotMessage;
    private String timestamp;

    public FirebaseReport(String userMessage, String chatbotMessage) {
        this.userMessage = userMessage;
        this.chatbotMessage = chatbotMessage;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH);
        this.timestamp = simpleDateFormat.format(new Date());
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
