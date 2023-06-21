package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class MenuVisitActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnVisitMember;
    Button btnVisitFarm;
    Button btnRecCir;

    private String UserZone = "";

    //===============DB================//
    SQLiteDatabase mydb = null;

    private String DBFile;
    SQLiteDatabase db;
    File Path = null;
    @SuppressLint("SdCardPath")
    String DATABASE_FILE_PATH = "/sdcard/MapDB/";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";

    private static final String DATABASE_VISIT_DB = "VISIT_DB";
    private static final String TABLE_VISIT_MEMBER_DATA = "visit_member_data";
    private static final String TABLE_VISIT_FARM_DATA = "visit_farm_data";
    private static final String TABLE_REC_RESULT_DATA = "rec_result_data";
    private static final String TABLE_GIRTH_DATA = "girth_data";
    private static final String TABLE_OBJECT_DATA = "object_see";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_visit);

        InitWidget();
        UserZone = getUserZone();

        //creat all table to use in this page
        creatDB(this);

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
//                visit_member_data
                String createTableQuery = "CREATE TABLE IF NOT EXISTS visit_member_data (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "   VisitMem_Date TEXT ," +
                        "   mem_Name TEXT ," +
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
                        "   VisitMemSuggest_NO11 TEXT);";

                this.mydb.execSQL(createTableQuery);
                Log.e("Check", "Database created  visit_member_data successfully");
                Toast.makeText(context, "Database created successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.d("errorCreateDB", String.valueOf(e));
            }
        }


//        mydb.execSQL(" create table if not exists " + TABLE_VISIT_FARM_DATA +
//                "   (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
//                "   VisitFarm_Date ,"+
//                "   qouta_ID TEXT ," +
//                "   Farm_ID TEXT ," +
//                "   Emp_ID TEXT ,"+
//                "   VisitFarm_NO1 TEXT ,"+
//                "   VisitFarmSuggest_NO1 TEXT ,"+
//                "   VisitFarm_NO2 TEXT ,"+
//                "   VisitFarmSuggest_NO2 TEXT ,"+
//                "   VisitFarm_NO3 TEXT ,"+
//                "   VisitFarmSuggest_NO3 TEXT ,"+
//                "   VisitFarm_NO4 TEXT ,"+
//                "   VisitFarmSuggest_NO4 TEXT ,"+
//                "   VisitFarm_NO5 TEXT ,"+
//                "   VisitFarmSuggest_NO5 TEXT ,"+
//                "   VisitFarm_NO6 TEXT ,"+
//                "   VisitFarmSuggest_NO6 TEXT ,"+
//                "   VisitFarm_NO7 TEXT ,"+
//                "   VisitFarmSuggest_NO7 TEXT ,"+
//                "   VisitFarm_NO8 TEXT ,"+
//                "   VisitFarmSuggest_NO8 TEXT ,"+
//                "   VisitFarm_NO9 TEXT ,"+
//                "   VisitFarmSuggest_NO9 TEXT ," +
//                "   rubberYearOfPlant INTEGER  ," +
//                "   rubberYearOld INTEGER ," +
//                "   rubberBreed TEXT);" );

//        mydb.execSQL(" create table if not exists " + TABLE_REC_RESULT_DATA +
//                "  (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
//                "   Rec_Date TEXT ,"+
//                "   Farm_ID TEXT ,"+
//                "   Farm_AreaRai REAL ,"+
//                "   Farm_AreaNgan REAL ,"+
//                "   Farm_AreaWa REAL ,"+
//                "   Farm_AreaAll REAL ,"+
//                "   Rec_PlantRows INTEGER ,"+
//                "   Rec_PlantTree INTEGER ,"+
//                "   Rec_RubberAll INTEGER ,"+
//                "   Rec_RubberAlive INTEGER ,"+
//                "   Rec_RubberAlivePercent TEXT ,"+
//                "   Rec_RubberDeath INTEGER ,"+
//                "   Rec_RubberDeathPercent TEXT ,"+
//                "   Rec_RubPerRai REAL ,"+
//                "   Rec_RubPerRaiAlive REAL ," +
//                "   Rec_AvgPerTree REAL ," +
//                "   Rec_AvgPerRai REAL ," +
//                "   Rec_AvgPerPlot REAL ," +
//                "   Rec_AvgTrunk REAL ," +
//                "   Rec_AvgBranch REAL ," +
//                "   Rec_Status TEXT ," +
//                "   Rec_Note TEXT);" );
//
//        mydb.execSQL(" create table if not exists " + TABLE_GIRTH_DATA +
//                "  (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
//                "   Gir_Date TEXT ,"+
//                "   Farm_ID TEXT ,"+
//                "   Gir_NO TEXT ,"+
//                "   Gir_Girth REAL ," +
//                "   Gir_kgPerTree REAL ," +
//                "   Gir_Status TEXT);" );

//        mydb.execSQL(" create table if not exists " + TABLE_OBJECT_DATA +
//                "  (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
//                "   Farm_ID TEXT ,"+
//                "   Obj_LocalName TEXT ,"+
//                "   Obj_Status TEXT);" );


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.visitMember:
                Intent visitMemberPage = new Intent(MenuVisitActivity.this, VisitMemberActivity.class);
                startActivity(visitMemberPage);
                break;

//            case R.id.visitFarm:
//                Intent visitFarmPage= new Intent(MenuVisitActivity.this,
//                        VisitFarmActivity.class);
//                startActivity(visitFarmPage);
//                break;

//            case R.id.RecCir:
//                Intent visitRecCirPage= new Intent(MenuVisitActivity.this,
//                        VisitRecCirActivity.class);
//                startActivity(visitRecCirPage);
//
//                break;

        }

    }

    private void InitWidget() {
        btnVisitMember = (Button) findViewById(R.id.visitMember);
//        btnVisitFarm = (Button) findViewById(R.id.visitFarm);
//        btnRecCir = (Button) findViewById(R.id.RecCir);

        btnVisitMember.setOnClickListener(this);
//        btnVisitFarm.setOnClickListener(this);
//        btnRecCir.setOnClickListener(this);
    }


}
