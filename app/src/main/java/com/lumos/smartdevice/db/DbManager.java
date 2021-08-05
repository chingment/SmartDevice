package com.lumos.smartdevice.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.PageDataBean;
import com.lumos.smartdevice.model.TripMsgBean;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.own.AppContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DbManager {
    static private DbManager dbMgr = new DbManager();
    private DbOpenHelper dbHelper;

    private DbManager(){
        dbHelper = DbOpenHelper.getInstance(AppContext.getInstance().getApplicationContext());
    }
    
    public static synchronized DbManager getInstance(){
        if(dbMgr == null){
            dbMgr = new DbManager();
        }
        return dbMgr;
    }

    synchronized public void closeDB(){
        if(dbHelper != null){
            dbHelper.closeDB();
        }
        dbMgr = null;
    }

    public void  init(){
        initConfig();
        initUser();
    }

    private void initConfig(){
        addConfig(ConfigDao.FIELD_VERSION_MODE,"0");
        addConfig(ConfigDao.FIELD_SCENE_MODE,"0");
    }

    private void initUser() {
        addUser("system", "123456", "系统维护员", "1", "app://default_avatar");
        addUser("admin", "123456", "后台管理员", "2", "app://default_avatar");
    }

    public void addConfig(String field,String value){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if(db.isOpen()){

            Cursor cursor = db.rawQuery(
                    "select * from   "+ConfigDao.TABLE_NAME+"  where   field=? ",
                    new String[] { field });
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

    public long addUser(String username,String password,String fullname, String type,String avatar) {
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

    public int updateUser(String userid,String fullname, String avatar) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = 0;
        if (db.isOpen()) {

            ContentValues values = new ContentValues();
            values.put(UserDao.COLUMN_NAME_FULLNAME, fullname);
            values.put(UserDao.COLUMN_NAME_AVATAR, avatar);

            rows = db.update(UserDao.TABLE_NAME, values, UserDao.COLUMN_NAME_USERID + " = ?", new String[]{String.valueOf(userid)});

        }

        return rows;
    }

    public UserBean checkUserPassword(String username, String password, String type) {

        UserBean user=null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where "+UserDao.COLUMN_NAME_USERNAME + " = ? and "+UserDao.COLUMN_NAME_PASSWORD+" = ? and "+UserDao.COLUMN_NAME_TYPE+" = ?",new String[]{username,password,type});

        boolean exist = (cursor.getCount() > 0);
        if(exist){

            while(cursor.moveToNext()) {

                user = new UserBean();

                String user_id = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USERID));
                String user_name = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USERNAME));
                String fullname = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_FULLNAME));


                user.setUserId(user_id);
                user.setUserName(user_name);
                user.setFullName(fullname);

            }
            cursor.close();

        }

        return user;

    }

    public boolean checkUserIsExist(String username) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where "+UserDao.COLUMN_NAME_USERNAME + " = ?",new String[]{username});

        boolean exist = (cursor.getCount() > 0);
        if(exist){
          return  true;
        }

        return false;

    }

    public PageDataBean<UserBean> GetUsers(int pageIndex, int pageSize, String userType){

        PageDataBean<UserBean> pageData=new PageDataBean<UserBean>();

        pageData.setPageSize(pageSize);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<UserBean> users = new ArrayList<>();
        if(db.isOpen()){

            Cursor cursor1 = db.rawQuery("select count(*) from " + UserDao.TABLE_NAME + "" +
                    " where "+UserDao.COLUMN_NAME_TYPE + " = ? ",new String[]{
                    String.valueOf(userType)
            });

            cursor1.moveToFirst();

            int total = cursor1.getInt(0);

            cursor1.close();

            pageData.setTotal(total);

            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + "" +
                    " where "+UserDao.COLUMN_NAME_TYPE + " = ? order by "+UserDao.COLUMN_NAME_USERID+" limit ?,?",new String[]{
                    String.valueOf(userType),
                    String.valueOf(pageIndex*pageSize),
                    String.valueOf(pageSize),
            });
            while(cursor.moveToNext()){
                UserBean user = new UserBean();
                String userId = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USERID));
                String userName = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USERNAME));
                String fullName = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_FULLNAME));
                String avatar=cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));

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

    public void updateConfig(String field,String value){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){

            ContentValues values = new ContentValues();
            values.put(ConfigDao.COLUMN_NAME_FIELD, field);
            values.put(ConfigDao.COLUMN_NAME_VALUE, value);

            db.update(ConfigDao.TABLE_NAME, values, ConfigDao.COLUMN_NAME_FIELD + " = ?", new String[]{String.valueOf(field)});
        }
    }

    public String getConfigValue(String field){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String val=null;
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + ConfigDao.TABLE_NAME + " where "+ConfigDao.COLUMN_NAME_FIELD + " = ?",new String[]{String.valueOf(field)});
            while(cursor.moveToNext()){


                val = cursor.getString(cursor.getColumnIndex(ConfigDao.COLUMN_NAME_VALUE));



            }
            cursor.close();
        }
        return val;
    }

    public HashMap<String, CabinetBean> getCabinets(){

        HashMap<String, CabinetBean>cabinets=new HashMap<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + CabinetDao.TABLE_NAME + "",null);
            while(cursor.moveToNext()) {

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

                if(!cabinets.containsKey(cabinet_id)) {
                    cabinets.put(cabinet_id, cabinet);
                }
            }
            cursor.close();
        }
        return cabinets;
    }

    public  void saveCabinet(CabinetBean cabinet){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + CabinetDao.TABLE_NAME + " where "+CabinetDao.COLUMN_NAME_CABINET_ID + " = ?",new String[]{String.valueOf(cabinet.getCabinetId())});

            boolean exist = (cursor.getCount() > 0);

            if(!exist){

                ContentValues values = new ContentValues();
                values.put(CabinetDao.COLUMN_NAME_CABINET_ID, cabinet.getCabinetId());
                values.put(CabinetDao.COLUMN_NAME_NAME, cabinet.getName());
                values.put(CabinetDao.COLUMN_NAME_COM_ID, cabinet.getComId());
                values.put(CabinetDao.COLUMN_NAME_COM_BAUD, cabinet.getComBaud());
                values.put(CabinetDao.COLUMN_NAME_COM_PRL, cabinet.getComPrl());
                values.put(CabinetDao.COLUMN_NAME_LAYOUT, cabinet.getLayout());

                db.insert(CabinetDao.TABLE_NAME, null, values);

            }
            else {


                ContentValues values = new ContentValues();
                values.put(CabinetDao.COLUMN_NAME_NAME, cabinet.getName());
                values.put(CabinetDao.COLUMN_NAME_COM_ID, cabinet.getComId());
                values.put(CabinetDao.COLUMN_NAME_COM_BAUD, cabinet.getComBaud());
                values.put(CabinetDao.COLUMN_NAME_COM_PRL, cabinet.getComPrl());
                values.put(CabinetDao.COLUMN_NAME_LAYOUT, cabinet.getLayout());

                db.update(CabinetDao.TABLE_NAME, values, CabinetDao.COLUMN_NAME_CABINET_ID + " = ?", new String[]{String.valueOf(cabinet.getCabinetId())});

            }

            cursor.close();
        }

    }

    public synchronized Integer saveTripMsg(String post_url, String content){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(TripMsgDao.COLUMN_NAME_POST_URL, post_url);
            values.put(TripMsgDao.COLUMN_NAME_CONTENT, content);
            values.put(TripMsgDao.COLUMN_NAME_STATUS, 0);
            db.insert(TripMsgDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + TripMsgDao.TABLE_NAME,null);
            if(cursor.moveToFirst()){
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    synchronized public List<TripMsgBean> getTripMsgs(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TripMsgBean> msgs = new ArrayList<>();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + TripMsgDao.TABLE_NAME,null);
            while(cursor.moveToNext()){
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

    synchronized public void deleteTripMsg(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(TripMsgDao.TABLE_NAME, TripMsgDao.COLUMN_NAME_MSG_ID + " = ?", new String[]{String.valueOf(id)});
        }
    }

    public long savelockerBoxUser(String cabinetId,String slotId, String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rows = 0;
        if (db.isOpen()) {

            Cursor cursor = db.rawQuery("select * from " + LockerBoxUserDao.TABLE_NAME + " where " + LockerBoxUserDao.COLUMN_NAME_CABINET_ID + "=? and " + LockerBoxUserDao.COLUMN_NAME_SLOT_ID + "=? and " + LockerBoxUserDao.COLUMN_NAME_USER_ID + "=?", new String[]{cabinetId, slotId, userId});

            boolean exist = (cursor.getCount() > 0);
            if (exist) {
                cursor.close();
                return rows;
            }

            ContentValues values = new ContentValues();
            values.put(LockerBoxUserDao.COLUMN_NAME_CABINET_ID, cabinetId);
            values.put(LockerBoxUserDao.COLUMN_NAME_SLOT_ID, slotId);
            values.put(LockerBoxUserDao.COLUMN_NAME_USER_ID, userId);

            rows = db.insert(LockerBoxUserDao.TABLE_NAME, null, values);

            cursor.close();
        }

        return rows;
    }

    public int deleteLockBoxUser(String cabinetId,String slotId,String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = 0;
        if (db.isOpen()) {

            rows = db.delete(LockerBoxUserDao.TABLE_NAME, LockerBoxUserDao.COLUMN_NAME_CABINET_ID + " = ? and " + LockerBoxUserDao.COLUMN_NAME_SLOT_ID + " = ? and " + LockerBoxUserDao.COLUMN_NAME_USER_ID + " = ? ", new String[]{cabinetId, slotId,userId});
        }

        return rows;
    }

    public UserBean getLockerBoxUser(String cabinetId,String slotId){

        UserBean user=null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + LockerBoxUserDao.TABLE_NAME + " where "+LockerBoxUserDao.COLUMN_NAME_CABINET_ID + " = ? and "+LockerBoxUserDao.COLUMN_NAME_SLOT_ID+" = ? ",new String[]{cabinetId,slotId});
        boolean exist = (cursor.getCount() > 0);
        if(exist){

            String user_id="";
            while(cursor.moveToNext()) {
                user_id = cursor.getString(cursor.getColumnIndex(LockerBoxUserDao.COLUMN_NAME_USER_ID));
            }
            cursor.close();

            Cursor cursor1 = db.rawQuery("select * from " + UserDao.TABLE_NAME + " where "+UserDao.COLUMN_NAME_USERID + " = ? ",new String[]{user_id});
            exist = (cursor1.getCount() > 0);
            if(exist){

                while(cursor1.moveToNext()) {

                    user = new UserBean();

                    String user_name = cursor1.getString(cursor1.getColumnIndex(UserDao.COLUMN_NAME_USERNAME));
                    String fullname = cursor1.getString(cursor1.getColumnIndex(UserDao.COLUMN_NAME_FULLNAME));

                    user.setUserId(user_id);
                    user.setUserName(user_name);
                    user.setFullName(fullname);
                }
                cursor1.close();
            }
        }
        return user;
    }
}
