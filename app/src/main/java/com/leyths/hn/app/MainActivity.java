package com.leyths.hn.app;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.leyths.hn.R;
import com.leyths.hn.fragments.ListFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.content) protected ViewGroup content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        if(savedInstanceState == null) {
            setupInitialFragment();
        }
    }

    private void setupInitialFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, ListFragment.newInstance(), ListFragment.FRAGMENT_TAG);
        ft.commit();
    }
}
