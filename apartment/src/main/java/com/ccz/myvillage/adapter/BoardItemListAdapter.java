package com.ccz.myvillage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.R;
import com.ccz.myvillage.activity.ViewerActivity;
import com.ccz.myvillage.common.ImageUtils;
import com.ccz.myvillage.common.Preferences;
import com.ccz.myvillage.common.TimeUtils;
import com.ccz.myvillage.dto.BoardArticleTitle;
import com.ccz.myvillage.dto.BoardItem;

import java.io.InputStream;
import java.util.List;

public class BoardItemListAdapter extends ArrayAdapter<BoardItem> {
    private final boolean isListType;
    public BoardItemListAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<BoardItem> boardItemList) {
        super(context, resource, textViewResourceId, boardItemList);

        isListType = Preferences.getBool(context, Preferences.VIEWTYPE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(
                    isListType ? R.layout.item_article_title : R.layout.item_article_shortcontent,
                    parent, false);
        }

        BoardItem item = getItem(position);
        if (item!= null) {
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
            TextView tvWatch = (TextView) convertView.findViewById(R.id.tvWatch);
            TextView tvLike = (TextView) convertView.findViewById(R.id.tvLike);
            TextView tvDislike = (TextView) convertView.findViewById(R.id.tvDislike);
            TextView tvReply = (TextView) convertView.findViewById(R.id.tvReply);
            TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);

            tvTitle.setText(item.getTitle());
            tvId.setText(item.getCreateusername());
            tvWatch.setText(item.getVisit()+"");
            tvLike.setText(item.getLikes()+"");
            tvDislike.setText(item.getDislikes()+"");
            tvReply.setText(item.getReply()+"");
            tvTime.setText(TimeUtils.calcLastTime(item.getCreatetime()));

            if(isListType==false) {
                ((TextView) convertView.findViewById(R.id.tvContent)).setText(item.getContent());
                ImageView ivCrop = (ImageView)convertView.findViewById(R.id.ivCrop);

                if(item.getCropurl()!=null) {
                    ivCrop.setVisibility(View.VISIBLE);
                    new DownloadImageTask(ivCrop).execute(item.getCropurl());
                }else
                    ivCrop.setVisibility(View.GONE);
            }

            convertView.setTag(item);
        }

        return convertView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView ivThumbnail;

        public DownloadImageTask(ImageView ivThumbnail) {
            this.ivThumbnail = ivThumbnail;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                return ImageUtils.scaleUpContent(bmp, IConst.ScreenPixels.getWidth(), IConst.ScreenPixels.getHeight());
                //return bmp;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            ivThumbnail.setImageBitmap(result);
        }
    }
}
