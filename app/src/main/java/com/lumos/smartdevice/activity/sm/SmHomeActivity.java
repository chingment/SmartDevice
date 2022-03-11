package com.lumos.smartdevice.activity.sm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.InitDataActivity;
import com.lumos.smartdevice.activity.dialog.DialogSmOwnInfo;
import com.lumos.smartdevice.adapter.GridNineItemAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetOwnLogout;
import com.lumos.smartdevice.api.rop.RetOwnSaveInfo;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.GridNineItemBean;
import com.lumos.smartdevice.model.GridNineItemType;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ostctrl.OstCtrlInterface;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.own.AppManager;
import com.lumos.smartdevice.own.AppVar;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.dialog.DialogConfirm;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

public class SmHomeActivity extends BaseFragmentActivity implements View.OnClickListener {

    private static final String TAG = "SmHomeActivity";

    private DialogConfirm dialog_Confirm;
    private MyGridView gdv_Nine;
    private List<GridNineItemBean> gdv_Nine_Items;
    private TextView tv_UserFullName;
    private ImageView iv_UserAvatar;
    private DialogSmOwnInfo dialog_OwnInfo;
    private Button btn_Logout;
    private DeviceBean device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_home);
        setNavHeaderTtile(R.string.aty_nav_title_smhome);
        device=getDevice();
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Logout= findViewById(R.id.btn_Logout);
        gdv_Nine = findViewById(R.id.gdv_Nine);
        tv_UserFullName= findViewById(R.id.tv_UserFullName);
        iv_UserAvatar= findViewById(R.id.iv_UserAvatar);
        dialog_OwnInfo=new DialogSmOwnInfo(SmHomeActivity.this);
        dialog_OwnInfo.checkUserNameIsPhoneFormat(false);
        dialog_OwnInfo.setOnClickListener(new DialogSmOwnInfo.OnClickListener() {
            @Override
            public void onSaveResult(ResultBean<RetOwnSaveInfo> rt) {
                if(rt.getCode()== ResultCode.SUCCESS) {
                    RetOwnSaveInfo ret = rt.getData();
                    tv_UserFullName.setText(ret.getFullName());
                    CommonUtil.loadImageFromUrl(SmHomeActivity.this,iv_UserAvatar,ret.getAvatar());
                    showToast(R.string.save_success);
                }
            }
        });
    }

    private void initEvent() {

        gdv_Nine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!NoDoubleClickUtil.isDoubleClick()) {
                    GridNineItemBean gdv_Nine_Item = gdv_Nine_Items.get(position);
                    int type = gdv_Nine_Item.getType();
                    String action = gdv_Nine_Item.getAction();
                    switch (type) {
                        case GridNineItemType.Function:
                            switch (action) {
                                case "lockerboxmanager":
                                    gdvLockerBoxManager();
                                    break;
                                case "lockerboxuserecord":
                                    gdvLockerBoxUseRecord();
                                    break;
                                case "bookertakestock":
                                    gdvBookerTakeStock();
                                    break;
                                case "usermanager":
                                    gdvUserManager();
                                    break;
                                case "checkupdateapp":
                                    break;
                                case "closeapp":
                                    gdvCloseApp();
                                    break;
                                case "rebootsys":
                                    gdvRebootSys();
                                    break;
                                case "opendoor":
                                    gdvOpenDoor();
                                    break;
                                case "deviceinfo":
                                    gdvDeviceInfo();
                                    break;
                            }
                        case GridNineItemType.Url:
                            break;
                    }
                }
            }
        });

        dialog_Confirm = new DialogConfirm(SmHomeActivity.this, "", true);
        dialog_Confirm.setOnClickListener(new DialogConfirm.OnClickListener() {
            @Override
            public void onSure() {
                String fun = dialog_Confirm.getFunction();

                switch (fun) {
                    case "closeapp":
                        dlgCloseApp();
                        break;
                    case "rebootsys":
                        dlgRebootSys();
                        break;
                    case "logout":
                        dlgLogout();
                        break;
                    case "opendoor":
                        dlgOpenDoor();
                        break;
                }
                dialog_Confirm.hide();
            }

            @Override
            public void onCancle() {
                dialog_Confirm.hide();
            }
        });

        btn_Logout.setOnClickListener(this);
        iv_UserAvatar.setOnClickListener(this);
    }

    private void initData() {

        UserBean currentUser= getCurrentUser();
        if(currentUser!=null) {
            tv_UserFullName.setText(currentUser.getFullName());
            CommonUtil.loadImageFromUrl(SmHomeActivity.this, iv_UserAvatar, currentUser.getAvatar());
        }

        gdv_Nine_Items = new ArrayList<>();


        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smdeviceinfo), GridNineItemType.Function, "deviceinfo", R.drawable.ic_sm_deviceinfo));

        if(device.getSceneMode()==AppVar.SCENE_MODE_1) {
            gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smlockerboxmanager), GridNineItemType.Function, "lockerboxmanager", R.drawable.ic_sm_lockerboxmanager));
            gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smlockerboxuserecord), GridNineItemType.Function, "lockerboxuserecord", R.drawable.ic_sm_lockerboxuserecord));
            gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smusermanager), GridNineItemType.Function, "usermanager", R.drawable.ic_sm_usermanager));
        }
        else if(device.getSceneMode()==AppVar.SCENE_MODE_2) {
            gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smbookertakestock), GridNineItemType.Function, "bookertakestock", R.drawable.ic_sm_bookertakestock));
        }


        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smopendoor), GridNineItemType.Function, "opendoor", R.drawable.ic_sm_opendoor));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smcheckupdateapp), GridNineItemType.Function, "checkupdateapp", R.drawable.ic_sm_checkupdateapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smcloseapp), GridNineItemType.Function, "closeapp", R.drawable.ic_sm_closeapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smrebootsys), GridNineItemType.Function, "rebootsys", R.drawable.ic_sm_rebootsys));

        GridNineItemAdapter gridNineItemAdapter = new GridNineItemAdapter(getAppContext(),gdv_Nine_Items);

        gdv_Nine.setAdapter(gridNineItemAdapter);

    }


    private void gdvCloseApp(){
        dialog_Confirm.setTipsImageVisibility(View.GONE);
        dialog_Confirm.setFunction("closeapp");
        dialog_Confirm.setTipsText(getAppContext().getString(R.string.confrim_tips_closeapp));
        dialog_Confirm.show();
    }

    private void gdvRebootSys(){
        dialog_Confirm.setTipsImageVisibility(View.GONE);
        dialog_Confirm.setFunction("rebootsys");
        dialog_Confirm.setTipsText(getAppContext().getString(R.string.confrim_tips_rebootsys));
        dialog_Confirm.show();
    }

    private void gdvOpenDoor(){
        dialog_Confirm.setTipsImageVisibility(View.GONE);
        dialog_Confirm.setFunction("opendoor");
        dialog_Confirm.setTipsText(getAppContext().getString(R.string.confrim_tips_opendoor));
        dialog_Confirm.show();
    }

    private void gdvLockerBoxManager(){
        Intent intent = new Intent(SmHomeActivity.this, SmLockerBoxManagerActivity.class);
        openActivity(intent);
    }

    private void gdvLockerBoxUseRecord(){
        Intent intent = new Intent(SmHomeActivity.this, SmLockerBoxUseRecordActivity.class);
        openActivity(intent);
    }

    private void gdvBookerTakeStock(){
        Intent intent = new Intent(SmHomeActivity.this, SmBookerTakeStockActivity.class);
        openActivity(intent);
    }

    private void gdvUserManager(){
        Intent intent = new Intent(SmHomeActivity.this, SmUserManagerActivity.class);
        intent.putExtra("scene_mode",1);
        openActivity(intent);
    }

    private void gdvDeviceInfo(){
        Intent intent = new Intent(SmHomeActivity.this, SmDeviceInfoActivity.class);
        openActivity(intent);
    }


    private void dlgCloseApp(){
        setHideSysStatusBar(false);
        AppManager.getAppManager().AppExit(SmHomeActivity.this);
    }

    private void dlgRebootSys(){
        setHideSysStatusBar(false);
        OstCtrlInterface.getInstance().reboot(SmHomeActivity.this);
    }

    private void dlgLogout() {

        RopOwnLogout rop = new RopOwnLogout();
        rop.setUserId(AppCacheManager.getCurrentUser().getUserId());
        ReqInterface.getInstance().ownLogout(rop, new ReqHandler() {
            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(SmHomeActivity.this);
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(SmHomeActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<RetOwnLogout> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetOwnLogout>>() {
                });

                if (rt.getCode() == ResultCode.SUCCESS) {
                    AppCacheManager.setCurrentUser(null);
                    Intent intent = new Intent(SmHomeActivity.this, InitDataActivity.class);
                    openActivity(intent);
                    AppManager.getAppManager().finishAllActivity();
                } else {
                    showToast(rt.getMsg());
                }
            }

            @Override
            public void onFailure(String msg, Exception e) {
                super.onFailure(msg, e);
            }
        });
    }


    private void dlgOpenDoor(){

    }


    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            int id = v.getId();


            if (id == R.id.iv_UserAvatar) {
                dialog_OwnInfo.show(AppCacheManager.getCurrentUser().getUserId(), device.getVersionMode());
            } else if (id == R.id.btn_Logout) {
                dialog_Confirm.setTipsImageVisibility(View.GONE);
                dialog_Confirm.setFunction("logout");
                dialog_Confirm.setTipsText(getAppContext().getString(R.string.confrim_tips_exitmanager));
                dialog_Confirm.show();
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog_Confirm != null) {
            dialog_Confirm.cancel();
        }

        if (dialog_OwnInfo != null) {
            dialog_OwnInfo.cancel();
        }
    }
}
