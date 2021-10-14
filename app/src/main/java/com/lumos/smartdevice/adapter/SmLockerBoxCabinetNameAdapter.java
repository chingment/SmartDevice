package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.ui.ViewHolder;

import java.util.List;

public class SmLockerBoxCabinetNameAdapter extends BaseAdapter {

    private static final String TAG = "SmLockerBoxCabinetNameAdapter";
    private final Context context;
    private final List<CabinetBean> items;
    private final int current_position;

    public SmLockerBoxCabinetNameAdapter(Context context, List<CabinetBean> items, int position) {
        this.context = context;
        this.items = items;
        this.current_position = position;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_smlockerbox_cabinetname, parent, false);
        }
        CabinetBean item = items.get(position);

        TextView tv_Name = ViewHolder.get(convertView, R.id.tv_Name);
        View v_Bg = ViewHolder.get(convertView, R.id.v_Bg);
        tv_Name.setText(item.getName());

        if (position == current_position) {
            v_Bg.setVisibility(View.VISIBLE);
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        } else {
            v_Bg.setVisibility(View.INVISIBLE);
            convertView.setBackgroundColor(ContextCompat.getColor(context,R.color.gray));
        }
        return convertView;
    }
}
