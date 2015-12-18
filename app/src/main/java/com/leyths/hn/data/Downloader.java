package com.leyths.hn.data;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.leyths.hn.app.Logger;
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

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static Gson gson = new GsonBuilder().create();

    public static Observable topStories() {
        return Observable.create(new Observable.OnSubscribe<List<Integer>>() {
            @Override
            public void call(Subscriber<? super List<Integer>> subscriber) {
                try {
                    InputStreamReader isr = new InputStreamReader(fetch(Urls.topStories()));

                    subscriber.onNext(inputStreamToList(isr));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Logger.e(TAG, e);
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<Integer> inputStreamToList(InputStreamReader isr) {
        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        return gson.fromJson(isr, listType);
    }

    private static InputStream fetch(String uri) throws IOException {
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
}
