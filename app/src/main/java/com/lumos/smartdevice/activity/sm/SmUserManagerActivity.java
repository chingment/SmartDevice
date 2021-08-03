package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.adapter.SmUserAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RopUserGetList;
import com.lumos.smartdevice.api.rop.RopUserSave;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.model.api.UserGetListResultBean;
import com.lumos.smartdevice.model.api.UserSaveResultBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.dialog.CustomDialogUserEdit;
import com.lumos.smartdevice.ui.refreshview.OnRefreshHandler;
import com.lumos.smartdevice.ui.refreshview.SuperRefreshLayout;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmUserManagerActivity extends BaseFragmentActivity {

    private TextView btn_NewUser;
    private CustomDialogUserEdit dialog_UserEdit;
    private SuperRefreshLayout lv_UsersRefresh;
    private RecyclerView lv_UsersData;
    private int lv_Users_PageIndex=0;
    private LinearLayout lv_UsersEmptyTips;
    private SmUserAdapter lv_UsersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smusermanager);

        setNavHeaderTtile(R.string.aty_smusermanager_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_NewUser = findViewById(R.id.btn_NewUser);
        lv_UsersRefresh =  findViewById(R.id.lv_UsersRefresh);
        lv_UsersData = findViewById(R.id.lv_UsersData);
        lv_UsersEmptyTips= findViewById(R.id.lv_UsersEmptyTips);
        dialog_UserEdit=new CustomDialogUserEdit(SmUserManagerActivity.this);
        dialog_UserEdit.setOnClickListener(new CustomDialogUserEdit.OnClickListener() {
            @Override
            public void onSave(HashMap<String, String> form) {

                String userid = form.get("userid");
                String username = form.get("username");
                String password = form.get("password");
                String fullname = form.get("fullname");
                String avatar=form.get("avatar");
                if(StringUtil.isEmptyNotNull(username)) {
                    showToast(R.string.tips_username_isnotnull);
                    return;
                }

                if(StringUtil.isEmptyNotNull(password)) {
                    showToast(R.string.tips_password_isnotnull);
                    return;
                }

                if(StringUtil.isEmptyNotNull(fullname)) {
                    showToast(R.string.tips_fullname_isnotnull);
                    return;
                }


                RopUserSave rop=new RopUserSave();
                rop.setUserId(userid);
                rop.setUserName(username);
                rop.setPassword(password);
                rop.setFullName(fullname);
                rop.setAvatar(avatar);

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
                        ResultBean<UserSaveResultBean> rt = JSON.parseObject(response, new TypeReference<ResultBean<UserSaveResultBean>>() {
                        });

                        if(rt.getCode()== ResultCode.SUCCESS) {
                            UserSaveResultBean d=rt.getData();
                            getUsers();
                            dialog_UserEdit.hide();
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
        });


        lv_UsersData.setLayoutManager(new GridLayoutManager(getAppContext(), 1));

        lv_UsersData.setItemAnimator(new DefaultItemAnimator());

        lv_UsersAdapter = new SmUserAdapter();

        lv_UsersAdapter.setOnClickListener(new SmUserAdapter.OnClickListener() {
            @Override
            public void onClick(UserBean bean) {
                dialog_UserEdit.show();
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
                ResultBean<UserGetListResultBean> rt = JSON.parseObject(response, new TypeReference<ResultBean<UserGetListResultBean>>() {
                });

                if(rt.getCode()== ResultCode.SUCCESS) {
                    UserGetListResultBean d=rt.getData();

                    List<UserBean> users = d.getItems();
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
                        lv_UsersEmptyTips.setVisibility(View.GONE);
                    } else {
                        if (lv_Users_PageIndex == 0) {
                            lv_UsersRefresh.setRefreshing(false);
                            lv_UsersAdapter.setData(new ArrayList<UserBean>(), SmUserManagerActivity.this);

                            lv_UsersRefresh.setVisibility(View.GONE);
                            lv_UsersEmptyTips.setVisibility(View.VISIBLE);
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
                    dialog_UserEdit.show();
                    break;
            }
        }
    }
}
