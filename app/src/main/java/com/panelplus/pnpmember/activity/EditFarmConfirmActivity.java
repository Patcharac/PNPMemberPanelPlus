package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

//import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Backup_Point_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Edit_Farm_DB_OpenHelper;
import com.panelplus.pnpmember.module.Utilities;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.app.DatePickerDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditFarmConfirmActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {
    LinearLayout layoutVisit;

    private String Farm_ID;
    String EditTab_Date;
    ImageView imgFarm;
    String Emp_ID ="";
    String UserZone="";

    String Polygon=null;
    String Area = null;

    String X="";
    String Y="";

    private String yearString;
    EditText edtPlantDate;
    EditText edtRubberPerRai;
    EditText edtNote;
    EditText edtFarm_ID;
    Button btnLetCamera;
    Button btnSaveEdit;
    CheckBox checkVisit;

    boolean status = false;

    Spinner mRubberBreed;
    Spinner spnRubberBreed;
    private ArrayList<String> mRub = new ArrayList<String>();

    private MaterialDatePicker mDatePicker;
//    private DatePickerDialog mDatePickerDialog;
    private Calendar mCalendar;

    //--------------- DB--------------//
    @SuppressLint("SdCardPath") String DATABASE_FILE_PATH="/mnt/sdcard/MAPDB";

    // Database Name
    private static final String DATABASE_EDIT_FARM_DB = "EDITFARM_DB";
    // Table Name
    private static final String TABLE_EDIT_FARM_DATA = "edit_farm_data";
    //get user
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    //mark point
    private static final String DATABASE_backup_edit_markPoint = "backup_edit_markpoint";

    SQLiteDatabase mydb=null;

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
    private byte[] PicFarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_farm_confirm);

        Emp_ID = getEmpID();
        UserZone = getUserZone();
        EditTab_Date = getDateTime();

        InitWidget();
        spinner();
        CreateFarmEditDB();

        Bundle b = getIntent().getExtras();
        Farm_ID = b.getString("Farm_ID");
        edtFarm_ID.setText(Farm_ID);
        Log.d("Farm_ID: ",Farm_ID);

        Polygon = b.getString("POLYGON");
        Area = b.getString("Area");
        X = b.getString("Poly_X");
        Y = b.getString("Poly_Y");

        Log.d("polygon = ",Polygon.toString());
        Log.d("XY = ","( "+X+" "+Y+" )");



        mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date");

// Set date range
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        Calendar minDate = Calendar.getInstance();
        minDate.set(2000, 0, 1); // January 1, 2000
        constraintsBuilder.setStart(minDate.getTimeInMillis());
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2030, 11, 31); // December 31, 2030
        constraintsBuilder.setEnd(maxDate.getTimeInMillis());
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> materialDatePicker = builder.build();

//        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
//            // ทำสิ่งที่คุณต้องการเมื่อผู้ใช้เลือกวันที่
//            // คุณสามารถใช้ตัวแปร selection เพื่อรับวันที่ที่เลือก
//            Date selectedDate = new Date(selection);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//            String formattedDate = dateFormat.format(selectedDate);
//            edtPlantDate.setText(formattedDate);
//        });

        materialDatePicker.show(getSupportFragmentManager(), "datePicker");
        // ให้สั่นหรือไม่?

    }

    private void CreateFarmEditDB() {
        try
        {
            mydb = this.openOrCreateDatabase(DATABASE_FILE_PATH + File.separator
                    + DATABASE_EDIT_FARM_DB + "-" + UserZone + ".sqlite", Context.MODE_PRIVATE, null);
            mydb.execSQL(" create table if not exists " + TABLE_EDIT_FARM_DATA +
                    "  (_id INTEGER PRIMARY KEY AUTOINCREMENT  ," +
                    "  Farm_ID TEXT ," +
                    "  Emp_ID TEXT ," +
                    "  EditTab_Date TEXT ,"+
                    "  EditTab_Area TEXT,"+
                    "  EditTab_Polygon TEXT ,"+
                    "  EditTab_Note TEXT,"+
                    "  EditTab_Status TEXT ,"+
                    "  Farm_Pic BLOB ," +
                    "  Farm_PlantDate TEXT ,"+
                    "  Farm_YearOfPlant TEXT ," +
                    "  Farm_RubberBreed TEXT,"+
                    "  Farm_RubberPerRai TEXT ) ;" );


        } catch(Exception e)

        {

        }
    }

    private void InitWidget() {
        layoutVisit = (LinearLayout) findViewById(R.id.layoutVisit);
        edtFarm_ID = (EditText) findViewById(R.id.edtFarm_ID);
        edtNote = (EditText) findViewById(R.id.edtNote);
        edtPlantDate = (EditText) findViewById(R.id.edtPlantDate);
        edtPlantDate.setOnClickListener(this);

        edtRubberPerRai = (EditText) findViewById(R.id.edtRubberPerRai);

        spnRubberBreed = (Spinner) findViewById(R.id.spnRubberBreed);

        imgFarm = (ImageView) findViewById(R.id.imgFarm);

        btnLetCamera = (Button) findViewById(R.id.btnLetCamera);
        btnLetCamera.setOnClickListener(this);

        btnSaveEdit = (Button) findViewById(R.id.btnSaveEdit);
        btnSaveEdit.setOnClickListener(this);

        checkVisit = (CheckBox) findViewById(R.id.checkVisit);
        checkVisit.setOnCheckedChangeListener(this);

    }

    private void spinner() {
        mRubberBreed = (Spinner) findViewById(R.id.spnRubberBreed);
        mRub.add("RRIM 600");
        mRub.add("RRIT 251");
        ArrayAdapter<String> mRubber = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mRub);
        mRubberBreed.setAdapter(mRubber);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edtPlantDate:
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Select Date");

                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                constraintsBuilder.setStart(getTimeInMillis(2000, 0, 1));
                constraintsBuilder.setEnd(getTimeInMillis(2030, 11, 31));
                builder.setCalendarConstraints(constraintsBuilder.build());

                mDatePicker = builder.build();
                mDatePicker.show(getSupportFragmentManager(), "datePicker");
                break;

            case R.id.btnLetCamera:
                captureImage();

                break;

            case R.id.btnSaveEdit:

                if(!status){
                    if(edtNote.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),
                                "กรุณาระบุสาเหตุในการแก้ไขพิกัดแปลง", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        beforeSave();
                    }

                } else{
                    if(edtPlantDate.getText().toString().isEmpty()
                            || edtRubberPerRai.getText().toString().isEmpty()
                            || PicFarm == null){
                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(EditFarmConfirmActivity.this);
                        builder2.setMessage("กรุณาใส่ข้อมูลให้ครบถ้วน และแนบรูปถ่ายก่อนการบันทึก");
                        builder2.setCancelable(true);
                        builder2.setNegativeButton("ตกลง", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        AlertDialog alertDialog = builder2.create();
                        alertDialog.show();

                    } else {
                        beforeSave();
                    }
                }

                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkVisit:
                if(isChecked){
                    layoutVisit.setVisibility(View.VISIBLE);
                    status = true;
                }else{
                    status = false;

                    layoutVisit.setVisibility(View.GONE);
                    edtPlantDate.setText("");
                    edtRubberPerRai.setText("");

                    imgFarm.setVisibility(View.GONE);
                    PicFarm = null;

                }
                break;


        }
    }

    private long getTimeInMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    public void beforeSave(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditFarmConfirmActivity.this);
        builder.setMessage("ต้องการจะบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener(){
            @Override
            public  void onClick(DialogInterface dialogInterface, int i) {
                saveEditFarmToSQLite();
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

    private void saveEditFarmToSQLite(){

        SQLiteDatabase db;
        String DBFile= DATABASE_EDIT_FARM_DB + "-" + UserZone +".sqlite";
        long number=0;

        ExternalStorage_Edit_Farm_DB_OpenHelper obj = new ExternalStorage_Edit_Farm_DB_OpenHelper(getApplicationContext(),DBFile);
        if(obj.databaseFileExists()){
            db=obj.getReadableDatabase();
            number = obj.InsertDataEditFarmGeoLocation(
                    Farm_ID,
                    Emp_ID,
                    EditTab_Date,
                    Area,
                    Polygon,
                    edtNote.getText().toString().trim(),
                    String.valueOf(status),
                    PicFarm,
                    edtPlantDate.getText().toString().trim(),
                    yearString,
                    spnRubberBreed.getSelectedItem().toString(),
                    edtRubberPerRai.getText().toString().trim(),
                    db);

             if(number<0){
                 Toast.makeText(getApplicationContext(),
                         "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT)
                         .show();
             } else {

                 String DBFile2=DATABASE_backup_edit_markPoint+".sqlite";
                 ExternalStorage_Backup_Point_DB_OpenHelper obj2= new ExternalStorage_Backup_Point_DB_OpenHelper(getApplicationContext(),DBFile2);
                 if(obj2.databaseFileExists())
                 {
                     db=obj2.getReadableDatabase();
                     obj2.DeleteAllData( db);
                     obj2.close();
                 }

                 Toast.makeText(getApplicationContext(),
                         "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT)
                         .show();
                 finish();
             }



        } else{

            Toast.makeText(getApplicationContext(),
                    "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT)
                    .show();
        }
    }



//    private DatePickerDialog.OnDateSetListener onDateSetListener =
//            new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
//
//                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
//                    mCalendar.set(year, month, day);
//                    Date cal = mCalendar.getTime();
//
//                    SimpleDateFormat dateFormatPic = new SimpleDateFormat(
//                            "dd-MM");
//                    String date = dateFormatPic.format(cal);
//
//                    SimpleDateFormat yearFormatPic = new SimpleDateFormat(
//                            "yyyy");
//                    int yearInt = Integer.parseInt(yearFormatPic.format(cal)) +543 ;
//                    yearString = String.valueOf(yearInt);
//
//                    edtPlantDate.setText(date+"-"+yearString);
//                }
//            };



    private void showDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        mDatePicker = builder.build();

        mDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);

                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
                Date cal = calendar.getTime();

                SimpleDateFormat dateFormatPic = new SimpleDateFormat("dd-MM");
                String date = dateFormatPic.format(cal);

                SimpleDateFormat yearFormatPic = new SimpleDateFormat("yyyy");
                int yearInt = Integer.parseInt(yearFormatPic.format(cal)) + 543;
                String yearString = String.valueOf(yearInt);

                edtPlantDate.setText(date + "-" + yearString);
            }
        });

        mDatePicker.show(getSupportFragmentManager(), "datePicker");
    }

    private String getDateTime() {
        int yearInt;
        String year;

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());


        Calendar cal2 = Calendar.getInstance();
        yearInt = cal2.get(Calendar.YEAR)+543;
        year = String.valueOf(yearInt);

        SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                "HH:mm:ss");
        Calendar cal3 = Calendar.getInstance();
        String time = String.valueOf((dateFormat3.format(cal3.getTime())));

        EditTab_Date = date + "-" + year + " "+time;
        //Log.d("year",String.valueOf(EditTab_Date));

        return EditTab_Date;
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
                .setTitle("ออกจากการลงทะเบียนแปลง")
                .setMessage("ออกจากการลงทะเบียนแปลงใช่หรือไม่?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }

                })
                .setNegativeButton("No", null)
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


                PicFarm = previewCapturedImage();
                imgFarm.setVisibility(View.VISIBLE);
                imgFarm.setImageBitmap(casheBitmap);

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
