package com.lumos.smartdevice.activity.sm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.ui.refreshview.MyViewHolder;
import com.lumos.smartdevice.ui.refreshview.RefreshAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chingment on 2018/6/13.
 */

public class SmBookerStockSlotAdapter extends RefreshAdapter {
    private static final String TAG = "SmBookerStockSlotAdapter";
    private Context context;
    private List<BookerSlotVo> items = new ArrayList<>();

    public SmBookerStockSlotAdapter() {

    }

    public void setData(List<BookerSlotVo> items, Context context) {
        this.items = items;
        this.context = context;
        notifyDataSetChanged();

    }

    public void addData(List<BookerSlotVo> items, Context context) {
        this.items.addAll(items);
        this.context = context;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sm_booker_stock_slot, parent, false)) {
        };
    }

    @Override
    public void onBindItemHolder(final RecyclerView.ViewHolder holder, int position) {

        TextView tv_Name = holder.itemView.findViewById(R.id.tv_Name);
        TextView tv_StockQuantity = holder.itemView.findViewById(R.id.tv_StockQuantity);
        TextView tv_Status = holder.itemView.findViewById(R.id.tv_Status);
        TextView tv_LastInboundTime = holder.itemView.findViewById(R.id.tv_LastInboundTime);
        TextView btn_TakeStock = holder.itemView.findViewById(R.id.btn_TakeStock);
        TextView btn_OpenDoor = holder.itemView.findViewById(R.id.btn_OpenDoor);


        BookerSlotVo item = items.get(position);

        if(item.getIsOpen()){
            tv_Status.setText("（已打开）");
            tv_Status.setTextColor(context.getResources().getColor(R.color.booker_borrow_status_2000));
        }
        else {
            tv_Status.setText("（已关闭）");
            tv_Status.setTextColor(context.getResources().getColor(R.color.booker_borrow_status_3000));
        }

        tv_Name.setText(String.format("%s号柜", item.getName()));
        tv_StockQuantity.setText(String.format("%s本", item.getStockQuantity()));
        tv_LastInboundTime.setText(item.getLastInboundTime());

        btn_TakeStock.setTag(item);

        btn_TakeStock.setOnClickListener(view -> {
            if(onClickListener!=null) {
                BookerSlotVo l_item = (BookerSlotVo) view.getTag();
                onClickListener.onTakeStock(l_item);
            }
        });

        btn_OpenDoor.setTag(item);

        btn_OpenDoor.setOnClickListener(view -> {
            if(onClickListener!=null) {
                BookerSlotVo l_item = (BookerSlotVo) view.getTag();
                onClickListener.onOpenDoor(l_item);
            }
        });

    }


    @Override
    public int getCount() {
        return items.size();
    }

    public OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onTakeStock(BookerSlotVo item);
        void onOpenDoor(BookerSlotVo item);
    }
}
