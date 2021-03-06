package com.lumos.smartdevice.activity.booker.adapter;

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

public class BookerBorrowReturnBookAdapter extends BaseAdapter {

    private static final String TAG = "BookerBorrowReturnBookAdapter";
    private final Context context;
    private final List<BookerBookVo> items;


    public BookerBorrowReturnBookAdapter(Context context, List<BookerBookVo> items) {
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

        final BookerBookVo item = items.get(position);


        TextView tv_Name = ViewHolder.get(convertView, R.id.tv_Name);


        tv_Name.setText(item.getName());

        return convertView;
    }
}
