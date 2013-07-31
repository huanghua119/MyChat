
package com.huanghua.pojo;

import com.huanghua.socket.SocketAgent;

public class User {

    private String ip;
    private String id;
    private String name;
    private String password;
    private int status;
    private int six;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private SocketAgent sAgent;

    public User() {

    }

    public User(String name, String pass) {
        this.name = name;
        this.password = pass;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSix() {
        return six;
    }

    public void setSix(int six) {
        this.six = six;
    }

    @Override
    public boolean equals(Object o) {
        User u = (User) o;
        if (u.getId().equals(this.getId())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(this.getId());
    }

}
