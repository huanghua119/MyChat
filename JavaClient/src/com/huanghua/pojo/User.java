
package com.huanghua.pojo;

public class User {

    private String id;
    private String name;
    private String password;
    private int status;

    public User() {
    }

    public User(String name, String pass) {
        this.name = name;
        this.password = pass;
    }

    public User(String id, String name, int status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public User(String id, String name, String pass, int status) {
        this.id = id;
        this.name = name;
        this.password = pass;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
