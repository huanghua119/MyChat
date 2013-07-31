
package com.huanghua.client;

import com.huanghua.pojo.User;
import com.huanghua.service.ChatService;
import com.huanghua.util.Configuration;

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
    private ChatService mService = null;

    public ClientThread(ChatService service) {
        mService = service;
    }

    public ClientThread() {

    }

    @Override
    public void run() {
        try {
            mSocket = new Socket(Configuration.SERVER_IP, Configuration.SERVER_PORT);
            mDis = new DataInputStream(mSocket.getInputStream());
            mDos = new DataOutputStream(mSocket.getOutputStream());
            userLogin(mService.getMySelf().getId(), mService.getMySelf().getPassword());
            while (mFlag) {
                String msg = mDis.readUTF();
                if (msg != null && msg.startsWith("<#SENDUSERLIST#>")) {
                    msg = msg.substring(16);
                    String[] type = msg.split("\\|");
                    int size = Integer.parseInt(type[0]);
                    for (int i = 0; i < size; i++) {
                        String temp = mDis.readUTF();
                        String[] user = temp.split("\\|");
                        User u = new User(user[0], user[1], Integer.parseInt(user[2]));
                        mService.addUser(u);
                    }
                } else if (msg != null && msg.startsWith("<#USER_OFFLINE#>")) {
                    close();
                } else if (msg != null && msg.startsWith("<#SENDUSEROFF#>")) {
                    String temp = mDis.readUTF();
                    String[] user = temp.split("\\|");
                    User u = new User(user[0], user[1], Integer.parseInt(user[2]));
                    mService.udpateUser(u);
                } else if (msg != null && msg.startsWith("<#GETMESSAGE#>")) {
                    String id = msg.substring(14);
                    String context = mDis.readUTF();
                    mService.setMessageById(context, id);
                } else if (msg != null && msg.startsWith("<#USERERROR#>")) {
                    msg = msg.substring(13);
                    String[] temp = msg.split("\\|");
                    mService.setError(temp[0], temp[1]);
                } else if (msg != null && msg.startsWith("<#USERPASSERROR#>")) {
                    mService.loginFail("passerror");
                } else if (msg != null && msg.startsWith("<#USERNOTFIND#>")) {
                    mService.loginFail("usernotfind");
                } else if (msg != null && msg.startsWith("<#USERLOGINSUCCES#>")) {
                    String[] self = mDis.readUTF().split("\\|");
                    User u = new User(self[0], self[1], self[2], Integer.parseInt(self[3]));
                    mService.setMySelf(u);
                    mService.loginSuccess();
                } else if (msg != null && msg.startsWith("<#FORCEOFFLINE#>")) {
                    mDos.writeUTF("<#FORCEOFFLINEOK#>");
                    mService.forceOffLine();
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

    public void userLogin(String id, String password) {
        try {
            mDos.writeUTF("<#USERLOGIN#>" + id + "|" + password);
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

    public void sendMessage(User u, String msg) {
        try {
            mDos.writeUTF("<#SENDMESSAGE#>" + u.getId());
            mDos.writeUTF(msg);
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
