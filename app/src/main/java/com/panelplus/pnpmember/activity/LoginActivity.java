package com.panelplus.pnpmember.activity;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.module.ConnectionDetector;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.internal.c;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Activity implements View.OnClickListener {
    private String username;
    private String password;

    private EditText PS;
    private EditText US;
    private Button btnlogin;
    private Button btnexit;


    private String Emp_ID;
    private String Emp_IDname;
    private String Emp_Name;
    private String Emp_NameEng;
    private String Emp_gender;
    private String Emp_Department;
    private String Emp_Post;
    private String Zone_Name;

    // flag for Internet connection status
    Boolean isInternetPresent = false;


    File Path = null;

    // Connection detector class
    private ConnectionDetector cd;
    public List<NameValuePair> loginparams;

    //insert DB

    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    SQLiteDatabase db;
    String DBFile = DATABASE_CENTER_DB +".sqlite";

    SQLiteDatabase mydb=null;
    private static final String DATABASE_TABLE_LOGIN = "user_login_data";
//    @SuppressLint("SdCardPath") String Path  = "/sdcard/MapDB/";
//    @SuppressLint("SdCardPath") String DATABASE_FILE_PATH="/sdcard/MapDB/";


//    private String  Path = null;

    private String DATABASE_FILE_PATH = null;
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitWidget();
        this.cd = new ConnectionDetector(this);
        Create_Center_DB(this);
    }

    private void InitWidget() {
        this.US = (EditText) findViewById(R.id.username);
        this.PS = (EditText) findViewById(R.id.password);
        this.btnlogin = (Button) findViewById(R.id.login);
        this.btnexit = (Button) findViewById(R.id.exit);
        this.btnlogin.setOnClickListener(this);
        this.btnexit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.login:

                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {

                    try {

                        username = US.getText().toString().trim();
                        password = PS.getText().toString().trim();

                        if (username.equals("") || password.equals("")) {

                            Toast toast = Toast.makeText(getApplicationContext(), "Please Input Username Password!!!",
                                    Toast.LENGTH_SHORT);
                            toast.show();

                        }else {
                            String url = "http://103.80.129.192/fsc/app/loginStage/checkLogin.php?UserName="+username+"&PassWord="+password;
                            Log.d("URL", url);


                            loginparams = new ArrayList<NameValuePair>();
                            loginparams.add(new BasicNameValuePair("UserName", username.trim()));
                            loginparams.add(new BasicNameValuePair("PassWord", password.trim()));

                            String resultServer  = getHttpPost(url,loginparams);
                            String LoginID = "0";
                            String LoginData = resultServer ;

                            JSONObject c;
                            try {
                                c = new JSONObject(resultServer);
                                LoginID = c.getString("LoginID");
                                LoginData = c.getString("LoginData");

                                Emp_ID = c.getString("Emp_ID");
                                Emp_IDname = c.getString("Emp_IDname");
                                Emp_Name = c.getString("Emp_Name");
                                Emp_NameEng = c.getString("Emp_NameEng");
                                Emp_Department = c.getString("Emp_Department");
                                Emp_Post = c.getString("Emp_Post");
                                Zone_Name = c.getString("Zone_Name");

                                Log.e("Check",Emp_ID);
                                Log.e("Check",Emp_IDname);
                                Log.e("Check",Emp_Name);
                                Log.e("Check",Emp_NameEng);
                                Log.e("Check",Emp_Department);
                                Log.e("Check",Emp_Post);
                                Log.e("Check",Zone_Name);


                                //Log.d("LoginID_LoginID", LoginID);
                                //Log.d("LoginData", LoginData);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if(LoginID.equals("0"))
                            {
                                Toast toast1 = Toast.makeText(getApplicationContext(), "LOGIN AGAIN, ERROR = "+LoginData ,
                                        Toast.LENGTH_SHORT);
                                toast1.show();
                                US.setText("");
                                PS.setText("");

                            } else {
                                ProgressDialog progress;

                                progress = new ProgressDialog(this);
                                progress.setMessage("Wait!!");
                                progress.setCancelable(true);
                                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progress.show();

                                Toast toast1 = Toast.makeText(getApplicationContext(), LoginData ,
                                        Toast.LENGTH_SHORT);
                                toast1.show();

                                insertDataToCenterDB(this);

                                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(myIntent);
                                LoginActivity.this.finish();

                                progress.hide();
                            }

                        }
                    }catch (Exception e) {
                        // TODO: handle exception
                        Toast toast= Toast.makeText(getApplicationContext(),e.toString().trim(),
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                //if internet not connection
                else {
                    Toast toast= Toast.makeText(getApplicationContext(),"กรุณาเชื่อต่ออินเตอร์ก่อนการล็อคอิน",
                            Toast.LENGTH_LONG);
                    toast.show();

                }


                break;

            case R.id.exit:
                LoginActivity.this.finish();
                break;


        }
    }

    public void Create_Center_DB(Context context) {
        if (context != null) {
            String folderName = "MapDB"; // ชื่อโฟลเดอร์
            String folder = context.getApplicationContext().getFilesDir() + "/" + folderName; // เส้นทางของโฟลเดอร์
            File appDbDir = new File(folder); // สร้างโฟลเดอร์
            if (!appDbDir.exists()) {
                appDbDir.mkdirs(); // สร้างโฟลเดอร์หากยังไม่มีอยู่

            }
            File databaseFile = new File(appDbDir, DATABASE_CENTER_DB + ".sqlite"); // ไฟล์ฐานข้อมูล
            this.mydb = SQLiteDatabase.openOrCreateDatabase(databaseFile, null); // เปิดหรือสร้างฐานข้อมูล

            // สร้างตาราง DATABASE_TABLE_LOGIN
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_LOGIN +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " Emp_ID TEXT, " +
                    " Emp_IDname TEXT, " +
                    " Emp_Name TEXT, " +
                    " Emp_NameEng TEXT, " +
                    " Emp_Department TEXT, " +
                    " Emp_Post TEXT, " +
                    " Zone_Name TEXT)";
            this.mydb.execSQL(createTableQuery);

            Toast.makeText(context, "Database created successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("Check", "Null context: " + context);
        }
    }


    private void insertDataToCenterDB(Context context) {
        String DBFile = "CENTER_DB"+".sqlite";
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context,DBFile);
        this.db = obj.getReadableDatabase();
//        Log.e("Check"," Check Obj" + obj.getReadableDatabase().toString());
        obj.InsertDataEmployeeLogin(this.Emp_ID,
                this.Emp_IDname,
                this.Emp_Name,
                this.Emp_NameEng,
                this.Emp_Department,
                this.Emp_Post,
                this.Zone_Name,
                this.db);
        obj.close();
    }

//    public String getHttpPost(String url, List<NameValuePair> params) {
//        StringBuilder str = new StringBuilder();
//        HttpClient client = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost(url);
//        if (Build.VERSION.SDK_INT > 26) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
//        }
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(params));
//            HttpResponse response = client.execute(httpPost);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//                while (true) {
//                    String line = reader.readLine();
//                    if (line == null) {
//                        break;
//                    }
//                    str.append(line);
//                }
//            } else {
//                Log.e("Log", "Failed to download result..");
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e2) {
//            e2.printStackTrace();
//        }
//        return str.toString();
//    }


//    public String getHttpPost(String url, List<NameValuePair> params) {
//        StringBuilder str = new StringBuilder();
//        OkHttpClient client = new OkHttpClient();
//
//        HttpPost httpPost = new HttpPost(url);
//
//        if (Build.VERSION.SDK_INT > 26) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
//        }
//
//        try {
//            // Set the parameters as JSON in the request body
//            List<NameValuePair> jsonParams = new ArrayList<NameValuePair>();
//            for (NameValuePair param : params) {
//                jsonParams.add(new BasicNameValuePair(param.getName(), param.getValue()));
//            }
//            httpPost.setEntity(new StringEntity(URLEncodedUtils.format(jsonParams, "UTF-8")));
//            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
//
//            Request.Builder requestBuilder = new Request.Builder()
//                    .url(url)
//                    .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), httpPost.getEntity().toString()));
//
//            Response response = client.newCall(requestBuilder.build()).execute();
//            if (response.code() == 200) {
//                String result = response.body().string();
//                Log.e("Error", result);
//                str.append(result);
//            } else {
//                Log.e("Error", "Failed to download result..");
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e2) {
//            e2.printStackTrace();
//        }
//        Log.e("result str ", str.toString());
//        return str.toString();
//
//    }

//    public String getHttpPost(String url, List<NameValuePair> params) {
//        StringBuilder str = new StringBuilder();
//
//        try {
//            HttpClient client = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost(url);
//
//            List<NameValuePair> jsonParams = new ArrayList<NameValuePair>();
//            for (NameValuePair param : params) {
//                jsonParams.add(new BasicNameValuePair(param.getName(), param.getValue()));
//            }
//            httpPost.setEntity(new UrlEncodedFormEntity(jsonParams, "UTF-8"));
//
//            HttpResponse response = client.execute(httpPost);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                HttpEntity entity = response.getEntity();
//                if (entity != null) {
//                    InputStream inputStream = entity.getContent();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        str.append(line);
//                    }
//                    inputStream.close();
//                }
//            } else {
//                Log.e("Log", "Failed to download result..");
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e2) {
//            e2.printStackTrace();
//        }
//        return str.toString();
//    }
public String getHttpPost(String url, List<NameValuePair> params) {
    StringBuilder str = new StringBuilder();
    OkHttpClient client = new OkHttpClient();

    HttpPost httpPost = new HttpPost(url);

    if (Build.VERSION.SDK_INT > 26) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
    }

    try {
        // Set the parameters as JSON in the request body
        List<NameValuePair> jsonParams = new ArrayList<>();
        for (NameValuePair param : params) {
            jsonParams.add(new BasicNameValuePair(param.getName(), param.getValue()));
        }
        httpPost.setEntity(new StringEntity(URLEncodedUtils.format(jsonParams, "UTF-8")));
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), httpPost.getEntity().toString()));

        Response response = client.newCall(requestBuilder.build()).execute();
        if (response.code() == 200) {
            String result = response.body().string();
            str.append(result);
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
