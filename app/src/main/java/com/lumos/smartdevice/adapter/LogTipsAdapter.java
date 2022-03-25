package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.vo.LogTipsBean;
import com.lumos.smartdevice.ui.ViewHolder;

import java.util.List;

public class LogTipsAdapter extends BaseAdapter {

    private static final String TAG = "LogTipsAdapter";
    private final Context context;
    private final List<LogTipsBean> items;


    public LogTipsAdapter(Context context, List<LogTipsBean> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_logtips, parent, false);
        }
        final LogTipsBean item = items.get(position);
        TextView tv_Content = ViewHolder.get(convertView, R.id.tv_Content);
        TextView tv_DateTime = ViewHolder.get(convertView, R.id.tv_DateTime);

        tv_Content.setText(item.getContent());
        tv_DateTime.setText(item.getDateTime());
        return convertView;
    }



}
