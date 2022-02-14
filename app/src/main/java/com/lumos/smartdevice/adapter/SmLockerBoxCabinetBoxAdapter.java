package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.LockerBoxBean;
import com.lumos.smartdevice.ui.refreshview.MyViewHolder;

import java.util.List;

/**
 * 实现瀑布流效果的适配器
 */
public class SmLockerBoxCabinetBoxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "SmLockerBoxCabinetBoxAdapter";

    private final Context context;
    private final List<LockerBoxBean> list;//数据
    private OnRecyclerItemClickListener mOnItemClickListener;//单击事件
    private onRecyclerItemLongClickListener mOnItemLongClickListener;//长按事件


    public SmLockerBoxCabinetBoxAdapter(Context context, List<LockerBoxBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_sm_locker_box_cabinet_box,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        TextView tv_Name = holder.itemView.findViewById(R.id.tv_Name);
        //TextView tv_UseStatus = holder.itemView.findViewById(R.id.tv_UseStatus);

        LockerBoxBean item = list.get(position);

        String[] cell_prams = item.getSlotId().split("-");

        String id = cell_prams[0];
        String plate = cell_prams[1];
        String name = cell_prams[2];

        tv_Name.setText(name);

        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = item.getWidth();
        params.height = item.getHeight();
        holder.itemView.setLayoutParams(params);

        if (item.isOpen()) {
            holder.itemView.setBackgroundResource(R.drawable.locker_box_door_status_2);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.locker_box_door_status_1);
        }

        if (item.isUsed()) {
            tv_Name.setTextColor(context.getResources().getColor(R.color.locker_box_use_status_2));
        } else {
            tv_Name.setTextColor(context.getResources().getColor(R.color.locker_box_use_status_1));
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setTag(item);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }

        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onItemLongClick(v, holder.getLayoutPosition());
                    return true;
                }
            });
        }
    }


    public   interface OnRecyclerItemClickListener {
        public void onItemClick(View view, int position);
    }


    public interface  onRecyclerItemLongClickListener{
        public void onItemLongClick(View view, int position);
    }


    public void setOnItemClickListener(OnRecyclerItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(onRecyclerItemLongClickListener onItemLongClickListener){
        mOnItemLongClickListener = onItemLongClickListener;
    }


}