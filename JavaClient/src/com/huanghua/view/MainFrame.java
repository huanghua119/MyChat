
package com.huanghua.view;

import com.huanghua.client.ClientThread;
import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.server.ChatServer;
import com.huanghua.util.NetUtil;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;
import org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.jvnet.substance.title.FlatTitlePainter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;

public class MainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 250;
    private static final int GAME_HEIGHT = 450;
    public static boolean sIsListenter = false;
    private int mPort = 0;

    private JTextField mNameText;
    private JButton mConnect;
    private JButton mDisconnect;
    private JScrollPane mJScroll;
    private JList mUserList;
    private List<User> mUser;
    private List<MessageFrame> mAllChatFrame;
    private User mSelf;
    private UserListListener mListListener;

    private ClientThread mClient;
    private ChatServer mCseServer;

    public MainFrame() {
        this.setTitle(Resource.getString("frame_title"));
        this.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (sIsListenter) {
                    offLine();
                }
            }
        });

        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EtchedBorder());
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        mConnect = new JButton(Resource.getString("connect"));
        mDisconnect = new JButton(Resource.getString("disconnect"));
        mNameText = new JTextField();
        mNameText.setText(Resource.getString("anonymous"));
        mConnect.addActionListener(this);
        mDisconnect.addActionListener(this);
        mDisconnect.setEnabled(false);
        topPanel.add(mNameText);
        topPanel.add(mConnect);
        topPanel.add(mDisconnect);
        this.add(topPanel, BorderLayout.NORTH);
        mUserList = new JList();
        mListListener = new UserListListener(this);
        mUserList.addMouseListener(mListListener);
        mUserList.addMouseMotionListener(mListListener);
        mJScroll = new JScrollPane(mUserList);
        this.add(mJScroll, BorderLayout.CENTER);
        mUser = new ArrayList<User>();
        //addUser(new User("192.168.1.94", "hh", 12345));
    }

    public void refreshList() {
        mUserList.removeAll();
        int size = mUser.size();
        String[] data = new String[size];
        for (int i = 0; i < size; i++) {
            User u = mUser.get(i);
            data[i] = u.getIp() + ":" + u.getName();
        }
        mUserList.setListData(data);
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

    public void setMySelf(User u) {
        mSelf = u;
    }
    public User getMySelf() {
        return mSelf;
    }

    public User getUserById(String id) {
        for (User u : mUser) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public JList getUserList() {
        return mUserList;
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
            mf = new MessageFrame(u, this, true);
            mf.setToClient();
            mAllChatFrame.add(mf);
        } else {
            mf = getMessageFrameById(u.getId());
            if (!mf.isVisible()) {
                mf.setToClient();
            }
        }
        mf.startChat();
    }

    public MessageFrame startChatForServer(User u) {
        MessageFrame mf = null;
        if (mAllChatFrame == null) {
            mAllChatFrame = new ArrayList<MessageFrame>();
        }
        if (!chatframeisVisible(u)) {
            mf = new MessageFrame(u, this, false);
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

    public void offLine() {
        mUser.clear();
        refreshList();
        if (mCseServer != null) {
            mCseServer.close();
        }
        mClient.offLine();
    }

    public String getName() {
        return this.mNameText.getText();
    }

    public int getPort() {
        return this.mPort;
    }

    public void startChatServer() {
        if (mCseServer == null) {
            mCseServer = new ChatServer(this);
        }
        if (!mCseServer.getFlag()) {
            new Thread(mCseServer).start();
        }
    }

    public void disconnect() {
        mConnect.setEnabled(true);
        mDisconnect.setEnabled(false);
        sIsListenter = false;
        offLine();
    }

    public void connect() {
        mConnect.setEnabled(false);
        sIsListenter = true;
        if (mPort == 0) {
            mPort = NetUtil.getValidPort(12346);
        }
        mClient = new ClientThread(this);
        mClient.start();
    }
    public void loginSuccess() {
        setAlwaysOnTop(true);
        mDisconnect.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mDisconnect) {
            disconnect();
        } else if (e.getSource() == mConnect) {
            connect();
        }
    }

    public static void main(String[] args) {
        Resource.setLanguage(Resource.Language_zh_CN);
        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
            SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
            SubstanceLookAndFeel.setCurrentTitlePainter(new FlatTitlePainter());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame main = new MainFrame();
                main.setVisible(true);
            }
        });
    }
}
