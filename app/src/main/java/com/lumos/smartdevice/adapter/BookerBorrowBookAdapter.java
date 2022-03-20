package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.BookerBorrowBookBean;
import com.lumos.smartdevice.ui.refreshview.MyViewHolder;
import com.lumos.smartdevice.ui.refreshview.RefreshAdapter;
import com.lumos.smartdevice.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chingment on 2018/6/13.
 */

public class BookerBorrowBookAdapter extends RefreshAdapter {
    private static final String TAG = "BookerBorrowedBookAdapter";
    private Context context;
    private List<BookerBorrowBookBean> items = new ArrayList<>();

    public BookerBorrowBookAdapter() {

    }

    public void setData(List<BookerBorrowBookBean> items, Context context) {
        this.items = items;
        this.context = context;
        notifyDataSetChanged();

    }

    public void addData(List<BookerBorrowBookBean> items, Context context) {
        this.items.addAll(items);
        this.context = context;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booker_borrow_book, parent, false)) {
        };
    }

    @Override
    public void onBindItemHolder(final RecyclerView.ViewHolder holder, int position) {

        TextView tv_SkuName = holder.itemView.findViewById(R.id.tv_SkuName);
        ImageView iv_SkuImg = holder.itemView.findViewById(R.id.iv_SkuImg);
        TextView tv_ExpireTime = holder.itemView.findViewById(R.id.tv_ExpireTime);
        TextView tv_StatusText = holder.itemView.findViewById(R.id.tv_StatusText);

        BookerBorrowBookBean item = items.get(position);


        tv_SkuName.setText(String.format("《%s》", item.getSkuName()));
        tv_ExpireTime.setText(item.getExpireTime());
        tv_StatusText.setText(item.getStatus().getText());
        CommonUtil.loadImageFromUrl(context, iv_SkuImg, item.getSkuImgUrl());


        int statusValue= item.getStatus().getValue();
        int statusTextColor=R.color.booker_borrow_status_1000;

        if(statusValue==1000){
            statusTextColor=R.color.booker_borrow_status_1000;
        }
        else if(statusValue==2000){
            statusTextColor=R.color.booker_borrow_status_2000;
        }
        else if(statusValue==3000){
            statusTextColor=R.color.booker_borrow_status_3000;
        }
        else  if(statusValue==4000){
            statusTextColor=R.color.booker_borrow_status_4000;
        }

        tv_StatusText.setTextColor(context.getResources().getColor(statusTextColor));

    }


    @Override
    public int getCount() {
        return items.size();
    }
}
