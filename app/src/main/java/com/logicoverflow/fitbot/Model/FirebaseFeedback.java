package com.logicoverflow.fitbot.Model;

import java.io.Serializable;

public class FirebaseFeedback implements Serializable {

    private int rating ;
    private String feedbackMessage;

    public FirebaseFeedback(int rating, String feedbackMessage) {
        this.rating = rating;
        this.feedbackMessage = feedbackMessage;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }
}
