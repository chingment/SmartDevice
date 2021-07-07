package com.lumos.smartdevice.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


import com.lumos.smartdevice.db.model.ConfigBean;
import com.lumos.smartdevice.model.CabinetBean;
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

    public  List<CabinetBean> getCabinets(){

        List<CabinetBean> cabinets=new ArrayList<>();

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

                cabinets.add(cabinet);
            }
            cursor.close();
        }
        return cabinets;
    }


    public  void addCabinet(CabinetBean cabinet){

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

            cursor.close();
        }

    }

}
