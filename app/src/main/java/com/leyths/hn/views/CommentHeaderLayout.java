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
import com.leyths.hn.ui.DateHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentHeaderLayout extends LinearLayout {

    @InjectView(R.id.title) protected TextView title;
    @InjectView(R.id.submitter) protected TextView submitter;
    @InjectView(R.id.date) protected TextView date;

    private Item item;

    public CommentHeaderLayout(Context context) {
        super(context);
        init();
    }

    public CommentHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_list_comment_header, this, true);
        ButterKnife.inject(this);
    }

    public void setItem(Item item) {
        this.item = item;

        if (item.isDeleted()) {
            date.setText(null);
            submitter.setText(null);
            title.setText(getResources().getString(R.string.deleted));
            return;
        }
        date.setText(DateHelper.dateToRelativeTime(item.getDate()));
        submitter.setText(item.getBy());
        title.setText(item.getTitle());
        setOnClickListener(v -> EventBus.post(new MainActivity.GoToContentEvent(item.getUrl())));
    }
}
