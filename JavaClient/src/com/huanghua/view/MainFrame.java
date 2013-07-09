
package com.huanghua.view;

import com.huanghua.client.ClientThread;
import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;

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
import javax.swing.border.EtchedBorder;

public class MainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 250;
    private static final int GAME_HEIGHT = 450;
    public static boolean sIsListenter = false;

    private JButton mConnect;
    private JButton mDisconnect;
    private JPanel mCenter;
    private JScrollPane mJScroll;
    private List<User> mUser;
    
    private ClientThread mClient;

    public MainFrame() {
        this.setTitle(Resource.getStringForSet("frame_title"));
        this.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EtchedBorder());
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        mConnect = new JButton(Resource.getStringForSet("connect"));
        mDisconnect = new JButton(Resource.getStringForSet("disconnect"));
        mConnect.addActionListener(this);
        mDisconnect.addActionListener(this);
        mDisconnect.setEnabled(false);
        topPanel.add(new JLabel("      "));
        topPanel.add(mConnect);
        topPanel.add(new JLabel("       "));
        topPanel.add(mDisconnect);
        this.add(topPanel, BorderLayout.NORTH);
        mCenter = new JPanel();
        mCenter.setBorder(new EtchedBorder());
        mCenter.setLayout(new BoxLayout(mCenter, BoxLayout.Y_AXIS));
        mJScroll = new JScrollPane(mCenter);
        this.add(mJScroll, BorderLayout.CENTER);
        mUser = new ArrayList<User>();
        //addUser(new User("192.168.1.94", "hh", 12345));
    }

    public void refreshList() {
        mCenter.removeAll();
        mCenter.validate();
        for (User u : mUser) {
            addUserToPanel(u);
        }
    }

    public void addUserToPanel(User u) {
        JPanel list = new JPanel();
        list.setBorder(new EtchedBorder());
        list.setLayout(new BoxLayout(list, BoxLayout.X_AXIS));
        list.add(new JLabel(u.getIp() + ":" + u.getName() + "                 "));
        JButton button = new JButton(Resource.getStringForSet("frame_title"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        list.add(button);
        mCenter.add(list);
        mCenter.validate();
    }
    public void addUser(User u) {
        boolean isHas = false;
        for (User u1 : mUser) {
            if (u.getName().equals(u1.getName())) {
                isHas = true;
                break;
            }
        }
        if (!isHas) {
            mUser.add(u);
            addUserToPanel(u);
        }
    }

    public void offLine() {
        mUser.clear();
        refreshList();
        mClient.offLine();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mDisconnect) {
            mConnect.setEnabled(true);
            mDisconnect.setEnabled(false);
            sIsListenter = false;
            offLine();
        } else if (e.getSource() == mConnect) {
            mConnect.setEnabled(false);
            mDisconnect.setEnabled(true);
            sIsListenter = true;
            mClient = new ClientThread(this);
            mClient.start();
        }
    }

    public static void main(String[] args) {
        Resource.setLanguage(Resource.Language_zh_CN);
        MainFrame main = new MainFrame();
        main.setVisible(true);
    }
}
