package com.panelplus.pnpmember.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class ExternalStorage_Farm_GIS_DB_OpenHelper {
	private static final String DATABASE_TABLE = "fscGisData";
	public static final String KEY_EMP_ID = "Emp_ID";
	public static final String KEY_EMP_NAME = "Emp_Name";
	public static final String KEY_FARM_AREA = "Farm_Area";
	public static final String KEY_FARM_GEO = "Farm_Geo";
	public static final String KEY_FARM_GEOX = "Farm_GeoX";
	public static final String KEY_FARM_GEOY = "Farm_GeoY";
	public static final String KEY_FARM_ID = "Farm_ID";
	public static final String KEY_FARM_RUBBERBREED = "Farm_RubberBreed";
	public static final String KEY_FARM_STATUS = "Farm_Status";
	public static final String KEY_FARM_YEAROFPLANT = "Farm_YearOfPlant";
	public static final String KEY_MEM_GENDER = "mem_gender";
	public static final String KEY_MEM_LASTNAME = "mem_LastName";
	public static final String KEY_MEM_NAME = "mem_Name";
	public static final String KEY_MEM_TEL = "mem_Tel";
	public static final String KEY_QOUTA_ID = "qouta_ID";
	private SQLiteDatabase database;
	private File dbFile;

	private Context context;
	private SQLiteDatabase.CursorFactory factory;

	public ExternalStorage_Farm_GIS_DB_OpenHelper(Context context,String dbFileName) {

//		if (!Environment.getExternalStorageState().equals("mounted")) {
//			throw new AndroidRuntimeException("External storage (SD-Card) not mounted");
//		}


		String folderName = "MapDB"; // folder name
		String appDbDir = context.getApplicationContext().getFilesDir() + "/" + folderName; // folder path

		File folder = new File(appDbDir); // create folder
		if (!folder.exists()) {
			folder.mkdirs(); // create folder if it doesn't exist
		}


		this.dbFile = new File(folder, dbFileName);
		Log.e("Error","ExternalStorage_Farm_GIS_DB_OpenHelper : " +dbFile.getAbsolutePath());

	}

	public boolean databaseFileExists() {
		return this.dbFile.exists();
	}

	private void open() {
		if (this.dbFile.exists()) {
			this.database = SQLiteDatabase.openDatabase(this.dbFile.getAbsolutePath(), this.factory, 0);
		}
	}

	public void close() {
		if (this.database != null) {
			this.database.close();
			this.database = null;
		}
	}

	public SQLiteDatabase getReadableDatabase() {
		return getDatabase();
	}

	private SQLiteDatabase getDatabase() {
		if (this.database == null) {
			open();
		}
		return this.database;
	}

	public String GetNameFromQouta(String qouta, SQLiteDatabase db) throws SQLException {
		String data = "";
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{"mem_gender", "mem_Name", "mem_LastName"}, "qouta_ID=?", new String[]{qouta}, (String) null, (String) null, (String) null, (String) null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			if (mCursor.moveToFirst()) {
				do {
					data = mCursor.getString(0) + mCursor.getString(1) + "  " + mCursor.getString(2);
				} while (mCursor.moveToNext());
			}
			mCursor.close();
		}
		return data;
	}

	public ArrayList<HashMap<String, String>> getFarmGisSearch(String QOUTA_ID, SQLiteDatabase db) throws SQLException {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<>();
			 db = getReadableDatabase();
			String strSQL = "SELECT Farm_ID,Farm_Area,Farm_GeoX,Farm_GeoY,Farm_Status FROM fscGisData Where qouta_ID LIKE '%" + QOUTA_ID + "%'";
			Log.d("querySearch", strSQL);
			Cursor cursor = db.rawQuery(strSQL, (String[]) null);
			if (cursor == null || !cursor.moveToFirst()) {
				cursor.close();
				db.close();
				return MyArrList;
			}
			do {
				HashMap<String, String> map = new HashMap<>();
				map.put("FARM_ID", cursor.getString(0));
				map.put("FARM_AREA", cursor.getString(1));
				map.put("FARM_GEOX", cursor.getString(2));
				map.put("FARM_GEOY", cursor.getString(3));
				map.put("FARM_STATUS", cursor.getString(4));
				MyArrList.add(map);
			} while (cursor.moveToNext());
			cursor.close();
			db.close();
			return MyArrList;
		} catch (Exception e) {
			return null;
		}
	}

	public class sMemberDrawPolygons {
		String _EMP_ID;
		String _FARM_AREA;
		String _FARM_GEO;
		String _FARM_GEOX;
		String _FARM_GEOY;
		String _FARM_ID;
		String _FARM_RUBBERBREED;
		String _FARM_STATUS;
		String _FARM_YEAROFPLANT;
		String _MEM_GENDER;
		String _MEM_LASTNAME;
		String _MEM_NAME;
		String _QOUTA_ID;

		public sMemberDrawPolygons() {
		}

		public void sFARM_ID(String vFARM_ID) {
			this._FARM_ID = vFARM_ID;
		}

		public void sQOUTA_ID(String vQOUTA_ID) {
			this._QOUTA_ID = vQOUTA_ID;
		}

		public void sEMP_ID(String vEMP_ID) {
			this._EMP_ID = vEMP_ID;
		}

		public void sMEM_GENDER(String vMEM_GENDER) {
			this._MEM_GENDER = vMEM_GENDER;
		}

		public void sMEM_NAME(String vMEM_NAME) {
			this._MEM_NAME = vMEM_NAME;
		}

		public void sMEM_LASTNAME(String vMEM_LASTNAME) {
			this._MEM_LASTNAME = vMEM_LASTNAME;
		}

		public void sFARM_AREA(String vFARM_AREA) {
			this._FARM_AREA = vFARM_AREA;
		}

		public void sFARM_GEO(String vFARM_GEO) {
			this._FARM_GEO = vFARM_GEO;
		}

		public void sFARM_GEOX(String vFARM_GEOX) {
			this._FARM_GEOX = vFARM_GEOX;
		}

		public void sFARM_GEOY(String vFARM_GEOY) {
			this._FARM_GEOY = vFARM_GEOY;
		}

		public void sFARM_YEAROFPLANT(String vFARM_YEAROFPLANT) {
			this._FARM_YEAROFPLANT = vFARM_YEAROFPLANT;
		}

		public void sFARM_RUBBERBREED(String vFARM_RUBBERBREED) {
			this._FARM_RUBBERBREED = vFARM_RUBBERBREED;
		}

		public void sFARM_STATUS(String vFARM_STATUS) {
			this._FARM_STATUS = vFARM_STATUS;
		}

		public String gFARM_ID() {
			return this._FARM_ID;
		}

		public String gQOUTA_ID() {
			return this._QOUTA_ID;
		}

		public String gEMP_ID() {
			return this._EMP_ID;
		}

		public String gMEM_GENDER() {
			return this._MEM_GENDER;
		}

		public String gMEM_NAME() {
			return this._MEM_NAME;
		}

		public String gMEM_LASTNAME() {
			return this._MEM_LASTNAME;
		}

		public String gFARM_AREA() {
			return this._FARM_AREA;
		}

		public String gFARM_GEO() {
			return this._FARM_GEO;
		}

		public String gFARM_GEOX() {
			return this._FARM_GEOX;
		}

		public String gFARM_GEOY() {
			return this._FARM_GEOY;
		}

		public String gFARM_YEAROFPLANT() {
			return this._FARM_YEAROFPLANT;
		}

		public String gFARM_RUBBERBREED() {
			return this._FARM_RUBBERBREED;
		}

		public String gFARM_STATUS() {
			return this._FARM_STATUS;
		}
	}

	public List<sMemberDrawPolygons> SelectDataDrawPolygon(double Max_X, double Min_X, double Max_Y, double Min_Y, SQLiteDatabase db) throws SQLException {
		try {
			List<sMemberDrawPolygons> MemberList = new ArrayList<>();
			String strSQL = "SELECT Farm_ID,qouta_ID,Emp_ID,mem_gender,mem_Name,mem_LastName,Farm_Area,Farm_Geo,Farm_GeoX,Farm_GeoY,Farm_YearOfPlant,Farm_RubberBreed,Farm_Status FROM fscGisData Where Farm_GeoX <= " + Max_X + " AND " + KEY_FARM_GEOX + " >= " + Min_X + " AND  " + KEY_FARM_GEOY + " <= " + Max_Y + " AND  " + KEY_FARM_GEOY + " >= " + Min_Y + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
			Log.d("Get_Val_strSQL", String.valueOf(strSQL));
			Cursor mCursor = db.rawQuery(strSQL, (String[]) null);
			if (mCursor != null) {
				mCursor.moveToFirst();
				if (mCursor.moveToFirst()) {
					do {
						sMemberDrawPolygons cMember = new sMemberDrawPolygons();
						cMember.sFARM_ID(mCursor.getString(0).toString());
						cMember.sQOUTA_ID(mCursor.getString(1).toString());
						cMember.sEMP_ID(mCursor.getString(2).toString());
						cMember.sMEM_GENDER(mCursor.getString(3).toString());
						cMember.sMEM_NAME(mCursor.getString(4).toString());
						cMember.sMEM_LASTNAME(mCursor.getString(5).toString());
						cMember.sFARM_AREA(mCursor.getString(6).toString());
						cMember.sFARM_GEO(mCursor.getString(7).toString());
						Log.d("strSQL_Polygon", mCursor.getString(7).toString());
						cMember.sFARM_GEOX(mCursor.getString(8).toString());
						cMember.sFARM_GEOY(mCursor.getString(9).toString());
						cMember.sFARM_YEAROFPLANT(mCursor.getString(10).toString());
						cMember.sFARM_RUBBERBREED(mCursor.getString(11).toString());
						cMember.sFARM_STATUS(mCursor.getString(12).toString());
						MemberList.add(cMember);
					} while (mCursor.moveToNext());
				}
			}
			mCursor.close();
			return MemberList;
		} catch (Exception e) {
			return null;
		}
	}

	public class sMemberCalloutDatas {
		String _EMP_ID;
		String _EMP_NAME;
		String _FARM_AREA;
		String _FARM_GEO;
		String _FARM_GEOX;
		String _FARM_GEOY;
		String _FARM_ID;
		String _FARM_RUBBERBREED;
		String _FARM_STATUS;
		String _FARM_YEAROFPLANT;
		String _MEM_GENDER;
		String _MEM_LASTNAME;
		String _MEM_NAME;
		String _MEM_TEL;
		String _QOUTA_ID;

		public sMemberCalloutDatas() {
		}

		public void sFARM_ID(String vFARM_ID) {
			this._FARM_ID = vFARM_ID;
		}

		public void sQOUTA_ID(String vQOUTA_ID) {
			this._QOUTA_ID = vQOUTA_ID;
		}

		public void sEMP_ID(String vEMP_ID) {
			this._EMP_ID = vEMP_ID;
		}

		public void sEMP_NAME(String vEMP_NAME) {
			this._EMP_NAME = vEMP_NAME;
		}

		public void sMEM_GENDER(String vMEM_GENDER) {
			this._MEM_GENDER = vMEM_GENDER;
		}

		public void sMEM_NAME(String vMEM_NAME) {
			this._MEM_NAME = vMEM_NAME;
		}

		public void sMEM_LASTNAME(String vMEM_LASTNAME) {
			this._MEM_LASTNAME = vMEM_LASTNAME;
		}

		public void sMEM_TEL(String vMEM_TEL) {
			this._MEM_TEL = vMEM_TEL;
		}

		public void sFARM_AREA(String vFARM_AREA) {
			this._FARM_AREA = vFARM_AREA;
		}

		public void sFARM_GEO(String vFARM_GEO) {
			this._FARM_GEO = vFARM_GEO;
		}

		public void sFARM_GEOX(String vFARM_GEOX) {
			this._FARM_GEOX = vFARM_GEOX;
		}

		public void sFARM_GEOY(String vFARM_GEOY) {
			this._FARM_GEOY = vFARM_GEOY;
		}

		public void sFARM_YEAROFPLANT(String vFARM_YEAROFPLANT) {
			this._FARM_YEAROFPLANT = vFARM_YEAROFPLANT;
		}

		public void sFARM_RUBBERBREED(String vFARM_RUBBERBREED) {
			this._FARM_RUBBERBREED = vFARM_RUBBERBREED;
		}

		public void sFARM_STATUS(String vFARM_STATUS) {
			this._FARM_STATUS = vFARM_STATUS;
		}

		public String gFARM_ID() {
			return this._FARM_ID;
		}

		public String gQOUTA_ID() {
			return this._QOUTA_ID;
		}

		public String gEMP_ID() {
			return this._EMP_ID;
		}

		public String gEMP_NAME() {
			return this._EMP_NAME;
		}

		public String gMEM_GENDER() {
			return this._MEM_GENDER;
		}

		public String gMEM_NAME() {
			return this._MEM_NAME;
		}

		public String gMEM_LASTNAME() {
			return this._MEM_LASTNAME;
		}

		public String gMEM_TEL() {
			return this._MEM_TEL;
		}

		public String gFARM_AREA() {
			return this._FARM_AREA;
		}

		public String gFARM_GEO() {
			return this._FARM_GEO;
		}

		public String gFARM_GEOX() {
			return this._FARM_GEOX;
		}

		public String gFARM_GEOY() {
			return this._FARM_GEOY;
		}

		public String gFARM_YEAROFPLANT() {
			return this._FARM_YEAROFPLANT;
		}

		public String gFARM_RUBBERBREED() {
			return this._FARM_RUBBERBREED;
		}

		public String gFARM_STATUS() {
			return this._FARM_STATUS;
		}
	}

	public List<sMemberCalloutDatas> getCaneCallout(String FARM_ID_KEY, SQLiteDatabase db) throws SQLException {
		try {
			List<sMemberCalloutDatas> MemberList = new ArrayList<>();
			String strSQL = "SELECT Farm_ID,qouta_ID,Emp_ID,Emp_Name,mem_gender,mem_Name,mem_LastName,mem_Tel,Farm_Area,Farm_Geo,Farm_GeoX,Farm_GeoY,Farm_YearOfPlant,Farm_RubberBreed,Farm_Status FROM fscGisData Where Farm_ID = " + FARM_ID_KEY;
			Log.d("getCaneCallout_SQL", strSQL);
			Cursor mCursor = db.rawQuery(strSQL, (String[]) null);
			if (mCursor != null) {
				mCursor.moveToFirst();
				if (mCursor.moveToFirst()) {
					do {
						sMemberCalloutDatas cMember = new sMemberCalloutDatas();
						cMember.sFARM_ID(mCursor.getString(0).toString());
						cMember.sQOUTA_ID(mCursor.getString(1).toString());
						cMember.sEMP_ID(mCursor.getString(2).toString());
						cMember.sEMP_NAME(mCursor.getString(3).toString());
						cMember.sMEM_GENDER(mCursor.getString(4).toString());
						cMember.sMEM_NAME(mCursor.getString(5).toString());
						cMember.sMEM_LASTNAME(mCursor.getString(6).toString());
						cMember.sMEM_TEL(mCursor.getString(7).toString());
						cMember.sFARM_AREA(mCursor.getString(8).toString());
						cMember.sFARM_GEO(mCursor.getString(9).toString());
						cMember.sFARM_GEOX(mCursor.getString(10).toString());
						cMember.sFARM_GEOY(mCursor.getString(11).toString());
						cMember.sFARM_YEAROFPLANT(mCursor.getString(12).toString());
						cMember.sFARM_RUBBERBREED(mCursor.getString(13).toString());
						cMember.sFARM_STATUS(mCursor.getString(14).toString());
						MemberList.add(cMember);
					} while (mCursor.moveToNext());
				}
			}
			mCursor.close();
			return MemberList;
		} catch (Exception e) {
			return null;
		}
	}
}
