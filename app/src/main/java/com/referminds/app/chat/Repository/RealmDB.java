package com.referminds.app.chat.Repository;

import android.util.Log;
import com.referminds.app.chat.Model.Conversation;
import com.referminds.app.chat.Model.Message;
import com.referminds.app.chat.Utils.CommonSocketManager;
import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class RealmDB {
    private Realm mRealm;

    public RealmDB() {
        mRealm = Realm.getDefaultInstance();
    }

    public void updateConversation (final String conversationName, final String username, final String msg, final String timestamp) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                Conversation conversation =null;
                @Override
                public void execute(Realm realm) {
                    try {

                        conversation = realm.where(Conversation.class).equalTo(Conversation.PROPERTY_NAME, conversationName).findFirst();
                        if(conversation==null){
                            conversation = realm.createObject(Conversation.class,conversationName);
                            realm.copyToRealm(conversation);
                        }

                        Message message=realm.createObject(Message.class);
                        message.setmUsername(username);
                        message.setmMessage(msg);
                        message.setTimestamp(timestamp);
                        realm.copyToRealm(message);

                        conversation.getMessageList().add(message);
                        Log.e("realm", " Message updated");
                    } catch (RealmPrimaryKeyConstraintException e) {
                        Log.e("realm", "Primary Key exists, Press Update instead  " + e.getMessage());

                    }
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public void readConversation(final String username, final CommonSocketManager commonSocketManager) { mRealm.executeTransaction(new Realm.Transaction() {
            Conversation conversation;

            @Override
            public void execute(Realm realm) {
                try {
                    conversation = realm.where(Conversation.class).equalTo("username", username).findFirst();
                    if (conversation == null) {
                        conversation = realm.createObject(Conversation.class, username);
                        realm.copyToRealm(conversation);
                    }
                    commonSocketManager.loadConversation(conversation);

                   // Log.e("realm", "name " + conversation.getName() + " message: " + conversation.getMessageList().get(0).getMessage());
                } catch (RealmPrimaryKeyConstraintException e) {
                    //log the error
                    Log.e("realm", "Primary Key exists, Press Update instead  " + e.getMessage());
                }
            }
        });
    }
}
