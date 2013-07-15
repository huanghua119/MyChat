
package com.huanghua.socket;

import com.huanghua.pojo.User;
import com.huanghua.view.MainFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class SocketAgent extends Thread {
    private Socket mSocket;
    private DataOutputStream mDos;
    private DataInputStream mDis;
    private boolean mFlag = true;
    private MainFrame mFrame = null;
    private User mCurrent = null;

    public SocketAgent(Socket socket, MainFrame frame, User u) {
        this.mSocket = socket;
        this.mFrame = frame;
        this.mCurrent = u;
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
                if (msg != null && msg.startsWith("<#GET_USERLIST#>")) {
                    mFrame.sendUserList();
                } else if (msg != null && msg.startsWith("<#USER_OFFLINE#>")) {
                    mDos.writeUTF("<#USER_OFFLINE#>");
                    close();
                    mFrame.userOffLine(mCurrent);
                } else if (msg != null && msg.startsWith("<#USERLOGIN#>")) {
                    String name = msg.substring(13);
                    mCurrent.setName(name);
                    mFrame.sendUserList();
                } else if (msg != null && msg.startsWith("<#SENDMESSAGE#>")) {
                    String id = msg.substring(15);
                    String context = mDis.readUTF();
                    mFrame.sendContextByIdToUser(context, id, mCurrent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }

        }
    }

    public void sendUserOffline(User offline) {
        try {
            mDos.writeUTF("<#SENDUSEROFF#>");
            mDos.writeUTF(offline.getIp() + "|" + offline.getId() + "|" + offline.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserList() {
        List<User> list = mFrame.getUserList();
        try {
            mDos.writeUTF("<#SENDUSERLIST#>" + (list.size() - 1));
            for (User u : list) {
                if (!u.getId().equals(mCurrent.getId())) {
                    mDos.writeUTF(u.getIp() + "|" + u.getId() + "|" + u.getName());
                }
            }
            mDos.writeUTF(mCurrent.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg, User toUser) {
        try {
            mDos.writeUTF("<#GETMESSAGE#>" + toUser.getId());
            mDos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendError(String msg, String id) {
        try {
            mDos.writeUTF("<#USERERROR#>" + id + "|" + msg);
            mDos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
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
