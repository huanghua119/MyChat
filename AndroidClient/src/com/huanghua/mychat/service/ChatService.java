
package com.huanghua.mychat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.huanghua.mychat.ChatActivity;
import com.huanghua.mychat.Contact;
import com.huanghua.mychat.Home;
import com.huanghua.mychat.Login;
import com.huanghua.mychat.Messages;
import com.huanghua.mychat.Register;
import com.huanghua.mychat.client.ClientThread;
import com.huanghua.mychat.client.RegisterThread;
import com.huanghua.pojo.NewMessage;
import com.huanghua.pojo.Status;
import com.huanghua.pojo.User;

public class ChatService {

    private static ChatService service = null;
    private ClientThread mClient;
    private List<User> mUser;
    private User mSelf;
    private Context mContext;

    private BackStageService mBackStageService = null;
    private Handler mMessagesHandle = null;
    private Handler mContactHandle = null;
    private Handler mChatHandle = null;
    private Handler mRegisterHandle = null;
    private Handler mLoginHandle = null;
    private Handler mHomeHandle = null;

    private ChatService() {
        mUser = new ArrayList<User>();
    }

    public static ChatService getInstance() {
        if (service == null) {
            synchronized (ChatService.class) {
                if (service == null) {
                    service = new ChatService();
                }
            }
        }
        return service;
    }

    public void login(Context context, String id, String password) {
        if (mContext == null) {
            mContext = context;
        }
        mSelf = new User();
        mSelf.setId(id);
        mSelf.setPassword(password);
        mClient = new ClientThread(this);
        new Thread(mClient).start();
    }

    public void loginSuccess() {
        if (mLoginHandle != null) {
            mLoginHandle.sendEmptyMessage(Login.HANDLER_MEG_FINASH);
        } else {
            Intent intent = new Intent();
            intent.setClass(mContext, Home.class);
            mContext.startActivity(intent);
        }
        setLogin(mContext, true);
    }

    public void loginFail(String error) {
        if (mLoginHandle != null) {
            Bundle data = new Bundle();
            data.putString("error", error);
            Message m = new Message();
            m.setData(data);
            m.what = Login.HANDLE_MSG_LOGIN_FAIL;
            mLoginHandle.sendMessage(m);
        } else {
            Intent intent = new Intent();
            intent.setClass(mContext, Login.class);
            mContext.startActivity(intent);
        }
        mClient.close();
        mClient = null;
    }

    public void addUser(List<User> list) {
        mUser.clear();
        mUser = list;
        mContext.sendBroadcast(new Intent(BackStageService.CHAT_ACTION_READ_MESSAGE));
        refreshList();
    }

    public void removeUser(User offuser) {
        for (int i = 0; i < mUser.size(); i++) {
            User u = mUser.get(i);
            if (u.getId().equals(offuser.getId())) {
                mUser.remove(i);
                break;
            }
        }
        refreshList();
    }

    public void udpateUser(User updateUser) {
        if (mUser != null && mUser.contains(updateUser)) {
            for (User u : mUser) {
                if (u.equals(updateUser)) {
                    mUser.remove(u);
                    mUser.add(updateUser);
                    break;
                }
            }
        }
        refreshList();
    }

    public User getUserById(String id) {
        for (User u : mUser) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public void refreshList() {
        Collections.sort(mUser, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if (o1.getStatus() < o2.getStatus()) {
                    return -1;
                } else if (o1.getStatus() > o2.getStatus()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        if (mContactHandle != null) {
            mContactHandle.sendEmptyMessage(Contact.HANDLER_MEG_REFRESHLIST);
        }
    }

    public void startChat(int index) {
        if (mUser == null || mUser.size() == 0) {
            return;
        }
        User u = mUser.get(index);
        startChat(u);
    }

    public void startChat(User u) {
    }

    public void offLine() {
        mClient.offLine();
    }

    public void forceOffLine() {
        mHomeHandle.sendEmptyMessage(Home.HANDLER_MEG_FINISH);
        if (mChatHandle != null) {
            mChatHandle.sendEmptyMessage(ChatActivity.HANDLER_MEG_FINISH);
        }
        mSelf = null;
        mBackStageService.forceOffLineNotify();
    }

    public void goToLogin() {
        Intent intent = new Intent();
        intent.setClass(mContext, Login.class);
        mContext.startActivity(intent);
        mHomeHandle.sendEmptyMessage(Home.HANDLER_MEG_FINISH);
        mBackStageService.stopSelf();
        mBackStageService = null;
        mSelf = null;
        MessageService.clearAllMessage();
    }

    public void userRegister(String name, String pass, int six) {
        User u = new User(name, pass);
        u.setSix(six);
        RegisterThread rt = new RegisterThread(this, u);
        new Thread(rt).start();
    }

    public void userRegisterSucces(User u) {
        Message m = new Message();
        Bundle data = new Bundle();
        data.putString("user_pass", u.getPassword());
        data.putString("user_id", u.getId());
        m.setData(data);
        m.what = Register.HANDLER_MEG_REGISTER_SUCCESS;
        mRegisterHandle.sendMessage(m);
    }

    public void userRegisterFail() {
        mRegisterHandle.sendEmptyMessage(Register.HANDLER_MEG_REGISTER_FAIL);
    }

    public void setMySelf(User u) {
        mSelf = u;
    }

    public User getMySelf() {
        return mSelf;
    }

    public void setMessageById(String context, String id) {
        User u = getUserById(id);
        MessageService.addMessage(mContext, context, mSelf, true, u, u, mSelf);
        mMessagesHandle.sendEmptyMessage(Messages.HANDLER_MEG_REFRESHLIST);
        if (mChatHandle != null) {
            Message m = new Message();
            Bundle data = new Bundle();
            data.putString("user_id", id);
            m.setData(data);
            m.what = ChatActivity.HANDLER_MEG_REFRESHLIST;
            if (mChatHandle.hasMessages(ChatActivity.HANDLER_MEG_REFRESHLIST)) {
                mChatHandle.removeMessages(ChatActivity.HANDLER_MEG_REFRESHLIST);
            }
            mChatHandle.sendMessage(m);
        }
        if (mHomeHandle != null) {
            mHomeHandle.sendEmptyMessage(Home.HANDLER_MEG_NEW_COUNT);
        }
        mBackStageService.message(context, u);
    }

    public void sendMessageToUser(final User mCurrent, final String msg) {
        new Thread() {
            @Override
            public void run() {
                mClient.sendMessage(mCurrent, msg);
            }
        }.start();
    }

    public HashMap<User, ArrayList<NewMessage>> getMessageBox() {
        return MessageService.getMessageBox();
    }

    public void setError(String id, String msg) {
        User u = getUserById(id);
        MessageService.addMessage(mContext, msg, mSelf, false, u, mSelf, u);
        if (mChatHandle != null) {
            mChatHandle.sendEmptyMessage(ChatActivity.HANDLER_MEG_REFRESHLIST);
        }
    }

    public void setContactHandle(Handler handler) {
        mContactHandle = handler;
    }

    public void setMessagesHandle(Handler handler) {
        mMessagesHandle = handler;
    }

    public void setChatHandler(Handler handler) {
        mChatHandle = handler;
    }

    public void setRegisterHandler(Handler handler) {
        mRegisterHandle = handler;
    }

    public void setLoginHandler(Handler handler) {
        mLoginHandle = handler;
    }

    public void setHomeHandler(Handler mHandler) {
        mHomeHandle = mHandler;
    }

    public void setBackStageService(BackStageService sevice) {
        mBackStageService = sevice;
    }

    public List<User> getUserList() {
        return this.mUser;
    }

    public ArrayList<NewMessage> getMessageByUser(User mCurrentUser) {
        return MessageService.getMessageByUser(mCurrentUser);
    }

    public int getOnLineCount(int group) {
        if (group != 0) {
            return 0;
        }
        int result = 0;
        for (User u : mUser) {
            if (u.getStatus() != Status.STATUS_OFFLINE) {
                result++;
            }
        }
        return result;
    }

    public void setLogin(Context context, boolean isLogin) {
        SharedPreferences sp = context.getSharedPreferences("mychat", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("is_login", isLogin);
        editor.commit();
    }

    public void refreshMessageList() {
        if (mMessagesHandle != null) {
            mMessagesHandle.sendEmptyMessage(Messages.HANDLER_MEG_REFRESHLIST);
        }
        if (mHomeHandle != null) {
            mHomeHandle.sendEmptyMessage(Home.HANDLER_MEG_NEW_COUNT);
        }
    }
}
