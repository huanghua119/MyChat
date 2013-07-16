
package com.huanghua.pojo;

import com.huanghua.socket.SocketAgent;

public class User {

    private String ip;
    private String id;
    private String name;
    private String password;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private SocketAgent sAgent;

    public User() {

    }

    public User(String ip, String id, String name, SocketAgent agent) {
        this.ip = ip;
        this.id = id;
        this.name = name;
        this.sAgent = agent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SocketAgent getsAgent() {
        return sAgent;
    }

    public void setsAgent(SocketAgent sAgent) {
        this.sAgent = sAgent;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
