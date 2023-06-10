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

public class ExternalStorage_Edit_Farm_DB_OpenHelper {
    private SQLiteDatabase database;
    private File dbFile;
    private SQLiteDatabase.CursorFactory factory;

    public static final String KEY_ID = "_id";

    private static final String TABLE_EDIT_FARM_DATA = "edit_farm_data";

    public ExternalStorage_Edit_Farm_DB_OpenHelper(Context context, String dbFileName) {
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

    public long InsertDataEditFarmGeoLocation(String Farm_ID,
                                              String Emp_ID,
                                              String EditTab_Date,
                                              String EditTab_Area,
                                              String EditTab_Polygon,
                                              String EditTab_Note,
                                              String EditTab_Status,
                                              byte [] Farm_Pic,
                                              String Farm_PlantDate,
                                              String Farm_YearOfPlant,
                                              String Farm_RubberBreed,
                                              String Farm_RubberPerRai,
                                              SQLiteDatabase db) throws SQLException  {

        try {

            ContentValues Val = new ContentValues();

            Val.put("Farm_ID", Farm_ID);
            Val.put("Emp_ID", Emp_ID);
            Val.put("EditTab_Date", EditTab_Date);
            Val.put("EditTab_Area", EditTab_Area);
            Val.put("EditTab_Polygon", EditTab_Polygon);
            Val.put("EditTab_Note" ,EditTab_Note);
            Val.put("EditTab_Status",EditTab_Status);
            Val.put("Farm_Pic",Farm_Pic);
            Val.put("Farm_PlantDate", Farm_PlantDate);
            Val.put("Farm_YearOfPlant", Farm_YearOfPlant);
            Val.put("Farm_RubberBreed" ,Farm_RubberBreed);
            Val.put("Farm_RubberPerRai",Farm_RubberPerRai);


            long rows = db.insert(TABLE_EDIT_FARM_DATA, null, Val);

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
                db.query(true, TABLE_EDIT_FARM_DATA, new String[] {"*"},
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

            long rows =  db.delete(TABLE_EDIT_FARM_DATA, KEY_ID+" = ?",
                    new String[] { String.valueOf(ID) });
            return rows;
        } catch (Exception e) {
            // TODO: handle exception
            return -1;
        }
    }

}
