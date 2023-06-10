package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
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
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_EvaCon_DB_OpenHelper;
import com.panelplus.pnpmember.module.Utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class EvaContractorActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    Button btnLicenseSaw;
    Button btnLicenseCar;
    Button btnSave;

    EditText Con_Name,Con_ID,Farm_ID;

    CheckBox [] EvaCon_NO = new CheckBox[18];
    String [] EvaConString = new String[18];

    private int checkLicenseSaw = 0;
    private int checkLicenseCar = 0;
    private int checkImageSaw = 1;
    private int  checkImageCar =1;
    private String Emp_ID ="";
    private String UserZone="";

    private String dateNow;

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
    private byte[] PicLicenseSaw;
    private byte[] PiclicenseCar;
    private int numimage=0;
    private int numCountIMG=0;

    //===============DB================//
    SQLiteDatabase mydb=null;

    private String DBFile;
    SQLiteDatabase db;

    @SuppressLint("SdCardPath") String DATABASE_FILE_PATH="/sdcard/MapDB/";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";

    private static final String DATABASE_EVACON_DB = "EVACON_DB";
    private static final String TABLE_EVA_CONTRACTOR_DATA = "eva_contractor_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eva_contractor);

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
        //--------create DB-SQlite---------
        /*

        mydb = this.openOrCreateDatabase(DATABASE_FILE_PATH + File.separator
                + DATABASE_EVACON_DB + "-" + UserZone + ".sqlite", Context.MODE_PRIVATE, null);

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

                */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:

                for (int i=0; i<18 ;i++){
                    EvaConString[i] = getValueCheckbox(EvaCon_NO[i]);
                }

                if (checkLicenseSaw == 1 && PicLicenseSaw == null){
                    checkImageSaw = 0;
                }else{
                    checkImageSaw = 1;
                }

                if (checkLicenseCar == 1 && PiclicenseCar == null){
                    checkImageCar = 0;
                }else {
                    checkImageCar = 1;
                }

                if (Con_Name.getText().toString().isEmpty()||
                        Con_ID.getText().toString().isEmpty() ||
                        Farm_ID.getText().toString().isEmpty()||
                        checkImageSaw == 0 || checkImageCar == 0 ){

                    Toast.makeText(getApplicationContext(),
                            "กรุณากรอกข้อมูลหรือแนบรูปถ่ายให้ครบถ้วนก่อนการบันทึก", Toast.LENGTH_SHORT)
                            .show();
                }else {
                    beforeSave();
                }

                break;

            case R.id.licenseSaw:
                numimage = 1;
                captureImage();
                break;

            case R.id.licenseCar:
                numimage = 2;
                captureImage();
                break;
        }
    }

    private void InitWitget() {
        btnLicenseSaw = (Button) findViewById(R.id.licenseSaw);
        btnLicenseCar = (Button) findViewById(R.id.licenseCar);
        btnSave = (Button)findViewById(R.id.save);

        btnLicenseSaw.setOnClickListener(this);
        btnLicenseCar.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        EvaCon_NO[0] = (CheckBox) findViewById(R.id.EvaCon_NO1);
        EvaCon_NO[1] = (CheckBox) findViewById(R.id.EvaCon_NO2);
        EvaCon_NO[2] = (CheckBox) findViewById(R.id.EvaCon_NO3);
        EvaCon_NO[3] = (CheckBox) findViewById(R.id.EvaCon_NO4);
        EvaCon_NO[4] = (CheckBox) findViewById(R.id.EvaCon_NO5);
        EvaCon_NO[5] = (CheckBox) findViewById(R.id.EvaCon_NO6);
        EvaCon_NO[6] = (CheckBox) findViewById(R.id.EvaCon_NO7);
        EvaCon_NO[7] = (CheckBox) findViewById(R.id.EvaCon_NO8);
        EvaCon_NO[8] = (CheckBox) findViewById(R.id.EvaCon_NO9);
        EvaCon_NO[9] = (CheckBox) findViewById(R.id.EvaCon_NO10);
        EvaCon_NO[10]= (CheckBox) findViewById(R.id.EvaCon_NO11);
        EvaCon_NO[11]= (CheckBox) findViewById(R.id.EvaCon_NO12);
        EvaCon_NO[12]= (CheckBox) findViewById(R.id.EvaCon_NO13);
        EvaCon_NO[13]= (CheckBox) findViewById(R.id.EvaCon_NO14);
        EvaCon_NO[14]= (CheckBox) findViewById(R.id.EvaCon_NO15);
        EvaCon_NO[15]= (CheckBox) findViewById(R.id.EvaCon_NO16);
        EvaCon_NO[16]= (CheckBox) findViewById(R.id.EvaCon_NO17);
        EvaCon_NO[17]= (CheckBox) findViewById(R.id.EvaCon_NO18);

        EvaCon_NO[0].setOnCheckedChangeListener(this);
        EvaCon_NO[1].setOnCheckedChangeListener(this);

        Con_ID = (EditText) findViewById(R.id.Con_ID);
        Con_Name = (EditText) findViewById(R.id.Con_Name);
        Farm_ID = (EditText) findViewById(R.id.Farm_ID);

    }


    private String getValueCheckbox(CheckBox EvaCon_NO) {
        if(EvaCon_NO.isChecked()){
            return "1";
        }
        return "0";
    }

    //check enable button
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.EvaCon_NO1:
                if ( isChecked )
                {
                    btnLicenseSaw.setEnabled(true);
                    checkLicenseSaw = 1;

                }else{
                    btnLicenseSaw.setEnabled(false);
                    PicLicenseSaw = null;
                    checkLicenseSaw = 0;
                }
                break;

            case R.id.EvaCon_NO2:
                if ( isChecked )
                {
                    btnLicenseCar.setEnabled(true);
                    checkLicenseCar = 1;

                }else{
                    btnLicenseCar.setEnabled(false);
                    PiclicenseCar = null;
                    checkLicenseCar = 0;
                }
                break;
        }
    }

    public void beforeSave(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaContractorActivity.this);
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
        long check = obj.insertDataEvaContractor(dateNow,
                Con_Name.getText().toString().trim(),
                Con_ID.getText().toString().trim(),
                Farm_ID.getText().toString().trim(),
                Emp_ID,
                EvaConString,
                PicLicenseSaw,
                PiclicenseCar,
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaContractorActivity.this);
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
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view

                if (numimage == 1){
                    PicLicenseSaw = previewCapturedImage();

                }
                else if (numimage == 2){
                    PiclicenseCar = previewCapturedImage();
                }

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
