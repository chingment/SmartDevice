package com.lumos.smartdevice.activity.booker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.BookerSlotVo;
import com.lumos.smartdevice.ui.ViewHolder;

import java.util.List;

public class BookerBorrowReturnInspectSlotAdapter extends BaseAdapter {

    private static final String TAG = "BookerBorrowReturnInspectSlotAdapter";

    private final List<BookerSlotVo> items;
    private final Context context;

    public BookerBorrowReturnInspectSlotAdapter(Context context, List<BookerSlotVo> items) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_booker_borrow_return_inspect_slot, parent, false);
        }

        BookerSlotVo bean = items.get(position);

        CardView cv_Slot = ViewHolder.get(convertView, R.id.cv_Slot);
        TextView tv_SlotName = ViewHolder.get(convertView, R.id.tv_SlotName);
        tv_SlotName.setText(bean.getName());

        if(onClickListener!=null) {
            cv_Slot.setTag(bean);
            cv_Slot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookerSlotVo l_Bean = (BookerSlotVo) view.getTag();
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
        void onClick(BookerSlotVo v);
    }
}
