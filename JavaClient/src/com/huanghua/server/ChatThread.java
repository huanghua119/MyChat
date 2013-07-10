
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
                    System.out.println("id:" + id + " name:" + mCurrent.getName());
                    if (mMessageFrame == null) {
                        mMessageFrame = new MessageFrame(mCurrent);
                        mMessageFrame.setVisible(true);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }

        }
    }

    public void close() {
        mFlag = false;
        try {
            if (mDis != null) {
                mDis.close();
            }
            if (mDos != null) {
                mDos.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
