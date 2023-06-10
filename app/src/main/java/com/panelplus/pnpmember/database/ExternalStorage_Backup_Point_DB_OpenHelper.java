package com.panelplus.pnpmember.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.util.Log;

public class ExternalStorage_Backup_Point_DB_OpenHelper {
	private SQLiteDatabase database;
	private File dbFile;
	private SQLiteDatabase.CursorFactory factory;


	public static final String KEY_ID = "ID";
	public static final String KEY_PointX = "PointX";
	public static final String KEY_PointY = "PointY";






	private static final String DATABASE_TABLE = "markpoint";

	public ExternalStorage_Backup_Point_DB_OpenHelper(Context context,String dbFileName) {

		if (context != null) {
			String folderName = "MapDB"; // ชื่อโฟลเดอร์
			File appDbDir = new File(context.getApplicationContext().getFilesDir(), folderName); // สร้างตัวแปร appDbDir โดยใช้เมธอด getFilesDir() เพื่อรับพาธของโฟลเดอร์ที่แอปพลิเคชันสามารถเขียนได้
			if (!appDbDir.exists()) {
				appDbDir.mkdirs(); // สร้างโฟลเดอร์หากยังไม่มี
			}
			this.dbFile = new File(appDbDir, dbFileName);
		} else {
			// จัดการกรณี context เป็น null
		}

	}

	public boolean databaseFileExists() {
		return dbFile.exists();
	}

	private void open() {
		if (dbFile.exists()) {
			database = SQLiteDatabase.openDatabase(
					dbFile.getAbsolutePath(),
					factory,
					SQLiteDatabase.OPEN_READWRITE);
		}
	}

	public void close() {
		if (database != null ) {
			database.close();
			database = null;
		}
	}

	public SQLiteDatabase getReadableDatabase() {
		return getDatabase();
	}

	private SQLiteDatabase getDatabase() {
		if (database==null) {
			open();
		}
		return database;
	}

	//Select All Data
	public class sMembers {
		String _ID, _PointX, _PointY;

		// Set Value
		public void sID(String vID){
			this._ID = vID;
		}
		public void sPointX(String vPointX){
			this._PointX = vPointX;
		}
		public void sPointY(String vPointY){
			this._PointY = vPointY;
		}

		// Get Value
		public String gID(){
			return _ID;
		}
		public String gPointX(){
			return _PointX;
		}
		public String gPointY(){
			return _PointY;
		}
	}

	public List<sMembers> SelectAllData(SQLiteDatabase db) throws SQLException{
		// TODO Auto-generated method stub

		try {
			List<sMembers> MemberList = new ArrayList<sMembers>();



			String strSQL = "SELECT  * FROM " + DATABASE_TABLE;

			Log.d("Get SQL",strSQL);

			Cursor mCursor = db.rawQuery(strSQL, null);

			if (mCursor != null) {
				mCursor.moveToFirst();
				if (mCursor.moveToFirst()){
					do{
						sMembers cMember = new sMembers();
						cMember.sID(mCursor.getString(0));
						Log.d("Get SQL","SELECT");

						cMember.sPointX(mCursor.getString(1));
						Log.d("Get SQL","SELECT");
						cMember.sPointY(mCursor.getString(2));
						Log.d("Get SQL","SELECT");
						MemberList.add(cMember);
					}while(mCursor.moveToNext());
				}

			}
			mCursor.close();
			return MemberList;

		} catch (Exception e) {
			Log.d("Get SQL","NULL");
			return null;
		}

	}

	//Insert Data
	public long InsertData(String ID,String PointX, String PointY,
						   SQLiteDatabase db) throws SQLException   {


		try {


			ContentValues Val = new ContentValues();
			Val.put(KEY_ID , ID);
			Val.put(KEY_PointX , PointX);
			Val.put(KEY_PointY , PointY);

			//Log.d("Insert","qoata_id = "+ qoata_id + "centerX = "+centerX+ "centerY = "+centerY+ "polygonpoint = "+polygonpoint);

			long rows = db.insert(DATABASE_TABLE, null, Val);

			return rows; // return rows inserted.
		} catch (Exception e) {
			Log.d("SQL_Insert", String.valueOf(e));
			return -1;
		}

	}


	public long DeleteData(String ID, SQLiteDatabase db) throws SQLException   {

		try {

			long rows =  db.delete(DATABASE_TABLE, KEY_ID+" = ?",
					new String[] { String.valueOf(ID) });
			return rows;
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}

	public long DeleteAllData(SQLiteDatabase db) throws SQLException   {

		try {

			long rows =  db.delete(DATABASE_TABLE, null,
					null);
			return rows;
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}

}