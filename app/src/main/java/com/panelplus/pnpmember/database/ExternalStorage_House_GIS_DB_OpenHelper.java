package com.panelplus.pnpmember.database;

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

public class ExternalStorage_House_GIS_DB_OpenHelper {
    private SQLiteDatabase database;
    private File dbFile;
    private SQLiteDatabase.CursorFactory factory;


    public static final String KEY_QOUTA_ID = "qouta_ID";
    public static final String KEY_EMP_ID = "Emp_ID";
    public static final String KEY_EMP_NAME = "Emp_Name";
    public static final String KEY_ZONE = "Zone";
    public static final String KEY_MEM_GENDER = "mem_gender";
    public static final String KEY_MEM_NAME = "mem_Name";
    public static final String KEY_MEM_LASTNAME = "mem_LastName";
    public static final String KEY_MEM_TEL = "mem_Tel";
    public static final String KEY_AD_TAMBOL = "Ad_Tambol";
    public static final String KEY_AD_AMPHER = "Ad_Ampher";
    public static final String KEY_AD_PROVINCE = "Ad_Province";
    public static final String KEY_HOUSE_GEO = "House_Geo";
    public static final String KEY_HOUSE_GEOX = "House_GeoX";
    public static final String KEY_HOUSE_GEOY = "House_GeoY";
    public static final String KEY_HOUSE_LATITUDE = "House_Latitude";
    public static final String KEY_HOUSE_LONGITUDE = "House_Longitude";




    private static final String DATABASE_TABLE = "fscHouseData";

    public ExternalStorage_House_GIS_DB_OpenHelper(
        String dbFileName) {
      // this.factory = factory;

       if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
           throw new AndroidRuntimeException(
               "External storage (SD-Card) not mounted");
       }
       File appDbDir = new File(
           Environment.getExternalStorageDirectory(),
           "MapDB");
       if (!appDbDir.exists()) {
           appDbDir.mkdirs();
       }
       this.dbFile = new File(appDbDir, dbFileName);
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

    public ArrayList<HashMap<String, String>> getHouseGisSearch(String QOUTA_ID,
                                                             SQLiteDatabase db)  throws SQLException {
        // TODO Auto-generated method stub

        try {

            ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;


            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT "+KEY_QOUTA_ID+","+
                    KEY_HOUSE_GEOX+","+
                    KEY_HOUSE_GEOY+","+
                    KEY_MEM_GENDER+","+
                    KEY_MEM_NAME+","+
                    KEY_MEM_LASTNAME+","+
                    KEY_ZONE
                    +" FROM " + DATABASE_TABLE +
                    " Where " + KEY_QOUTA_ID +" LIKE '%"+ QOUTA_ID +"%'";
            Log.d("querySearch", strSQL);


//							if(QTS.trim().equals("โควต้า")){
//								strSQL+=" AND " + KEY_QT+" = "+ F_ID;
//							}else{
//								strSQL+=" AND " + KEY_F_ID+" = "+ F_ID;
//							}


            //Log.d("Search_SSTA", SSTA);
            //Log.d("Search", strSQL);



            Cursor cursor = db.rawQuery(strSQL, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    do {
                        map = new HashMap<String, String>();
                        map.put("QOUTA_ID", cursor.getString(0));
                        map.put("HOUSE_GEOX", cursor.getString(1));
                        map.put("HOUSE_GEOY", cursor.getString(2));

                        map.put("MEM_NAME", cursor.getString(3)+cursor.getString(4)+" "+cursor.getString(5));

                        map.put("ZONE", cursor.getString(6));


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

    public ArrayList<HashMap<String, String>> getHouseGisSearchByName(String NameSearch,
                                                                SQLiteDatabase db)  throws SQLException {
        // TODO Auto-generated method stub

        try {

            ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;


            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT "+KEY_QOUTA_ID+","+
                    KEY_HOUSE_GEOX+","+
                    KEY_HOUSE_GEOY+","+
                    KEY_MEM_GENDER+","+
                    KEY_MEM_NAME+","+
                    KEY_MEM_LASTNAME+","+
                    KEY_ZONE
                    +" FROM " + DATABASE_TABLE +
                    " Where " + KEY_MEM_NAME +" LIKE '%"+ NameSearch +"%'" +
                    "OR " + KEY_MEM_LASTNAME +" LIKE '%"+ NameSearch +"%'";
            Log.d("querySearch", strSQL);


//							if(QTS.trim().equals("โควต้า")){
//								strSQL+=" AND " + KEY_QT+" = "+ F_ID;
//							}else{
//								strSQL+=" AND " + KEY_F_ID+" = "+ F_ID;
//							}


            //Log.d("Search_SSTA", SSTA);
            //Log.d("Search", strSQL);



            Cursor cursor = db.rawQuery(strSQL, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    do {
                        map = new HashMap<String, String>();
                        map.put("QOUTA_ID", cursor.getString(0));
                        map.put("HOUSE_GEOX", cursor.getString(1));
                        map.put("HOUSE_GEOY", cursor.getString(2));

                        map.put("MEM_NAME", cursor.getString(3)+cursor.getString(4)+" "+cursor.getString(5));

                        map.put("ZONE", cursor.getString(6));


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


public class sMemberDrawPolygonsHouse {
    String _QOUTA_ID,
    _MEM_GENDER,
    _MEM_NAME,
    _MEM_LASTNAME,
    _ZONE,
    _HOUSE_GEO,
    _HOUSE_GEOX,
    _HOUSE_GEOY,
    _AD_TAMBOL,
    _AD_AMPHER,
    _AD_PROVINCE,
    _EMP_ID;


    // Set Value
    public void sQOUTA_ID(String vQOUTA_ID){
          this._QOUTA_ID= vQOUTA_ID;
    }
    // Set Value
    public void sEMP_ID(String vEMP_ID){
        this._EMP_ID = vEMP_ID;
    }
    // Set Value
    public void sMEM_GENDER(String vMEM_GENDER){
          this._MEM_GENDER = vMEM_GENDER;
    }
    // Set Value
    public void sMEM_NAME(String vMEM_NAME){
          this._MEM_NAME = vMEM_NAME;
    }
    // Set Value
    public void sMEM_LASTNAME(String vMEM_LASTNAME){
          this._MEM_LASTNAME = vMEM_LASTNAME;
    }
    // Set Value
    public void sZONE(String vZONE){
          this._ZONE = vZONE;
    }
    // Set Value
    public void sHOUSE_GEO(String vHOUSE_GEO){
          this._HOUSE_GEO = vHOUSE_GEO;
    }

    // Set Value
    public void sHOUSE_GEOX(String vHOUSE_GEOX){
          this._HOUSE_GEOX = vHOUSE_GEOX;
    }
    // Set Value
    public void sHOUSE_GEOY(String vHOUSE_GEOY){
          this._HOUSE_GEOY = vHOUSE_GEOY;
    }
    // Set Value
    public void sAD_TAMBOL(String vAD_TAMBOL){
          this._AD_TAMBOL = vAD_TAMBOL;
    }

    // Set Value
    public void sAD_AMPHER(String vAD_AMPHER){
          this._AD_AMPHER = vAD_AMPHER;
    }
    // Set Value
    public void sAD_PROVINCE(String vAD_PROVINCE){
          this._AD_PROVINCE = vAD_PROVINCE;
    }




// -------------------------------------


    // Get Value
    public String gQOUTA_ID(){
        return _QOUTA_ID;
    }

    // Get Value
    public String gEMP_ID(){
        return _EMP_ID;
    }

    // Get Value
    public String gMEM_GENDER(){
        return _MEM_GENDER;
    }

    // Get Value
    public String gMEM_NAME(){
        return _MEM_NAME;
    }

    // Get Value
    public String gMEM_LASTNAME(){
        return _MEM_LASTNAME;
    }

    // Get Value
    public String gZONE(){
        return _ZONE;
    }
    // Get Value
    public String gHOUSE_GEO(){
        return _HOUSE_GEO;
    }

    // Get Value
    public String gHOUSE_GEOX(){
        return _HOUSE_GEOX;
    }
    // Get Value
    public String gHOUSE_GEOY(){
        return _HOUSE_GEOY;
    }


    // Get Value
    public String gAD_TAMBOL(){
        return _AD_TAMBOL;
    }

    // Get Value
    public String gAD_AMPHER(){
        return _AD_AMPHER;
    }
    // Get Value
    public String gAD_PROVINCE(){
        return _AD_PROVINCE;
    }


}

public List<sMemberDrawPolygonsHouse> SelectDataDrawPolygonHouse(double Max_X, double Min_X, double Max_Y, double Min_Y,
                                                                 SQLiteDatabase db) throws SQLException
{
    //555
    try {
        List<sMemberDrawPolygonsHouse> MemberList = new ArrayList<sMemberDrawPolygonsHouse>();

            String strSQL = "SELECT "
                                +KEY_QOUTA_ID+","
                                +KEY_EMP_ID+","
                                +KEY_MEM_GENDER+","
                                +KEY_MEM_NAME+","
                                +KEY_MEM_LASTNAME+","
                                +KEY_ZONE+","
                                +KEY_HOUSE_GEO+","
                                +KEY_HOUSE_GEOX+","
                                +KEY_HOUSE_GEOY+","
                                +KEY_AD_TAMBOL+","
                                +KEY_AD_AMPHER+","
                                +KEY_AD_PROVINCE+
                                " FROM " + DATABASE_TABLE +
                                         " Where "+KEY_HOUSE_GEOX+" <= "+ Max_X+" AND "+KEY_HOUSE_GEOX+" >= "+ Min_X
                                         +" AND  "+KEY_HOUSE_GEOY+" <= "+ Max_Y+" AND  "+KEY_HOUSE_GEOY+" >= "+ Min_Y
                                         +" ";


            Log.d("Get_Val_strSQL", String.valueOf(strSQL));

             Cursor mCursor = db.rawQuery(strSQL, null);

            if (mCursor != null) {
                mCursor.moveToFirst();
                if (mCursor.moveToFirst()){
                   do{
                       sMemberDrawPolygonsHouse cMember = new sMemberDrawPolygonsHouse();
                       cMember.sQOUTA_ID(mCursor.getString(0).toString());
                       cMember.sEMP_ID(mCursor.getString(1).toString());
                       cMember.sMEM_GENDER(mCursor.getString(2).toString());

                       cMember.sMEM_NAME(mCursor.getString(3).toString());
                       cMember.sMEM_LASTNAME(mCursor.getString(4).toString());
                       cMember.sZONE(mCursor.getString(5).toString());
                       cMember.sHOUSE_GEO(mCursor.getString(6).toString());
                            Log.d("strSQL_Polygon", mCursor.getString(6).toString());
                       cMember.sHOUSE_GEOX(mCursor.getString(7).toString());
                       cMember.sHOUSE_GEOY(mCursor.getString(8).toString());
                       cMember.sAD_TAMBOL(mCursor.getString(9).toString());
                       cMember.sAD_AMPHER(mCursor.getString(10).toString());
                       cMember.sAD_PROVINCE(mCursor.getString(11).toString());

                       MemberList.add(cMember);
                   }while(mCursor.moveToNext());
                }



            }
            mCursor.close();
            return MemberList;

    } catch (Exception e) {
        // TODO: handle exception
        return null;
    }

}

public class sMemberCalloutDatasHouse {

    String  _QOUTA_ID,
            _EMP_ID,
            _EMP_NAME,
            _MEM_GENDER,
            _MEM_NAME,
            _MEM_LASTNAME,
            _MEM_TEL,
            _ZONE,
            _HOUSE_GEO,
            _HOUSE_GEOX,
            _HOUSE_GEOY,
            _HOUSE_LATITUDE,
            _HOUSE_LONGITUDE,
            _AD_TAMBOL,
            _AD_AMPHER,
            _AD_PROVINCE;



    // Set Value
    public void sQOUTA_ID(String vQOUTA_ID){
        this._QOUTA_ID= vQOUTA_ID;
    }
    // Set Value
    public void sEMP_ID(String vEMP_ID){
        this._EMP_ID = vEMP_ID;
    }
    public void sEMP_NAME(String vEMP_NAME){
        this._EMP_NAME = vEMP_NAME;
    }
    // Set Value
    public void sMEM_GENDER(String vMEM_GENDER){
        this._MEM_GENDER = vMEM_GENDER;
    }
    // Set Value
    public void sMEM_NAME(String vMEM_NAME){
        this._MEM_NAME = vMEM_NAME;
    }
    // Set Value
    public void sMEM_LASTNAME(String vMEM_LASTNAME){
        this._MEM_LASTNAME = vMEM_LASTNAME;
    }
    // Set Value
    public void sMEM_TEL(String vMEM_TEL){
        this._MEM_TEL = vMEM_TEL;
    }
    // Set Value
    public void sZONE(String vZONE){
        this._ZONE = vZONE;
    }
    // Set Value
    public void sHOUSE_GEO(String vHOUSE_GEO){
        this._HOUSE_GEO = vHOUSE_GEO;
    }

    // Set Value
    public void sHOUSE_GEOX(String vHOUSE_GEOX){
        this._HOUSE_GEOX = vHOUSE_GEOX;
    }
    // Set Value
    public void sHOUSE_GEOY(String vHOUSE_GEOY){
        this._HOUSE_GEOY = vHOUSE_GEOY;
    }
    // Set Value
    public void sHOUSE_LATITUDE(String vHOUSE_LATITUDE){
        this._HOUSE_LATITUDE= vHOUSE_LATITUDE;
    }
    // Set Value
    public void sHOUSE_LONGITUDE(String vHOUSE_LONGTITUDE){
        this._HOUSE_LONGITUDE = vHOUSE_LONGTITUDE;
    }
    // Set Value
    public void sAD_TAMBOL(String vAD_TAMBOL){
        this._AD_TAMBOL = vAD_TAMBOL;
    }

    // Set Value
    public void sAD_AMPHER(String vAD_AMPHER){
        this._AD_AMPHER = vAD_AMPHER;
    }
    // Set Value
    public void sAD_PROVINCE(String vAD_PROVINCE){
        this._AD_PROVINCE = vAD_PROVINCE;
    }




    // -------------------------------------


    // Get Value
    public String gQOUTA_ID(){
        return _QOUTA_ID;
    }

    // Get Value
    public String gEMP_ID(){
        return _EMP_ID;
    }

    public String gEMP_NAME(){
        return _EMP_NAME;
    }

    // Get Value
    public String gMEM_GENDER(){
        return _MEM_GENDER;
    }

    // Get Value
    public String gMEM_NAME(){
        return _MEM_NAME;
    }

    // Get Value
    public String gMEM_LASTNAME(){
        return _MEM_LASTNAME;
    }

    public String gMEM_TEL(){
        return _MEM_TEL;
    }



    // Get Value
    public String gZONE(){
        return _ZONE;
    }
    // Get Value
    public String gHOUSE_GEO(){
        return _HOUSE_GEO;
    }

    // Get Value
    public String gHOUSE_GEOX(){
        return _HOUSE_GEOX;
    }
    // Get Value
    public String gHOUSE_GEOY(){
        return _HOUSE_GEOY;
    }
    // Get Value
    public String gHOUSE_LATITUDE(){
        return _HOUSE_LATITUDE;
    }
    // Get Value
    public String gHOUSE_LONGITUDE(){
        return _HOUSE_LONGITUDE;
    }
    // Get Value
    public String gAD_TAMBOL(){
        return _AD_TAMBOL;
    }
    // Get Value
    public String gAD_AMPHER(){
        return _AD_AMPHER;
    }
    // Get Value
    public String gAD_PROVINCE(){
        return _AD_PROVINCE;
    }

}

public List<sMemberCalloutDatasHouse> getCaneCalloutHouse(String QOUTA_ID_KEY, SQLiteDatabase db) throws SQLException
{

            try {
                List<sMemberCalloutDatasHouse> MemberList = new ArrayList<sMemberCalloutDatasHouse>();

                 String strSQL =  "SELECT "
                            +KEY_QOUTA_ID+","
                             +KEY_EMP_ID+","
                             +KEY_EMP_NAME+","
                            +KEY_MEM_GENDER+","
                            +KEY_MEM_NAME+","
                            +KEY_MEM_LASTNAME+","
                             +KEY_MEM_TEL+","
                            +KEY_ZONE+","
                            +KEY_HOUSE_GEO+","
                            +KEY_HOUSE_GEOX+","
                            +KEY_HOUSE_GEOY+","
                            +KEY_HOUSE_LATITUDE+","
                            +KEY_HOUSE_LONGITUDE+","
                            +KEY_AD_TAMBOL+","
                            +KEY_AD_AMPHER+","
                            +KEY_AD_PROVINCE
                            +" FROM " + DATABASE_TABLE +
                             " Where "+KEY_QOUTA_ID+" = "+ QOUTA_ID_KEY;

                     Log.d("getCaneCallout_SQL", strSQL);
                     Cursor mCursor = db.rawQuery(strSQL, null);

                    if (mCursor != null) {
                        mCursor.moveToFirst();
                        if (mCursor.moveToFirst()){
                           do{
                               sMemberCalloutDatasHouse cMember = new sMemberCalloutDatasHouse();
                               cMember.sQOUTA_ID(mCursor.getString(0).toString());
                               cMember.sEMP_ID(mCursor.getString(1).toString());
                               cMember.sEMP_NAME(mCursor.getString(2).toString());
                               cMember.sMEM_GENDER(mCursor.getString(3).toString());
                               cMember.sMEM_NAME(mCursor.getString(4).toString());
                               cMember.sMEM_LASTNAME(mCursor.getString(5).toString());
                               cMember.sMEM_TEL(mCursor.getString(6).toString());
                               cMember.sZONE(mCursor.getString(7).toString());
                               cMember.sHOUSE_GEO(mCursor.getString(8).toString());
                               cMember.sHOUSE_GEOX(mCursor.getString(9).toString());
                               cMember.sHOUSE_GEOY(mCursor.getString(10).toString());
                               cMember.sHOUSE_LATITUDE(mCursor.getString(11).toString());
                               cMember.sHOUSE_LONGITUDE(mCursor.getString(12).toString());
                               cMember.sAD_TAMBOL(mCursor.getString(13).toString());
                               cMember.sAD_AMPHER(mCursor.getString(14).toString());
                               cMember.sAD_PROVINCE(mCursor.getString(15).toString());

                               MemberList.add(cMember);
                           }while(mCursor.moveToNext());
                        }

                    }
                    mCursor.close();
                   return MemberList;


            } catch (Exception e) {
                // TODO: handle exception
                return null;
            }
}

}