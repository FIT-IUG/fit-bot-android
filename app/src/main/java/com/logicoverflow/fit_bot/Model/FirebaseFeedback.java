package com.logicoverflow.fit_bot.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirebaseFeedback implements Serializable {

    private int rating ;
    private String feedbackMessage;
    private String timestamp;

    public FirebaseFeedback(int rating, String feedbackMessage) {
        this.rating = rating;
        this.feedbackMessage = feedbackMessage;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH);
        this.timestamp = simpleDateFormat.format(new Date());
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
