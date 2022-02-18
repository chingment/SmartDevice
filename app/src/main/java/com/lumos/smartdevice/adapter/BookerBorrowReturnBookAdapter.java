package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.BookBean;
import com.lumos.smartdevice.model.LockerBoxUsageBean;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ui.ViewHolder;
import com.lumos.smartdevice.utils.CommonUtil;

import java.util.List;

public class BookerBorrowReturnBookAdapter extends BaseAdapter {

    private static final String TAG = "BookerBorrowReturnBookAdapter";
    private final Context context;
    private final List<BookBean> items;


    public BookerBorrowReturnBookAdapter(Context context, List<BookBean> items) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_booker_borrow_return_book, parent, false);
        }

        final BookBean item = items.get(position);


        TextView tv_Name = ViewHolder.get(convertView, R.id.tv_Name);


        tv_Name.setText(item.getName());

        return convertView;
    }
}
