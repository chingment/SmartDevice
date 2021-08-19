package com.lumos.smartdevice.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.ResultUtil;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.LockerBoxBean;
import com.lumos.smartdevice.model.LockerBoxUsageBean;
import com.lumos.smartdevice.model.PageDataBean;
import com.lumos.smartdevice.model.TripMsgBean;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.own.AppContext;
import com.lumos.smartdevice.own.AppVar;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DbManager {
    static private DbManager dbMgr = new DbManager();
    private DbOpenHelper dbHelper;

    private DbManager() {
        dbHelper = DbOpenHelper.getInstance(AppContext.getInstance().getApplicationContext());
    }

    public static synchronized DbManager getInstance() {
        if (dbMgr == null) {
            dbMgr = new DbManager();
        }
        return dbMgr;
    }

    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        dbMgr = null;
    }

    public void init() {
        initConfig();
        initUser();
    }

    private void initConfig() {
        addConfig(ConfigDao.FIELD_VERSION_MODE, "0");
        addConfig(ConfigDao.FIELD_SCENE_MODE, "0");
    }

    private void initUser() {
        addUser("system", "123456", "系统维护员", "1", "app://default_avatar");
        addUser("admin", "123456", "后台管理员", "2", "app://default_avatar");
    }

    public void addConfig(String field, String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen()) {

            Cursor cursor = db.rawQuery(
                    "select * from   " + ConfigDao.TABLE_NAME + "  where   field=? ",
                    new String[]{field});
            while (cursor.moveToNext()) {
                db.close();
                return;
            }

            ContentValues values = new ContentValues();
            values.put(ConfigDao.COLUMN_NAME_FIELD, field);
            values.put(ConfigDao.COLUMN_NAME_VALUE, value);

            db.insert(ConfigDao.TABLE_NAME, null, values);

        }
    }

    public long addUser(String username, String password, String fullname, String type, String avatar) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rows = 0;
        if (db.isOpen()) {

            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_USERNAME + " = ?", new String[]{username});

            boolean exist = (cursor.getCount() > 0);
            if (exist) {
                cursor.close();
                return rows;
            }

            ContentValues values = new ContentValues();
            values.put(UserDao.COLUMN_NAME_USERNAME, username);
            values.put(UserDao.COLUMN_NAME_PASSWORD, password);
            values.put(UserDao.COLUMN_NAME_FULLNAME, fullname);
            values.put(UserDao.COLUMN_NAME_AVATAR, avatar);
            values.put(UserDao.COLUMN_NAME_TYPE, type);

            rows = db.insert(UserDao.TABLE_NAME, null, values);

            cursor.close();
        }

        return rows;
    }

    public int updateUser(String userid, String password, String fullname, String avatar) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = 0;
        if (db.isOpen()) {

            ContentValues values = new ContentValues();
            values.put(UserDao.COLUMN_NAME_FULLNAME, fullname);
            values.put(UserDao.COLUMN_NAME_AVATAR, avatar);

            if (!StringUtil.isEmptyNotNull(password)) {
                values.put(UserDao.COLUMN_NAME_PASSWORD, password);
            }


            rows = db.update(UserDao.TABLE_NAME, values, UserDao.COLUMN_NAME_USERID + " = ?", new String[]{String.valueOf(userid)});

        }

        return rows;
    }

    public UserBean checkUserPassword(String username, String password, String type) {

        UserBean user = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_USERNAME + " = ? and " + UserDao.COLUMN_NAME_PASSWORD + " = ? and " + UserDao.COLUMN_NAME_TYPE + " = ?", new String[]{username, password, type});

        boolean exist = (cursor.getCount() > 0);
        if (exist) {

            while (cursor.moveToNext()) {

                user = new UserBean();

                String user_id = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USERID));
                String user_name = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USERNAME));
                String fullname = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_FULLNAME));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));

                user.setUserId(user_id);
                user.setUserName(user_name);
                user.setFullName(fullname);
                user.setAvatar(avatar);

            }
            cursor.close();

        }

        return user;

    }

    public boolean checkUserIsExist(String username) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_USERNAME + " = ?", new String[]{username});

        boolean exist = (cursor.getCount() > 0);
        if (exist) {
            return true;
        }

        return false;

    }

    public PageDataBean<UserBean> GetUsers(int pageIndex, int pageSize, String userType, String keyWord) {

        PageDataBean<UserBean> pageData = new PageDataBean<UserBean>();

        pageData.setPageSize(pageSize);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<UserBean> users = new ArrayList<>();
        if (db.isOpen()) {

            String sql = "SELECT {0} FROM " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_TYPE + " = " + "'" + userType + "' ";


            if (!StringUtil.isEmptyNotNull(keyWord)) {
                sql += " and ( " + UserDao.COLUMN_NAME_USERNAME + " like '%" + keyWord + "%' ";
                sql += " or  " + UserDao.COLUMN_NAME_FULLNAME + " like '%" + keyWord + "%' ) ";
            }

            String sqlQuery = sql.replace("{0}", "*") + " limit " + String.valueOf(pageIndex * pageSize) + "," + pageSize;
            String sqlCount = sql.replace("{0}", "count(*)");
            Cursor cursor1 = db.rawQuery(sqlCount, null);

            cursor1.moveToFirst();

            int total = cursor1.getInt(0);

            cursor1.close();

            pageData.setTotal(total);

            Cursor cursor = db.rawQuery(sqlQuery, null);

            while (cursor.moveToNext()) {
                UserBean user = new UserBean();
                String userId = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USERID));
                String userName = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USERNAME));
                String fullName = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_FULLNAME));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));

                user.setUserId(userId);
                user.setUserName(userName);
                user.setFullName(fullName);
                user.setAvatar(avatar);
                users.add(user);
            }
            cursor.close();
        }

        pageData.setItems(users);

        return pageData;

    }

    public void updateConfig(String field, String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(ConfigDao.COLUMN_NAME_FIELD, field);
            values.put(ConfigDao.COLUMN_NAME_VALUE, value);
            db.update(ConfigDao.TABLE_NAME, values, ConfigDao.COLUMN_NAME_FIELD + " = ?", new String[]{String.valueOf(field)});
        }
    }

    public String getConfigValue(String field) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String val = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + ConfigDao.TABLE_NAME + " where " + ConfigDao.COLUMN_NAME_FIELD + " = ?", new String[]{String.valueOf(field)});
            while (cursor.moveToNext()) {
                val = cursor.getString(cursor.getColumnIndex(ConfigDao.COLUMN_NAME_VALUE));
            }
            cursor.close();
        }
        return val;
    }

    public HashMap<String, CabinetBean> getCabinets() {

        HashMap<String, CabinetBean> cabinets = new HashMap<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + CabinetDao.TABLE_NAME + "", null);
            while (cursor.moveToNext()) {

                CabinetBean cabinet = new CabinetBean();

                String cabinet_id = cursor.getString(cursor.getColumnIndex(CabinetDao.COLUMN_NAME_CABINET_ID));
                String name = cursor.getString(cursor.getColumnIndex(CabinetDao.COLUMN_NAME_NAME));
                String com_id = cursor.getString(cursor.getColumnIndex(CabinetDao.COLUMN_NAME_COM_ID));
                int com_baud = cursor.getInt(cursor.getColumnIndex(CabinetDao.COLUMN_NAME_COM_BAUD));
                String com_prl = cursor.getString(cursor.getColumnIndex(CabinetDao.COLUMN_NAME_COM_PRL));
                String layout = cursor.getString(cursor.getColumnIndex(CabinetDao.COLUMN_NAME_LAYOUT));

                cabinet.setCabinetId(cabinet_id);
                cabinet.setName(name);
                cabinet.setComId(com_id);
                cabinet.setComBaud(com_baud);
                cabinet.setComPrl(com_prl);
                cabinet.setLayout(layout);

                if (!cabinets.containsKey(cabinet_id)) {
                    cabinets.put(cabinet_id, cabinet);
                }
            }
            cursor.close();
        }
        return cabinets;
    }

    public ResultBean saveAppScene(String appVesionMode, String appSceneMode, String comPrl) {

        DbManager.getInstance().updateConfig(ConfigDao.FIELD_SCENE_MODE, appSceneMode);
        DbManager.getInstance().updateConfig(ConfigDao.FIELD_VERSION_MODE, appVesionMode);

        if (appSceneMode.equals(AppVar.SCENE_MODE_1)) {

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            if (db.isOpen()) {

                Cursor cursor = db.rawQuery("select * from " + LockerBoxDao.TABLE_NAME + " where " + LockerBoxDao.COLUMN_NAME_IS_USED + " = ?", new String[]{"1"});
                boolean isHasUsed = (cursor.getCount() > 0);

                if (isHasUsed) {
                    cursor.close();
                    return ResultUtil.isFailure("有柜子正在使用中");
                }

                cursor = db.rawQuery("select * from " + LockerBoxUsageDao.TABLE_NAME, null);

                isHasUsed = (cursor.getCount() > 0);

                if (isHasUsed) {
                    cursor.close();
                    return ResultUtil.isFailure("有柜子正在使用中");
                }

                List<CabinetBean> cabinets = JSON.parseObject(comPrl, new TypeReference<List<CabinetBean>>() {
                });

                if (cabinets == null || cabinets.size() <= 0) {
                    return ResultUtil.isFailure("解释串口协议失败");
                }

                db.delete(LockerBoxDao.TABLE_NAME, null, null);
                db.delete(CabinetDao.TABLE_NAME, null, null);

                for (CabinetBean cabinet : cabinets) {

                    ContentValues cv_Cabinet = new ContentValues();
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_CABINET_ID, cabinet.getCabinetId());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_NAME, cabinet.getName());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_COM_ID, cabinet.getComId());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_COM_BAUD, cabinet.getComBaud());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_COM_PRL, cabinet.getComPrl());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_LAYOUT, cabinet.getLayout());

                    db.insert(CabinetDao.TABLE_NAME, null, cv_Cabinet);


                    List<List<String>> layout = JSON.parseObject(cabinet.getLayout(), new TypeReference<List<List<String>>>() {
                    });

                    if (layout == null || layout.size() <= 0)
                        return ResultUtil.isFailure("解释布局协议失败");

                    int rowsSize = layout.size();
                    for (int i = 0; i < rowsSize; i++) {
                        List<String> cols = layout.get(i);
                        int colsSize = cols.size();
                        for (int j = 0; j < colsSize; j++) {
                            String slotId = cols.get(j);
                            String[] col_Prams = cols.get(j).split("-");
                            String isUse = col_Prams[3];
                            if (isUse.equals("0")) {
                                ContentValues cv_LockerBox = new ContentValues();
                                cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_CABINET_ID, cabinet.getCabinetId());
                                cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_SLOT_ID, slotId);
                                cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_IS_USED, "0");
                                cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_USAGE_TYPE, "");
                                db.insert(LockerBoxDao.TABLE_NAME, null, cv_LockerBox);
                            }
                        }
                    }

                }

                return ResultUtil.isSuccess("保存成功");
            }
        }

        return ResultUtil.isFailure("保存失败，未提供支持");

    }

    public synchronized Integer saveTripMsg(String post_url, String content) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(TripMsgDao.COLUMN_NAME_POST_URL, post_url);
            values.put(TripMsgDao.COLUMN_NAME_CONTENT, content);
            values.put(TripMsgDao.COLUMN_NAME_STATUS, 0);
            db.insert(TripMsgDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + TripMsgDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    synchronized public List<TripMsgBean> getTripMsgs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TripMsgBean> msgs = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TripMsgDao.TABLE_NAME, null);
            while (cursor.moveToNext()) {
                TripMsgBean msg = new TripMsgBean();
                int msgId = cursor.getInt(cursor.getColumnIndex(TripMsgDao.COLUMN_NAME_MSG_ID));
                String content = cursor.getString(cursor.getColumnIndex(TripMsgDao.COLUMN_NAME_CONTENT));
                int status = cursor.getInt(cursor.getColumnIndex(TripMsgDao.COLUMN_NAME_STATUS));
                String postUrl = cursor.getString(cursor.getColumnIndex(TripMsgDao.COLUMN_NAME_POST_URL));

                msg.setMsgId(msgId);
                msg.setContent(content);
                msg.setStatus(status);
                msg.setPostUrl(postUrl);
                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    synchronized public void deleteTripMsg(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(TripMsgDao.TABLE_NAME, TripMsgDao.COLUMN_NAME_MSG_ID + " = ?", new String[]{String.valueOf(id)});
        }
    }

    public ResultBean savelockerBoxUsage(String cabinetId, String slotId, String usageType, String usageData) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {

            Cursor cursor = db.rawQuery("select * from " + LockerBoxUsageDao.TABLE_NAME + " where " + LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + "=? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + "=? and " + LockerBoxUsageDao.COLUMN_NAME_USAGE_TYPE + "=? and " + LockerBoxUsageDao.COLUMN_NAME_USAGE_DATA + "=?", new String[]{cabinetId, slotId, usageType, usageData});
            boolean exist = (cursor.getCount() > 0);
            cursor.close();
            if (exist) {
                db.endTransaction();
                return ResultUtil.isFailure("保存失败，已存在");
            }

            ContentValues ct_LockerBoxUsage = new ContentValues();
            ct_LockerBoxUsage.put(LockerBoxUsageDao.COLUMN_NAME_CABINET_ID, cabinetId);
            ct_LockerBoxUsage.put(LockerBoxUsageDao.COLUMN_NAME_SLOT_ID, slotId);
            ct_LockerBoxUsage.put(LockerBoxUsageDao.COLUMN_NAME_USAGE_TYPE, usageType);
            ct_LockerBoxUsage.put(LockerBoxUsageDao.COLUMN_NAME_USAGE_DATA, usageData);

            long rows = db.insert(LockerBoxUsageDao.TABLE_NAME, null, ct_LockerBoxUsage);
            if(rows<0) {
                return ResultUtil.isFailure("保存失败，记录增加不成功");
            }

            ContentValues ct_LockerBox = new ContentValues();
            ct_LockerBox.put(LockerBoxDao.COLUMN_NAME_IS_USED, "1");
            ct_LockerBox.put(LockerBoxDao.COLUMN_NAME_USAGE_TYPE, usageType);

            rows=db.update(LockerBoxDao.TABLE_NAME, ct_LockerBox, LockerBoxDao.COLUMN_NAME_CABINET_ID + " = ? and " + LockerBoxDao.COLUMN_NAME_SLOT_ID + " = ?", new String[]{cabinetId, slotId});

            if(rows<0) {
                return ResultUtil.isFailure("保存失败，修改记录不成功");
            }

            db.setTransactionSuccessful();

            return ResultUtil.isSuccess("保存成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResultUtil.isException("保存发生异常:"+ex.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public ResultBean deleteLockBoxUsage(String cabinetId, String slotId, String usageType, String usageData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {

            long rows = db.delete(LockerBoxUsageDao.TABLE_NAME, LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_USAGE_TYPE + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_USAGE_DATA + " = ?", new String[]{cabinetId, slotId, usageType, usageData});
            if(rows<0) {
                return ResultUtil.isFailure("删除失败，记录找不到");
            }

            Cursor cursor = db.rawQuery("select * from " + LockerBoxUsageDao.TABLE_NAME + " where " + LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + "=? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + "=? and " + LockerBoxUsageDao.COLUMN_NAME_USAGE_TYPE + "=? ", new String[]{cabinetId, slotId, usageType});

            int count = cursor.getCount();

            cursor.close();

            String is_Used = "0";
            if (count > 0) {
                is_Used = "1";
            }

            ContentValues ct_LockerBox = new ContentValues();
            ct_LockerBox.put(LockerBoxDao.COLUMN_NAME_IS_USED, is_Used);

            rows = db.update(LockerBoxDao.TABLE_NAME, ct_LockerBox, LockerBoxDao.COLUMN_NAME_CABINET_ID + " = ? and " + LockerBoxDao.COLUMN_NAME_SLOT_ID + " = ?", new String[]{cabinetId, slotId});
            if(rows<0) {
                return ResultUtil.isFailure("删除失败，更新不成功");
            }

            db.setTransactionSuccessful();

            return ResultUtil.isSuccess("删除成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResultUtil.isException("删除发生异常:"+ex.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public LockerBoxBean getLockerBox(String cabinetId, String slotId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + LockerBoxDao.TABLE_NAME + " where " + LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + " = ? ", new String[]{cabinetId, slotId});
        boolean exist = (cursor.getCount() > 0);
        if(!exist){
            cursor.close();
            return null;
        }

        LockerBoxBean lockerBox=new LockerBoxBean();

        while (cursor.moveToNext()) {

            String l_CabinetId = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_CABINET_ID));
            String l_SlotId = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_SLOT_ID));
            String l_IsUsed = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_IS_USED));
            String l_UsageType = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_USAGE_TYPE));

            lockerBox.setCabinetId(l_CabinetId);
            lockerBox.setSlotId(l_SlotId);
            lockerBox.setIsUsed(l_IsUsed);
            lockerBox.setUsageType(l_UsageType);
        }


        List<LockerBoxUsageBean> usages = new ArrayList<>();

        cursor = db.rawQuery("select * from " + LockerBoxUsageDao.TABLE_NAME + " where " + LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + " = ? ", new String[]{cabinetId, slotId});
        exist = (cursor.getCount() > 0);
        if (exist) {

            while (cursor.moveToNext()) {

                String usageType = cursor.getString(cursor.getColumnIndex(LockerBoxUsageDao.COLUMN_NAME_USAGE_TYPE));
                String usageData = cursor.getString(cursor.getColumnIndex(LockerBoxUsageDao.COLUMN_NAME_USAGE_DATA));

                LockerBoxUsageBean usage = new LockerBoxUsageBean();

                usage.setCabinetId(cabinetId);
                usage.setSlotId(slotId);
                usage.setUsageType(usageType);
                usage.setUsageData(usageData);


                if (usageType.equals("1")) {

                    Cursor cursor1 = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_USERID + " = ? ", new String[]{usageData});
                    exist = (cursor1.getCount() > 0);
                    if (exist) {

                        while (cursor1.moveToNext()) {

                            UserBean user = new UserBean();

                            String user_name = cursor1.getString(cursor1.getColumnIndex(UserDao.COLUMN_NAME_USERNAME));
                            String fullname = cursor1.getString(cursor1.getColumnIndex(UserDao.COLUMN_NAME_FULLNAME));

                            user.setUserId(usageData);
                            user.setUserName(user_name);
                            user.setFullName(fullname);
                            usage.setCustomData(JSON.toJSONString(user));
                        }

                        cursor1.close();
                    }
                }

                usages.add(usage);

            }
            cursor.close();

        }

        lockerBox.setUsages(usages);

        return lockerBox;
    }

    public HashMap<String, LockerBoxBean>  getLockerBoxs(String cabinetId) {

        HashMap<String, LockerBoxBean> lockerBoxs = new HashMap<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if(db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + LockerBoxDao.TABLE_NAME + " where " + LockerBoxDao.COLUMN_NAME_CABINET_ID + " = ? ", new String[]{cabinetId});

            while (cursor.moveToNext()) {


                String slotId = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_SLOT_ID));
                String isUsed = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_IS_USED));
                String usageType = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_USAGE_TYPE));

                LockerBoxBean lockerBox = new LockerBoxBean();

                lockerBox.setCabinetId(cabinetId);
                lockerBox.setSlotId(slotId);
                lockerBox.setUsageType(usageType);
                lockerBox.setIsUsed(isUsed);


                if (!lockerBoxs.containsKey(slotId)) {
                    lockerBoxs.put(slotId, lockerBox);
                }
            }

            cursor.close();
        }

        return lockerBoxs;

    }


}
