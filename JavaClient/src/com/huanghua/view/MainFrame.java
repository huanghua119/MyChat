
package com.huanghua.view;

import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.service.ChatService;
import com.huanghua.util.ImageUtil;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
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
    private JPanel mRootPanel;

    private MouseAdapter moveWindowListener = new MouseAdapter() {
        private boolean top = false;
        private boolean down = false;
        private boolean left = false;
        private boolean right = false;
        private boolean drag = false;
        private Point lastPoint = null;
        private Point draggingAnchor = null;

        @Override
        public void mouseMoved(MouseEvent e) {
            if (e.getPoint().getY() == 0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                top = true;
            } else if (Math.abs(e.getPoint().getY() - getSize().getHeight()) <= 1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                down = true;
            } else if (e.getPoint().getX() == 0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                left = true;
            } else if (Math.abs(e.getPoint().getX() - getSize().getWidth()) <= 1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                right = true;
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                draggingAnchor = new Point(e.getX() + mRootPanel.getX(), e.getY() + mRootPanel.getY());
                top = false;
                down = false;
                left = false;
                right = false;
                drag = true;
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
            } else if (down) {

                dimension.setSize(dimension.getWidth(), e.getY());
                setSize(dimension);

            } else if (left) {

                dimension.setSize(dimension.getWidth() - e.getX(), dimension.getHeight());
                setSize(dimension);

                setLocation(getLocationOnScreen().x + e.getX(),
                        getLocationOnScreen().y);

            } else if (right) {

                dimension.setSize(e.getX(), dimension.getHeight());
                setSize(dimension);
            } else {
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
        mRootPanel = new JPanel();
        mRootPanel.setLayout(null);
        TopPanel topPanel2 = new TopPanel(this);
        topPanel2.hideMaxButton();
        topPanel2.addCloseButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mService.offLine();
            }
        });
        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setBounds(new Rectangle(GAME_WIDTH, 40));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        JPanel topPanel3 = new JPanel();
        topPanel3.setLayout(new BoxLayout(topPanel3, BoxLayout.X_AXIS));
        mName = new JLabel(service.getMySelf().getName());
        topPanel3.add(mName);
        topPanel.add(topPanel2);
        topPanel.add(topPanel3);
        mRootPanel.add(topPanel);
        mUserList = new JList();
        mListListener = new UserListListener(this, service);
        mUserList.addMouseListener(mListListener);
        mUserList.addMouseMotionListener(mListListener);
        mJScroll = new JScrollPane(mUserList);
        mJScroll.setBounds(0,40,GAME_WIDTH, 100);
        mRootPanel.add(mJScroll);
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
