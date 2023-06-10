package com.panelplus.pnpmember.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
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
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.runtime.LicenseLevel;
import com.esri.core.runtime.LicenseResult;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberCalloutDatas;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberDrawPolygons;
import com.panelplus.pnpmember.database.ExternalStorage_House_GIS_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_House_GIS_DB_OpenHelper.sMemberDrawPolygonsHouse;
import com.panelplus.pnpmember.module.Compass;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ViewFarmActivity extends AppCompatActivity implements View.OnClickListener {
    //variable from xml

    Button btn_map_search;
    Button btnSearchHouse;

    ///////////////////////

    GraphicsLayer GPSlayer;

    LocationDisplayManager lDisplayManager;
    private static final String CLIENT_ID = "BZvWswyDjaWgHxiN";
    private static final String LICENSE_STRING = "LmhvTXFIdXRLVUhMNm5IOWxXZkZrdFhKRC81N3VNaEpBY2EwaGhsN1k4WG1hazJVSUVDb3J3Vzk0VWpWMDRGRGZpKg==";
    private float currentDegree = 0f;

    private File dbFile;

    MapView map = null;
    ArcGISLocalTiledLayer local;
    TextView lblZoom;
    ToggleButton btnautopan;

    public LocationManager lm = null;
    public LocationListener locationListener;

    double cachedGPSX=0;
    double cachedGPSY=0;

    private TextView lbl_gpsX;
    private TextView lbl_gpsY;

    double Map_Scale = 500000;

    Point cachedLocation;

    @SuppressWarnings("unused")
    private SensorManager mSensorManager;
    //ImageView imageCompass;
    Compass mCompass;
    GraphicsLayer sketchlayer;
    GraphicsLayer sketchHouseLayer;
    GraphicsLayer Symbollayer;
    GraphicsLayer sketchwaterlayer;
    GraphicsLayer markpointlayer;
    GraphicsLayer searchlayer;

    MyTouchListener myListener = null;
    MyPanListener myPanListenner = null;
    MyZoomListener myZoomListener = null;

    //load map
    LoadMapAsync loadMapAsync;
    ArrayList<String> cachedFARM_ID_KEY;
    ArrayList<String> cachedQOUTA_ID_KEY;
    ArrayList<String> cachedPolygon;
    ArrayList<String> cachedPolygonHouse;
    ArrayList<String> cachedACC_DISTANCE;
    private static final String DATABASE_FSCGISDATANAME = "FSCGISDATA";

    //get user
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    String nowEmp_ID ="";

    //polygon
    private String positionid="";
    private String zoneid="";
    private String subzoneid="";
    private int Phase;

    //pan Listener
    Envelope rExtent;
    double Temp_Min_X1=0;
    double Temp_Min_X2=0;

    //touch Listener
    double cachedClickX=0;
    double cachedClickY=0;
    double Vecter = 0 ;

    String tmp[] = null;

    LayoutInflater inflater;
    ViewGroup calloutContent ;
    LayoutInflater winflater;
    ViewGroup wcalloutContent ;

    TextView lblqoata_id;
    TextView lblfid;
    TextView lblgis_mark;
    TextView lblcyear;
    TextView lblcyearold;
    TextView lblstatus;
    TextView lblname;
    TextView lblarea;
    TextView lblvality;
    TextView lbltel;
    TextView lblssone;

    TableRow tableRowFarm;
    TableRow tableRowYear;
    TableRow tableRowOld;
    TableRow tableRowStatus;
    TableRow tableRowArea;
    TableRow tableRowRubberBreed;

    Button btncallout_edit;
    Button btncallout_cancle;
    Button btnGotoMap;

    String getFID;
    String getFARM_ID_KEY;
    float getLocFarmX;
    float getLocFarmY;

    float getLocHouseX;
    float getLocHouseY;
    String getCane_AC_ID;

    String MenuCheck="VIS";

    private Bitmap casheBitmap;
    byte[] default_byte = {0};

    Callout callout;

    //search dialog
    private Dialog Search_dialog;

    //spinner
    private ArrayList<String> mSearch = new ArrayList<String>();

    ArrayList<HashMap<String, String>> MebmerList = null;
    ArrayList<HashMap<String, String>> MebmerListHouse = null;
    ArrayList<HashMap<String, String>> Alert_MebmerList = null;
    SimpleAdapter sAdap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_farm);


        nowEmp_ID = getEmpID(this);

        map = (MapView) findViewById(R.id.map);

        map.setMinResolution(0.1);


        lblZoom = (TextView) findViewById(R.id.lblZoom);
        btnautopan = (ToggleButton) findViewById(R.id.btnautopan);

        map.setMinResolution(0.1);



        LicenseResult licenseResult = ArcGISRuntime.setClientId(CLIENT_ID);
        LicenseResult licenseResult2 = ArcGISRuntime.License.setLicense(LICENSE_STRING);
        LicenseLevel licenseLevel = ArcGISRuntime.License.getLicenseLevel();

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new AndroidRuntimeException(
                    "External storage (SD-Card) not mounted");
        }
        File appDbDir = new File(
                Environment.getExternalStorageDirectory(),
                "BaseMap");

        dbFile = new File(appDbDir, "Parcel Map");

        if (!appDbDir.exists()) {
            //appDbDir.mkdirs();
            local = new ArcGISLocalTiledLayer("file:///mnt/extSdCard/BaseMap/Parcel Map");

        } else {

            local = new ArcGISLocalTiledLayer("file://" + String.valueOf(dbFile));
        }


        //local = new ArcGISLocalTiledLayer("file:///mnt/sdcard/BaseMap/Parcel Map");
        //local = new ArcGISLocalTiledLayer("file://"+String.valueOf(dbFile));
        map.addLayer(local);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        try {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            //imageCompass.setImageDrawable(getResources().getDrawable(R.drawable.compass128));
            map.setAllowRotationByPinch(true);
            map.enableWrapAround(true);

            mCompass = new Compass(this, null, map);
            MapView.LayoutParams params = new MapView.LayoutParams(332,332) ;

            map.addView(mCompass);


        } catch (Exception e) {
            // TODO: handle exception
            Log.d("SensorManager", String.valueOf(e));
        }

        callout = map.getCallout();
        callout.setStyle(R.xml.calloutstyle);

        cachedFARM_ID_KEY =new ArrayList<String>();
        cachedQOUTA_ID_KEY =new ArrayList<String>();
        cachedPolygon =new ArrayList<String>();
        cachedPolygonHouse =new ArrayList<String>();

        cachedACC_DISTANCE =new ArrayList<String>();

        inflater = getLayoutInflater();
        winflater = getLayoutInflater();
        calloutContent = (ViewGroup) inflater.inflate(R.layout.location_callout, null);


        // add graphics layer to draw on
        GPSlayer = new GraphicsLayer();
        map.addLayer(GPSlayer);

        // add graphics layer to draw on
        sketchlayer = new GraphicsLayer();
        map.addLayer(sketchlayer);

        // add graphics layer to draw on
        sketchHouseLayer = new GraphicsLayer();
        map.addLayer(sketchHouseLayer);

        // add graphics layer to draw on
        sketchwaterlayer = new GraphicsLayer();
        map.addLayer(sketchwaterlayer);

        // add graphics layer to draw on
        Symbollayer = new GraphicsLayer();
        map.addLayer(Symbollayer);

        // add graphics layer to draw on
        markpointlayer = new GraphicsLayer();
        map.addLayer(markpointlayer);

        // add graphics layer to draw on
        searchlayer = new GraphicsLayer();
        map.addLayer(searchlayer);

        // Set Pan
        myPanListenner = new MyPanListener();
        map.setOnPanListener(myPanListenner);

        // Set Zoom
        myZoomListener = new MyZoomListener();
        map.setOnZoomListener(myZoomListener);

        //Set Touch
        myListener = new MyTouchListener(ViewFarmActivity.this, map);
        map.setOnTouchListener(myListener);

        InitWidget();

    }

    private void InitWidget() {
        btn_map_search = (Button) findViewById(R.id.btn_map_search);
        btn_map_search.setOnClickListener(this);

        btnSearchHouse = (Button) findViewById(R.id.btnSearchHouse);
        btnSearchHouse.setOnClickListener(this);

        lblqoata_id = (TextView) calloutContent.findViewById(R.id.lblqoata_id);
        lblfid = (TextView) calloutContent.findViewById(R.id.lblfid);
        //lblgis_mark = (TextView) calloutContent.findViewById(R.id.lblgis_mark);
        lblcyear = (TextView) calloutContent.findViewById(R.id.lblcyear);
        lblcyearold = (TextView) calloutContent.findViewById(R.id.lblcyearold);
        lblstatus = (TextView) calloutContent.findViewById(R.id.lblstatus);
        lblname = (TextView) calloutContent.findViewById(R.id.lblcancle_project);
        lbltel = (TextView) calloutContent.findViewById(R.id.lblTel);
        lblarea = (TextView) calloutContent.findViewById(R.id.lblarea);
        lblvality = (TextView) calloutContent.findViewById(R.id.lblvality);
        lblssone = (TextView) calloutContent.findViewById(R.id.lblssone);
        btncallout_edit = (Button) calloutContent.findViewById(R.id.btncallout_edit);
        btncallout_cancle = (Button) calloutContent.findViewById(R.id.btncallout_cancle);
        btnGotoMap = (Button) calloutContent.findViewById(R.id.btnGotoMap);
        lbl_gpsX = (TextView) findViewById(R.id.lbl_gpsX);
        lbl_gpsY = (TextView) findViewById(R.id.lbl_gpsY);

        tableRowFarm = (TableRow) calloutContent.findViewById(R.id.TableRowFarm);
        tableRowYear = (TableRow) calloutContent.findViewById(R.id.TableRowYear);
        tableRowOld = (TableRow) calloutContent.findViewById(R.id.TableRowYearOld);
        tableRowStatus = (TableRow) calloutContent.findViewById(R.id.TableRowStatus);
        tableRowArea = (TableRow) calloutContent.findViewById(R.id.TableRowArea);
        tableRowRubberBreed = (TableRow) calloutContent.findViewById(R.id.TableRowRubberBreed);

        btnGotoMap.setVisibility(View.VISIBLE);
    }
    private void createSearchData() {
        mSearch.add("โควต้า");
        mSearch.add("เลขแปลง");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_map_search:
                Search_Dialog();
                break;

            case R.id.btnSearchHouse:
                SearchHouse_Dialog();
                break;
        }

    }

    private void Search_Dialog()
    {
        Search_dialog = new Dialog(this, R.style.FullHeightDialog); //this is a reference to the style above
        Search_dialog.setContentView(R.layout.search_popup); //I saved the xml file above as yesnomessage.xml
        Search_dialog.setCancelable(true);


        Button btnDismiss = (Button)Search_dialog.findViewById(R.id.dismiss);

        final EditText txt_search_qt = (EditText) Search_dialog.findViewById(R.id.txt_search_qt);
        Button btn_map_search = (Button)Search_dialog.findViewById(R.id.btn_map_search);
        final ListView lst_Map_Search = (ListView) Search_dialog.findViewById(R.id.lst_Map_Search);


        btn_map_search.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if(txt_search_qt.getText().toString().trim().equals("") ){

                    if(txt_search_qt.getText().toString().trim().equals("")) {
                        OK_Dialog("กรุณากรอกข้อมูล!");
                    }

//                    if(txt_search_qt.length()<6) {
//                        OK_Dialog("กรุณากรอกเลขโควต้าให้ครบถ้วน");
//                    }
                }else{

                    try {

                        SQLiteDatabase db;
                        String DBFile=DATABASE_FSCGISDATANAME+".sqlite";

                        ExternalStorage_Farm_GIS_DB_OpenHelper obj= new ExternalStorage_Farm_GIS_DB_OpenHelper(getApplicationContext(),DBFile);
                        if(obj.databaseFileExists()) {

                            db=obj.getReadableDatabase();

                            MebmerList = obj.getFarmGisSearch(txt_search_qt.getText().toString().trim(),

                                    db);
                            obj.close();
                        }

                        if (MebmerList == null || String.valueOf(MebmerList).trim().equals("[]") ){

                            OK_Dialog("ไม่พบหมายเลขแปลงหรือหมายเลขโควต้านี้ !!!");
                        }
                        else {
                            sAdap = new SimpleAdapter(ViewFarmActivity.this, MebmerList, R.layout.search_list_item
                                    ,new String[] {"FARM_ID","FARM_AREA","FARM_STATUS"}
                                    ,new int[] {R.id.Col_F_ID,R.id.Col_Area, R.id.Col_Sta});


                            lst_Map_Search.setAdapter(sAdap);
                        }
                        //Log.d("SQLData", String.valueOf(myData));




                    } catch (Exception e) {
                        // TODO: handle exception
                        OK_Dialog("ไม่พบหมายเลขแปลงหรือหมายเลขโควต้านี้ !!!");
                    }

                }
            }});






        lst_Map_Search.setOnItemClickListener(new ListView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long mylng) {

                //Log.d("Search_OnItemClick", "Click");


                try {
                    btnautopan.setChecked(false);
                    searchlayer.removeAll();

                    final String GIS_X = MebmerList.get(position).get("FARM_GEOX").toString();
                    final String GIS_Y = MebmerList.get(position).get("FARM_GEOY").toString();

                    Point sp = new Point((float)(Float.parseFloat(GIS_X)),
                            (float)(Float.parseFloat(GIS_Y)));

                    map.centerAt(sp,true );
                    //map.zoomToScale(sp, 11000);

                    searchlayer.addGraphic(new Graphic(sp,new PictureMarkerSymbol(ViewFarmActivity.this.getResources().getDrawable(
                            R.drawable.pin_red))));
                    //map.setScale(30000);
                    //Map_Scale = map.getScale();

                    Search_dialog.dismiss();
                } catch (Exception e) {
                    // TODO: handle exception
                }



            }
        });



        btnDismiss.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                Search_dialog.dismiss();
            }});

        Search_dialog.show();
    }

    private void SearchHouse_Dialog()
    {
        Search_dialog = new Dialog(this, R.style.FullHeightDialog); //this is a reference to the style above
        Search_dialog.setContentView(R.layout.search_house_popup); //I saved the xml file above as yesnomessage.xml
        Search_dialog.setCancelable(true);


        Button btnDismiss = (Button)Search_dialog.findViewById(R.id.dismiss);

        final EditText txt_search_qt = (EditText) Search_dialog.findViewById(R.id.txt_search_qt);
        Button btn_map_search = (Button)Search_dialog.findViewById(R.id.btn_map_search);
        final ListView lst_Map_Search = (ListView) Search_dialog.findViewById(R.id.lst_Map_Search);


        btn_map_search.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if(txt_search_qt.getText().toString().trim().equals("") ){

                    if(txt_search_qt.getText().toString().trim().equals("")) {
                        OK_Dialog("กรุณากรอกข้อมูล!");
                    }

//                    if(txt_search_qt.length()<6) {
//                        OK_Dialog("กรุณากรอกเลขโควต้าให้ครบถ้วน");
//                    }
                }else{



                    try {

                        SQLiteDatabase db;
                        String DBFile=DATABASE_FSCGISDATANAME+".sqlite";

                        ExternalStorage_House_GIS_DB_OpenHelper obj= new ExternalStorage_House_GIS_DB_OpenHelper(DBFile);
                        if(obj.databaseFileExists()) {

                            db=obj.getReadableDatabase();

                            MebmerListHouse = obj.getHouseGisSearch(txt_search_qt.getText().toString().trim(), db);

                            obj.close();

                            if (MebmerListHouse == null || String.valueOf(MebmerListHouse).trim().equals("[]") ){
                                ExternalStorage_House_GIS_DB_OpenHelper obj2= new ExternalStorage_House_GIS_DB_OpenHelper(DBFile);
                                if(obj2.databaseFileExists()) {

                                    db = obj2.getReadableDatabase();

                                    MebmerListHouse = obj2.getHouseGisSearchByName(txt_search_qt.getText().toString().trim(), db);

                                    obj2.close();
                                }
                            }


                            Log.d("memberListHouse", String.valueOf(MebmerListHouse));
                        }

                        if (MebmerListHouse == null || String.valueOf(MebmerListHouse).trim().equals("[]") ){

                            OK_Dialog("ไม่พบหมายเลขแปลงหรือหมายเลขโควต้านี้ !!!");

                        }
                        else
                        {

                            sAdap = new SimpleAdapter(ViewFarmActivity.this, MebmerListHouse, R.layout.search_house_list_item
                                    ,new String[] {"QOUTA_ID","MEM_NAME","ZONE"}
                                    ,new int[] {R.id.Col_F_ID,R.id.Col_Area, R.id.Col_Sta});


                            lst_Map_Search.setAdapter(sAdap);
                        }
                        //Log.d("SQLData", String.valueOf(myData));




                    } catch (Exception e) {
                        // TODO: handle exception
                        OK_Dialog("ไม่พบหมายเลขแปลงหรือหมายเลขโควต้านี้ !!!");
                    }

                }
            }});






        lst_Map_Search.setOnItemClickListener(new ListView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long mylng) {

                //Log.d("Search_OnItemClick", "Click");


                try {
                    btnautopan.setChecked(false);
                    searchlayer.removeAll();

                    final String GIS_X = MebmerListHouse.get(position).get("HOUSE_GEOX").toString();
                    final String GIS_Y = MebmerListHouse.get(position).get("HOUSE_GEOY").toString();

                    Point sp = new Point((float)(Float.parseFloat(GIS_X)),
                            (float)(Float.parseFloat(GIS_Y)));

                    map.centerAt(sp,true );
                    //map.zoomToScale(sp, 11000);

                    searchlayer.addGraphic(new Graphic(sp,new PictureMarkerSymbol(ViewFarmActivity.this.getResources().getDrawable(
                            R.drawable.pin_red))));
                    //map.setScale(30000);
                    //Map_Scale = map.getScale();

                    Search_dialog.dismiss();
                } catch (Exception e) {
                    // TODO: handle exception
                }



            }
        });



        btnDismiss.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                Search_dialog.dismiss();
            }});

        Search_dialog.show();
    }



    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            try {

                lDisplayManager = map.getLocationDisplayManager();
                lDisplayManager.start();


                if(location != null){


                    location.setLatitude(location.getLatitude());
                    location.setLongitude(location.getLongitude());

                    //Log.d("Get_Val_Longitude", String.valueOf(location.getLongitude()));
                    //Log.d("Get_Val_Latitude", String.valueOf(location.getLatitude()));

                    Point mapPoint = new Point(location.getLongitude(), location.getLatitude());
                    if (4236 != map.getSpatialReference().getID()) {
                        mapPoint = (Point) GeometryEngine.project(mapPoint, SpatialReference.create(4326),
                                map.getSpatialReference());
                    }

                    if (mapPoint == null || mapPoint.isEmpty()) {
                        Toast.makeText(ViewFarmActivity.this, "Cannot determine current location", Toast.LENGTH_SHORT).show();
                        return;
                    }




                    cachedGPSX = mapPoint.getX();
                    cachedGPSY = mapPoint.getY();

                    lbl_gpsX.setText(String.valueOf(Integer.valueOf((int) mapPoint.getX()) ));
                    lbl_gpsY.setText(String.valueOf(Integer.valueOf((int) mapPoint.getY()) ));

                    Log.d("Get_Val_GPSX", String.valueOf(cachedGPSX));
                    Log.d("Get_Val_GPSY", String.valueOf(cachedGPSY));


                    //set the cachedLocation of the main activity
                    //it is used by the QuerySources and QueryCatchment tasks
                    cachedLocation = mapPoint;

                    //pan to the location, animation enabled
                    //map.centerAt(mapPoint,true );
                    if(btnautopan.isChecked())
                    {
                        //map.centerAt(mapPoint,true );
                        //Toast.makeText(getApplicationContext(), "Autopland Click", Toast.LENGTH_SHORT).show();
                        lDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);

						 	/*if(lm == null){

						 		Toast.makeText(getApplicationContext(), "Autopland Click", Toast.LENGTH_SHORT).show();

						 		lm=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
								locationListener = new MyLocationListener();
								lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
								lDisplayManager.setAutoPanMode(AutoPanMode.LOCATION);

						 	}*/
                    }

                    GPSlayer.removeAll();


                    //GPSlayer.addGraphic(new Graphic(mapPoint,new SimpleMarkerSymbol(Color.YELLOW,25,STYLE.CIRCLE)));
                    //GPS2layer.addGraphic(new Graphic(mapPoint,new SimpleMarkerSymbol(Color.BLACK,27,STYLE.CIRCLE)));



                    //GPSlayer.addGraphic(new Graphic(mapPoint,new SimpleMarkerSymbol(Color.rgb(0, 0, 255),25,STYLE.CIRCLE)));


                    //GPSlayer.addGraphic(new Graphic(cachedLocation,
                    //new PictureMarkerSymbol(getResources().getDrawable(R.drawable.gps18))));



                }

            } catch (Exception e) {
                // TODO: handle exception
                Toast.makeText(getApplicationContext(), "MyLocationListener Error : "+String.valueOf(e) , Toast.LENGTH_LONG).show();
            }


        }



        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    }

    class MyPanListener implements OnPanListener {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public void postPointerMove(float arg0, float arg1, float arg2,
                                    float arg3) {
            // TODO Auto-generated method stub

            rExtent=new Envelope();
            map.getExtent().queryEnvelope(rExtent);

            double Min_X=rExtent.getXMin();  //ด้านบน
            double Min_Y=rExtent.getYMin();  //ด้านบน
            double Max_X=rExtent.getXMax();  //ด้านล่าง
            double Max_Y=rExtent.getYMax();  //ด้านล่าง


            Log.d("Get_Val_Pan_Min_X", String.valueOf(Min_X));
            Log.d("Get_Val_Pan_Min_Y", String.valueOf(Min_Y));
            Log.d("Get_Val_Pan_Max_X", String.valueOf(Max_X));
            Log.d("Get_Val_Pan_Max_Y", String.valueOf(Max_Y));


            if (Map_Scale > 35000){

                sketchlayer.removeAll();
                sketchHouseLayer.removeAll();
                Symbollayer.removeAll();
                sketchwaterlayer.removeAll();

            }

        }

        public void postPointerUp(float arg0, float arg1, float arg2, float arg3) {
            // TODO Auto-generated method stub
            if (Map_Scale > 35000){

                sketchlayer.removeAll();
                sketchHouseLayer.removeAll();
                Symbollayer.removeAll();
                sketchwaterlayer.removeAll();

            }
        }

        public void prePointerMove(float arg0, float arg1, float arg2,
                                   float arg3) {
            // TODO Auto-generated method stub
            if (Map_Scale > 35000){

                sketchlayer.removeAll();
                sketchHouseLayer.removeAll();
                Symbollayer.removeAll();
                sketchwaterlayer.removeAll();

            }

        }

        public void prePointerUp(float arg0, float arg1, float arg2, float arg3) {
            // TODO Auto-generated method stub
            //if (callout.isShowing()) {
            //callout.hide();
            //}

            rExtent=new Envelope();
            map.getExtent().queryEnvelope(rExtent);
            double Map_Scale = map.getScale();


            double Min_X=rExtent.getXMin();  //ด้านบน

            if (Temp_Min_X1 == 0){
                Temp_Min_X1 = Min_X ;
            }else if (Min_X > Temp_Min_X1){


                Temp_Min_X2 = (Min_X - Temp_Min_X1);
                Temp_Min_X1 = Min_X ;

            }else{


                Temp_Min_X2 = (Temp_Min_X1 - Min_X);
                Temp_Min_X1 = Min_X ;
            }


            if (Temp_Min_X2 > 70 && Temp_Min_X1 != 0){

                if (Map_Scale < 35000){

                    sketchlayer.removeAll();
                    sketchHouseLayer.removeAll();
                    Symbollayer.removeAll();
                    sketchwaterlayer.removeAll();

						/*if(MenuCheck.trim().equals("WAT")){
							loadWaterAsync = new LoadWaterAsync();
							loadWaterAsync.execute();
						}*/

                    loadMapAsync = new LoadMapAsync();
                    loadMapAsync.execute();



                }else{
                    sketchlayer.removeAll();
                    sketchHouseLayer.removeAll();
                    Symbollayer.removeAll();
                    sketchwaterlayer.removeAll();

                }

            }



        }}

    class MyZoomListener implements OnZoomListener {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public void postAction(float arg0, float arg1, double arg2) {
            // TODO Auto-generated method stub


            rExtent=new Envelope();
            map.getExtent().queryEnvelope(rExtent);

            double Min_X=rExtent.getXMin();  //ด้านบน
            double Min_Y=rExtent.getYMin();  //ด้านบน
            double Max_X=rExtent.getXMax();  //ด้านล่าง
            double Max_Y=rExtent.getYMax();  //ด้านล่าง


            Log.d("Get_Val_Zoom_Min_X", String.valueOf(Min_X));
            Log.d("Get_Val_Zoom_Min_Y", String.valueOf(Min_Y));
            Log.d("Get_Val_Zoom_Max_X", String.valueOf(Max_X));
            Log.d("Get_Val_Zoom_Max_Y", String.valueOf(Max_Y));


            //double Resolution;
            Map_Scale = map.getScale();



            if (Map_Scale < 35000){

                //if (callout.isShowing()) {
                //callout.hide();
                //}
                sketchlayer.removeAll();
                sketchHouseLayer.removeAll();
                Symbollayer.removeAll();
                sketchwaterlayer.removeAll();


				/*if(MenuCheck.trim().equals("WAT")){
							loadWaterAsync = new LoadWaterAsync();
							loadWaterAsync.execute();
				}*/

                loadMapAsync = new LoadMapAsync();
                loadMapAsync.execute();


            }else{


                sketchlayer.removeAll();
                Symbollayer.removeAll();
                sketchwaterlayer.removeAll();

            }

            lblZoom.setText(String.format("%.3f %n", Map_Scale));
        }

        public void preAction(float arg0, float arg1, double arg2) {
            // TODO Auto-generated method stub

            if (Map_Scale > 35000){
                sketchlayer.removeAll();
                sketchHouseLayer.removeAll();
                Symbollayer.removeAll();
                sketchwaterlayer.removeAll();
            }

            lblZoom.setText(String.format("%.3f %n", Map_Scale));
        }}

    class MyTouchListener extends MapOnTouchListener {


        MultiPath poly;
        String type = "POINT";
        Point startPoint = null;

        public MyTouchListener(Context context, MapView view) {
            super(context, view);
        }

        public void setType(String geometryType) {
            this.type = geometryType;
        }

        public String getType() {
            return this.type;
        }

        /*
         * Invoked when user single taps on the map view. This event handler
         * draws a point at user-tapped location, only after "Draw Point" is
         * selected from Spinner.
         *
         * @see
         * com.esri.android.map.MapOnTouchListener#onSingleTap(android.view.
         * MotionEvent)
         */



        @SuppressLint("LongLogTag")
        public boolean onSingleTap(MotionEvent e) throws NumberFormatException {
            //Log.d("PointClick", "onSingleTap");
            double Map_Scale = map.getScale();


            if (Map_Scale < 35000){
                Point PointClick = map.toMapPoint(e.getX(), e.getY());

                cachedClickX = PointClick.getX();
                cachedClickY = PointClick.getY();

                double X1 = cachedGPSX ;
                double Y1 = cachedGPSY ;
                double X2 = cachedClickX ;
                double Y2 = cachedClickY ;

                /*Vecter = Math.sqrt(Math.pow((X2-X1), 2))+ Math.sqrt( Math.pow((Y2-Y1), 2));

                if(Vecter < 0){
                    Vecter = Vecter*-1;
                }*/
                //convert xy to latlong by click
                /*
                SpatialReference sp = SpatialReference.create(4326);
                Point aux = (Point) GeometryEngine.project(PointClick, map.getSpatialReference(), sp);

                System.out.println("PointClick: "+PointClick);
                System.out.println("PointLatLongTest: "+aux);
                */


                if (type.length() > 1 && type.equalsIgnoreCase("POINT")) {

                    markpointlayer.removeAll();

                    Point mapPt = map.toMapPoint(e.getX(), e.getY());

                    markpointlayer.addGraphic(new Graphic(mapPt,new SimpleMarkerSymbol(Color.RED,8, SimpleMarkerSymbol.STYLE.CIRCLE)));


                    double PX = mapPt.getX();
                    double PY = mapPt.getY();



                    try {

                        //---------------------- FARM FOR CLICK -------------------------//
                        for (int x = 0; x < cachedFARM_ID_KEY.size(); x++) {

                            ArrayList<Float> verticesX ;
                            ArrayList<Float> verticesY ;

                            int vertices_count = 0 ;
                            int intersections = 0;
                            String strtmp1;
                            String strtmp2;

                            String [] tmp1;
                            String [] tmp2;



                            verticesX = new ArrayList<Float>();
                            verticesY=new ArrayList<Float>();


                            strtmp1 = cachedPolygon.get(x).toString().replace("POLYGON ((", "");
                            strtmp2 = strtmp1.replace("))", "");

                            tmp1=strtmp2.split(", ");

                            ////////////////// Loop 2 //////////////////
                            for(int y=0; y<tmp1 .length;y++){
                                tmp2=tmp1[y].split(" ");

                                verticesX.add(Float.parseFloat(tmp2 [0].toString().trim()));
                                verticesY.add(Float.parseFloat(tmp2 [1].toString().trim()));

                            }

                            vertices_count = verticesX.size();
                            //Log.d("PointClick_vertices_count",String.valueOf(vertices_count));


                            for (int i=1; i < vertices_count; i++) {
                                float vertex1X=verticesX.get(i-1);
                                float vertex1Y=verticesY.get(i-1);
                                float vertex2X=verticesX.get(i);
                                float vertex2Y=verticesY.get(i);

                                if (vertex1Y == vertex2Y && vertex1Y == PY &&
                                        PX > getMinXY(vertex1X, vertex2X) &&
                                        PX < getMaxXY(vertex1X, vertex2X)) { // Check if point is on an horizontal polygon boundary
                                    //return "boundary";
                                }

                                if (PY > getMinXY(vertex1Y, vertex2Y) &&
                                        PY <= getMaxXY(vertex1Y, vertex2Y) &&
                                        PX <= getMaxXY(vertex1X, vertex2X) &&
                                        vertex1Y != vertex2Y) {

                                    double xinters = (PY - vertex1Y) * (vertex2X - vertex1X) / (vertex2Y - vertex1Y) + vertex1X;
                                    //Log.d("PointClick", "xinters : "+String.valueOf(xinters));
                                    if (xinters == PX) { // Check if point is on the polygon boundary (other than horizontal)
                                        //return "boundary";
                                    }
                                    if (vertex1X == vertex2X || PX <= xinters) {
                                        intersections++;
                                    }
                                }

                            }

                            if ((intersections % 2) != 0) {


                                //LayoutInflater inflater = getLayoutInflater();
                                //ViewGroup calloutContent = (ViewGroup) inflater.inflate(R.layout.location_callout, null);
                                Vecter = Math.sqrt(Math.pow((X2-X1), 2))+ Math.sqrt( Math.pow((Y2-Y1), 2));

                                if(Vecter < 0){
                                    Vecter = Vecter*-1;
                                }


                                String FARM_ID_KEY = cachedFARM_ID_KEY.get(x);
                                Log.d("getCaneCallout_GIS_ID_KEY", FARM_ID_KEY);

                                if(positionid.toString().trim().equals("5")){
                                    Phase = Integer.valueOf(cachedACC_DISTANCE.get(x));
                                }else {
                                    Phase=150;
                                }



                                //Stack_Sta = cachedStack_Sta.get(x);
                                //Stack_Date = cachedStack_Date.get(x);


                                SQLiteDatabase db;
                                String DBFile= DATABASE_FSCGISDATANAME + ".sqlite";
                                Log.d("getCaneCallout_DBFile", DBFile);
                                String tmpData1;
                                List<sMemberCalloutDatas> CalloutData=null ;
                                Log.d("getCaneCallout_DBFile", "01");
                                ExternalStorage_Farm_GIS_DB_OpenHelper obj= new ExternalStorage_Farm_GIS_DB_OpenHelper(getApplicationContext(),DBFile);
                                if(obj.databaseFileExists())
                                {
                                    Log.d("getCaneCallout_DBFile", "02");
                                    db=obj.getReadableDatabase();
                                    CalloutData = obj.getCaneCallout(FARM_ID_KEY, db);
                                    Log.d("getCaneCallout_DBFile", "03");
                                    for (sMemberCalloutDatas mem : CalloutData) {

                                        //open
                                        tableRowFarm.setVisibility(View.VISIBLE);
                                        tableRowYear.setVisibility(View.VISIBLE);
                                        tableRowOld.setVisibility(View.VISIBLE);
                                        tableRowStatus.setVisibility(View.VISIBLE);
                                        tableRowArea.setVisibility(View.VISIBLE);
                                        tableRowRubberBreed.setVisibility(View.VISIBLE);
                                        btncallout_edit.setVisibility(View.VISIBLE);


                                        lblqoata_id.setText(mem.gQOUTA_ID().toString());
                                        lblfid.setText(mem.gFARM_ID().toString());
                                        getFID=mem.gFARM_ID().toString();
                                        getFARM_ID_KEY = FARM_ID_KEY;

                                        //lblgis_mark.setText(mem.gGIS_MARK().toString());
                                        //lblstatus.setText(Case_Cane(mem.gCASE_CANE().toString()));
                                        lblstatus.setText(mem.gFARM_STATUS().toString());
                                        lblcyear.setText(mem.gFARM_YEAROFPLANT().toString());

                                        int yearPlant = Integer.parseInt(mem.gFARM_YEAROFPLANT());
                                        Calendar calendar = Calendar.getInstance();
                                        int year = calendar.get(Calendar.YEAR)+543;

                                        int yearOld = year - yearPlant;
                                        lblcyearold.setText(String.valueOf(yearOld)+" ปี");

                                        lblname.setText(mem.gMEM_GENDER()+mem.gMEM_NAME()+" "+" "+mem.gMEM_LASTNAME());
                                        lbltel.setText(mem.gMEM_TEL().toString());
                                        lblarea.setText(mem.gFARM_AREA().toString());
                                        lblvality.setText(mem.gFARM_RUBBERBREED().toString());
                                        lblssone.setText(mem.gEMP_NAME().toString());

                                        try {
                                            getLocFarmX = Float.parseFloat(mem.gFARM_GEOX());
                                            getLocFarmY = Float.parseFloat(mem.gFARM_GEOY());
                                            Log.d("centerLocation","("+getLocFarmX +" "+getLocFarmY+  ")" );
                                        } catch (NumberFormatException ignored){
                                            Log.e("getPointLocation", String.valueOf(ignored));
                                        }

                                    }
                                    Log.d("getCaneCallout_DBFile", "04");
                                    obj.close();

                                }


                                btncallout_edit.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        // 150meter ready activity
//                                        if(Vecter <= Phase){
                                            if(Vecter >= Phase){
                                            Intent MenuActivityFarmPage = new Intent(ViewFarmActivity.this,
                                                    MenuActivityFarmActivity.class);
                                            Bundle b = new Bundle();
                                            b.putString("Farm_ID",getFID.toString());

                                            MenuActivityFarmPage.putExtras(b);

                                            startActivity(MenuActivityFarmPage);




                                        }//ไม่เกิน 50 เมตร
                                        else{ // เกิน 50เมตร
                                            OK_Dialog("ระยะห่างเกิน  "+ String.valueOf(Phase) +" เมตร");
                                        }



                                        if(lm != null){
                                            lm.removeUpdates(locationListener);
                                            lm=null;
                                        }

                                        //---------------------------------------------- Vector 50 Meter ----------------------------------

                                    }
                                });

                                btnGotoMap.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {

                                        //get lat long
                                        Point PointTo = new Point(getLocFarmX,getLocFarmY);

                                        SpatialReference sp = SpatialReference.create(4326);
                                        Point PointToLatLong = (Point) GeometryEngine.project(PointTo, map.getSpatialReference(), sp);

                                        System.out.println("PointTo: "+PointTo);
                                        System.out.println("PointLatLong: "+ PointToLatLong);


                                        // end get lat log
                                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+PointToLatLong.getY()+","+PointToLatLong.getX());
                                        //Uri gmmIntentUri = Uri.parse("geo:"+PointToLatLong.getY()+","+PointToLatLong.getX());

                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        startActivity(mapIntent);


                                    }
                                });

                                btncallout_cancle.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        callout.hide();
                                    }
                                });



                                callout.setMaxHeight(5000);
                                callout.setMaxWidth(5000);
                                callout.setCoordinates(mapPt);
                                callout.setContent(calloutContent);
                                callout.refresh();
                                callout.show();


                                break;
                            } //else {
                            //inside = false;
                            //return "outside";
                            //cachedClickX = 0;
                            //cachedClickY = 0;

                            //Vecter=1000;
                            //}

                        }

                        //---------------------- END FARM FOR CLICK -------------------------//

//---------------------- HOUSE FOR CLICK -----------------------//
                        for(int x=0;x<cachedQOUTA_ID_KEY.size(); x++ ){
                            ArrayList<Float> verticesX ;
                            ArrayList<Float> verticesY ;

                            int vertices_count = 0 ;
                            int intersections = 0;
                            String strtmp1;
                            String strtmp2;

                            String [] tmp1;
                            String [] tmp2;



                            verticesX = new ArrayList<Float>();
                            verticesY=new ArrayList<Float>();


                            strtmp1 = cachedPolygonHouse.get(x).toString().replace("POLYGON ((", "");
                            strtmp2 = strtmp1.replace("))", "");

                            tmp1=strtmp2.split(", ");

                            ////////////////// Loop 2 //////////////////
                            for(int y=0; y<tmp1 .length;y++){
                                tmp2=tmp1[y].split(" ");

                                verticesX.add(Float.parseFloat(tmp2 [0].toString().trim()));
                                verticesY.add(Float.parseFloat(tmp2 [1].toString().trim()));

                            }

                            vertices_count = verticesX.size();
                            //Log.d("PointClick_vertices_count",String.valueOf(vertices_count));

                            for (int i=1; i < vertices_count; i++) {
                                float vertex1X=verticesX.get(i-1);
                                float vertex1Y=verticesY.get(i-1);
                                float vertex2X=verticesX.get(i);
                                float vertex2Y=verticesY.get(i);

                                if (vertex1Y == vertex2Y && vertex1Y == PY &&
                                        PX > getMinXY(vertex1X, vertex2X) &&
                                        PX < getMaxXY(vertex1X, vertex2X)) { // Check if point is on an horizontal polygon boundary
                                    //return "boundary";
                                }

                                if (PY > getMinXY(vertex1Y, vertex2Y) &&
                                        PY <= getMaxXY(vertex1Y, vertex2Y) &&
                                        PX <= getMaxXY(vertex1X, vertex2X) &&
                                        vertex1Y != vertex2Y) {

                                    double xinters = (PY - vertex1Y) * (vertex2X - vertex1X) / (vertex2Y - vertex1Y) + vertex1X;
                                    //Log.d("PointClick", "xinters : "+String.valueOf(xinters));
                                    if (xinters == PX) { // Check if point is on the polygon boundary (other than horizontal)
                                        //return "boundary";
                                    }
                                    if (vertex1X == vertex2X || PX <= xinters) {
                                        intersections++;
                                    }
                                }

                            }

                            if ((intersections % 2) != 0) {

                                //LayoutInflater inflater = getLayoutInflater();
                                //ViewGroup calloutContent = (ViewGroup) inflater.inflate(R.layout.location_callout, null);
                                Vecter = Math.sqrt(Math.pow((X2 - X1), 2)) + Math.sqrt(Math.pow((Y2 - Y1), 2));

                                if (Vecter < 0) {
                                    Vecter = Vecter * -1;
                                }


                                String QOUTA_ID_KEY = cachedQOUTA_ID_KEY.get(x);
                                Log.d("getCaneCallout_GIS_ID_KEY", QOUTA_ID_KEY);

                                if (positionid.toString().trim().equals("5")) {
                                    Phase = Integer.valueOf(cachedACC_DISTANCE.get(x));
                                } else {
                                    Phase = 150;
                                }

                                SQLiteDatabase db;
                                String DBFile= DATABASE_FSCGISDATANAME + ".sqlite";
                                Log.d("getCaneCallout_DBFile", DBFile);
                                String tmpData1;
                                List<ExternalStorage_House_GIS_DB_OpenHelper.sMemberCalloutDatasHouse> CalloutData=null ;
                                Log.d("getCaneCallout_DBFile", "01");
                                ExternalStorage_House_GIS_DB_OpenHelper obj= new ExternalStorage_House_GIS_DB_OpenHelper(DBFile);
                                if(obj.databaseFileExists())
                                {
                                    Log.d("getCaneCallout_DBFile", "02");
                                    db=obj.getReadableDatabase();
                                    CalloutData = obj.getCaneCalloutHouse(QOUTA_ID_KEY, db);
                                    Log.d("getCaneCallout_DBFile", "03");
                                    for (ExternalStorage_House_GIS_DB_OpenHelper.sMemberCalloutDatasHouse mem : CalloutData) {

                                        lblqoata_id.setText(mem.gQOUTA_ID().toString());
                                        Calendar calendar = Calendar.getInstance();
                                        int year = calendar.get(Calendar.YEAR)+543;


                                        lblname.setText(mem.gMEM_GENDER()+mem.gMEM_NAME()+" "+" "+mem.gMEM_LASTNAME());
                                        lbltel.setText(mem.gMEM_TEL().toString());

                                        lblssone.setText(mem.gEMP_NAME().toString());

                                        //hide
                                        tableRowFarm.setVisibility(View.GONE);
                                        tableRowYear.setVisibility(View.GONE);
                                        tableRowOld.setVisibility(View.GONE);
                                        tableRowStatus.setVisibility(View.GONE);
                                        tableRowArea.setVisibility(View.GONE);
                                        tableRowRubberBreed.setVisibility(View.GONE);
                                        btncallout_edit.setVisibility(View.INVISIBLE);

                                        try {
                                            getLocHouseX = Float.parseFloat(mem.gHOUSE_LATITUDE());
                                            getLocHouseY = Float.parseFloat(mem.gHOUSE_LONGITUDE());
                                            Log.d("centerLocation","("+getLocHouseX +" "+getLocHouseY+  ")" );
                                        } catch (NumberFormatException ignored){
                                            Log.e("getPointLocation", String.valueOf(ignored));
                                        }

                                    }
                                    Log.d("getCaneCallout_DBFile", "04");
                                    obj.close();

                                    btncallout_cancle.setOnClickListener(new View.OnClickListener() {

                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            callout.hide();
                                        }
                                    });

                                    btnGotoMap.setOnClickListener(new View.OnClickListener() {

                                        public void onClick(View v) {

                                            //get lat long
                                            //Point PointTo = new Point(getLocHouseX,getLocHouseX);

                                            //SpatialReference sp = SpatialReference.create(4326);
                                            //Point PointToLatLong = (Point) GeometryEngine.project(PointTo, map.getSpatialReference(), sp);

                                            //System.out.println("PointTo: "+PointTo);
                                            //System.out.println("PointLatLong: "+ PointToLatLong);


                                            // end get lat log
                                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+getLocHouseX+","+getLocHouseY);
                                            //Uri gmmIntentUri = Uri.parse("geo:"+PointToLatLong.getY()+","+PointToLatLong.getX());

                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");
                                            startActivity(mapIntent);


                                        }
                                    });

                                    callout.setMaxHeight(5000);
                                    callout.setMaxWidth(5000);
                                    callout.setCoordinates(mapPt);
                                    callout.setContent(calloutContent);
                                    callout.refresh();
                                    callout.show();


                                    break;

                                }


                            }



                        }
                        //---------------------- END HOUSE FOR CLICK -----------------------//



                    } catch (Exception e2) {
                        // TODO: handle exception
                    }
                    //-------Cane-------



                    return true;
                }// หา Polygon


            }
            return false;


        }

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

    public float getMaxXY(float XY1, float XY2 ){

        float fPoint=0;

        if (XY1 > XY2){
            fPoint=XY1;
        }else{
            fPoint=XY2;
        }
        return fPoint;

    }

    public String Case_Cane(String CC){

        String Resault;

        if(CC.equals("B")){
            Resault="ไม้ยางพารา";
        }else if(CC.equals("F")){
            Resault="ไม้ยางพารา";
        }else if(CC.equals("N")){
            Resault="ไม้ยางพารา";
        }else if(CC.equals("S")){
            Resault="ไม้ยางพารา";
        }else if(CC.equals("W")){
            Resault="ไม้ยางพารา";
        }else if(CC.equals("L")){
            Resault="ไม้ยางพารา";
        }else{
            Resault="ไม้ยางพารา";
        }

        return Resault;

    }

    private void OK_Dialog(String Message)
    {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog); //this is a reference to the style above
        dialog.setContentView(R.layout.far_dialog); //I saved the xml file above as yesnomessage.xml
        dialog.setCancelable(true);


        //to set the message
        TextView message =(TextView) dialog.findViewById(R.id.tvmessagedialogtext);
        message.setText(Message);

        //add some action to the buttons

        Button ok = (Button) dialog.findViewById(R.id.bmessageDialogOK);
        ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();
    }


    public class LoadMapAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Log.d("Get_Val_strSQL", "LoadMapAsync");


            cachedFARM_ID_KEY.clear();
            cachedQOUTA_ID_KEY.clear();
            cachedPolygon.clear();
            cachedPolygonHouse.clear();


            rExtent=new Envelope();
            map.getExtent().queryEnvelope(rExtent);

            double Min_X=rExtent.getXMin();  //ด้านบน
            double Min_Y=rExtent.getYMin();  //ด้านบน
            double Max_X=rExtent.getXMax();  //ด้านล่าง
            double Max_Y=rExtent.getYMax();  //ด้านล่าง

            //--------------- FARM -----------------------//
            SQLiteDatabase db;
            //String DBFile=DATABASE_FSCGISDATANAME + UserZone+".sqlite";
            String DBFile= DATABASE_FSCGISDATANAME + ".sqlite";
            Log.d("Get_Val_strSQL", DBFile);
            // ExternalStorageReadOnlyOpenHelper.open();
            List<sMemberDrawPolygons> Poly_MebmerList=null ;


            ExternalStorage_Farm_GIS_DB_OpenHelper obj= new ExternalStorage_Farm_GIS_DB_OpenHelper(getApplicationContext(),DBFile);
            if(obj.databaseFileExists())
            {
                db=obj.getReadableDatabase();
                Poly_MebmerList = obj.SelectDataDrawPolygon(Max_X ,
                        Min_X ,
                        Max_Y ,
                        Min_Y ,db);
                for (sMemberDrawPolygons mem : Poly_MebmerList) {

                    cachedFARM_ID_KEY.add(mem.gFARM_ID());
                    cachedPolygon.add(mem.gFARM_GEO());
                    Log.d("FARM_ID",String.valueOf(mem.gFARM_GEO()));
                    CreatePolygon(mem.gFARM_GEO(),mem.gFARM_STATUS(),
                            mem.gEMP_ID());

                }

                obj.close();

            }
            //--------------- END FARM -----------------------//

            //--------------- MEMBER HOUSE -----------------------//

            List<sMemberDrawPolygonsHouse> Poly_MebmerHouseList=null ;
            ExternalStorage_House_GIS_DB_OpenHelper obj2 = new ExternalStorage_House_GIS_DB_OpenHelper(DBFile);
            if(obj2.databaseFileExists()) {
                db = obj2.getReadableDatabase();
                Poly_MebmerHouseList = obj2.SelectDataDrawPolygonHouse(Max_X,
                        Min_X,
                        Max_Y,
                        Min_Y,
                        db);
                for (sMemberDrawPolygonsHouse mem : Poly_MebmerHouseList){
                    cachedQOUTA_ID_KEY.add(mem.gQOUTA_ID());
                    cachedPolygonHouse.add(mem.gHOUSE_GEO());
                    Log.d("QOUTA_ID(HOUSE)",String.valueOf(mem.gHOUSE_GEO()));
                    CreatePolygonHouse(mem.gHOUSE_GEO());

                }
            }

            //--------------- END MEMBER HOUSE -----------------------//

            return null;
        }

    }

    public void CreatePolygon(String GeoPointData , String Farm_Status , String Emp_ID){

        String strtmp1;
        String strtmp2;
        String [] tmp1;
        String [] tmp2;

        strtmp1 = GeoPointData.replace("POLYGON ((", "");
        strtmp2 = strtmp1.replace("))", "");

        tmp1=strtmp2.split(", ");


        MultiPath PLG = new Polygon();
        for(int i=0; i<tmp1 .length;i++){
            tmp2=tmp1[i].split(" ");
            Point curPoint = new Point((float)(Float.parseFloat(tmp2 [0].toString())),
                    (float)(Float.parseFloat(tmp2 [1].toString())));


            if(i==0){
                PLG.startPath(curPoint);
            }else{
                PLG.lineTo(curPoint);
            }
        }

        CreatePolygonColor(PLG,Farm_Status,Emp_ID);
    }

    private void CreatePolygonColor(MultiPath PLG , String Farm_Status, String Emp_ID ){

        //แปลงทั่วไป
        if(Farm_Status.toString().trim().equals("G")){
            // Fill Color
            sketchlayer.addGraphic(new Graphic(PLG,new SimpleFillSymbol(Color.rgb(107, 142, 35))));

        }else if(Farm_Status.toString().trim().equals("R") ){
            //แปลงพร้อมตัด
            sketchlayer.addGraphic(new Graphic(PLG,new SimpleFillSymbol(Color.rgb(255, 255, 0))));

        }else if(Farm_Status.toString().trim().equals("RH")){
            //แปลงกำลัง
            sketchlayer.addGraphic(new Graphic(PLG,new SimpleFillSymbol(Color.rgb(255, 164, 96))));

        }else if(Farm_Status.toString().trim().equals("H")) {
            //แปลงตัดแล้ว
            sketchlayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(255, 69, 0))));
        }else if(Farm_Status.toString().trim().equals("C")) {
            //แปลงยกเลิก
            sketchlayer.addGraphic(new Graphic(PLG, new SimpleFillSymbol(Color.rgb(164, 164, 164))));
        }

        //Log.d("nowEmp_ID",String.valueOf(nowEmp_ID));
        if(nowEmp_ID.toString().equals(Emp_ID.toString())){
            Log.d("hear","hear equal");
            sketchlayer.addGraphic(new Graphic(PLG,new SimpleLineSymbol(Color.BLACK,1)));
        } else{
            Log.d("not equal",String.valueOf(Emp_ID));
            sketchlayer.addGraphic(new Graphic(PLG,new SimpleLineSymbol(Color.WHITE,1)));

        }

    }

    public void CreatePolygonHouse(String GeoPointData){
        String strtmp1;
        String strtmp2;
        String [] tmp1;
        String [] tmp2;

        strtmp1 = GeoPointData.replace("POLYGON ((", "");
        strtmp2 = strtmp1.replace("))", "");

        tmp1=strtmp2.split(", ");


        MultiPath PLG = new Polygon();
        for(int i=0; i<tmp1 .length;i++){
            tmp2=tmp1[i].split(" ");
            Point curPoint = new Point((float)(Float.parseFloat(tmp2 [0].toString())),
                    (float)(Float.parseFloat(tmp2 [1].toString())));


            if(i==0){
                PLG.startPath(curPoint);
            }else{
                PLG.lineTo(curPoint);
            }
        }

        sketchHouseLayer.addGraphic(new Graphic(PLG,new SimpleFillSymbol(Color.rgb(100, 67, 56))));
        sketchHouseLayer.addGraphic(new Graphic(PLG,new SimpleLineSymbol(Color.BLACK,1)));

    }

    private String getEmpID(Context context){
        SQLiteDatabase db;
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context,DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getReadableDatabase();
            nowEmp_ID = obj.GetEmpID("1", db);
            obj.close();

            return nowEmp_ID;
        }
        return "NULL";
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        new AlertDialog.Builder(this)

                .setMessage("ออกจากการสำรวจพื้นที่แปลงหรือไม่?")
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
                            ViewFarmActivity.this.finish();
                        } catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(getApplicationContext(), "Activity finish Error : "+String.valueOf(e) , Toast.LENGTH_LONG).show();
                        }

                    }

                })
                .setNegativeButton("ยกเลิก", null)
                .show();
    }
    //
//    private void ClearCachedPolygon(){
//        cachedFARM_ID_KEY.clear();
//        cachedQOUTA_ID_KEY.clear();
//        cachedPolygon.clear();
//        cachedPolygonHouse.clear();
//        sketchlayer.clearSelection();
//        sketchlayer.removeAll();
//        sketchHouseLayer.clearSelection();
//        sketchHouseLayer.removeAll();
//        Symbollayer.clearSelection();
//        Symbollayer.removeAll();
//        markpointlayer.clearSelection();
//        markpointlayer.removeAll();
//        GPSlayer.clearSelection();
//        GPSlayer.removeAll();
//        map.destroyDrawingCache();
//    }
    private void ClearCachedPolygon(){
        if(cachedFARM_ID_KEY != null) {
            cachedFARM_ID_KEY.clear();
        }
        if(cachedQOUTA_ID_KEY != null) {
            cachedQOUTA_ID_KEY.clear();
        }
        if(cachedPolygon != null) {
            cachedPolygon.clear();
        }
        if(cachedPolygonHouse != null) {
            cachedPolygonHouse.clear();
        }
        if(sketchlayer != null) {
            sketchlayer.clearSelection();
            sketchlayer.removeAll();
        }
        if(sketchHouseLayer != null) {
            sketchHouseLayer.clearSelection();
            sketchHouseLayer.removeAll();
        }
        if(Symbollayer != null) {
            Symbollayer.clearSelection();
            Symbollayer.removeAll();
        }
        if(markpointlayer != null) {
            markpointlayer.clearSelection();
            markpointlayer.removeAll();
        }
        if(GPSlayer != null) {
            GPSlayer.clearSelection();
            GPSlayer.removeAll();
        }
        if(map != null) {
            map.destroyDrawingCache();
        }
    }





}


