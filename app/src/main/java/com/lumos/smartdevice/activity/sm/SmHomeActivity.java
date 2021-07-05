package com.lumos.smartdevice.activity.sm;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.InitDataActivity;
import com.lumos.smartdevice.adapter.GridNineItemAdapter;
import com.lumos.smartdevice.model.GridNineItemBean;
import com.lumos.smartdevice.model.GridNineItemType;
import com.lumos.smartdevice.ostCtrl.OstCtrlInterface;
import com.lumos.smartdevice.own.AppManager;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.dialog.CustomDialogConfirm;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class SmHomeActivity extends BaseFragmentActivity {

    private static final String TAG = "SmHomeActivity";

    private CustomDialogConfirm dialog_Confirm;
    private MyGridView gdv_Nine;
    private List<GridNineItemBean> gdv_Nine_Items;

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

        gdv_Nine_Items = new ArrayList<GridNineItemBean>();


        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_checkupdateapp), GridNineItemType.Function, "gdv.checkupdateapp", R.drawable.ic_sm_checkupdateapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_closeapp), GridNineItemType.Function, "gdv.closeapp", R.drawable.ic_sm_closeapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_rebootsys), GridNineItemType.Function, "gdv.rebootsys", R.drawable.ic_sm_rebootsys));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_exitmanager), GridNineItemType.Function, "gdv.exitmanager", R.drawable.ic_sm_exitmanager));


        GridNineItemAdapter gridNineItemAdapter = new GridNineItemAdapter(getAppContext(), gdv_Nine_Items);

        gdv_Nine.setAdapter(gridNineItemAdapter);



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
                                case "gdv.checkupdateapp":
                                    break;
                                case "gdv.closeapp":
                                    gdvCloseApp();
                                    break;
                                case "gdv.rebootsys":
                                    gdvRebootSys();
                                    break;
                                case "gdv.exitmanager":
                                    gdvExitManager();
                                    break;
                            }
                        case GridNineItemType.Url:
                            break;
                    }
                }
            }
        });

        dialog_Confirm = new CustomDialogConfirm(SmHomeActivity.this, "", true);
        dialog_Confirm.getBtnSure().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();

                switch (tag) {
                    case "dlg.closeapp":
                        dlgCloseApp();
                        break;
                    case "dlg.rebootsys":
                        dlgRebootSys();
                        break;
                    case "dlg.exitmanager":
                        dlgExitManager();
                        break;
                }
                dialog_Confirm.hide();
            }
        });

        dialog_Confirm.getBtnCancle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_Confirm.hide();
            }
        });

    }

    private void initData() {

    }


    private void gdvExitManager(){
        dialog_Confirm.getTipsImage().setVisibility(View.GONE);
        dialog_Confirm.getBtnSure().setTag("dlg.exitmanager");
        dialog_Confirm.getTipsText().setText(getAppContext().getString(R.string.confrim_tips_exitmanager));
        dialog_Confirm.show();
    }

    private void gdvCloseApp(){
        dialog_Confirm.getTipsImage().setVisibility(View.GONE);
        dialog_Confirm.getBtnSure().setTag("dlg.closeapp");
        dialog_Confirm.getTipsText().setText(getAppContext().getString(R.string.confrim_tips_closeapp));
        dialog_Confirm.show();
    }

    private void gdvRebootSys(){
        dialog_Confirm.getTipsImage().setVisibility(View.GONE);
        dialog_Confirm.getBtnSure().setTag("dlg.rebootsys");
        dialog_Confirm.getTipsText().setText(getAppContext().getString(R.string.confrim_tips_rebootsys));
        dialog_Confirm.show();
    }


    private void dlgCloseApp(){
        setHideSysStatusBar(false);
        AppManager.getAppManager().AppExit(SmHomeActivity.this);
    }

    private void dlgRebootSys(){
        setHideSysStatusBar(false);
        OstCtrlInterface.getInstance().reboot(SmHomeActivity.this);
    }

    private void dlgExitManager(){
        Intent intent = new Intent(SmHomeActivity.this, InitDataActivity.class);
        startActivity(intent);
        AppManager.getAppManager().finishAllActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog_Confirm != null) {
            dialog_Confirm.cancel();
        }
    }
}