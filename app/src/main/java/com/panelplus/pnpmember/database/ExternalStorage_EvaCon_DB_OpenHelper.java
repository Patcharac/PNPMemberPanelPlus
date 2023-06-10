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
 * Created by DELL on 30/06/60.
 */

public class ExternalStorage_EvaCon_DB_OpenHelper {
    private SQLiteDatabase database;
    private File dbFile;
    private SQLiteDatabase.CursorFactory factory;

    public static final String KEY__id = "_id";

    private static final String TABLE_EVA_CONTRACTOR_DATA = "eva_contractor_data";
    private static final String TABLE_EVA_CON_BEFCUT_DATA = "eva_con_befcut_data";
    private static final String TABLE_EVA_CON_CUTTING_DATA = "eva_con_cutting_data";
    private static final String TABLE_EVA_CON_AFTCUT_DATA = "eva_con_aftcut_data";


    public ExternalStorage_EvaCon_DB_OpenHelper(Context context, String dbFileName){
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

    public long insertDataEvaContractor(String EvaCon_Date,
                                        String Con_Name,
                                        String Con_ID,
                                        String Farm_ID,
                                        String Emp_ID,
                                        String [] EvaConString,
                                        byte [] Pic_LicenseSaw,
                                        byte [] Pic_LicenseCar,
                                 SQLiteDatabase db) throws SQLException{

        try {
            ContentValues Con = new ContentValues();
            Con.put("EvaCon_Date",EvaCon_Date);
            Con.put("Con_Name",Con_Name);
            Con.put("Con_ID",Con_ID);
            Con.put("Farm_ID",Farm_ID);
            Con.put("Emp_ID",Emp_ID);
            Con.put("Pic_LicenseSaw",Pic_LicenseSaw);
            Con.put("Pic_LicenseCar",Pic_LicenseCar);

            for(int i=0;i<EvaConString.length;i++){
                Con.put("EvaCon_NO"+(i+1),EvaConString[i]);
            }

            long rows = db.insert(TABLE_EVA_CONTRACTOR_DATA, null, Con);
            return rows;
        }catch (Exception e) {
        Log.d("SQL_Insert", String.valueOf(e));
        return -1;
        }

    }

    public long insertDataEvaConBefCut(String EvaCon_Date,
                                       String Con_Name,
                                       String Con_ID,
                                       String Farm_ID,
                                       String Emp_ID,
                                       String[] EvaBefCutString,
                                       String[] EvaBefCutSuggestString,
                                       SQLiteDatabase db) throws SQLException{

        try {
            ContentValues Con = new ContentValues();
            Con.put("EvaBefCut_Date",EvaCon_Date);
            Con.put("Con_Name",Con_Name);
            Con.put("Con_ID",Con_ID);
            Con.put("Farm_ID",Farm_ID);
            Con.put("Emp_ID",Emp_ID);


            for(int i=0;i<EvaBefCutString.length;i++){
                Con.put("EvaBefCut_NO"+(i+1),EvaBefCutString[i]);
            }

            for(int i=0; i<EvaBefCutSuggestString.length;i++){
                Con.put("EvaBefCutSuggest_NO"+(i+1),EvaBefCutSuggestString[i]);
            }

            long rows = db.insert(TABLE_EVA_CON_BEFCUT_DATA , null, Con);
            return rows;
        }catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }

    }

    public long insertDataEvaConCutting(String EvaCon_Date,
                                       String Con_Name,
                                       String Con_ID,
                                       String Farm_ID,
                                       String Emp_ID,
                                       String Working_Step,
                                       String[] EvaCuttingString,
                                       String[] EvaCuttingSuggestString,
                                       SQLiteDatabase db) throws SQLException{

        try {
            ContentValues Con = new ContentValues();
            Con.put("EvaCutting_Date",EvaCon_Date);
            Con.put("Con_Name",Con_Name);
            Con.put("Con_ID",Con_ID);
            Con.put("Farm_ID",Farm_ID);
            Con.put("Emp_ID",Emp_ID);
            Con.put("Working_Step",Working_Step);

            for(int i=0;i<EvaCuttingString.length;i++){
                Con.put("EvaCutting_NO"+(i+1),EvaCuttingString[i]);
            }

            for(int i=0; i<EvaCuttingSuggestString.length;i++){
                Con.put("EvaCuttingSuggest_NO"+(i+1),EvaCuttingSuggestString[i]);
            }

            long rows = db.insert(TABLE_EVA_CON_CUTTING_DATA , null, Con);
            return rows;
        }catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }

    }

    public long insertDataEvaConAftCut(String EvaCon_Date,
                                        String Con_Name,
                                        String Con_ID,
                                        String Farm_ID,
                                        String Emp_ID,
                                        String[] EvaAftCutString,
                                        String[] EvaAftCutSuggestString,
                                        SQLiteDatabase db) throws SQLException{

        try {
            ContentValues Con = new ContentValues();
            Con.put("EvaAftCut_Date",EvaCon_Date);
            Con.put("Con_Name",Con_Name);
            Con.put("Con_ID",Con_ID);
            Con.put("Farm_ID",Farm_ID);
            Con.put("Emp_ID",Emp_ID);

            for(int i=0;i<EvaAftCutString.length;i++){
                Con.put("EvaAftCut_NO"+(i+1),EvaAftCutString[i]);
                Con.put("EvaAftCutSuggest_NO"+(i+1),EvaAftCutSuggestString[i]);
            }


            long rows = db.insert(TABLE_EVA_CON_AFTCUT_DATA , null, Con);
            return rows;
        }catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }

    }

    public String[] SelectDataContractor(String _id, SQLiteDatabase db) throws SQLException
    {
        String arrData[] = null;

        try {
            Cursor cursor =
                    db.query(true, TABLE_EVA_CONTRACTOR_DATA, new String[]{
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

    public String[] SelectDataConBefCut(String _id, SQLiteDatabase db) throws SQLException
    {
        String arrData[] = null;

        try {
            Cursor cursor =
                    db.query(true, TABLE_EVA_CON_BEFCUT_DATA, new String[]{
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

    public String[] SelectDataConCutting(String _id, SQLiteDatabase db) throws SQLException
    {
        String arrData[] = null;

        try {
            Cursor cursor =
                    db.query(true, TABLE_EVA_CON_CUTTING_DATA, new String[]{
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

    public String[] SelectDataConAftCut(String _id, SQLiteDatabase db) throws SQLException
    {
        String arrData[] = null;

        try {
            Cursor cursor =
                    db.query(true, TABLE_EVA_CON_AFTCUT_DATA, new String[]{
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
