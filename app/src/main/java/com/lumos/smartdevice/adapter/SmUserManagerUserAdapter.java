package com.lumos.smartdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ui.refreshview.MyViewHolder;
import com.lumos.smartdevice.ui.refreshview.RefreshAdapter;
import com.lumos.smartdevice.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chingment on 2018/6/13.
 */

public class SmUserManagerUserAdapter extends RefreshAdapter {
    private static final String TAG = "SmUserAdapter";
    private Context context;
    private List<UserBean> beans = new ArrayList<>();

    private final int scene_mode;

    public SmUserManagerUserAdapter(int scene_mode) {
        this.scene_mode = scene_mode;
    }

    public void setData(List<UserBean> beans, Context context) {
        this.beans = beans;
        this.context = context;
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

        View ll_Info = holder.itemView.findViewById(R.id.ll_Info);
        View ll_DividerLine = holder.itemView.findViewById(R.id.ll_DividerLine);
        ImageView img_Avatar = holder.itemView.findViewById(R.id.img_Avatar);
        TextView tv_UserName = holder.itemView.findViewById(R.id.tv_UserName);
        TextView tv_FullName = holder.itemView.findViewById(R.id.tv_FullName);
        TextView btn_SelectUser = holder.itemView.findViewById(R.id.btn_SelectUser);

        ll_Info.setTag(bean);
        ll_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserBean user = (UserBean) view.getTag();
                if (onClickListener != null) {
                    onClickListener.onItemClick(user);
                }
            }
        });


        img_Avatar.setTag(bean);
        img_Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserBean user = (UserBean) view.getTag();
                if (onClickListener != null) {
                    onClickListener.onItemClick(user);
                }
            }
        });

        if((beans.size()-position-1)==0){
            ll_DividerLine.setVisibility(View.INVISIBLE);
        }
        else {
            ll_DividerLine.setVisibility(View.VISIBLE);
        }

        tv_UserName.setText(bean.getUserName());
        tv_FullName.setText(bean.getFullName());

        if(scene_mode==1){
            btn_SelectUser.setVisibility(View.GONE);
        }
        else if(scene_mode==2) {
            btn_SelectUser.setVisibility(View.VISIBLE);
            btn_SelectUser.setTag(bean);
            btn_SelectUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserBean user = (UserBean) view.getTag();
                    if (onClickListener != null) {
                        onClickListener.onSelectClick(user);
                    }
                }
            });
        }


        CommonUtil.loadImageFromUrl(context, img_Avatar, avatar);

    }


    public OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onItemClick(UserBean bean);
        void onSelectClick(UserBean bean);
    }

    @Override
    public int getCount() {
        return beans.size();
    }
}
