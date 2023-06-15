package com.panelplus.pnpmember.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnPanListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Field;
import com.esri.core.map.Graphic;
import com.esri.core.runtime.LicenseLevel;
import com.esri.core.runtime.LicenseResult;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Backup_Point_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Backup_Point_DB_OpenHelper.sMembers;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberDrawPolygons;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberCalloutDatas;
import com.panelplus.pnpmember.module.Compass;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.internal.view.SupportMenu;

public class RegisFarmActivity extends AppCompatActivity implements View.OnClickListener {

    ArcGISLocalTiledLayer local;
    //    MapView map = null;
    MapView map;
    LocationManager lm = null;
    LocationListener locationListener;
    GraphicsLayer newcanelayer;

    GraphicsLayer gpslayer;
    Point cachedLocation;
    TextView txtArear;
    GraphicsLayer markpointlayer;

    private Button btnmarkpoit;
    private Button btnclearpoit;
    private Button btnsave;
    private ToggleButton btnautopan;
    private TextView lbl_gps_accuracy;

    private TextView lbl_gpsX;
    private TextView lbl_gpsY;

    ArrayList<Double> Poly_X = new ArrayList<>();
    ArrayList<Double> Poly_Y = new ArrayList<>();

    double cachedLocationX = 0.0;
    double cachedLocationY = 0.0;

    boolean ckhAoutpan = false;
    String PathPolygon = null;

    double Area2D = 0;
    double Absolute = 0;
    double Rai = 0;

    double Map_Scale = 500000;
    GraphicsLayer sketchlayer;
    double Vecter = 0;

    //String getGIS_ID_KEY;
    //String getCane_AC_ID;

    ArrayList<String> cachedFARM_ID_KEY;
    ArrayList<String> cachedPolygon;
    private    File Path = null;
    Envelope rExtent;
    private String ABC_Phase = "";
    private static final String DATABASE_FSCGISDATANAME = "FSCGISDATA";

    //get user
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    String nowEmp_ID ="";

    String UserZone = null;
    String DataSplit = null;
    private String positionid = "";
    private String zoneid = "";
    private String subzoneid = "";
    LoadMapAsync loadMapAsync;

    double cachedClickX = 0;
    double cachedClickY = 0;

    String getFID;
    String getFARM_ID_KEY;
    String getCane_AC_ID;
    String getSTA_CUT;
    Callout callout;

    double Temp_Min_X1 = 0;
    double Temp_Min_X2 = 0;

    MyTouchListener myListener = null;
    MyPanListener myPanListenner = null;
    MyZoomListener myZoomListener = null;
    private Dialog Popup_dialog;
    byte[] default_byte = {0};
    LocationDisplayManager lDisplayManager;
    final static double SEARCH_RADIUS = 5;
    private static final String CLIENT_ID = "BZvWswyDjaWgHxiN";
    private static final String LICENSE_STRING = "LmhvTXFIdXRLVUhMNm5IOWxXZkZrdFhKRC81N3VNaEpBY2EwaGhsN1k4WG1hazJVSUVDb3J3Vzk0VWpWMDRGRGZpKg==";
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    //ImageView imageCompass;
    Compass mCompass;
    private File dbFile;
    private GraphicsLayer mGraphicsLayer;
    //    @SuppressLint("SdCardPath")
//    String DATABASE_FILE_PATH = "/mnt/sdcard/MAPDB";
    SQLiteDatabase mydb = null;
    //Create
    private static final String DATABASE_backup_markpoint = "backup_markpoint";
    private static final String TABLE_MEMBER_markpoint = "markpoint";
    private static final String DATABASE_User_Login = "User_Login_DB";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis_farm);
        InitWidget();
        this.nowEmp_ID = getEmpID(this);
        if (Build.VERSION.SDK_INT > 26) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }

        File appDbDir = new File (Environment.getExternalStorageDirectory(),"BaseMap");
        this.dbFile = new File(appDbDir, "Parcel Map");

        if (!appDbDir.exists()) {
            Log.e("Error", "BaseMap folder not found");
            return;
        }

        if (!this.dbFile.exists()) {
            Log.e("Error", "Parcel Map file not found");
            return;
        }

        this.local = new ArcGISLocalTiledLayer("file://" + String.valueOf(this.dbFile));
//        String filePath = this.dbFile.getAbsolutePath(); // แปลง File เป็นเส้นทางแบบสตริง
//        this.local = new ArcGISLocalTiledLayer(filePath);
        this.map.addLayer(local);

        LicenseResult clientId = ArcGISRuntime.setClientId(CLIENT_ID);
        LicenseResult license = ArcGISRuntime.License.setLicense(LICENSE_STRING);
        LicenseLevel licenseLevel = ArcGISRuntime.License.getLicenseLevel();
        this.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.locationListener = new MyLocationListener();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Location permissions granted
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
            try {
                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                map.setAllowRotationByPinch(true);
                map.enableWrapAround(true);
                mCompass = new Compass(this, null, this.map);
                map.addView(this.mCompass);
            } catch (Exception e) {
                Log.e("Check", "SensorManager: " + String.valueOf(e));
            }
            myListener = new MyTouchListener(this, this.map);
            map.setOnTouchListener(myListener);
            myZoomListener = new MyZoomListener();
            map.setOnZoomListener(myZoomListener);
            myPanListenner = new MyPanListener();
            map.setOnPanListener(myPanListenner);
            callout = this.map.getCallout();
            callout.setStyle(R.xml.calloutstyle);
            sketchlayer = new GraphicsLayer();
            map.addLayer(sketchlayer);
            gpslayer = new GraphicsLayer();
            map.addLayer(gpslayer);
            newcanelayer = new GraphicsLayer();
            map.addLayer(newcanelayer);
            markpointlayer = new GraphicsLayer();
            map.addLayer(markpointlayer);
            Poly_X = new ArrayList<>();
            Poly_Y = new ArrayList<>();
            cachedFARM_ID_KEY = new ArrayList<>();
            cachedPolygon = new ArrayList<>();
            Create_Backup_Point_DB(this);
        } else {
            // Location permissions not granted, request them
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }




    }



    private void InitWidget(){

//        map = (MapView) findViewById(R.id.map);
        map = findViewById(R.id.map);
        lbl_gps_accuracy = (TextView) findViewById(R.id.lbl_gps_accuracy);
        btnmarkpoit = (Button) findViewById(R.id.btncallout_cancle);
        btnclearpoit = (Button) findViewById(R.id.btnclearpoint);
        btnsave = (Button) findViewById(R.id.btnsave);
        btnautopan = (ToggleButton) findViewById(R.id.btnautopan);
        txtArear = (TextView) findViewById(R.id.txtArear);

        lbl_gpsX = (TextView) findViewById(R.id.lbl_gpsX);
        lbl_gpsY = (TextView) findViewById(R.id.lbl_gpsY);

        btnautopan.setOnClickListener(this);
        btnmarkpoit.setOnClickListener(this);
        btnclearpoit.setOnClickListener(this);
        btnsave.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){


            case R.id.btnautopan:

                if(btnautopan.isChecked())
                {
                    ckhAoutpan = true;
                }
                else
                {
                    ckhAoutpan = false;
                }

                break;
            case R.id.btncallout_cancle: /** Start a new Activity MyCards.java */

                //if(lbl_gps_accuracy.getText().toString().trim().equals("...") ||
                //lbl_gps_accuracy.getText().toString().trim().equals("")){
                // OK_Dialog("สัญญาณ GPS ต่ำ ไม่สามารถระบุพิกัดได้", "สัญญาณ GPS");
                //}else{

                //double gps_accuracy = Double.parseDouble(lbl_gps_accuracy.getText().toString().trim());

                //if(gps_accuracy > 10 ){
                //gpslayer.removeAll();
                //OK_Dialog("สัญญาณ GPS ต่ำ ไม่สามารถระบุพิกัดได้", "สัญญาณ GPS");
                //}else{
                final AlertDialog.Builder delete_dialog = new AlertDialog.Builder(this);
                delete_dialog.setTitle("คุณต้องการ  Mark Point ใช่หรือไม่?");
                delete_dialog.setMessage("Mark Point");
                delete_dialog.setNegativeButton("No", null);

                delete_dialog.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        insertPointData(RegisFarmActivity.this);
                    }
                });

                delete_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        // Do nothing
                    }
                });

                delete_dialog.show();
                //}

                // }
                break;

            case R.id.btnclearpoint:

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("ลบการขึ้นรูปแปลง")
                        .setMessage("คุณต้องการเคลียข้อมูลการขึ้นรูปแปลงทั้งหมดหรือไม่?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete Database

                                if (newcanelayer != null) {
                                    newcanelayer.removeAll();
                                }
                                txtArear.setText("0");
                                if (Poly_X.size()>0){

		    					  /*for(int i=0;i<Poly_X.size();i++){

		    						  final Backup_Point_Track myDb = new Backup_Point_Track(TrkPolygon.this);
		    						  myDb.DeleteData(String.valueOf((i+1)));

		    					  }*/


//                                    SQLiteDatabase db;
//                                    String DBFile=DATABASE_backup_markpoint+".sqlite";
//                                    ExternalStorage_Backup_Point_DB_OpenHelper obj= new ExternalStorage_Backup_Point_DB_OpenHelper(DBFile);
//                                    if(obj.databaseFileExists())
//                                    {
//                                        db=obj.getReadableDatabase();
//                                        obj.DeleteData(String.valueOf(Poly_X.size()), db);
//                                        obj.close();
//                                    }

                                    SQLiteDatabase db;
                                    String DBFile = DATABASE_backup_markpoint + ".sqlite";
                                    ExternalStorage_Backup_Point_DB_OpenHelper obj = new ExternalStorage_Backup_Point_DB_OpenHelper(getApplicationContext(), DBFile);
                                    if (obj.databaseFileExists()) {
                                        db = obj.getReadableDatabase();
                                        obj.DeleteData(String.valueOf(Poly_X.size()), db);
                                        obj.close();
                                    }



                                    Log.d("PointX", String.valueOf(cachedLocationX));


                                    if (newcanelayer != null && sketchlayer != null) {
                                        newcanelayer.removeAll();
                                        sketchlayer.removeAll();
                                    }


                                    Poly_X.clear();
                                    Poly_Y.clear();

                                    List<sMembers> MebmerList =null ;

                                    SQLiteDatabase db2;
                                    String DBFile2=DATABASE_backup_markpoint+".sqlite";
                                    ExternalStorage_Backup_Point_DB_OpenHelper obj2= new ExternalStorage_Backup_Point_DB_OpenHelper(getApplicationContext(),DBFile2);
                                    if(obj2.databaseFileExists())
                                    {
                                        db2=obj2.getReadableDatabase();
                                        MebmerList =obj2.SelectAllData(db2);
                                        obj2.close();
                                    }

                                    if(MebmerList == null)
                                    {
                                        //Toast.makeText(MainActivity.this,"Not found Data!",
                                        //Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        for (sMembers mem : MebmerList) {
                                            if (mem != null) {
                                                Log.d("PointX", mem.gPointX());

                                                Poly_X.add(Double.parseDouble(mem.gPointX()));
                                                Poly_Y.add(Double.parseDouble(mem.gPointY()));
                                            }
                                        }
                                        SketchRander();
                                    }
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();



                break;

            case R.id.btnsave:

                final AlertDialog.Builder Save_dialog = new AlertDialog.Builder(this);
                Save_dialog.setTitle("คุณต้องการบันทึกรูปแปลงหรือไม่?");
                Save_dialog.setMessage("บันทึกรูปแปลง");
                Save_dialog.setNegativeButton("ยกเลิก", null);

                Save_dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // Yes Event

                        String X1="";
                        String X2="";

                        if (Poly_X.size()==0 || Poly_X.size() < 3){
                            //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                            OK_Dialog("การขึ้นรูปแปลงไม่ถูกต้อง(กดพิกัดน้อยกว่า3จุด)", "การขึ้นรูปแปลงไม่ถูกต้อง");

                        }else if (Poly_X.size()>0){

                            //UserLoginDB lg = new UserLoginDB(TrkPolygon.this);
                            //lg.UpdateData("1", DataSplit, getGIS_ID_KEY,
                            //getCane_AC_ID, getFID,ABC_Phase,default_byte,
                            //String.valueOf(cachedLocationX) ,String.valueOf(cachedLocationY),"");

                            PathPolygon = "POLYGON ((";


                            for(int i=0;i<Poly_X.size();i++){

                                PathPolygon = PathPolygon + Poly_X.get(i)+" "+ Poly_Y.get(i)+", ";
                                //final Backup_Point_Track myDb = new Backup_Point_Track(TrkPolygon.this);
                                //myDb.DeleteData(String.valueOf((i+1)));

                                SQLiteDatabase db;
                                String DBFile=DATABASE_backup_markpoint+".sqlite";
                                ExternalStorage_Backup_Point_DB_OpenHelper obj= new ExternalStorage_Backup_Point_DB_OpenHelper(getApplicationContext(),DBFile);
                                if(obj.databaseFileExists())
                                {
                                    db=obj.getReadableDatabase();
                                    //obj.DeleteData(String.valueOf((i+1)), db);
                                    obj.close();
                                }

                                X1=String.valueOf(Poly_X.get(i));
                                X2=String.valueOf(Poly_Y.get(i));
                            }

                            PathPolygon = PathPolygon +"))";

                            PathPolygon = PathPolygon.replace(", ))", "))");

                            try {


                                Intent myIntent = new Intent(RegisFarmActivity.this, RegisFarmConfirmActivity.class);
                                Bundle b = new Bundle();
                                b.putString("POLYGON", PathPolygon.toString().trim());
                                b.putString("Area",
                                        txtArear.getText().toString().trim().replace(" ไร่", ""));
                                b.putString("Poly_X", X1.toString().trim());
                                b.putString("Poly_Y", X2.toString().trim());
                                myIntent.putExtras(b);

                                startActivity(myIntent);

                                newcanelayer.removeAll();
                                sketchlayer.removeAll();
                                Poly_X.clear();
                                Poly_Y.clear();


                                RegisFarmActivity.this.finish();

                                if(lm != null){
                                    lm.removeUpdates(locationListener);
                                    lm=null;
                                }

                            } catch (Exception e) {
                                // TODO: handle exception
                                Log.d("RG_ERROR", String.valueOf(e));
                                Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
                Save_dialog.show();



                break;
        }

    }

//    private void insertPointData(Context context){
//        RegisFarmActivity.this.Poly_X.add(Double.valueOf(RegisFarmActivity.this.cachedLocationX));
//        RegisFarmActivity.this.Poly_Y.add(Double.valueOf(RegisFarmActivity.this.cachedLocationY));
//        ExternalStorage_Backup_Point_DB_OpenHelper obj = new ExternalStorage_Backup_Point_DB_OpenHelper(context,"backup_markpoint.sqlite");
//        if (obj.databaseFileExists()) {
//            obj.InsertData(String.valueOf(RegisFarmActivity.this.Poly_X.size()), String.valueOf(RegisFarmActivity.this.cachedLocationX), String.valueOf(RegisFarmActivity.this.cachedLocationY), obj.getReadableDatabase());
//            obj.close();
//        }
//        Log.d("PointX", String.valueOf(RegisFarmActivity.this.cachedLocationX));
//        RegisFarmActivity.this.SketchRander();
//    }

    private void insertPointData(Context context){
        Poly_X.add(Double.valueOf(cachedLocationX));
        Poly_Y.add(Double.valueOf(cachedLocationY));
        ExternalStorage_Backup_Point_DB_OpenHelper obj = new ExternalStorage_Backup_Point_DB_OpenHelper(context,"backup_markpoint.sqlite");
        if (obj.databaseFileExists()) {
            obj.InsertData(String.valueOf(Poly_X.size()), String.valueOf(cachedLocationX), String.valueOf(cachedLocationY), obj.getReadableDatabase());
            obj.close();
            Log.d("PointX", String.valueOf(cachedLocationX));
            Toast.makeText(context, "Database opened successfully.", Toast.LENGTH_SHORT).show();
        } else {
            String DBFile= "backup_markpoint"+".sqlite";
            SQLiteDatabase db;
            db = context.openOrCreateDatabase(DBFile, Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MEMBER_markpoint +
                    " (ID TEXT, PointX TEXT, PointY TEXT)");
            Toast.makeText(context, "New database created successfully!", Toast.LENGTH_SHORT).show();
            db.close();
            obj.InsertData(String.valueOf(Poly_X.size()), String.valueOf(cachedLocationX), String.valueOf(cachedLocationY), obj.getReadableDatabase());
            obj.close();
        }
        SketchRander();
    }



//    private void Create_Backup_Point_DB(Context context) {
//        if (context != null) {
//            String folderName = "MapDB"; // ชื่อโฟลเดอร์
//            File appDbDir = new File(context.getApplicationContext().getFilesDir(), folderName); // สร้างตัวแปร appDbDir โดยใช้เมธอด getFilesDir() เพื่อรับพาธของโฟลเดอร์ที่แอปพลิเคชันสามารถเขียนได้
//            if (!appDbDir.exists()) {
//                appDbDir.mkdirs(); // สร้างโฟลเดอร์หากยังไม่มี
//            }
//            this.Path = appDbDir;
//        } else {
//            // จัดการกรณี context เป็น null
//        }
//
//        try {
//            File dbFile = new File(this.Path, DATABASE_backup_markpoint + ".sqlite");
//            SQLiteDatabase mydb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
//
//            mydb.execSQL("CREATE TABLE IF NOT EXISTS markpoint (ID TEXT, PointX TEXT, PointY TEXT);");
//
//            List<ExternalStorage_Backup_Point_DB_OpenHelper.sMembers> MebmerList = null;
//            ExternalStorage_Backup_Point_DB_OpenHelper obj = new ExternalStorage_Backup_Point_DB_OpenHelper(context, "backup_markpoint.sqlite");
//            if (obj.databaseFileExists()) {
//                MebmerList = obj.SelectAllData(obj.getReadableDatabase());
//                obj.close();
//            }
//            if (MebmerList != null) {
//                for (ExternalStorage_Backup_Point_DB_OpenHelper.sMembers mem : MebmerList) {
//                    Log.d("PointX", mem.gPointX());
//                    this.Poly_X.add(Double.valueOf(Double.parseDouble(mem.gPointX())));
//                    this.Poly_Y.add(Double.valueOf(Double.parseDouble(mem.gPointY())));
//                }
//                SketchRander();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void Create_Backup_Point_DB(Context context) {
        Log.e("Error","Create_Backup_Point_DB 2");
        if (context != null) {
            String folderName = "MapDB"; // ชื่อโฟลเดอร์
            File appDbDir = new File(context.getApplicationContext().getFilesDir(), folderName); // สร้างตัวแปร appDbDir โดยใช้เมธอด getFilesDir() เพื่อรับพาธของโฟลเดอร์ที่แอปพลิเคชันสามารถเขียนได้
            if (!appDbDir.exists()) {
                appDbDir.mkdirs(); // สร้างโฟลเดอร์หากยังไม่มี
            }
            this.Path = appDbDir;
            Log.e("Error","Create_Backup_Point_DB Regis_Farm  : " + Path.getAbsolutePath());
        } else {
            // จัดการกรณี context เป็น null
        }

        try {
            File dbFile = new File(this.Path, DATABASE_backup_markpoint + ".sqlite");
            SQLiteDatabase mydb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            mydb.execSQL("CREATE TABLE IF NOT EXISTS markpoint (ID TEXT, PointX TEXT, PointY TEXT);");

            List<ExternalStorage_Backup_Point_DB_OpenHelper.sMembers> MebmerList = null;
            ExternalStorage_Backup_Point_DB_OpenHelper obj = new ExternalStorage_Backup_Point_DB_OpenHelper(context, "backup_markpoint.sqlite");
            if (obj.databaseFileExists()) {
                MebmerList = obj.SelectAllData(obj.getReadableDatabase());
                obj.close();
            }
            if (MebmerList != null) {
                for (ExternalStorage_Backup_Point_DB_OpenHelper.sMembers mem : MebmerList) {
                    Log.d("PointX", mem.gPointX());
                    this.Poly_X.add(Double.valueOf(Double.parseDouble(mem.gPointX())));
                    this.Poly_Y.add(Double.valueOf(Double.parseDouble(mem.gPointY())));
                }
                SketchRander();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    private void SketchRander(){
//
//        newcanelayer.removeAll();
//
//        if (Poly_X.size()==1){
//
//            Point mapPt = new Point(Float.parseFloat(String.valueOf(Poly_X.get(0)) ),
//                    Float.parseFloat(String.valueOf(Poly_Y.get(0))));
//            newcanelayer.addGraphic(new Graphic(mapPt,new SimpleMarkerSymbol(Color.RED,10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//
//        }else if (Poly_X.size()==2){
//            MultiPath PLL = new Polyline();
//
//            for(int i=0; i<Poly_X.size();i++){
//
//
//                Point curPoint = new Point(Float.parseFloat(String.valueOf(Poly_X.get(i)) ),
//                        Float.parseFloat(String.valueOf(Poly_Y.get(i))));
//
//                if(i==0){
//                    PLL.startPath(curPoint);
//                }else{
//                    PLL.lineTo(curPoint);
//                }
//            }
//
//
//            // Point Color
//            newcanelayer.addGraphic(new Graphic(PLL,new SimpleMarkerSymbol(Color.RED,10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//            // Line Color
//            newcanelayer.addGraphic(new Graphic(PLL,new SimpleLineSymbol(Color.BLACK,1)));
//
//
//        }else if (Poly_X.size() > 2) {
//
//
//            MultiPath PLG = new Polygon();
//
//            for(int i=0; i<Poly_X.size();i++){
//
//
//                Point curPoint = new Point(Float.parseFloat(String.valueOf(Poly_X.get(i)) ),
//                        Float.parseFloat(String.valueOf(Poly_Y.get(i))));
//
//                if(i==0){
//                    PLG.startPath(curPoint);
//                }else{
//                    PLG.lineTo(curPoint);
//                }
//
//            }
//
//            // Point Color
//            newcanelayer.addGraphic(new Graphic(PLG,new SimpleMarkerSymbol(Color.RED,10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//            // Fill Color
//            newcanelayer.addGraphic(new Graphic(PLG,new SimpleFillSymbol(Color.rgb(204, 120, 0))));
//            // Line Color
//            newcanelayer.addGraphic(new Graphic(PLG,new SimpleLineSymbol(Color.BLACK,1)));
//
//
//
//
//            Area2D = PLG.calculateArea2D();
//
//
//            if(Area2D < 0){
//                Absolute = Area2D * (-1) ;
//
//
//                Rai = Absolute / 1600 ;
//
//            }else{
//                Rai = Area2D / 1600 ;
//
//            }
//
//            DecimalFormat decim = new DecimalFormat("0.00");
//            Rai = Double.parseDouble(decim.format(Rai));
//
//            txtArear.setText(String.valueOf(Rai) + " ไร่");
//
//        }
//
//
//
//    }


//    private void SketchRander(){
//        if(newcanelayer != null){
//            newcanelayer.removeAll();
//
////            if (Poly_X.size()==1){
////                Point mapPt = new Point(Float.parseFloat(String.valueOf(Poly_X.get(0)) ),
////                        Float.parseFloat(String.valueOf(Poly_Y.get(0))));
////                newcanelayer.addGraphic(new Graphic(mapPt,new SimpleMarkerSymbol(Color.RED,10, SimpleMarkerSymbol.STYLE.CIRCLE)));
////            }
//            if (Poly_X.size() == 1) {
//                Point mapPt = new Point(Float.parseFloat(String.valueOf(Poly_X.get(0))),
//                        Float.parseFloat(String.valueOf(Poly_Y.get(0))));
//                newcanelayer.addGraphic(new Graphic(mapPt,
//                        new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//            }
//            else if (Poly_X.size()==2){
//                MultiPath PLL = new Polyline();
//                for(int i=0; i<Poly_X.size();i++){
//                    Point curPoint = new Point(Float.parseFloat(String.valueOf(Poly_X.get(i)) ),
//                            Float.parseFloat(String.valueOf(Poly_Y.get(i))));
//                    if(i==0){
//                        PLL.startPath(curPoint);
//                    }else{
//                        PLL.lineTo(curPoint);
//                    }
//                }
//                // Point Color
//                if(newcanelayer != null){
//                    newcanelayer.addGraphic(new Graphic(PLL,new SimpleMarkerSymbol(Color.RED,10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//                }
//                // Line Color
//                if(newcanelayer != null){
//                    newcanelayer.addGraphic(new Graphic(PLL,new SimpleLineSymbol(Color.BLACK,1)));
//                }
//            }else if (Poly_X.size() > 2) {
//                MultiPath PLG = new Polygon();
//                for(int i=0; i<Poly_X.size();i++){
//                    Point curPoint = new Point(Float.parseFloat(String.valueOf(Poly_X.get(i)) ),
//                            Float.parseFloat(String.valueOf(Poly_Y.get(i))));
//                    if(i==0){
//                        PLG.startPath(curPoint);
//                    }else{
//                        PLG.lineTo(curPoint);
//                    }
//                }
//                // Point Color
//                if(newcanelayer != null){
//                    newcanelayer.addGraphic(new Graphic(PLG,new SimpleMarkerSymbol(Color.RED,10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//                }
//                // Fill Color
//                if(newcanelayer != null){
//                    newcanelayer.addGraphic(new Graphic(PLG,new SimpleFillSymbol(Color.rgb(204, 120, 0))));
//                }
//                // Line Color
//                if(newcanelayer != null){
//                    newcanelayer.addGraphic(new Graphic(PLG,new SimpleLineSymbol(Color.BLACK,1)));
//                }
//                Area2D = PLG.calculateArea2D();
//                if(Area2D < 0){
//                    Absolute = Area2D * (-1) ;
//                    Rai = Absolute / 1600 ;
//                }else{
//                    Rai = Area2D / 1600 ;
//                }
//                DecimalFormat decim = new DecimalFormat("0.00");
//                Rai = Double.parseDouble(decim.format(Rai));
//                txtArear.setText(String.valueOf(Rai) + " ไร่");
//            }
//        }
//    }


    /* access modifiers changed from: private */
//    @SuppressLint("RestrictedApi")
//    public void SketchRander() {
//        if (this.newcanelayer != null) {
//            this.newcanelayer.removeAll();
//        }
//        if (this.Poly_X.size() == 1) {
////            this.newcanelayer.addGraphic(new Graphic(new Point((double) Float.parseFloat(String.valueOf(this.Poly_X.get(0))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(0)))), new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//            if (this.newcanelayer != null) {
//                this.newcanelayer.addGraphic(new Graphic(new Point((double) Float.parseFloat(String.valueOf(this.Poly_X.get(0))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(0)))), new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//            }
//        } else if (this.Poly_X.size() == 2) {
//            MultiPath PLL = new Polyline();
//            for (int i = 0; i < this.Poly_X.size(); i++) {
//                Point curPoint = new Point((double) Float.parseFloat(String.valueOf(this.Poly_X.get(i))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(i))));
//                if (i == 0) {
//                    PLL.startPath(curPoint);
//                } else {
//                    PLL.lineTo(curPoint);
//                }
//            }
//            this.newcanelayer.addGraphic(new Graphic(PLL, new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//            this.newcanelayer.addGraphic(new Graphic(PLL, new SimpleLineSymbol(-16777216, 1.0f)));
//        } else if (this.Poly_X.size() > 2) {
//            MultiPath PLG = new Polygon();
//            for (int i2 = 0; i2 < this.Poly_X.size(); i2++) {
//                Point curPoint2 = new Point((double) Float.parseFloat(String.valueOf(this.Poly_X.get(i2))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(i2))));
//                if (i2 == 0) {
//                    PLG.startPath(curPoint2);
//                } else {
//                    PLG.lineTo(curPoint2);
//                }
//            }
//            this.newcanelayer.addGraphic(new Graphic(PLG, new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//            this.newcanelayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(HttpStatus.SC_NO_CONTENT, Field.esriFieldTypeRaster, 0))));
//            this.newcanelayer.addGraphic(new Graphic(PLG, new SimpleLineSymbol(-16777216, 1.0f)));
//            this.Area2D = PLG.calculateArea2D();
//            if (this.Area2D < 0.0d) {
//                this.Absolute = this.Area2D * -1.0d;
//                this.Rai = this.Absolute / 1600.0d;
//            } else {
//                this.Rai = this.Area2D / 1600.0d;
//            }
//            this.Rai = Double.parseDouble(new DecimalFormat("0.00").format(this.Rai));
//            this.txtArear.setText(String.valueOf(this.Rai) + " ไร่");
//        }
//    }
//    @SuppressLint("RestrictedApi")
//    public void SketchRander() {
//        if (this.newcanelayer != null) {
//            if (this.newcanelayer.getNumberOfGraphics() > 0) {
//                this.newcanelayer.removeAll();
//            }
//            if (this.Poly_X.size() == 1) {
//                this.newcanelayer.addGraphic(new Graphic(new Point((double) Float.parseFloat(String.valueOf(this.Poly_X.get(0))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(0)))), new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//            } else if (this.Poly_X.size() == 2) {
//                MultiPath PLL = new Polyline();
//                for (int i = 0; i < this.Poly_X.size(); i++) {
//                    Point curPoint = new Point((double) Float.parseFloat(String.valueOf(this.Poly_X.get(i))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(i))));
//                    if (i == 0) {
//                        PLL.startPath(curPoint);
//                    } else {
//                        PLL.lineTo(curPoint);
//                    }
//                }
//                this.newcanelayer.addGraphic(new Graphic(PLL, new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//                this.newcanelayer.addGraphic(new Graphic(PLL, new SimpleLineSymbol(-16777216, 1.0f)));
//            } else if (this.Poly_X.size() > 2) {
//                MultiPath PLG = new Polygon();
//                for (int i2 = 0; i2 < this.Poly_X.size(); i2++) {
//                    Point curPoint2 = new Point((double) Float.parseFloat(String.valueOf(this.Poly_X.get(i2))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(i2))));
//                    if (i2 == 0) {
//                        PLG.startPath(curPoint2);
//                    } else {
//                        PLG.lineTo(curPoint2);
//                    }
//                }
//                this.newcanelayer.addGraphic(new Graphic(PLG, new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
//                this.newcanelayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(HttpStatus.SC_NO_CONTENT, Field.esriFieldTypeRaster, 0))));
//                this.newcanelayer.addGraphic(new Graphic(PLG, new SimpleLineSymbol(-16777216, 1.0f)));
//                this.Area2D = PLG.calculateArea2D();
//                if (this.Area2D < 0.0d) {
//                    this.Absolute = this.Area2D * -1.0d;
//                    this.Rai = this.Absolute / 1600.0d;
//                } else {
//                    this.Rai = this.Area2D / 1600.0d;
//                }
//                this.Rai = Double.parseDouble(new DecimalFormat("0.00").format(this.Rai));
//                this.txtArear.setText(String.valueOf(this.Rai) + " ไร่");
//            }
//        }
//    }

    @SuppressLint("RestrictedApi")
    public void SketchRander() {
        newcanelayer.removeAll();
        if (Poly_X.size() == 1) {
            newcanelayer.addGraphic(new Graphic(new Point((double) Float.parseFloat(String.valueOf(Poly_X.get(0))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(0)))), new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
        } else if (Poly_X.size() == 2) {
            MultiPath PLL = new Polyline();
            for (int i = 0; i < Poly_X.size(); i++) {
                Point curPoint = new Point((double) Float.parseFloat(String.valueOf(Poly_X.get(i))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(i))));
                if (i == 0) {
                    PLL.startPath(curPoint);
                } else {
                    PLL.lineTo(curPoint);
                }
            }
            newcanelayer.addGraphic(new Graphic(PLL, new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
            newcanelayer.addGraphic(new Graphic(PLL, new SimpleLineSymbol(-16777216, 1.0f)));
        } else if (Poly_X.size() > 2) {
            MultiPath PLG = new Polygon();
            for (int i2 = 0; i2 < Poly_X.size(); i2++) {
                Point curPoint2 = new Point((double) Float.parseFloat(String.valueOf(Poly_X.get(i2))), (double) Float.parseFloat(String.valueOf(this.Poly_Y.get(i2))));
                if (i2 == 0) {
                    PLG.startPath(curPoint2);
                } else {
                    PLG.lineTo(curPoint2);
                }
            }
            newcanelayer.addGraphic(new Graphic(PLG, new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
            newcanelayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(HttpStatus.SC_NO_CONTENT, Field.esriFieldTypeRaster, 0))));
            newcanelayer.addGraphic(new Graphic(PLG, new SimpleLineSymbol(-16777216, 1)));
            Area2D = PLG.calculateArea2D();
            if (Area2D < 0) {
                Absolute = Area2D * -1;
                Rai = Absolute / 1600;
            } else {
                Rai = Area2D / 1600;
            }
            Rai = Double.parseDouble(new DecimalFormat("0.00").format(Rai));
            txtArear.setText(String.valueOf(Rai) + " ไร่");
        }
    }




    public class LoadMapAsync extends AsyncTask<Void, Void, Void> {
        public LoadMapAsync() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            Log.d("Get_Val_strSQL", "LoadMapAsync");
            cachedFARM_ID_KEY.clear();
            cachedPolygon.clear();
            rExtent = new Envelope();
            map.getExtent().queryEnvelope(rExtent);
            double Min_X = rExtent.getXMin();
            double Min_Y = rExtent.getYMin();
            double Max_X = rExtent.getXMax();
            double Max_Y = rExtent.getYMax();
            Log.d("Get_Val_strSQL", "FSCGISDATA.sqlite");
            ExternalStorage_Farm_GIS_DB_OpenHelper obj = new ExternalStorage_Farm_GIS_DB_OpenHelper(getApplicationContext(),"FSCGISDATA.sqlite");
            if (!obj.databaseFileExists()) {
                return null;
            }
            for (ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberDrawPolygons mem : obj.SelectDataDrawPolygon(Max_X, Min_X, Max_Y, Min_Y, obj.getReadableDatabase())) {
                cachedFARM_ID_KEY.add(mem.gFARM_ID());
                cachedPolygon.add(mem.gFARM_GEO());
                CreatePolygon(mem.gFARM_GEO(), mem.gFARM_STATUS(), mem.gEMP_ID());
            }
            obj.close();
            return null;
        }
    }


    public void CreatePolygon(String GeoPointData, String Farm_Status, String Emp_ID) {
        String[] tmp1 = GeoPointData.replace("POLYGON ((", "").replace("))", "").split(", ");
        MultiPath PLG = new Polygon();
        for (int i = 0; i < tmp1.length; i++) {
            String[] tmp2 = tmp1[i].split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
            Point curPoint = new Point((double) Float.parseFloat(tmp2[0].toString()), (double) Float.parseFloat(tmp2[1].toString()));
            if (i == 0) {
                PLG.startPath(curPoint);
            } else {
                PLG.lineTo(curPoint);
            }
        }
        CreatePolygonColor(PLG, Farm_Status, Emp_ID);
    }

    private void CreatePolygonColor(MultiPath PLG, String Farm_Status, String Emp_ID) {
        if (Farm_Status.toString().trim().equals("G")) {
            sketchlayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(107, 142, 35))));
        } else if (Farm_Status.toString().trim().equals("R")) {
            sketchlayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(255, 255, 0))));
        } else if (Farm_Status.toString().trim().equals("RH")) {
            sketchlayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(255, 164, 96))));
        } else if (Farm_Status.toString().trim().equals("H")) {
            sketchlayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(255, 69, 0))));
        } else if (Farm_Status.toString().trim().equals("C")) {
            sketchlayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(164, 164, 164))));
        }
        if (this.nowEmp_ID.toString().equals(Emp_ID.toString())) {
            Log.d("hear", "hear equal");
            sketchlayer.addGraphic(new Graphic(PLG, new SimpleLineSymbol(-16777216, 1.0f)));
            return;
        }
        Log.d("not equal", String.valueOf(Emp_ID));
        sketchlayer.addGraphic(new Graphic(PLG, new SimpleLineSymbol(-1, 1.0f)));
    }

    private String getEmpID(Context context) {
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context,"CENTER_DB.sqlite");
        if (!obj.databaseFileExists()) {
            return "NULL";
        }
        nowEmp_ID = obj.GetEmpID("1", obj.getReadableDatabase());
        obj.close();
        return nowEmp_ID;
    }

    class MyLocationListener implements LocationListener {
        MyLocationListener() {
        }

        public void onLocationChanged(Location location) {
            lDisplayManager = map.getLocationDisplayManager();
            lDisplayManager.start();
            if (location != null) {
                location.setLatitude(location.getLatitude());
                location.setLongitude(location.getLongitude());
                lbl_gps_accuracy.setText(String.valueOf(location.getAccuracy()));
                Point mapPoint = new Point(location.getLongitude(), location.getLatitude());
                if (4236 != map.getSpatialReference().getID()) {
                    mapPoint = (Point) GeometryEngine.project((Geometry) mapPoint, SpatialReference.create(4326), map.getSpatialReference());
                }
                if (mapPoint == null || mapPoint.isEmpty()) {
                    Toast.makeText(RegisFarmActivity.this, "Cannot determine current location", Toast.LENGTH_SHORT).show();
                    return;
                }
                cachedLocationX = mapPoint.getX();
                cachedLocationY = mapPoint.getY();
                lbl_gpsX.setText(String.valueOf(Integer.valueOf((int) mapPoint.getX())));
                lbl_gpsY.setText(String.valueOf(Integer.valueOf((int) mapPoint.getY())));
                Log.d("Get_Val_GPSX", String.valueOf(cachedLocationX));
                Log.d("Get_Val_GPSY", String.valueOf(cachedLocationY));
                cachedLocation = mapPoint;
                if (btnautopan.isChecked()) {
                    lDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                    map.zoomToScale(mapPoint, 25000);
                    double zoomWidth = Unit.convertUnits(SEARCH_RADIUS, Unit.create(LinearUnit.Code.MILE_US), RegisFarmActivity.this.map.getSpatialReference().getUnit());
                    map.setExtent(new Envelope(mapPoint, zoomWidth, zoomWidth));
                    if (map.getScale() > 30000) {
                        sketchlayer.removeAll();
                    } else {
                        loadMapAsync = new LoadMapAsync();
                        loadMapAsync.execute(new Void[0]);
                        btnautopan.setChecked(false);
                    }
                } else {
                    ckhAoutpan = false;
                }
                if (location.getAccuracy() > 8) {
                    gpslayer.removeAll();
                } else {
                    gpslayer.removeAll();
                }
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    class MyPanListener implements OnPanListener {
        private static final long serialVersionUID = 1;

        MyPanListener() {
        }

        public void postPointerMove(float arg0, float arg1, float arg2, float arg3) {
            if (Map_Scale > 30000) {
                sketchlayer.removeAll();
            }
        }

        public void postPointerUp(float arg0, float arg1, float arg2, float arg3) {
            if (Map_Scale > 30000) {
                sketchlayer.removeAll();
            }
        }

        public void prePointerMove(float arg0, float arg1, float arg2, float arg3) {
            if (Map_Scale > 30000) {
                sketchlayer.removeAll();
            }
        }

        public void prePointerUp(float arg0, float arg1, float arg2, float arg3) {
            rExtent = new Envelope();
            map.getExtent().queryEnvelope(rExtent);
            double Map_Scale = map.getScale();
            double Min_X = rExtent.getXMin();
            if (Temp_Min_X1 == 0) {
                Temp_Min_X1 = Min_X;
            } else if (Min_X > Temp_Min_X1) {
                Temp_Min_X2 = Min_X - Temp_Min_X1;
                Temp_Min_X1 = Min_X;
            } else {
                Temp_Min_X2 = Temp_Min_X1 - Min_X;
                Temp_Min_X1 = Min_X;
            }
            if (Temp_Min_X2 > 70 && Temp_Min_X1 != 0) {
                if (Map_Scale < 30000) {
                    sketchlayer.removeAll();
                    loadMapAsync = new LoadMapAsync();
                    loadMapAsync.execute(new Void[0]);
                    return;
                }
                sketchlayer.removeAll();
            }
        }
    }

    class MyZoomListener implements OnZoomListener {
        private static final long serialVersionUID = 1;

        MyZoomListener() {
        }

        public void postAction(float arg0, float arg1, double arg2) {
            Map_Scale = map.getScale();
            Log.d("MapScale", String.valueOf(Map_Scale));
            if (Map_Scale < 30000) {
                sketchlayer.removeAll();
                loadMapAsync = new LoadMapAsync();
                loadMapAsync.execute(new Void[0]);
                return;
            }
            sketchlayer.removeAll();
        }

        public void preAction(float arg0, float arg1, double arg2) {
            if (Map_Scale > 30000) {
                sketchlayer.removeAll();
            }
        }
    }

    class MyTouchListener extends MapOnTouchListener {
        MultiPath poly;
        Point startPoint = null;
        String type = "POINT";

        public MyTouchListener(Context context, MapView view) {
            super(context, view);
        }

        public void setType(String geometryType) {
            this.type = geometryType;
        }

        public String getType() {
            return this.type;
        }

        @SuppressLint("RestrictedApi")
        public boolean onSingleTap(MotionEvent e) {
            if (map.getScale() < 25000) {
                Point PointClick = map.toMapPoint(e.getX(), e.getY());
                cachedClickX = PointClick.getX();
                cachedClickY = PointClick.getY();
                double X1 = cachedLocationX;
                double Y1 = cachedLocationY;
                Vecter = Math.sqrt(Math.pow(cachedClickX - X1, 2)) + Math.sqrt(Math.pow(cachedClickY - Y1, 2));
                if (Vecter < 0) {
                    Vecter *= -1;
                }
                if (type.length() > 1 && type.equalsIgnoreCase("POINT")) {
                    markpointlayer.removeAll();
                    Point mapPt = map.toMapPoint(e.getX(), e.getY());
                    markpointlayer.addGraphic(new Graphic(mapPt, new SimpleMarkerSymbol(SupportMenu.CATEGORY_MASK, 8, SimpleMarkerSymbol.STYLE.CIRCLE)));
                    double PX = mapPt.getX();
                    double PY = mapPt.getY();
                    int x = 0;
                    while (x < cachedFARM_ID_KEY.size()) {
                        try {
                            int intersections = 0;
                            ArrayList<Float> verticesX = new ArrayList<>();
                            ArrayList<Float> verticesY = new ArrayList<>();
                            String[] tmp1 = cachedPolygon.get(x).toString().replace("POLYGON ((", "").replace("))", "").split(", ");
                            for (int y = 0; y < tmp1.length; y++) {
                                String[] tmp2 = tmp1[y].split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
                                verticesX.add(Float.valueOf(Float.parseFloat(tmp2[0].toString().trim())));
                                verticesY.add(Float.valueOf(Float.parseFloat(tmp2[1].toString().trim())));
                            }
                            int vertices_count = verticesX.size();
                            for (int i = 1; i < vertices_count; i++) {
                                float vertex1X = verticesX.get(i - 1).floatValue();
                                float vertex1Y = verticesY.get(i - 1).floatValue();
                                float vertex2X = verticesX.get(i).floatValue();
                                float vertex2Y = verticesY.get(i).floatValue();
                                if (vertex1Y != vertex2Y || ((double) vertex1Y) != PY || PX <= ((double) getMinXY(vertex1X, vertex2X)) || PX < ((double) getMaxXY(vertex1X, vertex2X))) {
                                }
                                if (PY > ((double) getMinXY(vertex1Y, vertex2Y)) && PY <= ((double) getMaxXY(vertex1Y, vertex2Y)) && PX <= ((double) getMaxXY(vertex1X, vertex2X)) && vertex1Y != vertex2Y) {
                                    double xinters = (((PY - ((double) vertex1Y)) * ((double) (vertex2X - vertex1X))) / ((double) (vertex2Y - vertex1Y))) + ((double) vertex1X);
                                    if (xinters == PX) {
                                    }
                                    if (vertex1X == vertex2X || PX <= xinters) {
                                        intersections++;
                                    }
                                }
                            }
                            if (intersections % 2 != 0) {
                                ViewGroup calloutContent = (ViewGroup) getLayoutInflater().inflate(R.layout.location_callout, (ViewGroup) null);
                                String FARM_ID_KEY = cachedFARM_ID_KEY.get(x);
                                TextView lblqoata_id = (TextView) calloutContent.findViewById(R.id.lblqoata_id);
                                TextView lblfid = (TextView) calloutContent.findViewById(R.id.lblfid);
                                TextView lblstatus = (TextView) calloutContent.findViewById(R.id.lblstatus);
                                TextView lblcyearold = (TextView) calloutContent.findViewById(R.id.lblcyearold);
                                TextView lblname = (TextView) calloutContent.findViewById(R.id.lblcancle_project);
                                TextView lbltel = (TextView) calloutContent.findViewById(R.id.lblTel);
                                TextView lblarea = (TextView) calloutContent.findViewById(R.id.lblarea);
                                TextView lblvality = (TextView) calloutContent.findViewById(R.id.lblvality);
                                TextView lblssone = (TextView) calloutContent.findViewById(R.id.lblssone);
                                TextView lblcyear = (TextView) calloutContent.findViewById(R.id.lblcyear);
                                Button btncallout_cancle = (Button) calloutContent.findViewById(R.id.btncallout_cancle);
                                ((Button) calloutContent.findViewById(R.id.btncallout_edit)).setVisibility(View.INVISIBLE);
                                ExternalStorage_Farm_GIS_DB_OpenHelper externalStorage_Farm_GIS_DB_OpenHelper = new ExternalStorage_Farm_GIS_DB_OpenHelper(getApplicationContext(),"FSCGISDATA.sqlite");
                                if (externalStorage_Farm_GIS_DB_OpenHelper.databaseFileExists()) {
                                    for (ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberCalloutDatas mem : externalStorage_Farm_GIS_DB_OpenHelper.getCaneCallout(FARM_ID_KEY, externalStorage_Farm_GIS_DB_OpenHelper.getReadableDatabase())) {
                                        lblqoata_id.setText(mem.gQOUTA_ID().toString());
                                        lblfid.setText(mem.gFARM_ID().toString());
                                        getFID = mem.gFARM_ID().toString();
                                        getFARM_ID_KEY = FARM_ID_KEY;
                                        lblstatus.setText(mem.gFARM_STATUS().toString());
                                        lblcyear.setText(mem.gFARM_YEAROFPLANT().toString());
                                        lblname.setText(mem.gMEM_GENDER() + mem.gMEM_NAME() + "  " + mem.gMEM_LASTNAME());
                                        lbltel.setText(mem.gMEM_TEL().toString());
                                        lblarea.setText(mem.gFARM_AREA().toString());
                                        lblvality.setText(mem.gFARM_RUBBERBREED().toString());
                                        lblssone.setText(mem.gEMP_NAME().toString());
                                        lblcyearold.setText(String.valueOf((Calendar.getInstance().get(1) + 543) - Integer.parseInt(mem.gFARM_YEAROFPLANT())) + " ปี");
                                    }
                                    externalStorage_Farm_GIS_DB_OpenHelper.close();
                                }
                                btncallout_cancle.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        callout.hide();
                                    }
                                });
                                callout.setMaxHeight(1000);
                                callout.setMaxWidth(5000);
                                callout.setCoordinates(mapPt);
                                callout.setContent(calloutContent);
                                callout.refresh();
                                callout.show();
                            }
                            x++;
                        } catch (Exception e2) {
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }
    public String Case_Cane(String CC){

        String Resault;

        if(CC.equals("F")){
            Resault="FSC";
        }else if(CC.equals("I")){
            Resault="IWAY";
        }else if(CC.equals("S")){
            Resault="ทั่วไป";
        }else{
            Resault="";
        }

        return Resault;

    }

    public float getMaxXY(float XY1, float XY2 ){

        float fPoint=0;

        if (XY1 > XY2){
            fPoint=XY1;
        }else{
            fPoint=XY2;
        }
        return fPoint;

    }

    public float getMinXY(float XY1, float XY2 ){

        float fPoint=0;

        if (XY1 < XY2){
            fPoint=XY1;
        }else{
            fPoint=XY2;
        }
        return fPoint;

    }

    private void OK_Dialog(String Message,String Title) {
        Popup_dialog = new Dialog(this,R.style.FullHeightDialog); //this is a reference to the style above
        Popup_dialog.setContentView(R.layout.ok_dialog); //I saved the xml file above as yesnomessage.xml
        Popup_dialog.setCancelable(true);


        //to set the message
        TextView message =(TextView) Popup_dialog.findViewById(R.id.tvmessagedialogtext);
        message.setText(Message);
        TextView title = (TextView) Popup_dialog.findViewById(R.id.tvmessagedialogtitle);
        title.setText(Title);


        //add some action to the buttons

        Button ok = (Button) Popup_dialog.findViewById(R.id.bmessageDialogOK);
        ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Popup_dialog.dismiss();

            }
        });

        Popup_dialog.show();
    }


    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        new AlertDialog.Builder(this)

                .setMessage("ออกจากการสำรวจพื้นที่แปลงยางพาราใหม่หรือไม่?")
                .setPositiveButton("ใช่", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        ClearCachedPolygon();
                        try {
                            if(lm != null){
                                lm.removeUpdates(locationListener);
                                lm=null;
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(getApplicationContext(), "onBackPressed Error : "+String.valueOf(e) , Toast.LENGTH_LONG).show();
                        }



                        try {
                            RegisFarmActivity.this.finish();
                        } catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(getApplicationContext(), "Activity finish Error : "+String.valueOf(e) , Toast.LENGTH_LONG).show();
                        }

                    }

                })
                .setNegativeButton("ยกเลิก", null)
                .show();
    }

    private void ClearCachedPolygon(){
        if (cachedFARM_ID_KEY != null) {
            cachedFARM_ID_KEY.clear();
        }
        if (cachedPolygon != null) {
            cachedPolygon.clear();
        }
        if (sketchlayer != null) {
            sketchlayer.clearSelection();
            sketchlayer.removeAll();
        }
        if (markpointlayer != null) {
            markpointlayer.clearSelection();
            markpointlayer.removeAll();
        }
        if (gpslayer != null) {
            gpslayer.clearSelection();
            gpslayer.removeAll();
        }
        if (map != null) {
            map.destroyDrawingCache();
        }
        if (newcanelayer != null) {
            newcanelayer.removeAll();
        }
        if (Poly_X != null) {
            Poly_X.clear();
        }
        if (Poly_Y != null) {
            Poly_Y.clear();
        }
    }

}
