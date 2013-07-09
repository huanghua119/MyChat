
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
            sendUserList();
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
                        User u = new User(user[0], user[1], Integer.parseInt(user[2]));
                        mFrame.addUser(u);
                    }
                } else if (msg != null && msg.startsWith("<#USER_OFFLINE#>")) {
                    close();
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
    public void sendUserList() {
        try {
            mDos.writeUTF("<#GET_USERLIST#>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void offLine() {
        try {
            mDos.writeUTF("<#USER_OFFLINE#>");
            //close();
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
