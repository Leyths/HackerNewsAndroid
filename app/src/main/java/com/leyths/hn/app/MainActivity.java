package com.leyths.hn.app;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.leyths.hn.R;
import com.leyths.hn.fragments.CommentsFragment;
import com.leyths.hn.fragments.ContentFragment;
import com.leyths.hn.fragments.ListFragment;
import com.leyths.hn.models.Item;
import com.squareup.otto.Subscribe;

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

        EventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.unregister(this);
    }

    private void setupInitialFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, ListFragment.newInstance(), ListFragment.FRAGMENT_TAG);
        ft.commit();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void goToContent(GoToContentEvent event) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, ContentFragment.newInstance(event.contentUrl), ContentFragment.FRAGMENT_TAG);
        ft.addToBackStack(ContentFragment.FRAGMENT_TAG);
        ft.commit();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void goToComments(GoToCommentsEvent event) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, CommentsFragment.newInstance(event.item), CommentsFragment.FRAGMENT_TAG);
        ft.addToBackStack(CommentsFragment.FRAGMENT_TAG);
        ft.commit();
    }

    public static class GoToContentEvent {
        public final String contentUrl;

        public GoToContentEvent(String contentUrl) {
            this.contentUrl = contentUrl;
        }
    }

    public static class GoToCommentsEvent {
        public final Item item;

        public GoToCommentsEvent(Item item) {
            this.item = item;
        }
    }
}
