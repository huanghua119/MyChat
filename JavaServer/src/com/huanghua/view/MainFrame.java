
package com.huanghua.view;

import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.socket.SocketThread;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

public class MainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 250;
    private static final int GAME_HEIGHT = 450;
    public static boolean sIsListenter = false;
    private SocketThread mSocket;
    private List<User> mUser = null;

    private JButton mStartLinstater;
    private JButton mStopLinstater;
    private JTextArea mIPList;

    public MainFrame() {
        mUser = new ArrayList<User>();
        this.setTitle(Resource.getString("frame_title"));
        this.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EtchedBorder());
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        mStartLinstater = new JButton(Resource.getString("start"));
        mStopLinstater = new JButton(Resource.getString("stop"));
        mStartLinstater.addActionListener(this);
        mStopLinstater.addActionListener(this);
        mStopLinstater.setEnabled(false);
        topPanel.add(new JLabel("      "));
        topPanel.add(mStartLinstater);
        topPanel.add(new JLabel("       "));
        topPanel.add(mStopLinstater);
        this.add(topPanel, BorderLayout.NORTH);
        mIPList = new JTextArea();
        mIPList.setEditable(false);
        this.add(new JScrollPane(mIPList), BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mStopLinstater) {
            mStartLinstater.setEnabled(true);
            mStopLinstater.setEnabled(false);
            sIsListenter = false;
            mSocket.cancel();
        } else if (e.getSource() == mStartLinstater) {
            mStartLinstater.setEnabled(false);
            mStopLinstater.setEnabled(true);
            sIsListenter = true;
            mSocket = new SocketThread(this);
            Thread thread = new Thread(mSocket);
            thread.start();
        }
    }

    public void addUser(User u) {
        mUser.add(u);
    }

    public List<User> getUserList() {
        return this.mUser;
    }

    public void sendUserList() {
        mSocket.sendUserList();
    }
    public void sendUserOffline(User u) {
        mSocket.sendUserOffline(u);
    }
    public void userOffLine(User u) {
        setMessage(Resource.getString("offline") + u.getIp() + ":" + u.getId());
        mUser.remove(u);
        sendUserOffline(u);
    }

    public User getUserById(String id) {
        for (User u : mUser) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public void sendContextByIdToUser(String context, String id, User toUser) {
        User u = getUserById(id);
        if (u == null) {
            toUser.getsAgent().sendError(Resource.getString("usernotonline"), id);
        } else {
            u.getsAgent().sendMessage(context, toUser);
        }
    }

    public void setMessage(String message) {
        this.mIPList.append(message + "\n");
    }

    public static void main(String[] args) {
        Resource.setLanguage(Resource.Language_zh_CN);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame main = new MainFrame();
                main.setVisible(true);
            }
        });
    }
}
