package com.lumos.smartdevice.activity.booker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.alibaba.fastjson.TypeReference;
import com.danikula.videocache.HttpProxyCacheServer;
import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.service.BookerCtrl;
import com.lumos.smartdevice.activity.sm.SmLoginActivity;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetDeviceCheckAppVersion;
import com.lumos.smartdevice.api.vo.AdVo;
import com.lumos.smartdevice.api.vo.AdCreativeVo;
import com.lumos.smartdevice.api.vo.BookerCustomDataVo;
import com.lumos.smartdevice.api.vo.BookerDriveLockeqVo;
import com.lumos.smartdevice.api.vo.BookerDriveRfeqVo;
import com.lumos.smartdevice.api.vo.BookerSlotDrivesVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.http.HttpClient;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.own.Config;
import com.lumos.smartdevice.utils.DeviceUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LongClickUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookerMainActivity  extends BookerBaseActivity {

    private static final String TAG = "BookerMainActivity";

    private View btn_QueryBook;
    private View rl_Header;
    private View btn_BorrowBook;
    private View btn_ReturnBook;
    private VideoView vv_Ad;
    private int vv_Ad_StopLength;
    public static HttpProxyCacheServer proxy;

    private BookerCustomDataVo bookerCustomData;

    private BookerCtrl bookerCtrl;

    private MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_main);
        bookerCustomData = AppCacheManager.getBookerCustomData();

//        bookerCtrl=BookerCtrl.getInstance();
//
//
//        ExecutorService service = Executors.newFixedThreadPool(1000);
//        service.execute(()->{
//
//            try {
//
//                for (int i=0;i<10;i++){
//                    BookerSlotVo slot=new BookerSlotVo();
//
//                    slot.setSlotId(String.valueOf(i));
//                    slot.setName(String.valueOf(i));
//                    BookerSlotDrivesVo drivesVo=new BookerSlotDrivesVo();
//                    BookerDriveLockeqVo lockeq=new BookerDriveLockeqVo();
//                    lockeq.setDriveId("Lockeq_1");
//                    lockeq.setAnt("1");
//                    lockeq.setPlate("1");
//
//                    drivesVo.setLockeq(lockeq);
//                    BookerDriveRfeqVo rfeq=new BookerDriveRfeqVo();
//                    rfeq.setDriveId("Rfeq_1");
//                    rfeq.setAnt("1");
//                    drivesVo.setRfeq(rfeq);
//                    slot.setDrives(drivesVo);
//                    //String flowId=String.valueOf(i);
//
//                    bookerCtrl.borrowReturnStart("c89cae062f9b4b098687969fee260000",1,"1",getDevice(),slot,null);
//                    bookerCtrl.borrowReturnStart("c89cae062f9b4b098687969fee260000",1,"1",getDevice(),slot,null);
////                    Thread.sleep(200);
//                }
//
//            }catch (Exception e) {
//
//                e.printStackTrace();
//
//            }
//
//        });




        initView();
        initEvent();
        initData();

    }

    public void initView() {

        vv_Ad = findViewById(R.id.vv_Ad);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        vv_Ad.setLayoutParams(layoutParams);

        btn_QueryBook = findViewById(R.id.btn_QueryBook);
        btn_BorrowBook = findViewById(R.id.btn_BorrowBook);
        btn_ReturnBook = findViewById(R.id.btn_ReturnBook);

        rl_Header = findViewById(R.id.rl_Header);
    }

    public void initEvent() {
        btn_QueryBook.setOnClickListener(this);
        btn_BorrowBook.setOnClickListener(this);
        btn_ReturnBook.setOnClickListener(this);

        LongClickUtil.setLongClick(new Handler(), rl_Header, 500, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getAppContext(), SmLoginActivity.class);
                intent.putExtra("scene_mode","manager_center");
                openActivity(intent);
                return true;
            }
        });

        proxy = new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024) //1Gb 緩存
                .maxCacheFilesCount(5)//最大緩存5個視頻
                .build();

    }

    public void initData() {

        if (bookerCustomData != null) {


//广告
            HashMap<String, AdVo> ads = bookerCustomData.getAds();
            if (ads != null) {
                if (ads.containsKey("101")) {
                    AdVo ad = ads.get("101");
                    if (ad != null) {
                        List<AdCreativeVo> adCreatives = ad.getCreatives();
                        if (adCreatives != null && adCreatives.size() > 0) {
                            playVvAd(adCreatives.get(0).getFileUrl());
                        }
                    }
                }
            }


        }
    }

    private void  playVvAd(String fileUrl) {
        String vv_Ad_Url = proxy.getProxyUrl(fileUrl);

        Uri uri = Uri.parse(vv_Ad_Url);

        vv_Ad.setVideoURI(uri);


        vv_Ad.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                vv_Ad.start();
            }
        });


//        mediaMetadataRetriever.setDataSource(fileUrl);
//        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(1);
//        vv_Ad.setBackground(new BitmapDrawable(null, bitmap));


//        vv_Ad.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mPlayer) {
//
//                vv_Ad.setVideoPath(vv_Ad_Url);
//                vv_Ad.start();
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        vv_Ad_StopLength = vv_Ad.getCurrentPosition();

        vv_Ad.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        vv_Ad.seekTo(vv_Ad_StopLength);
        vv_Ad.start();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_BorrowBook) {
                Intent intent = new Intent(getAppContext(), BookerIdentityVerifyActivity.class);
                intent.putExtra("action", "borrow_books");
                openActivity(intent);
            } else if (id == R.id.btn_ReturnBook) {
                Intent intent = new Intent(getAppContext(), BookerIdentityVerifyActivity.class);
                intent.putExtra("action", "return_books");
                openActivity(intent);
            } else if (id == R.id.btn_QueryBook) {
                Intent intent = new Intent(getAppContext(), BookerDisplayBooksActivity.class);
                intent.putExtra("action", "display_books");
                openActivity(intent);
            }

        }
    }
}