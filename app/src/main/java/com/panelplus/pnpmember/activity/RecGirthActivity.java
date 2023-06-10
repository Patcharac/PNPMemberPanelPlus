package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Rec_Girth_DB_OpenHelper;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RecGirthActivity extends AppCompatActivity implements View.OnClickListener{

    String Farm_ID=null;
    private Button btnReadyCal;
    private Button btnSaveGirth;
    private Button btnSave;
    private LinearLayout layoutReady;
    private LinearLayout layoutStatus;
    private LinearLayout layoutRubberAll;
    private LinearLayout layoutResult;

    private EditText edtFarm_ID;
    private EditText edtFarm_AreaRai;
    private Float AreaRai;
    private EditText edtRec_PlantRows,edtRec_PlantTree;
    private TextView txtRubberNO;
    private TextView txtIDShow, txtAreaShow, txtTreeShow;
    private Float Area_Total = null;
    private String Farm_AreaTotalString = null;

    private int RubberAll = 0;

    //save girth
    private int count_RubberNo =0;
    private int Rub_Alive,Rub_Death =0;
    private EditText edtRub_Girth;
    private TextView txtRub_All, txtRub_Alive,
            txtRub_Death;
    private TextView txtRub_AllPercent, txtRub_AlivePercent,
            txtRub_DeathPercent, txtRubPerRai, txtRubPerRaiAlive;
    private TextView txtAvgKgPerRai,txtAvgKgPerPlot,txtAvgTrunk,txtAvgBranch;
    private TextView txtTrunkTon,txtBranckTon;
    float RubPerRai,RubPerRaiAlive = 0;
    private String RubPerRaiString,RubPerRaiAliveString;
    private Float Rub_AlivePercent,Rub_DeathPercent;
    String Rub_AlivePercentString,Rub_DeathPercentString;

    private Float girthFloat;
    private Float[] girth_NO ;
    private double[] kgPerTree;
    private double AvgKgPerTree,AvgKgPerRai,AvgKgPerPlot,AvgTrunk,AvgBranch;
    private double TrunkTon,BranchTon;
    private String AvgKgPerTreeString,AvgKgPerRaiSting,AvgKgPerPlotString,
                    AvgTrunkString,AvgBranchString,TrunkTonString,BranchTonString;

    private EditText edtRec_Note;

    //status visit or newbie
    private String Status;
    private EditText edtStatus;

    private int checkInput = 0;
    private int checkTextInput = 1;

    String Farm_Date;
    String Link_ID;
    private String Emp_ID ="";
    private String UserZone="";

    private String dateNow;
    //-----------DB-----------//
    SQLiteDatabase mydb=null;

    private String DBFile;
    SQLiteDatabase db;

    @SuppressLint("SdCardPath") String DATABASE_FILE_PATH="/sdcard/MapDB/";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    private static final String DATABASE_FSCGISDATANAME = "FSCGISDATA";


    private static final String DATABASE_REC_GIRTH_DB = "REC_GIRTH_DB";
    private static final String TABLE_REC_RESULT_DATA = "rec_result_data";
    private static final String TABLE_GIRTH_DATA = "girth_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_girth);
///////////////////////////////////////////////////////////////////
        InitWidget();
///////////////////////////////////////////////////////////////////
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR)+543;

        dateNow = new SimpleDateFormat("dd-MM",
                Locale.getDefault()).format(new Date());

        dateNow = dateNow+"-"+String.valueOf(year);
        Log.d("date",String.valueOf(dateNow));



        Bundle b = getIntent().getExtras();
        Status = b.getString("Status");

        if(Objects.equals(String.valueOf(Status), "NewMember")){
            checkInput = 0;
            Log.d("Status",String.valueOf(Status));
            layoutStatus.setVisibility(View.GONE);
        } else {
            checkInput = 1;
            Farm_ID = b.getString("Farm_ID");
            edtFarm_ID.setText(Farm_ID.toString());
            edtFarm_ID.setEnabled(false);

            SQLiteDatabase db;
            String DBFile= DATABASE_FSCGISDATANAME + ".sqlite";
            Log.d("getCaneCallout_DBFile", DBFile);
            String tmpData1;
            List<ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberCalloutDatas> CalloutData=null ;

            ExternalStorage_Farm_GIS_DB_OpenHelper obj= new ExternalStorage_Farm_GIS_DB_OpenHelper(getApplicationContext(),DBFile);
            if(obj.databaseFileExists()) {
                Log.d("getCaneCallout_DBFile", "02");
                db = obj.getReadableDatabase();
                CalloutData = obj.getCaneCallout(Farm_ID, db);
                for (ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberCalloutDatas mem : CalloutData) {
                    edtFarm_AreaRai.setText(mem.gFARM_AREA().toString());
                    edtFarm_AreaRai.setEnabled(false);

                    edtStatus.setText(String.valueOf(year));
                    edtStatus.setEnabled(false);

                }
            }


            layoutStatus.setVisibility(View.VISIBLE);
        }

        //get user
        Emp_ID = getEmpID();
        UserZone = getUserZone();
        Farm_Date = getDateTime();




        mydb = this.openOrCreateDatabase(DATABASE_FILE_PATH + File.separator
                + DATABASE_REC_GIRTH_DB + "-" + UserZone + ".sqlite", Context.MODE_PRIVATE, null);

        mydb.execSQL(" create table if not exists " + TABLE_REC_RESULT_DATA +
                "  (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "   Link_ID TEXT ," +
                "   Farm_ID TEXT ,"+
                "   Emp_ID TEXT ,"+
                "   Rec_Date TEXT ,"+
                "   Farm_AreaAll REAL ,"+
                "   Rec_PlantRows INTEGER ,"+
                "   Rec_PlantTree INTEGER ,"+
                "   Rec_Status TEXT ," +
                "   Rec_Note TEXT);" );

        mydb.execSQL(" create table if not exists " + TABLE_GIRTH_DATA +
                "  (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "   Link_ID TEXT ," +
                "   Farm_ID TEXT ,"+
                "   Gir_Date TEXT ,"+
                "   Gir_NO TEXT ,"+
                "   Gir_Girth REAL);" );

    }

    private void InitWidget() {

        layoutReady = (LinearLayout) findViewById(R.id.ready);
        layoutStatus = (LinearLayout) findViewById(R.id.layoutStatus);
        layoutRubberAll = (LinearLayout) findViewById(R.id.layoutRubberAll);
        layoutResult = (LinearLayout) findViewById(R.id.layoutResult);

        btnReadyCal = (Button) findViewById(R.id.readyCal);
        btnSaveGirth = (Button) findViewById(R.id.saveGirth);
        btnSave = (Button) findViewById(R.id.save);

        btnReadyCal.setOnClickListener(this);
        btnSaveGirth.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        edtFarm_ID = (EditText) findViewById(R.id.Farm_ID);
        edtFarm_AreaRai = (EditText) findViewById(R.id.Farm_AreaRai);
        edtRec_PlantRows = (EditText) findViewById(R.id.Rec_PlantRows);
        edtRec_PlantTree = (EditText) findViewById(R.id.Rec_PlantTree);

        txtRubberNO = (TextView) findViewById(R.id.RubberNO);
        txtIDShow = (TextView) findViewById(R.id.idShow);
        txtAreaShow = (TextView) findViewById(R.id.areaShow);
        txtTreeShow = (TextView) findViewById(R.id.treeShow);
        edtRub_Girth = (EditText) findViewById(R.id.Rub_Girth);

        txtRub_All = (TextView) findViewById(R.id.Rub_All);
        txtRub_Alive = (TextView) findViewById(R.id.Rub_Alive);
        txtRub_Death = (TextView) findViewById(R.id.Rub_Death);
        txtRubPerRai = (TextView) findViewById(R.id.RubPerRai);

        txtRub_AllPercent = (TextView) findViewById(R.id.Rub_AllPercent);
        txtRub_AlivePercent = (TextView) findViewById(R.id.Rub_AlivePercent);
        txtRub_DeathPercent = (TextView) findViewById(R.id.Rub_DeathPercent);
        txtRubPerRaiAlive = (TextView) findViewById(R.id.RubPerRaiAlive);

        txtAvgKgPerPlot = (TextView) findViewById(R.id.AvgKgPerPlot);
        txtAvgKgPerRai = (TextView) findViewById(R.id.AvgKgPerRai);
        txtAvgTrunk = (TextView) findViewById(R.id.AvgTrunk);
        txtAvgBranch = (TextView) findViewById(R.id.AvgBranch);
        txtTrunkTon = (TextView) findViewById(R.id.TrunkTon);
        txtBranckTon = (TextView) findViewById(R.id.BranchTon);

        edtStatus = (EditText) findViewById(R.id.Gir_Status);


        edtRec_Note = (EditText) findViewById(R.id.Rec_Note);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.readyCal:

                try {
                    AreaRai = Float.parseFloat(edtFarm_AreaRai.getText().toString());

                    int Rec_PlantRow = Integer.parseInt(edtRec_PlantRows.getText().toString());
                    int Rec_PlantTree = Integer.parseInt(edtRec_PlantTree.getText().toString());

                    int Farm_IDint = Integer.parseInt(edtFarm_ID.getText().toString());

                    Area_Total = AreaRai;
                    DecimalFormat AreaTotal2digit = new DecimalFormat("#.##");
                    Farm_AreaTotalString = String.valueOf(Float.valueOf(AreaTotal2digit.format(Area_Total)));

                    if(checkInput == 1 && edtStatus.getText().toString().isEmpty()){
                        checkTextInput = 0;
                    }else {
                        checkTextInput = 1;
                    }

                    if(Farm_IDint == 0 || Rec_PlantRow == 0 ||
                            Rec_PlantTree == 0 || checkTextInput == 0 || edtFarm_ID.length()<9){
                        if(Farm_IDint == 0 || edtFarm_ID.length()<9) {
                            Toast.makeText(getApplicationContext(),
                                    "กรุณากรอก ID แปลง", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        if(Rec_PlantRow == 0 || Rec_PlantTree == 0){
                            Toast.makeText(getApplicationContext(),
                                    "กรุณากรอกระยะปลูก", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        if(checkTextInput == 0){
                            Toast.makeText(getApplicationContext(),
                                    "กรุณากรอกปีที่ทำการเยี่ยม", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }else {
                        try {
                            if(checkInput != 0) {
                                int year = Integer.parseInt(edtStatus.getText().toString());
                            }

                            if (Area_Total > 0 && Area_Total <= 10) {
                                RubberAll = 70;
                            } else if (Area_Total > 10) {
                                int over = (int) (Area_Total - 10);
                                RubberAll = 70 + (over * 7);
                            }

                            ///////////////////////////////////////////////
                            // for test
                            //RubberAll =5;

                            girth_NO = new Float[RubberAll];
                            kgPerTree = new double[RubberAll];

                            RubPerRai = 1600 / (Rec_PlantRow * Rec_PlantTree);


                            layoutReady.setVisibility(View.VISIBLE);
                            layoutRubberAll.setVisibility(View.VISIBLE);
                            edtFarm_ID.setEnabled(false);
                            edtFarm_AreaRai.setEnabled(false);
                            btnReadyCal.setEnabled(false);
                            edtRec_PlantRows.setEnabled(false);
                            edtRec_PlantTree.setEnabled(false);

                            edtStatus.setEnabled(false);

                            txtIDShow.setText("ID แปลง : " + edtFarm_ID.getText().toString());
                            txtAreaShow.setText("พื้นที่รวม : " + String.valueOf(Farm_AreaTotalString));
                            txtTreeShow.setText("จำนวนต้นที่ต้องวัด : " + String.valueOf(RubberAll));
                            txtRubberNO.setText(String.valueOf(count_RubberNo + 1));


                        }catch (NumberFormatException e) {
                            Toast.makeText(getApplicationContext(),
                                    "กรุณาระบุปีเป็นตัวเลข พ.ศ.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(),
                            "กรุณากรอก ID แปลง,พื้นที่และระยะปลูกเป็นตัวเลข", Toast.LENGTH_SHORT)
                            .show();

                }


                break;

            case R.id.saveGirth:

                try {
                    girthFloat = Float.valueOf(edtRub_Girth.getText().toString().trim());

                    if(girthFloat<200){
                        beforeSaveGirth();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "เส้นรอบวงมีค่ามากเกินไป", Toast.LENGTH_SHORT)
                                .show();
                    }


                    } catch (NumberFormatException e){
                        Toast.makeText(getApplicationContext(),
                                "กรุณากรอกขนาดเส้นรอบวงเป็นตัวเลข", Toast.LENGTH_SHORT)
                                .show();
                    }

                break;

            case R.id.save:
                if(checkInput==0){
                    Status = "NewMember";
                }else{
                    Status = "Visitation "+edtStatus.getText().toString();
                }
                beforeSaveAll();

                break;

        }

    }

    private void beforeSaveGirth() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RecGirthActivity.this);
        builder.setMessage("ต้องการจะบันทึกเส้นรอบวงต้นที่  "+(String.valueOf(count_RubberNo+1))+" หรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener(){
            @Override
            public  void onClick(DialogInterface dialogInterface, int i) {
                girth_NO[count_RubberNo] = girthFloat;
                edtRub_Girth.setText("");

                count_RubberNo = count_RubberNo+1;
                txtRubberNO.setText(String.valueOf(count_RubberNo+1));

                if (count_RubberNo == RubberAll){
                    txtRubberNO.setText(String.valueOf(count_RubberNo));
                    btnSaveGirth.setEnabled(false);
                    edtRub_Girth.setEnabled(false);

                    ShowResult();
                }

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

    private void ShowResult() {
        double sum=0;
        try {


            layoutResult.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            edtRec_Note.setVisibility(View.VISIBLE);

            for (int i = 0; i < girth_NO.length; i++) {
                if (girth_NO[i] == 0) {
                    Rub_Death++;
                } else {
                    Rub_Alive++;
                }

                kgPerTree[i] = ((0.00004*(Math.pow(girth_NO[i],2.1963)))*944);
                if(kgPerTree[i] != 0){
                    sum = sum+kgPerTree[i];
                }

            }

            Rub_AlivePercent = (float) (Rub_Alive * 100) / RubberAll;
            Rub_DeathPercent = (float) (Rub_Death * 100) / RubberAll;

            RubPerRaiAlive = ((RubPerRai*Rub_AlivePercent))/100;
            AvgKgPerTree = sum/Rub_Alive;
            AvgKgPerRai = AvgKgPerTree*RubPerRaiAlive;
            AvgKgPerPlot = AvgKgPerRai*Area_Total;
            AvgTrunk = AvgKgPerPlot*0.7;
            AvgBranch = AvgKgPerPlot*0.3;
            TrunkTon = AvgKgPerRai*0.7/1000;
            BranchTon = AvgKgPerRai*0.3/1000;

            DecimalFormat float2digit = new DecimalFormat("#.##");
            Rub_AlivePercentString = String.valueOf(Float.valueOf(float2digit.format(Rub_AlivePercent)));
            Rub_DeathPercentString = String.valueOf(Float.valueOf(float2digit.format(Rub_DeathPercent)));

            AvgKgPerTreeString = String.valueOf(Float.valueOf(float2digit.format(AvgKgPerTree)));
            AvgKgPerRaiSting = String.valueOf(Float.valueOf(float2digit.format(AvgKgPerRai)));
            AvgKgPerPlotString = String.valueOf(Float.valueOf(float2digit.format(AvgKgPerPlot)));
            AvgTrunkString = String.valueOf(Float.valueOf(float2digit.format(AvgTrunk)));
            AvgBranchString = String.valueOf(Float.valueOf(float2digit.format(AvgBranch)));

            TrunkTonString = String.valueOf(Float.valueOf(float2digit.format(TrunkTon)));
            BranchTonString = String.valueOf(Float.valueOf(float2digit.format(BranchTon)));

            DecimalFormat floatNoDigit = new DecimalFormat("#.#");
            RubPerRaiString = String.valueOf((Float.valueOf(floatNoDigit.format(RubPerRai))));
            RubPerRaiAliveString = String.valueOf(Float.valueOf(floatNoDigit.format(RubPerRaiAlive)));

            txtRub_All.setText(String.valueOf(RubberAll));
            txtRub_Alive.setText(String.valueOf(Rub_Alive));
            txtRub_Death.setText(String.valueOf(Rub_Death));

            txtRub_AllPercent.setText("100%");
            txtRub_AlivePercent.setText(Rub_AlivePercentString+"%");
            txtRub_DeathPercent.setText(Rub_DeathPercentString+"%");
            txtRubPerRai.setText(String.valueOf(RubPerRai));
            txtRubPerRaiAlive.setText(String.valueOf(RubPerRaiAliveString));

            txtAvgKgPerRai.setText(String.valueOf(AvgKgPerRaiSting)+" kg");
            txtAvgKgPerPlot.setText(String.valueOf(AvgKgPerPlotString)+ " kg");
            txtAvgTrunk.setText(String.valueOf(AvgTrunkString)+ " kg");
            txtAvgBranch.setText(String.valueOf(AvgBranchString)+" kg");
            txtTrunkTon.setText(String.valueOf(TrunkTonString)+" ตัน");
            txtBranckTon.setText(String.valueOf(BranchTonString)+" ตัน");

        }catch (Exception e){
            Log.d("Result",String.valueOf(e));
        }
    }

    private void beforeSaveAll(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(RecGirthActivity.this);
        builder.setMessage("ต้องการจะบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener(){
            @Override
            public  void onClick(DialogInterface dialogInterface, int i) {
                saveRecResulttoSQlite();

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

    private void saveRecResulttoSQlite(){
        Link_ID = edtFarm_ID.getText().toString().trim()+"-"+Farm_Date;

        DBFile = DATABASE_REC_GIRTH_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Rec_Girth_DB_OpenHelper obj = new ExternalStorage_Rec_Girth_DB_OpenHelper(getApplicationContext(),DBFile);
        db = obj.getReadableDatabase();
        long checkResult = obj.insertRecResult(
                Link_ID,
                dateNow,
                edtFarm_ID.getText().toString().trim(),
                Emp_ID,
                Farm_AreaTotalString,
                edtRec_PlantRows.getText().toString().trim(),
                edtRec_PlantTree.getText().toString().trim(),
                Status,
                edtRec_Note.getText().toString().trim(),
                db);
        long checkGirth = -1;

        for(int i=0;i<girth_NO.length; i++){
            checkGirth = obj.insertGirth(Link_ID,
                    dateNow,
                    edtFarm_ID.getText().toString(),
                    String.valueOf(i+1),
                    girth_NO[i],
                    db);
        }
            
        obj.close();

        
        if (checkResult != -1 && checkGirth != -1 ){
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


    private String getDateTime() {
        int yearint;
        String year;

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());


        Calendar cal2 = Calendar.getInstance();
        yearint = cal2.get(Calendar.YEAR)+543;
        year = String.valueOf(yearint);

        SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                "HH:mm:ss");
        Calendar cal3 = Calendar.getInstance();
        String time = String.valueOf((dateFormat3.format(cal3.getTime())));

        Farm_Date = date + "-" + year + " "+time;
        Log.d("year",String.valueOf(Farm_Date));

        return  Farm_Date;
    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(RecGirthActivity.this);
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
