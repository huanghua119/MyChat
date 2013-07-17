
package com.huanghua.view;

import com.huanghua.i18n.Resource;
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
    private JPopupMenu mAlertPop;
    private JLabel mAlertLabel;

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
                startRegister();
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
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH,
                GAME_HEIGHT);
        this.setLayout(null);
        JLabel loadingbg = new JLabel(mLoginbg);
        loadingbg.setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);
        TopPanel topPanel = new TopPanel(this);
        topPanel.addTile(Resource.getString("register"));
        topPanel.addCloseButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mLogin != null) {
                    setVisible(false);
                    mLogin.setVisible(true);
                } else {
                    System.exit(0);
                }
            }
        });
        topPanel.hideMaxButton();

        JLabel name = new JLabel(Resource.getStringForColor("name", "black"));
        name.setFont(FONT_12_BOLD);
        name.setBounds(80, 100, 60, 25);
        mName = new JTextField();
        mName.setBorder(null);
        mName.setBounds(140, 100, 150, 25);
        mName.addKeyListener(mKeyAdapter);

        JLabel pass = new JLabel(Resource.getStringForColor("pass", "black"));
        pass.setFont(FONT_12_BOLD);
        pass.setBounds(80, 135, 60, 25);
        mPass = new JPasswordField();
        mPass.setBorder(null);
        mPass.setBounds(140, 135, 150, 25);
        mPass.addKeyListener(mKeyAdapter);

        JLabel twopass = new JLabel(Resource.getStringForColor("twopass", "black"));
        twopass.setFont(FONT_12_BOLD);
        twopass.setBounds(80, 170, 60, 25);
        mTwoPass = new JPasswordField();
        mTwoPass.setBorder(null);
        mTwoPass.setBounds(140, 170, 150, 25);
        mTwoPass.addKeyListener(mKeyAdapter);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(231, 236, 240));
        bottomPanel.setBounds(0, getHeight() - 50, getWidth(), 50);
        mOK = new JButton("提交");
        mOK.setBorder(null);
        mOK.setIcon(mButtonNormal);
        mOK.setPressedIcon(mButtonPress);
        mOK.setBorderPainted(false);
        mOK.setContentAreaFilled(false);
        mOK.setHorizontalTextPosition(SwingConstants.CENTER);
        mOK.setBounds(getWidth() / 2 - 78 / 2, getHeight() - 40, 78, 30);
        mOK.addActionListener(this);
        bottomPanel.add(mOK);
        this.setContentPane(loadingbg);
        this.add(topPanel);
        this.add(pass);
        this.add(mPass);
        this.add(name);
        this.add(mName);
        this.add(twopass);
        this.add(mTwoPass);
        this.add(bottomPanel);
        this.addMouseListener(moveWindowListener);
        this.addMouseMotionListener(moveWindowListener);
        mAlertPop = new JPopupMenu();
        mAlertLabel = new JLabel();
        mAlertLabel.setFont(FONT_12_BOLD);
        mAlertPop.add(mAlertLabel);
    }

    public static void main(String[] args) {
        Register r = new Register(null);
        r.setVisible(true);
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
            ChatService mService = ChatService.getInstance();
            mService.userRegister(name, pass);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mOK) {
            startRegister();
        }
    }

}
