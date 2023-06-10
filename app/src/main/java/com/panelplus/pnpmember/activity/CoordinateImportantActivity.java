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
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.util.AndroidRuntimeException;
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
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;
import com.esri.core.runtime.LicenseLevel;
import com.esri.core.runtime.LicenseResult;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper;
import com.panelplus.pnpmember.module.Compass;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class CoordinateImportantActivity extends AppCompatActivity implements View.OnClickListener{

    ArcGISLocalTiledLayer local;
    MapView map = null;
    LocationManager lm = null;
    LocationListener locationListener;
    GraphicsLayer newcanelayer;
    GraphicsLayer gpslayer;
    Point cachedLocation;

    GraphicsLayer markpointlayer;


    private Button btnsave;
    private ToggleButton btnautopan;
    private TextView lbl_gps_accuracy;

    private TextView lbl_gpsX;
    private TextView lbl_gpsY;

    ArrayList<Double> Poly_X = null;
    ArrayList<Double> Poly_Y = null;

    double cachedLocationX = 0;
    double cachedLocationY = 0;
    private double cachedLatitude = 0;
    private double cachedLongitude = 0;

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

    Envelope rExtent;
    private String ABC_Phase = "";
    private static final String DATABASE_FSCGISDATANAME = "FSCGISDATA";

    //get user
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    String nowEmp_ID ="";

    String UserZone = null;
    String DataSplit = null;

    LoadMapAsync loadMapAsync;

    double cachedClickX = 0;
    double cachedClickY = 0;

    String getFID;
    String getFARM_ID_KEY;
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

    @SuppressLint("SdCardPath")
    String DATABASE_FILE_PATH = "/mnt/sdcard/MAPDB";
    SQLiteDatabase mydb = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinate_important);

        InitWidget();

        nowEmp_ID= getEmpID();
        btnsave.setEnabled(false);

        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }


        // Find Basemap in Tablet
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

        // Add Layer Basemap
        map.addLayer(local);

        // License Module
        LicenseResult licenseResult = ArcGISRuntime.setClientId(CLIENT_ID);
        LicenseResult licenseResult2 = ArcGISRuntime.License.setLicense(LICENSE_STRING);
        LicenseLevel licenseLevel = ArcGISRuntime.License.getLicenseLevel();


        // GPS Module
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


        // Rotate Map Module
        try {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            //imageCompass.setImageDrawable(getResources().getDrawable(R.drawable.compass128));
            map.setAllowRotationByPinch(true);
            map.enableWrapAround(true);

            mCompass = new Compass(this, null, map);
            map.addView(mCompass);


        } catch (Exception e) {
            // TODO: handle exception
            Log.d("SensorManager", String.valueOf(e));
        }

        // Set Touch
        myListener = new MyTouchListener(CoordinateImportantActivity.this, map);
        map.setOnTouchListener(myListener);

        // Set Zoom
        myZoomListener = new MyZoomListener();
        map.setOnZoomListener(myZoomListener);

        // Set Pan
        myPanListenner = new MyPanListener();
        map.setOnPanListener(myPanListenner);

        callout = map.getCallout();
        callout.setStyle(R.xml.calloutstyle);


        // add graphics layer to draw on
        sketchlayer = new GraphicsLayer();
        map.addLayer(sketchlayer);

        // add graphics layer to draw on
        gpslayer = new GraphicsLayer();
        map.addLayer(gpslayer);

        // add graphics layer to draw on
        newcanelayer = new GraphicsLayer();
        map.addLayer(newcanelayer);

        // add graphics layer to draw on
        markpointlayer = new GraphicsLayer();
        map.addLayer(markpointlayer);


        Poly_X = new ArrayList<Double>();
        Poly_Y = new ArrayList<Double>();
        cachedFARM_ID_KEY =new ArrayList<String>();
        cachedPolygon =new ArrayList<String>();
    }

    private void InitWidget(){

        map = (MapView) findViewById(R.id.map);
        lbl_gps_accuracy = (TextView) findViewById(R.id.lbl_gps_accuracy);

        btnsave = (Button) findViewById(R.id.btnsave);
        btnautopan = (ToggleButton) findViewById(R.id.btnautopan);


        lbl_gpsX = (TextView) findViewById(R.id.lbl_gpsX);
        lbl_gpsY = (TextView) findViewById(R.id.lbl_gpsY);

        btnautopan.setOnClickListener(this);
        btnsave.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
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

            case R.id.btnsave:

                final AlertDialog.Builder Save_dialog = new AlertDialog.Builder(this);

                Save_dialog.setMessage("คุณต้องการบันทึกพิกัดจุดสำคัญหรือไม่?");
                Save_dialog.setNegativeButton("ยกเลิก", null);

                Save_dialog.setPositiveButton("ใช่", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        int XMinusTen = (int) cachedLocationX - 10;
                        int XPlusTen = (int) cachedLocationX + 10;
                        int XMinusFifteen = (int) cachedLocationX - 15;
                        int XPlusFifteen = (int) cachedLocationX + 15;

                        int YMinusTen = (int) cachedLocationY - 10;
                        int YPlusTen = (int) cachedLocationY + 10;
                        //int YMinusFifteen = (int) cachedLocationY -15;
                        int YPlusFifteen = (int) cachedLocationY +15;


                        PathPolygon = "POLYGON ((";
                        PathPolygon += String.valueOf((int) cachedLocationX) +" " +String.valueOf(YMinusTen)+", ";
                        PathPolygon += String.valueOf(XPlusTen) +" " +String.valueOf((int) cachedLocationY)+", ";
                        PathPolygon += String.valueOf((int) cachedLocationX) +" " +String.valueOf(YPlusTen)+", ";
                        PathPolygon += String.valueOf(XMinusTen) +" " +String.valueOf((int) cachedLocationY);


                        PathPolygon = PathPolygon +"))";
                        //PathPolygon = PathPolygon.replace(", ))", "))");
                        Log.d("polygon",PathPolygon);


                        Intent saveCoHouse = new Intent(CoordinateImportantActivity.this,
                                CoordinateImportantConfirmActivity.class);
                        Bundle b = new Bundle();
                        b.putString("coordinateX", String.valueOf(cachedLocationX));
                        b.putString("coordinateY", String.valueOf(cachedLocationY));
                        b.putString("latitude", String.valueOf(cachedLatitude));
                        b.putString("longitude", String.valueOf(cachedLongitude));
                        b.putString("polygon", PathPolygon);

                        saveCoHouse.putExtras(b);

                        startActivity(saveCoHouse);

                        newcanelayer.removeAll();
                        sketchlayer.removeAll();

                        CoordinateImportantActivity.this.finish();

                        if(lm != null){
                            lm.removeUpdates(locationListener);
                            lm=null;
                        }
                    }
                });
                Save_dialog.show();
        }
    }

    public class LoadMapAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Log.d("Get_Val_strSQL", "LoadMapAsync");


            cachedFARM_ID_KEY.clear();
            cachedPolygon.clear();

            rExtent=new Envelope();
            map.getExtent().queryEnvelope(rExtent);

            double Min_X=rExtent.getXMin();  //ด้านบน
            double Min_Y=rExtent.getYMin();  //ด้านบน
            double Max_X=rExtent.getXMax();  //ด้านล่าง
            double Max_Y=rExtent.getYMax();  //ด้านล่าง

            SQLiteDatabase db;
            String DBFile= DATABASE_FSCGISDATANAME +".sqlite";
            Log.d("Get_Val_strSQL", DBFile);
            // ExternalStorageReadOnlyOpenHelper.open();
            List<ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberDrawPolygons> Poly_MebmerList=null ;


            ExternalStorage_Farm_GIS_DB_OpenHelper obj= new ExternalStorage_Farm_GIS_DB_OpenHelper(getApplicationContext(),DBFile);
            if(obj.databaseFileExists())
            {
                db=obj.getReadableDatabase();
                Poly_MebmerList = obj.SelectDataDrawPolygon(Max_X ,
                        Min_X ,
                        Max_Y ,
                        Min_Y ,db);
                for (ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberDrawPolygons mem : Poly_MebmerList) {

                    cachedFARM_ID_KEY.add(mem.gFARM_ID());
                    cachedPolygon.add(mem.gFARM_GEO());
                    CreatePolygon(mem.gFARM_GEO(),mem.gFARM_STATUS(),
                            mem.gEMP_ID());

                }

                obj.close();

            }



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

    private String getEmpID(){
        SQLiteDatabase db;
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(getApplicationContext(),DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getReadableDatabase();
            nowEmp_ID = obj.GetEmpID("1", db);
            obj.close();

            return nowEmp_ID;
        }
        return "NULL";
    }

    class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

            lDisplayManager = map.getLocationDisplayManager();
            lDisplayManager.start();

            if(location != null){


                location.setLatitude(location.getLatitude());
                location.setLongitude(location.getLongitude());

                lbl_gps_accuracy.setText(String.valueOf(location.getAccuracy()));

                //myMapAndroid.removeDialog(myMapAndroid.DIALOG_LOADING);
                Point mapPoint = new Point(location.getLongitude(), location.getLatitude());
                if (4236 != map.getSpatialReference().getID()) {
                    mapPoint = (Point) GeometryEngine.project(mapPoint, SpatialReference.create(4326),
                            map.getSpatialReference());
                }

                if (mapPoint == null || mapPoint.isEmpty()) {
                    Toast.makeText(CoordinateImportantActivity.this, "Cannot determine current location", Toast.LENGTH_SHORT).show();
                    return;
                }


                cachedLocationX = mapPoint.getX() ;
                cachedLocationY = mapPoint.getY() ;

                cachedLatitude = location.getLatitude();
                cachedLongitude = location.getLongitude();

                // get XY LOCATION
                lbl_gpsX.setText(String.valueOf(Integer.valueOf((int) mapPoint.getX()) ));
                lbl_gpsY.setText(String.valueOf(Integer.valueOf((int) mapPoint.getY()) ));
                btnsave.setEnabled(true);

                Log.d("Get_Val_GPSX", String.valueOf(cachedLocationX));
                Log.d("Get_Val_GPSY", String.valueOf(cachedLocationY));

                //set the cachedLocation of the main activity
                //it is used by the QuerySources and QueryCatchment tasks
                cachedLocation = mapPoint;

                //pan to the location, animation enabled
                //map.centerAt(mapPoint,ckhAoutpan );


                if(btnautopan.isChecked())
                {

                    //map.centerAt(mapPoint,true );
                    //map.zoomToResolution(mapPoint, 6.0);
                    lDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                    map.zoomToScale(mapPoint, 25000);
                    Unit mapUnit = map.getSpatialReference()
                            .getUnit();
                    double zoomWidth = Unit.convertUnits(
                            SEARCH_RADIUS,
                            Unit.create(LinearUnit.Code.MILE_US),
                            mapUnit);


                    Envelope zoomExtent = new Envelope(mapPoint,
                            zoomWidth, zoomWidth);
                    map.setExtent(zoomExtent);


                    if (map.getScale() > 30000){
                        sketchlayer.removeAll();

                    } else {
                        loadMapAsync = new LoadMapAsync();
                        loadMapAsync.execute();
                        btnautopan.setChecked(false);

                    }
                }
                else
                {
                    ckhAoutpan = false;
                }

                if(location.getAccuracy()  > 8 ){
                    //if(location.getAccuracy()  > 10 ){
                    gpslayer.removeAll();
                }else{
                    gpslayer.removeAll();

                    //gpslayer.addGraphic(new Graphic(mapPoint,new SimpleMarkerSymbol(Color.rgb(0, 0, 255),25,STYLE.CIRCLE)));


                    //gpslayer.addGraphic(new Graphic(cachedLocation,
                    //new PictureMarkerSymbol(getResources().getDrawable(R.drawable.gps18))));


                }

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
            if (Map_Scale > 30000){
                sketchlayer.removeAll();

            }

        }

        public void postPointerUp(float arg0, float arg1, float arg2, float arg3) {
            // TODO Auto-generated method stub
            if (Map_Scale > 30000){
                sketchlayer.removeAll();

            }
        }

        public void prePointerMove(float arg0, float arg1, float arg2,
                                   float arg3) {
            // TODO Auto-generated method stub
            if (Map_Scale > 30000){
                sketchlayer.removeAll();

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

                if (Map_Scale < 30000){

                    sketchlayer.removeAll();
                    loadMapAsync = new LoadMapAsync();
                    loadMapAsync.execute();

                }else{
                    sketchlayer.removeAll();

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


            //double Resolution;
            Map_Scale = map.getScale();

            Log.d("MapScale", String.valueOf(Map_Scale));



            if (Map_Scale < 30000){

                //if (callout.isShowing()) {
                //callout.hide();
                //}

                sketchlayer.removeAll();
                loadMapAsync = new LoadMapAsync();
                loadMapAsync.execute();


            }else{


                sketchlayer.removeAll();



            }
        }

        public void preAction(float arg0, float arg1, double arg2) {
            // TODO Auto-generated method stub

            if (Map_Scale > 30000){
                sketchlayer.removeAll();

            }


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



        public boolean onSingleTap(MotionEvent e) {
            //Log.d("PointClick", "onSingleTap");
            double Map_Scale = map.getScale();



            if (Map_Scale < 25000){
                Point PointClick = map.toMapPoint(e.getX(), e.getY());

                cachedClickX = PointClick.getX();
                cachedClickY = PointClick.getY();


                double X1 = cachedLocationX ;
                double Y1 = cachedLocationY ;
                double X2 = cachedClickX ;
                double Y2 = cachedClickY ;

                Vecter = Math.sqrt(Math.pow((X2-X1), 2))+ Math.sqrt( Math.pow((Y2-Y1), 2));

                if(Vecter < 0){
                    Vecter = Vecter*-1;
                }




                if (type.length() > 1 && type.equalsIgnoreCase("POINT")) {

                    markpointlayer.removeAll();



                    Point mapPt = map.toMapPoint(e.getX(), e.getY());

                    markpointlayer.addGraphic(new Graphic(mapPt,new SimpleMarkerSymbol(Color.RED,8, SimpleMarkerSymbol.STYLE.CIRCLE)));


                    double PX = mapPt.getX();
                    double PY = mapPt.getY();


                    try {



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


                                LayoutInflater inflater = getLayoutInflater();
                                ViewGroup calloutContent = (ViewGroup) inflater.inflate(R.layout.location_callout, null);


                                String FARM_ID_KEY = cachedFARM_ID_KEY.get(x);


                                TextView lblqoata_id = (TextView) calloutContent.findViewById(R.id.lblqoata_id);
                                TextView lblfid = (TextView) calloutContent.findViewById(R.id.lblfid);
                                //TextView lblgis_mark = (TextView) calloutContent.findViewById(R.id.lblgis_mark);
                                TextView lblstatus = (TextView) calloutContent.findViewById(R.id.lblstatus);
                                TextView lblcyearold = (TextView) calloutContent.findViewById(R.id.lblcyearold);
                                TextView lblname = (TextView) calloutContent.findViewById(R.id.lblcancle_project);
                                TextView lbltel = (TextView) calloutContent.findViewById(R.id.lblTel);
                                TextView lblarea = (TextView) calloutContent.findViewById(R.id.lblarea);
                                TextView lblvality = (TextView) calloutContent.findViewById(R.id.lblvality);
                                TextView lblssone = (TextView) calloutContent.findViewById(R.id.lblssone);
                                TextView lblcyear = (TextView) calloutContent.findViewById(R.id.lblcyear);

                                Button btncallout_edit = (Button) calloutContent.findViewById(R.id.btncallout_edit);
                                Button btncallout_cancle = (Button) calloutContent.findViewById(R.id.btncallout_cancle);

                                btncallout_edit.setVisibility(View.INVISIBLE);

                                SQLiteDatabase db;
                                String DBFile= DATABASE_FSCGISDATANAME +".sqlite";
                                String tmpData1;
                                List<ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberCalloutDatas> CalloutData=null ;

                                ExternalStorage_Farm_GIS_DB_OpenHelper obj= new ExternalStorage_Farm_GIS_DB_OpenHelper(getApplicationContext(),DBFile);
                                if(obj.databaseFileExists())
                                {
                                    db=obj.getReadableDatabase();
                                    CalloutData = obj.getCaneCallout(FARM_ID_KEY, db);
                                    for (ExternalStorage_Farm_GIS_DB_OpenHelper.sMemberCalloutDatas mem : CalloutData) {
                                        lblqoata_id.setText(mem.gQOUTA_ID().toString());
                                        lblfid.setText(mem.gFARM_ID().toString());
                                        getFID=mem.gFARM_ID().toString();
                                        getFARM_ID_KEY = FARM_ID_KEY;

                                        //lblgis_mark.setText(mem.gGIS_MARK().toString());
                                        //lblstatus.setText(Case_Cane(mem.gCASE_CANE().toString()));
                                        lblstatus.setText(mem.gFARM_STATUS().toString());
                                        lblcyear.setText(mem.gFARM_YEAROFPLANT().toString());
                                        lblname.setText(mem.gMEM_GENDER()+mem.gMEM_NAME()+" "+" "+mem.gMEM_LASTNAME());
                                        lbltel.setText(mem.gMEM_TEL().toString());
                                        lblarea.setText(mem.gFARM_AREA().toString());
                                        lblvality.setText(mem.gFARM_RUBBERBREED().toString());
                                        lblssone.setText(mem.gEMP_NAME().toString());

                                        int yearPlant = Integer.parseInt(mem.gFARM_YEAROFPLANT());
                                        Calendar calendar = Calendar.getInstance();
                                        int year = calendar.get(Calendar.YEAR)+543;

                                        int yearOld = year - yearPlant;
                                        lblcyearold.setText(String.valueOf(yearOld)+" ปี");



                                    }

                                    obj.close();
                                }

                                btncallout_cancle.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        callout.hide();
                                    }
                                });


                                callout.setMaxHeight(1000);
                                callout.setMaxWidth(5000);
                                callout.setCoordinates(mapPt);
                                callout.setContent(calloutContent);
                                callout.refresh();
                                callout.show();



                            } else {
                                //inside = false;
                                //return "outside";
                            }

                        }

                    } catch (Exception e2) {
                        // TODO: handle exception
                    }
                    return true;
                }// หา Polygon


            }
            return false;


        }

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

    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        new AlertDialog.Builder(this)

                .setMessage("ออกจากการเก็บพิกัดจุดสำคัญหรือไม่?")
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
                            CoordinateImportantActivity.this.finish();
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
