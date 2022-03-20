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
        TextView tv_BorrowTime = holder.itemView.findViewById(R.id.tv_BorrowTime);
        TextView tv_ExpireTime = holder.itemView.findViewById(R.id.tv_ExpireTime);
        TextView tv_StatusText = holder.itemView.findViewById(R.id.tv_StatusText);

        BookerBorrowBookBean item = items.get(position);


        tv_SkuName.setText(String.format("《%s》", item.getSkuName()));
        tv_BorrowTime.setText(item.getBorrowTime());
        tv_ExpireTime.setText(item.getExpireTime());
        tv_StatusText.setText(item.getStatus().getText());

        CommonUtil.loadImageFromUrl(context, iv_SkuImg, item.getSkuImgUrl());

//      View ll_Info = holder.itemView.findViewById(R.id.ll_Info);
//        View ll_DividerLine = holder.itemView.findViewById(R.id.ll_DividerLine);
//
//
//        TextView tv_BoxName = holder.itemView.findViewById(R.id.tv_BoxName);
//        TextView tv_UseTime = holder.itemView.findViewById(R.id.tv_UseTime);
//        TextView tv_UseRemark = holder.itemView.findViewById(R.id.tv_UseRemark);
//        TextView tv_UseResult = holder.itemView.findViewById(R.id.tv_UseResult);
//
//        String slotId=bean.getSlotId();
//
//        tv_BoxName.setText(slotId.split("-")[2]);
//
//        tv_UseTime.setText(bean.getUseTime());
//        tv_UseRemark.setText(bean.getUseRemark());
//
//        if(bean.getUseResult()==1){
//            tv_UseResult.setText("成功");
//        }
//        else if(bean.getUseResult()==2) {
//            tv_UseRemark.setText("失败");
//        }
//
//        if((beans.size()-position-1)==0){
//            ll_DividerLine.setVisibility(View.INVISIBLE);
//        }
//        else {
//            ll_DividerLine.setVisibility(View.VISIBLE);
//        }


    }


    @Override
    public int getCount() {
        return items.size();
    }
}
