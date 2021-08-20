package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.LockerBoxUsageBean;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ui.ViewHolder;
import com.lumos.smartdevice.utils.CommonUtil;

import java.util.List;

public class SmLockerBoxUsageAdapter extends BaseAdapter {

    private static final String TAG = "SmLockerBoxUsageAdapter";
    private Context context;
    private List<LockerBoxUsageBean> items;


    public SmLockerBoxUsageAdapter(Context context, List<LockerBoxUsageBean> items) {
        this.context = context;
        this.items = items;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_smlockerboxusage, parent, false);
        }
        final LockerBoxUsageBean item = items.get(position);

        String usageType=item.getUsageType();

        View ll_DividerLine = ViewHolder.get(convertView,R.id.ll_DividerLine);
        ImageView img_Avatar =ViewHolder.get(convertView,R.id.img_Avatar);
        TextView tv_BigTitle = ViewHolder.get(convertView, R.id.tv_BigTitle);
        TextView tv_SmallTtile = ViewHolder.get(convertView, R.id.tv_SmallTtile);
        TextView btn_Delete = ViewHolder.get(convertView, R.id.btn_Delete);
        if(usageType.equals("1")) {

            UserBean user = JSON.parseObject(item.getCustomData(), new TypeReference<UserBean>() {
            });

            tv_BigTitle.setText(user.getFullName());
            tv_SmallTtile.setText(user.getUserName());
            CommonUtil.loadImageFromUrl(context,img_Avatar,user.getAvatar());

        }
        btn_Delete.setTag(item);
        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener!=null){
                    LockerBoxUsageBean l_Item=(LockerBoxUsageBean)view.getTag();
                    onClickListener.onDeleteClick(l_Item);
                }
            }
        });

        if((items.size()-position-1)==0){
            ll_DividerLine.setVisibility(View.INVISIBLE);
        }
        else {
            ll_DividerLine.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onDeleteClick(LockerBoxUsageBean usage);
    }
}
