package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.CabinetBoxBean;
import com.lumos.smartdevice.ui.ViewHolder;

import java.util.List;

public class BookerBorrowReturnInspectCabinetBoxAdapter extends BaseAdapter {

    private static final String TAG = "BookerBorrowReturnInspectCabinetBoxAdapter";

    private final List<CabinetBoxBean> items;
    private final Context context;

    public BookerBorrowReturnInspectCabinetBoxAdapter(Context context,List<CabinetBoxBean> items) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_nine_booker_borrow_return_inspect_cabinet_box, parent, false);
        }

        CabinetBoxBean bean = items.get(position);

        TextView tv_BoxName = ViewHolder.get(convertView, R.id.tv_BoxName);
        tv_BoxName.setText(bean.getBoxName());

        if(onClickListener!=null) {
            tv_BoxName.setTag(bean);
            tv_BoxName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CabinetBoxBean l_Bean = (CabinetBoxBean) view.getTag();
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
        void onClick(CabinetBoxBean v);
    }
}
