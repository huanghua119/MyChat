
package com.huanghua.mychat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Handler;

import com.huanghua.mychat.ChatActivity;
import com.huanghua.mychat.Contact;
import com.huanghua.mychat.Home;
import com.huanghua.mychat.Login;
import com.huanghua.mychat.Messages;
import com.huanghua.mychat.client.ClientThread;
import com.huanghua.mychat.client.RegisterThread;
import com.huanghua.pojo.NewMessage;
import com.huanghua.pojo.User;

public class ChatService {

    private static ChatService service = null;
    private ClientThread mClient;
    private Login mLogin;
    private Home mHome;
    private List<User> mUser;
    private User mSelf;

    private Handler mMessagesHandle = null;
    private Handler mContactHandle = null;
    private Handler mChatHandle = null;

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

    public void login(Login login, String id, String password) {
        if (mLogin == null) {
            mLogin = login;
        }
        mSelf = new User();
        mSelf.setId(id);
        mSelf.setPassword(password);
        mClient = new ClientThread(this);
        new Thread(mClient).start();
    }

    public void loginSuccess() {
        Intent intent = new Intent();
        intent.setClass(mLogin, Home.class);
        mLogin.startActivity(intent);
        mLogin.finish();
        mLogin = null;
    }

    public void loginFail(String error) {
        mLogin.loginFail(error);
        mClient.close();
        mClient = null;
    }

    public void addUser(User u) {
        boolean isHas = false;
        for (User u1 : mUser) {
            if (u.getId().equals(u1.getId())) {
                isHas = true;
                break;
            }
        }
        if (!isHas) {
            mUser.add(u);
            refreshList();
        }
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

    public User getUserById(String id) {
        for (User u : mUser) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public void refreshList() {
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
    }

    public void goToLogin() {
        Intent intent = new Intent();
        intent.setClass(mHome, Login.class);
        mHome.startActivity(intent);
        mHome.finish();
        mHome = null;
    }

    public void userRegister(String name, String pass) {
        RegisterThread rt = new RegisterThread(this, new User(name, pass));
        new Thread(rt).start();
    }

    public void userRegisterSucces(User u) {
    }

    public void userRegisterFail() {
    }

    public void setMySelf(User u) {
        mSelf = u;
    }

    public User getMySelf() {
        return mSelf;
    }

    public void setMessageById(String context, String id) {
        User u = getUserById(id);
        MessageService.addMessage(context, u, true, u);
        mMessagesHandle.sendEmptyMessage(Messages.HANDLER_MEG_REFRESHLIST);
        if (mChatHandle != null) {
            mChatHandle.sendEmptyMessage(ChatActivity.HANDLER_MEG_REFRESHLIST);
        }
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
        MessageService.addMessage(msg, u, false, mSelf);
        if (mChatHandle != null) {
            mChatHandle.sendEmptyMessage(ChatActivity.HANDLER_MEG_REFRESHLIST);
        }
    }

    public void setHome(Home home) {
        mHome = home;
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

    public List<User> getUserList() {
        return this.mUser;
    }

    public ArrayList<NewMessage> getMessageByUser(User mCurrentUser) {
        return MessageService.getMessageByUser(mCurrentUser);
    }
}
