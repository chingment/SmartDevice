package com.lumos.smartdevice.activity.sm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.InitDataActivity;
import com.lumos.smartdevice.activity.dialog.CustomDialogUserEdit;
import com.lumos.smartdevice.adapter.GridNineItemAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetOwnLogout;
import com.lumos.smartdevice.api.rop.RetUserSave;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.model.GridNineItemBean;
import com.lumos.smartdevice.model.GridNineItemType;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ostCtrl.OstCtrlInterface;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.own.AppManager;
import com.lumos.smartdevice.own.AppVar;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.dialog.CustomDialogConfirm;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SmHomeActivity extends BaseFragmentActivity implements View.OnClickListener {

    private static final String TAG = "SmHomeActivity";

    private CustomDialogConfirm dialog_Confirm;
    private MyGridView gdv_Nine;
    private List<GridNineItemBean> gdv_Nine_Items;
    private TextView tv_UserFullName;
    private ImageView iv_UserAvatar;
    private CustomDialogUserEdit dialog_UserEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smhome);

        setNavHeaderTtile(R.string.aty_smhome_nav_title);

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        gdv_Nine = findViewById(R.id.gdv_Nine);
        tv_UserFullName= findViewById(R.id.tv_UserFullName);
        iv_UserAvatar= findViewById(R.id.iv_UserAvatar);
        dialog_UserEdit=new CustomDialogUserEdit(SmHomeActivity.this);
        dialog_UserEdit.checkUserNameIsPhoneFormat(false);
        dialog_UserEdit.setOnClickListener(new CustomDialogUserEdit.OnClickListener() {
            @Override
            public void onSaveResult(ResultBean<RetUserSave> rt) {
                if(rt.getCode()== ResultCode.SUCCESS) {
                    RetUserSave ret = rt.getData();


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
                                case "lockerbox":
                                    gdvLockerBox();
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
                                case "exitmanager":
                                    gdvExitManager();
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

        dialog_Confirm = new CustomDialogConfirm(SmHomeActivity.this, "", true);
        dialog_Confirm.setOnClickListener(new CustomDialogConfirm.OnClickListener() {
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
                    case "exitmanager":
                        dlgExitManager();
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

        iv_UserAvatar.setOnClickListener(this);
    }

    private void initData() {

        UserBean currentUser= getCurrentUser();
        if(currentUser!=null) {
            tv_UserFullName.setText(currentUser.getFullName());
            CommonUtil.loadImageFromUrl(SmHomeActivity.this, iv_UserAvatar, currentUser.getAvatar());
        }

        gdv_Nine_Items = new ArrayList<>();


        if(getDevice().getSceneMode().equals(AppVar.SCENE_MODE_1)) {
            gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_lockerbox), GridNineItemType.Function, "lockerbox", R.drawable.ic_sm_lockerbox));
            gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_lockerboxuserecord), GridNineItemType.Function, "lockerboxuserecord", R.drawable.ic_sm_lockerboxuserecord));
        }


        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_deviceinfo), GridNineItemType.Function, "deviceinfo", R.drawable.ic_sm_deviceinfo));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_usermanager), GridNineItemType.Function, "usermanager", R.drawable.ic_sm_usermanager));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_opendoor), GridNineItemType.Function, "opendoor", R.drawable.ic_sm_opendoor));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_checkupdateapp), GridNineItemType.Function, "checkupdateapp", R.drawable.ic_sm_checkupdateapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_closeapp), GridNineItemType.Function, "closeapp", R.drawable.ic_sm_closeapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_rebootsys), GridNineItemType.Function, "rebootsys", R.drawable.ic_sm_rebootsys));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_exitmanager), GridNineItemType.Function, "exitmanager", R.drawable.ic_sm_exitmanager));


        GridNineItemAdapter gridNineItemAdapter = new GridNineItemAdapter(getAppContext(), gdv_Nine_Items);

        gdv_Nine.setAdapter(gridNineItemAdapter);

    }


    private void gdvExitManager(){
        dialog_Confirm.setTipsImageVisibility(View.GONE);
        dialog_Confirm.setFunction("exitmanager");
        dialog_Confirm.setTipsText(getAppContext().getString(R.string.confrim_tips_exitmanager));
        dialog_Confirm.show();
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

    private void gdvLockerBox(){
        Intent intent = new Intent(SmHomeActivity.this, SmLockerBoxActivity.class);
        startActivity(intent);
    }

    private void gdvUserManager(){
        Intent intent = new Intent(SmHomeActivity.this, SmUserManagerActivity.class);
        intent.putExtra("scene_mode",1);
        startActivity(intent);
    }

    private void gdvDeviceInfo(){
        Intent intent = new Intent(SmHomeActivity.this, SmDeviceInfoActivity.class);
        startActivity(intent);
    }


    private void dlgCloseApp(){
        setHideSysStatusBar(false);
        AppManager.getAppManager().AppExit(SmHomeActivity.this);
    }

    private void dlgRebootSys(){
        setHideSysStatusBar(false);
        OstCtrlInterface.getInstance().reboot(SmHomeActivity.this);
    }

    private void dlgExitManager() {

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
                    startActivity(intent);
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




    private void getOwnInfo(){
        dialog_UserEdit.show(AppCacheManager.getCurrentUser().getUserId());
    }
    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            switch (v.getId()) {
                case R.id.iv_UserAvatar:
                    getOwnInfo();
                    break;
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog_Confirm != null) {
            dialog_Confirm.cancel();
        }
    }
}
