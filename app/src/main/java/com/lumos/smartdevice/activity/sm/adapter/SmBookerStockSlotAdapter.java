package com.lumos.smartdevice.activity.sm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.vo.BookerBorrowBookVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.BookerStockSlotVo;
import com.lumos.smartdevice.ui.refreshview.MyViewHolder;
import com.lumos.smartdevice.ui.refreshview.RefreshAdapter;
import com.lumos.smartdevice.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chingment on 2018/6/13.
 */

public class SmBookerStockSlotAdapter extends RefreshAdapter {
    private static final String TAG = "SmBookerStockSlotAdapter";
    private Context context;
    private List<BookerStockSlotVo> items = new ArrayList<>();

    public SmBookerStockSlotAdapter() {

    }

    public void setData(List<BookerStockSlotVo> items, Context context) {
        this.items = items;
        this.context = context;
        notifyDataSetChanged();

    }

    public void addData(List<BookerStockSlotVo> items, Context context) {
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
        TextView tv_LastTakeTime = holder.itemView.findViewById(R.id.tv_LastTakeTime);
        TextView btn_StockTake = holder.itemView.findViewById(R.id.btn_StockTake);

        BookerStockSlotVo item = items.get(position);

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
        tv_LastTakeTime.setText(item.getLastTakeTime());

        btn_StockTake.setTag(item);

        btn_StockTake.setOnClickListener(view -> {
            if(onClickListener!=null) {
                BookerStockSlotVo l_item = (BookerStockSlotVo) view.getTag();
                onClickListener.onStockTake(l_item);
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
        void onStockTake(BookerStockSlotVo bean);
    }
}
