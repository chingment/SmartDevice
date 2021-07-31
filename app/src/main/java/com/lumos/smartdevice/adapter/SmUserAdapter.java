package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ui.refreshview.MyViewHolder;
import com.lumos.smartdevice.ui.refreshview.RefreshAdapter;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chingment on 2018/6/13.
 */

public class SmUserAdapter extends RefreshAdapter {

    private Context context;
    private List<UserBean> beans = new ArrayList<>();
    private LayoutInflater inflater;

    public void setData(List<UserBean> beans, Context context) {
        this.beans = beans;
        this.context = context;
        LogUtil.e("测试2");
        notifyDataSetChanged();

    }

    public void addData(List<UserBean> beans, Context context)
    {   this.context = context;
        this.beans.addAll(beans);
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_smuser, parent, false)) {
        };
    }

    @Override
    public void onBindItemHolder(final RecyclerView.ViewHolder holder, int position) {

        final UserBean bean = beans.get(position);
        String avatar = bean.getAvatar();
        ImageView img_Avatar = (ImageView) holder.itemView.findViewById(R.id.img_Avatar);

        TextView tv_Username = (TextView) holder.itemView.findViewById(R.id.tv_Username);


        tv_Username.setText(bean.getUserName());

        if (!StringUtil.isEmptyNotNull(avatar)) {

            if(avatar.contains("app://")) {
                String imgName = avatar.split("//")[1];
                int imgId = CommonUtil.getAppDrawableImages(imgName);
                img_Avatar.setImageDrawable(ContextCompat.getDrawable(context,imgId));
            }

        }

    }

    @Override
    public int getCount() {
        return beans.size();
    }
}
