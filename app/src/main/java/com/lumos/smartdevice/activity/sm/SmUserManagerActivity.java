package com.lumos.smartdevice.activity.sm;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.adapter.SmUserAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetUserGetList;
import com.lumos.smartdevice.api.rop.RetUserSave;
import com.lumos.smartdevice.api.rop.RopLockerBoxSaveBelongUser;
import com.lumos.smartdevice.api.rop.RopUserGetList;
import com.lumos.smartdevice.api.rop.RopUserSave;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.dialog.CustomDialogUserEdit;
import com.lumos.smartdevice.ui.refreshview.OnRefreshHandler;
import com.lumos.smartdevice.ui.refreshview.SuperRefreshLayout;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmUserManagerActivity extends BaseFragmentActivity {
    private static final String TAG = "SmUserManagerActivity";
    private ImageView btn_NewUser;
    private CustomDialogUserEdit dialog_UserEdit;
    private SuperRefreshLayout lv_UsersRefresh;
    private RecyclerView lv_UsersData;
    private int lv_Users_PageIndex=0;
    private LinearLayout ll_UsersEmpty;
    private SmUserAdapter lv_UsersAdapter;
    private EditText et_Search;
    private int scene_mode=1;//页面场景

    private Map<String,String> scene_param=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smusermanager);

        setNavHeaderBtnByGoBackIsVisible(true);

        scene_mode = getIntent().getIntExtra("scene_mode", 1);

        if(scene_mode==1){
            setNavHeaderTtile(R.string.aty_smusermanager_nav_title);
        }
        else if(scene_mode==2) {
            scene_param = (HashMap<String, String>) getIntent().getSerializableExtra("scene_param");
            setNavHeaderTtile(R.string.aty_smusermanager_nav_title_select_user);
        }

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_NewUser = findViewById(R.id.btn_NewUser);
        lv_UsersRefresh =  findViewById(R.id.lv_UsersRefresh);
        lv_UsersData = findViewById(R.id.lv_UsersData);
        ll_UsersEmpty= findViewById(R.id.ll_UsersEmpty);
        et_Search= findViewById(R.id.et_Search);
        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String key = charSequence.toString();
                LogUtil.d("key:"+key);

                getUsers();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog_UserEdit=new CustomDialogUserEdit(SmUserManagerActivity.this);
        dialog_UserEdit.setOnClickListener(new CustomDialogUserEdit.OnClickListener() {
            @Override
            public void onSave(UserBean bean) {


                if (StringUtil.isEmptyNotNull(bean.getUserName())) {
                    showToast(R.string.tips_username_isnotnull);
                    return;
                }

                if (StringUtil.isEmptyNotNull(bean.getUserId())) {
                    if (StringUtil.isEmptyNotNull(bean.getPassword())) {
                        showToast(R.string.tips_password_isnotnull);
                        return;
                    }
                }

                if(StringUtil.isEmptyNotNull(bean.getFullName())) {
                    showToast(R.string.tips_fullname_isnotnull);
                    return;
                }


                RopUserSave rop=new RopUserSave();
                rop.setUserId(bean.getUserId());
                rop.setUserName(bean.getUserName());
                rop.setPassword(bean.getPassword());
                rop.setFullName(bean.getFullName());
                rop.setAvatar(bean.getAvatar());

                ReqInterface.getInstance().userSave(rop, new ReqHandler(){

                    @Override
                    public void onBeforeSend() {
                        super.onBeforeSend();
                        showLoading(SmUserManagerActivity.this);
                    }

                    @Override
                    public void onAfterSend() {
                        super.onAfterSend();
                        hideLoading(SmUserManagerActivity.this);
                    }

                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<RetUserSave> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetUserSave>>() {
                        });

                        showToast(rt.getMsg());

                        if(rt.getCode()== ResultCode.SUCCESS) {
                            lv_Users_PageIndex = 0;
                            getUsers();
                            dialog_UserEdit.hide();
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                    }
                });

            }
        });


        lv_UsersData.setLayoutManager(new GridLayoutManager(getAppContext(), 1));

        lv_UsersData.setItemAnimator(new DefaultItemAnimator());


        lv_UsersAdapter = new SmUserAdapter(scene_mode);

        lv_UsersAdapter.setOnClickListener(new SmUserAdapter.OnClickListener() {
            @Override
            public void onItemClick(UserBean bean) {
                dialog_UserEdit.setData(bean);
                dialog_UserEdit.show();
            }

            @Override
            public void onSelectClick(UserBean bean) {

                RopLockerBoxSaveBelongUser rop=new RopLockerBoxSaveBelongUser();
                rop.setDeviceId(scene_param.get("device_id"));
                rop.setCabinetId(scene_param.get("cabinet_id"));
                rop.setSlotId(scene_param.get("slot_id"));
                rop.setUserId(bean.getUserId());
                ReqInterface.getInstance().lockerBoxSaveBelongUser(rop, new ReqHandler(){

                    @Override
                    public void onBeforeSend() {
                        super.onBeforeSend();
                    }

                    @Override
                    public void onAfterSend() {
                        super.onAfterSend();
                    }

                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);

                        ResultBean<Object> rt = JSON.parseObject(response, new TypeReference<ResultBean<Object>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                    }
                });

            }
        });

        lv_UsersRefresh.setAdapter(lv_UsersData, lv_UsersAdapter);
        lv_UsersRefresh.setOnRefreshHandler(new OnRefreshHandler() {
            @Override
            public void refresh() {
                lv_UsersRefresh.setRefreshing(true);
                lv_Users_PageIndex = 0;
                getUsers();
            }

            @Override
            public void loadMore() {
                super.loadMore();
                lv_Users_PageIndex++;
                getUsers();
            }
        });

        lv_Users_PageIndex = 0;
        getUsers();
    }

    private void initEvent() {
        btn_NewUser.setOnClickListener(this);
    }

    private void initData() {

    }

    private void getUsers(){

        RopUserGetList rop=new RopUserGetList();
        rop.setPageIndex(lv_Users_PageIndex);
        rop.setPageSize(10);
        rop.setKeyWord(et_Search.getText().toString());
        ReqInterface.getInstance().userGetList(rop, new ReqHandler(){

            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(SmUserManagerActivity.this);
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(SmUserManagerActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<RetUserGetList> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetUserGetList>>() {
                });

                if(rt.getCode()== ResultCode.SUCCESS) {
                    RetUserGetList d=rt.getData();

                    List<UserBean> users = d.getItems();

                    if(d.getTotal()==0){
                        ll_UsersEmpty.setVisibility(View.VISIBLE);
                    }
                    else {
                        ll_UsersEmpty.setVisibility(View.GONE);
                    }



                    if (users != null && users.size() > 0) {
                        if (lv_Users_PageIndex == 0) {
                            lv_UsersRefresh.setRefreshing(false);
                            lv_UsersRefresh.loadComplete(true);
                            lv_UsersAdapter.setData(users, SmUserManagerActivity.this);

                        } else {
                            lv_UsersRefresh.loadComplete(true);
                            lv_UsersAdapter.addData(users, SmUserManagerActivity.this);
                        }
                        lv_UsersRefresh.setVisibility(View.VISIBLE);
                    } else {
                        if (lv_Users_PageIndex == 0) {
                            lv_UsersRefresh.setRefreshing(false);
                            lv_UsersAdapter.setData(new ArrayList<UserBean>(), SmUserManagerActivity.this);
                            lv_UsersRefresh.setVisibility(View.GONE);
                        }

                        lv_UsersRefresh.loadComplete(false);
                    }
                }
                else {
                    showToast(rt.getMsg());
                }
            }

            @Override
            public void onFailure(String msg, Exception e) {
                super.onFailure(msg, e);
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog_UserEdit != null) {
            dialog_UserEdit.cancel();
        }


    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            switch (v.getId()) {
                case R.id.btn_Nav_Header_Goback:
                    finish();
                    break;
                case  R.id.btn_NewUser:
                    dialog_UserEdit.setData(null);
                    dialog_UserEdit.show();
                    break;
            }
        }
    }
}
