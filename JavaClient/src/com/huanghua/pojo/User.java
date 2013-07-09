
package com.huanghua.pojo;

public class User {

    private String ip;
    private String name;
    private int port;

    public User() {

    }

    public User(String ip, String name, int port) {
        this.ip = ip;
        this.name = name;
        this.port = port;
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
}
