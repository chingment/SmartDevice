package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RetDeviceInitData;
import com.lumos.smartdevice.api.rop.RetOwnLogin;
import com.lumos.smartdevice.api.rop.RetOwnLogout;
import com.lumos.smartdevice.api.rop.RetUserGetList;
import com.lumos.smartdevice.api.rop.RetUserSave;
import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopLockerBoxDeleteBelongUser;
import com.lumos.smartdevice.api.rop.RopLockerBoxSaveBelongUser;
import com.lumos.smartdevice.api.rop.RopLockerBoxGetBelongUser;
import com.lumos.smartdevice.api.rop.RopOwnLoginByAccount;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.api.rop.RopUserGetList;
import com.lumos.smartdevice.api.rop.RopUserSave;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.PageDataBean;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.own.AppVar;
import com.lumos.smartdevice.utils.StringUtil;

public class ReqStandAlone implements IReqVersion{

    @Override
    public void deviceInitData(RopDeviceInitData rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        ResultBean result;

        RetDeviceInitData ret = new RetDeviceInitData();

        if (rop.getSceneMode().equals(AppVar.SCENE_MODE_1)) {

            DeviceBean device = new DeviceBean();
            device.setDeviceId("1224567");
            device.setSceneMode(rop.getSceneMode());
            device.setVersionMode(rop.getVesionMode());
            device.setCabinets(DbManager.getInstance().getCabinets());

            ret.setDevice(device);

            result = new ResultBean<>(ResultCode.SUCCESS, "获取成功", ret);

        } else {

            result = new ResultBean<>(ResultCode.FAILURE, "该场景模式未启用");
        }

        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void ownLoginByAccount(RopOwnLoginByAccount rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        ResultBean result;

        UserBean user=DbManager.getInstance().checkUserPassword(rop.getUserName(), rop.getPassword(), "2");

        if (user==null) {
            result = new ResultBean(ResultCode.FAILURE, "用户密码错误");
            reqHandler.sendSuccessMessage(result.toJSONString());
            return;
        }


        RetOwnLogin ret=new RetOwnLogin();
        ret.setUserId(user.getUserId());
        ret.setUserName(user.getUserName());
        ret.setFullName(user.getFullName());

        result = new ResultBean<>(ResultCode.SUCCESS, "登录成功",ret);

        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void ownLogout(RopOwnLogout rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        RetOwnLogout ret=new RetOwnLogout();

        ret.setUserId(rop.getUserId());

        ResultBean result = new ResultBean<>(ResultCode.SUCCESS, "退出成功",ret);

        reqHandler.sendSuccessMessage(result.toJSONString());

    }


    @Override
    public void userSave(RopUserSave rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        if(rop==null) {
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("参数不能空"));
            return;
        }

        if(StringUtil.isEmptyNotNull(rop.getUserName())){
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("用户名不能为空"));
            return;
        }

        if(StringUtil.isEmptyNotNull(rop.getUserId())) {
            if (StringUtil.isEmptyNotNull(rop.getPassword())) {
                reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("密码不能为空"));
                return;
            }
        }

        if(StringUtil.isEmptyNotNull(rop.getFullName())){
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("姓名不能为空"));
            return;
        }

        String userId=rop.getUserId().trim();
        String userName=rop.getUserName().trim();
        String password=rop.getPassword().trim();
        String fullName=rop.getFullName().trim();
        String avatar=rop.getAvatar();

        if(StringUtil.isEmptyNotNull(rop.getUserId())) {
            boolean userIsExist = DbManager.getInstance().checkUserIsExist(userName);
            if (userIsExist) {
                reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("用户名已经存在"));
                return;
            }
            DbManager.getInstance().addUser(userName,password,fullName, "3",avatar);
        }
        else {
            DbManager.getInstance().updateUser(userId, fullName, avatar);
        }


        RetUserSave ret = new RetUserSave();

        ResultBean result = new ResultBean<>(ResultCode.SUCCESS, "保存成功", ret);

        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void userGetList(RopUserGetList rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean result = null;
        RetUserGetList ret = new RetUserGetList();
        PageDataBean<UserBean> users = DbManager.getInstance().GetUsers(rop.getPageIndex(), rop.getPageSize(), "3",rop.getKeyWord());
        ret.setTotal(users.getTotal());
        ret.setPageSize(users.getPageSize());
        ret.setItems(users.getItems());
        result = new ResultBean<>(ResultCode.SUCCESS, "", ret);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void lockerBoxSaveBelongUser(RopLockerBoxSaveBelongUser rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();
        ResultBean result;
        long rows = DbManager.getInstance().savelockerBoxUser(rop.getCabinetId(), rop.getSlotId(),rop.getUserId());
        result = new ResultBean<>(ResultCode.SUCCESS, "");
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void lockerBoxGetBelongUser(RopLockerBoxGetBelongUser rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean result;
        UserBean user = DbManager.getInstance().getLockerBoxUser(rop.getCabinetId(), rop.getSlotId());
        result = new ResultBean<>(ResultCode.SUCCESS, "", user);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void lockerBoxDeleteBelongUser(RopLockerBoxDeleteBelongUser rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean result;
        long rows = DbManager.getInstance().deleteLockBoxUser(rop.getCabinetId(), rop.getSlotId(),rop.getUserId());
        result = new ResultBean<>(ResultCode.SUCCESS, "");
        reqHandler.sendSuccessMessage(result.toJSONString());
    }
}
