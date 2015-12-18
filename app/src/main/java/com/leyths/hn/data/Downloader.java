package com.leyths.hn.data;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.leyths.hn.models.Item;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class Downloader {
    private static final String TAG = Downloader.class.getSimpleName();

    static OkHttpClient okHttpClient = new OkHttpClient();
    static Gson gson = new GsonBuilder().create();

    public static Observable topStories() {
        return Observable.create(new Observable.OnSubscribe<List<Item>>() {
            @Override
            public void call(Subscriber<? super List<Item>> subscriber) {
                try {
                    InputStreamReader isr = new InputStreamReader(fetchSync(Urls.topStories()));

                    List<Integer> ids = inputStreamToIdList(isr);

                    ListDownloadState listDownloadState = new ListDownloadState(subscriber, ids);
                    listDownloadState.get();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<Integer> inputStreamToIdList(InputStreamReader isr) {
        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        return gson.fromJson(isr, listType);
    }

    private static InputStream fetchSync(String uri) throws IOException {
        Request.Builder b = new Request.Builder();
        b.url(uri);

        Response response = okHttpClient
                .newCall(b.build())
                .execute();

        if(!response.isSuccessful()) {
            throw new IOException(String.format("failed with code %d", response.code()));
        }

        return response.body().byteStream();
    }


    public static Observable comments(Item item) {
        return Observable.create(new Observable.OnSubscribe<List<Item>>() {
            @Override
            public void call(Subscriber<? super List<Item>> subscriber) {
                try {
                    List<Integer> ids = item.getKids();
                    ListDownloadState listDownloadState = new ListDownloadState(subscriber, ids);
                    listDownloadState.get();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
