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

/**
 * Created by FSC on 17/7/2561.
 */

public class ExternalStorage_Coor_House_OpenHelper {
    private SQLiteDatabase database;
    private File dbFile;
    private SQLiteDatabase.CursorFactory factory;

    public static final String KEY_ID = "_id";

    private static final String TABLE_COOR_HOUSE_DATA = "coor_house_data";

    public ExternalStorage_Coor_House_OpenHelper(Context context, String dbFileName) {
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

    public long InsertDataHouseGeoLocation(String qouta_ID,
                                              String Emp_ID,
                                              String House_Date,
                                              String House_Geo,
                                              String House_GeoX,
                                              String House_GeoY,
                                              String House_Latitude,
                                              String House_Longitude,


                                              SQLiteDatabase db) throws SQLException  {

        try {

            ContentValues Val = new ContentValues();

            Val.put("qouta_ID", qouta_ID);
            Val.put("Emp_ID", Emp_ID);
            Val.put("House_Date", House_Date);
            Val.put("House_GeoX", House_GeoX);
            Val.put("House_GeoY", House_GeoY);
            Val.put("House_Latitude" ,House_Latitude);
            Val.put("House_Longitude",House_Longitude);
            Val.put("House_Geo",House_Geo);

            long rows = db.insert(TABLE_COOR_HOUSE_DATA, null, Val);

            return rows; // return rows inserted.
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }



    public String[] SelectData(String _id, SQLiteDatabase db) throws SQLException
    {
        String arrData[] = null;

        Cursor cursor =
                db.query(true, TABLE_COOR_HOUSE_DATA, new String[] {
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

            long rows =  db.delete(TABLE_COOR_HOUSE_DATA, KEY_ID+" = ?",
                    new String[] { String.valueOf(ID) });
            return rows;
        } catch (Exception e) {
            // TODO: handle exception
            return -1;
        }
    }

}
