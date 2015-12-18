package com.leyths.hn.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Item implements Serializable {
    private long id;
    private boolean deleted;
    private String type;
    private String by;
    private long time;
    private String text;
    private boolean dead;
    private Integer parent;
    private List<Integer> kids;
    private String url;
    private Integer score;
    private String title;
    private int descendants;

    public Item() {

    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBy() {
        return by;
    }

    public String getUrl() {
        return url;
    }

    public String getDescendants() {
        return String.valueOf(descendants);
    }

    public List<Integer> getKids() {
        return kids;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        Date date = new Date();
        date.setTime(time * 1000L);
        return date;
    }
}
