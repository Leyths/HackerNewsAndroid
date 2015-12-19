package com.leyths.hn.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.leyths.hn.R;
import com.leyths.hn.app.Logger;
import com.leyths.hn.data.Downloader;
import com.leyths.hn.models.Item;
import com.leyths.hn.views.CommentHeaderLayout;
import com.leyths.hn.views.CommentLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentsFragment extends Fragment {
    private static final String TAG = CommentsFragment.class.getSimpleName();
    public static final String FRAGMENT_TAG = "commentsFragment";
    private static final String ARG_ITEM = "arg_item";
    private static final String STATE_ITEMS = "items";

    @InjectView(R.id.recyclerview) protected RecyclerView recyclerView;
    @InjectView(R.id.progress) protected ProgressBar progressBar;
    @InjectView(R.id.error) protected View error;

    private ListAdapter listAdapter = new ListAdapter();
    private ArrayList<Item> flattenedItems = new ArrayList<>();
    private Subscription subscription;

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

        if(!flattenedItems.isEmpty()) {
            showContent();
        } else if(savedInstanceState != null && savedInstanceState.containsKey(STATE_ITEMS)) {
            flattenedItems = (ArrayList<Item>) savedInstanceState.getSerializable(STATE_ITEMS);
            showContent();
        } else {
            fetchData();
        }
    }

    @OnClick(R.id.error)
    protected void fetchData() {
        unsubscribe();
        subscription = Downloader.comments(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    Item newItem = (Item) o;

                    ArrayList<Item> flattened = new ArrayList<>();

                    flattened.add(newItem);
                    for (Item item : newItem.getChildren()) {
                        add(flattened, item, 0);
                    }
                    flattenedItems = flattened;

                    showContent();
                }, throwable -> {
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);

                    Logger.e(TAG, (Throwable) throwable);
                });
    }

    private void unsubscribe() {
        if(subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);

        listAdapter.notifyDataSetChanged();
    }

    private void add(List<Item> flattened, Item currentItem, int depth) {
        currentItem.setDepth(depth);
        flattened.add(currentItem);
        if (currentItem.hasChildren()) {
            for (Item child : currentItem.getChildren()) {
                add(flattened, child, depth + 1);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!flattenedItems.isEmpty()) {
            outState.putSerializable(STATE_ITEMS, flattenedItems);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private static final int VIEW_TYPE_COMMENT = 0;
        private static final int VIEW_TYPE_HEADER = 1;

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_COMMENT:
                    return new ListViewHolder(new CommentLayout(parent.getContext()));
                case VIEW_TYPE_HEADER:
                    return new ListViewHolder(new CommentHeaderLayout(parent.getContext()));
            }
            return new ListViewHolder(new LinearLayout(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            Item item = flattenedItems.get(position);

            if (holder.getItemViewType() == VIEW_TYPE_COMMENT) {
                CommentLayout commentLayout = (CommentLayout) holder.itemView;
                commentLayout.setItem(item);

                int backgroundResource = position % 2 == 0 ? R.color.listColorOne : R.color.listColorTwo;
                commentLayout.setBackgroundColor(getResources().getColor(backgroundResource));
            } else if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
                CommentHeaderLayout commentLayout = (CommentHeaderLayout) holder.itemView;
                commentLayout.setItem(item);
            }
        }

        @Override
        public int getItemViewType(int position) {
            Item item = flattenedItems.get(position);

            if (Item.TYPE_STORY.equals(item.getType())) {
                return VIEW_TYPE_HEADER;
            } else {
                return VIEW_TYPE_COMMENT;
            }
        }

        @Override
        public int getItemCount() {
            return flattenedItems.size();
        }
    }

    private static class ListViewHolder extends RecyclerView.ViewHolder {

        public ListViewHolder(View itemView) {
            super(itemView);
        }
    }

}
