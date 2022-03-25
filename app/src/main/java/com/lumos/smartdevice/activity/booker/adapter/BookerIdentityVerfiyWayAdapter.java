package com.lumos.smartdevice.activity.booker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.adapter.GridNineItemBean;
import com.lumos.smartdevice.ui.ViewHolder;
import com.lumos.smartdevice.utils.CommonUtil;

import java.util.List;

public class BookerIdentityVerfiyWayAdapter extends BaseAdapter {

    private static final String TAG = "BookerIdentityVerfiyWayAdapter";
    private final List<GridNineItemBean> items;
    private final Context context;


    public BookerIdentityVerfiyWayAdapter(Context context, List<GridNineItemBean> items) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_booker_identity_verify_way, parent, false);
        }

        GridNineItemBean bean = items.get(position);

        ImageView img_Icon = ViewHolder.get(convertView, R.id.img_Icon);
        TextView tv_Title = ViewHolder.get(convertView, R.id.tv_Title);
        tv_Title.setText(bean.getTitle());


        if (bean.getIcon() instanceof Integer) {
            img_Icon.setImageDrawable(ContextCompat.getDrawable(context,((int) bean.getIcon())));
        } else {
            CommonUtil.loadImageFromUrl(context, img_Icon, bean.getIcon().toString());
        }

        return convertView;
    }
}
