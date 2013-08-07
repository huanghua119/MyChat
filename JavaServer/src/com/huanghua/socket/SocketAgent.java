
package com.huanghua.socket;

import com.huanghua.dao.DBUtil;
import com.huanghua.i18n.Resource;
import com.huanghua.pojo.Status;
import com.huanghua.pojo.User;
import com.huanghua.service.ServerService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SocketAgent extends Thread {
    private Socket mSocket;
    private DataOutputStream mDos;
    private DataInputStream mDis;
    private boolean mFlag = true;
    private User mCurrent = null;
    private List<User> mAllFriend = null;
    private ServerService mService;

    public SocketAgent(Socket socket, ServerService service) {
        this.mSocket = socket;
        this.mService = service;
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
                    mService.sendUserList();
                } else if (msg != null && msg.startsWith("<#USER_OFFLINE#>")) {
                    mDos.writeUTF("<#USER_OFFLINE#>");
                    close();
                    mCurrent.setStatus(Status.STATUS_OFFLINE);
                    mService.userOffLine(mCurrent);
                } else if (msg != null && msg.startsWith("<#USERLOGIN#>")) {
                    msg = msg.substring(13);
                    String[] temp = msg.split("\\|");
                    String id = temp[0];
                    String pass = temp[1];
                    userLogin(id, pass);
                } else if (msg != null && msg.startsWith("<#SENDMESSAGE#>")) {
                    String id = msg.substring(15);
                    String context = mDis.readUTF();
                    mService.sendContextByIdToUser(context, id, mCurrent);
                } else if (msg != null && msg.startsWith("<#USERREGISTER#>")) {
                    String temp[] = msg.substring(16).split("\\|");
                    User u = new User(temp[0], temp[1]);
                    u.setSix(Integer.parseInt(temp[2]));
                    userRegister(u);
                } else if (msg != null && msg.startsWith("<#FORCEOFFLINEOK#>")) {
                    close();
                } else if (msg != null && msg.startsWith("<#UPDATE_SIGNATURE#>")) {
                    String id = msg.substring(20);
                    String signature = mDis.readUTF();
                    updateSingature(id, signature);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mCurrent.setStatus(Status.STATUS_OFFLINE);
                mService.userOffLine(mCurrent);
                close();
            }

        }
    }

    public void forceLogin(User u, String id, String pass) {
        u.getsAgent().sendForceOffLine();
        mService.userOffLine(u);
        userLogin(id, pass);
    }

    public void sendForceOffLine() {
        try {
            mDos.writeUTF("<#FORCEOFFLINE#>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void userRegister(User u) {
        String userId = mService.getUserId();
        u.setId(userId);
        String sql = "insert into User(userId, userName, userPass, userSex,statusId,registerTime) values('"
                + u.getId() + "', '" + u.getName() + "', '"
                + u.getPassword() + "', " + u.getSix() + ", " + Status.STATUS_OFFLINE + ", now())";
        int result = DBUtil.executeUpdate(sql);
        try {
            if (result != 0) {
                mDos.writeUTF("<#REGISTERSUCCES#>" + userId);
            } else {
                mDos.writeUTF("<#REGISTERFAIL#>");
            }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void userLogin(String id, String pass) {
        String sql = "select * from User where userId=" + id;
        ResultSet rs = DBUtil.executeQuery(sql);
        try {
            if (rs.next()) {
                String password = rs.getString("userPass");
                if (password.equals(pass)) {
                    String name = rs.getString("userName");
                    String signature = rs.getString("signature");
                    String ip = mSocket.getInetAddress().toString().replace("/", "");
                    User u = mService.getUserById(id);
                    if (u != null) {
                        forceLogin(u, id, password);
                        return;
                    }
                    mCurrent = new User();
                    mCurrent.setIp(ip);
                    mCurrent.setId(id);
                    mCurrent.setName(name);
                    mCurrent.setPassword(pass);
                    mCurrent.setsAgent(this);
                    mCurrent.setStatus(Status.STATUS_ONLINE);
                    mCurrent.setSignature(signature);
                    mService.addUser(mCurrent);
                    mService.setMessage(Resource.getString("newPersor") + ip + "|" + id);
                    sendLoginSucces();
                    mService.sendUserList();
                } else {
                    mDos.writeUTF("<#USERPASSERROR#>");
                    close();
                }
            } else {
                mDos.writeUTF("<#USERNOTFIND#>");
                close();
            }
            DBUtil.close(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLoginSucces() {
        String sql = "update User set statusId=" + mCurrent.getStatus() + " ,lastLoginTime=now()"
                + " where userId=" + mCurrent.getId();
        DBUtil.executeUpdate(sql);
        try {
            mDos.writeUTF("<#USERLOGINSUCCES#>");
            mDos.writeUTF(mCurrent.getId() + "|" + mCurrent.getName() + "|"
                    + mCurrent.getPassword() + "|" + mCurrent.getStatus() + "|" + mCurrent.getSignature());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserOffline(User offline) {
        if (mAllFriend != null && mAllFriend.size() > 0 && mAllFriend.contains(offline)) {
            try {
                mDos.writeUTF("<#SENDUSEROFF#>");
                mDos.writeUTF(offline.getId() + "|" + offline.getName() + "|" + offline.getStatus());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendUserList() {
        String sql1 = "select count(*) from User where userId !=" + mCurrent.getId();
        String sql2 = "select * from User where userId !=" + mCurrent.getId();
        ResultSet rs1 = DBUtil.executeQuery(sql1);
        int count = 0;
        mAllFriend = new ArrayList<User>();
        try {
            while (rs1.next()) {
                count = rs1.getInt(1);
            }
            rs1.close();
            mDos.writeUTF("<#SENDUSERLIST#>" + count);
            if (count > 0) {
                ResultSet rs2 = DBUtil.executeQuery(sql2);
                while (rs2.next()) {
                    User u = new User();
                    u.setId(rs2.getString("userId"));
                    u.setName(rs2.getString("userName"));
                    u.setStatus(rs2.getInt("statusId"));
                    mAllFriend.add(u);
                    mDos.writeUTF(u.getId() + "|" + u.getName() + "|" + u.getStatus());
                }
                rs2.close();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String msg, User toUser) {
        boolean result = true;
        if (mSocket.isClosed()) {
            result = true;
            return result;
        }
        try {
            mDos.writeUTF("<#GETMESSAGE#>" + toUser.getId());
            mDos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public void sendError(String msg, String id) {
        try {
            mDos.writeUTF("<#USERERROR#>" + id + "|" + msg);
            mDos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateSingature(String id, String singature) {
        String sql = "update User set signature='" + singature + "' where userId=" + id;
        System.out.println("sql:" + sql);
        int result = DBUtil.executeUpdate(sql);
        if (result > 0) {
            try {
                mDos.writeUTF("<#UPDATE_SIGNATUREOK#>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mDos.writeUTF("<#UPDATE_SIGNATUREFAIL#>");
            } catch (IOException e) {
                e.printStackTrace();
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

}
