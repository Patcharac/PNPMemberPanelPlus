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
 * Created by DELL on 06/07/60.
 */

public class ExternalStorage_Rec_Girth_DB_OpenHelper {
    private SQLiteDatabase database;
    private File dbFile;
    private SQLiteDatabase.CursorFactory factory;

    public static final String KEY__id = "_id";
    public static final String KEY_Farm_ID = "Farm_ID";

    private static final String TABLE_REC_RESULT_DATA = "rec_result_data";
    private static final String TABLE_GIRTH_DATA = "girth_data";

    public ExternalStorage_Rec_Girth_DB_OpenHelper(Context context, String dbFileName){
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


    public long insertRecResult(String Link_ID,
                                String Rec_Date,
                                String Farm_ID,
                                String Emp_ID,
                                String Farm_AreaAll,
                                String Rec_PlantRows,
                                String Rec_PlantTree,
                                String Rec_Status,
                                String Rec_Note,
                                SQLiteDatabase db) throws SQLException{

        try{
            ContentValues Con = new ContentValues();
            Con.put("Link_ID",Link_ID);
            Con.put("Rec_Date",Rec_Date);
            Con.put("Farm_ID",Farm_ID);
            Con.put("Emp_ID",Emp_ID);
            Con.put("Farm_AreaAll",Farm_AreaAll);
            Con.put("Rec_PlantRows",Rec_PlantRows);
            Con.put("Rec_PlantTree",Rec_PlantTree);
            Con.put("Rec_Status",Rec_Status);
            Con.put("Rec_Note",Rec_Note);

            long rows = db.insert(TABLE_REC_RESULT_DATA , null, Con);
            return rows;
        }catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }

    public long insertGirth(String Link_ID,
                            String Gir_Date,
                            String Farm_ID,
                            String Gir_NO,
                            Float Gir_Girth,
                            SQLiteDatabase db) throws SQLException{

        try{
            ContentValues Con = new ContentValues();
            Con.put("Link_ID",Link_ID);
            Con.put("Gir_Date",Gir_Date);
            Con.put("Farm_ID",Farm_ID);
            Con.put("Gir_NO",Gir_NO);
            Con.put("Gir_Girth",Gir_Girth);

            long rows = db.insert(TABLE_GIRTH_DATA , null, Con);
            return rows;
        }catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }




    public String[] SelectDataRecCircum(String _id, SQLiteDatabase db) throws SQLException
    {
        String arrData[] = null;

        try {
            Cursor cursor =
                    db.query(true, TABLE_REC_RESULT_DATA, new String[]{
                                    "*"
                            },
                            KEY__id + "=?",
                            new String[]{_id},
                            null,
                            null,
                            null,
                            null);
            if (cursor != null) {
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
        }catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return null;
        }
        return arrData;
    }
}
