
package com.huanghua.client;

import com.huanghua.pojo.User;
import com.huanghua.view.MainFrame;
import com.huanghua.view.MessageFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient extends Thread {

    private User mCurrent;
    private DataInputStream mDis;
    private DataOutputStream mDos;
    private Socket mSocket;

    private MainFrame mFrame;
    private MessageFrame mMessageFrame;
    private boolean mFlag = false;

    public ChatClient(MainFrame frame, User u) {
        mCurrent = u;
        mFrame = frame;
    }

    @Override
    public void run() {
        try {
            mSocket = new Socket(mCurrent.getIp(), mCurrent.getPort());
            mDis = new DataInputStream(mSocket.getInputStream());
            mDos = new DataOutputStream(mSocket.getOutputStream());
            mDos.writeUTF("<#STARTCHAT#>" + mFrame.getMySelf().getId());
            mMessageFrame = new MessageFrame(mCurrent);
            mMessageFrame.setVisible(true);
            String itname = mCurrent.getName();
            mFlag = true;
            while (mFlag) {
                String msg = mDis.readUTF();
                mMessageFrame.setMessage(itname + ":" + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            close();
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
