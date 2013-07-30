
package com.huanghua.mychat.service;

import com.huanghua.pojo.NewMessage;
import com.huanghua.pojo.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageService {

    private static HashMap<User, ArrayList<NewMessage>> mMessageBox = new HashMap<User, ArrayList<NewMessage>>();

    public static ArrayList<NewMessage> getMessageByUser(User mCurrentUser) {
        ArrayList<NewMessage> result = new ArrayList<NewMessage>();
        if (mMessageBox != null) {
            if (mMessageBox.containsKey(mCurrentUser)) {
                result = mMessageBox.get(mCurrentUser);
            }
        }
        return result;
    }

    public static void addMessage(String context, User u, boolean isNew, User sendUser) {
        if (mMessageBox.containsKey(u)) {
            ArrayList<NewMessage> message = mMessageBox.get(u);
            NewMessage m = new NewMessage();
            m.setMessageDate(new Date());
            m.setContext(context);
            m.setUser(sendUser);
            m.setNew(isNew);
            message.add(m);
        } else {
            ArrayList<NewMessage> message = new ArrayList<NewMessage>();
            NewMessage m = new NewMessage();
            m.setMessageDate(new Date());
            m.setContext(context);
            m.setUser(sendUser);
            m.setNew(isNew);
            message.add(m);
            mMessageBox.put(u, message);
        }
    }

    public static HashMap<User, ArrayList<NewMessage>> getMessageBox() {
        return mMessageBox;
    }

    public static int getNewMessageByUser(User u) {
        int result = 0;
        if (mMessageBox.containsKey(u)) {
            ArrayList<NewMessage> message = mMessageBox.get(u);
            int i = 0;
            for (NewMessage nm : message) {
                if (nm.isNew()) {
                    i++;
                }
            }
            result = i;
        }

        return result;
    }

}
