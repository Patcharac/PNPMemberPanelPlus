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
import com.panelplus.pnpmember.database.ExternalStorage_EvaCon_DB_OpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EvaConAftCutActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSave;

    private EditText Con_Name, Con_ID, Farm_ID;

    private CheckBox[] EvaAftCut_NO = new CheckBox[10];
    private String[] EvaAftCutString = new String[10];

    private EditText[] EvaAftCutSuggest_NO = new EditText[10];
    private String[] EvaAftCutSuggestString = new String[10];

    private String Emp_ID = "";
    private String UserZone = "";

    private String dateNow;

    //===============DB================//
    SQLiteDatabase mydb = null;

    private String DBFile;
    SQLiteDatabase db;

    @SuppressLint("SdCardPath")
    String DATABASE_FILE_PATH = "/sdcard/MapDB/";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";

    private static final String DATABASE_EVACON_DB = "EVACON_DB";
    private static final String TABLE_EVA_CON_AFTCUT_DATA = "eva_con_aftcut_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eva_con_aft_cut);

        InitWidget();

        //get user
        Emp_ID = getEmpID();
        UserZone = getUserZone();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 543;

        dateNow = new SimpleDateFormat("dd-MM", Locale.getDefault()).format(new Date());

        dateNow = dateNow + "-" + String.valueOf(year);
        Log.d("date", String.valueOf(dateNow));
        //--------create DB-SQlite---------
        /*
        mydb = this.openOrCreateDatabase(DATABASE_FILE_PATH + File.separator
                + DATABASE_EVACON_DB + "-" + UserZone + ".sqlite", Context.MODE_PRIVATE, null);

        mydb.execSQL(" create table if not exists " + TABLE_EVA_CON_AFTCUT_DATA +
                "   (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "   EvaAftCut_Date TEXT ," +
                "   Con_Name TEXT ," +
                "   Con_ID TEXT ," +
                "   Farm_ID TEXT ," +
                "   Emp_ID TEXT ," +
                "   EvaAftCut_NO1 TEXT ," +
                "   EvaAftCutSuggest_NO1 TEXT ," +
                "   EvaAftCut_NO2 TEXT ," +
                "   EvaAftCutSuggest_NO2 TEXT ," +
                "   EvaAftCut_NO3 TEXT ," +
                "   EvaAftCutSuggest_NO3 TEXT ," +
                "   EvaAftCut_NO4 TEXT ," +
                "   EvaAftCutSuggest_NO4 TEXT ," +
                "   EvaAftCut_NO5 TEXT ," +
                "   EvaAftCutSuggest_NO5 TEXT ," +
                "   EvaAftCut_NO6 TEXT ," +
                "   EvaAftCutSuggest_NO6 TEXT ," +
                "   EvaAftCut_NO7 TEXT ," +
                "   EvaAftCutSuggest_NO7 TEXT ," +
                "   EvaAftCut_NO8 TEXT ," +
                "   EvaAftCutSuggest_NO8 TEXT ," +
                "   EvaAftCut_NO9 TEXT ," +
                "   EvaAftCutSuggest_NO9 TEXT ," +
                "   EvaAftCut_NO10 TEXT ," +
                "   EvaAftCutSuggest_NO10 TEXT );" );

              */

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:

                for (int i = 0; i < 10; i++) {
                    EvaAftCutString[i] = getValueCheckbox(EvaAftCut_NO[i]);

                    EvaAftCutSuggestString[i] = EvaAftCutSuggest_NO[i].getText().toString().trim();
                    if (EvaAftCutSuggestString[i].isEmpty()) {
                        EvaAftCutSuggestString[i] = "";
                    }
                }

                if (Con_Name.getText().toString().isEmpty() ||
                        Con_ID.getText().toString().isEmpty() ||
                        Farm_ID.getText().toString().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "กรุณกรอกข้อมูลให้ครบถ้วนก่อนการบันทึก", Toast.LENGTH_SHORT).show();
                } else {
                    beforeSave(this);
                }

                break;
        }
    }


    private void InitWidget() {
        btnSave = (Button) findViewById(R.id.save);
        btnSave.setOnClickListener(this);

        Con_ID = (EditText) findViewById(R.id.Con_ID);
        Con_Name = (EditText) findViewById(R.id.Con_Name);
        Farm_ID = (EditText) findViewById(R.id.Farm_ID);

        EvaAftCut_NO[0] = (CheckBox) findViewById(R.id.EvaAftCut_NO1);
        EvaAftCut_NO[1] = (CheckBox) findViewById(R.id.EvaAftCut_NO2);
        EvaAftCut_NO[2] = (CheckBox) findViewById(R.id.EvaAftCut_NO3);
        EvaAftCut_NO[3] = (CheckBox) findViewById(R.id.EvaAftCut_NO4);
        EvaAftCut_NO[4] = (CheckBox) findViewById(R.id.EvaAftCut_NO5);
        EvaAftCut_NO[5] = (CheckBox) findViewById(R.id.EvaAftCut_NO6);
        EvaAftCut_NO[6] = (CheckBox) findViewById(R.id.EvaAftCut_NO7);
        EvaAftCut_NO[7] = (CheckBox) findViewById(R.id.EvaAftCut_NO8);
        EvaAftCut_NO[8] = (CheckBox) findViewById(R.id.EvaAftCut_NO9);
        EvaAftCut_NO[9] = (CheckBox) findViewById(R.id.EvaAftCut_NO10);

        EvaAftCutSuggest_NO[0] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO1);
        EvaAftCutSuggest_NO[1] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO2);
        EvaAftCutSuggest_NO[2] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO3);
        EvaAftCutSuggest_NO[3] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO4);
        EvaAftCutSuggest_NO[4] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO5);
        EvaAftCutSuggest_NO[5] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO6);
        EvaAftCutSuggest_NO[6] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO7);
        EvaAftCutSuggest_NO[7] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO8);
        EvaAftCutSuggest_NO[8] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO9);
        EvaAftCutSuggest_NO[9] = (EditText) findViewById(R.id.EvaAftCutSuggest_NO10);

    }


    public void beforeSave(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaConAftCutActivity.this);
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
        this.DBFile = DATABASE_EVACON_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_EvaCon_DB_OpenHelper obj = new ExternalStorage_EvaCon_DB_OpenHelper(context, DBFile);
        if (obj.databaseFileExists()) {
            this.mydb = obj.getWritableDatabase(); // เปิดฐานข้อมูลให้เป็นแบบเขียนได้
            long check = -1;
            try {
                this.mydb.beginTransaction();
                check = obj.insertDataEvaConAftCut(dateNow,
                        Con_Name.getText().toString().trim(),
                        Con_ID.getText().toString().trim(),
                        Farm_ID.getText().toString().trim(),
                        Emp_ID,
                        EvaAftCutString,
                        EvaAftCutSuggestString,
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
            } else {
                Log.e("Check", "บันทึกข้อมูลล้มเหลว");
            }
        }
    }

    private String getValueCheckbox(CheckBox EvaAftCut_NO) {
        if (EvaAftCut_NO.isChecked()) {
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaConAftCutActivity.this);
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
