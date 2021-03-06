package com.lumos.smartdevice.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

import com.lumos.smartdevice.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chingment on 2017/12/18.
 */

public class CommonUtil {

    public static int getAppDrawableImages(String name){
        Class drawable = R.drawable.class;
        Field field = null;
        try {
            field =drawable.getField(name);
            int images = field.getInt(field.getName());
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }



    public static void loadImageFromUrl(Context context, final ImageView photoView, String imageUrl) {
        if (StringUtil.isEmptyNotNull(imageUrl)) {
            photoView.setBackgroundResource(R.drawable.default_image);
        } else if(imageUrl.contains("app://")){
            String imgName = imageUrl.split("//")[1];
            int imgId = CommonUtil.getAppDrawableImages(imgName);
            photoView.setImageDrawable(ContextCompat.getDrawable(context, imgId));
        }else
            //.centerCrop()
            Picasso.with(context).load(imageUrl)
                    .placeholder(R.drawable.default_image).fit().centerInside()
                    .into(photoView, new Callback() {
                        @Override
                        public void onSuccess() {

                            photoView.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onError() {
                            photoView.setBackgroundResource(R.drawable.default_image);
                        }
                    });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {

        // ??????ListView?????????Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()????????????????????????

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0); // ????????????View ?????????
            totalHeight += listItem.getMeasuredHeight(); // ??????????????????????????????

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        // listView.getDividerHeight()???????????????????????????????????????

        // params.height??????????????????ListView???????????????????????????

        listView.setLayoutParams(params);

    }

    public static void setListViewHeightBasedOnChildren(
            View contentlayout, ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() == 0) {
            return;
        }

        int totalHeight = 0;
        int count = listAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        ViewGroup.LayoutParams contentlayout_params = contentlayout
                .getLayoutParams();
        contentlayout_params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
                + 13;
        contentlayout.setLayoutParams(contentlayout_params);
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridview) {

        ListAdapter listAdapter = gridview.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // ???????????????????????????
        int numColumns= gridview.getNumColumns(); //5
        int totalHeight = 0;
        // ??????????????????????????????
        for (int i = 0; i < listAdapter.getCount(); i += numColumns) {
            // ??????gridview????????????item
            View listItem = listAdapter.getView(i, null, gridview);
            listItem.measure(0, 0);
            // ??????item????????????
            totalHeight += listItem.getMeasuredHeight();
        }
        // ??????gridview???????????????
        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        params.height = totalHeight;
        gridview.setLayoutParams(params);


    }


    public static int px2dip(int pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    public static void setRadioGroupCheckedByStringTag(RadioGroup testRadioGroup, String checkedtag) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) testRadioGroup.getChildAt(i);
            String tag = String.valueOf(rb.getTag());

            if (tag.equals(checkedtag)) {
                rb.setChecked(true);
            }
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    public static boolean isDateOneBigger(String str1, String str2) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }

    public static String ConvertPrice(float price) {
        String s = String.format("%.2f", price);

        return s;
    }

    public static String[] getPrice(String price) {


        String[] arr = new String[]{"0", ".00"};


        String[] arrPrice = price.split("\\.");

        if (arrPrice.length == -1) {
            arr[0] = price;
        } else {
            if (arrPrice.length >= 2) {
                arr[0] = arrPrice[0];
                arr[1] = "." + arrPrice[1];
            }
        }


        return arr;

    }

    public static Boolean Char2Bool(char c) {
        if(c=='0')
        return false;

        return true;
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static boolean isPhone(String phone){
        String regex="^1[3456789]\\d{9}$";
        if (phone.length()!=11){
            return false;
        }else {
            Pattern p=Pattern.compile(regex);
            Matcher m=p.matcher(phone);
            boolean isMatch=m.matches();
            return isMatch;
        }
    }

    public static boolean isPassword(String password) {
        Pattern p = Pattern.compile("[0-9a-zA-Z_]{6,18}");
        Matcher m = p.matcher(password);
        return m.matches();
    }
    
}
