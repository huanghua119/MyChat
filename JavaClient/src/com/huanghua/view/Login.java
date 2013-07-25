package com.huanghua.view;

import com.huanghua.i18n.Resource;
import com.huanghua.service.ChatService;
import com.huanghua.util.Configuration;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
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
    private ImageIcon mCheckBox = new ImageIcon(ImageUtil
            .getImage("image/Check_01.png"));
    private ImageIcon mCheckBoxCheck = new ImageIcon(ImageUtil
            .getImage("image/Check_02.png"));
    private ImageIcon mCheckBoxSelect = new ImageIcon(ImageUtil
            .getImage("image/CheckBox_border.png"));

    private Font FONT_12_BOLD = new Font("宋体", 0, 12);
    private JButton mLogin;
    private JTextField mUserId;
    private JPasswordField mUserPass;
    private JLabel mRegister;
    private JLabel mFindPass;
    private JLabel mUserImage;
    private JLabel mRememberLabel;
    private JLabel mAutoLabel;
    private JCheckBox mRemember;
    private JCheckBox mAutoLogin;
    private JPopupMenu mAlertPop;
    private JLabel mAlertLabel;
    private NumberDocument numberDocument = new NumberDocument();
    private ChatService mService;
    private Register mRegisterFrame;

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
                if (mAlertPop.isShowing()) {
                    mAlertPop.setVisible(false);
                }
                return;
            }
            int offsetX = point.x - lastPoint.x;
            int offsetY = point.y - lastPoint.y;
            Rectangle bounds = Login.this.getBounds();
            bounds.x += offsetX;
            bounds.y += offsetY;
            Login.this.setBounds(bounds);
            lastPoint = point;
        }
    };

    private KeyAdapter mKeyAdapter = new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                startLogin();
            }
        }
    };

    private MouseAdapter mButtonMouseAdapter = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == mRememberLabel) {
                mRemember.setSelected(!mRemember.isSelected());
            } else if (e.getSource() == mAutoLabel) {
                mAutoLogin.setSelected(!mAutoLogin.isSelected());
            } else if (e.getSource() == mRegister) {
                if (mRegisterFrame == null) {
                    mRegisterFrame = new Register(Login.this);
                }
                mRegisterFrame.setLocation(getX(), getY());
                mRegisterFrame.setVisible(true);
                mRegisterFrame.toFront();
                setVisible(false);
            }
        }

        public void mouseEntered(MouseEvent e) {
            if (e.getSource() == mRememberLabel) {
                mRemember.setIcon(mCheckBoxSelect);
            } else if (e.getSource() == mAutoLabel) {
                mAutoLogin.setIcon(mCheckBoxSelect);
            }
        }

        public void mouseExited(MouseEvent e) {
            if (e.getSource() == mRememberLabel) {
                mRemember.setIcon(mCheckBox);
            } else if (e.getSource() == mAutoLabel) {
                mAutoLogin.setIcon(mCheckBox);
            }
        }
    };

    public Login() {
        this.setIconImage(ImageUtil.getImage("image/icon.png"));
        this.setTitle(Resource.getString("login_title"));
        this.setResizable(false);
        this.setUndecorated(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH,
                GAME_HEIGHT);
        this.setLayout(null);
        this.addWindowListener(new WindowAdapter() {
            public void windowIconified(WindowEvent e) {
                mService.windowIconified(Login.this);
            }
        });

        JLabel loadingbg = new JLabel(mLoginbg);
        loadingbg.setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);

        TopPanel topPanel = new TopPanel(this);
        topPanel.hideMaxButton();

        mUserImage = new JLabel();
        mUserImage.setIcon(mUserImageDefalut);
        mUserImage.setBounds(20, 130, 82, 82);

        mUserId = new JTextField();
        mUserId.setDocument(numberDocument);
        mUserId.setBorder(null);
        mUserId.setBounds(115, 130, 200, 25);
        mUserId.addKeyListener(mKeyAdapter);

        mRegister = new JLabel("<html><font color=blue>" + Resource.getString("register")
                + "</font></html>");
        mRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mRegister.setFont(FONT_12_BOLD);
        mRegister.setBounds(320, 130, 150, 25);
        mRegister.addMouseListener(mButtonMouseAdapter);

        mUserPass = new JPasswordField();
        mUserPass.setBorder(null);
        mUserPass.setBounds(115, 165, 200, 25);
        mUserPass.addKeyListener(mKeyAdapter);

        mFindPass = new JLabel("<html><font color=blue>" + Resource.getString("findPass")
                + "</font></html>");
        mFindPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mFindPass.setFont(FONT_12_BOLD);
        mFindPass.setBounds(320, 165, 150, 25);
        mFindPass.addMouseListener(mButtonMouseAdapter);

        mRemember = new JCheckBox();
        mRemember.setBorder(null);
        mRemember.setIcon(mCheckBox);
        mRemember.setSelectedIcon(mCheckBoxCheck);
        mRemember.setRolloverIcon(mCheckBoxSelect);
        mRemember.setBounds(115, 198, 15, 15);
        mRememberLabel = new JLabel("<html><font color=black>"
                + Resource.getString("remember") + "</font></html>");
        mRememberLabel.setFont(FONT_12_BOLD);
        mRememberLabel.setBounds(133, 193, 50, 25);
        mRememberLabel.addMouseListener(mButtonMouseAdapter);

        mAutoLogin = new JCheckBox();
        mAutoLogin.setBorder(null);
        mAutoLogin.setIcon(mCheckBox);
        mAutoLogin.setSelectedIcon(mCheckBoxCheck);
        mAutoLogin.setRolloverIcon(mCheckBoxSelect);
        mAutoLogin.setBounds(200, 198, 15, 15);
        mAutoLabel = new JLabel("<html><font color=black>"
                + Resource.getString("autologin") + "</font></html>");
        mAutoLabel.setFont(FONT_12_BOLD);
        mAutoLabel.setBounds(218, 193, 50, 25);
        mAutoLabel.addMouseListener(mButtonMouseAdapter);

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
        this.add(mRegister);
        this.add(mUserPass);
        this.add(mFindPass);
        this.add(mRemember);
        this.add(mRememberLabel);
        this.add(mAutoLogin);
        this.add(mAutoLabel);
        this.add(mLogin);
        this.add(bottomPanel);
        this.addMouseListener(moveWindowListener);
        this.addMouseMotionListener(moveWindowListener);
        mService = ChatService.getInstance();
        mService.setLogin(this);
        mAlertPop = new JPopupMenu();
        mAlertLabel = new JLabel();
        mAlertLabel.setFont(FONT_12_BOLD);
        mAlertPop.add(mAlertLabel);
        mService.addSystemTray();
        getConfiguragtion();
    }

    public void getConfiguragtion() {
        List<String> list = Configuration.sUserList;
        if (list != null && list.size() > 0) {
            String s = list.get(list.size() - 1);
            String[] temp = s.split("::");
            String id = temp[0];
            String pass = temp[1];
            boolean isRemeber = Boolean.parseBoolean(temp[2]);
            boolean isAuto = Boolean.parseBoolean(temp[3]);
            mUserId.setText(id);
            if (isRemeber) {
                mUserPass.setText(pass);
                mRemember.setSelected(true);
            }
            if (isAuto) {
                //mAutoLogin.setSelected(true);
                //autoLogin(id, pass);
            }
        }
    }

    public void setVisible(boolean vFlag){
        super.setVisible(vFlag);
        if (vFlag) {
            String tempId = mUserId.getText();
            String pass = new String(mUserPass.getPassword());
            if (tempId == null || "".equals(tempId) || pass == null || "".equals(pass)) {
                mUserId.requestFocus();
            } else {
                mUserPass.requestFocus();
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mLogin) {
            startLogin();
        }
    }

    private void startLogin() {
        String userId = mUserId.getText();
        String userPass = new String(mUserPass.getPassword());
        if (userId == null || "".equals(userId)) {
            mAlertLabel.setText(Resource.getStringForColor("notid", "black"));
            mAlertPop.show(mUserId, -25, mUserId.getHeight() / 2);
        } else if (userPass == null || "".equals(userPass)) {
            mAlertLabel.setText(Resource.getStringForColor("notpass", "black"));
            mAlertPop.show(mUserPass, -25, mUserPass.getHeight() / 2);
        } else {
            mLogin.setEnabled(false);
            mService.login(userId, userPass);
            Configuration.sIsSavePass = mRemember.isSelected();
            Configuration.sIsAutoLogin = mAutoLogin.isSelected();
        }
    }

    public void loginFail(String error) {
        mAlertLabel.setText(Resource.getStringForColor(error, "black"));
        if (error.equals("passerror")) {
            mAlertPop.show(mUserPass, -25, mUserPass.getHeight() / 2);
        } else {
            mAlertPop.show(mUserId, -25, mUserId.getHeight() / 2);
        }
        mLogin.setEnabled(true);
    }

    public void autoLogin(String userId, String pass) {
        mUserId.setText(userId);
        mUserPass.setText(pass);
        startLogin();
    }

    public static void main(String[] args) {
        try {
            System.setProperty("swing.useSystemFontSettings", "false");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
           // UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
           // SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
           // SubstanceLookAndFeel.setCurrentTitlePainter(new FlatTitlePainter());
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
