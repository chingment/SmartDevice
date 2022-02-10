package com.lumos.smartdevice.activity.sm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.InitDataActivity;
import com.lumos.smartdevice.activity.dialog.CustomDialogOwnInfo;
import com.lumos.smartdevice.adapter.GridNineItemAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetOwnLogout;
import com.lumos.smartdevice.api.rop.RetOwnSaveInfo;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.model.GridNineItemBean;
import com.lumos.smartdevice.model.GridNineItemType;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ostctrl.OstCtrlInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SmHelpToolActivity extends BaseFragmentActivity {
    private static final String TAG = "SmHelpToolActivity";

    private CustomDialogConfirm dialog_Confirm;
    private MyGridView gdv_Nine;
    private List<GridNineItemBean> gdv_Nine_Items;
    private TextView tv_UserFullName;
    private ImageView iv_UserAvatar;
    private CustomDialogOwnInfo dialog_OwnInfo;
    private Button btn_Logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_help_tool);

        setNavHeaderTtile(R.string.aty_nav_title_smhelptool);

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Logout= findViewById(R.id.btn_Logout);
        tv_UserFullName= findViewById(R.id.tv_UserFullName);
        iv_UserAvatar= findViewById(R.id.iv_UserAvatar);
        gdv_Nine = findViewById(R.id.gdv_Nine);


        dialog_OwnInfo=new CustomDialogOwnInfo(SmHelpToolActivity.this);
        dialog_OwnInfo.checkUserNameIsPhoneFormat(false);
        dialog_OwnInfo.setOnClickListener(new CustomDialogOwnInfo.OnClickListener() {
            @Override
            public void onSaveResult(ResultBean<RetOwnSaveInfo> rt) {
                if(rt.getCode()== ResultCode.SUCCESS) {
                    RetOwnSaveInfo ret = rt.getData();
                    tv_UserFullName.setText(ret.getFullName());
                    CommonUtil.loadImageFromUrl(SmHelpToolActivity.this,iv_UserAvatar,ret.getAvatar());
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
                                case "factorysetting":
                                    gdvFactorySetting();
                                    break;
                                case "showsysstatusbar":
                                    gdvShowSysStatusBar();
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
                            }
                         case GridNineItemType.Url:
                             break;
                    }
                }
            }
        });

        dialog_Confirm = new CustomDialogConfirm(SmHelpToolActivity.this, "", true);
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
                    case "logout":
                        logout();
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
            CommonUtil.loadImageFromUrl(SmHelpToolActivity.this, iv_UserAvatar, currentUser.getAvatar());
        }

        gdv_Nine_Items = new ArrayList<>();
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smfactorysetting), GridNineItemType.Function, "factorysetting", R.drawable.ic_sm_factorysetting));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smshowsysstatusbar), GridNineItemType.Function, "showsysstatusbar", R.drawable.ic_sm_showsysstatusbar));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smopendoor), GridNineItemType.Function, "opendoor", R.drawable.ic_sm_opendoor));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smcheckupdateapp), GridNineItemType.Function, "checkupdateapp", R.drawable.ic_sm_checkupdateapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smcloseapp), GridNineItemType.Function, "closeapp", R.drawable.ic_sm_closeapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_nav_title_smrebootsys), GridNineItemType.Function, "rebootsys", R.drawable.ic_sm_rebootsys));

        GridNineItemAdapter gridNineItemAdapter = new GridNineItemAdapter(getAppContext(), gdv_Nine_Items);

        gdv_Nine.setAdapter(gridNineItemAdapter);




    }

    private void gdvFactorySetting(){
        Intent intent = new Intent(SmHelpToolActivity.this, SmFactorySettingActivity.class);
        startActivity(intent);

    }

    private void gdvShowSysStatusBar(){
        OstCtrlInterface.getInstance().setHideStatusBar(SmHelpToolActivity.this,false);
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


    private void dlgCloseApp(){
        setHideSysStatusBar(false);
        AppManager.getAppManager().AppExit(SmHelpToolActivity.this);
    }

    private void dlgRebootSys(){
        setHideSysStatusBar(false);
        OstCtrlInterface.getInstance().reboot(SmHelpToolActivity.this);
    }

    private void logout() {

        RopOwnLogout rop = new RopOwnLogout();
        rop.setUserId(AppCacheManager.getCurrentUser().getUserId());
        ReqInterface.getInstance(AppVar.VERSION_MODE_1).ownLogout(rop, new ReqHandler() {
            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(SmHelpToolActivity.this);
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(SmHelpToolActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<RetOwnLogout> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetOwnLogout>>() {
                });

                if (rt.getCode() == ResultCode.SUCCESS) {
                    AppCacheManager.setCurrentUser(null);
                    Intent intent = new Intent(SmHelpToolActivity.this, InitDataActivity.class);
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


    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            int id = v.getId();

            if (id == R.id.iv_UserAvatar) {
                dialog_OwnInfo.show(AppCacheManager.getCurrentUser().getUserId(), AppVar.VERSION_MODE_1);
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
    }
}
