package com.panelplus.pnpmember.activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Backup_Point_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_FarmGeo_DB_OpenHelper;
import com.panelplus.pnpmember.module.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class RegisFarmConfirmActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    private int checkPicFarm = 0;

    //Spinner sp_peetor;

    private Dialog Popup_dialog;

    @SuppressLint("SdCardPath")
    String DATABASE_FILE_PATH = "/mnt/sdcard/MAPDB";


    // Database Name
    private static final String DATABASE_FARMGEO_DB = "FARMGEO_DB";
    // Table Name
    private static final String TABLE_FARM_GEO_DATA = "farm_geo_data";
    private static final String TABLE_OBJECT_DATA = "object_see";
    //get user
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    //mark point
    private static final String DATABASE_backup_markpoint = "backup_markpoint";

    SQLiteDatabase mydb = null;

    Button btnSaveRegis;
    Button btn_regis_exit;
    Button btnLetCamera;

    EditText txtQT;
    //EditText txtLinkID;
    EditText txtYearOfPlant;
    EditText txtRubberPerRai;

    ImageView imgFarm;

    String Emp_ID = "";
    String UserZone = "";
    String Polygon = null;
    String Area = null;

    Spinner mRubberBreed;
    Spinner spnRubberBreed;
    private ArrayList<String> mRub = new ArrayList<String>();

    CheckBox Farm_Status;
    String Farm_StatusString;

    private EditText DirNorth_Use, DirNorth_Owner, DirNorth_Tel;
    private EditText DirEast_Use, DirEast_Owner, DirEast_Tel;
    private EditText DirSouth_Use, DirSouth_Owner, DirSouth_Tel;
    private EditText DirWest_Use, DirWest_Owner, DirWest_Tel;

    CheckBox[] EvaFarm_NO = new CheckBox[14];
    String[] EvaFarmString = new String[14];

    EditText[] EvaFarmSuggest = new EditText[14];
    String[] EvaFarmSuggestString = new String[14];
    private int numimage = 0;
    //find object
    private Button btnFindObject;
    private EditText ObjLocalNameSrc;

    String X = "";
    String Y = "";
    int X1 = 0;
    int Y1 = 0;

    String Farm_Date;
    String Link_ID;

    //----------------------- Camera  ---------------------

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri; // file url to store image/video
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap casheBitmap;
    byte[] default_byte = {0};


    //===========image=========//
    private String time_str;
    private byte[] image;
    private byte[] PicFarm;

    File Path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis_farm_confirm);

        InitWidget();
        spinner();
        //get user
        Emp_ID = getEmpID();
        UserZone = getUserZone();
        Farm_Date = getDateTime();

        EvaFarmSuggest[1].setText("พื้นที่ราบ");
//        File directDownload = new File(DATABASE_FILE_PATH);
//
//        if(!directDownload.exists())
//        {
//            if(directDownload.mkdir())
//            {
//                //directory is created;
//            }
//
//        }

        CreateFarmRegisDB(this);

        Bundle b = getIntent().getExtras();
        Polygon = b.getString("POLYGON");
        Area = b.getString("Area");
        X = b.getString("Poly_X");
        Y = b.getString("Poly_Y");

        Log.d("polygon = ", Polygon.toString());
    }

    private void spinner() {
        mRubberBreed = (Spinner) findViewById(R.id.spnRubberBreed);
        mRub.add("RRIM 600");
        mRub.add("RRIT 251");
        ArrayAdapter<String> mRubber = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mRub);
        mRubberBreed.setAdapter(mRubber);
    }


    private void CreateFarmRegisDB(Context context) {

        if (context != null) {
            String folderName = "MapDB"; // folder name
            String folder = context.getApplicationContext().getFilesDir() + "/" + folderName; // folder path
            File appDbDir = new File(folder); // create folder
            if (!appDbDir.exists()) {
                appDbDir.mkdirs(); // create folder if it doesn't exist
            }
            this.Path = new File(folder);
        } else {
            // handle null context case
        }


        mydb = context.openOrCreateDatabase(DATABASE_FARMGEO_DB + "-" + UserZone + ".sqlite", Context.MODE_PRIVATE, null);
        String createTableQuery = "CREATE TABLE IF NOT EXISTS farm_geo_data (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "  Link_ID TEXT ," +
                "  qouta_ID TEXT ," +
                "  Emp_ID TEXT ," +
                "  Farm_Date TEXT ," +
                "  Farm_Area TEXT ," +
                "  Farm_Polygon TEXT ," +
                "  Farm_GeoStatus TEXT ," +
                "  Farm_Pic BLOB ," +
                "  Farm_YearOfPlant TEXT ," +
                "  Farm_RubberBreed TEXT ," +
                "  Farm_RubberPerRai TEXT ," +
                "  Farm_Status TEXT ," +
                "   DirNorth_Use TEXT ," +
                "   DirNorth_Owner TEXT ," +
                "   DirNorth_Tel TEXT ," +
                "   DirEast_Use TEXT ," +
                "   DirEast_Owner TEXT ," +
                "   DirEast_Tel TEXT ," +
                "   DirSouth_Use TEXT ," +
                "   DirSouth_Owner TEXT ," +
                "   DirSouth_Tel TEXT ," +
                "   DirWest_Use TEXT ," +
                "   DirWest_Owner TEXT ," +
                "   DirWest_Tel TEXT ," +
                "   EvaFarm_NO1 TEXT ," +
                "   EvaFarmSuggest_NO1 TEXT ," +
                "   EvaFarm_NO2 TEXT ," +
                "   EvaFarmSuggest_NO2 TEXT ," +
                "   EvaFarm_NO3 TEXT ," +
                "   EvaFarmSuggest_NO3 TEXT ," +
                "   EvaFarm_NO4 TEXT ," +
                "   EvaFarmSuggest_NO4 TEXT ," +
                "   EvaFarm_NO5 TEXT ," +
                "   EvaFarmSuggest_NO5 TEXT ," +
                "   EvaFarm_NO6 TEXT ," +
                "   EvaFarmSuggest_NO6 TEXT ," +
                "   EvaFarm_NO7 TEXT ," +
                "   EvaFarmSuggest_NO7 TEXT ," +
                "   EvaFarm_NO8 TEXT ," +
                "   EvaFarmSuggest_NO8 TEXT ," +
                "   EvaFarm_NO9 TEXT ," +
                "   EvaFarmSuggest_NO9 TEXT ," +
                "   EvaFarm_NO10 TEXT ," +
                "   EvaFarmSuggest_NO10 TEXT ," +
                "   EvaFarm_NO11 TEXT ," +
                "   EvaFarmSuggest_NO11 TEXT ," +
                "   EvaFarm_NO12 TEXT ," +
                "   EvaFarmSuggest_NO12 TEXT ," +
                "   EvaFarm_NO13 TEXT ," +
                "   EvaFarmSuggest_NO13 TEXT ," +
                "   EvaFarm_NO14 TEXT ," +
                "   EvaFarmSuggest_NO14 TEXT);";

        String createTableQuery2 = "CREATE TABLE IF NOT EXISTS object_see (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   Link_ID TEXT ," +
                "   Obj_LocalName TEXT ," +
                "   Obj_Status TEXT);";

        try {
            this.mydb.execSQL(createTableQuery);
            this.mydb.execSQL(createTableQuery2);
            Log.e("Check", "Database created successfully");
            Toast.makeText(context, "Database created successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Check", "Failed to create database: " + e.getMessage());
            Toast.makeText(context, "Failed to create database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    private void InitWidget() {
        txtQT = (EditText) findViewById(R.id.txtQT);
        //txtLinkID = (EditText) findViewById(R.id.txtLinkID);
        txtYearOfPlant = (EditText) findViewById(R.id.txtYearOfPlant);
        spnRubberBreed = (Spinner) findViewById(R.id.spnRubberBreed);
        txtRubberPerRai = (EditText) findViewById(R.id.txtRubberPerRai);

        btnSaveRegis = (Button) findViewById(R.id.btnSaveRegis);
        btn_regis_exit = (Button) findViewById(R.id.btnExit);
        btnLetCamera = (Button) findViewById(R.id.btnLetCamera);
        btnFindObject = (Button) findViewById(R.id.findObject);

        Farm_Status = (CheckBox) findViewById(R.id.Farm_Status);

        imgFarm = (ImageView) findViewById(R.id.imgFarm);

        btn_regis_exit.setOnClickListener(this);
        btnSaveRegis.setOnClickListener(this);
        btnLetCamera.setOnClickListener(this);
        btnFindObject.setOnClickListener(this);

        DirNorth_Use = (EditText) findViewById(R.id.DirNorth_Use);
        DirNorth_Owner = (EditText) findViewById(R.id.DirNorth_Owner);
        DirNorth_Tel = (EditText) findViewById(R.id.DirNorth_Tel);

        DirEast_Use = (EditText) findViewById(R.id.DirEast_Use);
        DirEast_Owner = (EditText) findViewById(R.id.DirEast_Owner);
        DirEast_Tel = (EditText) findViewById(R.id.DirEast_Tel);

        DirSouth_Use = (EditText) findViewById(R.id.DirSouth_Use);
        DirSouth_Owner = (EditText) findViewById(R.id.DirSouth_Owner);
        DirSouth_Tel = (EditText) findViewById(R.id.DirSouth_Tel);

        DirWest_Use = (EditText) findViewById(R.id.DirWest_Use);
        DirWest_Owner = (EditText) findViewById(R.id.DirWest_Owner);
        DirWest_Tel = (EditText) findViewById(R.id.DirWest_Tel);

        Farm_Status = (CheckBox) findViewById(R.id.Farm_Status);

        EvaFarm_NO[0] = (CheckBox) findViewById(R.id.evaFarm_no01);
        EvaFarm_NO[1] = (CheckBox) findViewById(R.id.evaFarm_no02);
        EvaFarm_NO[2] = (CheckBox) findViewById(R.id.evaFarm_no03);
        EvaFarm_NO[3] = (CheckBox) findViewById(R.id.evaFarm_no04);
        EvaFarm_NO[4] = (CheckBox) findViewById(R.id.evaFarm_no05);
        EvaFarm_NO[5] = (CheckBox) findViewById(R.id.evaFarm_no06);
        EvaFarm_NO[6] = (CheckBox) findViewById(R.id.evaFarm_no07);
        EvaFarm_NO[7] = (CheckBox) findViewById(R.id.evaFarm_no08);
        EvaFarm_NO[8] = (CheckBox) findViewById(R.id.evaFarm_no09);
        EvaFarm_NO[9] = (CheckBox) findViewById(R.id.evaFarm_no10);
        EvaFarm_NO[10] = (CheckBox) findViewById(R.id.evaFarm_no11);
        EvaFarm_NO[11] = (CheckBox) findViewById(R.id.evaFarm_no12);
        EvaFarm_NO[12] = (CheckBox) findViewById(R.id.evaFarm_no13);
        EvaFarm_NO[13] = (CheckBox) findViewById(R.id.evaFarm_no14);


        EvaFarmSuggest[0] = (EditText) findViewById(R.id.evaFarm01);
        EvaFarmSuggest[1] = (EditText) findViewById(R.id.evaFarm02);
        EvaFarmSuggest[2] = (EditText) findViewById(R.id.evaFarm03);
        EvaFarmSuggest[3] = (EditText) findViewById(R.id.evaFarm04);
        EvaFarmSuggest[4] = (EditText) findViewById(R.id.evaFarm05);
        EvaFarmSuggest[5] = (EditText) findViewById(R.id.evaFarm06);
        EvaFarmSuggest[6] = (EditText) findViewById(R.id.evaFarm07);
        EvaFarmSuggest[7] = (EditText) findViewById(R.id.evaFarm08);
        EvaFarmSuggest[8] = (EditText) findViewById(R.id.evaFarm09);
        EvaFarmSuggest[9] = (EditText) findViewById(R.id.evaFarm10);
        EvaFarmSuggest[10] = (EditText) findViewById(R.id.evaFarm11);
        EvaFarmSuggest[11] = (EditText) findViewById(R.id.evaFarm12);
        EvaFarmSuggest[12] = (EditText) findViewById(R.id.evaFarm13);
        EvaFarmSuggest[13] = (EditText) findViewById(R.id.evaFarm14);

        EvaFarm_NO[1].setOnCheckedChangeListener(this);

        ObjLocalNameSrc = (EditText) findViewById(R.id.ObjectLocalNameSearch);


    }

    private String getDateTime() {
        int yearint;
        String year;

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());


        Calendar cal2 = Calendar.getInstance();
        yearint = cal2.get(Calendar.YEAR) + 543;
        year = String.valueOf(yearint);

        SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                "HH:mm:ss");
        Calendar cal3 = Calendar.getInstance();
        String time = String.valueOf((dateFormat3.format(cal3.getTime())));

        Farm_Date = date + "-" + year + " " + time;
        Log.d("year", String.valueOf(Farm_Date));

        return Farm_Date;
    }

    //check enable button
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.evaFarm_no02:
                if (isChecked) {
                    EvaFarmSuggest[1].setText("");
                } else {
                    EvaFarmSuggest[1].setText("พื้นที่ราบ");
                }
                break;


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveRegis:

                for (int i = 0; i < 14; i++) {
                    EvaFarmString[i] = getValueCheckbox(EvaFarm_NO[i]);
                    EvaFarmSuggestString[i] = EvaFarmSuggest[i].getText().toString().trim();

                    if (EvaFarmSuggestString[i].isEmpty()) {
                        EvaFarmSuggestString[i] = "";
                    }
                }

                //F_ID= lblf_id.getText().toString().trim()+txtf_id.getText().toString().trim();

                if (txtQT.getText().toString().trim().isEmpty()
                        || txtYearOfPlant.getText().toString().isEmpty()
                        || txtRubberPerRai.getText().toString().isEmpty()
                        || PicFarm == null) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(RegisFarmConfirmActivity.this);
                    builder.setMessage("กรุณาใส่ข้อมูลให้ครบถ้วน และแนบรูปถ่ายก่อนการบันทึก");
                    builder.setCancelable(true);
                    builder.setNegativeButton("ตกลง", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                } else {

                    beforeSave();

                }


                break;

            case R.id.btnExit:

                RegisFarmConfirmActivity.this.finish();

                break;

            case R.id.btnLetCamera:
                numimage = 1;
                captureImageCamera();

                break;

            case R.id.findObject:
                if (txtQT.getText().toString().isEmpty() ||
                        ObjLocalNameSrc.getText().toString().isEmpty()) {

                    if (txtQT.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                        "กรุรากรอกเลขโควต้าก่อนบันทึก", Toast.LENGTH_SHORT)
                                .show();
                    }

                    if (ObjLocalNameSrc.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                        "กรุรากรอกสิ่งที่พบก่อนการบันทึก", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    beforeSaveObject(this);
                }
        }


    }

    public void beforeSave() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisFarmConfirmActivity.this);
        builder.setMessage("ต้องการจะบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                saveFarmGeoToSQlite();
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

    private void saveFarmGeoToSQlite() {


        if (Farm_Status.isChecked()) {
            Farm_StatusString = "R";
        }

        Link_ID = txtQT.getText().toString().trim() + "-" + Farm_Date;


        SQLiteDatabase db;
        String DBFile = DATABASE_FARMGEO_DB + "-" + UserZone + ".sqlite";
        long number = 0;
        ExternalStorage_FarmGeo_DB_OpenHelper obj = new ExternalStorage_FarmGeo_DB_OpenHelper(getApplicationContext(), DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getReadableDatabase();
            number = obj.InsertDataFarmGeoLocation(
                    txtQT.getText().toString().trim(),
                    Link_ID,
                    Emp_ID,
                    Farm_Date,
                    Area,
                    Polygon,
                    "S",
                    PicFarm,
                    txtYearOfPlant.getText().toString().trim(),
                    spnRubberBreed.getSelectedItem().toString(),
                    txtRubberPerRai.getText().toString().trim(),
                    Farm_StatusString,
                    DirNorth_Use.getText().toString().trim(),
                    DirNorth_Owner.getText().toString().trim(),
                    DirNorth_Tel.getText().toString().trim(),

                    DirEast_Use.getText().toString().trim(),
                    DirEast_Owner.getText().toString().trim(),
                    DirEast_Tel.getText().toString().trim(),

                    DirSouth_Use.getText().toString().trim(),
                    DirSouth_Owner.getText().toString().trim(),
                    DirSouth_Tel.getText().toString().trim(),

                    DirWest_Use.getText().toString().trim(),
                    DirWest_Owner.getText().toString().trim(),
                    DirWest_Tel.getText().toString().trim(),

                    EvaFarmString, EvaFarmSuggestString,
                    db);

            if (number < 0) {
                Toast.makeText(getApplicationContext(),
                                "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT)
                        .show();
            } else {
                String DBFile2 = DATABASE_backup_markpoint + ".sqlite";
                ExternalStorage_Backup_Point_DB_OpenHelper obj2 = new ExternalStorage_Backup_Point_DB_OpenHelper(getApplicationContext(), DBFile2);
                if (obj2.databaseFileExists()) {
                    db = obj2.getReadableDatabase();
                    obj2.DeleteAllData(db);
                    obj2.close();
                }

                Toast.makeText(getApplicationContext(),
                                "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }

            obj.close();
        } else {

            Toast.makeText(getApplicationContext(),
                            "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void beforeSaveObject(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisFarmConfirmActivity.this);
        builder.setMessage("ต้องการจะบันทึกสิ่งที่พบหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                saveObjectToSqlite(context);
                txtQT.setEnabled(false);
                ObjLocalNameSrc.setText("");
                dialogInterface.cancel();

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

    private void saveObjectToSqlite(Context context) {

        Link_ID = txtQT.getText().toString().trim() + "-" + Farm_Date;

        String DBFile = DATABASE_FARMGEO_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_FarmGeo_DB_OpenHelper obj2 = new ExternalStorage_FarmGeo_DB_OpenHelper(context, DBFile);
        if (obj2.databaseFileExists()) {
            this.mydb = obj2.getWritableDatabase(); // เปิดฐานข้อมูลให้เป็นแบบเขียนได้
            long check = -1;
            try {
                this.mydb.beginTransaction();
                check = obj2.insertDataObject(Link_ID,
                        ObjLocalNameSrc.getText().toString().trim(),
                        "EvaFarm",
                        mydb);

                mydb.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("Check", "Failed to insert data into database: " + e.getMessage());
                Toast.makeText(context, "Failed to insert data into database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                mydb.endTransaction();
                obj2.close();
            }

            if (check != -1) {
                Toast.makeText(getApplicationContext(), "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, EvaMemberActivity.class));
            } else {
                Log.e("Check", "บันทึกข้อมูลล้มเหลว");
            }
        }
    }


    private String getValueCheckbox(CheckBox EvaFarm_NO) {
        if (EvaFarm_NO.isChecked()) {
            return "1";
        }
        return "0";
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


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();

        if (android.os.Build.VERSION.SDK_INT >= 26) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("ออกจากการลงทะเบียนแปลง")
                .setMessage("ออกจากการลงทะเบียนแปลงใช่หรือไม่?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Link_ID = txtQT.getText().toString().trim() + "-" + Farm_Date;

                        SQLiteDatabase db;
                        String DBFile = DATABASE_FARMGEO_DB + "-" + UserZone + ".sqlite";
                        ExternalStorage_FarmGeo_DB_OpenHelper obj = new ExternalStorage_FarmGeo_DB_OpenHelper(getApplicationContext(), DBFile);
                        db = obj.getReadableDatabase();
                        long a = obj.DeleteDataObject(Link_ID, db);
                        obj.close();

                        RegisFarmConfirmActivity.this.finish();
                    }

                }).setNegativeButton("No", null).show();
    }


    private void captureImageCamera() {
        if (checkCameraPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

        } else {
            requestCameraPermission();
        }
    }


    private boolean checkCameraPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ได้รับอนุญาตการเข้าถึงกล้อง
                Log.e("Check", "Permission Ok");
                captureImageCamera();

            } else {
                // การอนุญาตถูกปฏิเสธ
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
        outState.clear();
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.fileUri = (Uri) savedInstanceState.getParcelable("file_uri");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (numimage == 1) {

                Log.e("Check", "numimage == 5 : " + "ใช้ในการสมัครโครงการ FSC");

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                Bitmap textBitmap = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(textBitmap);

                canvas.drawBitmap(imageBitmap, 0, 0, null);

                float centerX = textBitmap.getWidth() / 2f;
                float centerY = textBitmap.getHeight() / 2f;

                String text = "ใช้ในการสมัครโครงการ FSC";
                Paint textPaint = new Paint();
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(12);
                textPaint.setAntiAlias(true);

                float textWidth = textPaint.measureText(text);
                float textHeight = textPaint.getTextSize();

                // หากต้องการเอียงข้อความ 45 องศา
                Matrix matrix = new Matrix();
                matrix.postRotate(45, centerX, centerY);
                canvas.setMatrix(matrix);

                float textX = centerX - (textWidth / 2f);
                float textY = centerY + (textHeight / 2f);

                canvas.drawText(text, textX, textY, textPaint);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                textBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                this.PicFarm = byteArray;

                int newWidth = 720;
                int newHeight = 1280;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(textBitmap, newWidth, newHeight, true);

                ImageView imageView = findViewById(R.id.imgFarm);
                imageView.setImageBitmap(scaledBitmap);


            }
        }

    }

}



