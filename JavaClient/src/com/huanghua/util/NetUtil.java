package com.huanghua.util;

import java.net.Socket;


public class NetUtil {

    public static int getValidPort(int port) {
        try {
            Socket socket = new Socket("127.0.0.1", port);
            socket.close();
            return getValidPort(port + 1);
        } catch (Exception e) {
            return port;
        }
    }
}
