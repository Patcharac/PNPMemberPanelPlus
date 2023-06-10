package com.panelplus.pnpmember.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExternalStorage_FarmGeo_DB_OpenHelper {
private SQLiteDatabase database;
private File dbFile;
private SQLiteDatabase.CursorFactory factory;

public static final String KEY_ID = "_id";
public static final String KEY_Link_ID = "Link_ID";


private static final String TABLE_FARM_GEO_DATA = "farm_geo_data";
private static final String TABLE_OBJECT_DATA = "object_see";

public ExternalStorage_FarmGeo_DB_OpenHelper(Context context , String dbFileName) {
	String folderName = "MapDB"; // folder name
	String appDbDir = context.getApplicationContext().getFilesDir() + "/" + folderName; // folder path

	File folder = new File(appDbDir); // create folder
	if (!folder.exists()) {
		folder.mkdirs(); // create folder if it doesn't exist
	}

	this.dbFile = new File(folder, dbFileName);
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

public ArrayList<HashMap<String, String>> SelectAllData2( SQLiteDatabase db)  throws SQLException {
	// TODO Auto-generated method stub
	
	 try {
		 
		 ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
		 HashMap<String, String> map;
		 
		
		 db = this.getReadableDatabase(); // Read Data
			
		 String strSQL = "select (select count(RG_F_ID) from "+ TABLE_FARM_GEO_DATA +" b  where a.RG_F_ID >= b.RG_F_ID) as Rec,RG_F_ID,RG_PROJECT,RG_GIS_AREA from "+ TABLE_FARM_GEO_DATA +" a";
		    
		 Cursor cursor = db.rawQuery(strSQL, null);
		 
		 	if(cursor != null)
		 	{
		 	    if (cursor.moveToFirst()) {
		 	        do {
		 	        	map = new HashMap<String, String>();
		 	        	map.put("Rec", cursor.getString(0));
			 	        map.put("RG_F_ID", cursor.getString(1));
			 	        map.put("RG_PROJECT", cursor.getString(2));
			 	        map.put("RG_GIS_AREA", cursor.getString(3));
			 	        MyArrList.add(map);
		 	        } while (cursor.moveToNext());
		 	    }
		 	}
		 	cursor.close();
		 	db.close();
			return MyArrList;
			
	 } catch (Exception e) {
	    return null;
	 }

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
			 
		
				
			 String strSQL = "SELECT  * FROM " + TABLE_FARM_GEO_DATA;
			 
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
	public long InsertDataFarmGeoLocation(String qouta_ID ,
										  String Link_ID,
										  String Emp_ID ,
										  String Farm_Date,
										  String Farm_Area,
										  String Farm_Polygon,
										  String Farm_GeoStatus,
										  byte [] Farm_Pic,
										  String Farm_YearOfPlant,
										  String Farm_RubberBreed,
										  String Farm_RubberPerRai,
										  String Farm_Status,
										  String DirNorth_Use,
										  String DirNorth_Owner,
										  String DirNorth_Tel,

										  String DirEast_Use,
										  String DirEast_Owner,
										  String DirEast_Tel,

										  String DirSouth_Use,
										  String DirSouth_Owner,
										  String DirSouth_Tel,

										  String DirWest_Use,
										  String DirWest_Owner,
										  String DirWest_Tel,
										  String[] evaFarmString,
										  String[] evaFarmSuggestString,
										  SQLiteDatabase db) throws SQLException   {

		int yearNow,Rubber_YearOld;


		DateFormat df = new SimpleDateFormat("yyyy");
		yearNow = Integer.parseInt(df.format(Calendar.getInstance().getTime()))+543;
		Rubber_YearOld = yearNow - Integer.parseInt(Farm_YearOfPlant);


		if(!Objects.equals(Farm_Status, "R")){
//			if (Rubber_YearOld <20){
//				Farm_Status = "L20";
//			} else if(Rubber_YearOld >= 20){
//				Farm_Status = "M20";
//			}
			Farm_Status = "G";
		}
		
		 try {
		
			
			 ContentValues Val = new ContentValues();
				//Val.put(KEY_ID , ID);
			 Val.put("qouta_ID" , qouta_ID);
			 Val.put("Link_ID", Link_ID);
			 Val.put("Emp_ID" , Emp_ID);
			 Val.put("Farm_Date" , Farm_Date);
			 Val.put("Farm_Area" , Farm_Area);
			 Val.put("Farm_Polygon" , Farm_Polygon);
			 Val.put("Farm_GeoStatus" , Farm_GeoStatus);
			 Val.put("Farm_Pic" , Farm_Pic);
			 Val.put("Farm_YearOfPlant" , Farm_YearOfPlant);
			 Val.put("Farm_RubberBreed" , Farm_RubberBreed);
			 Val.put("Farm_RubberPerRai" , Farm_RubberPerRai);
			 Val.put("Farm_Status" , Farm_Status);
			 Val.put("DirNorth_Use",DirNorth_Use);
			 Val.put("DirNorth_Owner",DirNorth_Owner);
			 Val.put("DirNorth_Tel",DirNorth_Tel);

			 Val.put("DirEast_Use",DirEast_Use);
			 Val.put("DirEast_Owner",DirEast_Owner);
			 Val.put("DirEast_Tel",DirEast_Tel);

			 Val.put("DirSouth_Use", DirSouth_Use);
			 Val.put("DirSouth_Owner", DirSouth_Owner);
			 Val.put("DirSouth_Tel", DirSouth_Tel);

			 Val.put("DirWest_Use",DirWest_Use);
			 Val.put("DirWest_Owner",DirWest_Owner);
			 Val.put("DirWest_Tel",DirWest_Tel);


			 for(int i=0;i<evaFarmString.length;i++){
				 Val.put("EvaFarm_NO"+(i+1),evaFarmString[i]);
				 Val.put("EvaFarmSuggest_NO"+(i+1),evaFarmSuggestString[i]);
			 }

			 //Log.d("Insert","qoata_id = "+ qoata_id + "centerX = "+centerX+ "centerY = "+centerY+ "polygonpoint = "+polygonpoint);
		  	  
					long rows = db.insert(TABLE_FARM_GEO_DATA, null, Val);
		
					return rows; // return rows inserted.
		 } catch (Exception e) {
			 Log.d("SQL_Insert", String.valueOf(e));
		    return -1;
		 }

	}

	public long insertDataObject(String Link_ID,
								 String Obj_LocalName,
								 String Obj_Status,
								 SQLiteDatabase db) throws SQLException{

		try{
			ContentValues Con = new ContentValues();
			Con.put("Link_ID",Link_ID);
			Con.put("Obj_LocalName",Obj_LocalName);
			Con.put("Obj_Status",Obj_Status);

			long rows = db.insert(TABLE_OBJECT_DATA , null, Con);
			return rows;
		}catch (Exception e) {
			Log.d("SQL_Insert", String.valueOf(e));
			return -1;
		}
	}

	public long DeleteDataObject (String Farm_ID, SQLiteDatabase db) throws SQLException   {

		try {

			long rows =  db.delete(TABLE_OBJECT_DATA, KEY_Link_ID+" = ?",
					new String[] { Farm_ID });
			return rows;
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}


	public String CountData( SQLiteDatabase db) throws SQLException 
	{
	    String data="";
	    String strSQL = "SELECT  distinct RG_F_ID  FROM " + TABLE_FARM_GEO_DATA;
	    Cursor mCursor = db.rawQuery(strSQL, null);		    
	   
	    if (mCursor != null) {	    	
	    	data = String.valueOf(mCursor.getCount());	       
	        mCursor.close();
	    }
	    return data;
	}

	public String[] SelectData(String _id, SQLiteDatabase db) throws SQLException
	{
		String arrData[] = null;

		Cursor cursor =
				db.query(true, TABLE_FARM_GEO_DATA, new String[] {
								"*"
						},
						KEY_ID + "=?",
						new String[]{_id},
						null,
						null,
						null,
						null);
		if(cursor != null)
		{
			if (cursor.moveToFirst()) {
				arrData = new String[cursor.getColumnCount()];
				arrData[0] = cursor.getString(1);
				arrData[1] = cursor.getString(2);
				arrData[2] = cursor.getString(3);
				arrData[3] = cursor.getString(4);
				arrData[4] = cursor.getString(5);

			}
		}

		cursor.close();

		return arrData;
	}
	

	public long DeleteData(String ID, SQLiteDatabase db) throws SQLException   {
		
		try {
			
			long rows =  db.delete(TABLE_FARM_GEO_DATA, KEY_ID+" = ?",
		            new String[] { String.valueOf(ID) });
			return rows;
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}	
	}

}