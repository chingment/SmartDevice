package com.lumos.smartdevice.activity.sm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.vo.BookerBookVo;
import com.lumos.smartdevice.ui.ViewHolder;

import java.util.List;

public class SmBookerTakeStockBookAdapter extends BaseAdapter {

    private static final String TAG = "SmBookerTakeStockBookAdapter";
    private final Context context;
    private final List<BookerBookVo> items;


    public SmBookerTakeStockBookAdapter(Context context, List<BookerBookVo> items) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sm_booker_take_stock_book, parent, false);
        }

        final BookerBookVo item = items.get(position);


        TextView tv_Name = ViewHolder.get(convertView, R.id.tv_Name);
        TextView tv_RfId = ViewHolder.get(convertView, R.id.tv_RfId);

        tv_Name.setText(item.getName());
        tv_RfId.setText(item.getRfId());
        return convertView;
    }
}
