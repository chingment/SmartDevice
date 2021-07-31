package com.lumos.smartdevice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 8;
	private static DbOpenHelper instance;

	private static final String CONFIG_TABLE_CREATE = "CREATE TABLE "
			+ ConfigDao.TABLE_NAME + " ("
			+ ConfigDao.COLUMN_NAME_FIELD + " TEXT PRIMARY KEY , "
			+ ConfigDao.COLUMN_NAME_VALUE + " TEXT);";

	private static final String CABINET_TABLE_CREATE = "CREATE TABLE "
			+ CabinetDao.TABLE_NAME + " ("
			+ CabinetDao.COLUMN_NAME_CABINET_ID + " TEXT PRIMARY KEY , "
			+ CabinetDao.COLUMN_NAME_NAME + " TEXT , "
			+ CabinetDao.COLUMN_NAME_COM_ID + " TEXT , "
			+ CabinetDao.COLUMN_NAME_COM_BAUD + " INTEGER , "
			+ CabinetDao.COLUMN_NAME_COM_PRL + " TEXT , "
			+ CabinetDao.COLUMN_NAME_LAYOUT + " TEXT);";

	private static final String USER_TABLE_CREATE = "CREATE TABLE "
			+ UserDao.TABLE_NAME + " ("
			+ UserDao.COLUMN_NAME_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
			+ UserDao.COLUMN_NAME_USERNAME + " TEXT , "
			+ UserDao.COLUMN_NAME_PASSWORD + " TEXT , "
			+ UserDao.COLUMN_NAME_FULLNAME + " TEXT , "
			+ UserDao.COLUMN_NAME_AVATAR + " TEXT , "
			+ UserDao.COLUMN_NAME_TYPE + " TEXT );";


	private static final String USERUNLOCKKEY_TABLE_CREATE = "CREATE TABLE "
			+ UserUnlockKeyDao.TABLE_NAME + " ("
			+ UserUnlockKeyDao.COLUMN_NAME_USERID + " INTEGER , "
			+ UserUnlockKeyDao.COLUMN_NAME_KEYTYPE + " TEXT , "
			+ UserUnlockKeyDao.COLUMN_NAME_KEYDATA + " TEXT );";


	private static final String TRIPMSG_TABLE_CREATE = "CREATE TABLE "
			+ TripMsgDao.TABLE_NAME + " ("
			+ TripMsgDao.COLUMN_NAME_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
			+ TripMsgDao.COLUMN_NAME_CONTENT + " TEXT , "
			+ TripMsgDao.COLUMN_NAME_POST_URL + " TEXT , "
			+ TripMsgDao.COLUMN_NAME_STATUS + " INTEGER );";


	private DbOpenHelper(Context context) {
		super(context, "smartdevice.db", null, DATABASE_VERSION);
	}
	
	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CONFIG_TABLE_CREATE);
		db.execSQL(CABINET_TABLE_CREATE);
		db.execSQL(USER_TABLE_CREATE);
		db.execSQL(USERUNLOCKKEY_TABLE_CREATE);
		db.execSQL(TRIPMSG_TABLE_CREATE);
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
