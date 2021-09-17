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

import java.util.HashMap;
import java.util.List;

/**
 * 实现瀑布流效果的适配器
 */
public class SmCabinetLayoutBoxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> list;//数据
    private HashMap<String, LockerBoxBean> boxs;//数据
    private OnRecyclerItemClickListener mOnItemClickListener;//单击事件
    private onRecyclerItemLongClickListener mOnItemLongClickListener;//长按事件


    public SmCabinetLayoutBoxAdapter(Context context, List<String> list, HashMap<String, LockerBoxBean> boxs) {
        this.context = context;
        this.list = list;
        this.boxs = boxs;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //找到item的布局
        View view= LayoutInflater.from(context).inflate(R.layout.item_smcabinet_layout_box,parent,false);
        return new MyViewHolder(view);//将布局设置给holder
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 绑定视图到holder,就如同ListView的getView(),但是这里已经把复用实现了,我们只需要填充数据就行.
     * 由于在复用的时候都是调用该方法填充数据,但是上滑的时候,又会随机产生高度设置到控件上,这样当滑
     * 到顶部可能就会看到一片空白,因为后面随机产生的高度和之前的高度不一样,就不能填充屏幕了,所以
     * 需要记录每个控件产生的随机高度,然后在复用的时候再设置上去
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //填充数据

        TextView tv_Box = holder.itemView.findViewById(R.id.tv_Box);

        String cell = list.get(position);

        String[] cell_prams = cell.split("-");

        LockerBoxBean box = boxs.get(cell);

        String id = cell_prams[0];
        String plate = cell_prams[1];
        String name = cell_prams[2];
        String isUse = cell_prams[3];
        Integer height = Integer.valueOf(cell_prams[4]);

        tv_Box.setText(name);

        //由于需要实现瀑布流的效果,所以就需要动态的改变控件的高度了
        ViewGroup.LayoutParams params = tv_Box.getLayoutParams();
        params.width=200;
        params.height = height;
        tv_Box.setLayoutParams(params);


        if (box != null) {
            String box_IsUsed = box.getIsUsed();
            if (box_IsUsed.equals("0")) {
                tv_Box.setBackgroundColor(context.getResources().getColor(R.color.locker_box_status_1));
            } else {
                tv_Box.setBackgroundColor(context.getResources().getColor(R.color.locker_box_status_2));
            }
        }

        //设置单击事件
        if (mOnItemClickListener != null) {
            tv_Box.setTag(box);
            tv_Box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这里是为textView设置了单击事件,回调出去
                    //mOnItemClickListener.onItemClick(v,position);这里需要获取布局中的position,不然乱序
                    mOnItemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }

        //长按事件
        if (mOnItemLongClickListener != null) {
            tv_Box.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //回调出去
                    mOnItemLongClickListener.onItemLongClick(v, holder.getLayoutPosition());
                    return true;//不返回true,松手还会去执行单击事件
                }
            });
        }
    }

//    class MyViewHolder extends RecyclerView.ViewHolder{
//
//        TextView textView;
//
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            textView = (TextView) itemView.findViewById(R.id.tv_Box);
//        }
//    }

    /**
     * 处理item的点击事件,因为recycler没有提供单击事件,所以只能自己写了
     */
     public   interface OnRecyclerItemClickListener {
        public void onItemClick(View view, int position);
    }

    /**
     * 长按事件
     */
    public interface  onRecyclerItemLongClickListener{
        public void onItemLongClick(View view, int position);
    }

    /**
     * 暴露给外面的设置单击事件
     */
    public void setOnItemClickListener(OnRecyclerItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 暴露给外面的长按事件
     */
    public void setOnItemLongClickListener(onRecyclerItemLongClickListener onItemLongClickListener){
        mOnItemLongClickListener = onItemLongClickListener;
    }


}