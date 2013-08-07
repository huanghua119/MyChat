
package com.huanghua.mychat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.huanghua.mychat.service.ChatService;
import com.huanghua.pojo.User;

public class ClientThread implements Runnable {
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
            mSocket = new Socket("huanghua119.xicp.net", 26741);
            mDis = new DataInputStream(mSocket.getInputStream());
            mDos = new DataOutputStream(mSocket.getOutputStream());
            userLogin(mService.getMySelf().getId(), mService.getMySelf().getPassword());
            while (mFlag) {
                String msg = mDis.readUTF();
                if (msg != null && msg.startsWith("<#SENDUSERLIST#>")) {
                    msg = msg.substring(16);
                    String[] type = msg.split("\\|");
                    int size = Integer.parseInt(type[0]);
                    List<User> list = new ArrayList<User>();
                    for (int i = 0; i < size; i++) {
                        String temp = mDis.readUTF();
                        String[] user = temp.split("\\|");
                        User u = new User(user[0], user[1], Integer.parseInt(user[2]));
                        list.add(u);
                    }
                    mService.addUser(list);
                } else if (msg != null && msg.startsWith("<#USER_OFFLINE#>")) {
                    close();
                    mService.goToLogin();
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
                    if (self[4].equals("null")) {
                        self[4] = "";
                    }
                    u.setSignature(self[4]);
                    mService.setMySelf(u);
                    mService.loginSuccess();
                } else if (msg != null && msg.startsWith("<#FORCEOFFLINE#>")) {
                    mDos.writeUTF("<#FORCEOFFLINEOK#>");
                    mService.forceOffLine();
                    close();
                } else if (msg != null && msg.startsWith("<#UPDATE_SIGNATUREOK#>")) {
                    mService.updateSignatureSuccess();
                } else if (msg != null && msg.startsWith("<#UPDATE_SIGNATUREFAIL#>")) {
                    mService.updateSignatureFail();
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

    public void sendToServer(String msg) {
        try {
            mDos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
