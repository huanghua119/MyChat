
package com.huanghua.view;

import com.huanghua.client.ChatClient;
import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.server.ChatThread;

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
    private MainFrame mFrame;
    private User mCurrent;
    private ChatClient mChatClient;
    private ChatThread mChatServer;
    private boolean mIsClient;

    public MessageFrame(User u, MainFrame frame, boolean isClient) {
        mIsClient = isClient;
        mCurrent = u;
        mFrame = frame;
        this.setTitle(mCurrent.getName());
        this.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (mIsClient) {
                    mChatClient.sendMessage("<#CLIENTCLOSE#>");
                } else {
                    mChatServer.sendMessage("<#SERVERCLOSE#>");
                }
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
    }

    public void startChat() {
        this.setVisible(true);
        this.toFront();
    }

    public void setToServer(ChatThread ct) {
        if (mChatServer == null) {
            if (mChatClient != null) {
                mChatClient.close();
                mChatClient = null;
            }
            mChatServer = ct;
            this.mIsClient = false;
        }
    }
    public void setToClient() {
        mChatServer = null;
        System.out.println("chatclient:" + mChatClient);
        if (mChatClient == null) {
            mChatClient = new ChatClient(mFrame, this, mCurrent);
        }
        if (!mChatClient.isRun()) {
            System.out.println("Priority:" + mChatClient.getPriority());
            mChatClient.start();
        }
        this.mIsClient = true;
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
        setMessage(msg, mFrame.getMySelf());
        if (mIsClient) {
            mChatClient.sendMessage(msg);
        } else {
            mChatServer.sendMessage(msg);
        }
        mMessage.setText("");
    }

    public void setMessage(String message, User u) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("H:m:ss");
        String time = sdf.format(date);
        String time2 = u.getName() + "(" + u.getId() + ") "
                + time;
        this.mMessageList.append(time2 + "\n");
        this.mMessageList.append("       " + message + "\n");
    }

}
