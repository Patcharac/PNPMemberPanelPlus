package com.panelplus.pnpmember.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Visit_DB_OpenHelper;
import com.panelplus.pnpmember.module.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static androidx.media.MediaBrowserServiceCompat.RESULT_OK;

public class VisitFarmActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    String Farm_ID;

    ImageView imgFarm;
    Button btnLetCamera;
    File Path = null;
    private Button btnSave;
    private Button btnFindObject;
    private EditText edtFarm_ID;
    private EditText ObjLocalNameSrc;

    private EditText rubberYearOfPlant, rubberBreed;
    private CheckBox checkPlant;
    int rubberYearOfPlantInt = 0;
    private int checkTextPB = 1;
    private int checkTextPlant = 1;

    private CheckBox[] VisitFarm_NO = new CheckBox[12];
    private String[] VisitFarmString = new String[10];

    private EditText[] VisitFarmSuggest_NO = new EditText[12];
    private String[] VisitFarmSuggestString = new String[12];

    private int checkSuggest = 0;
    private int checkTextSuggest = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String Emp_ID = "";
    private String UserZone = "";

    private String dateNow;
    private int yearNow;

    //-----------DB-----------//
    SQLiteDatabase mydb = null;

    private String DBFile;
    SQLiteDatabase db;

    @SuppressLint("SdCardPath")
    String DATABASE_FILE_PATH = "/sdcard/MapDB/";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";

    private static final String DATABASE_VISIT_DB = "VISIT_DB";

    private static final String TABLE_VISIT_FARM_DATA = "visit_farm_data";
    private static final String TABLE_OBJECT_DATA = "object_see";

    //----------------------- Camera  ---------------------
    private ActivityResultLauncher<Uri> captureImageLauncher;
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri; // file url to store image/video

    private Bitmap casheBitmap;
    byte[] default_byte = {0};
    private static int RESULT_LOAD_IMAGE = 1;

    //===========image=========//
    private String time_str;
    private byte[] image;
//    private byte[] PicFarm;

    private byte[] PicFarm = null;
    private int numimage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_farm);

        InitWidget();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }


        Bundle b = getIntent().getExtras();
        Farm_ID = b.getString("Farm_ID");
        edtFarm_ID.setText(Farm_ID);
        edtFarm_ID.setEnabled(false);

        //get user
        Emp_ID = getEmpID();
        UserZone = getUserZone();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 543;

        dateNow = new SimpleDateFormat("dd-MM",
                Locale.getDefault()).format(new Date());

        dateNow = dateNow + "-" + String.valueOf(year);
        Log.d("date", String.valueOf(dateNow));

        yearNow = year;
        createPrimaryDB(this);


    }

    private void InitWidget() {
        btnSave = (Button) findViewById(R.id.save);
        btnSave.setOnClickListener(this);

        edtFarm_ID = (EditText) findViewById(R.id.Farm_ID);

        VisitFarm_NO[0] = (CheckBox) findViewById(R.id.VisitFarm_NO1);
        VisitFarm_NO[1] = (CheckBox) findViewById(R.id.VisitFarm_NO2);
        VisitFarm_NO[2] = (CheckBox) findViewById(R.id.VisitFarm_NO3);
        VisitFarm_NO[3] = (CheckBox) findViewById(R.id.VisitFarm_NO4);
        VisitFarm_NO[4] = (CheckBox) findViewById(R.id.VisitFarm_NO5);
        VisitFarm_NO[5] = (CheckBox) findViewById(R.id.VisitFarm_NO6);
        VisitFarm_NO[6] = (CheckBox) findViewById(R.id.VisitFarm_NO7);
        VisitFarm_NO[7] = (CheckBox) findViewById(R.id.VisitFarm_NO8);
        VisitFarm_NO[8] = (CheckBox) findViewById(R.id.VisitFarm_NO9);
        VisitFarm_NO[9] = (CheckBox) findViewById(R.id.VisitFarm_NO10);
        VisitFarm_NO[10] = (CheckBox) findViewById(R.id.VisitFarm_NO11);
        VisitFarm_NO[11] = (CheckBox) findViewById(R.id.VisitFarm_NO12);

        VisitFarm_NO[2].setOnCheckedChangeListener(this);
        VisitFarm_NO[3].setOnCheckedChangeListener(this);
        VisitFarm_NO[8].setOnCheckedChangeListener(this);
        VisitFarm_NO[9].setOnCheckedChangeListener(this);
        VisitFarm_NO[10].setOnCheckedChangeListener(this);


        VisitFarmSuggest_NO[0] = (EditText) findViewById(R.id.VisitFarmSuggest_NO1);
        VisitFarmSuggest_NO[1] = (EditText) findViewById(R.id.VisitFarmSuggest_NO2);
        VisitFarmSuggest_NO[2] = (EditText) findViewById(R.id.VisitFarmSuggest_NO3);
        VisitFarmSuggest_NO[3] = (EditText) findViewById(R.id.VisitFarmSuggest_NO4);
        VisitFarmSuggest_NO[4] = (EditText) findViewById(R.id.VisitFarmSuggest_NO5);
        VisitFarmSuggest_NO[5] = (EditText) findViewById(R.id.VisitFarmSuggest_NO6);
        VisitFarmSuggest_NO[6] = (EditText) findViewById(R.id.VisitFarmSuggest_NO7);
        VisitFarmSuggest_NO[7] = (EditText) findViewById(R.id.VisitFarmSuggest_NO8);
        VisitFarmSuggest_NO[8] = (EditText) findViewById(R.id.VisitFarmSuggest_NO9);
        VisitFarmSuggest_NO[9] = (EditText) findViewById(R.id.VisitFarmSuggest_NO10);
        VisitFarmSuggest_NO[10] = (EditText) findViewById(R.id.VisitFarmSuggest_NO11);
        VisitFarmSuggest_NO[11] = (EditText) findViewById(R.id.VisitFarmSuggest_NO12);
        rubberYearOfPlant = (EditText) findViewById(R.id.rubberYearOfPlant);
        rubberBreed = (EditText) findViewById(R.id.rubberBreed);
        checkPlant = (CheckBox) findViewById(R.id.checkPlant);

        checkPlant.setOnCheckedChangeListener(this);

        ObjLocalNameSrc = (EditText) findViewById(R.id.ObjectLocalNameSearch);
        btnFindObject = (Button) findViewById(R.id.findObject);
        btnFindObject.setOnClickListener(this);

        imgFarm = (ImageView) findViewById(R.id.imgFarm);

        btnLetCamera = (Button) findViewById(R.id.btnLetCamera);
        btnLetCamera.setOnClickListener(this);

    }

    private void createPrimaryDB(Context context) {
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

                String createTableQuery = "CREATE TABLE IF NOT EXISTS visit_farm_data (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "   VisitFarm_Date ," +
                        "   Farm_ID TEXT ," +
                        "   Emp_ID TEXT ," +
                        "   Farm_Pic BLOB ," +
                        "   VisitFarm_NO1 TEXT ," +
                        "   VisitFarmSuggest_NO1 TEXT ," +
                        "   VisitFarm_NO2 TEXT ," +
                        "   VisitFarmSuggest_NO2 TEXT ," +
                        "   VisitFarm_NO3 TEXT ," +
                        "   VisitFarmSuggest_NO3 TEXT ," +
                        "   VisitFarm_NO4 TEXT ," +
                        "   VisitFarmSuggest_NO4 TEXT ," +
                        "   VisitFarm_NO5 TEXT ," +
                        "   VisitFarmSuggest_NO5 TEXT ," +
                        "   VisitFarm_NO6 TEXT ," +
                        "   VisitFarmSuggest_NO6 TEXT ," +
                        "   VisitFarm_NO7 TEXT ," +
                        "   VisitFarmSuggest_NO7 TEXT ," +
                        "   VisitFarm_NO8 TEXT ," +
                        "   VisitFarmSuggest_NO8 TEXT ," +
                        "   VisitFarm_NO9 TEXT ," +
                        "   VisitFarmSuggest_NO9 TEXT ," +
                        "   rubberYearOfPlant INTEGER  ," +
                        "   rubberYearOld INTEGER ," +
                        "   rubberBreed TEXT);";

                String createTableQuery2 = "CREATE TABLE IF NOT EXISTS object_see (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "   Farm_ID TEXT ," +
                        "   Obj_LocalName TEXT ," +
                        "   Obj_Status TEXT);";

                this.mydb.execSQL(createTableQuery);
                this.mydb.execSQL(createTableQuery2);
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

            case R.id.btnLetCamera:
                numimage = 1;
                captureImageCamera();

                break;

            case R.id.save:

                try {
                    int b = Integer.parseInt(edtFarm_ID.getText().toString());

                    if (!rubberYearOfPlant.getText().toString().isEmpty()) {
                        rubberYearOfPlantInt = Integer.parseInt(rubberYearOfPlant.getText().toString().trim());
                    }

                    for (int i = 0; i < 9; i++) {
                        VisitFarmString[i] = getValueCheckbox(VisitFarm_NO[i]);

                        VisitFarmSuggestString[i] = VisitFarmSuggest_NO[i].getText().toString().trim();
                        if (VisitFarmSuggestString[i].isEmpty()) {
                            VisitFarmSuggestString[i] = "";
                        }
                    }
                    //check Suggestion
                    if (checkSuggest == 1 && VisitFarmSuggestString[8] == "") {
                        checkTextSuggest = 0;
                    } else {
                        checkTextSuggest = 1;
                    }

                    //check plant
                    if (rubberYearOfPlant.getText().toString().isEmpty() ||
                            rubberBreed.getText().toString().isEmpty()) {
                        checkTextPB = 0;
                    } else {
                        checkTextPB = 1;
                    }

                    if (checkPlant.isChecked() && checkTextPB == 0) {
                        checkTextPlant = 0;
                    } else {
                        checkTextPlant = 1;
                    }

                    //before save
                    if (edtFarm_ID.getText().toString().isEmpty() ||
                            checkTextSuggest == 0 || checkTextPlant == 0 ||
                            PicFarm == null) {

                        if (edtFarm_ID.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(),
                                            "กรุณากรอก ID สมาชิกและ ID แปลงก่อนการบันทึก", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        if (checkTextSuggest == 0) {
                            Toast.makeText(getApplicationContext(),
                                            "กรุณกรอกคำแนะนำที่จำเป็นก่อนการบันทึก", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        if (checkTextPlant == 0) {
                            Toast.makeText(getApplicationContext(),
                                            "กรุณกรอกปีที่ปลูกและพันธ์ยางก่อนการบันทึก", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        if (PicFarm == null) {
                            Toast.makeText(getApplicationContext(),
                                            "กรุณาถ่ายรูปแปลงก่อนการบันทึก", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } else {

                        beforeSave(this);

                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(),
                                    "กรุณากรอก IDสมาชิก IDสวนยางพารา เป็นตัวเลข", Toast.LENGTH_SHORT)
                            .show();
                }


                break;

            case R.id.findObject:
                if (edtFarm_ID.getText().toString().isEmpty() ||
                        ObjLocalNameSrc.getText().toString().isEmpty()) {
                    if (edtFarm_ID.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                        "กรุณากรอก ID แปลงก่อนบันทึก", Toast.LENGTH_SHORT)
                                .show();
                    }

                    if (ObjLocalNameSrc.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                        "กรุณากรอกสิ่งที่พบก่อนการบันทึก", Toast.LENGTH_SHORT)
                                .show();

                    }
                } else {
                    beforeSaveObject(this);
                }
                break;
        }

    }

    public void beforeSave(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VisitFarmActivity.this);
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
                check = obj.insertDataVisitFarm(dateNow,
                        edtFarm_ID.getText().toString().trim(),
                        Emp_ID,
                        PicFarm,
                        rubberYearOfPlantInt,
                        yearNow,
                        rubberBreed.getText().toString().trim(),
                        VisitFarmString,
                        VisitFarmSuggestString,
                        mydb)
                ;
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

    public void beforeSaveObject(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VisitFarmActivity.this);
        builder.setMessage("ต้องการจะบันทึกสิ่งที่พบหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                saveObjectToSqlite(context);
                edtFarm_ID.setEnabled(false);
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
        String DBFile = DATABASE_VISIT_DB + "-" + UserZone + ".sqlite";
        Log.e("Check", "Check Log DBFILE " + DBFile);
        ExternalStorage_Visit_DB_OpenHelper obj = new ExternalStorage_Visit_DB_OpenHelper(context, DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getWritableDatabase(); // เปิดฐานข้อมูลให้เป็นแบบเขียนได้
            Log.e("Check", "Check db : " + db.getPath());
            long check = -1;
            try {
                db.beginTransaction();
                Log.e("Check", "Check edtFarm_ID : " + edtFarm_ID.getText().toString().trim());
                check = obj.insertDataObject(edtFarm_ID.getText().toString().trim(),
                        ObjLocalNameSrc.getText().toString().trim(), "VisitFarm"
                        , db);

                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("Check", "Failed to insert data into database: " + e.getMessage());
                Toast.makeText(context, "Failed to insert data into database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                db.endTransaction();
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


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.VisitFarm_NO9:
                if (isChecked) {
                    VisitFarmSuggest_NO[8].setEnabled(true);
                    checkSuggest = 1;
                } else {
                    VisitFarmSuggest_NO[8].setEnabled(false);
                    VisitFarmSuggest_NO[8].setText("");
                    checkSuggest = 0;
                }

                break;

            case R.id.checkPlant:
                if (isChecked) {
                    rubberBreed.setEnabled(true);
                    rubberYearOfPlant.setEnabled(true);
                } else {
                    rubberBreed.setEnabled(false);
                    rubberYearOfPlant.setEnabled(false);

                    rubberBreed.setText("");
                    rubberYearOfPlant.setText("");
                }
                break;
        }

    }

    private String getValueCheckbox(CheckBox VisitFarm_NO) {
        if (VisitFarm_NO.isChecked()) {
            return "1";
        } else {
            return "0";
        }
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(VisitFarmActivity.this);
        builder.setMessage("ต้องการจะออกจากการบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    int Farm_IDint = Integer.parseInt(edtFarm_ID.getText().toString());

                    if (!edtFarm_ID.getText().toString().isEmpty()) {
                        DBFile = DATABASE_VISIT_DB + "-" + UserZone + ".sqlite";
                        ExternalStorage_Visit_DB_OpenHelper obj = new ExternalStorage_Visit_DB_OpenHelper(getApplicationContext(), DBFile);
                        db = obj.getReadableDatabase();
                        long a = obj.DeleteDataObject(edtFarm_ID.getText().toString().trim(),
                                db);
                        obj.close();

                        if (a != 0 && a != -1)
                            Toast.makeText(getApplicationContext(),
                                            "ลบข้อมูลการบันทึกสิ่งที่พบออกแล้ว", Toast.LENGTH_SHORT)
                                    .show();
                    }

                } catch (NumberFormatException e) {
                    Log.d("Farm ID", String.valueOf(e));
                }
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


    ////////////////////Camera///////////////////////////////////////

    /**
     * Capturing Camera Image will lauch camera app requrest image capture
     */
//    private void captureImage() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//
//        // start the image capture Intent
//        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
//    }
//    private void captureImage() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE, "My Image");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image captured by Camera");
//
//        ContentResolver contentResolver = getContentResolver();
//        fileUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//
//        // start the image capture Intent
//        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
//    }
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

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {


            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                if (numimage == 1) {

                    Log.e("Check", "numimage == 5 : " + "ใช้ในการโครงการ FSC");

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

                    // การเอียงข้อความ 45 องศา
                    Matrix matrix = new Matrix();
                    matrix.postRotate(45, centerX, centerY);
                    canvas.setMatrix(matrix);

                    float textX = centerX - (textWidth / 2f);
                    float textY = centerY + (textHeight / 2f);

                    canvas.drawText(text, textX, textY, textPaint);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    textBitmap.compress(Bitmap.CompressFormat.JPEG, 1, stream);
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
}
