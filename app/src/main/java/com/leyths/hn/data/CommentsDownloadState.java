package com.leyths.hn.data;

import com.leyths.hn.app.Logger;
import com.leyths.hn.models.Item;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Arrays;

import rx.Subscriber;

class CommentsDownloadState {
    private static final String TAG = CommentsDownloadState.class.getSimpleName();

    private boolean failed = false;
    private Subscriber<? super Item> subscriber;

    private Item topLevelItem;

    public CommentsDownloadState(Subscriber<? super Item> subscriber, Item item) {
        this.subscriber = subscriber;
        this.topLevelItem = item;
    }

    public void get() {
       getChildrenRecursive(topLevelItem);
    }

    private void getChildrenRecursive(Item item) {
        Item[] children = new Item[item.getKids().size()];
        for (int i = 0; i < children.length; i++) {
            int position = i;
            Request.Builder b = new Request.Builder();
            String url = Urls.item(item.getKids().get(i));
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
                                    Item childItem = Downloader.gson.fromJson(response.body().charStream(), Item.class);
                                    if (childItem == null) {
                                        throw new IOException("Item was null");
                                    }

                                    children[position] = childItem;
                                    if (childItem.hasKids()) {
                                        Logger.d(TAG, String.format("get children recursive for %s", childItem.getId()));
                                        getChildrenRecursive(childItem);
                                    }
                                    if (checkForSuccess(children)) {
                                        Logger.d(TAG, String.format("Check for children success true %s", item.getId()));
                                        item.setChildren(Arrays.asList(children));
                                        if (checkForSuccess(topLevelItem)) {
                                            subscriber.onNext(topLevelItem);
                                            subscriber.onCompleted();
                                        } else {
                                            Logger.d(TAG, "Check for top level item success false");
                                        }
                                    } else {
                                        Logger.d(TAG, String.format("Check for children success false %s", item.getId()));
                                    }
                                } catch (Exception e) {
                                    failed(e);
                                }
                            }
                        }
                    });
        }
    }

    private boolean checkForSuccess(Item[] items) {
        for (Item i : items) {
            if (i == null) {
                Logger.d(TAG, "Items success false (null)");
                return false;
            }
        }
        return true;
    }

    private boolean checkForSuccess(Item item) {
        if (!item.hasKids()) {
            Logger.d(TAG, String.format("Item %s success true (nokids)", item.getId()));
            return true;
        }
        if (!item.hasChildren()) {
            Logger.d(TAG, String.format("Item %s success false (children null)", item.getId()));
            return false;
        }
        for (Item child : item.getChildren()) {
            if (!checkForSuccess(child)) {
                Logger.d(TAG, String.format("Item %s success false (child %s fail)", item.getId(), child.getId()));
                return false;
            }
        }
        return true;
    }

    private void failed(Throwable e) {
        failed = true;
        subscriber.onError(e);
    }
}
