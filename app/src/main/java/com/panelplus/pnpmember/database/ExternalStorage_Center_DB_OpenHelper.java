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

public class ExternalStorage_Center_DB_OpenHelper {
    private static final String DATABASE_TABLE_LOGIN = "user_login_data";
    public static final String KEY_Department = "Emp_Department";
    public static final String KEY_EmpID = "Emp_ID";
    public static final String KEY_EmpName = "Emp_Name";
    public static final String KEY_Zone_Name = "Zone_Name";
    public static final String KEY__id = "_id";
    private SQLiteDatabase database;
    private Context context;
    private File dbFile;
    private SQLiteDatabase.CursorFactory factory;

//    public ExternalStorage_Center_DB_OpenHelper(Context context,String dbFileName) {
//        File appDbDir = context.getDir("MapDB", Context.MODE_PRIVATE);
//        this.dbFile = new File(appDbDir, dbFileName);
//    }
public ExternalStorage_Center_DB_OpenHelper(Context context, String dbFileName) {

    String folderName = "MapDB"; // folder name
    String folder = context.getApplicationContext().getFilesDir() + "/" + folderName; // folder path
    File appDbDir = new File(folder); // create folder
    if (!appDbDir.exists()) {
        appDbDir.mkdirs(); // create folder if it doesn't exist
    }

    this.dbFile = new File(folder, dbFileName);
    Log.e("Check","this.dbFile : " + this.dbFile);
}


    public boolean databaseFileExists() {
            return this.dbFile.exists();
    }

    public long InsertDataEmployeeLogin(String Emp_ID,
                                        String Emp_IDname,
                                        String Emp_Name,
                                        String Emp_NameEng,
                                        String Emp_Department,
                                        String Emp_Post,
                                        String Zone_Name,
                                        SQLiteDatabase db) throws SQLException {
        try {
            ContentValues Con = new ContentValues();
            Con.put("Emp_ID",Emp_ID);
            Con.put("Emp_IDname",Emp_IDname);
            Con.put("Emp_Name",Emp_Name);
            Con.put("Emp_NameEng",Emp_NameEng);
            Con.put("Emp_Department",Emp_Department);
            Con.put("Emp_Post",Emp_Post);
            Con.put("Zone_Name",Zone_Name);

            long rows = db.insert(DATABASE_TABLE_LOGIN, null, Con);

            return rows; // return rows inserted.

        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }

    }

    public long InsertJSON(String s, SQLiteDatabase db) throws SQLException {
        try {
            ContentValues con = new ContentValues();
            con.put("Qouta_ID", s);
            return db.insert(DATABASE_TABLE_LOGIN, (String) null, con);
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }

    private void open() {
        if (dbFile.exists()) {
            database = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), factory, SQLiteDatabase.OPEN_READWRITE);
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

    public String GetEmpName(String _id, SQLiteDatabase db) throws SQLException
    {
        String data="";
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_LOGIN , new String[] {
                                KEY_EmpName
                        },
                        KEY__id + "=?",
                        new String[]{_id}, null, null,
                        null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            if (mCursor.moveToFirst()){
                do{
                    data = mCursor.getString(0);
                    // do what ever you want here
                }while(mCursor.moveToNext());
            }
            mCursor.close();
        }
        return data;
    }

    public String LoginData(String _id, SQLiteDatabase db) throws SQLException
    {
        String data="";
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_LOGIN , new String[] {
                                KEY_Zone_Name
                        },
                        KEY__id + "=?",
                        new String[]{_id},
                        null,
                        null,
                        null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            if (mCursor.moveToFirst()){
                do{
                    data = mCursor.getString(0);
                    // do what ever you want here
                }while(mCursor.moveToNext());
            }
            mCursor.close();
        }
        return data;
    }

    public String GetEmpID(String _id, SQLiteDatabase db) throws SQLException
    {
        String data="";
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_LOGIN , new String[] {
                                KEY_EmpID
                        },
                        KEY__id + "=?",
                        new String[]{_id},
                        null,
                        null,
                        null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            if (mCursor.moveToFirst()){
                do{
                    data = mCursor.getString(0);
                    // do what ever you want here
                }while(mCursor.moveToNext());
            }
            mCursor.close();
        }
        return data;
    }

    public long DeleteData(String ID, SQLiteDatabase db) throws SQLException   {

        try {

            long rows =  db.delete(DATABASE_TABLE_LOGIN, KEY__id +" = ?",
                    new String[] { String.valueOf(ID) });
            return rows;
        } catch (Exception e) {
            // TODO: handle exception
            return -1;
        }
    }
}
