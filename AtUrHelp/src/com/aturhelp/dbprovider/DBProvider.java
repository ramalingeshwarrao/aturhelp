package com.aturhelp.dbprovider;

import com.aturhelp.common.AtUrHelpInfo;
import com.aturhelp.common.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBProvider extends SQLiteOpenHelper {

	public DBProvider(Context context) {
		super(context, Constants.DATABASE_NAME, null, 1);
	}

	public DBProvider(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//is_register_loc the use of this field is in the location page we need to check whether register is happen or not.
		db.execSQL("create table aturhelpinfo "
				+ "(is_register text, is_admin text, cus_name text, email text, mobileno text, is_register_loc text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS aturhelpinfo");
		onCreate(db);
	}

	public boolean updateAtUrHelpInfoAdminStatus(AtUrHelpInfo helpInfo) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constants.ATURHELP_COL_IS_ADMIN, helpInfo.getIsAdmin());
		db.update(Constants.ATURHELP_TABLE_NAME, contentValues, null, null);
		return true;
	}
	
	public boolean updateAtUrHelpInfo(AtUrHelpInfo helpInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constants.ATURHELP_COL_IS_ADMIN, helpInfo.getIsAdmin());
		contentValues.put(Constants.ATURHELP_COL_IS_REGISTER, helpInfo.getIsRegister());
		contentValues.put(Constants.ATURHELP_COL_CUS_NAME, helpInfo.getCusName());
		contentValues.put(Constants.ATURHELP_COL_EMAIL, helpInfo.getEmail());
		contentValues.put(Constants.ATURHELP_COL_MOBILE_NUMBER, helpInfo.getMobileNo());
		db.update(Constants.ATURHELP_TABLE_NAME, contentValues, null, null);
		return true;
	}
	
	// In the location page we need to cross check whether registration is successful or not.
	public boolean updateAtUrHelpInfoAtLoc() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constants.ATURHELP_COL_LOC_IS_REGISTER, "yes");
		db.update(Constants.ATURHELP_TABLE_NAME, contentValues, null, null);
		return true;
	}

	public AtUrHelpInfo getAtUrHelpInfo() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from aturhelpinfo", null);
		res.moveToFirst();
		System.out.println("the res value is " + res);
		System.out.println("the res size is " + res.getCount());

		AtUrHelpInfo helpInfo = null;
		if (res.getCount() != 0) {
			helpInfo = new AtUrHelpInfo();
			helpInfo.setIsRegister(res.getString(res
					.getColumnIndexOrThrow(Constants.ATURHELP_COL_IS_REGISTER)));
			helpInfo.setIsAdmin(res.getString(res
					.getColumnIndexOrThrow(Constants.ATURHELP_COL_IS_ADMIN)));
			helpInfo.setCusName(res.getString(res
					.getColumnIndexOrThrow(Constants.ATURHELP_COL_CUS_NAME)));
			helpInfo.setEmail(res.getString(res
					.getColumnIndexOrThrow(Constants.ATURHELP_COL_EMAIL)));
			helpInfo.setMobileNo(res.getString(res
					.getColumnIndexOrThrow(Constants.ATURHELP_COL_MOBILE_NUMBER)));
			helpInfo.setIsRegisterLoc(res.getString(res
					.getColumnIndexOrThrow(Constants.ATURHELP_COL_LOC_IS_REGISTER)));
		}
		return helpInfo;
	}

	@Deprecated
	public boolean insertContact(String isRegister, String isAdmin) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constants.ATURHELP_COL_IS_ADMIN, isAdmin);
		contentValues.put(Constants.ATURHELP_COL_IS_REGISTER, isRegister);
		db.insert(Constants.ATURHELP_TABLE_NAME, null, contentValues);
		return true;
	}
	
	public boolean insertContact(AtUrHelpInfo helpInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constants.ATURHELP_COL_IS_ADMIN, helpInfo.getIsAdmin());
		contentValues.put(Constants.ATURHELP_COL_IS_REGISTER, helpInfo.getIsRegister());
		contentValues.put(Constants.ATURHELP_COL_CUS_NAME, helpInfo.getCusName());
		contentValues.put(Constants.ATURHELP_COL_EMAIL, helpInfo.getEmail());
		contentValues.put(Constants.ATURHELP_COL_MOBILE_NUMBER, helpInfo.getMobileNo());
		contentValues.put(Constants.ATURHELP_COL_LOC_IS_REGISTER, "no");
		db.insert(Constants.ATURHELP_TABLE_NAME, null, contentValues);
		return true;
	}

}
