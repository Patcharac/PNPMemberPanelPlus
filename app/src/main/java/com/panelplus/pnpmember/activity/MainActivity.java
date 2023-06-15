package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
//import android.support.v4.app.FragmentManager;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.TransactionTooLargeException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Farm_GIS_DB_OpenHelper;
import com.panelplus.pnpmember.fragment.MssBefLogoutFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


public class  MainActivity extends AppCompatActivity implements View.OnClickListener {

    @SuppressLint({"SdCardPath"})
    File Path = null;

    String DataSplit=null;
    @SuppressLint({"SdCardPath"})
    String DATABASE_FILE_PAT = "/MapDB/";
    private Button btnregis;
    private Button btnlogging;
    private Button btnupload;
    private Button btnlogout;
    private Button btnFramRegis;
    private Button btnVisit;
    private Button btnDownload;
    private TextView txtUser;

    ProgressDialog progress;


    String UserZone="";

    String User="";

    public List<NameValuePair> loginparams;

    SQLiteDatabase mydb=null;
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    private static final String DATABASE_TABLE_LOGIN = "user_login_data";

    private static final String DATABASE_FSCGISDATANAME = "FSCGISDATA";

    private static final String DATABASE_REGIS_DB = "REGIS_DB";
    private static final String TABLE_MEMBER_DATA = "user_member_data";
//    @SuppressLint("SdCardPath")
//    String DATABASE_FILE_PATH="MapDB";



    //////////////////////////////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitWidget();

        this.UserZone = getUserZone(this);
        Create_Primary_DB(this);
        CheckLogin(this);

        progress = new ProgressDialog(this);
        progress.setMessage("Wait!!");
        progress.setCancelable(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        try {
            Field f = TransactionTooLargeException.class.getDeclaredField("MAX_SIZE");
            f.setAccessible(true);
            f.set(null, 16 * 1024 * 1024); // 16 MB
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ////////////////////////////////////////////////////////////

    private void InitWidget(){
        btnregis = (Button) findViewById(R.id.register);
        btnFramRegis = (Button) findViewById(R.id.farmRegis);
        btnlogging = (Button) findViewById(R.id.logging);
        btnupload = (Button) findViewById(R.id.upload);
        btnlogout = (Button) findViewById(R.id.logout);
        btnVisit = (Button) findViewById(R.id.visit);
        btnDownload = (Button) findViewById(R.id.download);
        txtUser = (TextView) findViewById(R.id.username);
        btnregis.setOnClickListener(this);
        btnFramRegis.setOnClickListener(this);
        btnlogging.setOnClickListener(this);
        btnupload.setOnClickListener(this);
        btnlogout.setOnClickListener(this);
        btnVisit.setOnClickListener(this);
        btnDownload.setOnClickListener(this);

    }

    private void initData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "ต้องการจะปรับปรุงข้อมูลแผนที่หรือไม่ ?");
        builder.setCancelable(true);
        builder.setPositiveButton((CharSequence) "ใช่", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.downloadSQLite(MainActivity.this);
            }
        });
        builder.setNegativeButton((CharSequence) "ยกเลิก", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

//    private void Create_Primary_DB() {
//        if (new File(this.Path).mkdir()) {
//        }
//        this.mydb = openOrCreateDatabase(this.DATABASE_FILE_PATH + File.separator + DATABASE_REGIS_DB + "-" + this.UserZone + ".sqlite", 0, (SQLiteDatabase.CursorFactory) null);
//        this.mydb.execSQL(" create table if not exists user_member_data  (_id INTEGER PRIMARY KEY AUTOINCREMENT ,   " +
//                "mem_Date TEXT ,   " +
//                "mem_gender TEXT ,   " +
//                "mem_Name TEXT ,   " +
//                "mem_LastName TEXT ,   " +
//                "mem_Tel TEXT ,   " +
//                "mem_DateOfBirth TEXT ,   " +
//                "mem_IDcard TEXT ,   " +
//                "Ad_No TEXT ,   " +
//                "Ad_Mooban TEXT ,   " +
//                "Ad_MoobanName TEXT ,   " +
//                "Ad_Tambol TEXT ,   " +
//                "Ad_Ampher TEXT ,   " +
//                "Ad_Province TEXT ,   " +
//                "Ad_IDpost TEXT ,   " +
//                "mem_Email TEXT ,   " +
//                "mem_MemberFarm TEXT ,   " +
//                "mem_OccuPrime TEXT ,   " +
//                "mem_OccuSecond TEXT ,   " +
//                "mem_Graduate TEXT ,   " +
//                "attach_IDcard BLOB ,   " +
//                "attach_HouseRegis BLOB ,   " +
//                "attach_Authorise BLOB ,   " +
//                "attach_AccountBank BLOB ,   " +
//                "attech_Signature BLOB ,   " +
//                "Acc_Num TEXT ,   " +
//                "Acc_Bank TEXT ,   " +
//                "Acc_Branc TEXT );");
//    }
//private void Create_Primary_DB() {
//    File directory = new File(Environment.getExternalStorageDirectory() + DATABASE_FILE_PATH);
//    if (!directory.exists()) {
//        directory.mkdir();
//    }
//
//    this.mydb = openOrCreateDatabase(Environment.getExternalStorageDirectory() + DATABASE_FILE_PATH + DATABASE_REGIS_DB + "-" + this.UserZone + ".sqlite", Context.MODE_PRIVATE, null);
//    this.mydb.execSQL("CREATE TABLE IF NOT EXISTS user_member_data (_id INTEGER PRIMARY KEY AUTOINCREMENT, mem_Date TEXT, mem_gender TEXT, mem_Name TEXT, mem_LastName TEXT, mem_Tel TEXT, mem_DateOfBirth TEXT, mem_IDcard TEXT, Ad_No TEXT, Ad_Mooban TEXT, Ad_MoobanName TEXT, Ad_Tambol TEXT, Ad_Ampher TEXT, Ad_Province TEXT, Ad_IDpost TEXT, mem_Email TEXT, mem_MemberFarm TEXT, mem_OccuPrime TEXT, mem_OccuSecond TEXT, mem_Graduate TEXT, attach_IDcard BLOB, attach_HouseRegis BLOB, attach_Authorise BLOB, attach_AccountBank BLOB, attech_Signature BLOB, Acc_Num TEXT, Acc_Bank TEXT, Acc_Branc TEXT);");
//}

//    private void Create_Primary_DB() {
//        File folder = new File(getFilesDir() + "/MapDB");
////    File directory = new File(Environment.getExternalStorageDirectory() + DATABASE_FILE_PATH);
//        boolean success = true;
//        if (!folder.exists()) {
//            success = folder.mkdir();
//        }
//        if (success) {
//            this.Path = folder.getAbsolutePath() + File.separator;
//            String dbPath = folder.getAbsolutePath() + File.separator + DATABASE_REGIS_DB + "-" + UserZone + ".sqlite";
//            this.mydb = this.openOrCreateDatabase(dbPath, Context.MODE_PRIVATE, null);
//
//            this.mydb.execSQL("CREATE TABLE IF NOT EXISTS user_member_data  (_id INTEGER PRIMARY KEY AUTOINCREMENT, mem_Date TEXT, mem_gender TEXT, mem_Name TEXT, mem_LastName TEXT, mem_Tel TEXT, mem_DateOfBirth TEXT, mem_IDcard TEXT, Ad_No TEXT, Ad_Mooban TEXT, Ad_MoobanName TEXT, Ad_Tambol TEXT, Ad_Ampher TEXT, Ad_Province TEXT, Ad_IDpost TEXT, mem_Email TEXT, mem_MemberFarm TEXT, mem_OccuPrime TEXT, mem_OccuSecond TEXT, mem_Graduate TEXT, attach_IDcard BLOB, attach_HouseRegis BLOB, attach_Authorise BLOB, attach_AccountBank BLOB, attech_Signature BLOB, Acc_Num TEXT, Acc_Bank TEXT, Acc_Branc TEXT);");
//
//            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setMessage("MainMene " + folder.getName() + "MainMene  " + folder.getPath());
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        } else {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setMessage("ที่เก็บข้อมูลไม่สำเร็จ  " + folder.getAbsolutePath() + "ที่เก็บข้อมูลไม่สำเร็จ  " + folder.getPath());
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        }
//    }

    private void Create_Primary_DB(Context context) {

        if (context != null) {
            String folderName = "MapDB"; // folder name
            String folder = context.getApplicationContext().getFilesDir() + "/" + folderName; // folder path
            File appDbDir = new File(folder); // create folder
            if (!appDbDir.exists()) {
                appDbDir.mkdirs(); // create folder if it doesn't exist
            }
            this.Path = new File(folder);
        } else {
            // handle null context case
        }

        this.mydb = context.openOrCreateDatabase(DATABASE_REGIS_DB + "-" + this.UserZone + ".sqlite", 0, null);
        String createTableQuery = "CREATE TABLE IF NOT EXISTS user_member_data (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mem_Date TEXT, " +
                "mem_gender TEXT, " +
                "mem_Name TEXT, " +
                "mem_LastName TEXT, " +
                "mem_Tel TEXT, " +
                "mem_DateOfBirth TEXT, " +
                "mem_IDcard TEXT, " +
                "Ad_No TEXT, " +
                "Ad_Mooban TEXT, " +
                "Ad_MoobanName TEXT, " +
                "Ad_Tambol TEXT, " +
                "Ad_Ampher TEXT, " +
                "Ad_Province TEXT, " +
                "Ad_IDpost TEXT, " +
                "mem_Email TEXT, " +
                "mem_MemberFarm TEXT, " +
                "mem_OccuPrime TEXT, " +
                "mem_OccuSecond TEXT, " +
                "mem_Graduate TEXT, " +
                "attach_IDcard BLOB, " +
                "attach_HouseRegis BLOB, "  +
                "attach_Authorise BLOB, " +
                "attach_AccountBank BLOB, " +
                "attech_Signature BLOB, " +
                "Acc_Num TEXT, " +
                "Acc_Bank TEXT, " +
                "Acc_Branc TEXT);";

        try {
            this.mydb.execSQL(createTableQuery);
            Log.e("Check", "Database created successfully");
            Toast.makeText(context, "Database created successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Check", "Failed to create database: " + e.getMessage());
            Toast.makeText(context, "Failed to create database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        initData();

    }

//    private void CheckLogin() {
//        String User="";
//        SQLiteDatabase db;
//        String DBFile=DATABASE_CENTER_DB+".sqlite";
//        ExternalStorage_Center_DB_OpenHelper obj= null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//            obj = new ExternalStorage_Center_DB_OpenHelper(DBFile);
//        }
//        if(obj.databaseFileExists())
//        {
//            db=obj.getReadableDatabase();
//            User=obj.GetEmpName("1", db);
//            obj.close();
//        }
//
//        Log.d("UserLoginDB_tmp", "UserLoginDB_tmp");
//
//        if(User.isEmpty() || User == ""){
//            Intent loginPage = new Intent(MainActivity.this,
//                    LoginActivity.class);
//            startActivity(loginPage);
//            MainActivity.this.finish();
//
//
//        }else {
//            DataSplit = User;
//            txtUser.setText("ผู้ใช้: "+User);
//        }
//
//    }

    private void CheckLogin(Context context) {
        String User="";
        SQLiteDatabase db;
        String DBFile="CENTER_DB"+".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj= new ExternalStorage_Center_DB_OpenHelper(context,DBFile);
        if(obj.databaseFileExists())
        {
            db=obj.getReadableDatabase();
            User=obj.GetEmpName("1", db);
            obj.close();
        }

        Log.d("UserLoginDB_tmp", "UserLoginDB_tmp");

        if(User == null || User == ""){
            Intent loginPage = new Intent(MainActivity.this,
                    LoginActivity.class);
            startActivity(loginPage);
            MainActivity.this.finish();


        }else {
            DataSplit = User;
            txtUser.setText("ผู้ใช้: "+User);
        }

    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage((CharSequence) "ต้องการจะปรับปรุงข้อมูลแผนที่หรือไม่ ?");
                builder.setCancelable(true);
                builder.setPositiveButton((CharSequence) "ใช่", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.downloadSQLite(MainActivity.this);
                    }
                });
                builder.setNegativeButton((CharSequence) "ยกเลิก", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
                return;
            case R.id.farmRegis:
                startActivity(new Intent(this, MenuFarmActivity.class));
                return;
            case R.id.logging:
                startActivity(new Intent(this, MenuEvaCutActivity.class));
                return;
            case R.id.logout:
                new MssBefLogoutFragment().show(getSupportFragmentManager(), "MessageDialog");
                return;
            case R.id.register:
                this.progress.show();
                startActivity(new Intent(this, RegisMemberActivity.class));
                return;
            case R.id.upload:
                startActivity(new Intent(this, UploadActivity.class));
                return;
            case R.id.visit:
                startActivity(new Intent(this, VisitMemberActivity.class));
                return;
            default:
                return;
        }
    }



    private String getUserZone(Context context) {
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context,"CENTER_DB.sqlite");
        if (!obj.databaseFileExists()) {
            return "NULL";
        }
        this.UserZone = obj.LoginData("1", obj.getReadableDatabase());
        obj.close();
        return this.UserZone;
    }

    public void downloadSQLite(Context context) {

        if (context != null) {
            String folderName = "MapDB"; // folder name
            String folder = context.getApplicationContext().getFilesDir() + "/" + folderName; // folder path

            File appDbDir = new File(folder); // create folder
            if (!appDbDir.exists()) {
                appDbDir.mkdirs(); // create folder if it doesn't exist
            }
            Path = new File(folder, "FSCGISDATA.sqlite"); // add file name
            Log.e("Error","downloadSQLite Path : " + Path.getAbsolutePath() );
        } else {
            // handle null context case
        }

        try {
            File file = new File(Path.getPath());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new DownloadFile_FSC_Async(this).execute("http://103.80.129.192/fsc/app/download/PanelPlus/FSCGISDATA.sqlite");

    }

    public class DownloadFile_FSC_Async extends AsyncTask<String, Integer, Boolean> {
        private final ProgressDialog progressDialog;
        private Context mContext;
        private File Path;

        public DownloadFile_FSC_Async(Context context) {
            progressDialog = new ProgressDialog(context);
            mContext = context;

            if (context != null) {
                String folderName = "MapDB";
                File folder = new File(context.getApplicationContext().getFilesDir(), folderName);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                Path = new File(folder, DATABASE_FSCGISDATANAME + ".sqlite");
            } else {
                return;
                // handle null context case
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Wait!!");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String downloadUrl = strings[0];
            InputStream inputStream = null;
            OutputStream outputStream = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3600000);
                connection.setReadTimeout(3600000);
                int lengthOfFile = connection.getContentLength();
                inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(Path);
                byte[] data = new byte[1024];
                long total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / lengthOfFile));
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                connection.disconnect();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result) {
                Toast.makeText(mContext, "File downloaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Failed to download file", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void logoutStage(Context context) {
        ProgressDialog progress2 = new ProgressDialog(this);
        progress2.setMessage("Wait!!");
        progress2.setCancelable(true);
        progress2.setProgressStyle(0);
        progress2.show();

        if (context != null) {
            String folderName = "MapDB"; // folder name
            String folder = context.getApplicationContext().getFilesDir() + "/" + folderName; // folder path
            File appDbDir = new File(folder); // create folder
            if (!appDbDir.exists()) {
                appDbDir.mkdirs(); // create folder if it doesn't exist
            }
            Path = new File(folder);
        } else {
            // handle null context case
        }


        this.loginparams = new ArrayList();
        this.loginparams = new ArrayList();
        this.loginparams.add(new BasicNameValuePair("UserZone", this.UserZone.trim()));
        String resultServer = getHttpPost("http://103.80.129.192/fsc/app/loginStage/logoutControl.php?UserZone=" + this.UserZone, this.loginparams);
        String LogoutData = resultServer;
        try {
            JSONObject c = new JSONObject(resultServer);
            String LogoutID = c.getString("LogoutID");
            LogoutData = c.getString("LoginData");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (LogoutData.equals("1")) {
            Toast.makeText(getApplicationContext(), LogoutData, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "LOGOUT COMPLETE", Toast.LENGTH_SHORT).show();
        }
        Boolean valueOf = Boolean.valueOf(new File(Path + DATABASE_CENTER_DB + ".sqlite").delete());
    }
    public String getHttpPost(String url,List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        if (android.os.Build.VERSION.SDK_INT > 26 ) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str.toString();

    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("ต้องการจะออกจากโปรแกรมหรือไม่ ?");
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

    public void myOnResume() {
        this.onResume();

        logoutStage(this);

        Intent login = new Intent(MainActivity.this,
                LoginActivity.class);
        startActivity(login);
        MainActivity.this.finish();

    }

    @Override
    public void onResume(){
        progress.hide();
        super.onResume();

    }
}


