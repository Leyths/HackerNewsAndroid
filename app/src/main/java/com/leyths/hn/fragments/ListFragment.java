package com.leyths.hn.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.leyths.hn.R;
import com.leyths.hn.app.Logger;
import com.leyths.hn.data.Downloader;
import com.leyths.hn.models.Item;
import com.leyths.hn.views.ItemLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = ListFragment.class.getSimpleName();
    public static final String FRAGMENT_TAG = "listFragment";
    private static final String STATE_ITEMS = "items";

    @InjectView(R.id.recyclerview) protected RecyclerView recyclerView;
    @InjectView(R.id.progress) protected ProgressBar progressBar;
    @InjectView(R.id.swipe_refresh) protected SwipeRefreshLayout swipeRefreshLayout;

    private ListAdapter listAdapter = new ListAdapter();
    private ArrayList<Item> items = new ArrayList<>();

    private Subscription subscription;

    public static ListFragment newInstance() {
        ListFragment listFragment = new ListFragment();
        Bundle args = new Bundle();
        listFragment.setArguments(args);
        return listFragment;
    }

    public ListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(listAdapter);

        if(!items.isEmpty()) {
            showContent();
        } else if(savedInstanceState != null && savedInstanceState.containsKey(STATE_ITEMS)) {
            items = (ArrayList<Item>) savedInstanceState.getSerializable(STATE_ITEMS);
            showContent();
        } else {
            fetchData();
        }
    }

    private void fetchData() {
        Downloader.topStories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    items = new ArrayList<>((List<Item>) o);
                    showContent();
                }, throwable -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Logger.e(TAG, (Throwable)throwable);
                });
    }

    private void showContent() {
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        fetchData();
    }

    private void unsubscribe() {
        if(subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ListViewHolder(new ItemLayout(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            Item item = items.get(position);

            ItemLayout itemLayout = (ItemLayout) holder.itemView;
            itemLayout.setItem(item);

            int backgroundResource = position % 2 == 0 ? R.color.listColorOne : R.color.listColorTwo;

            itemLayout.setBackgroundColor(getResources().getColor(backgroundResource));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class ListViewHolder extends RecyclerView.ViewHolder {

        public ListViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!items.isEmpty()) {
            outState.putSerializable(STATE_ITEMS, items);
        }
    }
}
