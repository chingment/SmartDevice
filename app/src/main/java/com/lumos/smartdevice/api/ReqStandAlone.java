package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RetBookerTakeStock;
import com.lumos.smartdevice.api.rop.RetDeviceInitData;
import com.lumos.smartdevice.api.rop.RetLockerGetBox;
import com.lumos.smartdevice.api.rop.RetLockerGetBoxUseRecords;
import com.lumos.smartdevice.api.rop.RetLockerGetCabinet;
import com.lumos.smartdevice.api.rop.RetOwnGetInfo;
import com.lumos.smartdevice.api.rop.RetOwnLogin;
import com.lumos.smartdevice.api.rop.RetOwnLogout;
import com.lumos.smartdevice.api.rop.RetOwnSaveInfo;
import com.lumos.smartdevice.api.rop.RetUserGetDetail;
import com.lumos.smartdevice.api.rop.RetUserGetList;
import com.lumos.smartdevice.api.rop.RetUserSave;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerDisplayBooks;
import com.lumos.smartdevice.api.rop.RopBookerRenewBooks;
import com.lumos.smartdevice.api.rop.RopBookerSawBorrowBooks;
import com.lumos.smartdevice.api.rop.RopBookerStockInbound;
import com.lumos.smartdevice.api.rop.RopBookerStockSlots;
import com.lumos.smartdevice.api.rop.RopBookerTakeStock;
import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopIdentityInfo;
import com.lumos.smartdevice.api.rop.RopIdentityVerify;
import com.lumos.smartdevice.api.rop.RopLockerDeleteBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBoxUseRecords;
import com.lumos.smartdevice.api.rop.RopLockerGetCabinet;
import com.lumos.smartdevice.api.rop.RopLockerSaveBoxOpenResult;
import com.lumos.smartdevice.api.rop.RopLockerSaveBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBox;
import com.lumos.smartdevice.api.rop.RopOwnGetInfo;
import com.lumos.smartdevice.api.rop.RopOwnLoginByAccount;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.api.rop.RopOwnSaveInfo;
import com.lumos.smartdevice.api.rop.RopUserGetDetail;
import com.lumos.smartdevice.api.rop.RopUserGetList;
import com.lumos.smartdevice.api.rop.RopUserSave;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.db.dao.ConfigDao;
import com.lumos.smartdevice.api.vo.CabinetVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.LockerBoxVo;
import com.lumos.smartdevice.api.vo.LockerBoxUseRecordVo;
import com.lumos.smartdevice.api.vo.PageDataBean;
import com.lumos.smartdevice.api.vo.UserVo;
import com.lumos.smartdevice.app.AppVar;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.List;

public class ReqStandAlone implements IReqVersion{

    @Override
    public void deviceInitData(RopDeviceInitData rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        ResultBean<RetDeviceInitData> result;

        RetDeviceInitData ret = new RetDeviceInitData();

        int version_mode=DbManager.getInstance().getConfigIntValue(ConfigDao.FIELD_VERSION_MODE);
        int scene_mode= DbManager.getInstance().getConfigIntValue(ConfigDao.FIELD_SCENE_MODE);
        if (scene_mode==AppVar.SCENE_MODE_1||scene_mode==AppVar.SCENE_MODE_2) {

            DeviceVo device = new DeviceVo();
            device.setDeviceId("1224567");
            device.setSceneMode(scene_mode);
            device.setVersionMode(version_mode);
           // device.setCabinets(DbManager.getInstance().getCabinets());

            ret.setDevice(device);

            result = new ResultBean<>(ResultCode.SUCCESS, "????????????", ret);

        } else {

            result = new ResultBean<>(ResultCode.FAILURE, "????????????????????????");
        }

        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void ownLoginByAccount(RopOwnLoginByAccount rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        ResultBean<RetOwnLogin> result;

        UserVo user=DbManager.getInstance().checkUserPassword(rop.getUserName(), rop.getPassword(), "2");

        if (user==null) {
            result = new ResultBean<>(ResultCode.FAILURE, "??????????????????");
            reqHandler.sendSuccessMessage(result.toJSONString());
            return;
        }


        RetOwnLogin ret=new RetOwnLogin();
        ret.setUserId(user.getUserId());
        ret.setUserName(user.getUserName());
        ret.setFullName(user.getFullName());
        ret.setAvatar(user.getAvatar());
        result = new ResultBean<>(ResultCode.SUCCESS, "????????????",ret);
        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void ownLogout(RopOwnLogout rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        RetOwnLogout ret=new RetOwnLogout();
        ret.setUserId(rop.getUserId());
        ResultBean<RetOwnLogout> result = new ResultBean<>(ResultCode.SUCCESS, "????????????",ret);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void ownGetInfo(RopOwnGetInfo rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean<RetOwnGetInfo> result = null;
        RetOwnGetInfo ret = new RetOwnGetInfo();
        UserVo user = DbManager.getInstance().GetUser(rop.getUserId());
        ret.setUserId(user.getUserId());
        ret.setFullName(user.getFullName());
        ret.setUserName(user.getUserName());
        ret.setAvatar(user.getAvatar());
        result = new ResultBean<>(ResultCode.SUCCESS, "", ret);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void ownSaveInfo(RopOwnSaveInfo rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();

        if (rop == null) {
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("???????????????"));
            return;
        }

        if (StringUtil.isEmptyNotNull(rop.getUserName())) {
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("?????????????????????"));
            return;
        }

        if (StringUtil.isEmptyNotNull(rop.getUserId())) {
            if (StringUtil.isEmptyNotNull(rop.getPassword())) {
                reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("??????????????????"));
                return;
            }
        }

        if (StringUtil.isEmptyNotNull(rop.getFullName())) {
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("??????????????????"));
            return;
        }

        String userId = rop.getUserId() == null ? null : rop.getUserId().trim();
        String userName = rop.getUserName().trim();
        String password = rop.getPassword().trim();
        String fullName = rop.getFullName().trim();
        String avatar = rop.getAvatar();

        ResultBean<Object> result;

        if (StringUtil.isEmptyNotNull(userId)) {
            boolean userIsExist = DbManager.getInstance().checkUserIsExist(userName);
            if (userIsExist) {
                reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("?????????????????????"));
                return;
            }
            result = DbManager.getInstance().addUser(userName, password, fullName, "3", avatar);
        } else {
            result = DbManager.getInstance().updateUser(userId, password, fullName, avatar);
        }


        RetOwnSaveInfo ret = new RetOwnSaveInfo();
        ret.setUserName(userName);
        ret.setFullName(fullName);
        ret.setAvatar(avatar);
        result.setData(ret);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }


    @Override
    public void userSave(RopUserSave rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        if (rop == null) {
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("???????????????"));
            return;
        }

        if (StringUtil.isEmptyNotNull(rop.getUserName())) {
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("?????????????????????"));
            return;
        }

        if (StringUtil.isEmptyNotNull(rop.getUserId())) {
            if (StringUtil.isEmptyNotNull(rop.getPassword())) {
                reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("??????????????????"));
                return;
            }
        }

        if (StringUtil.isEmptyNotNull(rop.getFullName())) {
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("??????????????????"));
            return;
        }

        String userId = rop.getUserId() == null ? null : rop.getUserId().trim();
        String userName = rop.getUserName().trim();
        String password = rop.getPassword().trim();
        String fullName = rop.getFullName().trim();
        String avatar = rop.getAvatar();

        ResultBean<Object> result;

        if (StringUtil.isEmptyNotNull(userId)) {
            boolean userIsExist = DbManager.getInstance().checkUserIsExist(userName);
            if (userIsExist) {
                reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("?????????????????????"));
                return;
            }
            result = DbManager.getInstance().addUser(userName, password, fullName, "3", avatar);
        } else {
            result = DbManager.getInstance().updateUser(userId, password, fullName, avatar);
        }


        RetUserSave ret = new RetUserSave();
        ret.setUserName(userName);
        ret.setFullName(fullName);
        ret.setAvatar(avatar);
        result.setData(ret);
        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void userGetList(RopUserGetList rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean<RetUserGetList> result = null;
        RetUserGetList ret = new RetUserGetList();
        PageDataBean<UserVo> users = DbManager.getInstance().GetUsers(rop.getPageIndex(), rop.getPageSize(), "3",rop.getKeyWord());
        ret.setTotalSize(users.getTotalSize());
        ret.setPageSize(users.getPageSize());
        ret.setItems(users.getItems());
        result = new ResultBean<>(ResultCode.SUCCESS, "", ret);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void userGetDetail(RopUserGetDetail rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean<RetUserGetDetail> result = null;
        RetUserGetDetail ret = new RetUserGetDetail();
        UserVo user = DbManager.getInstance().GetUser(rop.getUserId());
        ret.setUserId(user.getUserId());
        ret.setFullName(user.getFullName());
        ret.setUserName(user.getUserName());
        ret.setAvatar(user.getAvatar());
        result = new ResultBean<>(ResultCode.SUCCESS, "", ret);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void lockerGetCabinet(RopLockerGetCabinet rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean<RetLockerGetCabinet> result;

        CabinetVo cabinet=DbManager.getInstance().getCabinets().get(rop.getCabinetId());

        List<LockerBoxVo> boxs = DbManager.getInstance().getLockerBoxs(rop.getCabinetId());
        RetLockerGetCabinet ret=new RetLockerGetCabinet();
        ret.setCabinetId(cabinet.getCabinetId());
        ret.setName(cabinet.getName());
        ret.setComBaud(cabinet.getComBaud());
        ret.setComId(cabinet.getComId());
        ret.setComPrl(cabinet.getComPrl());
        ret.setLayout(cabinet.getLayout());
        ret.setBoxs(boxs);
        result = new ResultBean<>(ResultCode.SUCCESS, "",ret);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void lockerGetBox(RopLockerGetBox rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean<RetLockerGetBox> result;
        LockerBoxVo lockerBox = DbManager.getInstance().getLockerBox(rop.getCabinetId(), rop.getSlotId());
        RetLockerGetBox ret=new RetLockerGetBox();
        ret.setDeviceId(lockerBox.getDeviceId());
        ret.setCabinetId(lockerBox.getCabinetId());
        ret.setUsed(lockerBox.isUsed());
        ret.setType(lockerBox.getType());
        ret.setWidth(lockerBox.getWidth());
        ret.setHeight(lockerBox.getHeight());
        ret.setUsages(lockerBox.getUsages());
        result = new ResultBean<>(ResultCode.SUCCESS, "", ret);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void lockerSaveBoxUsage(RopLockerSaveBoxUsage rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean<Object> result = DbManager.getInstance().saveLockerBoxUsage(rop.getCabinetId(), rop.getSlotId(),rop.getUsageType(),rop.getUsageData());
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void lockerDeleteBoxUsage(RopLockerDeleteBoxUsage rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean<Object> result = DbManager.getInstance().deleteLockBoxUsage(rop.getCabinetId(), rop.getSlotId(),rop.getUsageType(),rop.getUsageData());
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void lockerGetBoxUseRecords(RopLockerGetBoxUseRecords rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();
        ResultBean<RetLockerGetBoxUseRecords> result = null;
        RetLockerGetBoxUseRecords ret = new RetLockerGetBoxUseRecords();
        PageDataBean<LockerBoxUseRecordVo> users = DbManager.getInstance().GetLockBoxUseRecords(rop.getPageIndex(), rop.getPageSize());
        ret.setTotalSize(users.getTotalSize());
        ret.setPageSize(users.getPageSize());
        ret.setItems(users.getItems());
        result = new ResultBean<>(ResultCode.SUCCESS, "", ret);
        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void lockerSaveBoxOpenResult(RopLockerSaveBoxOpenResult rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean<Object> result = DbManager.getInstance().saveLockBoxUseRecord(rop.getCabinetId(), rop.getSlotId(),rop.getAction(),rop.getResult(),rop.getRemark());
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

    @Override
    public void identityInfo(RopIdentityInfo rop, final ReqHandler reqHandler) {

    }

    @Override
    public void identityVerify(RopIdentityVerify rop, final ReqHandler reqHandler) {


    }

    @Override
    public ResultBean<RetBookerCreateFlow>  bookerCreateFlow(RopBookerCreateFlow rop) {

        return null;

    }

    @Override
    public ResultBean<RetBookerBorrowReturn>  bookerBorrowReturn(RopBookerBorrowReturn rop) {


        return  null;
    }

    @Override
    public void bookerSawBorrowBooks(RopBookerSawBorrowBooks rop, final ReqHandler reqHandler) {

    }

    @Override
    public void bookerRenewBooks(RopBookerRenewBooks rop, final ReqHandler reqHandler) {

    }

    @Override
    public void bookerDisplayBooks(RopBookerDisplayBooks rop, final ReqHandler reqHandler) {

    }

    @Override
    public ResultBean<RetBookerTakeStock> bookerTakeStock(RopBookerTakeStock rop){

        return null;
    }

    @Override
    public void bookerStockStocks(RopBookerStockSlots rop, final ReqHandler reqHandler) {

    }

    @Override
    public void bookerStockInbound(RopBookerStockInbound rop, final ReqHandler reqHandler) {

    }

}
