package com.pengdi.keyguardapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pengdi on 16-12-2.
 * 通知栏的listview适配器
 */
public class NotificationAdapter extends BaseAdapter {
    private Context mContext;
    private List<Notification> mList;
    private LayoutInflater mLayoutInflater;

    public NotificationAdapter(Context mContext, List<Notification> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationItem viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_notification, parent);
            viewHolder = new NotificationItem();
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.notification_title);
            viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.notification_content);
            viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.notification_time);
            viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.notification_icon);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (NotificationItem) convertView.getTag();
        }
        viewHolder.titleTextView.setText(mList.get(position).title);
        viewHolder.contentTextView.setText(mList.get(position).content);
        viewHolder.timeTextView.setText(mList.get(position).time);
        viewHolder.iconImageView.setImageBitmap(mList.get(position).icon);

        return convertView;
    }

    public static class Notification {
        String title; // 标题
        String content; // 内容
        String time; // 时间
        Bitmap icon; // 大图标
    }

    public class NotificationItem{
        TextView titleTextView; // 标题
        TextView contentTextView; // 内容
        TextView timeTextView; // 时间
        ImageView iconImageView; // 大图标
    }
}
