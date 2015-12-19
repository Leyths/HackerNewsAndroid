package com.leyths.hn.data;

import com.leyths.hn.app.Logger;
import com.leyths.hn.models.Item;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import rx.Subscriber;

class ListDownloadState {
    private static final String TAG = ListDownloadState.class.getSimpleName();

    private boolean failed = false;
    private List<Integer> ids;
    private Subscriber<? super List<Item>> subscriber;

    private Item[] items;

    public ListDownloadState(Subscriber<? super List<Item>> subscriber, List<Integer> ids) {
        this.subscriber = subscriber;
        this.ids = ids;

        items = new Item[Math.min(ids.size(), 25)];
    }

    public void get() {
        for (int i = 0; i < items.length; i++) {
            int position = i;
            Request.Builder b = new Request.Builder();
            String url = Urls.item(ids.get(i));
            Logger.d(TAG, String.format("Requesting: %s", url));
            b.url(url);

            Downloader.okHttpClient
                    .newCall(b.build())
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            failed(e);
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (!failed && !subscriber.isUnsubscribed()) {
                                try {
                                    Item item = Downloader.gson.fromJson(response.body().charStream(), Item.class);
                                    if (item == null) {
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
        for (Item i : items) {
            if (i == null) {
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
