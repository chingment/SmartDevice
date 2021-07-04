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

    public void initConfig(){
        addConfig("version_mode","1");
        addConfig("scene_mode","1");
    }

    public void addConfig(String field,String value){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if(db.isOpen()){
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


    public ConfigBean getConfig(String field){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ConfigBean msg=null;
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + ConfigDao.TABLE_NAME + " where "+ConfigDao.COLUMN_NAME_FIELD + " = ?",new String[]{String.valueOf(field)});
            while(cursor.moveToNext()){
                msg = new ConfigBean();

                String field1 = cursor.getString(cursor.getColumnIndex(ConfigDao.COLUMN_NAME_FIELD));
                String value1 = cursor.getString(cursor.getColumnIndex(ConfigDao.COLUMN_NAME_VALUE));

                msg.setField(field1);
                msg.setValue(value1);

            }
            cursor.close();
        }
        return msg;
    }
    

}
