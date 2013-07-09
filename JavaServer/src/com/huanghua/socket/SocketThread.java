
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
    private List<SocketAgent> mAgent = null;
    private int port = 12345;

    public SocketThread(MainFrame frame) {
        mAgent = new ArrayList<SocketAgent>();
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
                u.setName(i + "");
                u.setPort(port + i);
                mFrame.setMessage(Resource.getStringForSet("newPersor") + ip + ":" + u.getName());
                mFrame.addUser(u);
                SocketAgent agent = new SocketAgent(s, mFrame, u);
                u.setsAgent(agent);
                agent.start();
                mAgent.add(agent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendUserList() {
        List<User> mList = mFrame.getUserList();
        for (User u : mList) {
            u.getsAgent().sendUserList();
        }
/*        for (SocketAgent sa : mAgent) {
            sa.sendUserList();
        }*/
    }

    public void cancel() {
        try {
            if (mAgent != null && mAgent.size() > 0) {
                for (SocketAgent sa : mAgent) {
                    sa.close();
                }
                mAgent = null;
            }
            if (mScocket != null) {
                mScocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
