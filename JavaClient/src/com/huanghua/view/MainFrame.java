
package com.huanghua.view;

import com.huanghua.client.ChatClient;
import com.huanghua.client.ClientThread;
import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.server.ChatServer;
import com.huanghua.util.NetUtil;

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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
    private User mSelf;
    private UserListListener mListListener;

    private ClientThread mClient;
    private ChatServer mCseServer;

    public MainFrame() {
        this.setTitle(Resource.getStringForSet("frame_title"));
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
        mConnect = new JButton(Resource.getStringForSet("connect"));
        mDisconnect = new JButton(Resource.getStringForSet("disconnect"));
        mNameText = new JTextField();
        mNameText.setText(Resource.getStringForSet("anonymous"));
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
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public JList getUserList() {
        return mUserList;
    }

    public void startChat(int index) {
        User u = mUser.get(index);
        startChat(u);
    }

    public void startChat(User u) {
        ChatClient chatClient = new ChatClient(this, u);
        new Thread(chatClient).start();
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
        mDisconnect.setEnabled(true);
        sIsListenter = true;
        if (mPort == 0) {
            mPort = NetUtil.getValidPort(12346);
        }
        mClient = new ClientThread(this);
        mClient.start();
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
        MainFrame main = new MainFrame();
        main.setVisible(true);
    }
}
