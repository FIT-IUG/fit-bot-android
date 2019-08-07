package com.logicoverflow.fit_bot.Model;

/**
 * Created by Fitbot on 11/05/16.
 */
public class ChatMessage {

    public static final String DB_NAME = "MESSAGE_DB";
    public static final String TABLE_NAME = "MESSAGE_TABLE";
    public static final String MESSAGE_COL_ID = "MESSAGE_COL_ID";
    public static final String MESSAGE_COL = "MESSAGE_JSON";

    public static final String CREATE_TABLE =
            "CREATE TABLE "+TABLE_NAME+" ( "
                    + MESSAGE_COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + MESSAGE_COL +" TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    private boolean isImage, isMine;
    private String content;

    public ChatMessage() {
    }

    public ChatMessage(String message, boolean mine, boolean image) {
        content = message;
        isMine = mine;
        isImage = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }
}
