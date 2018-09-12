package com.ccz.myvillage.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccz.myvillage.R;
import com.ccz.myvillage.common.Preferences;
import com.ccz.myvillage.common.TimeUtils;
import com.ccz.myvillage.dto.BoardItem;
import com.ccz.myvillage.dto.WordHistory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WordHistoryAdapter extends ArrayAdapter<WordHistory> {
    private final boolean isListType;
    public WordHistoryAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<WordHistory> boardItemList) {
        super(context, resource, textViewResourceId, boardItemList);

        isListType = Preferences.getBool(context, Preferences.VIEWTYPE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(
                    isListType ? R.layout.item_article_title : R.layout.item_article_shortcontent,
                    parent, false);
        }

        WordHistory item = getItem(position);
        if (item!= null) {
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvWord);
            tvTitle.setText(item.searh);
        }

        return convertView;
    }


}
