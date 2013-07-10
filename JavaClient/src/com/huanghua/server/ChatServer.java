
package com.huanghua.server;

import com.huanghua.view.MainFrame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements Runnable {

    private MainFrame mFrame;
    private ServerSocket mSocket;
    private boolean mFlag = false;
    private List<ChatThread> mChatList;

    public ChatServer(MainFrame frame) {
        this.mFrame = frame;
    }

    @Override
    public void run() {
        try {
            mSocket = new ServerSocket(mFrame.getPort());
            mChatList = new ArrayList<ChatThread>();
            mFlag = true;
            while (mFlag) {
                Socket s = mSocket.accept();
                ChatThread cThread = new ChatThread(mFrame, s);
                cThread.start();
                mChatList.add(cThread);
            }
        } catch (Exception e) {
            e.printStackTrace();
            close();
        }
    }

    public void setFlag(boolean flag) {
        this.mFlag = flag;
    }

    public boolean getFlag() {
        return mFlag;
    }

    public void close() {
        if (mSocket != null) {
            mFlag = false;
            if (mChatList != null && mChatList.size() > 0) {
                for (ChatThread thread : mChatList) {
                    thread.close();
                }
                mChatList.clear();
                mChatList = null;
            }
            try {
                mSocket.close();
                mSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
