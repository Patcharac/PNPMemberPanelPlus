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
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by DELL on 06/07/60.
 */

public class ExternalStorage_Visit_DB_OpenHelper {
    private SQLiteDatabase database;
    private File dbFile;
    private SQLiteDatabase.CursorFactory factory;

    public static final String KEY__id = "_id";
    public static final String KEY_Farm_ID = "Farm_ID";

    private static final String TABLE_VISIT_MEMBER_DATA = "visit_member_data";
    private static final String TABLE_VISIT_FARM_DATA = "visit_farm_data";
    private static final String TABLE_REC_RESULT_DATA = "rec_result_data";
    private static final String TABLE_GIRTH_DATA = "girth_data";
    private static final String TABLE_OBJECT_DATA = "object_see";

    public ExternalStorage_Visit_DB_OpenHelper(Context context, String dbFileName) {
        String folderName = "MapDB"; // folder name
        String appDbDir = context.getApplicationContext().getFilesDir() + "/" + folderName; // folder path

        File folder = new File(appDbDir); // create folder
        if (!folder.exists()) {
            folder.mkdirs(); // create folder if it doesn't exist
        }

        this.dbFile = new File(folder, dbFileName);
        Log.e("Check", "dbFile Check Data : " +dbFile);
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
        if (database != null) {
            database.close();
            database = null;
        }
    }

    public SQLiteDatabase getReadableDatabase() {

        return getDatabase();
    }

    public SQLiteDatabase getWritableDatabase() {
        return getDatabase();
    }

    private SQLiteDatabase getDatabase() {
        if (database == null) {
            open();
        }
        return database;
    }

    public long insertDataVisitMember(String Visit_Date,
                                      String qouta_ID,
                                      String Emp_ID,
                                      String[] VisitMem_NO,
                                      String[] VisitMemSuggest_NO,
                                      SQLiteDatabase db) throws SQLException {

        try {
            ContentValues Con = new ContentValues();
            Con.put("VisitMem_Date", Visit_Date);
//            Con.put("mem_Name",mem_Name);
            Con.put("qouta_ID", qouta_ID);
            Con.put("Emp_ID", Emp_ID);


            for (int i = 0; i < 11; i++) {
                Con.put("VisitMem_NO" + (i + 1), VisitMem_NO[i]);
                Con.put("VisitMemSuggest_NO" + (i + 1), VisitMemSuggest_NO[i]);
            }


            long rows = db.insert(TABLE_VISIT_MEMBER_DATA, null, Con);
            return rows;
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }

    public long insertDataVisitFarm(String VisitFarm_Date,
                                    String Farm_ID,
                                    String Emp_ID,
                                    byte[] Farm_Pic,
                                    int rubberYearOfPlant,
                                    int yearNow,
                                    String rubberBreed,
                                    String[] VisitFarm_NO,
                                    String[] VisitFarmSuggest_NO,
                                    SQLiteDatabase db) throws SQLException {

        int rubberYearOld = yearNow - rubberYearOfPlant;

        try {
            ContentValues Con = new ContentValues();
            Con.put("VisitFarm_Date", VisitFarm_Date);
            Con.put("Farm_Pic", Farm_Pic);
            Con.put("Farm_ID", Farm_ID);
            Con.put("Emp_ID", Emp_ID);
            Con.put("rubberYearOfPlant", rubberYearOfPlant);
            Con.put("rubberYearOld", rubberYearOld);
            Con.put("rubberBreed", rubberBreed);


            for (int i = 0; i < 9; i++) {
                Con.put("VisitFarm_NO" + (i + 1), VisitFarm_NO[i]);
                Con.put("VisitFarmSuggest_NO" + (i + 1), VisitFarmSuggest_NO[i]);
            }


            long rows = db.insert(TABLE_VISIT_FARM_DATA, null, Con);
            return rows;
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }

    public long insertDataObject(String Farm_ID,
                                 String Obj_LocalName,
                                 String Obj_Status,
                                 SQLiteDatabase db) throws SQLException {

        try {
            ContentValues Con = new ContentValues();
            Con.put("Farm_ID", Farm_ID);
            Con.put("Obj_LocalName", Obj_LocalName);
            Con.put("Obj_Status", Obj_Status);

            long rows = db.insert(TABLE_OBJECT_DATA, null, Con);
            return rows;
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }

    public long insertRecResult(String Rec_Date,
                                String Farm_ID,
                                Float Farm_AreaRai,
                                Float Farm_AreaNgan,
                                Float Farm_AreaWa,
                                String Farm_AreaAll,
                                String Rec_PlantRows,
                                String Rec_PlantTree,
                                int Rec_RubberAll,
                                int Rec_RubberAlive,
                                String Rec_RubberAlivePercent,
                                int Rec_RubberDeath,
                                String Rec_RubberDeathPercent,
                                String Rec_RubPerRai,
                                String Rec_RubPerRaiAlive,
                                String Rec_AvgPerTree,
                                String Rec_AvgPerRai,
                                String Rec_AvgPerPlot,
                                String Rec_AvgTrunk,
                                String Rec_AvgBranch,
                                String Rec_Status,
                                String Rec_Note,
                                SQLiteDatabase db) throws SQLException {

        try {
            ContentValues Con = new ContentValues();
            Con.put("Rec_Date", Rec_Date);
            Con.put("Farm_ID", Farm_ID);
            Con.put("Farm_AreaRai", Farm_AreaRai);
            Con.put("Farm_AreaNgan", Farm_AreaNgan);
            Con.put("Farm_AreaWa", Farm_AreaWa);
            Con.put("Farm_AreaAll", Farm_AreaAll);
            Con.put("Rec_PlantRows", Rec_PlantRows);
            Con.put("Rec_PlantTree", Rec_PlantTree);
            Con.put("Rec_RubberAll", Rec_RubberAll);
            Con.put("Rec_RubberAlive", Rec_RubberAlive);
            Con.put("Rec_RubberAlivePercent", Rec_RubberAlivePercent);
            Con.put("Rec_RubberDeath", Rec_RubberDeath);
            Con.put("Rec_RubberDeathPercent", Rec_RubberDeathPercent);
            Con.put("Rec_RubPerRai", Rec_RubPerRai);
            Con.put("Rec_RubPerRaiAlive", Rec_RubPerRaiAlive);
            Con.put("Rec_AvgPerTree", Rec_AvgPerTree);
            Con.put("Rec_AvgPerRai", Rec_AvgPerRai);
            Con.put("Rec_AvgPerPlot", Rec_AvgPerPlot);
            Con.put("Rec_AvgTrunk", Rec_AvgTrunk);
            Con.put("Rec_AvgBranch", Rec_AvgBranch);
            Con.put("Rec_Status", Rec_Status);
            Con.put("Rec_Note", Rec_Note);

            long rows = db.insert(TABLE_REC_RESULT_DATA, null, Con);
            return rows;
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }

    public long insertGirth(String Gir_Date,
                            String Farm_ID,
                            String Gir_NO,
                            Float Gir_Girth,
                            double Gir_kgPerTree,
                            String Gir_Status,
                            SQLiteDatabase db) throws SQLException {

        try {
            DecimalFormat kgPerTree2digit = new DecimalFormat("#.##");
            String kgPerTreeString = String.valueOf(Float.valueOf(kgPerTree2digit.format(Gir_kgPerTree)));

            ContentValues Con = new ContentValues();
            Con.put("Gir_Date", Gir_Date);
            Con.put("Farm_ID", Farm_ID);
            Con.put("Gir_NO", Gir_NO);
            Con.put("Gir_Girth", Gir_Girth);
            Con.put("Gir_kgPerTree", kgPerTreeString);
            Con.put("Gir_Status", Gir_Status);

            long rows = db.insert(TABLE_GIRTH_DATA, null, Con);
            return rows;
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }

    public long DeleteDataObject(String Farm_ID, SQLiteDatabase db) throws SQLException {

        try {

            long rows = db.delete(TABLE_OBJECT_DATA, KEY_Farm_ID + " = ?",
                    new String[]{Farm_ID});
            return rows;
        } catch (Exception e) {
            // TODO: handle exception
            return -1;
        }
    }

    public String[] SelectDataVisitMember(String _id, SQLiteDatabase db) throws SQLException {
        String arrData[] = null;

        try {
            Cursor cursor =
                    db.query(true, TABLE_VISIT_MEMBER_DATA, new String[]{
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
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return null;
        }
        return arrData;
    }

    public int getRowCount() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_VISIT_FARM_DATA;
        Cursor cursor = db.rawQuery(query, null);

        int rowCount = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                rowCount = cursor.getInt(0);
            }
            cursor.close();
        }

        return rowCount;
    }

    public String[] SelectDataVisitFarm(String _id, SQLiteDatabase db) throws SQLException {
        String arrData[] = null;

        try {
            Cursor cursor = db.query(true, TABLE_VISIT_FARM_DATA, new String[]{"*"}, KEY__id + "=?", new String[]{_id}, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getColumnCount()];
                    arrData[0] = cursor.getString(1);
                    arrData[1] = cursor.getString(2);
                    arrData[2] = cursor.getString(3);
//                    arrData[3] = cursor.getString(4);
//                    arrData[4] = cursor.getString(5);
//                    arrData[5] = cursor.getString(7);
//                    arrData[6] = cursor.getString(8);
                }
            }

            cursor.close();
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return null;
        }
        Log.e("Check", "arrData : " + arrData[0].toString());
        Log.e("Check", "arrData2 : " + arrData[1].toString());
        Log.e("Check", "arrData3 : " + arrData[2].toString());
//        Log.e("Check", "arrData4 : " + arrData[3].toString());
//        Log.e("Check", "arrData5 : " + arrData[4].toString());
//        Log.e("Check", "arrData6 : " + arrData[5].toString());
        return arrData;
    }

//    public String[] SelectDataVisitFarm(String _id, SQLiteDatabase db) throws SQLException {
//        String[] arrData = null;
//
//        try {
//            Cursor cursor = db.query(TABLE_VISIT_FARM_DATA, null, KEY__id + "=?", new String[]{_id}, null, null, null);
//            if (cursor != null) {
//                int rowCount = cursor.getCount();
//                int columnCount = cursor.getColumnCount();
//                if (rowCount > 0 && columnCount > 0) {
//                    arrData = new String[rowCount * columnCount];
//                    int dataIndex = 0;
//                    while (cursor.moveToNext()) {
//                        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
//                            arrData[dataIndex] = cursor.getString(columnIndex);
//                            dataIndex++;
//                        }
//                    }
//                }
//                cursor.close();
//            }
//        } catch (Exception e) {
//            Log.d("SQL_Insert", String.valueOf(e));
//            return null;
//        }
//        Log.e("Check","arrData : " + arrData.length);
//        return arrData;
//    }


//    public String[] SelectDataVisitFarm(String _id, SQLiteDatabase db) throws SQLException {
//        String[] arrData = null;
//
//        try {
//            Cursor cursor = db.query(TABLE_VISIT_FARM_DATA, null, KEY__id + "=?", new String[]{_id}, null, null, null);
//            if (cursor != null) {
//                int rowCount = cursor.getCount();
//                int columnCount = cursor.getColumnCount();
//                if (rowCount > 0 && columnCount > 0) {
//                    arrData = new String[rowCount * columnCount];
//                    int dataIndex = 0;
//                    while (cursor.moveToNext()) {
//                        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
//                            arrData[dataIndex] = cursor.getString(columnIndex);
//                            dataIndex++;
//                        }
//                    }
//                }
//                cursor.close();
//            }
//        } catch (Exception e) {
//            Log.d("SQL_Insert", String.valueOf(e));
//            return null;
//        }
//        return arrData;
//    }


    public String[] SelectDataRecCircum(String _id, SQLiteDatabase db) throws SQLException {
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
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return null;
        }
        return arrData;
    }
}
