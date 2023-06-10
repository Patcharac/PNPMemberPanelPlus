package com.panelplus.pnpmember.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by DELL on 24/06/60.
 */

public class ExternalStorage_Farm_DB_OpenHelper {
    private SQLiteDatabase database;
    private File dbFile;
    private SQLiteDatabase.CursorFactory factory;

    public static final String KEY_qoutaID = "qouta_ID";
    public static final String KEY_Farm_ID = "Farm_ID";
    public static final String KEY__id = "_id";

    private static final String TABLE_FARM_DATA = "farm_data";
    private static final String TABLE_OBJECT_DATA = "object_see";

    public ExternalStorage_Farm_DB_OpenHelper(String dbFileName){
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

    public long insertDataFarmer(String qouta_ID,
                                 String Emp_ID,
                                 String Farm_NO,
                                 String Farm_Date,
                                 String Farm_Category,
                                 String Farm_CategoryNo,
                                 String Farm_LocalName,
                                 String Farm_Tambol,
                                 String Farm_Aumpher,
                                 String Farm_Province,
                                 float Farm_AreaRai,
                                 float Farm_AreaNgan,
                                 float Farm_AreaWa,

                                 String Rubber_Breed,
                                 int Rubber_PerRai,
                                 int yearPlant,
                                 String Farm_Status,

                                 byte[] Pic_OwnLand,
                                 byte[] Pic_Map1,

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
                                 SQLiteDatabase db) throws SQLException{

        //---------calculate Area Total-----------
        Float AreaTotal = ((Farm_AreaRai*1600)+(Farm_AreaNgan*400)+(Farm_AreaWa*4))/1600;
        DecimalFormat AreaTotal2digit = new DecimalFormat("#.##");
        Float Farm_AreaTotal = Float.valueOf(AreaTotal2digit.format(AreaTotal));
        String Farm_AreaTotalString = String.valueOf(Farm_AreaTotal);


        String Farm_ID = qouta_ID+Farm_NO;

        int yearNow,Rubber_YearOld;


        DateFormat df = new SimpleDateFormat("yyyy");
        yearNow = Integer.parseInt(df.format(Calendar.getInstance().getTime()))+543;
        Rubber_YearOld = yearNow-yearPlant;


        if(Farm_Status != "R"){
            if (Rubber_YearOld <20){
                Farm_Status = "L20";
            } else if(Rubber_YearOld >= 20){
                Farm_Status = "m20";
            }
        }



        try {
            ContentValues Con = new ContentValues();
            Con.put("Farm_ID",Farm_ID);
            Con.put("qouta_ID",qouta_ID);
            Con.put("Emp_ID",Emp_ID);
            Con.put("Farm_NO",Farm_NO);
            Con.put("Farm_Date",Farm_Date);
            Con.put("Farm_Category",Farm_Category);
            Con.put("Farm_CategoryNo",Farm_CategoryNo);
            Con.put("Farm_LocalName",Farm_LocalName);
            Con.put("Farm_Tambol",Farm_Tambol);
            Con.put("Farm_Aumpher",Farm_Aumpher);
            Con.put("Farm_Province",Farm_Province);
            Con.put("Farm_AreaRai",Farm_AreaRai);
            Con.put("Farm_AreaNgan",Farm_AreaNgan);
            Con.put("Farm_AreaWa",Farm_AreaWa);
            Con.put("Farm_AreaTotal",Farm_AreaTotalString);
            Con.put("Rubber_Breed",Rubber_Breed);
            Con.put("Rubber_PerRai",Rubber_PerRai);
            Con.put("Rubber_YearOfPlant", yearPlant);
            Con.put("Rubber_YearOld",Rubber_YearOld);
            Con.put("Farm_Status",Farm_Status);

            Con.put("Pic_OwnLand",Pic_OwnLand);
            Con.put("Pic_Map1",Pic_Map1);

            Con.put("DirNorth_Use",DirNorth_Use);
            Con.put("DirNorth_Owner",DirNorth_Owner);
            Con.put("DirNorth_Tel",DirNorth_Tel);

            Con.put("DirEast_Use",DirEast_Use);
            Con.put("DirEast_Owner",DirEast_Owner);
            Con.put("DirEast_Tel",DirEast_Tel);

            Con.put("DirSouth_Use", DirSouth_Use);
            Con.put("DirSouth_Owner", DirSouth_Owner);
            Con.put("DirSouth_Tel", DirSouth_Tel);

            Con.put("DirWest_Use",DirWest_Use);
            Con.put("DirWest_Owner",DirWest_Owner);
            Con.put("DirWest_Tel",DirWest_Tel);


            for(int i=0;i<evaFarmString.length;i++){
                Con.put("EvaFarm_NO"+(i+1),evaFarmString[i]);
                Con.put("EvaFarmSuggest_NO"+(i+1),evaFarmSuggestString[i]);
            }

            long rows = db.insert(TABLE_FARM_DATA, null, Con);

            return rows;
        }catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }

    }

    public long insertDataObject(String Farm_ID,
                                 String Obj_LocalName,
                                 String Obj_Status,
                                 SQLiteDatabase db) throws SQLException{

        try{
            ContentValues Con = new ContentValues();
            Con.put("Farm_ID",Farm_ID);
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

            long rows =  db.delete(TABLE_OBJECT_DATA, KEY_Farm_ID+" = ?",
                    new String[] { Farm_ID });
            return rows;
        } catch (Exception e) {
            // TODO: handle exception
            return -1;
        }
    }

    public String[] SelectData(String _id, SQLiteDatabase db) throws SQLException
    {
        String arrData[] = null;

        Cursor cursor =
                db.query(true, TABLE_FARM_DATA, new String[] {
                                "*"
                        },
                        KEY__id  + "=?",
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
                arrData[5] = cursor.getString(7);
                arrData[6] = cursor.getString(8);
            }
        }

        cursor.close();

        return arrData;
    }



}
