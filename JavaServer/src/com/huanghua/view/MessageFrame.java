
package com.huanghua.view;

import com.huanghua.i18n.Resource;
import com.huanghua.socket.SocketAgent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class MessageFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 250;
    private static final int GAME_HEIGHT = 450;
    public static boolean sIsListenter = false;

    private JTextField mMessage;
    private JButton mSendButton;
    private JTextArea mMessageList;
    private SocketAgent mAgent;

    public MessageFrame(SocketAgent agent) {
        this.setTitle(Resource.getStringForSet("frame_title"));
        this.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        mMessage = new JTextField();
        mSendButton = new JButton(Resource.getStringForSet("send"));
        mSendButton.addActionListener(this);
        topPanel.setBorder(new EtchedBorder());
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(mMessage);
        topPanel.add(mSendButton);
        this.add(topPanel, BorderLayout.SOUTH);
        mMessageList = new JTextArea();
        mMessageList.setEditable(false);
        this.add(new JScrollPane(mMessageList), BorderLayout.CENTER);
        mAgent = agent;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (mSendButton == e.getSource()) {
            mAgent.sendMessage(mMessage.getText());
            mMessage.setText("");
        }
    }

    public void setMessage(String message) {
        this.mMessageList.append(message + "\n");
    }

}
