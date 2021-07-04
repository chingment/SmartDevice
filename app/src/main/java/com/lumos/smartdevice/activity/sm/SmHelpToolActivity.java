package com.lumos.smartdevice.activity.sm;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.InitDataActivity;
import com.lumos.smartdevice.adapter.GridNineItemAdapter;
import com.lumos.smartdevice.model.GridNineItemBean;
import com.lumos.smartdevice.model.GridNineItemType;
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

public class SmHelpToolActivity extends BaseFragmentActivity {
    private static final String TAG = "SmHelpToolActivity";


    private CustomDialogConfirm dialog_Confirm;

    private MyGridView gdv_Nine;
    private List<GridNineItemBean> gdv_Nine_Items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smhelptool);

        setNavHeaderTtile(R.string.aty_smhelptool_nav_title);

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        gdv_Nine = findViewById(R.id.gdv_Nine);

        gdv_Nine_Items = new ArrayList<GridNineItemBean>();
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_factorysetting), GridNineItemType.Function, "fun.factorysetting", R.drawable.ic_sm_showsysnav));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_showsysnav), GridNineItemType.Function, "fun.showsysnav", R.drawable.ic_sm_showsysnav));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_checkupdateapp), GridNineItemType.Function, "fun.checkupdateapp", R.drawable.ic_sm_checkupdateapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_closeapp), GridNineItemType.Function, "fun.closeapp", R.drawable.ic_sm_closeapp));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_rebootsys), GridNineItemType.Function, "fun.rebootsys", R.drawable.ic_sm_rebootsys));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.aty_smhelptool_gdv_nine_it_txt_exitmanager), GridNineItemType.Function, "fun.exitmanager", R.drawable.ic_sm_exitmanager));


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
                                case "fun.factorysetting":
                                    factorySetting();
                                    break;
                                case "fun.showsysnav":
                                    break;
                                case "fun.checkupdateapp":
                                    break;
                                case "fun.closeapp":
                                    break;
                                case "fun.rebootsys":
                                    break;
                                case "fun.exitmanager":
                                    exitManager();
                                    break;
                            }
                         case GridNineItemType.Url:
                             break;
                    }
                }
            }
        });

        dialog_Confirm = new CustomDialogConfirm(SmHelpToolActivity.this, "", true);
        dialog_Confirm.getBtnSure().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();

                switch (tag) {
                    case "fun.closeapp":
                        break;
                    case "fun.rebootsys":
                        break;
                    case "fun.exitmanager":
                        Intent intent = new Intent(getAppContext(), InitDataActivity.class);
                        startActivity(intent);
                        AppManager.getAppManager().finishAllActivity();
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

    private void factorySetting(){
        Intent intent = new Intent(getAppContext(), SmFactorySettingActivity.class);
        startActivity(intent);

    }

    private void exitManager(){
        dialog_Confirm.getTipsImage().setVisibility(View.GONE);
        dialog_Confirm.getBtnSure().setTag("fun.exitmanager");
        dialog_Confirm.getTipsText().setText(getAppContext().getString(R.string.confrim_tips_exitmanager));
        dialog_Confirm.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog_Confirm != null) {
            dialog_Confirm.cancel();
        }
    }
}
