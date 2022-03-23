package com.lumos.smartdevice.activity.sm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.LockerBoxUseRecordVo;
import com.lumos.smartdevice.model.UserVo;
import com.lumos.smartdevice.ui.refreshview.MyViewHolder;
import com.lumos.smartdevice.ui.refreshview.RefreshAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chingment on 2018/6/13.
 */

public class SmLockerBoxUseRecordAdapter extends RefreshAdapter {
    private static final String TAG = "SmLockerBoxUseRecordAdapter";
    private Context context;
    private List<LockerBoxUseRecordVo> beans = new ArrayList<>();

    public SmLockerBoxUseRecordAdapter() {

    }

    public void setData(List<LockerBoxUseRecordVo> beans, Context context) {
        this.beans = beans;
        this.context = context;
        notifyDataSetChanged();

    }

    public void addData(List<LockerBoxUseRecordVo> beans, Context context)
    {   this.context = context;
        this.beans.addAll(beans);
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sm_locker_box_userecord, parent, false)) {
        };
    }

    @Override
    public void onBindItemHolder(final RecyclerView.ViewHolder holder, int position) {

        final LockerBoxUseRecordVo bean = beans.get(position);

        View ll_Info = holder.itemView.findViewById(R.id.ll_Info);
        View ll_DividerLine = holder.itemView.findViewById(R.id.ll_DividerLine);


        TextView tv_BoxName = holder.itemView.findViewById(R.id.tv_BoxName);
        TextView tv_UseTime = holder.itemView.findViewById(R.id.tv_UseTime);
        TextView tv_UseRemark = holder.itemView.findViewById(R.id.tv_UseRemark);
        TextView tv_UseResult = holder.itemView.findViewById(R.id.tv_UseResult);

        String slotId=bean.getSlotId();

        tv_BoxName.setText(slotId.split("-")[2]);

        tv_UseTime.setText(bean.getUseTime());
        tv_UseRemark.setText(bean.getUseRemark());

        if(bean.getUseResult()==1){
            tv_UseResult.setText("成功");
        }
        else if(bean.getUseResult()==2) {
            tv_UseRemark.setText("失败");
        }

        if((beans.size()-position-1)==0){
            ll_DividerLine.setVisibility(View.INVISIBLE);
        }
        else {
            ll_DividerLine.setVisibility(View.VISIBLE);
        }



    }


    public OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onItemClick(UserVo bean);
        void onSelectClick(UserVo bean);
    }

    @Override
    public int getCount() {
        return beans.size();
    }
}
