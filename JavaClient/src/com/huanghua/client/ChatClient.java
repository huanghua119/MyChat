
package com.huanghua.client;

import com.huanghua.pojo.User;
import com.huanghua.view.MainFrame;
import com.huanghua.view.MessageFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient implements Runnable {

    private User mCurrent;
    private DataInputStream mDis;
    private DataOutputStream mDos;
    private Socket mSocket;

    private MainFrame mFrame;
    private MessageFrame mMessageFrame;
    private boolean mFlag = false;

    public ChatClient(MainFrame frame, MessageFrame mf, User u) {
        mCurrent = u;
        mFrame = frame;
        mMessageFrame = mf;
        mFlag = false;
    }

    @Override
    public void run() {
        try {
            mSocket = new Socket(mCurrent.getIp(), mCurrent.getPort());
            mDis = new DataInputStream(mSocket.getInputStream());
            mDos = new DataOutputStream(mSocket.getOutputStream());
            mDos.writeUTF("<#STARTCHAT#>" + mFrame.getMySelf().getId());
            mFlag = true;
            while (mFlag) {
                String msg = mDis.readUTF();
                if (msg != null && msg.startsWith("<#SERVERCLOSE#>")) {
                    sendMessage("<#SERVERCLOSEOK#>");
                    close();
                } else if (msg != null && msg.startsWith("<#CLIENTCLOSEOK#>")) {
                    close();
                } else {
                    mMessageFrame.setMessage(msg, mCurrent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public boolean isRun() {
        return mFlag;
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
    }

    public void sendMessage(String msg) {
        try {
            if (mDos != null && !mSocket.isClosed()) {
                mDos.writeUTF(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
