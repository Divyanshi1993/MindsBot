package com.referminds.app.chat.Model;

public class ServerMessage {
    private String from_id;
    private String to_id;
    private String message;

    public ServerMessage(String fromId, String toId, String message) {
        this();
        this.from_id = fromId;
        this.to_id = toId;
        this.message = message;
    }

    public ServerMessage() {
    }

    public String getFromId() {
        return from_id;
    }

    public String getMessage() {
        return message;
    }

    public String getToId() {
        return to_id;
    }

    public void setFromId(String fromId) {
        this.from_id = fromId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setToId(String toId) {
        this.to_id = toId;
    }
}
