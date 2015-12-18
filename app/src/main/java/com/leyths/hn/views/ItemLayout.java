package com.leyths.hn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leyths.hn.R;
import com.leyths.hn.models.Item;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ItemLayout extends LinearLayout {

    @InjectView(R.id.title) protected TextView title;
    @InjectView(R.id.submitter) protected TextView submitter;

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
        title.setText(item.getTitle());
        submitter.setText(item.getBy());
    }
}
