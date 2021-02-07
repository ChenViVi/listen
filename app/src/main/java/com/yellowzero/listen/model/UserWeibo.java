package com.yellowzero.listen.model;

import java.io.Serializable;

public class UserWeibo implements Serializable {
    private int id;
    private String name;
    private String avatar;

    public UserWeibo(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

