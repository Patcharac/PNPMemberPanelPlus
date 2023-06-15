package com.panelplus.pnpmember.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.widget.Toast;

import com.panelplus.pnpmember.activity.EvaMemberActivity;

import java.io.File;

import androidx.appcompat.app.AlertDialog;

public class ExternalStorage_Member_DB_OpenHelper {
    public static final String KEY__id = "_id";
    private static final String TABLE_EVA_MEMBER_DATA = "eva_member_data";
    private static final String TABLE_USER_MEMBER_DATA = "user_member_data";
    private SQLiteDatabase database;
    private File dbFile;
    private SQLiteDatabase.CursorFactory factory = null;
    private static final int DATABASE_VERSION = 1;

    public ExternalStorage_Member_DB_OpenHelper(Context context, String dbFileName) {
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
    public long InsertDataRegisMember(String mem_Date,
                                      String mem_gender,
                                      String mem_Name,
                                      String mem_LastName,
                                      String mem_Tel,
                                      String mem_DateOfBirth,
                                      String mem_IDcard,
                                      String Ad_No,
                                      String Ad_Mooban,
                                      String Ad_MoobanName,
                                      String Ad_Tambol,
                                      String Ad_Ampher,
                                      String Ad_Province,
                                      String Ad_IDpost,
                                      String mem_Email,
                                      String mem_MemberFarm,
                                      String mem_OccuPrime,
                                      String mem_OccuSecond,
                                      String mem_Graduate,
                                      byte[] attach_IDcard,
                                      byte[] attach_HouseRegis,
                                      byte[] attach_Authorise,
//                                      byte[] attach_FaceRegister,
                                      byte[] attach_AccountBank,
                                      byte[] attech_Signature,
//                                      byte[] attach_Signature,
                                      String Acc_Num,
                                      String Acc_Bank,
                                      String Acc_Branc,
                                      SQLiteDatabase mydb) throws SQLException {
        Log.e("Check", "Check db insert : " + mydb);
        Log.e("Check", "Check db attach_IDcard : " + attach_IDcard);
        Log.e("Check", "Check db attach_HouseRegis : " + attach_HouseRegis);
        Log.e("Check", "Check db attach_Authorise : " + attach_Authorise);
//        Log.e("Check", "Check db attach_Authorise : " + attach_FaceRegister);
        Log.e("Check", "Check db attach_AccountBank : " + attach_AccountBank);
        Log.e("Check", "Check db attach_Signature : " + attech_Signature);

        try {
            ContentValues values = new ContentValues();

            values.put("mem_Date", mem_Date);
            values.put("mem_gender", mem_gender);
            values.put("mem_Name", mem_Name);
            values.put("mem_LastName", mem_LastName);
            values.put("mem_Tel", mem_Tel);
            values.put("mem_DateOfBirth", mem_DateOfBirth);
            values.put("mem_IDcard", mem_IDcard);
            values.put("Ad_No", Ad_No);
            values.put("Ad_Mooban", Ad_Mooban);
            values.put("Ad_MoobanName", Ad_MoobanName);
            values.put("Ad_Tambol", Ad_Tambol);
            values.put("Ad_Ampher", Ad_Ampher);
            values.put("Ad_Province", Ad_Province);
            values.put("Ad_IDpost", Ad_IDpost);
            values.put("mem_Email", mem_Email);
            values.put("mem_MemberFarm", mem_MemberFarm);
            values.put("mem_OccuPrime", mem_OccuPrime);
            values.put("mem_OccuSecond", mem_OccuSecond);
            values.put("mem_Graduate", mem_Graduate);

            if (attach_IDcard != null) {
                values.put("attach_IDcard", attach_IDcard);
            }
            if (attach_HouseRegis != null) {
                values.put("attach_HouseRegis", attach_HouseRegis);
            }
            if (attach_Authorise != null) {
                values.put("attach_Authorise", attach_Authorise);
            }

//            if (attach_FaceRegister != null) {
//                values.put("attach_FaceRegister", attach_FaceRegister);
//            }
            if (attach_AccountBank != null) {
                values.put("attach_AccountBank", attach_AccountBank);
            }
            if (attech_Signature != null) {
                values.put("attech_Signature", attech_Signature);
            }

            values.put("Acc_Num", Acc_Num);
            values.put("Acc_Bank", Acc_Bank);
            values.put("Acc_Branc", Acc_Branc);

            long rows = mydb.insert("user_member_data", null, values);
            return rows;
        } catch (Exception e) {
            Log.e("Check", "Failed to insert data into database: " + e.getMessage());
            return -1;
        }
    }


    public long InsertDataEvaMember(String EvaMem_Date, String[] EvaMem_NO, String[] EvaMemSuggest, SQLiteDatabase db) throws SQLException {
        try {
            ContentValues Con = new ContentValues();
            int sizeCheckbox = EvaMem_NO.length;
            int sizeEdittext = EvaMemSuggest.length;
            Con.put("EvaMem_Date", EvaMem_Date);
            for (int i = 0; i < sizeCheckbox; i++) {
                Con.put("EvaMem_NO" + (i + 1), EvaMem_NO[i]);
            }
            for (int i2 = 0; i2 < sizeEdittext; i2++) {
                Con.put("EvaMemSuggest_NO" + (i2 + 1), EvaMemSuggest[i2]);
            }
            return db.insert(TABLE_EVA_MEMBER_DATA, (String) null, Con);
        } catch (Exception e) {
            Log.d("SQL_Insert", String.valueOf(e));
            return -1;
        }
    }

    private void open() {
        if (dbFile.exists()) {
            database = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), factory, SQLiteDatabase.OPEN_READWRITE);
        } else {
            database = SQLiteDatabase.openOrCreateDatabase(dbFile, factory);
            Log.e("Check","Check create : " + database);
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
        } else {
        }

        return database;
    }


    public void close() {
        if (database != null) {
            database.close();
            database = null;
        }
    }







    public String[] SelectData(String _id, SQLiteDatabase db) throws SQLException {
        String arrData[] = null;

        Cursor cursor = db.query(true, TABLE_USER_MEMBER_DATA, new String[] {"*"},null,null,null,null,null,null);
        if(cursor != null)
        {
            if (cursor != null && cursor.moveToFirst()) {
                arrData = new String[cursor.getColumnCount()];
                arrData[0] = cursor.getString(1);
                arrData[1] = cursor.getString(2);
                arrData[2] = cursor.getString(3);
                arrData[3] = cursor.getString(4);
                arrData[4] = cursor.getString(5);
                arrData[5] = cursor.getString(6);
                arrData[6] = cursor.getString(7);
            }
        }

        cursor.close();
        return arrData;



    }



    public long DeleteDataMember(SQLiteDatabase db) throws SQLException {
        try {
            return (long) db.delete(TABLE_USER_MEMBER_DATA, "_id=(SELECT MAX(_id) FROM user_member_data)", (String[]) null);
        } catch (Exception e) {
            return -1;
        }
    }
}
