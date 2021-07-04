package com.lumos.smartdevice.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


import com.lumos.smartdevice.db.model.ConfigBean;
import com.lumos.smartdevice.own.AppContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
    }

    private void initConfig(){
        addConfig(ConfigDao.FIELD_VERSION_MODE,"0");
        addConfig(ConfigDao.FIELD_SCENE_MODE,"0");
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
    

}
