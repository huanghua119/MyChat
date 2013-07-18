
package com.huanghua.socket;

import com.huanghua.i18n.Resource;
import com.huanghua.service.ServerService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketThread implements Runnable {

    private ServerSocket mScocket;
    private int port = 12345;
    private ServerService mService;

    public SocketThread(ServerService service) {
        mService = service;
    }

    @Override
    public void run() {
        try {
            mScocket = new ServerSocket(port);
            mService.setMessage(Resource.getString("nowListener") + mScocket.getInetAddress());
            int[] poolRange = mService.getPoolRangeForSql();
            mService.generatePool(poolRange[0], poolRange[1]);
            while (ServerService.sIsListenter) {
                Socket s = mScocket.accept();
                SocketAgent agent = new SocketAgent(s, mService);
                agent.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (mScocket != null) {
                mScocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
