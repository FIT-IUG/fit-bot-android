package com.logicoverflow.fit_bot.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirebaseMessage implements Serializable {

    private String message;
    private String answer;
    private boolean isAnswered;
    private String timestamp;

    public FirebaseMessage(String message, String answer, boolean isAnswered) {
        this.message = message;
        this.answer = answer;
        this.isAnswered = isAnswered;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH);
        this.timestamp = simpleDateFormat.format(new Date());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
