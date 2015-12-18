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
import com.leyths.hn.data.Downloader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ListFragment extends Fragment {

    public static final String FRAGMENT_TAG = "listFragment";

    @InjectView(R.id.recyclerview) protected RecyclerView recyclerView;

    private ListAdapter listAdapter = new ListAdapter();
    private List<Integer> topStories = new ArrayList<>();

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

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(listAdapter);
        fetchData();
    }

    private void fetchData() {
        Downloader.topStories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    topStories = (List<Integer>) o;
                    listAdapter.notifyDataSetChanged();
                }, throwable -> {

                });
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ListViewHolder(new TextView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            TextView tv = (TextView) holder.itemView;
            tv.setText(String.valueOf(topStories.get(position)));
        }

        @Override
        public int getItemCount() {
            return topStories.size();
        }
    }

    private static class ListViewHolder extends RecyclerView.ViewHolder {

        public ListViewHolder(View itemView) {
            super(itemView);
        }
    }

}
