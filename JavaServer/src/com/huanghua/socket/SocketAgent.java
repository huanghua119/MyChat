
package com.huanghua.socket;

import com.huanghua.dao.DBUtil;
import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.view.MainFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SocketAgent extends Thread {
    private Socket mSocket;
    private DataOutputStream mDos;
    private DataInputStream mDis;
    private boolean mFlag = true;
    private MainFrame mFrame = null;
    private User mCurrent = null;

    public SocketAgent(Socket socket, MainFrame frame) {
        this.mSocket = socket;
        this.mFrame = frame;
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
                    msg = msg.substring(13);
                    String[] temp = msg.split("\\|");
                    String id = temp[0];
                    String pass = temp[1];
                    userLogin(id, pass);
                } else if (msg != null && msg.startsWith("<#SENDMESSAGE#>")) {
                    String id = msg.substring(15);
                    String context = mDis.readUTF();
                    mFrame.sendContextByIdToUser(context, id, mCurrent);
                } else if (msg != null && msg.startsWith("<#USERREGISTER#>")) {
                    String temp[] = mDis.readUTF().split("\\|");
                    userRegister(new User(temp[0], temp[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }

        }
    }

    public void userRegister(User u) {
    }

    public void userLogin(String id, String pass) {
        String sql = "select * from User where userId=" + id;
        ResultSet rs = DBUtil.executeQuery(sql);
        try {
            if (rs.next()) {
                String password = rs.getString("userPass");
                if (password.equals(pass)) {
                    String name = rs.getString("userName");
                    String ip = mSocket.getInetAddress().toString().replace("/", "");
                    mFrame.setMessage(Resource.getString("newPersor") + ip + "|" + id);
                    mCurrent = new User();
                    mCurrent.setIp(ip);
                    mCurrent.setId(id);
                    mCurrent.setName(name);
                    mCurrent.setPassword(pass);
                    mCurrent.setsAgent(this);
                    mFrame.addUser(mCurrent);
                    sendLoginSucces();
                    mFrame.sendUserList();
                } else {
                    mDos.writeUTF("<#USERPASSERROR#>");
                    close();
                }
            } else {
                mDos.writeUTF("<#USERNOTFIND#>");
                close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLoginSucces() {
        try {
            mDos.writeUTF("<#USERLOGINSUCCES#>");
            mDos.writeUTF(mCurrent.getId() + "|" + mCurrent.getName() + "|" + mCurrent.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
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
