
package com.huanghua.service;

import com.huanghua.client.ClientThread;
import com.huanghua.pojo.User;
import com.huanghua.view.Login;
import com.huanghua.view.MainFrame;
import com.huanghua.view.MessageFrame;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;
import org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.jvnet.substance.title.FlatTitlePainter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ChatService {

    private static ChatService service = null;
    private ClientThread mClient;
    private MainFrame mFrame;
    private Login mLogin;
    private List<User> mUser;
    private List<MessageFrame> mAllChatFrame;
    private User mSelf;

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

    public void setMainFrame(MainFrame frame) {
        this.mFrame = frame;
    }

    public void login(Login login, String id, String password) {
        mLogin = login;
        mSelf = new User();
        mSelf.setId(id);
        mSelf.setPassword(password);
        mClient = new ClientThread(this);
        mClient.start();
    }

    public void loginSuccess() {
        if (mFrame == null) {
            try {
                UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
                SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
                SubstanceLookAndFeel.setCurrentTitlePainter(new FlatTitlePainter());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            mLogin.setVisible(false);
            mFrame = new MainFrame(this);
            mFrame.setVisible(true);
            mFrame.setAlwaysOnTop(true);
        }
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
        mFrame.refreshList(mUser);
    }

    public void startChat(int index) {
        if (mUser == null || mUser.size() == 0) {
            return;
        }
        User u = mUser.get(index);
        startChat(u);
    }

    public void startChat(User u) {
        MessageFrame mf = null;
        if (mAllChatFrame == null) {
            mAllChatFrame = new ArrayList<MessageFrame>();
        }
        if (!chatframeisVisible(u)) {
            mf = new MessageFrame(u, this, mClient);
            mAllChatFrame.add(mf);
        } else {
            mf = getMessageFrameById(u.getId());
        }
        mf.startChat();
    }

    public MessageFrame startChatForServer(User u) {
        MessageFrame mf = null;
        if (mAllChatFrame == null) {
            mAllChatFrame = new ArrayList<MessageFrame>();
        }
        if (!chatframeisVisible(u)) {
            mf = new MessageFrame(u, this, mClient);
            mAllChatFrame.add(mf);
        } else {
            mf = getMessageFrameById(u.getId());
        }
        return mf;
    }

    public MessageFrame getMessageFrameById(String id) {
        MessageFrame frame = null;
        for (MessageFrame ms : mAllChatFrame) {
            User chatuser = ms.getChatUser();
            if (chatuser.getId().equals(id)) {
                frame = ms;
                break;
            }
        }
        return frame;
    }

    public boolean chatframeisVisible(User u) {
        if (mAllChatFrame == null || mAllChatFrame.size() == 0) {
            return false;
        } else {
            for (MessageFrame ms : mAllChatFrame) {
                User chatuser = ms.getChatUser();
                if (chatuser.getId().equals(u.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setMessageById(String context, String id) {
        User u = getUserById(id);
        MessageFrame mf = startChatForServer(u);
        mf.startChat();
        mf.setMessage(context, u);
    }

    public void setError(String id, String msg) {
        MessageFrame mf = getMessageFrameById(id);
        mf.setMessage(msg, mSelf);
    }

    public void offLine() {
        mClient.offLine();
        if (mAllChatFrame != null && mAllChatFrame.size() > 0) {
            for (MessageFrame mf : mAllChatFrame) {
                if (mf.isVisible()) {
                    mf.setVisible(false);
                }
            }
            mAllChatFrame.clear();
            mAllChatFrame = null;
        }
        System.exit(0);
    }

    public void setMySelf(User u) {
        mSelf = u;
    }

    public User getMySelf() {
        return mSelf;
    }
}
