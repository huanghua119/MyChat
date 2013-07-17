package com.huanghua.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.huanghua.util.ImageUtil;

public class TopPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JButton mClose;
    private JButton mMax;
    private JButton mMini;
    private JFrame mJFrame;
    private boolean mIsMax = false;

    public TopPanel(JFrame frame) {
        this.mJFrame = frame;
        setLayout(new BorderLayout());
        JPanel toprightPanel = new JPanel();
        toprightPanel.setLayout(new BoxLayout(toprightPanel, BoxLayout.X_AXIS));
        toprightPanel.setOpaque(false);
        mMini = new JButton();
        mMini.setPreferredSize(new Dimension(24, 18));
        mMini.setIcon(new ImageIcon(ImageUtil
                .getImage("image/btn_mini_normal.png")));
        mMini.setRolloverIcon(new ImageIcon(ImageUtil
                .getImage("image/btn_mini_highlight.png")));
        mMini.setPressedIcon(new ImageIcon(ImageUtil
                .getImage("image/btn_mini_down.png")));
        mMini.setBorderPainted(false);
        mMini.setContentAreaFilled(false);
        mMini.addActionListener(this);
        mMax = new JButton();
        mMax.setPreferredSize(new Dimension(24, 18));
        mMax.setIcon(new ImageIcon(ImageUtil
                .getImage("image/btn_max_normal.png")));
        mMax.setRolloverIcon(new ImageIcon(ImageUtil
                .getImage("image/btn_max_highlight.png")));
        mMax.setPressedIcon(new ImageIcon(ImageUtil
                .getImage("image/btn_max_down.png")));
        mMax.setBorderPainted(false);
        mMax.setContentAreaFilled(false);
        mMax.addActionListener(this);
        mClose = new JButton();
        mClose.setPreferredSize(new Dimension(38, 18));
        mClose.setIcon(new ImageIcon(ImageUtil
                .getImage("image/btn_close_normal.png")));
        mClose.setRolloverIcon(new ImageIcon(ImageUtil
                .getImage("image/btn_close_highlight.png")));
        mClose.setPressedIcon(new ImageIcon(ImageUtil
                .getImage("image/btn_close_down.png")));
        mClose.setBorderPainted(false);
        mClose.setContentAreaFilled(false);
        mClose.addActionListener(this);
        toprightPanel.add(mMini);
        toprightPanel.add(mMax);
        toprightPanel.add(mClose);
        add(toprightPanel, BorderLayout.EAST);
        setBounds(0, 0, mJFrame.getWidth(), 18);
        setOpaque(false);
    }

    public void addTile(String title) {
        add(new JLabel(title), BorderLayout.WEST);
    }
    public void hideMaxButton() {
        mMax.setVisible(false);
    }

    public void addCloseButtonListener(ActionListener listener) {
        mClose.removeActionListener(this);
        mClose.addActionListener(listener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mClose) {
            System.exit(0);
        } else if (e.getSource() == mMini) {
            mJFrame.setExtendedState(JFrame.ICONIFIED);
        } else if (e.getSource() == mMax) {
            if (mJFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                mJFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                mJFrame.setExtendedState(JFrame.NORMAL);
            }
        }
    }
}
