package com.leyths.hn.views;

import android.content.Context;
import android.text.Html;
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
import butterknife.OnClick;

public class CommentLayout extends LinearLayout {

    @InjectView(R.id.text) protected TextView text;
    @InjectView(R.id.submitter) protected TextView submitter;
    @InjectView(R.id.date) protected TextView date;

    private Item item;

    public CommentLayout(Context context) {
        super(context);
        init();
    }

    public CommentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_list_comment, this, true);
        ButterKnife.inject(this);
    }

    public void setItem(Item item) {
        this.item = item;

        setPadding(item.getDepth() * getResources().getDimensionPixelSize(R.dimen.padding_left), 0, 0, 0);

        if (item.isDeleted()) {
            date.setText(null);
            submitter.setText(null);
            text.setText(getResources().getString(R.string.deleted));
            return;
        }
        date.setText(DateHelper.dateToRelativeTime(item.getDate()));
        submitter.setText(item.getBy());
        text.setText(Html.fromHtml(item.getText()));
    }
}
