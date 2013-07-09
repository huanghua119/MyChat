
package com.huanghua.socket;

import com.huanghua.view.MainFrame;
import com.huanghua.view.MessageFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketAgent extends Thread {
    private Socket mSocket;
    private DataOutputStream mDos;
    private DataInputStream mDis;
    private boolean mFlag = true;
    private MainFrame mFrame = null;
    private MessageFrame mMessFrame = null;

    public SocketAgent(Socket socket, MainFrame frame) {
        this.mSocket = socket;
        this.mFrame = frame;
        try {
            mFlag = true;
            mDos = new DataOutputStream(socket.getOutputStream());
            mDis = new DataInputStream(socket.getInputStream());
            mMessFrame = new MessageFrame(this);
            mMessFrame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // ScannerThread mScannerThread = new ScannerThread(mDos);
        // mScannerThread.start();
        while (mFlag) {
            try {
                String msg = mDis.readUTF();
                mMessFrame.setMessage("it:" + msg);
            } catch (IOException e) {
                mFlag = false;
                mFrame.setMessage(e.getMessage());
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
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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

    public void sendMessage(String text) {
        if (mDos != null) {
            try {
                mMessFrame.setMessage("me:" + text);
                mDos.writeUTF(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
