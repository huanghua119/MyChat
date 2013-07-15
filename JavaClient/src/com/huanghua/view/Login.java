
package com.huanghua.view;

import com.huanghua.i18n.Resource;
import com.huanghua.util.ImageUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Login extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 376;
    private static final int GAME_HEIGHT = 282;
    private JButton mClose;
    private JButton mMini;

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
        this.setTitle(Resource.getString("login_title"));
        this.setResizable(false);
        this.setUndecorated(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(new JLabel(Resource.getString("login_title")), BorderLayout.WEST);
        JPanel toprightPanel = new JPanel();
        toprightPanel.setLayout(new BoxLayout(toprightPanel, BoxLayout.X_AXIS));
        mMini = new JButton();
        mMini.setPreferredSize(new Dimension(25, 18));
        mMini.setIcon(new ImageIcon(ImageUtil.getImage("image/btn_mini_normal.png")));
        mMini.setRolloverIcon(new ImageIcon(ImageUtil.getImage("image/btn_mini_highlight.png")));
        mMini.setPressedIcon(new ImageIcon(ImageUtil.getImage("image/btn_mini_down.png")));
        mMini.setBorderPainted(false);
        mMini.setContentAreaFilled(false);
        mMini.addActionListener(this);
        mClose = new JButton();
        mClose.setPreferredSize(new Dimension(38, 18));
        mClose.setIcon(new ImageIcon(ImageUtil.getImage("image/btn_close_normal.png")));
        mClose.setRolloverIcon(new ImageIcon(ImageUtil.getImage("image/btn_close_highlight.png")));
        mClose.setPressedIcon(new ImageIcon(ImageUtil.getImage("image/btn_close_down.png")));
        mClose.setBorderPainted(false);
        mClose.setContentAreaFilled(false);
        mClose.addActionListener(this);
        toprightPanel.add(mMini);
        toprightPanel.add(mClose);
        topPanel.add(toprightPanel, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);
        this.addMouseListener(moveWindowListener);
        this.addMouseMotionListener(moveWindowListener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mClose) {
            System.exit(0);
        } else if (e.getSource() == mMini) {
            setExtendedState(JFrame.ICONIFIED);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Login login = new Login();
                login.setVisible(true);
            }
        });
    }

}
