package com.logicoverflow.fitbot.Model;

import java.io.Serializable;

public class FirebaseMessage implements Serializable {

    private String message;
    private String answer;
    private boolean isAnswered;

    public FirebaseMessage(String message, String answer, boolean isAnswered) {
        this.message = message;
        this.answer = answer;
        this.isAnswered = isAnswered;
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
}
