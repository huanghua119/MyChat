
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

    public SocketThread(MainFrame frame) {
        mAgent = new ArrayList<SocketAgent>();
        mFrame = frame;
    }

    @Override
    public void run() {
        try {
            mScocket = new ServerSocket(12345);
            mFrame.setMessage(Resource.getStringForSet("nowListener") + mScocket.getInetAddress());
            int i = 1;
            while (MainFrame.sIsListenter) {
                Socket s = mScocket.accept();
                mFrame.setMessage(Resource.getStringForSet("newPersor") + s.getInetAddress());
                User u = new User();
                u.setIp(s.getInetAddress().toString());
                u.setName(i + "");
                i++;
                mFrame.addUser(u);
                SocketAgent agent = new SocketAgent(s, mFrame);
                agent.start();
                mAgent.add(agent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
