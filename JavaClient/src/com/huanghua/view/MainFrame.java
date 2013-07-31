
package com.huanghua.view;

import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.service.ChatService;
import com.huanghua.util.ImageUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 250;
    private static final int GAME_HEIGHT = 600;
    private ImageIcon mFrameBackground = new ImageIcon(ImageUtil.getImage("image/mian_defalut.jpg"));
    private ImageIcon mSettingNormal = new ImageIcon(ImageUtil.getImage("image/settings_normal.png"));
    private ImageIcon mSettingPress = new ImageIcon(ImageUtil.getImage("image/settings_press.png"));

    private JLabel mName;
    private JScrollPane mJScroll;
    private JList mUserList;
    private ChatService mService;
    private JPanel mRootPanel;
    private JLabel mBackground;
    private JPanel mBottom;

    private MouseAdapter moveWindowListener = new MouseAdapter() {
        private boolean top = false;
        private boolean down = false;
        private boolean left = false;
        private boolean right = false;
        private Point draggingAnchor = null;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == mUserList) {
                if (e.getButton() == 1) {
                    int index = getUserList().locationToIndex(e.getPoint());
                    int clickNum = e.getClickCount();
                    // 双击鼠标
                    if (clickNum == 2) {
                        mService.startChat(index);
                    }
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (e.getPoint().getY() <= 2) {
                if (e.getSource() == mUserList) {
                    return;
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                top = true;
            } else if (Math.abs(e.getPoint().getY() - getSize().getHeight()) <= 3) {
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                down = true;
            } else if (e.getPoint().getX() <= 2) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                left = true;
            } else if (Math.abs(e.getPoint().getX() - getSize().getWidth()) <= 3) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                right = true;
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                draggingAnchor = new Point(e.getX() + mRootPanel.getX(), e.getY() + mRootPanel.getY());
                top = false;
                down = false;
                left = false;
                right = false;
            }

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Dimension dimension = getSize();
            if (top) {

                dimension.setSize(dimension.getWidth(), dimension.getHeight() - e.getY());
                setSize(dimension);
                setLocation(getLocationOnScreen().x, getLocationOnScreen().y
                        + e.getY());
                mBackground = new JLabel();
                mBackground.setIcon(mFrameBackground);
                mBackground.setBounds(0, 0, getWidth(), getHeight());
                getLayeredPane().add(mBackground, new Integer(Integer.MIN_VALUE));
            } else if (down) {

                dimension.setSize(dimension.getWidth(), e.getY());
                setSize(dimension);
                mBackground = new JLabel();
                mBackground.setIcon(mFrameBackground);
                mBackground.setBounds(0, 0, getWidth(), getHeight());
                getLayeredPane().add(mBackground, new Integer(Integer.MIN_VALUE));
            } else if (left) {

                dimension.setSize(dimension.getWidth() - e.getX(), dimension.getHeight());
                setSize(dimension);

                setLocation(getLocationOnScreen().x + e.getX(),
                        getLocationOnScreen().y);
                mBackground = new JLabel();
                mBackground.setIcon(mFrameBackground);
                mBackground.setBounds(0, 0, getWidth(), getHeight());
                getLayeredPane().add(mBackground, new Integer(Integer.MIN_VALUE));
            } else if (right) {

                dimension.setSize(e.getX(), dimension.getHeight());
                setSize(dimension);
                mBackground = new JLabel();
                mBackground.setIcon(mFrameBackground);
                mBackground.setBounds(0, 0, getWidth(), getHeight());
                getLayeredPane().add(mBackground, new Integer(Integer.MIN_VALUE));
            } else {
                if (e.getSource() == mUserList) {
                    return;
                }
                setLocation(e.getLocationOnScreen().x - draggingAnchor.x,
                        e.getLocationOnScreen().y - draggingAnchor.y);
            }
        }
    };

    public MainFrame(ChatService service) {
        this.mService = service;
        this.setIconImage(ImageUtil.getImage("image/icon.png"));
        this.setTitle(Resource.getString("frame_title"));
        this.setResizable(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.setMinimumSize(new Dimension(GAME_WIDTH,GAME_HEIGHT));
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mService.offLine();
            }
            public void windowIconified(WindowEvent e) {
                mService.windowIconified(MainFrame.this);
            }
        });

        setUndecorated(true);

        mBackground = new JLabel();
        mBackground.setIcon(mFrameBackground);
        mBackground.setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);
        this.getLayeredPane().add(mBackground, new Integer(Integer.MIN_VALUE));

        mRootPanel = new JPanel();
        mRootPanel.setLayout(new BorderLayout());
        mRootPanel.setOpaque(false);
        TopPanel topPanel2 = new TopPanel(this);
        topPanel2.hideMaxButton();
        topPanel2.addCloseButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mService.offLine();
            }
        });
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        JPanel topPanel3 = new JPanel();
        topPanel3.setLayout(new BoxLayout(topPanel3, BoxLayout.X_AXIS));
        topPanel3.setOpaque(false);
        mName = new JLabel(Resource.getColor(service.getMySelf().getName(), "white"));
        topPanel3.add(mName);
        topPanel.add(topPanel2);
        topPanel.add(topPanel3);
        mRootPanel.add(topPanel, BorderLayout.NORTH);
        mUserList = new JList();
        mUserList.addMouseListener(moveWindowListener);
        mUserList.addMouseMotionListener(moveWindowListener);
        mJScroll = new JScrollPane(mUserList);
        mRootPanel.add(mJScroll, BorderLayout.CENTER);
        mBottom = new JPanel();
        mBottom.setOpaque(false);
        mBottom.setLayout(new BorderLayout());
        mBottom.setBackground(new Color(231, 236, 240));
        JButton mSetting = new JButton(mSettingNormal);
        mSetting.setPressedIcon(mSettingPress);
        JPanel bottom2 = new JPanel();
        bottom2.setLayout(new FlowLayout(FlowLayout.LEFT));
        bottom2.setBorder(null);
        bottom2.add(mSetting);
        mBottom.add(bottom2, BorderLayout.SOUTH);
        mRootPanel.add(mBottom, BorderLayout.SOUTH);
        mRootPanel.addMouseListener(moveWindowListener);
        mRootPanel.addMouseMotionListener(moveWindowListener);
        this.setContentPane(mRootPanel);
    }

    public void refreshList(List<User> mUser) {
        mUserList.removeAll();
        int size = mUser.size();
        String[] data = new String[size];
        for (int i = 0; i < size; i++) {
            User u = mUser.get(i);
            data[i] = "[" + Resource.getUserStatus(u.getStatus()) + "]  " + "(" + u.getId() + "): " + u.getName();
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
