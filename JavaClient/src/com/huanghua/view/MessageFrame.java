
package com.huanghua.view;

import com.huanghua.client.ClientThread;
import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.service.ChatService;
import com.huanghua.util.Configuration;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class MessageFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int GAME_WIDTH = 395;
    private static final int GAME_HEIGHT = 516;

    private JTextField mMessage;
    private JButton mSendButton;
    private JButton mCloseButton;
    private JTextPane mMessageList;
    private StyledDocument mMessageDocStyle;
    private User mCurrent;
    private ClientThread mChlientThread;
    private ChatService mService;

    public MessageFrame(User u, ChatService service, ClientThread client) {
        mChlientThread = client;
        mCurrent = u;
        mService = service;
        this.setTitle(mCurrent.getName());
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int) (dim.getWidth() - GAME_WIDTH) / 2,
                (int) (dim.getHeight() - GAME_HEIGHT) / 2, GAME_WIDTH, GAME_HEIGHT);
        this.setMinimumSize(new Dimension(GAME_WIDTH,GAME_HEIGHT));
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
            }
        });

        this.setLayout(new BorderLayout());
        JPanel bottom = new JPanel();
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
        mCloseButton = new JButton(Resource.getString("close"));
        mCloseButton.addActionListener(this);
        bottom.setBorder(new EtchedBorder());
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JPanel bottom2 = new JPanel();
        bottom2.setLayout(new BorderLayout());
        bottom2.setBorder(null);
        JPanel bottom2left = new JPanel();
        bottom2left.setBorder(null);
        bottom2left.setLayout(new BoxLayout(bottom2left, BoxLayout.X_AXIS));
        bottom2left.add(mCloseButton);
        bottom2left.add(createBlackPanel());
        bottom2left.add(mSendButton);
        bottom2left.add(createBlackPanel());
        bottom2.add(bottom2left, BorderLayout.EAST);
        bottom.add(mMessage);
        bottom.add(bottom2);
        this.add(bottom, BorderLayout.SOUTH);
        mMessageDocStyle = new DefaultStyledDocument();
        mMessageList = new JTextPane(mMessageDocStyle);
        createStyle("itTitleStyle", mMessageDocStyle, 12, false, false, false, Color.BLUE, "宋体",
                Configuration.MESSAGE_TITLE_INTEND);
        createStyle("myTitleStyle", mMessageDocStyle, 12, false, false, false,
                new Color(0, 80, 64), "宋体", Configuration.MESSAGE_TITLE_INTEND);
        createStyle("itTextStyle", mMessageDocStyle, 14, false, false, false, Color.black, "宋体",
                Configuration.MESSAGE_TEXT_INTEND);
        createStyle("myTextStyle", mMessageDocStyle, 14, false, false, false, Color.BLUE, "宋体",
                Configuration.MESSAGE_TEXT_INTEND);
        mMessageList.setEditable(false);
        JScrollPane  messageScroll = new JScrollPane(mMessageList);
        this.add(messageScroll, BorderLayout.CENTER);
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
        } else if (mCloseButton ==  e.getSource()) {
            dispose();
        }
    }

    private void sendMessage() {
        String msg = mMessage.getText();
        if (msg == null || "".equals(msg)) {
            return;
        }
        mChlientThread.sendMessage(mCurrent, msg);
        setMessage(msg, mService.getMySelf());
        mMessage.setText("");
    }

    public void setMessage(String message, User u) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
        String time = sdf.format(date);
        String time2 = u.getName() + "    " + time;
        if (u.getId().equals(mService.getMySelf().getId())) {
            insertDoc(mMessageDocStyle, time2 + "\n", "myTitleStyle");
            insertDoc(mMessageDocStyle, message + "\n", "myTextStyle");
        } else {
            insertDoc(mMessageDocStyle, time2 + "\n", "itTitleStyle");
            insertDoc(mMessageDocStyle, message + "\n", "itTextStyle");
        }
        mMessageList.setCaretPosition(mMessageList.getText().length());
    }

    public void createStyle(String styleName, StyledDocument doc, int size,
            boolean bold,
            boolean italic,
            boolean underline, Color color, String fontName, float indent) {
        Style sys = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        try {
            doc.removeStyle(styleName);
        } catch (Exception e) {
        }

        Style s = doc.addStyle(styleName, sys);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_LEFT);
        StyleConstants.setLeftIndent(s, indent);
        StyleConstants.setFontSize(s, size);
        StyleConstants.setBold(s, bold);
        StyleConstants.setItalic(s, italic);
        StyleConstants.setUnderline(s, underline);
        StyleConstants.setForeground(s, color);
        StyleConstants.setFontFamily(s, fontName);
        StyleConstants.setSpaceAbove(s, 1);
        StyleConstants.setSpaceBelow(s, 1);
    }

    public void insertDoc(StyledDocument styledDoc, String content, String currentStyle) {
        try {
            styledDoc.setParagraphAttributes(styledDoc.getLength(), content.length(), styledDoc.getStyle(currentStyle), false);
            styledDoc.insertString(styledDoc.getLength(), content, styledDoc.getStyle(currentStyle));
        } catch (BadLocationException e) {
            System.err.println("BadLocationException: " + e);
        }
    }

    private JPanel createBlackPanel() {
        JPanel blankPanel = new JPanel();
        blankPanel.setOpaque(false);
        JLabel blankLabel = new JLabel(" ");
        blankPanel.add(blankLabel);
        return blankPanel;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (mMessageList != null) {
            mMessageList.setCaretPosition(mMessageList.getText().length());
        }
    }

}
