
package com.huanghua.pojo;

import com.huanghua.socket.SocketAgent;

public class User {

    private String ip;
    private String name;
    private int port;
    private SocketAgent sAgent;

    public User() {

    }

    public User(String ip, String name, int port, SocketAgent agent) {
        this.ip = ip;
        this.name = name;
        this.port = port;
        this.sAgent = agent;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SocketAgent getsAgent() {
        return sAgent;
    }

    public void setsAgent(SocketAgent sAgent) {
        this.sAgent = sAgent;
    }
}
