
package com.huanghua.service;

import com.huanghua.dao.DBUtil;
import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.socket.SocketThread;
import com.huanghua.view.MainFrame;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerService {

    private MainFrame mFrame;
    private static ServerService service = null;
    public static boolean sIsListenter = false;
    private SocketThread mSocket;
    private List<User> mUser = null;
    private List<String> mNumberPool;

    private ServerService() {
        mUser = new ArrayList<User>();
    }

    public static ServerService getInstance() {
        if (service == null) {
            synchronized (ServerService.class) {
                if (service == null) {
                    service = new ServerService();
                }
            }
        }
        return service;
    }

    public void setMainFrame(MainFrame frame) {
        this.mFrame = frame;
    }

    public void startListener() {
        sIsListenter = true;
        mSocket = new SocketThread(service);
        Thread thread = new Thread(mSocket);
        thread.start();
        new Thread(mSendUserListRunnable).start();
    }

    public void addUser(User u) {
        mUser.add(u);
    }

    public List<User> getUserList() {
        return this.mUser;
    }

    public void sendUserList() {
        for (User u : mUser) {
            u.getsAgent().sendUserList();
        }
    }

    public void sendUserOffline(User offUser) {
        for (User u : mUser) {
            u.getsAgent().sendUserOffline(offUser);
        }
    }

    public void userOffLine(User u) {
        mUser.remove(u);
        setMessage(Resource.getString("offline") + u.getIp() + ":" + u.getId());
        String sql = "update User set statusId=" + u.getStatus() + " where userId=" + u.getId();
        DBUtil.executeUpdate(sql);
        sendUserOffline(u);
    }

    public User getUserById(String id) {
        for (User u : mUser) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public void sendContextByIdToUser(String context, String id, User toUser) {
        User u = getUserById(id);
        if (u == null) {
            toUser.getsAgent().sendError(Resource.getString("usernotonline"), id);
        } else {
            boolean isSuccess = u.getsAgent().sendMessage(context, toUser);
            if (!isSuccess) {
                toUser.getsAgent().sendError(Resource.getString("sendfail"), id);
            }
        }
    }

    public int[] getPoolRangeForSql() {
        String sql = "select * from NumberPool";
        ResultSet rs = DBUtil.executeQuery(sql);
        int[] result = {
                10000, 100000
        };
        try {
            if (rs.next()) {
                int start = rs.getInt("numberstart");
                int end = rs.getInt("numberend");
                result[0] = start;
                result[1] = end;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
        }
        return result;
    }

    public synchronized void generatePool(int start, int end) {
        if (mNumberPool == null) {
            mNumberPool = new ArrayList<String>();
        }
        for (int i = start; i < end; i++) {
            mNumberPool.add(i + "");
        }
    }

    public boolean numberIsUsed(String number) {
        String sql = "select * from User where userId=" + number;
        ResultSet rs = DBUtil.executeQuery(sql);
        boolean result = false;
        try {
            if (rs.next()) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
        }
        return result;
    }

    public synchronized String getUserId() {
        Random random = new Random();
        int index = random.nextInt(mNumberPool.size());
        String id = mNumberPool.get(index);
        mNumberPool.remove(index);
        addNumberPool();
        return numberIsUsed(id) ? getUserId() : id;
    }

    public void addNumberPool() {
        if (mNumberPool.size() > 100) {
            return;
        }
        int range[] = getPoolRangeForSql();
        int start = range[0] * 10;
        int end = range[1] * 10;
        String sql = "update  NumberPool set numberstart=" + start + ", numberend=" + end;
        DBUtil.executeUpdate(sql);
        for (int i = start; i < end; i++) {
            mNumberPool.add(i + "");
        }
    }

    public void setMessage(String msg) {
        mFrame.setMessage(msg);
    }

    public void cancel() {
        sIsListenter = false;
        if (mUser != null && mUser.size() > 0) {
            for (User u : mUser) {
                u.getsAgent().close();
            }
        }
        mSocket.close();
    }

    private Runnable mSendUserListRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                sendUserList();
                try {
                    Thread.sleep(1000 * 60 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
