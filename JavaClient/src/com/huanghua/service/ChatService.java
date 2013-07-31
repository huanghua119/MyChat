
package com.huanghua.service;

import com.huanghua.client.ClientThread;
import com.huanghua.client.RegisterThread;
import com.huanghua.i18n.Resource;
import com.huanghua.listener.TrayListener;
import com.huanghua.pojo.User;
import com.huanghua.util.Configuration;
import com.huanghua.util.ImageUtil;
import com.huanghua.view.Login;
import com.huanghua.view.MainFrame;
import com.huanghua.view.MessageFrame;
import com.huanghua.view.Register;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ChatService {

    private static ChatService service = null;
    private ClientThread mClient;
    private MainFrame mFrame;
    private Register mRegister;
    private Login mLogin;
    private List<User> mUser;
    private List<MessageFrame> mAllChatFrame;
    private User mSelf;
    private SystemTray mSystemtary;
    private TrayIcon mTrayIcon;
    private TrayListener mTrayListener = new TrayListener(this);
    private boolean mIsLogin = false;
    private TaryFalsh mTrayFalsh = new TaryFalsh();
    private List<MessageFrame> mMessageBox;

    private class TaryFalsh implements Runnable {
        boolean flag = false;

        @Override
        public void run() {
            while (flag) {
                mTrayIcon.setImage(new ImageIcon("").getImage());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mTrayIcon.setImage(ImageUtil.getImage("image/tray.png"));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setFalsh() {
            flag = true;
        }

        public void setStop() {
            flag = false;
        }

        public boolean getFlag() {
            return flag;
        }
    };

    private ChatService() {
        mUser = new ArrayList<User>();
        mMessageBox = new ArrayList<MessageFrame>();
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

    public void login(String id, String password) {
        mSelf = new User();
        mSelf.setId(id);
        mSelf.setPassword(password);
        mClient = new ClientThread(this);
        mClient.start();
    }

    public void loginSuccess() {
        if (mFrame == null) {
            mFrame = new MainFrame(this);
        }
        mIsLogin = true;
        changeSystemTray();
        mFrame.setVisible(true);
        mFrame.setAlwaysOnTop(true);
        mLogin.setVisible(false);
        mLogin.dispose();
        Configuration.saveUser(mSelf.getId(), mSelf.getPassword());
    }

    public void loginFail(String error) {
        mIsLogin = false;
        mLogin.loginFail(error);
        mClient.close();
        mClient = null;
    }

    public void addUser(User u) {
        boolean isHas = false;
        for (User u1 : mUser) {
            if (u.getId().equals(u1.getId())) {
                isHas = true;
                mUser.remove(u1);
                mUser.add(u);
                break;
            }
        }
        if (!isHas) {
            mUser.add(u);
        }
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
        mf.setMessage(context, u);
        if (!mf.isVisible()) {
            if (!mMessageBox.contains(mf)) {
                mMessageBox.add(mf);
            }
            if (!mTrayFalsh.getFlag()) {
                mTrayFalsh.setFalsh();
                new Thread(mTrayFalsh).start();
            }
        }
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

    public void forceOffLine() {
        int message = JOptionPane.showConfirmDialog(mFrame,Resource.getString("offlineWarn"),
                Resource.getString("offlineNotification"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                new ImageIcon(ImageUtil.getImage("image/recent_icon_failed.png")));
        if (message == 1) {
            System.exit(0);
        } else {
            mLogin.setVisible(true);
            mLogin.autoLogin(mSelf.getId(), mSelf.getPassword());
        }
    }

    public void setRegister(Register r) {
        mRegister = r;
    }

    public void userRegister(String name, String pass, int six) {
        User u = new User(name, pass);
        u.setSix(six);
        RegisterThread rt = new RegisterThread(this, u);
        new Thread(rt).start();
    }

    public void userRegisterSucces(User u) {
        mRegister.userRegisterSucces(u);
    }

    public void userRegisterFail() {
        mRegister.userRegisterFail();
    }

    public void setMySelf(User u) {
        mSelf = u;
    }

    public User getMySelf() {
        return mSelf;
    }

    public void addSystemTray() {
        if (SystemTray.isSupported()) {
            this.mSystemtary = SystemTray.getSystemTray();
            PopupMenu pop = new PopupMenu();
            MenuItem open = new MenuItem(Resource.getString("openFrame"));
            MenuItem exit = new MenuItem(Resource.getString("exit"));
            pop.add(open);
            pop.add(exit);
            open.addActionListener(mTrayListener);
            exit.addActionListener(mTrayListener);
            try {
                this.mTrayIcon = new TrayIcon(
                        ImageUtil.getImage("image/tray.png"),
                        Resource.getString("frame_title"), pop);
                this.mSystemtary.add(mTrayIcon);
                this.mTrayIcon.addMouseListener(mTrayListener);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
    public void changeSystemTray() {
        PopupMenu pop = new PopupMenu();
        MenuItem open = new MenuItem(Resource.getString("openFrame"));
        MenuItem exit = new MenuItem(Resource.getString("exit"));
        pop.add(open);
        pop.add(exit);
        open.addActionListener(mTrayListener);
        exit.addActionListener(mTrayListener);
        try {
            mSystemtary.remove(mTrayIcon);
            this.mTrayIcon = new TrayIcon(
                    ImageUtil.getImage("image/tray.png"),
                    "QQ: " + mSelf.getName() + " (" + mSelf.getId() + ")", pop);
            this.mSystemtary.add(mTrayIcon);
            this.mTrayIcon.addMouseListener(mTrayListener);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void showMainFrame() {
        JFrame frame = mIsLogin ? mFrame : mLogin;
        frame.setExtendedState(Frame.NORMAL);
        frame.setVisible(true);
        frame.toFront();
    }

    public void windowIconified(JFrame frame) {
        frame.dispose();
    }

    public void setLogin(Login login) {
        mLogin = login;
    }

    public void exit() {
        if (mIsLogin) {
            offLine();
        } else {
            System.exit(0);
        }
    }

    public List<MessageFrame> getMessageBox() {
        return this.mMessageBox;
    }

    public void stopFlash() {
        mTrayFalsh.setStop();
    }
}
