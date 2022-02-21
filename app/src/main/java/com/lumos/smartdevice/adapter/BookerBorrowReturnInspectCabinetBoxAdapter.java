package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import androidx.cardview.widget.CardView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.CabinetSlotBean;
import com.lumos.smartdevice.ui.ViewHolder;

import java.util.List;

public class BookerBorrowReturnInspectCabinetBoxAdapter extends BaseAdapter {

    private static final String TAG = "BookerBorrowReturnInspectCabinetBoxAdapter";

    private final List<CabinetSlotBean> items;
    private final Context context;

    public BookerBorrowReturnInspectCabinetBoxAdapter(Context context,List<CabinetSlotBean> items) {
        this.items = items;
        this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_booker_borrow_return_inspect_cabinet_slot, parent, false);
        }

        CabinetSlotBean bean = items.get(position);

        CardView cv_Slot = ViewHolder.get(convertView, R.id.cv_Slot);
        TextView tv_SlotName = ViewHolder.get(convertView, R.id.tv_SlotName);
        tv_SlotName.setText(bean.getSlotName());

        if(onClickListener!=null) {
            cv_Slot.setTag(bean);
            cv_Slot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CabinetSlotBean l_Bean = (CabinetSlotBean) view.getTag();
                    onClickListener.onClick(l_Bean);
                }
            });
        }

        return convertView;
    }

    private OnClickListener onClickListener;

    public void  setOnClickListener(OnClickListener l){
        this.onClickListener=l;
    }

    public  interface OnClickListener{
        void onClick(CabinetSlotBean v);
    }
}
