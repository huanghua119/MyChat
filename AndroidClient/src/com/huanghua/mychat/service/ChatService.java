
package com.huanghua.mychat.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Handler;

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
    private HashMap<User, ArrayList<NewMessage>> mMessageBox;

    private Handler mMessagesHandle = null;
    private Handler mContactHandle = null;

    private ChatService() {
        mUser = new ArrayList<User>();
        mMessageBox = new HashMap<User, ArrayList<NewMessage>>();
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
        if (mMessageBox.containsKey(u)) {
            ArrayList<NewMessage> message = mMessageBox.get(u);
            NewMessage m = new NewMessage();
            m.setMessageDate(new Date());
            m.setContext(context);
            message.add(m);
        } else {
            ArrayList<NewMessage> message = new ArrayList<NewMessage>();
            NewMessage m = new NewMessage();
            m.setMessageDate(new Date());
            m.setContext(context);
            message.add(m);
            mMessageBox.put(u, message);
        }
        mMessagesHandle.sendEmptyMessage(Messages.HANDLER_MEG_REFRESHLIST);
    }

    public HashMap<User, ArrayList<NewMessage>> getMessageBox() {
        return this.mMessageBox;
    }

    public void setError(String string, String string2) {

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

    public List<User> getUserList() {
        return this.mUser;
    }
}
