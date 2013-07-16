package com.huanghua.view;

import com.huanghua.i18n.Resource;
import com.huanghua.util.ImageUtil;
import com.huanghua.util.NumberDocument;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Login extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 376;
    private static final int GAME_HEIGHT = 282;

    private ImageIcon mLoginbg = new ImageIcon(
            ImageUtil.getImage("image/login_bg.png"));
    private ImageIcon mUserImageDefalut = new ImageIcon(
            ImageUtil.getImage("image/qq_defalut.png"));
    private ImageIcon mLoginFocus = new ImageIcon(ImageUtil
            .getImage("image/login_btn_focus.png"));
    private ImageIcon mLoginDown = new ImageIcon(ImageUtil
            .getImage("image/login_btn_down.png"));
    private ImageIcon mLoginHighlight = new ImageIcon(ImageUtil
            .getImage("image/login_btn_highlight.png"));

    private Font FONT_12_BOLD = new Font("宋体", 0, 12);
    private JButton mLogin;
    private JTextField mUserId;
    private JPasswordField mUserPass;
    private JLabel mUserImage;
    private NumberDocument numberDocument = new NumberDocument();

    private MouseAdapter moveWindowListener = new MouseAdapter() {

        private Point lastPoint = null;

        @Override
        public void mousePressed(MouseEvent e) {
            lastPoint = e.getLocationOnScreen();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point point = e.getLocationOnScreen();
            int offsetX = point.x - lastPoint.x;
            int offsetY = point.y - lastPoint.y;
            Rectangle bounds = Login.this.getBounds();
            bounds.x += offsetX;
            bounds.y += offsetY;
            Login.this.setBounds(bounds);
            lastPoint = point;
        }
    };

    public Login() {
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

        mUserImage = new JLabel();
        mUserImage.setIcon(mUserImageDefalut);
        mUserImage.setBounds(20, 130, 82, 82);

        mUserId = new JTextField();
        mUserId.setDocument(numberDocument);
        mUserId.setBorder(null);
        mUserId.setBounds(115, 130, 200, 25);

        JLabel register = new JLabel("<html><font color=blue>" + Resource.getString("register")
                + "</font></html>");
        register.setCursor(new Cursor(Cursor.HAND_CURSOR));
        register.setFont(FONT_12_BOLD);
        register.setBounds(320, 130, 150, 25);

        mUserPass = new JPasswordField();
        mUserPass.setBorder(null);
        mUserPass.setBounds(115, 165, 200, 25);

        JLabel findPass = new JLabel("<html><font color=blue>" + Resource.getString("findPass")
                + "</font></html>");
        findPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        findPass.setFont(FONT_12_BOLD);
        findPass.setBounds(320, 165, 150, 25);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(231, 236, 240));
        bottomPanel.setBounds(0, getHeight() - 50, getWidth(), 50);
        mLogin = new JButton();
        mLogin.setIcon(mLoginFocus);
        mLogin.setPressedIcon(mLoginDown);
        mLogin.setRolloverIcon(mLoginHighlight);
        mLogin.setBorderPainted(false);
        mLogin.setContentAreaFilled(false);
        mLogin.setBounds(getWidth() / 2 - 78 / 2, getHeight() - 40, 78, 30);
        mLogin.addActionListener(this);
        this.setContentPane(loadingbg);
        this.add(topPanel);
        this.add(mUserImage);
        this.add(mUserId);
        this.add(register);
        this.add(mUserPass);
        this.add(findPass);
        this.add(mLogin);
        this.add(bottomPanel);
        this.addMouseListener(moveWindowListener);
        this.addMouseMotionListener(moveWindowListener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mLogin) {
            String userId = mUserId.getText();
            String userPass = new String(mUserPass.getPassword());
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("swing.useSystemFontSettings", "false");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Login login = new Login();
                login.setVisible(true);
            }
        });
    }

}
