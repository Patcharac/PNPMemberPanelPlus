package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_EvaCon_DB_OpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EvaConCuttingActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    //spinner and getString
    private Spinner workingSpinner;
    private ArrayList<String> workingArrString = new ArrayList<String>();

    private Spinner working;
    private String workingS;

    private Button btnSave;

    private EditText Con_Name,Con_ID,Farm_ID;

    //check box and suggest
    private CheckBox [] EvaCutting_NO = new CheckBox[22];
    private String [] EvaCuttingString = new String[22];

    private EditText [] EvaCuttingSuggest_NO = new EditText[22];
    private String [] EvaCuttingSuggestString = new String[22];
    private int checkComplaint = 0;
    private int checkTextComplaint =1;


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
    private static final String TABLE_EVA_CON_CUTTING_DATA = "eva_con_cutting_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eva_con_cutting);

        spinner();
        initWidget();

        //get user
        Emp_ID = getEmpID();
        UserZone = getUserZone();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR)+543;

        dateNow = new SimpleDateFormat("dd-MM",
                Locale.getDefault()).format(new Date());

        dateNow = dateNow+"-"+String.valueOf(year);
        Log.d("date",String.valueOf(dateNow));
        //--------create DB-SQlite---------
    /*
        mydb = this.openOrCreateDatabase(DATABASE_FILE_PATH + File.separator
                + DATABASE_EVACON_DB + "-" + UserZone + ".sqlite", Context.MODE_PRIVATE, null);

        mydb.execSQL(" create table if not exists " + TABLE_EVA_CON_CUTTING_DATA +
                "   (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "   EvaCutting_Date TEXT ," +
                "   Con_Name TEXT ," +
                "   Con_ID TEXT ," +
                "   Farm_ID TEXT ," +
                "   Emp_ID TEXT ," +
                "   Working_Step TEXT ,"+
                "   EvaCutting_NO1 TEXT ,"+
                "   EvaCuttingSuggest_NO1 TEXT ,"+
                "   EvaCutting_NO2 TEXT ,"+
                "   EvaCuttingSuggest_NO2 TEXT ,"+
                "   EvaCutting_NO3 TEXT ,"+
                "   EvaCuttingSuggest_NO3 TEXT ,"+
                "   EvaCutting_NO4 TEXT ,"+
                "   EvaCuttingSuggest_NO4 TEXT ,"+
                "   EvaCutting_NO5 TEXT ,"+
                "   EvaCuttingSuggest_NO5 TEXT ,"+
                "   EvaCutting_NO6 TEXT ,"+
                "   EvaCuttingSuggest_NO6 TEXT ,"+
                "   EvaCutting_NO7 TEXT ,"+
                "   EvaCuttingSuggest_NO7 TEXT ,"+
                "   EvaCutting_NO8 TEXT ,"+
                "   EvaCuttingSuggest_NO8 TEXT ,"+
                "   EvaCutting_NO9 TEXT ,"+
                "   EvaCuttingSuggest_NO9 TEXT ,"+
                "   EvaCutting_NO10 TEXT ,"+
                "   EvaCuttingSuggest_NO10 TEXT ,"+
                "   EvaCutting_NO11 TEXT ,"+
                "   EvaCuttingSuggest_NO11 TEXT ,"+
                "   EvaCutting_NO12 TEXT ,"+
                "   EvaCuttingSuggest_NO12 TEXT ,"+
                "   EvaCutting_NO13 TEXT ,"+
                "   EvaCuttingSuggest_NO13 TEXT ,"+
                "   EvaCutting_NO14 TEXT ,"+
                "   EvaCuttingSuggest_NO14 TEXT ,"+
                "   EvaCutting_NO15 TEXT ,"+
                "   EvaCuttingSuggest_NO15 TEXT ,"+
                "   EvaCutting_NO16 TEXT ,"+
                "   EvaCuttingSuggest_NO16 TEXT ,"+
                "   EvaCutting_NO17 TEXT ,"+
                "   EvaCuttingSuggest_NO17 TEXT ,"+
                "   EvaCutting_NO18 TEXT ,"+
                "   EvaCuttingSuggest_NO18 TEXT ,"+
                "   EvaCutting_NO19 TEXT ,"+
                "   EvaCuttingSuggest_NO19 TEXT ,"+
                "   EvaCutting_NO20 TEXT ,"+
                "   EvaCuttingSuggest_NO20 TEXT ,"+
                "   EvaCutting_NO21 TEXT ,"+
                "   EvaCuttingSuggest_NO21 TEXT ,"+
                "   EvaCutting_NO22 TEXT ,"+
                "   EvaCuttingSuggest_NO22 TEXT );");
         */

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                workingS = working.getSelectedItem().toString();

                for (int i=0; i<22; i++){
                    EvaCuttingString[i] = getValueCheckbox(EvaCutting_NO[i]);

                    EvaCuttingSuggestString[i] = EvaCuttingSuggest_NO[i].getText().toString();
                    if (EvaCuttingSuggestString[i].isEmpty()){
                        EvaCuttingSuggestString[i] = "";
                    }
                }

                if(checkComplaint == 1 && EvaCuttingSuggestString[21]==""){
                    checkTextComplaint = 0;
                }else {
                    checkTextComplaint = 1;
                }

                if (Con_Name.getText().toString().isEmpty()||
                    Con_ID.getText().toString().isEmpty() ||
                    Farm_ID.getText().toString().isEmpty()||
                    checkTextComplaint == 0){

                    if (Con_Name.getText().toString().isEmpty()||
                            Con_ID.getText().toString().isEmpty() ||
                            Farm_ID.getText().toString().isEmpty()){

                        Toast.makeText(getApplicationContext(),
                                "กรุณกรอกข้อมูลให้ครบถ้วนก่อนการบันทึก", Toast.LENGTH_SHORT)
                                .show();
                    }

                    if(checkTextComplaint == 0){
                        Toast.makeText(getApplicationContext(),
                                "กรุณกรอกข้อมูลเรื่องร้องเรียนก่อนการบันทึก", Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {

                    beforeSave();

                }


                break;
        }

    }

    private void initWidget() {
        working = (Spinner) findViewById(R.id.working);
        btnSave = (Button) findViewById(R.id.save);

        btnSave.setOnClickListener(this);

        Con_ID = (EditText) findViewById(R.id.Con_ID);
        Con_Name = (EditText) findViewById(R.id.Con_Name);
        Farm_ID = (EditText) findViewById(R.id.Farm_ID);

        EvaCutting_NO[0] = (CheckBox) findViewById(R.id.EvaCutting_NO1);
        EvaCutting_NO[1] = (CheckBox) findViewById(R.id.EvaCutting_NO2);
        EvaCutting_NO[2] = (CheckBox) findViewById(R.id.EvaCutting_NO3);
        EvaCutting_NO[3] = (CheckBox) findViewById(R.id.EvaCutting_NO4);
        EvaCutting_NO[4] = (CheckBox) findViewById(R.id.EvaCutting_NO5);
        EvaCutting_NO[5] = (CheckBox) findViewById(R.id.EvaCutting_NO6);
        EvaCutting_NO[6] = (CheckBox) findViewById(R.id.EvaCutting_NO7);
        EvaCutting_NO[7] = (CheckBox) findViewById(R.id.EvaCutting_NO8);
        EvaCutting_NO[8] = (CheckBox) findViewById(R.id.EvaCutting_NO9);
        EvaCutting_NO[9] = (CheckBox) findViewById(R.id.EvaCutting_NO10);
        EvaCutting_NO[10] = (CheckBox) findViewById(R.id.EvaCutting_NO11);
        EvaCutting_NO[11] = (CheckBox) findViewById(R.id.EvaCutting_NO12);
        EvaCutting_NO[12] = (CheckBox) findViewById(R.id.EvaCutting_NO13);
        EvaCutting_NO[13] = (CheckBox) findViewById(R.id.EvaCutting_NO14);
        EvaCutting_NO[14] = (CheckBox) findViewById(R.id.EvaCutting_NO15);
        EvaCutting_NO[15] = (CheckBox) findViewById(R.id.EvaCutting_NO16);
        EvaCutting_NO[16] = (CheckBox) findViewById(R.id.EvaCutting_NO17);
        EvaCutting_NO[17] = (CheckBox) findViewById(R.id.EvaCutting_NO18);
        EvaCutting_NO[18] = (CheckBox) findViewById(R.id.EvaCutting_NO19);
        EvaCutting_NO[19] = (CheckBox) findViewById(R.id.EvaCutting_NO20);
        EvaCutting_NO[20] = (CheckBox) findViewById(R.id.EvaCutting_NO21);
        EvaCutting_NO[21] = (CheckBox) findViewById(R.id.EvaCutting_NO22);

        EvaCutting_NO[21].setOnCheckedChangeListener(this);

        EvaCuttingSuggest_NO[0] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO1);
        EvaCuttingSuggest_NO[1] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO2);
        EvaCuttingSuggest_NO[2] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO3);
        EvaCuttingSuggest_NO[3] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO4);
        EvaCuttingSuggest_NO[4] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO5);
        EvaCuttingSuggest_NO[5] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO6);
        EvaCuttingSuggest_NO[6] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO7);
        EvaCuttingSuggest_NO[7] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO8);
        EvaCuttingSuggest_NO[8] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO9);
        EvaCuttingSuggest_NO[9] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO10);
        EvaCuttingSuggest_NO[10] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO11);
        EvaCuttingSuggest_NO[11] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO12);
        EvaCuttingSuggest_NO[12] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO13);
        EvaCuttingSuggest_NO[13] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO14);
        EvaCuttingSuggest_NO[14] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO15);
        EvaCuttingSuggest_NO[15] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO16);
        EvaCuttingSuggest_NO[16] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO17);
        EvaCuttingSuggest_NO[17] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO18);
        EvaCuttingSuggest_NO[18] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO19);
        EvaCuttingSuggest_NO[19] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO20);
        EvaCuttingSuggest_NO[20] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO21);
        EvaCuttingSuggest_NO[21] = (EditText) findViewById(R.id.EvaCuttingSuggest_NO22);

    }

    public void beforeSave(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaConCuttingActivity.this);
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
        long check = obj.insertDataEvaConCutting(dateNow,
                Con_Name.getText().toString().trim(),
                Con_ID.getText().toString().trim(),
                Farm_ID.getText().toString().trim(),
                Emp_ID,
                workingS,
                EvaCuttingString,
                EvaCuttingSuggestString,
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

    private void spinner() {
        workingSpinner = (Spinner) findViewById(R.id.working);

        workingArrString.add("ระยะแรก");
        workingArrString.add("ระยะกลาง");
        workingArrString.add("ระยะสุดท้าย");

        ArrayAdapter<String> adapterWorking = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, workingArrString);
        workingSpinner.setAdapter(adapterWorking);

    }
    private String getValueCheckbox(CheckBox EvaCutting_NO){
        if(EvaCutting_NO.isChecked()){
            return "1";
        }
        return "0";
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.EvaCutting_NO22:
                if (isChecked){
                    EvaCuttingSuggest_NO[21].setEnabled(true);
                    checkComplaint = 1;
                }else{
                    EvaCuttingSuggest_NO[21].setEnabled(false);
                    EvaCuttingSuggest_NO[21].setText("");
                    checkComplaint = 0;
                }
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaConCuttingActivity.this);
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
