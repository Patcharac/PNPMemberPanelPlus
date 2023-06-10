package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CoordinateImportantConfirmActivity extends AppCompatActivity implements View.OnClickListener{



    @SuppressLint("SdCardPath") String DATABASE_FILE_PATH="/mnt/sdcard/MAPDB";


    // Database Name
    private static final String DATABASE_COOR_IMPORTANCE_DB = "COOR_IMPORTANCE_DB";
    // Table Name
    private static final String TABLE_COOR_IMPORTANCE_DATA = "coor_importance_data";
    //get user
    private static final String DATABASE_CENTER_DB = "CENTER_DB";

    SQLiteDatabase mydb=null;

    Button btnSave;
    String latitude, longitude;
    String coordinateX, coordinateY;
    String polygon;

    String dateStamp;
    EditText txtQT;
    EditText edtNote;
    ImageView imgImportance;

    String Emp_ID ="";
    String UserZone="";

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
    private byte[] image ;
    private byte[] PicImportance;

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

        CreateCoorImPortanceDB();
    }

    private void CreateCoorImPortanceDB(){
        try{
            mydb = this.openOrCreateDatabase(DATABASE_FILE_PATH + File.separator
                    + DATABASE_COOR_IMPORTANCE_DB + "-" + UserZone + ".sqlite", Context.MODE_PRIVATE, null);
            mydb.execSQL(" create table if not exists " + TABLE_COOR_IMPORTANCE_DATA +
                    "  (_id INTEGER PRIMARY KEY AUTOINCREMENT  ," +
                    "  qouta_ID TEXT ," +
                    "  Emp_ID TEXT ," +
                    "  Imp_Date TEXT ,"+
                    "  Imp_Geo TEXT ,"+
                    "  Imp_GeoX TEXT ,"+
                    "  Imp_GeoY TEXT ,"+
                    "  Imp_Latitude TEXT ,"+
                    "  Imp_Longitude TEXT ," +
                    "  Imp_Pic BLOB," +
                    "  Imp_Note TEXT );" );


        } catch(Exception e) {
            Log.d("errorCreateDB", String.valueOf(e));
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

        dateStamp = date + "-" + year + " "+time;
        Log.d("year",String.valueOf(dateStamp));

        return  dateStamp;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgHouse:
                captureImage();
                break;

            case R.id.btnSave:

                if (txtQT.getText().toString().trim().isEmpty()
                        ||txtQT.getText().toString().length()<6
                        ||edtNote.getText().toString().isEmpty()
                        ||PicImportance==null){

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

                    beforeSave();

                }
                break;

        }
    }

    public void beforeSave(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(CoordinateImportantConfirmActivity.this);
        builder.setMessage("ต้องการจะบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener(){
            @Override
            public  void onClick(DialogInterface dialogInterface, int i) {
                saveCoorHouseToSQlite();
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

    private void saveCoorHouseToSQlite(){
        SQLiteDatabase db;

        String DBFile = DATABASE_COOR_IMPORTANCE_DB + "-" + UserZone +".sqlite";
        long number = 0;

        ExternalStorage_Coor_Importance_OpenHelper obj = new ExternalStorage_Coor_Importance_OpenHelper(getApplicationContext(),DBFile);
        if (obj.databaseFileExists()) {
            db=obj.getReadableDatabase();
            number = obj.InsertDataImportanceGeoLocation(
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
                    db);

            if(number<0){
                Toast.makeText(getApplicationContext(),
                        "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
            obj.close();
        }else{

            Toast.makeText(getApplicationContext(),
                    "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT)
                    .show();
        }
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




    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();

        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("ออกจากการลงเก็บพิกัดจุดสำคัญใช่หรือไม่?")
                .setPositiveButton("ใช่", new DialogInterface.OnClickListener()
                {
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
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view


                PicImportance = previewCapturedImage();
                // imgImportance.setVisibility(View.VISIBLE);
                imgImportance.setImageBitmap(casheBitmap);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                //Toast.makeText(getApplicationContext(),
                //"User cancelled image capture", Toast.LENGTH_SHORT)
                //.show();
                //captureImage();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                                "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    /**
     * Display image from a path to ImageView
     */
    private byte[] previewCapturedImage() {
        try {
            // hide video preview

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 1;

            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            Bitmap bm1 = null;



            try {
                ExifInterface exif = new ExifInterface(fileUri.getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

                } else if (orientation == 1) {
                    matrix.postRotate(0);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

                }

                if(orientation == 1 || orientation == 3){
                    bm1 = getResizedBitmap(bitmap,341,455);
                } else {
                    bm1 = getResizedBitmap(bitmap,455,341);

                }
            }
            catch (Exception e) {

            }

            Bitmap newBitmap = null;

            Bitmap.Config config = bm1.getConfig();
            if(config == null){
                config = Bitmap.Config.ARGB_8888;
            }

            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(bm1, 0, 0, null);




            //String captionString ="X=1011111_Y=17150000_2014-05-22";
            String captionString =time_str;
            if(captionString != null){

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.RED);
                paintText.setTextSize(10);
                paintText.setStyle(Paint.Style.FILL);
                paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

                newCanvas.drawText(captionString,
                        0, rectText.height(), paintText);



            }

            casheBitmap=newBitmap;
            //imgPreview.setImageBitmap(newBitmap);

            File fdelete = new File(fileUri.getPath());
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    //System.out.println("file Deleted :" + fileUri.getPath());
                } else {
                    //System.out.println("file not Deleted :" + fileUri.getPath());
                }
            }



            image = Utilities.getBytes(casheBitmap);



	          /*Intent myIntentCheckCaneRegis_ABC = new Intent(Regis_New_Cane.this, ABC_New_Cane.class);
			  Bundle b = new Bundle();
			  b.putString("ABC_Phase", ABC_Phase);
			  b.putString("PER_ID", UserZone);
			  b.putString("RG_KEY",regis_active_key);
			  myIntentCheckCaneRegis_ABC.putExtras(b);
	          startActivity(myIntentCheckCaneRegis_ABC);*/




        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return image;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
