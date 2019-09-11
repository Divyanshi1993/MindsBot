package com.referminds.app.chat.data.Model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Message extends RealmObject {
    public static final int TYPE_My_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;
    public static final int TYPE_OTHER_MESSGE = 3;

    private int mType;
    @Required
    private String mMessage;
    @Required
    private String mUsername;
    @Required
    private String timestamp;

    public Message() {

    }

    public Message(String mUsername, String mMessage, String timestamp) {
        this();
        this.mMessage = mMessage;
        this.mUsername = mUsername;
        this.timestamp = timestamp;
    }

    public int getType() {
        return mType;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;

        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            return message;
        }
    }
}
