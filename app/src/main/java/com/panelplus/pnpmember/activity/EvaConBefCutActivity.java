package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
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


public class EvaConBefCutActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnSave;
    EditText Con_Name,Con_ID,Farm_ID;

    CheckBox [] EvaBefCut_NO = new CheckBox[9];
    String [] EvaBefCutString = new String[9];

    EditText [] EvaBefCutSuggest_NO = new EditText[12];
    String [] EvaBefCutSuggestString = new String[12];

    private String Emp_ID ="";
    private String UserZone="";

    private String dateNow;

    //===============DB================//
    SQLiteDatabase mydb=null;

    private String DBFile;
    SQLiteDatabase db;

    @SuppressLint("SdCardPath") String DATABASE_FILE_PATH="/sdcard/MapDB/";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";

    private static final String DATABASE_EVACON_DB = "EVACON_DB";
    private static final String TABLE_EVA_CON_BEFCUT_DATA = "eva_con_befcut_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eva_con_bef_cut);

        InitWitget();

        //get user
        Emp_ID = getEmpID();
        UserZone = getUserZone();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR)+543;

        dateNow = new SimpleDateFormat("dd-MM",
                Locale.getDefault()).format(new Date());

        dateNow = dateNow+"-"+String.valueOf(year);
        Log.d("date",String.valueOf(dateNow));
        /*

        mydb = this.openOrCreateDatabase(DATABASE_FILE_PATH + File.separator
                + DATABASE_EVACON_DB + "-" + UserZone + ".sqlite", Context.MODE_PRIVATE, null);

        mydb.execSQL(" create table if not exists " + TABLE_EVA_CON_BEFCUT_DATA +
                "   (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "   EvaBefCut_Date TEXT ," +
                "   Con_Name TEXT ," +
                "   Con_ID TEXT ," +
                "   Farm_ID TEXT ," +
                "   Emp_ID TEXT ," +
                "   EvaBefCut_NO1 TEXT ," +
                "   EvaBefCutSuggest_NO1 TEXT ,"+
                "   EvaBefCut_NO2 TEXT ," +
                "   EvaBefCutSuggest_NO2 TEXT ,"+
                "   EvaBefCut_NO3 TEXT ," +
                "   EvaBefCutSuggest_NO3 TEXT ,"+
                "   EvaBefCut_NO4 TEXT ," +
                "   EvaBefCutSuggest_NO4 TEXT ,"+
                "   EvaBefCut_NO5 TEXT ," +
                "   EvaBefCutSuggest_NO5 TEXT ,"+
                "   EvaBefCut_NO6 TEXT ," +
                "   EvaBefCutSuggest_NO6 TEXT ,"+
                "   EvaBefCut_NO7 TEXT ," +
                "   EvaBefCutSuggest_NO7 TEXT ,"+
                "   EvaBefCut_NO8 TEXT ," +
                "   EvaBefCutSuggest_NO8 TEXT ,"+
                "   EvaBefCut_NO9 TEXT ," +
                "   EvaBefCutSuggest_NO9 TEXT ,"+
                "   EvaBefCutSuggest_NO10 TEXT ,"+
                "   EvaBefCutSuggest_NO11 TEXT ,"+
                "   EvaBefCutSuggest_NO12 TEXT );" );

          */
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                for(int i=0; i<9; i++){
                    EvaBefCutString[i] = getValueCheckbox(EvaBefCut_NO[i]);
                }

                for(int i=0; i<12; i++){
                    EvaBefCutSuggestString[i] = EvaBefCutSuggest_NO[i].getText().toString().trim();

                    if (EvaBefCutSuggestString[i].isEmpty()){
                        EvaBefCutSuggestString[i] ="";
                    }
                }

                if (Con_Name.getText().toString().isEmpty()||
                    Con_ID.getText().toString().isEmpty() ||
                    Farm_ID.getText().toString().isEmpty()||
                    EvaBefCutSuggestString[9] =="" ||
                    EvaBefCutSuggestString[10] =="" ||
                    EvaBefCutSuggestString[11] == ""){

                    Toast.makeText(getApplicationContext(),
                            "กรุณากรอกข้อมูลให้ครบถ้วนก่อนการบันทึก", Toast.LENGTH_SHORT)
                            .show();
                }else {
                    beforeSave();
                }
        }
    }

    private void InitWitget() {
        btnSave = (Button) findViewById(R.id.save);
        btnSave.setOnClickListener(this);

        Con_ID = (EditText) findViewById(R.id.Con_ID);
        Con_Name = (EditText) findViewById(R.id.Con_Name);
        Farm_ID = (EditText) findViewById(R.id.Farm_ID);

        EvaBefCut_NO[0] = (CheckBox) findViewById(R.id.EvaBefCut_NO1);
        EvaBefCut_NO[1] = (CheckBox) findViewById(R.id.EvaBefCut_NO2);
        EvaBefCut_NO[2] = (CheckBox) findViewById(R.id.EvaBefCut_NO3);
        EvaBefCut_NO[3] = (CheckBox) findViewById(R.id.EvaBefCut_NO4);
        EvaBefCut_NO[4] = (CheckBox) findViewById(R.id.EvaBefCut_NO5);
        EvaBefCut_NO[5] = (CheckBox) findViewById(R.id.EvaBefCut_NO6);
        EvaBefCut_NO[6] = (CheckBox) findViewById(R.id.EvaBefCut_NO7);
        EvaBefCut_NO[7] = (CheckBox) findViewById(R.id.EvaBefCut_NO8);
        EvaBefCut_NO[8] = (CheckBox) findViewById(R.id.EvaBefCut_NO9);

        EvaBefCutSuggest_NO[0] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO1);
        EvaBefCutSuggest_NO[1] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO2);
        EvaBefCutSuggest_NO[2] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO3);
        EvaBefCutSuggest_NO[3] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO4);
        EvaBefCutSuggest_NO[4] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO5);
        EvaBefCutSuggest_NO[5] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO6);
        EvaBefCutSuggest_NO[6] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO7);
        EvaBefCutSuggest_NO[7] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO8);
        EvaBefCutSuggest_NO[8] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO9);
        EvaBefCutSuggest_NO[9] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO10);
        EvaBefCutSuggest_NO[10] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO11);
        EvaBefCutSuggest_NO[11] = (EditText) findViewById(R.id.EvaBefCutSuggest_NO12);


    }

    private String getValueCheckbox(CheckBox EvaBefCut_NO){
        if(EvaBefCut_NO.isChecked()){
            return "1";
        }
        return "0";
    }

    public void beforeSave(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaConBefCutActivity.this);
        builder.setMessage("ต้องการจะบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener(){
            @Override
            public  void onClick(DialogInterface dialogInterface, int i) {
                savetoSQlite();

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

    private void savetoSQlite() {
        DBFile = DATABASE_EVACON_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_EvaCon_DB_OpenHelper obj = new ExternalStorage_EvaCon_DB_OpenHelper(getApplicationContext(),DBFile);
        db = obj.getReadableDatabase();
        long check = obj.insertDataEvaConBefCut(dateNow,
                Con_Name.getText().toString().trim(),
                Con_ID.getText().toString().trim(),
                Farm_ID.getText().toString().trim(),
                Emp_ID,
                EvaBefCutString,
                EvaBefCutSuggestString,
                db);

        obj.close();

        if (check != -1){
            Toast.makeText(getApplicationContext(),
                    "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT)
                    .show();
            finish();

        } else {
            Toast.makeText(getApplicationContext(),
                    "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT)
                    .show();
        }


    }

    private String getEmpID(){
        SQLiteDatabase db;
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(getApplicationContext(),DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getReadableDatabase();
            Emp_ID = obj.GetEmpID("1", db);
            obj.close();

            return Emp_ID;
        }
        return "NULL";
    }

    private String getUserZone(){
        SQLiteDatabase db;
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(getApplicationContext(),DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getReadableDatabase();
            UserZone = obj.LoginData("1", db);
            obj.close();

            return UserZone;
        }
        return "NULL";
    }
    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaConBefCutActivity.this);
        builder.setMessage("ต้องการจะออกจากการบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener(){
            @Override
            public  void onClick(DialogInterface dialogInterface, int i) {
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
