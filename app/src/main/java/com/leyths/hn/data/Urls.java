package com.leyths.hn.data;

public class Urls {

    private static final String API_BASE = "https://hacker-news.firebaseio.com/v0/";
    private static final String TOP_STORIES = API_BASE + "topstories.json";

    public static String topStories() {
        return TOP_STORIES;
    }
}
