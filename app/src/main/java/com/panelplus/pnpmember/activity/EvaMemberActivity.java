package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Member_DB_OpenHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EvaMemberActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSave;

    CheckBox[] EvaMem_NO = new CheckBox[11];
    String[] EvaMemString = new String[11];
    EditText[] EvaMemSuggest = new EditText[13];
    String[] EvaMemSuggestString = new String[13];

    SQLiteDatabase mydb = null;

    private String DBFile;
    SQLiteDatabase db;
    File Path = null;
    @SuppressLint("SdCardPath")
    String DATABASE_FILE_PATH = "/sdcard/MapDB/";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";

    private static final String DATABASE_REGIS_DB = "REGIS_DB";
    private static final String TABLE_EVA_MEMBER_DATA = "eva_member_data";

    String Emp_ID = "";
    String UserZone = "";

    private String dateNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eva_member);

        InitWidget();
        //get user
        Emp_ID = getEmpID(this);
        UserZone = getUserZone(this);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 543;

        dateNow = new SimpleDateFormat("dd-MM",
                Locale.getDefault()).format(new Date());

        dateNow = dateNow + "-" + String.valueOf(year);
        Log.d("date", String.valueOf(dateNow));

        Create_Primary_DB(this);

    }

    private void Create_Primary_DB(Context context) {

        if (context != null) {
            String folderName = "MapDB";
            String folderPath = context.getApplicationContext().getFilesDir() + "/" + folderName;
            File appDbDir = new File(folderPath);
            if (!appDbDir.exists()) {
                appDbDir.mkdirs();
            }
            this.Path = appDbDir;


            String databaseName = DATABASE_REGIS_DB + "-" + this.UserZone + ".sqlite";
            String databasePath = folderPath + "/" + databaseName;
            Log.e("Check", "databasePath : " + databasePath);
            try {
                this.mydb = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                String createTableQuery = "CREATE TABLE IF NOT EXISTS eva_member_data (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "   EvaMem_Date TEXT ," +
                        "   EvaMem_NO1 TEXT ," +
                        "   EvaMemSuggest_NO1 TEXT ," +
                        "   EvaMem_NO2 TEXT ," +
                        "   EvaMemSuggest_NO2 TEXT," +
                        "   EvaMem_NO3 TEXT ," +
                        "   EvaMemSuggest_NO3 TEXT ," +
                        "   EvaMem_NO4 TEXT ," +
                        "   EvaMemSuggest_NO4 TEXT ," +
                        "   EvaMem_NO5 TEXT ," +
                        "   EvaMemSuggest_NO5 TEXT ," +
                        "   EvaMem_NO6 TEXT ," +
                        "   EvaMemSuggest_NO6 TEXT ," +
                        "   EvaMem_NO7 TEXT ," +
                        "   EvaMemSuggest_NO7 TEXT ," +
                        "   EvaMem_NO8 TEXT ," +
                        "   EvaMemSuggest_NO8 TEXT ," +
                        "   EvaMem_NO9 TEXT ," +
                        "   EvaMemSuggest_NO9 TEXT ," +
                        "   EvaMem_NO10 TEXT ," +
                        "   EvaMemSuggest_NO10 TEXT ," +
                        "   EvaMem_NO11 TEXT ," +
                        "   EvaMemSuggest_NO11 TEXT ," +
                        "   EvaMemSuggest_NO12 TEXT ," +
                        "   EvaMemSuggest_NO13 TEXT );";


                this.mydb.execSQL(createTableQuery);
                Log.e("Check", "Database created successfully");
                Toast.makeText(context, "Database created successfully!", Toast.LENGTH_SHORT).show();

                // Check the path of the created table
                String tablePath = this.mydb.getPath();
                Log.e("Check", "Table path Eva : " + createTableQuery);


            } catch (Exception e) {
                Log.e("Check", "Failed to create database: " + e.getMessage());
                Toast.makeText(context, "Failed to create database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            // handle null context case
        }

    }


    private void InitWidget() {

        btnSave = (Button) findViewById(R.id.save);

        btnSave.setOnClickListener(this);

        EvaMem_NO[0] = ((CheckBox) findViewById(R.id.EvaMem_NO1));
        EvaMem_NO[1] = ((CheckBox) findViewById(R.id.EvaMem_NO2));
        EvaMem_NO[2] = ((CheckBox) findViewById(R.id.EvaMem_NO3));
        EvaMem_NO[3] = ((CheckBox) findViewById(R.id.EvaMem_NO4));
        EvaMem_NO[4] = ((CheckBox) findViewById(R.id.EvaMem_NO5));
        EvaMem_NO[5] = ((CheckBox) findViewById(R.id.EvaMem_NO6));
        EvaMem_NO[6] = ((CheckBox) findViewById(R.id.EvaMem_NO7));
        EvaMem_NO[7] = ((CheckBox) findViewById(R.id.EvaMem_NO8));
        EvaMem_NO[8] = ((CheckBox) findViewById(R.id.EvaMem_NO9));
        EvaMem_NO[9] = ((CheckBox) findViewById(R.id.EvaMem_NO10));
        EvaMem_NO[10] = ((CheckBox) findViewById(R.id.EvaMem_NO11));

        EvaMemSuggest[0] = (EditText) findViewById(R.id.EvaMemSuggest_NO1);
        EvaMemSuggest[1] = (EditText) findViewById(R.id.EvaMemSuggest_NO2);
        EvaMemSuggest[2] = (EditText) findViewById(R.id.EvaMemSuggest_NO3);
        EvaMemSuggest[3] = (EditText) findViewById(R.id.EvaMemSuggest_NO4);
        EvaMemSuggest[4] = (EditText) findViewById(R.id.EvaMemSuggest_NO5);
        EvaMemSuggest[5] = (EditText) findViewById(R.id.EvaMemSuggest_NO6);
        EvaMemSuggest[6] = (EditText) findViewById(R.id.EvaMemSuggest_NO7);
        EvaMemSuggest[7] = (EditText) findViewById(R.id.EvaMemSuggest_NO8);
        EvaMemSuggest[8] = (EditText) findViewById(R.id.EvaMemSuggest_NO9);
        EvaMemSuggest[9] = (EditText) findViewById(R.id.EvaMemSuggest_NO10);
        EvaMemSuggest[10] = (EditText) findViewById(R.id.EvaMemSuggest_NO11);
        EvaMemSuggest[11] = (EditText) findViewById(R.id.EvaMemSuggest_NO12);
        EvaMemSuggest[12] = (EditText) findViewById(R.id.EvaMemSuggest_NO13);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:

                for (int i = 0; i < 11; i++) {
                    EvaMemString[i] = getValueCheckbox(EvaMem_NO[i]);
                }

                for (int i = 0; i < 13; i++) {

                    EvaMemSuggestString[i] = EvaMemSuggest[i].getText().toString().trim();

                    if (EvaMemSuggestString[i].isEmpty()) {
                        EvaMemSuggestString[i] = "";
                    }
                }
                beforeSave(this);
                break;
        }
    }


    private String getUserZone(Context context) {
        SQLiteDatabase db;
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context, DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getReadableDatabase();
            UserZone = obj.LoginData("1", db);
            obj.close();

            return UserZone;
        }
        return "NULL";
    }

    private String getEmpID(Context context) {
        SQLiteDatabase db;
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context, DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getReadableDatabase();
            Emp_ID = obj.GetEmpID("1", db);
            obj.close();

            return Emp_ID;
        }
        return "NULL";
    }

    public void savetoSQlite(Context context) {
      String DBFile = DATABASE_REGIS_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Member_DB_OpenHelper obj5 = new ExternalStorage_Member_DB_OpenHelper(context, DBFile);
        this.mydb = obj5.getWritableDatabase();
        long check = obj5.InsertDataEvaMember(dateNow.trim(), EvaMemString, EvaMemSuggestString, mydb);
        obj5.close();

        if (check != -1) {
            Log.e("Check","savetoSQlite Eva : " + "บันทึกข้อมูลเสร็จสิ้น");
            Toast.makeText(getApplicationContext(), "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            Toast.makeText(getApplicationContext(), "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteLastRow() {
        DBFile = DATABASE_REGIS_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Member_DB_OpenHelper obj6 = new ExternalStorage_Member_DB_OpenHelper(getApplicationContext(), DBFile);
        db = obj6.getReadableDatabase();
        long check = obj6.DeleteDataMember(db);
        obj6.close();

        if (check < 0) {
            Toast.makeText(getApplicationContext(),
                            "ไม่สามารถกลับสู่หน้าหลักได้กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT)
                    .show();
            finish();

        } else {
            Toast.makeText(getApplicationContext(),
                            "กลับสู่หน้าหลัก", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private String getValueCheckbox(CheckBox EvaMem_NO) {
        if (EvaMem_NO.isChecked()) {
            return "1";
        }
        return "0";
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaMemberActivity.this);
        builder.setMessage("ต้องการจะออกจากการบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteLastRow();
                finish();
            }
        });
        builder.setNegativeButton("ไม่ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void beforeSave(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaMemberActivity.this);
        builder.setMessage("ต้องการจะบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                savetoSQlite(context);
            }
        });
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
