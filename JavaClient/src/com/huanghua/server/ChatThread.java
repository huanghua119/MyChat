
package com.huanghua.server;

import com.huanghua.pojo.User;
import com.huanghua.view.MainFrame;
import com.huanghua.view.MessageFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatThread extends Thread{

    private MainFrame mFrame;
    private Socket mSocket;
    private boolean mFlag = true;
    private DataOutputStream mDos;
    private DataInputStream mDis;
    private MessageFrame mMessageFrame;
    private User mCurrent;

    public ChatThread(MainFrame frame, Socket socket) {
        this.mFrame = frame;
        this.mSocket = socket;
        try {
            mFlag = true;
            mDos = new DataOutputStream(socket.getOutputStream());
            mDis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (mFlag) {
            try {
                String msg = mDis.readUTF();
                if (msg != null && msg.startsWith("<#STARTCHAT#>")) {
                    String id = msg.substring(13);
                    mCurrent = mFrame.getUserById(id);
                } else if (msg != null && msg.startsWith("<#CLIENTCLOSE#>")) {
                    sendMessage("<#CLIENTCLOSEOK#>");
                    close();
                    if (mMessageFrame != null ) {
                        mMessageFrame.setToClient();
                    }
                } else if (msg != null && msg.startsWith("<#SERVERCLOSEOK#>")) {
                    close();
                } else {
                    if (mMessageFrame == null) {
                        mMessageFrame = mFrame.startChatForServer(mCurrent);
                        mMessageFrame.setToServer(this);
                        mMessageFrame.setVisible(true);
                    }
                    mMessageFrame.setMessage(msg, mCurrent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }

        }
    }

    public void sendMessage(String msg) {
        try {
            if (mDos != null) {
                mDos.writeUTF(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        mFlag = false;
        try {
            if (mDis != null) {
                mDis.close();
                mDis = null;
            }
            if (mDos != null) {
                mDos.close();
                mDos = null;
            }
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.isAlive()) {
            this.interrupt();
        }
    }
}
