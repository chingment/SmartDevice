package com.lumos.smartdevice.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultUtil;
import com.lumos.smartdevice.db.dao.CabinetDao;
import com.lumos.smartdevice.db.dao.ConfigDao;
import com.lumos.smartdevice.db.dao.LockerBoxDao;
import com.lumos.smartdevice.db.dao.LockerBoxUsageDao;
import com.lumos.smartdevice.db.dao.LockerBoxUseRecordDao;
import com.lumos.smartdevice.db.dao.TripMsgDao;
import com.lumos.smartdevice.db.dao.UserDao;
import com.lumos.smartdevice.api.vo.CabinetVo;
import com.lumos.smartdevice.api.vo.CabinetLayoutVo;
import com.lumos.smartdevice.api.vo.LockerBoxVo;
import com.lumos.smartdevice.api.vo.LockerBoxUsageVo;
import com.lumos.smartdevice.api.vo.LockerBoxUseRecordVo;
import com.lumos.smartdevice.api.vo.PageDataBean;
import com.lumos.smartdevice.api.vo.TripMsgBean;
import com.lumos.smartdevice.api.vo.UserVo;
import com.lumos.smartdevice.app.AppContext;
import com.lumos.smartdevice.app.AppVar;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class DbManager {
    private final static String TAG = "DbManager";

    static private DbManager dbMgr = new DbManager();
    private final DbOpenHelper dbHelper;

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

    private void addConfig(String field, String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!db.isOpen())
            return;


        Cursor cursor = db.rawQuery("select * from   " + ConfigDao.TABLE_NAME + "  where   field=? ", new String[]{field});

        if (cursor.getCount() > 0) {
            cursor.close();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ConfigDao.COLUMN_NAME_FIELD, field);
        values.put(ConfigDao.COLUMN_NAME_VALUE, value);

        db.insert(ConfigDao.TABLE_NAME, null, values);

    }


    public ResultBean<Object> addUser(String username, String password, String fullname, String type, String avatar) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            return ResultUtil.isFailure("数据库文件未打开");
        }

        Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_USERNAME + " = ?", new String[]{username});

        boolean exist = (cursor.getCount() > 0);
        if (exist) {
            cursor.close();
            return ResultUtil.isFailure("用户名已存在");
        }

        ContentValues ct_User = new ContentValues();
        ct_User.put(UserDao.COLUMN_NAME_USERNAME, username);
        ct_User.put(UserDao.COLUMN_NAME_PASSWORD, password);
        ct_User.put(UserDao.COLUMN_NAME_FULLNAME, fullname);
        ct_User.put(UserDao.COLUMN_NAME_AVATAR, avatar);
        ct_User.put(UserDao.COLUMN_NAME_TYPE, type);

        long rows = db.insert(UserDao.TABLE_NAME, null, ct_User);

        if(rows>0){
            return ResultUtil.isSuccess("保存成功");
        }
        else{
            return ResultUtil.isFailure("保存失败");
        }

    }

    public ResultBean<Object> updateUser(String userid, String password, String fullname, String avatar) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!db.isOpen()) {
            return ResultUtil.isFailure("数据库文件未打开");
        }


        ContentValues ct_User = new ContentValues();
        ct_User.put(UserDao.COLUMN_NAME_FULLNAME, fullname);
        ct_User.put(UserDao.COLUMN_NAME_AVATAR, avatar);

        if (!StringUtil.isEmptyNotNull(password)) {
            ct_User.put(UserDao.COLUMN_NAME_PASSWORD, password);
        }


        long rows = db.update(UserDao.TABLE_NAME, ct_User, UserDao.COLUMN_NAME_USERID + " = ?", new String[]{String.valueOf(userid)});

        if(rows>0){
            return ResultUtil.isSuccess("保存成功");
        }
        else{
            return ResultUtil.isFailure("保存失败");
        }

    }

    public UserVo checkUserPassword(String username, String password, String type) {

        UserVo user = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_USERNAME + " = ? and " + UserDao.COLUMN_NAME_PASSWORD + " = ? and " + UserDao.COLUMN_NAME_TYPE + " = ?", new String[]{username, password, type});

        boolean exist = (cursor.getCount() > 0);
        if (exist) {

            while (cursor.moveToNext()) {

                user = new UserVo();

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

        cursor.close();

        return exist;

    }

    public PageDataBean<UserVo> GetUsers(int pageIndex, int pageSize, String userType, String keyWord) {

        PageDataBean<UserVo> pageData = new PageDataBean<>();

        pageData.setPageSize(pageSize);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<UserVo> users = new ArrayList<>();
        if (db.isOpen()) {

            String sql = "SELECT {0} FROM " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_TYPE + " = " + "'" + userType + "' ";


            if (!StringUtil.isEmptyNotNull(keyWord)) {
                sql += " and ( " + UserDao.COLUMN_NAME_USERNAME + " like '%" + keyWord + "%' ";
                sql += " or  " + UserDao.COLUMN_NAME_FULLNAME + " like '%" + keyWord + "%' ) ";
            }

            String sqlQuery = sql.replace("{0}", "*") + " limit " + (pageIndex * pageSize) + "," + pageSize;
            String sqlCount = sql.replace("{0}", "count(*)");
            Cursor cursor1 = db.rawQuery(sqlCount, null);

            cursor1.moveToFirst();

            int total = cursor1.getInt(0);

            cursor1.close();

            pageData.setTotalSize(total);

            Cursor cursor = db.rawQuery(sqlQuery, null);

            while (cursor.moveToNext()) {
                UserVo user = new UserVo();
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

    public UserVo GetUser(String userId) {

        UserVo user = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (db.isOpen()) {

            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_USERID + " = ?", new String[]{String.valueOf(userId)});

            while (cursor.moveToNext()) {
                user = new UserVo();

                String userName = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USERNAME));
                String fullName = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_FULLNAME));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));

                user.setUserId(userId);
                user.setUserName(userName);
                user.setFullName(fullName);
                user.setAvatar(avatar);
            }
            cursor.close();
        }

        return user;
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

    public String getConfigStringValue(String field) {
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

    public int getConfigIntValue(String field){
        try {
            String v = getConfigStringValue(field);
            if (StringUtil.isEmptyNotNull(field))
                return 0;
            return Integer.parseInt(v);
        }
        catch (Exception ex)
        {
            return  0;
        }
    }


    public HashMap<String, CabinetVo> getCabinets() {

        HashMap<String, CabinetVo> cabinets = new HashMap<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + CabinetDao.TABLE_NAME + "", null);
            while (cursor.moveToNext()) {

                CabinetVo cabinet = new CabinetVo();

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

    public ResultBean<Object> saveAppScene(int appVesionMode, int appSceneMode, String json_cabinets) {


        DbManager.getInstance().updateConfig(ConfigDao.FIELD_SCENE_MODE,String.valueOf(appSceneMode));
        DbManager.getInstance().updateConfig(ConfigDao.FIELD_VERSION_MODE,String.valueOf(appVesionMode));

        if (appSceneMode==AppVar.SCENE_MODE_1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            if (db.isOpen()) {

                Cursor cursor = db.rawQuery("select * from " + LockerBoxDao.TABLE_NAME + " where " + LockerBoxDao.COLUMN_NAME_IS_USED + " = ?", new String[]{"1"});
                boolean isHasUsed = (cursor.getCount() > 0);

                if (isHasUsed) {
                    cursor.close();
                    return ResultUtil.isFailure("保存失败，有柜子正在使用中");
                }

                cursor = db.rawQuery("select * from " + LockerBoxUsageDao.TABLE_NAME, null);

                isHasUsed = (cursor.getCount() > 0);

                if (isHasUsed) {
                    cursor.close();
                    return ResultUtil.isFailure("保存失败，有柜子正在使用中");
                }

                List<CabinetVo> cabinets = JsonUtil.toObject(json_cabinets,new TypeReference<List<CabinetVo> >() {});

                if (cabinets == null || cabinets.size() <= 0) {
                    return ResultUtil.isFailure("保存失败，解释串口协议不成功");
                }

                db.delete(LockerBoxDao.TABLE_NAME, null, null);
                db.delete(CabinetDao.TABLE_NAME, null, null);

                for (CabinetVo cabinet : cabinets) {

                    ContentValues cv_Cabinet = new ContentValues();
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_CABINET_ID, cabinet.getCabinetId());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_NAME, cabinet.getName());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_COM_ID, cabinet.getComId());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_COM_BAUD, cabinet.getComBaud());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_COM_PRL, cabinet.getComPrl());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_LAYOUT, cabinet.getLayout());

                    db.insert(CabinetDao.TABLE_NAME, null, cv_Cabinet);


                    CabinetLayoutVo layout = JsonUtil.toObject(cabinet.getLayout(),new TypeReference<CabinetLayoutVo>() {});

                    if (layout == null)
                        return ResultUtil.isFailure("保存失败，解释布局协议失败");

                    if (layout.getSpanCount() == 0)
                        return ResultUtil.isFailure("保存失败，解释布局协议失败,SpanCount不能为0");

                    if (layout.getCells() == null || layout.getCells().size() == 0)
                        return ResultUtil.isFailure("保存失败，解释布局协议失败,Cells不能为空");


                    List<String> cells = layout.getCells();
                    for (int j = 0; j < cells.size(); j++) {
                        String slotId = cells.get(j);
                        String[] cell_Prams = cells.get(j).split("-");
                        String type = cell_Prams[3];
                        ContentValues cv_LockerBox = new ContentValues();
                        cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_CABINET_ID, cabinet.getCabinetId());
                        cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_SLOT_ID, slotId);
                        cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_IS_USED, 0);
                        cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_TYPE, type);
                        cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_HEIGHT,140);
                        cv_LockerBox.put(LockerBoxDao.COLUMN_NAME_WIDTH,280);
                        db.insert(LockerBoxDao.TABLE_NAME, null, cv_LockerBox);
                    }
                }

                return ResultUtil.isSuccess("保存成功");
            }
        }
        else if(appSceneMode==AppVar.SCENE_MODE_2){


            SQLiteDatabase db = dbHelper.getReadableDatabase();
            if (db.isOpen()) {

                List<CabinetVo> cabinets = JsonUtil.toObject(json_cabinets,new TypeReference< List<CabinetVo>>() {});

                if (cabinets == null || cabinets.size() <= 0) {
                    return ResultUtil.isFailure("保存失败，解释串口协议不成功");
                }

                db.delete(CabinetDao.TABLE_NAME, null, null);

                for (CabinetVo cabinet : cabinets) {

                    ContentValues cv_Cabinet = new ContentValues();
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_CABINET_ID, cabinet.getCabinetId());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_NAME, cabinet.getName());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_COM_ID, cabinet.getComId());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_COM_BAUD, cabinet.getComBaud());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_COM_PRL, cabinet.getComPrl());
                    cv_Cabinet.put(CabinetDao.COLUMN_NAME_LAYOUT, cabinet.getLayout());

                    db.insert(CabinetDao.TABLE_NAME, null, cv_Cabinet);


                    CabinetLayoutVo layout = JsonUtil.toObject(cabinet.getLayout(),new TypeReference<CabinetLayoutVo>() {});

                    if (layout == null)
                        return ResultUtil.isFailure("保存失败，解释布局协议失败");

                    if (layout.getSpanCount() == 0)
                        return ResultUtil.isFailure("保存失败，解释布局协议失败,SpanCount不能为0");

                    if (layout.getCells() == null || layout.getCells().size() == 0)
                        return ResultUtil.isFailure("保存失败，解释布局协议失败,Cells不能为空");
                }

                return ResultUtil.isSuccess("保存成功");
            }



            return ResultUtil.isSuccess("保存成功");
        }

        return ResultUtil.isFailure("保存失败，未提供支持");

    }

    public synchronized void saveTripMsg(String msg_id, String post_url, String content) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(TripMsgDao.COLUMN_NAME_MSG_ID, msg_id);
            values.put(TripMsgDao.COLUMN_NAME_POST_URL, post_url);
            values.put(TripMsgDao.COLUMN_NAME_CONTENT, content);
            values.put(TripMsgDao.COLUMN_NAME_STATUS, 0);
            values.put(TripMsgDao.COLUMN_NAME_CREATETIME, System.currentTimeMillis());

            db.insert(TripMsgDao.TABLE_NAME, null, values);

        }
    }

    synchronized public List<TripMsgBean> getTripMsgs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TripMsgBean> msgs = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TripMsgDao.TABLE_NAME, null);
            while (cursor.moveToNext()) {
                TripMsgBean msg = new TripMsgBean();
                String msgId = cursor.getString(cursor.getColumnIndex(TripMsgDao.COLUMN_NAME_MSG_ID));
                String content = cursor.getString(cursor.getColumnIndex(TripMsgDao.COLUMN_NAME_CONTENT));
                int status = cursor.getInt(cursor.getColumnIndex(TripMsgDao.COLUMN_NAME_STATUS));
                String postUrl = cursor.getString(cursor.getColumnIndex(TripMsgDao.COLUMN_NAME_POST_URL));
                long createTime = cursor.getLong(cursor.getColumnIndex(TripMsgDao.COLUMN_NAME_CREATETIME));
                msg.setMsgId(msgId);
                msg.setContent(content);
                msg.setStatus(status);
                msg.setPostUrl(postUrl);
                msg.setCreateTime(createTime);
                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    synchronized public void deleteTripMsg(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(TripMsgDao.TABLE_NAME, TripMsgDao.COLUMN_NAME_MSG_ID + " = ?", new String[]{String.valueOf(id)});
        }
    }

    public ResultBean<Object> saveLockerBoxUsage(String cabinetId, String slotId, String usageType, String usageData) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {

            Cursor cursor = db.rawQuery("select * from " + LockerBoxUsageDao.TABLE_NAME + " where " + LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + "=? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + "=? and " + LockerBoxUsageDao.COLUMN_NAME_USAGE_TYPE + "=? and " + LockerBoxUsageDao.COLUMN_NAME_USAGE_DATA + "=?", new String[]{cabinetId, slotId, usageType, usageData});
            boolean exist = (cursor.getCount() > 0);
            cursor.close();
            if (exist) {
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

    public ResultBean<Object> deleteLockBoxUsage(String cabinetId, String slotId, String usageType, String usageData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {

            long rows = db.delete(LockerBoxUsageDao.TABLE_NAME, LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_USAGE_TYPE + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_USAGE_DATA + " = ?", new String[]{cabinetId, slotId, usageType, usageData});
            if(rows<0) {
                return ResultUtil.isFailure("删除失败，记录找不到");
            }

            Cursor cursor = db.rawQuery("select * from " + LockerBoxUsageDao.TABLE_NAME + " where " + LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + "=? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + "=?", new String[]{cabinetId, slotId});

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

    public LockerBoxVo getLockerBox(String cabinetId, String slotId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + LockerBoxDao.TABLE_NAME + " where " + LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + " = ? ", new String[]{cabinetId, slotId});
        boolean exist = (cursor.getCount() > 0);
        if(!exist){
            cursor.close();
            return null;
        }

        LockerBoxVo lockerBox=new LockerBoxVo();

        while (cursor.moveToNext()) {

            String l_CabinetId = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_CABINET_ID));
            String l_SlotId = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_SLOT_ID));
            int l_IsUsed = cursor.getInt(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_IS_USED));
            int l_Type = cursor.getInt(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_TYPE));
            int l_Width = cursor.getInt(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_WIDTH));
            int l_Height = cursor.getInt(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_HEIGHT));

            lockerBox.setCabinetId(l_CabinetId);
            lockerBox.setSlotId(l_SlotId);
            lockerBox.setUsed(l_IsUsed != 0);
            lockerBox.setType(l_Type);
            lockerBox.setWidth(l_Width);
            lockerBox.setHeight(l_Height);
        }


        List<LockerBoxUsageVo> usages = new ArrayList<>();

        cursor = db.rawQuery("select * from " + LockerBoxUsageDao.TABLE_NAME + " where " + LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + " = ? and " + LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + " = ? ", new String[]{cabinetId, slotId});
        exist = (cursor.getCount() > 0);
        if (exist) {

            while (cursor.moveToNext()) {

                String usageType = cursor.getString(cursor.getColumnIndex(LockerBoxUsageDao.COLUMN_NAME_USAGE_TYPE));
                String usageData = cursor.getString(cursor.getColumnIndex(LockerBoxUsageDao.COLUMN_NAME_USAGE_DATA));

                LockerBoxUsageVo usage = new LockerBoxUsageVo();

                usage.setCabinetId(cabinetId);
                usage.setSlotId(slotId);
                usage.setUsageType(usageType);
                usage.setUsageData(usageData);


                if (usageType.equals("1")) {

                    Cursor cursor1 = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where " + UserDao.COLUMN_NAME_USERID + " = ? ", new String[]{usageData});
                    exist = (cursor1.getCount() > 0);
                    if (exist) {

                        while (cursor1.moveToNext()) {

                            UserVo user = new UserVo();

                            String avatar = cursor1.getString(cursor1.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                            String user_name = cursor1.getString(cursor1.getColumnIndex(UserDao.COLUMN_NAME_USERNAME));
                            String fullname = cursor1.getString(cursor1.getColumnIndex(UserDao.COLUMN_NAME_FULLNAME));

                            user.setUserId(usageData);
                            user.setAvatar(avatar);
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

    public List<LockerBoxVo>  getLockerBoxs(String cabinetId) {

        List<LockerBoxVo> lockerBoxs = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if(db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + LockerBoxDao.TABLE_NAME + " where " + LockerBoxDao.COLUMN_NAME_CABINET_ID + " = ? ", new String[]{cabinetId});

            while (cursor.moveToNext()) {


                String slotId = cursor.getString(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_SLOT_ID));
                int isUsed = cursor.getInt(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_IS_USED));
                int type = cursor.getInt(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_TYPE));
                int width = cursor.getInt(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(LockerBoxDao.COLUMN_NAME_HEIGHT));

                LockerBoxVo lockerBox = new LockerBoxVo();

                lockerBox.setCabinetId(cabinetId);
                lockerBox.setSlotId(slotId);
                lockerBox.setUsed(isUsed != 0);
                lockerBox.setType(type);
                lockerBox.setHeight(height);
                lockerBox.setWidth(width);

                lockerBoxs.add(lockerBox);
            }

            cursor.close();
        }

        return lockerBoxs;

    }


    public ResultBean<Object> saveLockBoxUseRecord(String cabinetId, String slotId, String use_action,int use_result,String use_remark) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(LockerBoxUseRecordDao.COLUMN_NAME_CABINET_ID, cabinetId);
            values.put(LockerBoxUseRecordDao.COLUMN_NAME_SLOT_ID, slotId);
            values.put(LockerBoxUseRecordDao.COLUMN_NAME_USE_ACTION, use_action);
            values.put(LockerBoxUseRecordDao.COLUMN_NAME_USE_RESULT, use_result);
            values.put(LockerBoxUseRecordDao.COLUMN_NAME_USE_REMARK, use_remark);

            long datetime =System.currentTimeMillis();



            values.put(LockerBoxUseRecordDao.COLUMN_NAME_USE_TIME, datetime);

            db.insert(LockerBoxUseRecordDao.TABLE_NAME, null, values);
        }

        return ResultUtil.isSuccess("保存成功");
    }


    public PageDataBean<LockerBoxUseRecordVo> GetLockBoxUseRecords(int pageIndex, int pageSize) {

        PageDataBean<LockerBoxUseRecordVo> pageData = new PageDataBean<>();

        pageData.setPageSize(pageSize);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<LockerBoxUseRecordVo> records = new ArrayList<>();
        if (db.isOpen()) {

            String sql = "SELECT {0} FROM " + LockerBoxUseRecordDao.TABLE_NAME + " ";



            String sqlQuery = sql.replace("{0}", "*") + " order by use_time desc limit " + (pageIndex * pageSize) + "," + pageSize;
            String sqlCount = sql.replace("{0}", "count(*)");
            Cursor cursor1 = db.rawQuery(sqlCount, null);

            cursor1.moveToFirst();

            int total = cursor1.getInt(0);

            cursor1.close();

            pageData.setTotalSize(total);

            Cursor cursor = db.rawQuery(sqlQuery, null);

            while (cursor.moveToNext()) {
                LockerBoxUseRecordVo record = new LockerBoxUseRecordVo();
                String recordId = cursor.getString(cursor.getColumnIndex(LockerBoxUseRecordDao.COLUMN_NAME_RECORD_ID));
                String cabinetId = cursor.getString(cursor.getColumnIndex(LockerBoxUseRecordDao.COLUMN_NAME_CABINET_ID));
                String slotId = cursor.getString(cursor.getColumnIndex(LockerBoxUseRecordDao.COLUMN_NAME_SLOT_ID));
                String useAction = cursor.getString(cursor.getColumnIndex(LockerBoxUseRecordDao.COLUMN_NAME_USE_ACTION));
                int useResult = cursor.getInt(cursor.getColumnIndex(LockerBoxUseRecordDao.COLUMN_NAME_USE_RESULT));
                String useRemark = cursor.getString(cursor.getColumnIndex(LockerBoxUseRecordDao.COLUMN_NAME_USE_REMARK));
                long useTime = cursor.getLong(cursor.getColumnIndex(LockerBoxUseRecordDao.COLUMN_NAME_USE_TIME));
                record.setRecordId(recordId);
                record.setCabinetId(cabinetId);
                record.setSlotId(slotId);
                record.setUseAction(useAction);
                record.setUseResult(useResult);
                record.setUseRemark(useRemark);



                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(useTime);
                String str_useTime = simpleDateFormat.format(date);


                record.setUseTime(str_useTime);

                records.add(record);
            }
            cursor.close();
        }

        pageData.setItems(records);

        return pageData;

    }


}
