package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class MenuEvaCutActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnEvaCon;
    Button btnEvaConBefCut;
    Button btnEvaConCutting;
    Button btnEvaConAtfCut;

    private String UserZone="";

    //===============DB================//
    SQLiteDatabase mydb=null;

    private String DBFile;
    SQLiteDatabase db;

    @SuppressLint("SdCardPath") String DATABASE_FILE_PATH="/sdcard/MapDB/";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";

    private static final String DATABASE_EVACON_DB = "EVACON_DB";
    private static final String TABLE_EVA_CON_AFTCUT_DATA = "eva_con_aftcut_data";
    private static final String TABLE_EVA_CON_BEFCUT_DATA = "eva_con_befcut_data";
    private static final String TABLE_EVA_CON_CUTTING_DATA = "eva_con_cutting_data";
    private static final String TABLE_EVA_CONTRACTOR_DATA = "eva_contractor_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_eva_logging);

        InitWidget();
        UserZone = getUserZone();

        //creat all table to use in this page
        creatDB();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.evaCon:
                Intent EvaConPage = new Intent(MenuEvaCutActivity.this,
                        EvaContractorActivity.class);
                startActivity(EvaConPage);
                break;

            case R.id.evaConBefCut:
                Intent EvaBefConCutPage = new Intent(MenuEvaCutActivity.this,
                        EvaConBefCutActivity.class);
                startActivity(EvaBefConCutPage);
                break;

            case R.id.evaConCutting:
                Intent EvaConCuttingPage = new Intent(MenuEvaCutActivity.this,
                        EvaConCuttingActivity.class);
                startActivity(EvaConCuttingPage);
                break;

            case R.id.evaConAftCut:
                Intent EvaConAftCutPage = new Intent(MenuEvaCutActivity.this,
                        EvaConAftCutActivity.class);
                startActivity(EvaConAftCutPage );
                break;


        }
    }

    private void InitWidget() {
        btnEvaCon = (Button) findViewById(R.id.evaCon);
        btnEvaConBefCut = (Button) findViewById(R.id.evaConBefCut);
        btnEvaConCutting = (Button) findViewById(R.id.evaConCutting);
        btnEvaConAtfCut = (Button) findViewById(R.id.evaConAftCut);

        btnEvaCon.setOnClickListener(this);
        btnEvaConBefCut.setOnClickListener(this);
        btnEvaConCutting.setOnClickListener(this);
        btnEvaConAtfCut.setOnClickListener(this);
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

    private void creatDB() {
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

        mydb.execSQL(" create table if not exists " + TABLE_EVA_CONTRACTOR_DATA +
                "   (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "   EvaCon_Date TEXT ," +
                "   Con_Name TEXT ," +
                "   Con_ID TEXT ," +
                "   Farm_ID TEXT ," +
                "   Emp_ID TEXT ," +
                "   EvaCon_NO1 TEXT ," +
                "   EvaCon_NO2 TEXT ," +
                "   EvaCon_NO3 TEXT ," +
                "   EvaCon_NO4 TEXT ," +
                "   EvaCon_NO5 TEXT ," +
                "   EvaCon_NO6 TEXT ," +
                "   EvaCon_NO7 TEXT ," +
                "   EvaCon_NO8 TEXT ," +
                "   EvaCon_NO9 TEXT ," +
                "   EvaCon_NO10 TEXT ," +
                "   EvaCon_NO11 TEXT ," +
                "   EvaCon_NO12 TEXT ," +
                "   EvaCon_NO13 TEXT ," +
                "   EvaCon_NO14 TEXT ," +
                "   EvaCon_NO15 TEXT ," +
                "   EvaCon_NO16 TEXT ," +
                "   EvaCon_NO17 TEXT ," +
                "   EvaCon_NO18 TEXT ," +
                "   Pic_LicenseSaw BLOB ,"+
                "   Pic_LicenseCar BLOB);" );


    }


}
