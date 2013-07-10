
package com.huanghua.socket;

import com.huanghua.i18n.Resource;
import com.huanghua.pojo.User;
import com.huanghua.view.MainFrame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketThread implements Runnable {

    private ServerSocket mScocket;
    private MainFrame mFrame;
    private int port = 12345;

    public SocketThread(MainFrame frame) {
        mFrame = frame;
    }

    @Override
    public void run() {
        try {
            mScocket = new ServerSocket(port);
            mFrame.setMessage(Resource.getStringForSet("nowListener") + mScocket.getInetAddress());
            int i = 0;
            while (MainFrame.sIsListenter) {
                Socket s = mScocket.accept();
                i++;
                String ip = s.getInetAddress().toString().replace("/", "");
                User u = new User();
                u.setIp(ip);
                u.setId(i + "");
                mFrame.setMessage(Resource.getStringForSet("newPersor") + ip + ":" + u.getId());
                mFrame.addUser(u);
                SocketAgent agent = new SocketAgent(s, mFrame, u);
                u.setsAgent(agent);
                agent.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUserOffline(User offUser) {
        List<User> mList = mFrame.getUserList();
        for (User u : mList) {
            u.getsAgent().sendUserOffline(offUser);
        }
    }
    public void sendUserList() {
        List<User> mList = mFrame.getUserList();
        for (User u : mList) {
            u.getsAgent().sendUserList();
        }
    }

    public void cancel() {
        try {
            List<User> mList = mFrame.getUserList();
            if (mList != null && mList.size() > 0) {
                for (User u : mList) {
                    u.getsAgent().close();
                }
            }
            if (mScocket != null) {
                mScocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
