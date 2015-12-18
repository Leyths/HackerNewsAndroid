package com.leyths.hn.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leyths.hn.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ListFragment extends Fragment {

    public static final String FRAGMENT_TAG = "listFragment";

    @InjectView(R.id.recyclerview) protected RecyclerView recyclerView;

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
        ButterKnife.inject(this, view);
        return view;
    }
}
