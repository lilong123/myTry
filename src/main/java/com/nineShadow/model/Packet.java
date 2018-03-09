package com.nineShadow.model;

import java.util.Date;

public class Packet {
    private  Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    private Date time;

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", time=" + time +
                '}';
    }
}
