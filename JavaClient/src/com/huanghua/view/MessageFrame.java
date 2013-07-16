
package com.huanghua.view;

import com.huanghua.client.ClientThread;
import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.service.ChatService;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class MessageFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 250;
    private static final int GAME_HEIGHT = 450;

    private JTextField mMessage;
    private JButton mSendButton;
    private JTextArea mMessageList;
    private User mCurrent;
    private ClientThread mChlientThread;
    private ChatService mService;

    public MessageFrame(User u, ChatService service, ClientThread client) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        mChlientThread = client;
        mCurrent = u;
        mService = service;
        this.setTitle(mCurrent.getName());
        this.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });

        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        mMessage = new JTextField();
        mMessage.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        mSendButton = new JButton(Resource.getString("send"));
        mSendButton.addActionListener(this);
        topPanel.setBorder(new EtchedBorder());
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(mMessage);
        topPanel.add(mSendButton);
        this.add(topPanel, BorderLayout.SOUTH);
        mMessageList = new JTextArea();
        mMessageList.setEditable(false);
        this.add(new JScrollPane(mMessageList), BorderLayout.CENTER);
    }

    public void startChat() {
        this.setVisible(true);
        this.toFront();
    }

    public User getChatUser() {
        return this.mCurrent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (mSendButton == e.getSource()) {
            sendMessage();
        }
    }

    private void sendMessage() {
        String msg = mMessage.getText();
        mChlientThread.sendMessage(mCurrent, msg);
        setMessage(msg, mService.getMySelf());
        mMessage.setText("");
    }

    public void setMessage(String message, User u) {
        if (!this.isVisible()) {
            setVisible(true);
            toFront();
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("H:m:ss");
        String time = sdf.format(date);
        String time2 = u.getName() + "(" + u.getId() + ") "
                + time;
        this.mMessageList.append(time2 + "\n");
        this.mMessageList.append("       " + message + "\n");
    }

}
