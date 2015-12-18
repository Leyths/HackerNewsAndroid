package com.leyths.hn.models;

import java.util.Date;
import java.util.List;

public class Item {
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
}
