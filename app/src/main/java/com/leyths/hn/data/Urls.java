package com.leyths.hn.data;

public class Urls {

    private static final String API_BASE = "https://hacker-news.firebaseio.com/v0/";
    private static final String TOP_STORIES = API_BASE + "topstories.json";
    private static final String ITEMS = API_BASE + "item/%d.json";

    public static String topStories() {
        return TOP_STORIES;
    }

    public static String item(int id) {
        return String.format(ITEMS, id);
    }
}
