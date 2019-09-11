package com.referminds.app.chat.data.Model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Conversation extends RealmObject {

    public static final String PROPERTY_NAME = "username";

    @PrimaryKey
    @Required
    private String username;
    private RealmList<Message> messageList;

    public Conversation() {

    }

    public RealmList<Message> getMessageList() {
        return messageList;
    }

    public String getName() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMessageList(RealmList<Message> messageList) {
        this.messageList = messageList;
    }
}
