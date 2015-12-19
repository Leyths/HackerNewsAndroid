package com.leyths.hn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leyths.hn.R;
import com.leyths.hn.app.EventBus;
import com.leyths.hn.app.MainActivity;
import com.leyths.hn.models.Item;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ItemLayout extends LinearLayout {

    @InjectView(R.id.title) protected TextView title;
    @InjectView(R.id.domain) protected TextView domain;
    @InjectView(R.id.points) protected TextView points;
    @InjectView(R.id.comments) protected TextView comments;

    private Item item;

    public ItemLayout(Context context) {
        super(context);
        init();
    }

    public ItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_list_item, this, true);
        ButterKnife.inject(this);
    }

    public void setItem(Item item) {
        this.item = item;
        title.setText(item.getTitle());


        String domainStr = item.getDomain();
        if(domainStr != null) {
            domainStr = domainStr.replaceAll("www.", "");
            domainStr = getResources().getString(R.string.domain, domainStr);
        }
        domain.setText(domainStr);
        comments.setText(String.valueOf(item.getDescendants()));
        points.setText(item.getScore());

        setOnClickListener(v -> EventBus.post(new MainActivity.GoToContentEvent(item.getUrl())));
    }

    @OnClick(R.id.comments)
    @SuppressWarnings("unused")
    void commentsClicked() {
        EventBus.post(new MainActivity.GoToCommentsEvent(item));
    }
}
