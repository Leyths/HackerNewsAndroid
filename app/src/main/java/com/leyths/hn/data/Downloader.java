package com.leyths.hn.data;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.leyths.hn.app.Logger;
import com.leyths.hn.models.Item;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class Downloader {
    private static final String TAG = Downloader.class.getSimpleName();

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static Gson gson = new GsonBuilder().create();

    public static Observable topStories() {
        return Observable.create(new Observable.OnSubscribe<List<Item>>() {
            @Override
            public void call(Subscriber<? super List<Item>> subscriber) {
                try {
                    InputStreamReader isr = new InputStreamReader(fetchSync(Urls.topStories()));

                    List<Integer> ids = inputStreamToIdList(isr);

                    DownloadState downloadState = new DownloadState(subscriber, ids);
                    downloadState.get();
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

    private static class DownloadState {
        private boolean failed = false;
        private List<Integer> ids;
        private Subscriber<? super List<Item>> subscriber;

        private Item[] items;

        public DownloadState(Subscriber<? super List<Item>> subscriber, List<Integer> ids) {
            this.subscriber = subscriber;
            this.ids = ids;

            items = new Item[25];
        }

        public void get() {
            for(int i=0; i < 25; i++) {
                int position = i;
                Request.Builder b = new Request.Builder();
                String url = Urls.item(ids.get(i));
                Logger.d(TAG, String.format("Requesting: %s", url));
                b.url(url);

                okHttpClient
                    .newCall(b.build())
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            failed(e);
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if(!failed) {
                                try {
                                    Item item = gson.fromJson(response.body().charStream(), Item.class);
                                    if(item == null) {
                                        throw new IOException("Item was null");
                                    }

                                    items[position] = item;
                                    checkForSuccess();
                                } catch (Exception e) {
                                    failed(e);
                                }
                            }
                        }
                    });
            }
        }

        private void checkForSuccess() {
            for(Item i : items) {
                if(i == null) {
                    return;
                }
            }
            subscriber.onNext(Arrays.asList(items));
            subscriber.onCompleted();
        }

        private void failed(Throwable e) {
            failed = true;
            subscriber.onError(e);
        }
    }
}
