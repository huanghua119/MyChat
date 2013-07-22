
package com.huanghua.view;

import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.service.ChatService;
import com.huanghua.util.ImageUtil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Register extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 376;
    private static final int GAME_HEIGHT = 282;

    private ImageIcon mLoginbg = new ImageIcon(
            ImageUtil.getImage("image/login_bg.png"));
    private ImageIcon mButtonNormal = new ImageIcon(
            ImageUtil.getImage("image/qz_btn_gray_short_normal.png"));
    private ImageIcon mButtonPress = new ImageIcon(
            ImageUtil.getImage("image/qz_btn_gray_short_pressed.png"));
    private Login mLogin;
    private JButton mOK;
    private JTextField mName;
    private JPasswordField mPass;
    private JPasswordField mTwoPass;
    private JLabel mNameLabel;
    private JLabel mPassLabel;
    private JLabel mTwoPassLabel;
    private JLabel mSuccesLabel;
    private JPopupMenu mAlertPop;
    private JLabel mAlertLabel;
    private User mRegisterUser;

    private ChatService mService;

    private Font FONT_12_BOLD = new Font("宋体", 0, 12);
    private MouseAdapter moveWindowListener = new MouseAdapter() {

        private Point lastPoint = null;

        @Override
        public void mousePressed(MouseEvent e) {
            lastPoint = e.getLocationOnScreen();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point point = e.getLocationOnScreen();
            if (point == null || lastPoint == null) {
                return;
            }
            int offsetX = point.x - lastPoint.x;
            int offsetY = point.y - lastPoint.y;
            Rectangle bounds = getBounds();
            bounds.x += offsetX;
            bounds.y += offsetY;
            setBounds(bounds);
            lastPoint = point;
        }
    };
    private KeyAdapter mKeyAdapter = new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (mOK.getActionCommand().equals("startRegister")) {
                    startRegister();
                } else if (mOK.getActionCommand().equals("autoLogin")) {
                    setVisible(false);
                    mLogin.setLocation(getX(), getY());
                    dispose();
                    mLogin.setVisible(true);
                    mLogin.autoLogin(mRegisterUser.getId(), mRegisterUser.getPassword());
                }
            }
        }
    };

    public Register(Login login) {
        this.mLogin = login;
        this.setIconImage(ImageUtil.getImage("image/icon.png"));
        this.setTitle(Resource.getString("login_title"));
        this.setResizable(false);
        this.setUndecorated(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (dim.getWidth() - GAME_WIDTH) / 2;
        int y = (int) (dim.getHeight() - GAME_HEIGHT) / 2;
        if (mLogin != null) {
            x = mLogin.getX();
            y = mLogin.getY();
        }
        this.setBounds(x, y, GAME_WIDTH, GAME_HEIGHT);
        this.setLayout(null);
        this.addWindowListener(new WindowAdapter() {
            public void windowIconified(WindowEvent e) {
                if (mService == null) {
                    mService = ChatService.getInstance();
                }
                mService.windowIconified(Register.this);
            }
        });
        JLabel loadingbg = new JLabel(mLoginbg);
        loadingbg.setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);
        TopPanel topPanel = new TopPanel(this);
        topPanel.addTile(Resource.getStringForColor("register", "white"));
        topPanel.addCloseButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mLogin != null) {
                    mLogin.setLocation(getX(), getY());
                    mLogin.toFront();
                    mLogin.setVisible(true);
                    setVisible(false);
                } else {
                    System.exit(0);
                }
            }
        });
        topPanel.hideMaxButton();

        mNameLabel = new JLabel(Resource.getStringForColor("name", "black"));
        mNameLabel.setFont(FONT_12_BOLD);
        mNameLabel.setBounds(80, 100, 60, 25);
        mName = new JTextField();
        mName.setBorder(null);
        mName.setBounds(140, 100, 150, 25);
        mName.addKeyListener(mKeyAdapter);

        mPassLabel = new JLabel(Resource.getStringForColor("pass", "black"));
        mPassLabel.setFont(FONT_12_BOLD);
        mPassLabel.setBounds(80, 135, 60, 25);
        mPass = new JPasswordField();
        mPass.setBorder(null);
        mPass.setBounds(140, 135, 150, 25);
        mPass.addKeyListener(mKeyAdapter);

        mTwoPassLabel = new JLabel(Resource.getStringForColor("twopass", "black"));
        mTwoPassLabel.setFont(FONT_12_BOLD);
        mTwoPassLabel.setBounds(80, 170, 60, 25);
        mTwoPass = new JPasswordField();
        mTwoPass.setBorder(null);
        mTwoPass.setBounds(140, 170, 150, 25);
        mTwoPass.addKeyListener(mKeyAdapter);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(231, 236, 240));
        bottomPanel.setBounds(0, getHeight() - 50, getWidth(), 50);
        mOK = new JButton(Resource.getString("commit"));
        mOK.setBorder(null);
        mOK.setIcon(mButtonNormal);
        mOK.setPressedIcon(mButtonPress);
        mOK.setBorderPainted(false);
        mOK.setContentAreaFilled(false);
        mOK.setHorizontalTextPosition(SwingConstants.CENTER);
        mOK.setBounds(getWidth() / 2 - 78 / 2, getHeight() - 40, 78, 30);
        mOK.setActionCommand("startRegister");
        mOK.addActionListener(this);
        mSuccesLabel = new JLabel();
        mSuccesLabel.setVisible(false);
        mSuccesLabel.setFont(new Font("宋体", 0, 14));
        mSuccesLabel.setBounds(80, 100, 240, 45);
        bottomPanel.add(mOK);
        this.setContentPane(loadingbg);
        this.add(topPanel);
        this.add(mPassLabel);
        this.add(mPass);
        this.add(mNameLabel);
        this.add(mName);
        this.add(mTwoPassLabel);
        this.add(mTwoPass);
        this.add(mSuccesLabel);
        this.add(bottomPanel);
        this.addMouseListener(moveWindowListener);
        this.addMouseMotionListener(moveWindowListener);
        mAlertPop = new JPopupMenu();
        mAlertLabel = new JLabel();
        mAlertLabel.setFont(FONT_12_BOLD);
        mAlertPop.add(mAlertLabel);
    }

    private void startRegister() {
        String name = mName.getText();
        String pass = new String(mPass.getPassword());
        String twopass = new String(mTwoPass.getPassword());
        if (name == null || "".equals(name)) {
            mAlertLabel.setText(Resource.getStringForColor("namenotnull", "red"));
            mAlertPop.show(mName, -25, mName.getHeight() / 2);
        } else if (pass == null || "".equals(pass)) {
            mAlertLabel.setText(Resource.getStringForColor("passnotnull", "red"));
            mAlertPop.show(mPass, -25, mPass.getHeight() / 2);
        } else if (!pass.equals(twopass)) {
            mAlertLabel.setText(Resource.getStringForColor("twopassnotpass", "red"));
            mAlertPop.show(mTwoPass, -25, mTwoPass.getHeight() / 2);
        } else {
            if (invalidCode(name)) {
                mAlertLabel.setText(Resource.getStringForColor("invalidCode", "red"));
                mAlertPop.show(mName, -25, mName.getHeight() / 2);
                return;
            }
            if (mService == null) {
                mService = ChatService.getInstance();
                mService.setRegister(this);
            }
            mName.setEditable(false);
            mPass.setEditable(false);
            mPassLabel.setEnabled(false);
            mService.userRegister(name, pass);
            mOK.setText(Resource.getString("beingRegister"));
            mOK.setEnabled(false);
        }
    }

    public boolean invalidCode(String name) {
        boolean result = false;
        result = name.contains(" ");
        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("startRegister")) {
            startRegister();
        } else if (e.getActionCommand().equals("autoLogin")) {
            this.setVisible(false);
            mLogin.setLocation(this.getX(), this.getY());
            dispose();
            mLogin.setVisible(true);
            mLogin.autoLogin(mRegisterUser.getId(), mRegisterUser.getPassword());
        }
    }

    public void userRegisterSucces(User u) {
        mRegisterUser = u;
        mTwoPassLabel.setVisible(false);
        mPassLabel.setVisible(false);
        mNameLabel.setVisible(false);
        mTwoPass.setVisible(false);
        mPass.setVisible(false);
        mName.setVisible(false);
        mOK.setEnabled(true);
        mSuccesLabel.setText("<html><font color=white>"
                + Resource.getString("succesAlert", u.getId()) + "</font></html>");
        mSuccesLabel.setVisible(true);
        mOK.setText(Resource.getString("autoLogin"));
        mOK.setActionCommand("autoLogin");
    }

    public void userRegisterFail() {
        mAlertLabel.setText(Resource.getStringForColor("registerFail", "red"));
        mAlertPop.show(mTwoPass, -25, mTwoPass.getHeight() / 2);
        mName.setEditable(true);
        mPass.setEditable(true);
        mPassLabel.setEnabled(true);
        mOK.setActionCommand("startRegister");
        mOK.setText(Resource.getString("commit"));
        mOK.setEnabled(true);
    }

}
