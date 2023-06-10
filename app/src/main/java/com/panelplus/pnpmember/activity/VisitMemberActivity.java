package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Visit_DB_OpenHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class VisitMemberActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button btnSearch, btnSave;
    private EditText mem_Name, qouta_ID;

    private CheckBox[] VisitMem_NO = new CheckBox[11];
    private String[] VisitMemString = new String[11];

    private EditText[] VisitMemSuggest_NO = new EditText[11];
    private String[] VisitMemSuggestString = new String[11];
    private int[] checkSuggest = new int[11];
    private int[] checkTextSuggest = new int[11];

    File Path = null;
    private String Emp_ID = "";
    private String UserZone = "";

    private String dateNow;

    //-----------DB-----------//
    SQLiteDatabase mydb = null;

    private String DBFile;
    SQLiteDatabase db;

    @SuppressLint("SdCardPath")
    String DATABASE_FILE_PATH = "/sdcard/MapDB/";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    private static final String DATABASE_FSCGISDATANAME = "FSCGISDATA";


    private static final String DATABASE_VISIT_DB = "VISIT_DB";
    private static final String TABLE_VISIT_MEMBER_DATA = "visit_member_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_member);

        InitWidget();


        //get user
        Emp_ID = getEmpID();
        UserZone = getUserZone();

        creatDB(this);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 543;

        dateNow = new SimpleDateFormat("dd-MM",
                Locale.getDefault()).format(new Date());

        dateNow = dateNow + "-" + String.valueOf(year);
        Log.d("date", String.valueOf(dateNow));

    }

    private void creatDB(Context context) {

        if (context != null) {
            String folderName = "MapDB";
            String folderPath = context.getApplicationContext().getFilesDir() + "/" + folderName;
            File appDbDir = new File(folderPath);
            if (!appDbDir.exists()) {
                appDbDir.mkdirs();
            }
            this.Path = appDbDir;

            String databaseName = DATABASE_VISIT_DB + "-" + UserZone + ".sqlite";
            String databasePath = folderPath + "/" + databaseName;

            try {
                this.mydb = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

                String createTableQuery = "CREATE TABLE IF NOT EXISTS visit_member_data (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "   VisitMem_Date TEXT ," +
                        "   qouta_ID TEXT ," +
                        "   Emp_ID TEXT ," +
                        "   VisitMem_NO1 TEXT," +
                        "   VisitMemSuggest_NO1 TEXT ," +
                        "   VisitMem_NO2 TEXT ," +
                        "   VisitMemSuggest_NO2  ," +
                        "   VisitMem_NO3 TEXT ," +
                        "   VisitMemSuggest_NO3 TEXT ," +
                        "   VisitMem_NO4 TEXT ," +
                        "   VisitMemSuggest_NO4 TEXT ," +
                        "   VisitMem_NO5 TEXT ," +
                        "   VisitMemSuggest_NO5 TEXT ," +
                        "   VisitMem_NO6 TEXT ," +
                        "   VisitMemSuggest_NO6 TEXT ," +
                        "   VisitMem_NO7 TEXT ," +
                        "   VisitMemSuggest_NO7 TEXT ," +
                        "   VisitMem_NO8 TEXT ," +
                        "   VisitMemSuggest_NO8 TEXT ," +
                        "   VisitMem_NO9 TEXT ," +
                        "   VisitMemSuggest_NO9 TEXT ," +
                        "   VisitMem_NO10 TEXT ," +
                        "   VisitMemSuggest_NO10 TEXT ," +
                        "   VisitMem_NO11 TEXT ," +
                        "   VisitMemSuggest_NO11 TEXT );";


                this.mydb.execSQL(createTableQuery);
                Log.e("Check", "Database created  user_member_data successfully");
                Toast.makeText(context, "Database created successfully!", Toast.LENGTH_SHORT).show();

                // Check the path of the created table
                String tablePath = this.mydb.getPath();
                Log.e("Check", "Table path: " + tablePath);
            } catch (Exception e) {
                Log.e("Check", "Failed to create database: " + e.getMessage());
                Toast.makeText(context, "Failed to create database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            // handle null context case
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                searchData(this);
                break;

            case R.id.save:

                for (int i = 0; i < 11; i++) {
                    VisitMemString[i] = getValueCheckbox(VisitMem_NO[i]);

                    VisitMemSuggestString[i] = VisitMemSuggest_NO[i].getText().toString();
                    if (VisitMemSuggestString[i].isEmpty()) {
                        VisitMemSuggestString[i] = "";
                    }

                    if (checkSuggest[i] == 1 && VisitMemSuggestString[i] == "") {
                        checkTextSuggest[i] = 0;
                    } else {
                        checkTextSuggest[i] = 1;
                    }

                }

                if (mem_Name.getText().toString().isEmpty() ||
                        qouta_ID.getText().toString().isEmpty() ||
                        checkTextSuggest[6] == 0 || checkTextSuggest[7] == 0 ||
                        checkTextSuggest[8] == 0 || checkTextSuggest[10] == 0) {

                    if (mem_Name.getText().toString().isEmpty() ||
                            qouta_ID.getText().toString().isEmpty()) {

                        Toast.makeText(getApplicationContext(),
                                        "กรุณากรอกชื่อและรหัสสมาชิกก่อนการบันทึก", Toast.LENGTH_SHORT)
                                .show();
                    }

                    if (checkTextSuggest[6] == 0 || checkTextSuggest[7] == 0 ||
                            checkTextSuggest[8] == 0 || checkTextSuggest[10] == 0) {
                        Toast.makeText(getApplicationContext(),
                                        "กรุณกรอกคำแนะนำที่จำเป็นก่อนการบันทึก", Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {
                    beforeSave(this);
                }
                break;
        }

    }

    private void searchData(Context context){
        if (!qouta_ID.getText().toString().isEmpty()) {

            SQLiteDatabase db;
            String DBFile = DATABASE_FSCGISDATANAME + ".sqlite";

            Log.d("getCaneCallout_DBFile", "01");
            ExternalStorage_Farm_GIS_DB_OpenHelper obj = new ExternalStorage_Farm_GIS_DB_OpenHelper(context, DBFile);
            if (obj.databaseFileExists()) {

                db = obj.getReadableDatabase();
                String Name = obj.GetNameFromQouta(qouta_ID.getText().toString(), db);
                Log.d("NameSearch", Name);

//                if (Name == null || String.valueOf(Name).trim().equals("[]")) {
                    if (Name == null) {
                    Toast.makeText(getApplicationContext(), "ไม่พบรหัสสมาชิกนี้", Toast.LENGTH_SHORT).show();
                    Log.e("Check","Check User : " + "ไม่พบรหัสสมาชิกนี้");

                } else {
                    mem_Name.setText(Name);
                }

                obj.close();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                            "กรุณากรอกรหัสสมาชิกก่อนการค้นหา", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void InitWidget() {
        btnSave = (Button) findViewById(R.id.save);
        btnSave.setOnClickListener(this);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        mem_Name = (EditText) findViewById(R.id.Member_Name);
        qouta_ID = (EditText) findViewById(R.id.qouta_ID);

        VisitMem_NO[0] = (CheckBox) findViewById(R.id.VisitMem_NO1);
        VisitMem_NO[1] = (CheckBox) findViewById(R.id.VisitMem_NO2);
        VisitMem_NO[2] = (CheckBox) findViewById(R.id.VisitMem_NO3);
        VisitMem_NO[3] = (CheckBox) findViewById(R.id.VisitMem_NO4);
        VisitMem_NO[4] = (CheckBox) findViewById(R.id.VisitMem_NO5);
        VisitMem_NO[5] = (CheckBox) findViewById(R.id.VisitMem_NO6);
        VisitMem_NO[6] = (CheckBox) findViewById(R.id.VisitMem_NO7);
        VisitMem_NO[7] = (CheckBox) findViewById(R.id.VisitMem_NO8);
        VisitMem_NO[8] = (CheckBox) findViewById(R.id.VisitMem_NO9);
        VisitMem_NO[9] = (CheckBox) findViewById(R.id.VisitMem_NO10);
        VisitMem_NO[10] = (CheckBox) findViewById(R.id.VisitMem_NO11);

        VisitMem_NO[6].setOnCheckedChangeListener(this);
        VisitMem_NO[7].setOnCheckedChangeListener(this);
        VisitMem_NO[8].setOnCheckedChangeListener(this);
        VisitMem_NO[10].setOnCheckedChangeListener(this);

        VisitMemSuggest_NO[0] = (EditText) findViewById(R.id.VisitMemSuggest_NO1);
        VisitMemSuggest_NO[1] = (EditText) findViewById(R.id.VisitMemSuggest_NO2);
        VisitMemSuggest_NO[2] = (EditText) findViewById(R.id.VisitMemSuggest_NO3);
        VisitMemSuggest_NO[3] = (EditText) findViewById(R.id.VisitMemSuggest_NO4);
        VisitMemSuggest_NO[4] = (EditText) findViewById(R.id.VisitMemSuggest_NO5);
        VisitMemSuggest_NO[5] = (EditText) findViewById(R.id.VisitMemSuggest_NO6);
        VisitMemSuggest_NO[6] = (EditText) findViewById(R.id.VisitMemSuggest_NO7);
        VisitMemSuggest_NO[7] = (EditText) findViewById(R.id.VisitMemSuggest_NO8);
        VisitMemSuggest_NO[8] = (EditText) findViewById(R.id.VisitMemSuggest_NO9);
        VisitMemSuggest_NO[9] = (EditText) findViewById(R.id.VisitMemSuggest_NO10);
        VisitMemSuggest_NO[10] = (EditText) findViewById(R.id.VisitMemSuggest_NO11);

    }

    public void beforeSave(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VisitMemberActivity.this);
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

    private void savetoSQlite(Context context) {


        this.DBFile = DATABASE_VISIT_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Visit_DB_OpenHelper obj = new ExternalStorage_Visit_DB_OpenHelper(context, DBFile);

        if (obj.databaseFileExists()) {
            this.mydb = obj.getWritableDatabase(); // เปิดฐานข้อมูลให้เป็นแบบเขียนได้
            long check = -1;
            try {
                this.mydb.beginTransaction();
                check = obj.insertDataVisitMember(dateNow,
                        qouta_ID.getText().toString().trim(),
                        Emp_ID,
                        VisitMemString,
                        VisitMemSuggestString,
                        mydb);

                mydb.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("Check", "Failed to insert data into database: " + e.getMessage());
                Toast.makeText(context, "Failed to insert data into database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                mydb.endTransaction();
                obj.close();
            }

            if (check != -1) {
                Toast.makeText(getApplicationContext(), "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                finish();
//                startActivity(new Intent(this, EvaMemberActivity.class));
            } else {
                Log.e("Check", "บันทึกข้อมูลล้มเหลว");
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.VisitMem_NO7:
                if (isChecked) {
                    VisitMemSuggest_NO[6].setEnabled(true);
                    checkSuggest[6] = 1;
                } else {
                    VisitMemSuggest_NO[6].setEnabled(false);
                    VisitMemSuggest_NO[6].setText("");
                    checkSuggest[6] = 0;
                }
                break;

            case R.id.VisitMem_NO8:
                if (isChecked) {
                    VisitMemSuggest_NO[7].setEnabled(true);
                    checkSuggest[7] = 1;
                } else {
                    VisitMemSuggest_NO[7].setEnabled(false);
                    VisitMemSuggest_NO[7].setText("");
                    checkSuggest[7] = 0;
                }
                break;

            case R.id.VisitMem_NO9:
                if (isChecked) {
                    VisitMemSuggest_NO[8].setEnabled(true);
                    checkSuggest[8] = 1;
                } else {
                    VisitMemSuggest_NO[8].setEnabled(false);
                    VisitMemSuggest_NO[8].setText("");
                    checkSuggest[8] = 0;
                }
                break;

            case R.id.VisitMem_NO11:
                if (isChecked) {
                    VisitMemSuggest_NO[10].setEnabled(true);
                    checkSuggest[10] = 1;
                } else {
                    VisitMemSuggest_NO[10].setEnabled(false);
                    VisitMemSuggest_NO[10].setText("");
                    checkSuggest[10] = 0;
                }
                break;

        }
    }

    private String getValueCheckbox(CheckBox VisitMem_NO) {
        if (VisitMem_NO.isChecked()) {
            return "1";
        }
        return "0";
    }

    private String getEmpID() {
        SQLiteDatabase db;
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(getApplicationContext(), DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getReadableDatabase();
            Emp_ID = obj.GetEmpID("1", db);
            obj.close();

            return Emp_ID;
        }
        return "NULL";
    }

    private String getUserZone() {
        SQLiteDatabase db;
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(getApplicationContext(), DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getReadableDatabase();
            UserZone = obj.LoginData("1", db);
            obj.close();

            return UserZone;
        }
        return "NULL";
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VisitMemberActivity.this);
        builder.setMessage("ต้องการจะออกจากการบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
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
