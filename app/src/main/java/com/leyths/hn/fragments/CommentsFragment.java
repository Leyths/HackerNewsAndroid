package com.leyths.hn.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leyths.hn.R;
import com.leyths.hn.app.Logger;
import com.leyths.hn.data.Downloader;
import com.leyths.hn.models.Item;
import com.leyths.hn.views.CommentLayout;
import com.leyths.hn.views.ItemLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentsFragment extends Fragment {
    private static final String TAG = CommentsFragment.class.getSimpleName();
    public static final String FRAGMENT_TAG = "commentsFragment";
    private static final String ARG_ITEM = "arg_item";

    @InjectView(R.id.recyclerview) protected RecyclerView recyclerView;

    private ListAdapter listAdapter = new ListAdapter();
    private List<Item> items = new ArrayList<>();

    private Item item;

    public static CommentsFragment newInstance(Item item) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = (Item) getArguments().getSerializable(ARG_ITEM);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(listAdapter);
        fetchData();
    }

    private void fetchData() {
        Downloader.comments(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    items = (List<Item>) o;
                    listAdapter.notifyDataSetChanged();
                }, throwable -> {
                    Logger.e(TAG, (Throwable)throwable);
                });
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ListViewHolder(new CommentLayout(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            Item item = items.get(position);

            CommentLayout commentLayout = (CommentLayout) holder.itemView;
            commentLayout.setItem(item);

            int backgroundResource = position % 2 == 0 ? R.color.listColorOne : R.color.listColorTwo;
            commentLayout.setBackgroundColor(getResources().getColor(backgroundResource));
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

}
