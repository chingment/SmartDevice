package com.lumos.smartdevice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.lumos.smartdevice.db.dao.CabinetDao;
import com.lumos.smartdevice.db.dao.ConfigDao;
import com.lumos.smartdevice.db.dao.LockerBoxDao;
import com.lumos.smartdevice.db.dao.LockerBoxUsageDao;
import com.lumos.smartdevice.db.dao.LockerBoxUseRecordDao;
import com.lumos.smartdevice.db.dao.TripMsgDao;
import com.lumos.smartdevice.db.dao.UserDao;
import com.lumos.smartdevice.db.dao.UserUnlockKeyDao;

import java.io.File;


public class DbOpenHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "smartdevice.db";

	private static final int DATABASE_VERSION = 8;
	private static DbOpenHelper instance;

	private static final String TABLE_CREATE_CONFIG = "CREATE TABLE if not exists  "
			+ ConfigDao.TABLE_NAME + " ("
			+ ConfigDao.COLUMN_NAME_FIELD + " TEXT PRIMARY KEY , "
			+ ConfigDao.COLUMN_NAME_VALUE + " TEXT);";

	private static final String TABLE_CREATE_CABINET = "CREATE TABLE if not exists  "
			+ CabinetDao.TABLE_NAME + " ("
			+ CabinetDao.COLUMN_NAME_CABINET_ID + " TEXT PRIMARY KEY , "
			+ CabinetDao.COLUMN_NAME_NAME + " TEXT , "
			+ CabinetDao.COLUMN_NAME_COM_ID + " TEXT , "
			+ CabinetDao.COLUMN_NAME_COM_BAUD + " INTEGER , "
			+ CabinetDao.COLUMN_NAME_COM_PRL + " TEXT , "
			+ CabinetDao.COLUMN_NAME_LAYOUT + " TEXT);";

	private static final String TABLE_CREATE_USER = "CREATE TABLE if not exists  "
			+ UserDao.TABLE_NAME + " ("
			+ UserDao.COLUMN_NAME_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
			+ UserDao.COLUMN_NAME_USERNAME + " TEXT , "
			+ UserDao.COLUMN_NAME_PASSWORD + " TEXT , "
			+ UserDao.COLUMN_NAME_FULLNAME + " TEXT , "
			+ UserDao.COLUMN_NAME_AVATAR + " TEXT , "
			+ UserDao.COLUMN_NAME_TYPE + " TEXT );";


	private static final String TABLE_CREATE_USERUNLOCKKEY = "CREATE TABLE if not exists  "
			+ UserUnlockKeyDao.TABLE_NAME + " ("
			+ UserUnlockKeyDao.COLUMN_NAME_USERID + " INTEGER , "
			+ UserUnlockKeyDao.COLUMN_NAME_KEYTYPE + " TEXT , "
			+ UserUnlockKeyDao.COLUMN_NAME_KEYDATA + " TEXT );";


	private static final String TABLE_CREATE_TRIPMSG = "CREATE TABLE if not exists "
			+ TripMsgDao.TABLE_NAME + " ("
			+ TripMsgDao.COLUMN_NAME_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
			+ TripMsgDao.COLUMN_NAME_CONTENT + " TEXT , "
			+ TripMsgDao.COLUMN_NAME_POST_URL + " TEXT , "
			+ TripMsgDao.COLUMN_NAME_STATUS + " INTEGER );";


	private static final String TABLE_CREATE_LOCKERBOXUSAGE = "CREATE TABLE if not exists "
			+ LockerBoxUsageDao.TABLE_NAME + " ("
			+ LockerBoxUsageDao.COLUMN_NAME_CABINET_ID + " TEXT , "
			+ LockerBoxUsageDao.COLUMN_NAME_SLOT_ID + " TEXT , "
			+ LockerBoxUsageDao.COLUMN_NAME_USAGE_DATA + " TEXT , "
			+ LockerBoxUsageDao.COLUMN_NAME_USAGE_TYPE + " TEXT );";

	private static final String TABLE_CREATE_LOCKERBOX = "CREATE TABLE if not exists "
			+ LockerBoxDao.TABLE_NAME + " ("
			+ LockerBoxDao.COLUMN_NAME_CABINET_ID + " TEXT , "
			+ LockerBoxDao.COLUMN_NAME_SLOT_ID + " TEXT , "
			+ LockerBoxDao.COLUMN_NAME_TYPE + " INTEGER , "
			+ LockerBoxDao.COLUMN_NAME_HEIGHT + " INTEGER , "
			+ LockerBoxDao.COLUMN_NAME_WIDTH + " INTEGER , "
			+ LockerBoxDao.COLUMN_NAME_IS_USED + " INTEGER );";


	private static final String TABLE_CREATE_LOCKERBOXUSERECORD = "CREATE TABLE if not exists  "
			+ LockerBoxUseRecordDao.TABLE_NAME + " ("
			+ LockerBoxUseRecordDao.COLUMN_NAME_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
			+ LockerBoxUseRecordDao.COLUMN_NAME_CABINET_ID + " TEXT , "
			+ LockerBoxUseRecordDao.COLUMN_NAME_SLOT_ID + " TEXT , "
			+ LockerBoxUseRecordDao.COLUMN_NAME_USE_ACTION + " TEXT , "
			+ LockerBoxUseRecordDao.COLUMN_NAME_USE_RESULT + " INTEGER , "
			+ LockerBoxUseRecordDao.COLUMN_NAME_USE_REMARK + " TEXT , "
			+ LockerBoxUseRecordDao.COLUMN_NAME_USE_TIME + " INTEGER );";

	private DbOpenHelper(Context context) {
		super(context, getMyDatabaseName(context), null, DATABASE_VERSION);
	}

	private static String getMyDatabaseName(Context context) {
		String databasename = DB_NAME;
		boolean isSdcardEnable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {//SDCard是否插入
			isSdcardEnable = true;
		}
		String dbPath = null;
		if (isSdcardEnable) {
			dbPath = Environment.getExternalStorageDirectory().getPath() + "/database/";
		} else {//未插入SDCard，建在内存中
			dbPath = context.getFilesDir().getPath() + "/database/";
		}
		File dbp = new File(dbPath);
		if (!dbp.exists()) {
			dbp.mkdirs();
		}
		databasename = dbPath + DB_NAME;
		return databasename;
	}

	
	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_CONFIG);
		db.execSQL(TABLE_CREATE_CABINET);
		db.execSQL(TABLE_CREATE_USER);
		db.execSQL(TABLE_CREATE_USERUNLOCKKEY);
		db.execSQL(TABLE_CREATE_TRIPMSG);
		db.execSQL(TABLE_CREATE_LOCKERBOXUSAGE);
		db.execSQL(TABLE_CREATE_LOCKERBOX);
		db.execSQL(TABLE_CREATE_LOCKERBOXUSERECORD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


	}
	
	public void closeDB() {
	    if (instance != null) {
	        try {
	            SQLiteDatabase db = instance.getWritableDatabase();
	            db.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        instance = null;
	    }
	}
	
}
