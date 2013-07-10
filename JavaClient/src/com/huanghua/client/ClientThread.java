
package com.huanghua.client;

import com.huanghua.pojo.User;
import com.huanghua.view.MainFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread extends Thread {
    private Socket mSocket;
    private DataInputStream mDis;
    private DataOutputStream mDos;
    private boolean mFlag = true;
    private MainFrame mFrame = null;

    public ClientThread(MainFrame frame) {
        mFrame = frame;
    }

    public ClientThread() {

    }

    @Override
    public void run() {
        try {
            mSocket = new Socket("192.168.1.94", 12345);
            mDis = new DataInputStream(mSocket.getInputStream());
            mDos = new DataOutputStream(mSocket.getOutputStream());
            userLogin();
            mFrame.startChatServer();
            while (mFlag) {
                String msg = mDis.readUTF();
                if (msg != null && msg.startsWith("<#SENDUSERLIST#>")) {
                    msg = msg.substring(16);
                    String[] type = msg.split("\\|");
                    int size = Integer.parseInt(type[0]);
                    System.out.println("size:" + size);
                    for (int i = 0; i < size; i++) {
                        String temp = mDis.readUTF();
                        String[] user = temp.split("\\|");
                        User u = new User(user[0], user[1], Integer.parseInt(user[2]), user[3]);
                        mFrame.addUser(u);
                    }
                    String id = mDis.readUTF();
                    User u = new User("", id, mFrame.getPort(), mFrame.getName());
                    mFrame.setMySelf(u);
                } else if (msg != null && msg.startsWith("<#USER_OFFLINE#>")) {
                    close();
                } else if (msg != null && msg.startsWith("<#SENDUSEROFF#>")) {
                    String temp = mDis.readUTF();
                    String[] user = temp.split("\\|");
                    User u = new User(user[0], user[1], Integer.parseInt(user[2]), user[3]);
                    mFrame.removeUser(u);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void userLogin() {
        try {
            mDos.writeUTF("<#USERLOGIN#>" + mFrame.getName() + "|" + mFrame.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserList() {
        try {
            mDos.writeUTF("<#GET_USERLIST#>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void offLine() {
        try {
            if (mDos != null) {
                mDos.writeUTF("<#USER_OFFLINE#>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            mFlag = false;
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
