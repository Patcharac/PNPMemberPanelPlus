package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegisFarmConfirmActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {



    private int checkPicFarm = 0;

    //Spinner sp_peetor;

    private Dialog Popup_dialog;

    @SuppressLint("SdCardPath") String DATABASE_FILE_PATH="/mnt/sdcard/MAPDB";


    // Database Name
    private static final String DATABASE_FARMGEO_DB = "FARMGEO_DB";
    // Table Name
    private static final String TABLE_FARM_GEO_DATA = "farm_geo_data";
    private static final String TABLE_OBJECT_DATA = "object_see";
    //get user
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    //mark point
    private static final String DATABASE_backup_markpoint = "backup_markpoint";

    SQLiteDatabase mydb=null;

    Button btnSaveRegis;
    Button btn_regis_exit;
    Button btnLetCamera;

    EditText txtQT;
    //EditText txtLinkID;
    EditText txtYearOfPlant;
    EditText txtRubberPerRai;

    ImageView imgFarm;

    String Emp_ID ="";
    String UserZone="";
    String Polygon=null;
    String Area = null;

    Spinner mRubberBreed;
    Spinner spnRubberBreed;
    private ArrayList<String> mRub = new ArrayList<String>();

    CheckBox Farm_Status;
    String Farm_StatusString;

    private EditText DirNorth_Use,DirNorth_Owner,DirNorth_Tel;
    private EditText DirEast_Use,DirEast_Owner,DirEast_Tel;
    private EditText DirSouth_Use, DirSouth_Owner, DirSouth_Tel;
    private EditText DirWest_Use,DirWest_Owner,DirWest_Tel;

    CheckBox [] EvaFarm_NO = new CheckBox[14];
    String [] EvaFarmString = new String[14];

    EditText [] EvaFarmSuggest = new EditText[14];
    String [] EvaFarmSuggestString = new String[14];

    //find object
    private Button btnFindObject;
    private EditText ObjLocalNameSrc;

    String X="";
    String Y="";
    int X1=0;
    int Y1=0;

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

    private Bitmap casheBitmap;
    byte[] default_byte = {0};


    //===========image=========//
    private String time_str;
    private byte[] image ;
    private byte[] PicFarm;



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

        CreateFarmRegisDB();

        Bundle b = getIntent().getExtras();
        Polygon = b.getString("POLYGON");
        Area = b.getString("Area");
        X = b.getString("Poly_X");
        Y = b.getString("Poly_Y");

        Log.d("polygon = ",Polygon.toString());
    }

    private void spinner() {
        mRubberBreed = (Spinner) findViewById(R.id.spnRubberBreed);
        mRub.add("RRIM 600");
        mRub.add("RRIT 251");
        ArrayAdapter<String> mRubber = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mRub);
        mRubberBreed.setAdapter(mRubber);
    }


    private void CreateFarmRegisDB() {
        try
        {
            mydb = this.openOrCreateDatabase(DATABASE_FILE_PATH + File.separator
                    + DATABASE_FARMGEO_DB + "-" + UserZone + ".sqlite", Context.MODE_PRIVATE, null);
            mydb.execSQL(" create table if not exists " + TABLE_FARM_GEO_DATA +
                    "  (_id INTEGER PRIMARY KEY AUTOINCREMENT  ," +
                    "  Link_ID TEXT ," +
                    "  qouta_ID TEXT ," +
                    "  Emp_ID TEXT ," +
                    "  Farm_Date TEXT ,"+
                    "  Farm_Area TEXT ,"+
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
                    "   EvaFarmSuggest_NO14 TEXT);" );

            mydb.execSQL(" create table if not exists " + TABLE_OBJECT_DATA +
                    "  (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    "   Link_ID TEXT ,"+
                    "   Obj_LocalName TEXT ,"+
                    "   Obj_Status TEXT);" );
        }
        catch(Exception e)
        {

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

    //check enable button
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.evaFarm_no02:
                if ( isChecked ) {
                    EvaFarmSuggest[1].setText("");
                } else {
                    EvaFarmSuggest[1].setText("พื้นที่ราบ");
                }
                break;


        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSaveRegis:

                for(int i=0; i<14 ;i++){
                    EvaFarmString[i] = getValueCheckbox(EvaFarm_NO [i]);
                    EvaFarmSuggestString[i] = EvaFarmSuggest[i].getText().toString().trim();

                    if (EvaFarmSuggestString[i].isEmpty()){
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
                captureImage();

                break;

            case R.id.findObject:
                if(txtQT.getText().toString().isEmpty() ||
                        ObjLocalNameSrc.getText().toString().isEmpty()){

                    if(txtQT.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "กรุรากรอกเลขโควต้าก่อนบันทึก", Toast.LENGTH_SHORT)
                                .show();
                    }

                    if(ObjLocalNameSrc.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),
                                "กรุรากรอกสิ่งที่พบก่อนการบันทึก", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    beforeSaveObject();
                }
        }


    }

    public void beforeSave(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisFarmConfirmActivity.this);
        builder.setMessage("ต้องการจะบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener(){
            @Override
            public  void onClick(DialogInterface dialogInterface, int i) {
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


        if(Farm_Status.isChecked()){
            Farm_StatusString = "R";
        }

        Link_ID = txtQT.getText().toString().trim()+"-"+Farm_Date;


        SQLiteDatabase db;
        String DBFile= DATABASE_FARMGEO_DB + "-" + UserZone +".sqlite";
        long number=0;
        ExternalStorage_FarmGeo_DB_OpenHelper obj= new ExternalStorage_FarmGeo_DB_OpenHelper(getApplicationContext(),DBFile);
        if(obj.databaseFileExists())
        {
            db=obj.getReadableDatabase();
            number=obj.InsertDataFarmGeoLocation(
                    txtQT.getText().toString().trim(),
                    Link_ID,
                    Emp_ID,
                    Farm_Date,
                    Area,
                    Polygon,
                    "S",
                    PicFarm ,
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

                    EvaFarmString,EvaFarmSuggestString,
                    db);

            if(number<0){
                Toast.makeText(getApplicationContext(),
                        "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT)
                        .show();
            }else{
                String DBFile2=DATABASE_backup_markpoint+".sqlite";
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

            obj.close();
        }else{

            Toast.makeText(getApplicationContext(),
                    "การบันทึกข้อมูลผิดพลาดกรุณาบันทึกข้อมูลอีกครั้ง", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void beforeSaveObject(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisFarmConfirmActivity.this);
        builder.setMessage("ต้องการจะบันทึกสิ่งที่พบหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener(){
            @Override
            public  void onClick(DialogInterface dialogInterface, int i) {

                saveObjectToSqlite();
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

    private void saveObjectToSqlite(){

        Link_ID = txtQT.getText().toString().trim()+"-"+Farm_Date;

        SQLiteDatabase db;
        String DBFile = DATABASE_FARMGEO_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_FarmGeo_DB_OpenHelper obj2 = new ExternalStorage_FarmGeo_DB_OpenHelper(getApplicationContext(),DBFile);
        db = obj2.getReadableDatabase();
        obj2.insertDataObject(Link_ID,
                ObjLocalNameSrc.getText().toString().trim(),
                "EvaFarm",
                db);

        obj2.close();
    }



    private String getValueCheckbox(CheckBox EvaFarm_NO){
        if(EvaFarm_NO.isChecked()){
            return "1";
        }
        return "0";
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
                .setTitle("ออกจากการลงทะเบียนแปลง")
                .setMessage("ออกจากการลงทะเบียนแปลงใช่หรือไม่?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {

                        Link_ID = txtQT.getText().toString().trim()+"-"+Farm_Date;

                        SQLiteDatabase db;
                        String DBFile = DATABASE_FARMGEO_DB + "-" + UserZone + ".sqlite";
                        ExternalStorage_FarmGeo_DB_OpenHelper obj = new ExternalStorage_FarmGeo_DB_OpenHelper(getApplicationContext(),DBFile);
                        db = obj.getReadableDatabase();
                        long a = obj.DeleteDataObject(Link_ID, db);
                        obj.close();

                        RegisFarmConfirmActivity.this.finish();
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
