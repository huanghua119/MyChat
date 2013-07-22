
package com.huanghua.view;

import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.service.ChatService;
import com.huanghua.util.ImageUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

public class MainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 250;
    private static final int GAME_HEIGHT = 450;

    private JLabel mName;
    private JScrollPane mJScroll;
    private JList mUserList;
    private ChatService mService;
    private UserListListener mListListener;

    public MainFrame(ChatService service) {
        this.mService = service;
        this.setIconImage(ImageUtil.getImage("image/icon.png"));
        this.setTitle(Resource.getString("frame_title"));
        this.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mService.offLine();
            }
            public void windowIconified(WindowEvent e) {
                mService.windowIconified(MainFrame.this);
            }
        });

        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EtchedBorder());
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        mName = new JLabel(service.getMySelf().getName());
        topPanel.add(mName);
        this.add(topPanel, BorderLayout.NORTH);
        mUserList = new JList();
        mListListener = new UserListListener(this, service);
        mUserList.addMouseListener(mListListener);
        mUserList.addMouseMotionListener(mListListener);
        mJScroll = new JScrollPane(mUserList);
        this.add(mJScroll, BorderLayout.CENTER);
    }

    public void refreshList(List<User> mUser) {
        mUserList.removeAll();
        int size = mUser.size();
        String[] data = new String[size];
        for (int i = 0; i < size; i++) {
            User u = mUser.get(i);
            data[i] = "(" + u.getId() + "): " + u.getName();
        }
        mUserList.setListData(data);
    }

    public JList getUserList() {
        return mUserList;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

}
