package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;

import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.internal.view.SupportMenu;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Coor_House_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Coor_Importance_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Edit_Farm_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_EvaCon_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_FarmGeo_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Member_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Rec_Girth_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Visit_DB_OpenHelper;
import com.panelplus.pnpmember.module.ConnectionDetector;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadActivity extends AppCompatActivity {
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    private static final String DATABASE_COOR_HOUSE_DB = "COOR_HOUSE_DB";
    private static final String DATABASE_COOR_IMPORTANCE_DB = "COOR_IMPORTANCE_DB";
    private static final String DATABASE_EDIT_FARM_DB = "EDITFARM_DB";
    private static final String DATABASE_EVACON_DB = "EVACON_DB";
    private static final String DATABASE_FARMGEO_DB = "FARMGEO_DB";
    private static final String DATABASE_REC_GIRTH_DB = "REC_GIRTH_DB";
    private static final String DATABASE_REGIS_DB = "REGIS_DB";
    private static final String DATABASE_VISIT_DB = "VISIT_DB";
    private Dialog Cooldown_dialog;
    @SuppressLint({"SdCardPath"})
    String DATABASE_FILE_PATH = "/sdcard/MapDB/";
    private String DBFile;
    private String DBFile2;
    private String DBFile3;
    private String DBFile4;
    private String DBFile5;
    private String DBFile6;
    private String DBFile7;
    private String DBFile8;
    String DataSplit = null;
    List<String> ImageList;
    File Path;
    private String UserEmpID;
    /* access modifiers changed from: private */
    public String UserZone;
    private String Username;
    private Button btnDelete;
    private Button btnExitUpload;

    /* renamed from: cd */
    ConnectionDetector cd;
    CountDownTimer cdt;
    /* access modifiers changed from: private */
    public Handler handler = new Handler();
    Boolean isInternetPresent = false;
    /* access modifiers changed from: private */
    public ListView lstView;
    ProgressBar pbTimer;
    private String resultServer;
    String[] tmp = null;
    String[] tmp2 = null;
    String[] tmp3 = null;
    String[] tmp4 = null;

    //    List<String> tmp4 = new ArrayList<>();
    String[] tmp5 = null;
    String[] tmp6 = null;
    String[] tmp7 = null;
    String[] tmp8 = null;
//    File Path = null;

//    private String DBFile;
//    private String DBFile2;
//    private String DBFile3;
//    private String DBFile4;
//    private String DBFile5;
//    private String DBFile6;
//    private String DBFile7;
//    private String DBFile8;

    SQLiteDatabase db;
    SQLiteDatabase db2;
    SQLiteDatabase db3;
    SQLiteDatabase db4;
    SQLiteDatabase db5;
    SQLiteDatabase db6;
    SQLiteDatabase db7;
    SQLiteDatabase db8;
    TextView tvTimer;
    List<String> uploadName = new ArrayList();
    public List<NameValuePair> uploadparams;
    public List<NameValuePair> uploadparams2;
    public List<NameValuePair> uploadparams3;
    public List<NameValuePair> uploadparams4;
    public List<NameValuePair> uploadparams5;
    public List<NameValuePair> uploadparams6;
    public List<NameValuePair> uploadparams7;
    public List<NameValuePair> uploadparams8;
    public List<NameValuePair> uploadparamsFarm;
    public List<NameValuePair> uploadparamsMember;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        this.btnExitUpload = (Button) findViewById(R.id.btnExitUpload);
        this.btnExitUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UploadActivity.this.finish();
            }
        });
        this.UserZone = getUserZone(this);
        this.UserEmpID = getEmpID(this);
        this.Username = getUsername(this);

        initExternalStorage_Member(this);
        initExternalStorage_Edit_Farm(this);
        initExternalStorage_EvaCon(this);
        initExternalStorage_Visit(this);
        initExternalStorage_FarmGeo(this);
        initExternalStorage_Coor_Importance(this);
        initExternalStorage_Coor_House(this);
        initExternalStorage_Rec_Girth(this);

        initData(this);

        this.ImageList = getSD();
        if (this.tmp != null) {
            this.lstView = (ListView) findViewById(R.id.listView1);
            this.lstView.setAdapter(new ImageAdapter(this));
        } else if (this.tmp2 != null) {
            this.lstView = (ListView) findViewById(R.id.listView1);
            this.lstView.setAdapter(new ImageAdapter(this));
        } else if (this.tmp3 != null) {
            this.lstView = (ListView) findViewById(R.id.listView1);
            this.lstView.setAdapter(new ImageAdapter(this));
        } else if (this.tmp4 != null) {
            this.lstView = (ListView) findViewById(R.id.listView1);
            this.lstView.setAdapter(new ImageAdapter(this));
        } else if (this.tmp5 != null) {
            this.lstView = (ListView) findViewById(R.id.listView1);
            this.lstView.setAdapter(new ImageAdapter(this));
        } else if (this.tmp6 != null) {
            this.lstView = (ListView) findViewById(R.id.listView1);
            this.lstView.setAdapter(new ImageAdapter(this));
        } else if (this.tmp7 != null) {
            this.lstView = (ListView) findViewById(R.id.listView1);
            this.lstView.setAdapter(new ImageAdapter(this));
        } else if (this.tmp8 != null) {
            this.lstView = (ListView) findViewById(R.id.listView1);
            this.lstView.setAdapter(new ImageAdapter(this));
        }
    }

    private void alert() {
        Toast.makeText(getApplicationContext(), "ไม่สามารถเข้าถึงข้อมูลได้", Toast.LENGTH_SHORT).show();
    }

    private String getUserZone(Context context) {
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context, DBFile);
        if (!obj.databaseFileExists()) {
            return "NULL";
        }
        this.UserZone = obj.LoginData("1", obj.getReadableDatabase());
        obj.close();
        return this.UserZone;
    }

    private String getEmpID(Context context) {
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context, DBFile);
        if (!obj.databaseFileExists()) {
            return "NULL";
        }
        this.db = obj.getReadableDatabase();
        this.UserEmpID = obj.GetEmpID("1", this.db);
        obj.close();
        return this.UserEmpID;
    }

    private String getUsername(Context context) {
        String DBFile = DATABASE_CENTER_DB + ".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context, DBFile);
        if (!obj.databaseFileExists()) {
            return "NULL";
        }
        this.db = obj.getReadableDatabase();
        this.Username = obj.GetEmpName("1", this.db);
        obj.close();
        return this.Username;
    }

    private void initExternalStorage_Member(Context context) {
        String DBFile = DATABASE_REGIS_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Member_DB_OpenHelper obj = new ExternalStorage_Member_DB_OpenHelper(context, DBFile);
        if (obj.databaseFileExists()) {
            db = obj.getWritableDatabase(); // เปลี่ยนเป็น getWritableDatabase() เพื่อให้สามารถเขียนฐานข้อมูลได้ด้วย
            try {
                tmp = obj.SelectData("1", db);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                obj.close();
            }
        } else {
            Toast.makeText(context, "ไม่สามารถใช้งานฐานข้อมูลได้", Toast.LENGTH_SHORT).show();
        }
    }


    private void initExternalStorage_Edit_Farm(Context context) {
        String DBFile2 = DATABASE_EDIT_FARM_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Edit_Farm_DB_OpenHelper obj2 = new ExternalStorage_Edit_Farm_DB_OpenHelper(context, DBFile2);
        if (obj2.databaseFileExists()) {
            db2 = obj2.getReadableDatabase();
            tmp2 = obj2.SelectData("1", db2);
            obj2.close();
        }
    }

    private void initExternalStorage_EvaCon(Context context) {
        String DBFile3 = DATABASE_EVACON_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_EvaCon_DB_OpenHelper obj3 = new ExternalStorage_EvaCon_DB_OpenHelper(context, DBFile3);
        if (obj3.databaseFileExists()) {
            db3 = obj3.getReadableDatabase();
            //tmp3 = obj3.SelectData("1",db3);

            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0:
                        tmp3 = obj3.SelectDataContractor("1", db3);
                        break;
                    case 1:
                        tmp3 = obj3.SelectDataConBefCut("1", db3);
                        break;
                    case 2:
                        tmp3 = obj3.SelectDataConCutting("1", db3);
                        break;
                    case 3:
                        tmp3 = obj3.SelectDataConAftCut("1", db3);
                        break;
                }
                if (tmp3 != null) {
                    break;
                }
            }
            obj3.close();
        }
    }


//    private void initExternalStorage_Visit(Context context) {
//        String DBFile4 = DATABASE_VISIT_DB + "-" + UserZone + ".sqlite";
//        ExternalStorage_Visit_DB_OpenHelper obj4 = new ExternalStorage_Visit_DB_OpenHelper(context, DBFile4);
//        if (obj4.databaseFileExists()) {
//            db4 = obj4.getWritableDatabase(); // เปลี่ยนเป็น getWritableDatabase() เพื่อให้สามารถเขียนฐานข้อมูลได้ด้วย
//            try {
//                for (int i = 0; i < 3; i++) {
//                    switch (i) {
//                        case 0:
//                            tmp4 = obj4.SelectDataVisitMember("1", db4);
//                            break;
//                        case 1:
//
//                            String query = "SELECT COUNT(*) FROM " + "visit_farm_data";
//                            Cursor cursor = db4.rawQuery(query, null);
//
//                            int rowCount = 0;
//                            if (cursor != null) {
//                                if (cursor.moveToFirst()) {
//                                    rowCount = cursor.getInt(0);
//                                }
//                                cursor.close();
//                            }
//
//                            Log.e("Check","RowCout : "+ rowCount);
//
//                            for (int row = 1; row <= rowCount; row++) {
//                                String rowNumber = String.valueOf(row);
//                                tmp4 = obj4.SelectDataVisitFarm(rowNumber, db4);
//
//                                Log.e("tmp4", "Value: " + tmp4.toString().trim());
//                            }
//                            break;
//                        //case 2: tmp4 = obj4.SelectDataRecCircum("1", db4); break;
//                    }
//
//                    if (tmp4 != null) {
//                        Log.e("Check","tmp4 : " + tmp4.length);
//                        break;
//                    }
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            } finally {
//                obj4.close();
//            }
//        }
//    }

    private void initExternalStorage_Visit(Context context) {
        String DBFile4 = DATABASE_VISIT_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Visit_DB_OpenHelper obj4 = new ExternalStorage_Visit_DB_OpenHelper(context, DBFile4);
        if (obj4.databaseFileExists()) {
            db4 = obj4.getWritableDatabase(); // เปลี่ยนเป็น getWritableDatabase() เพื่อให้สามารถเขียนฐานข้อมูลได้ด้วย
            List<String> tmp4List = new ArrayList<>();
            try {
                for (int i = 0; i < 3; i++) {
                    switch (i) {
                        case 0:
                            String[] tmpData1 = obj4.SelectDataVisitMember("1", db4);
                            if (tmpData1 != null) {
                                tmp4List.addAll(Arrays.asList(tmpData1));
                            }
                            break;
                        case 1:
                            String query = "SELECT COUNT(*) FROM visit_farm_data";
                            Cursor cursor = db4.rawQuery(query, null);

                            int rowCount = 0;
                            if (cursor != null) {
                                if (cursor.moveToFirst()) {
                                    rowCount = cursor.getInt(0);
                                }
                                cursor.close();
                            }

                            Log.e("Check", "RowCount: " + rowCount);

                            for (int row = 1; row <= rowCount; row++) {
                                String rowNumber = String.valueOf(row);
                                String[] tmpData2 = obj4.SelectDataVisitFarm(rowNumber, db4);
                                if (tmpData2 != null) {
                                    tmp4List.addAll(Arrays.asList(tmpData2));
                                }

                                Log.e("tmp4", "Value: " + Arrays.toString(tmpData2));
                            }
                            break;
                        //case 2: String[] tmpData3 = obj4.SelectDataRecCircum("1", db4); break;
                    }

                    if (!tmp4List.isEmpty()) {
                        Log.e("Check", "tmp4: " + tmp4List.size());
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                obj4.close();
            }

// แปลง List<String> เป็น String[] (ถ้าต้องการตัวแปร tmp4 เป็นอาร์เรย์)

             tmp4 = tmp4List.toArray(new String[0]);
            Log.e("Check", "Value tmp4: " + tmp4);
        }
    }

//    private void initExternalStorage_Visit(Context context) {
//        String DBFile4 = DATABASE_VISIT_DB + "-" + UserZone + ".sqlite";
//        ExternalStorage_Visit_DB_OpenHelper obj4 = new ExternalStorage_Visit_DB_OpenHelper(context, DBFile4);
//        if (obj4.databaseFileExists()) {
//            db4 = obj4.getWritableDatabase();
//            try {
//                // สร้างอาเรย์เพื่อเก็บข้อมูลทั้งหมดที่พบ
//                List<String[]> allData = new ArrayList<>();
//
//                for (int i = 0; i < 3; i++) {
//                    switch (i) {
//                        case 0:
//                            tmp4 = obj4.SelectDataVisitMember("1", db4);
//                            break;
//                        case 1:
//                            // เรียกใช้ SelectDataVisitFarm สำหรับหลาย id
//                            String[] ids = {"1", "2", "3"};  // ตัวอย่างเช่น กำหนด id ที่ต้องการให้เรียก
//                            for (String id : ids) {
//                                String[] data = obj4.SelectDataVisitFarm(id, db4);
//                                if (data != null) {
//                                    allData.add(data);
//                                }
//                            }
//                            break;
//                        //case 2: tmp4 = obj4.SelectDataRecCircum("1", db4); break;
//                    }
//
//                    if (tmp4 != null) {
//                        break;
//                    }
//                }
//
//                // ใช้ข้อมูลที่ได้รับในอาเรย์ allData
//                for (String[] data : allData) {
//                    // ทำอย่างอื่นที่ต้องการกับข้อมูล
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            } finally {
//                obj4.close();
//            }
//        }
//    }


    private void initExternalStorage_FarmGeo(Context context) {
        String DBFile5 = DATABASE_FARMGEO_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_FarmGeo_DB_OpenHelper obj5 = new ExternalStorage_FarmGeo_DB_OpenHelper(context, DBFile5);
        if (obj5.databaseFileExists()) {
            db5 = obj5.getReadableDatabase();
            tmp5 = obj5.SelectData("1", db5);
            obj5.close();
        }
    }

    private void initExternalStorage_Rec_Girth(Context context) {

        String DBFile6 = DATABASE_REC_GIRTH_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Rec_Girth_DB_OpenHelper obj6 = new ExternalStorage_Rec_Girth_DB_OpenHelper(context, DBFile6);
        if (obj6.databaseFileExists()) {
            db6 = obj6.getReadableDatabase();
            tmp6 = obj6.SelectDataRecCircum("1", db6);
            obj6.close();
        }
    }

    private void initExternalStorage_Coor_House(Context context) {
        String DBFile7 = DATABASE_COOR_HOUSE_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Coor_House_OpenHelper obj7 = new ExternalStorage_Coor_House_OpenHelper(context, DBFile7);
        if (obj7.databaseFileExists()) {
            db7 = obj7.getReadableDatabase();
            tmp7 = obj7.SelectData("1", db7);
            obj7.close();
        }
    }

    private void initExternalStorage_Coor_Importance(Context context) {
        String DBFile8 = DATABASE_COOR_IMPORTANCE_DB + "-" + UserZone + ".sqlite";
        ExternalStorage_Coor_Importance_OpenHelper obj8 = new ExternalStorage_Coor_Importance_OpenHelper(context, DBFile8);
        if (obj8.databaseFileExists()) {
            db8 = obj8.getReadableDatabase();
            tmp8 = obj8.SelectData("1", db8);
            obj8.close();
        }
    }

    private void initData(Context context) {

        File folder = new File(getFilesDir() + "/MapDB/");
        boolean success = true;
        if (!folder.exists()) {
            Toast.makeText(context.getApplicationContext(), "Not Create File", Toast.LENGTH_SHORT).show();
        }
        if (success) {
            Toast.makeText(context.getApplicationContext(), "Success Create File", Toast.LENGTH_SHORT).show();

        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("ที่เก็บข้อมูลไม่สำเร็จ  " + folder.getAbsolutePath() + "ที่เก็บข้อมูลไม่สำเร็จ  " + folder.getPath());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        this.Path = folder;
    }

    private List<String> getSD() {
        List<String> it = new ArrayList<String>();

        if (tmp != null) {

            File folder = new File(getFilesDir() + "/MapDB/");
            String DBFile = "REGIS_DB-" + this.UserZone + ".sqlite";
            File directUpload1 = new File(folder, DBFile);
            Log.e("Check", "directUpload1 tmp : " + directUpload1);

            if (directUpload1.exists()) {
                it.add(directUpload1.getPath());
            }
        }
        if (tmp2 != null) {

            File folder = new File(getFilesDir() + "/MapDB/");
            String DBFile = DATABASE_EDIT_FARM_DB + "-" + this.UserZone + ".sqlite";
            File directUpload1 = new File(folder, DBFile);
            Log.e("Check", "directUpload1 tmp2 : " + directUpload1);
            if (directUpload1.exists()) {
                it.add(directUpload1.getPath());
            }


        }

        if (tmp3 != null) {

            File folder = new File(getFilesDir() + "/MapDB/");
            String DBFile = DATABASE_EVACON_DB + "-" + this.UserZone + ".sqlite";
            File directUpload1 = new File(folder, DBFile);
            Log.e("Check", "directUpload1 tmp3 : " + directUpload1);
            if (directUpload1.exists()) {
                it.add(directUpload1.getPath());
            }

        }

        if (tmp4 != null) {

            File folder = new File(getFilesDir() + "/MapDB/");
            String DBFile = DATABASE_VISIT_DB + "-" + this.UserZone + ".sqlite";
            File directUpload1 = new File(folder, DBFile);
            Log.e("Check", "directUpload1 size " + directUpload1.length());
            Log.e("Check", "directUpload1 tmp4 : " + directUpload1);
            if (directUpload1.exists()) {
                it.add(directUpload1.getPath());
            }
        }

        if (tmp5 != null) {
            File folder = new File(getFilesDir() + "/MapDB/");
            String DBFile = DATABASE_FARMGEO_DB + "-" + this.UserZone + ".sqlite";
            File directUpload1 = new File(folder, DBFile);
            Log.e("Check", "directUpload1 tmp5 : " + directUpload1);
            if (directUpload1.exists()) {
                it.add(directUpload1.getPath());
            }
        }

        if (tmp6 != null) {
            File folder = new File(getFilesDir() + "/MapDB/");
            String DBFile = DATABASE_REC_GIRTH_DB + "-" + this.UserZone + ".sqlite";
            File directUpload1 = new File(folder, DBFile);
            Log.e("Check", "directUpload1 tmp6 : " + directUpload1);
            if (directUpload1.exists()) {
                it.add(directUpload1.getPath());
            }
        }

        if (tmp7 != null) {

            File folder = new File(getFilesDir() + "/MapDB/");
            String DBFile = DATABASE_COOR_HOUSE_DB + "-" + this.UserZone + ".sqlite";
            File directUpload1 = new File(folder, DBFile);
            Log.e("Check", "directUpload1 tmp7 : " + directUpload1);
            if (directUpload1.exists()) {
                it.add(directUpload1.getPath());
            }
        }

        if (tmp8 != null) {
            File folder = new File(getFilesDir() + "/MapDB/");
            String DBFile = DATABASE_COOR_IMPORTANCE_DB + "-" + this.UserZone + ".sqlite";
            File directUpload1 = new File(folder, DBFile);
            Log.e("Check", "directUpload1  tmp8 : " + directUpload1);
            if (directUpload1.exists()) {
                it.add(directUpload1.getPath());
            }
        }
        Log.e("Check", "it : " + it.toString());
        return it;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c) {
            this.context = c;
        }

        public int getCount() {
            return ImageList.size();
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_column, (ViewGroup) null);
            }
            TextView txtName = (TextView) convertView.findViewById(R.id.ColImgName);
            final String strPath = ImageList.get(position).toString();
            String fileName = strPath.substring(strPath.lastIndexOf(47) + 1, strPath.length());
            Log.e("Check", "String fileName : " + fileName);
            long length = new File(strPath).length();
            txtName.setPadding(3, 0, 0, 0);
            String File1 = "REGIS_DB-" + UserZone + ".sqlite";
            String File2 = "EDITFARM_DB-" + UserZone + ".sqlite";
            String File3 = "EVACON_DB-" + UserZone + ".sqlite";
            String File4 = "VISIT_DB-" + UserZone + ".sqlite";
            String File5 = "FARMGEO_DB-" + UserZone + ".sqlite";
            String File6 = "REC_GIRTH_DB-" + UserZone + ".sqlite";
            String File7 = "COOR_HOUSE_DB-" + UserZone + ".sqlite";
            String File8 = "COOR_IMPORTANCE_DB-" + UserZone + ".sqlite";
            String FileNameResult = "";
            if (fileName.toString().trim().equals(File1)) {
                FileNameResult = "สมัคเข้าร่วมโครงการ";
                uploadName.add(FileNameResult);
            } else if (fileName.toString().trim().equals(File2)) {
                FileNameResult = "แก้ไขพิกัดแปลงสมาชิก";
                uploadName.add(FileNameResult);
            } else if (fileName.toString().trim().equals(File3)) {
                FileNameResult = "รายการประเมินการทำไม้";
                uploadName.add(FileNameResult);
            } else if (fileName.toString().trim().equals(File4)) {
                FileNameResult = "รายการตรวจเยี่ยมสมาชิก";
                uploadName.add(FileNameResult);
            } else if (fileName.toString().trim().equals(File5)) {
                FileNameResult = "รังวัดแปลงสมาชิก";
                uploadName.add(FileNameResult);
            } else if (fileName.toString().trim().equals(File6)) {
                FileNameResult = "วัดลำต้น";
                uploadName.add(FileNameResult);
            } else if (fileName.toString().trim().equals(File7)) {
                FileNameResult = "เก็บพิกัดบ้านสมาชิก";
                uploadName.add(FileNameResult);
            } else if (fileName.toString().trim().equals(File8)) {
                FileNameResult = "เก็บพิกัดจุดสำคัญ";
                uploadName.add(FileNameResult);
            }
            txtName.setText(FileNameResult + " (" + (length / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) + " KB.)");
            final TextView txtStatus = (TextView) convertView.findViewById(R.id.ColStatus);
            txtStatus.setPadding(3, 0, 0, 0);
            txtStatus.setText("...");
            ProgressBar progress = (ProgressBar) convertView.findViewById(R.id.progressBar);
            progress.setVisibility(View.GONE);
            progress.setPadding(0, 0, 0, 0);
            final Button btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
            final Button btnUpload = (Button) convertView.findViewById(R.id.btnUpload);
            btnUpload.setTextColor(-16777216);
            final int i = position;
            btnUpload.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cd = new ConnectionDetector(context);
                    isInternetPresent = Boolean.valueOf(cd.isConnectingToInternet());
                    if (isInternetPresent.booleanValue()) {
                        beforeUpload();
                    } else {
                        Toast.makeText(getApplicationContext(), "กรุณาเชื่อต่ออินเตอร์ก่อนการอัพโหลด", Toast.LENGTH_LONG).show();
                    }
                }

                public void beforeUpload() {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                    builder.setMessage("ต้องการจะอัพโหลดข้อมูลหรือไม่ ?");
                    builder.setCancelable(true);

                    builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.e("Check", "position onClick : " + position);
                            startUpload(position);
                            btnUpload.setEnabled(false);
                            btnUpload.setTextColor(Color.GRAY);

                            btnDelete.setEnabled(false);
                            btnDelete.setTextColor(Color.GRAY);
                        }
                    });
                    builder.setNegativeButton((CharSequence) "ยกเลิก", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.create().show();
                }
            });
            btnDelete.setTextColor(-16777216);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    beforeDelete();
                }

                public void beforeDelete() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                    builder.setMessage((CharSequence) "ต้องการจะลบข้อมูลหรือไม่ ?");
                    builder.setCancelable(true);
                    builder.setPositiveButton((CharSequence) "ใช่", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Boolean.valueOf(new File(strPath).delete()).equals(true)) {
                                btnDelete.setEnabled(false);
                                btnDelete.setTextColor(-7829368);
                                txtStatus.setText("ไฟล์ถูกลบแล้ว");
                                btnUpload.setEnabled(false);
                                btnUpload.setTextColor(-7829368);
                            }
                        }
                    });
                    builder.setNegativeButton((CharSequence) "ยกเลิก", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.create().show();
                }
            });
            return convertView;
        }
    }

//    public void startUpload(final int position) {
//        new Thread(new Runnable() {
//            public void run() {
//                UploadActivity.this.handler.post(new Runnable() {
//                    public void run() {
//                        View v = UploadActivity.this.lstView.getChildAt(position - lstView.getFirstVisiblePosition());
//                        ((ProgressBar) v.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
//                        ((TextView) v.findViewById(R.id.ColStatus)).setText("Uploading..");
//                        new UploadFileAsync().execute(new String[]{String.valueOf(position)});
//                    }
//                });
//            }
//        }).start();
//    }

    public void startUpload(int position) {

        Runnable runnable = new Runnable() {

            public void run() {


                handler.post(new Runnable() {
                    public void run() {

                        View v = lstView.getChildAt(position - lstView.getFirstVisiblePosition());

                        // Show ProgressBar
                        ProgressBar progress = (ProgressBar) v.findViewById(R.id.progressBar);
                        progress.setVisibility(View.VISIBLE);

                        // Status
                        TextView status = (TextView) v.findViewById(R.id.ColStatus);
                        status.setText("Uploading..");

                        new UploadFileAsync().execute(String.valueOf(position));
                    }
                });

            }
        };
        new Thread(runnable).start();
    }


    public class UploadFileAsync extends AsyncTask<String, Integer, Void> {
        private ProgressDialog dialog;
        int position;
        String resServer;

        public UploadFileAsync() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.dialog = new ProgressDialog(UploadActivity.this);
            this.dialog.setMessage("Uploading...");
            this.dialog.setProgressStyle(1);
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setCancelable(false);
            this.dialog.setProgress(0);
            this.dialog.show();
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(String... params) {
            this.position = Integer.parseInt(params[0]);
            String resMessage = "";
            String strSDPath = ImageList.get(this.position).toString();
            String strUrlServer = "http://103.80.129.192/fsc/app/UploadFile.php?UserZone=" + UserZone;
            Log.d("URL", strUrlServer);
            Log.d("strSDPath", strSDPath);
            try {
                if (!new File(strSDPath).exists()) {
                    this.resServer = "{\"StatusID\":\"0\",\"Error\":\"Please check path on SD Card\"}";
                    return null;
                }
                FileInputStream fileInputStream = new FileInputStream(new File(strSDPath));
                HttpURLConnection conn = (HttpURLConnection) new URL(strUrlServer).openConnection();
                conn.setConnectTimeout(3600000);
                conn.setReadTimeout(3600000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod(HttpPost.METHOD_NAME);
                conn.setRequestProperty("Connection", HTTP.CONN_KEEP_ALIVE);
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + "*****");
                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes("--" + "*****" + "\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + strSDPath + "\"" + "\r\n");
                outputStream.writeBytes("\r\n");

                int bufferSize = Math.min(fileInputStream.available(), 1048576);
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                long sentBytes = 0;

                while ((bytesRead = fileInputStream.read(buffer, 0, bufferSize)) > 0) {
                    sentBytes += bytesRead;
                    outputStream.write(buffer, 0, bytesRead);
                    int bytesAvailable = fileInputStream.available();
                    publishProgress((int) ((100 * sentBytes) / 1048576));
                    bufferSize = Math.min(bytesAvailable, 1048576);
                    Log.d("bytesAvailable", String.valueOf(bytesAvailable));
                    Log.d("sentBytes", String.valueOf(sentBytes));
                    Log.d("maxBufferSize", String.valueOf(1048576));
                }

                outputStream.writeBytes("\r\n");
                outputStream.writeBytes("--" + "*****" + "--" + "\r\n");
                outputStream.flush();
                outputStream.close();

                if (conn.getResponseCode() == 200) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int read;
                    byte[] resultBuffer = new byte[1024];

                    while ((read = is.read(resultBuffer)) != -1) {
                        bos.write(resultBuffer, 0, read);
                    }

                    byte[] result = bos.toByteArray();
                    bos.close();
                    resMessage = new String(result);
                }

                fileInputStream.close();
                conn.disconnect();

                this.resServer = resMessage.toString();


//                FileInputStream fileInputStream = new FileInputStream(new File(strSDPath));
//                HttpURLConnection conn = (HttpURLConnection) new URL(strUrlServer).openConnection();
//                conn.setConnectTimeout(3600000);
//                conn.setReadTimeout(3600000);
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setUseCaches(false);
//                conn.setRequestMethod(HttpPost.METHOD_NAME);
//                conn.setRequestProperty("Connection", HTTP.CONN_KEEP_ALIVE);
//                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + "*****");
//                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
//                outputStream.writeBytes("--" + "*****" + "\r\n");
//                outputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + strSDPath + "\"" + "\r\n");
//                outputStream.writeBytes("\r\n");
//                int bufferSize = Math.min(fileInputStream.available(), 1048576);
//                byte[] buffer = new byte[bufferSize];
//                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                long sentBytes = 0;
//                while (bytesRead > 0) {
//                    sentBytes += (long) bytesRead;
//                    outputStream.write(buffer, 0, bufferSize);
//                    int bytesAvailable = fileInputStream.available();
//                    publishProgress(new Integer[]{Integer.valueOf((int) ((100 * sentBytes) / ((long) 1048576)))});
//                    bufferSize = Math.min(bytesAvailable, 1048576);
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                    Log.d("bytesAvailable", String.valueOf(bytesAvailable));
//                    Log.d("sentBytes", String.valueOf(sentBytes));
//                    Log.d("maxBufferSize", String.valueOf(1048576));
//                }
//                outputStream.writeBytes("\r\n");
//                outputStream.writeBytes("--" + "*****" + "--" + "\r\n");
//                if (conn.getResponseCode() == 200) {
//                    InputStream is = conn.getInputStream();
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    while (true) {
//                        int read = is.read();
//                        if (read == -1) {
//                            break;
//                        }
//                        bos.write(read);
//                    }
//                    byte[] result = bos.toByteArray();
//                    bos.close();
//                    resMessage = new String(result);
//                }
//                fileInputStream.close();
//                outputStream.flush();
//                outputStream.close();
//                this.resServer = resMessage.toString();
                Log.e("Check", "Check resServer : " + resServer);
                return null;
            } catch (Exception e) {
                Log.e("Check", "Check catch : " + e.getMessage());
                return null;
            }
        }

        /* access modifiers changed from: protected */
        @Override
        public void onProgressUpdate(Integer... progress) {
            this.dialog.setProgress(progress[0].intValue());
        }

        /* access modifiers changed from: protected */
        @Override
        public void onPostExecute(Void unused) {
            Log.e("Check", "this.resServer : " + this.resServer);
            Log.e("Check", "ImageList.get :  " + ImageList.get(this.position).toString());
            statusWhenFinish(this.position, this.resServer, ImageList.get(this.position).toString());
            try {
                this.dialog.dismiss();
            } catch (Exception e) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void statusWhenFinish(int position, String resServer, String DelPath) {
        Log.e("Check", "position : " + position);
        Log.e("Check", "resServer : " + resServer);
        Log.e("Check", "DelPath : " + DelPath);
        View v = this.lstView.getChildAt(position - lstView.getFirstVisiblePosition());
        Log.e("Check", "View v : " + v.toString());
        ((ProgressBar) v.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        TextView status = (TextView) v.findViewById(R.id.ColStatus);
        String strStatusID = "0";
        String strError = "Unknow Status!";
        try {
            JSONObject c = new JSONObject(resServer);
            strStatusID = c.getString("StatusID");
            strError = c.getString("Error");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (strStatusID.equals("0")) {

            status.setText("ERROR (" + strError + ")");
            status.setTextColor(Color.RED);
            Button btnUpload = (Button) v.findViewById(R.id.btnUpload);
            btnUpload.setText("กรุณาอัพโหลดข้อมูลอีกครั้ง");
            btnUpload.setTextColor(Color.RED);
            btnUpload.setEnabled(true);
            btnDelete.setEnabled(true);
            btnDelete.setTextColor(Color.BLACK);

            Toast toast1 = Toast.makeText(getApplicationContext(), "Upload status = " + strError, Toast.LENGTH_SHORT);
            toast1.show();
            //dialog.dismiss();

        }
        Toast.makeText(getApplicationContext(), "Upload status = " + strError, Toast.LENGTH_SHORT).show();
        status.setText("อัพโหลดเสร็จสิ้น");
        status.setTextColor(-16777216);
        DeleteFileUploadAsync(DelPath);
    }

    //    public String CountRow(String countPath){
//
//        SQLiteDatabase db;
//        File file1 = new File(countPath);
//        String RowResault="";
//
//        File DelPathCaneRegis = new File(Path+ DATABASE_REGIS_DB +".sqlite");
//
//        try {
//            if(file1.equals(DelPathCaneRegis)){
//
//                String DBFile = DATABASE_REGIS_DB +".sqlite";
//
//                ExternalStorage_Regis_DB_OpenHelper obj= new ExternalStorage_Regis_DB_OpenHelper(DBFile);
//                if(obj.databaseFileExists())
//                {
//                    db=obj.getReadableDatabase();
//                    RowResault = obj.CountData(db);
//                    obj.close();
//                }
//
//            }
//
//
//
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//
//
//        return RowResault;
//
//    }
    public void DeleteFileUploadAsync(String... params) {
        File file = new File(params[0]);
        Log.e("Check", "Path file: " + file.getAbsolutePath());

        new File(params[0] + "-journal");
        File DelPathMemRegis = new File(Path + "/" + DATABASE_REGIS_DB + "-" + this.UserZone + ".sqlite");
        Log.e("Check", "Path : " + DelPathMemRegis.getAbsolutePath());

        File DelPathEditFarmRegis = new File(Path + "/" + DATABASE_EDIT_FARM_DB + "-" + this.UserZone + ".sqlite");
        Log.e("Check", "Path : " + DelPathEditFarmRegis.getAbsolutePath());

        File DelPathEvaCon = new File(Path + "/" + DATABASE_EVACON_DB + "-" + this.UserZone + ".sqlite");
        Log.e("Check", "Path : " + DelPathEvaCon.getAbsolutePath());

        File DelPathVisit = new File(Path + "/" + DATABASE_VISIT_DB + "-" + this.UserZone + ".sqlite");
        Log.e("Check", "Path : " + DelPathVisit.getAbsolutePath());

        File DelPathFarmGeo = new File(Path + "/" + DATABASE_FARMGEO_DB + "-" + this.UserZone + ".sqlite");
        Log.e("Check", "Path : " + DelPathFarmGeo.getAbsolutePath());

        File DelPathRecGirth = new File(Path + "/" + DATABASE_REC_GIRTH_DB + "-" + this.UserZone + ".sqlite");
        Log.e("Check", "Path : " + DelPathRecGirth.getAbsolutePath());

        File DelPathCoorHouse = new File(Path + "/" + DATABASE_COOR_HOUSE_DB + "-" + this.UserZone + ".sqlite");
        Log.e("Check", "Path : " + DelPathCoorHouse.getAbsolutePath());

        File DelPathCoorImportance = new File(Path + "/" + DATABASE_COOR_IMPORTANCE_DB + "-" + this.UserZone + ".sqlite");
        Log.e("Check", "Path : " + DelPathCoorImportance.getAbsolutePath());

        try {
            if (file.equals(DelPathMemRegis)) {
                String url = "http://103.80.129.192/fsc/app/uploadDB/updateMemberRegisDB.php?UserZone=" + UserZone + "&Emp_ID=" + this.UserEmpID;
                Log.d("RegisURL", String.valueOf(url));
                this.uploadparams = new ArrayList();
                this.uploadparams.add(new BasicNameValuePair("UserZone", UserZone.trim()));

                String resultServer = getHttpPost(url, uploadparams);
                Log.d("Check", "resultServer ; " + resultServer);
                String StatusID = null;
                String nowQoutaID = null;
                String memName = null;
                //String LoginData = resultServer ;

                try {
                    JSONObject c1 = new JSONObject(resultServer);
                    nowQoutaID = c1.getString("nowQoutaID");
                    StatusID = c1.getString("StatusID");
                    memName = c1.getString("memName");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (Objects.equals(StatusID, "1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage((CharSequence) memName + ": เลขโควต้าคือ " + nowQoutaID);
                    builder.setCancelable(true);
                    builder.setPositiveButton((CharSequence) "ตกลง", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UploadActivity.this.finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    this.uploadparamsMember = new ArrayList();
                    this.uploadparamsMember.add(new BasicNameValuePair("qouta_ID", nowQoutaID.trim()));
                    this.uploadparamsMember.add(new BasicNameValuePair("mem_Name", memName.toString().trim()));
                    this.uploadparamsMember.add(new BasicNameValuePair(ExternalStorage_Center_DB_OpenHelper.KEY_Zone_Name, this.Username));
                    String httpPost = getHttpPost("http://app.loca.fun/NotifyMember.php", this.uploadparamsMember);
                    Log.d("urlLineMember", String.valueOf("http://app.loca.fun/NotifyMember.php"));
                    Log.d("paramsMember", String.valueOf(this.uploadparamsMember));
                    file.delete();
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    builder2.setMessage((CharSequence) memName + ": สถานะ " + nowQoutaID);
                    builder2.setCancelable(true);
                    builder2.setPositiveButton((CharSequence) "ตกลง", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UploadActivity.this.finish();
                        }
                    });
                    AlertDialog alertDialog2 = builder2.create();
                    alertDialog2.setCanceledOnTouchOutside(false);
                    alertDialog2.setCancelable(false);
                    alertDialog2.show();
                    if (memName == null) {
                        return;
                    } else {
                        file.delete();
                    }

                }
            }
            if (file.equals(DelPathEditFarmRegis)) {
                this.uploadparams = new ArrayList();
                this.uploadparams.add(new BasicNameValuePair("UserZone", this.UserZone.trim()));
                String resultServer2 = getHttpPost("http://103.80.129.192/fsc/app/uploadDB/updateEditFarmDB.php?UserZone=" + this.UserZone, this.uploadparams);
                Log.d("response", String.valueOf(resultServer2));
                if (!resultServer2.equals((Object) null)) {
                    JSONArray jSONArray = new JSONArray(resultServer2);
                    String[] Farm_ID = new String[jSONArray.length()];
                    String[] Area = new String[jSONArray.length()];
                    String[] Status = new String[jSONArray.length()];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < jSONArray.length(); i++) {
                        JSONObject jsonobject = jSONArray.getJSONObject(i);
                        Farm_ID[i] = jsonobject.getString("Farm_ID");
                        Area[i] = jsonobject.getString("Area");
                        Status[i] = jsonobject.getString("Status");
                        Log.d("jsonObject", String.valueOf(jSONArray.getJSONObject(i)));
                        sb.append("แก้ไขพิกัดแปลงยางพารา เลขแปลง:" + Farm_ID[i] + ", เนื้อที่:" + String.valueOf(Area[i]) + " ไร่, สถานะ:" + Status[i]);
                        sb.append("\n\n");
                        if (!Status[i].equals((Object) null)) {
                            this.uploadparamsFarm = new ArrayList();
                            this.uploadparamsFarm.add(new BasicNameValuePair("Farm_ID", Farm_ID[i].trim()));
                            this.uploadparamsFarm.add(new BasicNameValuePair("Emp_Name", this.Username));
                            this.uploadparamsFarm.add(new BasicNameValuePair(ExternalStorage_Farm_GIS_DB_OpenHelper.KEY_FARM_AREA, String.valueOf(Area[i]) + " (" + Status[i] + ")"));
                            String httpPost2 = getHttpPost("http://app.loca.fun/NotifyEditFarm.php", this.uploadparamsFarm);
                            Log.d("urlLineFarm", String.valueOf("http://app.loca.fun/NotifyEditFarm.php"));
                            Log.d("paramsFarm", String.valueOf(this.uploadparamsFarm));
                        }
                    }
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                    builder3.setMessage((CharSequence) sb.toString());
                    builder3.setCancelable(false);
                    builder3.setPositiveButton((CharSequence) "ตกลง", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UploadActivity.this.finish();
                        }
                    });
                    AlertDialog alertDialog3 = builder3.create();
                    alertDialog3.setCanceledOnTouchOutside(false);
                    alertDialog3.setCancelable(false);
                    alertDialog3.show();
                    file.delete();
                } else {
                    Toast.makeText(getApplicationContext(), "ไม่สามารถเข้าถึงเซิร์ฟเว่อร์ได้กรุณลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                }
            }
            if (file.equals(DelPathEvaCon)) {
                String url2 = "http://103.80.129.192/fsc/app/uploadDB/updateEvaConDB.php?UserZone=" + this.UserZone;
                this.uploadparams = new ArrayList();
                this.uploadparams.add(new BasicNameValuePair("UserZone", this.UserZone.trim()));
                String StatusID2 = null;
                try {
                    StatusID2 = new JSONObject(getHttpPost(url2, this.uploadparams)).getString("StatusID");
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
                if (Objects.equals(StatusID2, "1")) {
                    file.delete();
                }
            }
            if (file.equals(DelPathVisit)) {
                Log.e("Check", "DelPathVisit");
                String url3 = "http://103.80.129.192/fsc/app/uploadDB/updateVisitDB.php?UserZone=" + this.UserZone;
                this.uploadparams3 = new ArrayList();
                this.uploadparams3.add(new BasicNameValuePair("UserZone", UserZone.trim()));

                String resultServer = getHttpPost(url3, uploadparams3);
                Log.d("Check", "resultServer ; " + resultServer);
                String StatusID = null;
                String nowQoutaID = null;
                String memName = null;
                //String LoginData = resultServer ;

                try {
                    JSONObject c1 = new JSONObject(resultServer);
                    nowQoutaID = c1.getString("nowQoutaID");
                    StatusID = c1.getString("StatusID");
                    memName = c1.getString("memName");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if (Objects.equals(StatusID, "1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage((CharSequence) memName + ": เลขโควต้าคือ " + nowQoutaID);
                        builder.setCancelable(true);
                        builder.setPositiveButton((CharSequence) "ตกลง", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UploadActivity.this.finish();
                            }
                        });

                        Log.d("urlLineMember", String.valueOf("http://app.loca.fun/NotifyMember.php"));
                        Log.d("paramsMember", String.valueOf(this.uploadparamsMember));
//                        file.delete();
                    }
                } catch (Exception e4) {
                    // การจัดการข้อผิดพลาดที่เกิดขึ้น
                    Log.e("Check", "การจัดการข้อผิดพลาดที่เกิดขึ้น" + e4.getMessage());
                }
            }
            if (file.equals(DelPathFarmGeo)) {
                this.uploadparams = new ArrayList();
                this.uploadparams.add(new BasicNameValuePair("UserZone", this.UserZone.trim()));
                String resultServer3 = getHttpPost("http://103.80.129.192/fsc/app/uploadDB/updateFarmGeoDB.php?UserZone=" + this.UserZone, this.uploadparams);
                Log.d("response", String.valueOf(resultServer3));
                if (!resultServer3.equals((Object) null)) {
                    JSONArray jSONArray2 = new JSONArray(resultServer3);
                    String[] qouta_ID = new String[jSONArray2.length()];
                    String[] Farm_ID2 = new String[jSONArray2.length()];
                    String[] Area2 = new String[jSONArray2.length()];
                    String[] Status2 = new String[jSONArray2.length()];
                    StringBuilder sb2 = new StringBuilder();
                    for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                        JSONObject jsonobject2 = jSONArray2.getJSONObject(i2);
                        qouta_ID[i2] = jsonobject2.getString("qouta_ID");
                        Farm_ID2[i2] = jsonobject2.getString("Farm_ID");
                        Area2[i2] = jsonobject2.getString("Area");
                        Status2[i2] = jsonobject2.getString("Status");
                        Log.d("jsonObject", String.valueOf(jSONArray2.getJSONObject(i2)));
                        sb2.append("เลขโควต้า: " + qouta_ID[i2] + ", เลขแปลง:" + Farm_ID2[i2] + ", เนื้อที่:" + String.valueOf(Area2[i2]) + " ไร่, สถานะ:" + Status2[i2]);
                        sb2.append("\n\n");
                        if (!Status2[i2].equals((Object) null)) {
                            this.uploadparamsFarm = new ArrayList();
                            this.uploadparamsFarm.add(new BasicNameValuePair("qouta_ID", qouta_ID[i2].trim()));
                            this.uploadparamsFarm.add(new BasicNameValuePair("Farm_ID", Farm_ID2[i2].trim()));
                            this.uploadparamsFarm.add(new BasicNameValuePair("Emp_Name", this.Username));
                            this.uploadparamsFarm.add(new BasicNameValuePair(ExternalStorage_Farm_GIS_DB_OpenHelper.KEY_FARM_AREA, String.valueOf(Area2[i2]) + " (" + Status2[i2] + ")"));
                            String httpPost3 = getHttpPost("http://app.loca.fun/NotifyFarm.php", this.uploadparamsFarm);
                            Log.d("urlLineFarm", String.valueOf("http://app.loca.fun/NotifyFarm.php"));
                            Log.d("paramsFarm", String.valueOf(this.uploadparamsFarm));
                        }
                    }
                    AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
                    builder4.setMessage((CharSequence) sb2.toString());
                    builder4.setCancelable(false);
                    builder4.setPositiveButton((CharSequence) "ตกลง", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UploadActivity.this.finish();
                        }
                    });
                    AlertDialog alertDialog4 = builder4.create();
                    alertDialog4.setCanceledOnTouchOutside(false);
                    alertDialog4.setCancelable(false);
                    alertDialog4.show();
                    file.delete();
                } else {
                    Toast.makeText(getApplicationContext(), "ไม่สามารถเข้าถึงเซิร์ฟเว่อร์ได้กรุณลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                }
            }
            if (file.equals(DelPathRecGirth)) {
                String url4 = "http://103.80.129.192/fsc/app/uploadDB/updateRecGirthDB.php?UserZone=" + this.UserZone;
                this.uploadparams = new ArrayList();
                this.uploadparams.add(new BasicNameValuePair("UserZone", this.UserZone.trim()));
                String StatusID4 = null;
                try {
                    StatusID4 = new JSONObject(getHttpPost(url4, this.uploadparams)).getString("StatusID");
                } catch (JSONException e5) {
                    e5.printStackTrace();
                }
                try {
                    if (StatusID4.equals("1")) {
                        Toast.makeText(getApplicationContext(), "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                        file.delete();
                    } else {
                        Toast.makeText(getApplicationContext(), "ไม่สามารถบันทึกได้", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e6) {
                }
            }
            if (file.equals(DelPathCoorHouse)) {
                String url5 = "http://103.80.129.192/fsc/app/uploadDB/updateGeoHouseDB.php?UserZone=" + this.UserZone;
                this.uploadparams = new ArrayList();
                this.uploadparams.add(new BasicNameValuePair("UserZone", this.UserZone.trim()));
                String StatusID5 = null;
                try {
                    StatusID5 = new JSONObject(getHttpPost(url5, this.uploadparams)).getString("StatusID");
                    Log.d("StatusID", StatusID5);
                } catch (JSONException e7) {
                    e7.printStackTrace();
                }
                try {
                    if (Objects.equals(StatusID5, "1")) {
                        Toast.makeText(getApplicationContext(), "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                        file.delete();
                    } else {
                        Toast.makeText(getApplicationContext(), "ไม่สามารถบันทึกได้", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e8) {
                }
            }
            if (file.equals(DelPathCoorImportance)) {
                String url6 = "http://103.80.129.192/fsc/app/uploadDB/updateGeoImportanceDB.php?UserZone=" + this.UserZone;
                this.uploadparams = new ArrayList();
                this.uploadparams.add(new BasicNameValuePair("UserZone", this.UserZone.trim()));
                String StatusID6 = null;
                try {
                    StatusID6 = new JSONObject(getHttpPost(url6, this.uploadparams)).getString("StatusID");
                    Log.d("StatusID", StatusID6);
                } catch (JSONException e9) {
                    e9.printStackTrace();
                }
                try {
                    if (Objects.equals(StatusID6, "1")) {
                        Toast.makeText(getApplicationContext(), "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                        file.delete();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "ไม่สามารถบันทึกได้", Toast.LENGTH_SHORT).show();
                } catch (Exception e10) {
                }
            }
        } catch (Exception e11) {
        }
    }

    public String getHttpPost(String url, List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        if (Build.VERSION.SDK_INT >= 26) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return str.toString();
    }
}
