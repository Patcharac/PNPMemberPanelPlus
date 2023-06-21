package com.panelplus.pnpmember.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Coor_Importance_OpenHelper;
import com.panelplus.pnpmember.module.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CoordinateImportantConfirmActivity extends AppCompatActivity implements View.OnClickListener {


    @SuppressLint("SdCardPath")
    String DATABASE_FILE_PATH = "/mnt/sdcard/MAPDB";


    // Database Name
    private static final String DATABASE_COOR_IMPORTANCE_DB = "COOR_IMPORTANCE_DB";
    // Table Name
    private static final String TABLE_COOR_IMPORTANCE_DATA = "coor_importance_data";
    //get user
    private static final String DATABASE_CENTER_DB = "CENTER_DB";

    SQLiteDatabase mydb = null;

    Button btnSave;
    String latitude, longitude;
    String coordinateX, coordinateY;
    String polygon;

    String dateStamp;
    EditText txtQT;
    EditText edtNote;
    ImageView imgImportance;

    String Emp_ID = "";
    String UserZone = "";
    File Path = null;
    private int numimage = 0;
    //----------------------- Camera  ---------------------

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri; // file url to store image/video

    private Bitmap casheBitmap;
    byte[] default_byte = {0};

    //===========image=========//
    private String time_str;
    private byte[] image;
    private byte[] PicImportance;
    private String DBFile;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinate_important_confirm);

        Bundle b = getIntent().getExtras();
        latitude = b.getString("latitude");
        longitude = b.getString("longitude");
        coordinateX = b.getString("coordinateX");
        coordinateY = b.getString("coordinateY");
        polygon = b.getString("polygon");

        InitWidget();

        //get user
        Emp_ID = getEmpID();
        UserZone = getUserZone();
        dateStamp = getDateTime();

        CreateCoorImPortanceDB(this);
    }

    private void CreateCoorImPortanceDB(Context context) {
        if (context != null) {
            String folderName = "MapDB";
            String folderPath = context.getApplicationContext().getFilesDir() + "/" + folderName;
            File appDbDir = new File(folderPath);
            if (!appDbDir.exists()) {
                appDbDir.mkdirs();
            }
            this.Path = appDbDir;

            String databaseName = DATABASE_COOR_IMPORTANCE_DB + "-" + UserZone + ".sqlite";
            String databasePath = folderPath + "/" + databaseName;

//            coor_importance_data

            try {
                this.mydb = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

                String createTableQuery = "CREATE TABLE IF NOT EXISTS coor_importance_data (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "  qouta_ID TEXT ," +
                        "  Emp_ID TEXT ," +
                        "  Imp_Date TEXT ," +
                        "  Imp_Geo TEXT ," +
                        "  Imp_GeoX TEXT ," +
                        "  Imp_GeoY TEXT ," +
                        "  Imp_Latitude TEXT ," +
                        "  Imp_Longitude TEXT ," +
                        "  Imp_Pic BLOB," +
                        "  Imp_Note TEXT);";

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

    private void InitWidget() {
        txtQT = (EditText) findViewById(R.id.txtQT);
        edtNote = (EditText) findViewById(R.id.edtNote);

        imgImportance = (ImageView) findViewById(R.id.imgHouse);
        imgImportance.setOnClickListener(this);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
    }

    private String getDateTime() {
        int yearint;
        String year;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());


        Calendar cal2 = Calendar.getInstance();
        yearint = cal2.get(Calendar.YEAR) + 543;
        year = String.valueOf(yearint);

        SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm:ss");
        Calendar cal3 = Calendar.getInstance();
        String time = String.valueOf((dateFormat3.format(cal3.getTime())));

        dateStamp = date + "-" + year + " " + time;
        Log.d("year", String.valueOf(dateStamp));

        return dateStamp;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgHouse:
                numimage = 1;
                captureImageCamera();
                break;

            case R.id.btnSave:

                if (txtQT.getText().toString().trim().isEmpty()
                        || txtQT.getText().toString().length() < 6
                        || edtNote.getText().toString().isEmpty()
                        || PicImportance == null) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(CoordinateImportantConfirmActivity.this);
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

                    beforeSave(this);

                }
                break;

        }
    }

    public void beforeSave(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CoordinateImportantConfirmActivity.this);
        builder.setMessage("ต้องการจะบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveCoorHouseToSQlite(context);
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

    private void saveCoorHouseToSQlite(Context context) {

        this.DBFile = DATABASE_COOR_IMPORTANCE_DB + "-" + UserZone + ".sqlite";

        ExternalStorage_Coor_Importance_OpenHelper obj = new ExternalStorage_Coor_Importance_OpenHelper(context, DBFile);
        if (obj.databaseFileExists()) {
            this.mydb = obj.getWritableDatabase(); // เปิดฐานข้อมูลให้เป็นแบบเขียนได้
            long check = -1;
            try {
                this.mydb.beginTransaction();
                check = obj.InsertDataImportanceGeoLocation(
                        txtQT.getText().toString().trim(),
                        Emp_ID,
                        dateStamp,
                        polygon,
                        coordinateX,
                        coordinateY,
                        latitude,
                        longitude,
                        PicImportance,
                        edtNote.getText().toString().trim(),
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

        if (android.os.Build.VERSION.SDK_INT >=26) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("ออกจากการลงเก็บพิกัดจุดสำคัญใช่หรือไม่?")
                .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        CoordinateImportantConfirmActivity.this.finish();
                    }

                })
                .setNegativeButton("ยกเลิก", null)
                .show();
    }

    ////////////////////Camera///////////////////////////////////////

    /**
     * Capturing Camera Image will lauch camera app requrest image capture
     */
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
                this.PicImportance = byteArray;

                int newWidth = 720;
                int newHeight = 1280;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(textBitmap, newWidth, newHeight, true);

                ImageView imageView = findViewById(R.id.imgHouse);
                imageView.setImageBitmap(scaledBitmap);


            }
        }
    }


    /**
     * Display image from a path to ImageView
     */

}
