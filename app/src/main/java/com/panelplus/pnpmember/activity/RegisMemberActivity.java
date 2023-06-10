package com.panelplus.pnpmember.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.TransactionTooLargeException;
import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.res.ResourcesCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.panelplus.pnpmember.BuildConfig;
import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Member_DB_OpenHelper;
import com.panelplus.pnpmember.module.ConnectionDetector;
import com.panelplus.pnpmember.module.GPSTracker;
import com.panelplus.pnpmember.module.Utilities;
import com.panelplus.pnpmember.signature.SignaturePad;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
//import id.zelory.compressor.Compressor;

import static android.service.controls.ControlsProviderService.TAG;
import static java.security.AccessController.getContext;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisMemberActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnSave;
    private Button btnPicIdCard;
    private Button btnPicHouseregis;
    private Button btnPicAutorize;
    private Button btnPicAccountBank;
    private Button btnVerify;
    private TextView txtStatus;

    private ArrayList<String> mAcc = new ArrayList<String>();
    private ArrayList<String> mGra = new ArrayList<String>();
    private ArrayList<String> mGen = new ArrayList<String>();
    private ArrayList<String> mBa = new ArrayList<String>();
    private ArrayList<String> mMem = new ArrayList<String>();

    EditText name, lastname, tel, date, idcard, email,
            houseNo, moobanNo, moobanName, tambol, aumpher,
            province, idpost, AccNum, AccBranc;

    private Spinner mAccSpinner, mAccSpinner2, mGraSpinner, mGenderSpinner, mBankSpinner, mMemberFarmSpinner;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2000;
    private Spinner occuprime, occusecond, graduate, gender, accBank, memberFarm;

    private String occuprime1, occusecond1, graduate1;
    private int checkVerifyed = 0;
    private String IDcardStatusID = "";
    private ActivityResultLauncher<Intent> launcher;
    ImageView imgIdCard, imgHouseregis, imgAutorize, imgAccountBank;
    private static final int REQUEST_PICK_IMAGE = 1001;
    private static final int MAX_IMAGE_SIZE = 2 * 1024 * 1024; // 2MB

    private Context mContext;
    //    private String Path = null;
    private String DATABASE_FILE_PATH = null;
    //DB
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private static final String DATABASE_REGIS_DB = "REGIS_DB";
    private static final String TABLE_MEMBER_DATA = "user_member_data";
//    @SuppressLint("SdCardPath") String DATABASE_FILE_PATH="/sdcard/MapDB/";

    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    String UserZone = null;

    SQLiteDatabase db;
    //    private String DBFile = null ;
    private File photoFile;
    //----------------------- Camera  ---------------------

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    //private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Camera";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri fileUri; // file url to store image/video

    private Bitmap casheBitmap;
    byte[] default_byte = {0};

    private String DBFile = "REGIS_DB-" + this.UserZone + ".sqlite";
    String time_str;
    private static int RESULT_LOAD_IMAGE = 1;

    private byte[] image;
    private byte[] picIdcard = null;
    private byte[] picHouseregis = null;
    private byte[] picAutorize = null;
    private byte[] picAccountBank = null;
    private int numimage = 0;
    private int numCountIMG = 0;
    private String dateNow;
    //connect PHP
    private ConnectionDetector cd;
    public List<NameValuePair> loginparams = null;
    File Path = null;
    SQLiteDatabase mydb;
    //------------------------ signature --------------------//
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private SignaturePad mSignaturePad = null;
    private Button mClearButton;
    //private Button mSaveButton;
    private int checkSignature = 0;
    private byte[] picSignature = null;
    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    GPSTracker gps;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_regis_member);

        //spinner
        spinner();
        initWidget();

        gps = new GPSTracker(RegisMemberActivity.this);

        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.d("latitude", String.valueOf(latitude));
            Log.d("longitude", String.valueOf(longitude));

            getAddress(latitude, longitude);

        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 543;

        dateNow = new SimpleDateFormat("dd-MM",
                Locale.getDefault()).format(new Date());

        dateNow = dateNow + "-" + String.valueOf(year);
        Log.d("date", String.valueOf(dateNow));


        //get user
        UserZone = getUserZone(this);


        createPrimaryDB(this);
        //create database


//        Button buttonLoadImage = (Button) findViewById(R.id.picIdCard);
//        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                Intent i = new Intent(
//                        Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
//            }
//        });


        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Toast.makeText(RegisMemberActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                //mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
                checkSignature = 1;
            }

            @Override
            public void onClear() {
                //mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
                checkSignature = 0;
            }
        });
//        try {
//            Field f = TransactionTooLargeException.class.getDeclaredField("MAX_SIZE");
//            f.setAccessible(true);
//            f.set(null, 16 * 1024 * 1024); // 16 MB
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    private void createPrimaryDB(Context context) {
        if (context != null) {
            String folderName = "MapDB";
            String folderPath = context.getApplicationContext().getFilesDir() + "/" + folderName;
            File appDbDir = new File(folderPath);
            if (!appDbDir.exists()) {
                appDbDir.mkdirs();
            }
            this.Path = appDbDir;

            String databaseName = DATABASE_REGIS_DB + "-" + this.UserZone + ".sqlite";
            String databasePath = folderPath + "/" + databaseName;

            try {
                this.mydb = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

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
                        "attach_HouseRegis BLOB, " +
                        "attach_Authorise BLOB, " +
//                        "attach_FaceRegister BLOB, " +
                        "attach_AccountBank BLOB, " +
                        "attech_Signature BLOB, " +
//                        "attach_Signature BLOB, " +
                        "Acc_Num TEXT, " +
                        "Acc_Bank TEXT, " +
                        "Acc_Branc TEXT);";

                this.mydb.execSQL(createTableQuery);
                Log.e("Check", "Database created  user_member_data successfully");
                Toast.makeText(context, "Database created successfully!", Toast.LENGTH_SHORT).show();

                // Check the path of the created table
                String tablePath = this.mydb.getPath();
                Log.e("Check", "Table path: " + tablePath);
            } catch (Exception e) {
                Log.e("Check", "Failed to create database: " + e.getMessage());
                Toast.makeText(context, "Failed to create database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            // handle null context case
        }
    }

    private void getAddress(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                Log.d("Address", String.valueOf(address));

                province.setText(address.getAdminArea());
                idpost.setText(address.getPostalCode());

                String tambolString0 = address.getLocality();
                if (tambolString0 != null) {
                    String tambolString1 = tambolString0.replace(" ", "");
                    String tambolString2 = tambolString1.replace("ตำบล", "");
                    tambol.setText(tambolString2);
                }

                String aumpherString0 = address.getSubAdminArea();
                if (aumpherString0 == null) {
                    aumpherString0 = address.getLocality();
                }
                if (aumpherString0 != null) {
                    String aumpherString1 = aumpherString0.replace(" ", "");
                    String aumpherString2 = aumpherString1.replace("อำเภอ", "");
                    aumpher.setText(aumpherString2);
                }
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
    }


//    private void getAddress(double latitude, double longitude) {
//
//        try {
//            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            if (addresses.size() > 0) {
//                Address address = addresses.get(0);
//                Log.d("Address", String.valueOf(address));
//
//                province.setText(address.getAdminArea());
//                idpost.setText(address.getPostalCode());
//
//                String tambolString0 = address.getLocality();//.getlocalityName();
//                String tambolString1 = tambolString0.replace(" ", "");
//                String tambolString2 = tambolString1.replace("ตำบล", "");
//                if (tambolString2 != null) {
//                    try {
//                        tambol.setText(tambolString2);
//                    } catch (ArrayIndexOutOfBoundsException e) {
//                        //Log.e("splitTabol ",e.toString());
//                        try {
//                            tambol.setText(tambolString2);
//                        } catch (ArrayIndexOutOfBoundsException e1) {
//                            Log.e("splitTabol ", e1.toString());
//                        }
//                    }
//                }
//
//                String aumpherString0 = address.getSubAdminArea();
//                String aumpherString1 = aumpherString0.replace(" ", "");
//                String aumpherString2 = aumpherString1.replace("อำเภอ", "");
//                if (aumpherString2 == null) {
//                    aumpherString0 = address.getLocality();
//                    aumpherString1 = aumpherString0.replace(" ", "");
//                    aumpherString2 = aumpherString1.replace("ตำบล", "");
//                    //String[] aumpherStringAft = aumpherString.split(" ");
//                    //aumpher.setText(aumpherStringAft[0]);
//                    aumpher.setText(aumpherString2);
//                } else {
//                    //String[] aumpherStringAft = aumpherString.split(" ");
//                    //aumpher.setText(aumpherStringAft[0]);
//                    aumpher.setText(aumpherString2);
//                }
//                /*
//                //อำเภอ
//                address.getLocality();
//                //จังหวัด
//                address.getAdminArea();
//
//                //รหัสไปรษณีย์
//                address.getPostalCode();
//
//                */
//            }
//        } catch (IOException e) {
//            Log.e("tag", e.getMessage());
//        }
//
//
//    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                occuprime1 = occuprime.getSelectedItem().toString();
                occusecond1 = occusecond.getSelectedItem().toString();
                graduate1 = graduate.getSelectedItem().toString();


                checkimage();

                if (IDcardStatusID == "0" || checkVerifyed == 0) {
                    //if(idcard.getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(), "กรุณาตรวจสอบเลขบัตรประชาชนก่อน",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (idcard.getText().toString().isEmpty() ||
                            name.getText().toString().isEmpty() ||
                            lastname.getText().toString().isEmpty() ||
                            tel.getText().toString().isEmpty() ||
                            date.getText().toString().isEmpty() ||
                            idcard.getText().toString().isEmpty() ||
                            email.getText().toString().isEmpty() ||
                            houseNo.getText().toString().isEmpty() ||
                            moobanNo.getText().toString().isEmpty() ||
                            tambol.getText().toString().isEmpty() ||
                            aumpher.getText().toString().isEmpty() ||
                            province.getText().toString().isEmpty() ||
                            idpost.getText().toString().isEmpty() ||
                            AccNum.getText().toString().isEmpty() ||
                            AccBranc.getText().toString().isEmpty() ||
                            numCountIMG == 0 || checkSignature == 0) {
                        if (idcard.getText().toString().isEmpty() ||
                                name.getText().toString().isEmpty() ||
                                lastname.getText().toString().isEmpty() ||
                                tel.getText().toString().isEmpty() ||
                                date.getText().toString().isEmpty() ||
                                idcard.getText().toString().isEmpty() ||
                                email.getText().toString().isEmpty() ||
                                houseNo.getText().toString().isEmpty() ||
                                moobanNo.getText().toString().isEmpty() ||
                                tambol.getText().toString().isEmpty() ||
                                aumpher.getText().toString().isEmpty() ||
                                province.getText().toString().isEmpty() ||
                                idpost.getText().toString().isEmpty() ||
                                AccNum.getText().toString().isEmpty() ||
                                AccBranc.getText().toString().isEmpty()) {

                            Toast toast = Toast.makeText(getApplicationContext(), "กรุณากรอข้อมูลให้ครบถ้วนก่อนการบันทึก",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        if (numCountIMG == 0) {
                            if (picIdcard == null) {
                                Toast toast1 = Toast.makeText(getApplicationContext(), "กรุณาถ่ายรูปบัตรประชาชนก่อนการบันทึก",
                                        Toast.LENGTH_SHORT);
                                toast1.show();
                            }
                            if (picHouseregis == null) {
                                Toast toast1 = Toast.makeText(getApplicationContext(), "กรุณาถ่ายรูปสำเนาทะเบียนบ้านก่อนการบันทึก",
                                        Toast.LENGTH_SHORT);
                                toast1.show();
                            }
                            if (picAutorize == null) {
                                Toast toast1 = Toast.makeText(getApplicationContext(), "กรุณาถ่ายรูปผู้สมัครเข้าร่วมโครงการก่อนการบันทึก",
                                        Toast.LENGTH_SHORT);
                                toast1.show();
                            }
                            if (picAccountBank == null) {
                                Toast toast1 = Toast.makeText(getApplicationContext(), "กรุณาถ่ายรูปหน้าสมุดบัญชีธนาคารก่อนการบันทึก",
                                        Toast.LENGTH_SHORT);
                                toast1.show();
                            }

                            if (checkSignature == 0) {
                                Toast.makeText(RegisMemberActivity.this, "กรุณาลงลายมือชื่อก่อนการบันทึก", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        beforeSave(this);
                        //savetoSQlite();
                    }

                }

                break;

            case R.id.picIdCard:
                numimage = 1;
                captureImage();
                break;

            case R.id.picHouseregis:
                numimage = 2;
                captureImage();
                break;

            case R.id.picAutorize:
                numimage = 3;
                captureImage();
                break;

            case R.id.picAccountBank:
                numimage = 4;
                captureImage();
                break;

            case R.id.clear_button:
                mSignaturePad.clear();
                break;


            case R.id.verify:
                String idcardS = idcard.getText().toString();
                if (idcardS.equals("") || idcardS.length() < 13) {
                    Toast toast = Toast.makeText(getApplicationContext(), "กรุณาใส่เลขบัตรประชาชนก่อนการตรวจสอบ",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    ProgressDialog progress;

                    progress = new ProgressDialog(this);
                    progress.setMessage("Wait!!");
                    progress.setCancelable(true);
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();


                    //String url = "http://192.168.43.160:8282/app/verifyid.php?IDcard="+idcardS;
                    //String url = "http://210.86.153.17/pnp-member/app/verifyid.php?IDcard="+idcardS;
                    String url = "http://103.80.129.192/fsc/app/verifyid.php?IDcard=" + idcardS;
                    loginparams = new ArrayList<NameValuePair>();
                    loginparams.add(new BasicNameValuePair("IDcard", idcardS.trim()));

                    String resultServer = getHttpPost(url, loginparams);
                    String IDcardStatus = resultServer;

                    JSONObject c;
                    try {
                        c = new JSONObject(resultServer);
                        IDcardStatusID = c.getString("IDcardStatusID");
                        IDcardStatus = c.getString("IDcardStatus");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (IDcardStatusID.equals("0")) {
                        txtStatus.setText(IDcardStatus);
                        progress.hide();
                    } else {
                        txtStatus.setText(IDcardStatus);
                        checkVerifyed = 1;
                        idcard.setEnabled(false);
                        progress.hide();
                    }

                }
                break;

        }
    }

    private String getUserZone(Context context) {
        ExternalStorage_Center_DB_OpenHelper obj = new ExternalStorage_Center_DB_OpenHelper(context, "CENTER_DB.sqlite");
        if (!obj.databaseFileExists()) {
            return "NULL";
        }
        this.UserZone = obj.LoginData("1", obj.getReadableDatabase());
        Log.e("Check", "UserZone : " + UserZone);
        obj.close();
        return UserZone;
    }


    public static void verifyStoragePermissions(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
        }
    }

    public File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        mediaScanIntent.setData(Uri.fromFile(photo));
        sendBroadcast(mediaScanIntent);
    }

    public boolean addpngSignatureToGallery(Bitmap signature) {
        try {
            @SuppressLint("DefaultLocale") File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.png", new Object[]{Long.valueOf(System.currentTimeMillis())}));
            saveBitmapToPNG(signature, photo);
            scanMediaFile(photo);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveBitmapToPNG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(-1);
        canvas.drawBitmap(bitmap, 0, 0, (Paint) null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
        stream.close();
        this.picSignature = Utilities.getBytes(newBitmap);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        try {
            @SuppressLint("DefaultLocale") File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", new Object[]{Long.valueOf(System.currentTimeMillis())}));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void spinner() {
        //combo prime acc
        mAccSpinner = (Spinner) findViewById(R.id.accprime);

        createAccData();

        ArrayAdapter<String> adapterAcc1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mAcc);
        mAccSpinner.setAdapter(adapterAcc1);
        //end combo prime acc

        //combo prime second
        mAccSpinner2 = (Spinner) findViewById(R.id.accsecond);


        ArrayAdapter<String> adapterAcc2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mAcc);
        mAccSpinner2.setAdapter(adapterAcc2);
        //end combo prime second

        //ระดับการศึกษา
        mGraSpinner = (Spinner) findViewById(R.id.graduate);

        createGraData();

        ArrayAdapter<String> mGraduate = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mGra);
        mGraSpinner.setAdapter(mGraduate);
        ///////////////////

        mGenderSpinner = (Spinner) findViewById(R.id.gender);
        mGen.add("นาย");
        mGen.add("นาง");
        mGen.add("นางสาว");
        ArrayAdapter<String> mGender = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mGen);
        mGenderSpinner.setAdapter(mGender);


        mBankSpinner = (Spinner) findViewById(R.id.AccBank);
        mBa.add("ธนาคารกรุงเทพ");
        mBa.add("ธนาคารกสิกรไทย");
        mBa.add("ธนาคารกรุงไทย");
        mBa.add("ธนาคารกรุงศรีอยุธยา");
        mBa.add("ธนาคารไทยพาณิชย์");
        mBa.add("ธนาคารทหารไทย");
        mBa.add("ธนาคารเพื่อการเกษตรและสหกรณ์การเกษตร");
        mBa.add("ธนาคารออมสิน");
        mBa.add("ธนาคารอาคารสงเคราะห์");
        mBa.add("ธนาคารอิสลามแห่งประเทศไทย");
        mBa.add("ธนาคารเกียรตินาคิน");
        mBa.add("ธนาคารซีไอเอ็มบีไทย");
        mBa.add("ธนาคารธนชาต");
        ArrayAdapter<String> mBank = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mBa);
        mBankSpinner.setAdapter(mBank);

        mMemberFarmSpinner = (Spinner) findViewById(R.id.memberFarm);
        mMem.add("-");
        mMem.add("การยางแห่งประเทศไทย");
        mMem.add("ธนาคารเพื่อการเกษตรและสหกรณ์");
        mMem.add("สหกรณ์การเกษตร");
        ArrayAdapter<String> mMember = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mMem);
        mMemberFarmSpinner.setAdapter(mMember);


    }

    private void initWidget() {
        //insert to table and Go to eva_member
        gender = (Spinner) findViewById(R.id.gender);
        name = (EditText) findViewById(R.id.name);
        lastname = (EditText) findViewById(R.id.lastname);
        tel = (EditText) findViewById(R.id.tel);
        date = (EditText) findViewById(R.id.date);
        idcard = (EditText) findViewById(R.id.idcard);
        email = (EditText) findViewById(R.id.email);
        houseNo = (EditText) findViewById(R.id.houseNo);
        moobanNo = (EditText) findViewById(R.id.moobanNo);
        moobanName = (EditText) findViewById(R.id.moobanName);
        tambol = (EditText) findViewById(R.id.tambol);
        aumpher = (EditText) findViewById(R.id.aumpher);
        province = (EditText) findViewById(R.id.province);
        idpost = (EditText) findViewById(R.id.idpost);
        memberFarm = (Spinner) findViewById(R.id.memberFarm);

        accBank = (Spinner) findViewById(R.id.AccBank);
        AccNum = (EditText) findViewById(R.id.AccNum);
        AccBranc = (EditText) findViewById(R.id.AccBranc);

        occuprime = (Spinner) findViewById(R.id.accprime);
        occusecond = (Spinner) findViewById(R.id.accsecond);
        graduate = (Spinner) findViewById(R.id.graduate);

        btnSave = (Button) findViewById(R.id.save);
        btnPicIdCard = (Button) findViewById(R.id.picIdCard);
        btnPicHouseregis = (Button) findViewById(R.id.picHouseregis);
        btnPicAutorize = (Button) findViewById(R.id.picAutorize);
        btnPicAccountBank = (Button) findViewById(R.id.picAccountBank);

        btnVerify = (Button) findViewById(R.id.verify);
        txtStatus = (TextView) findViewById(R.id.status);

        mClearButton = (Button) findViewById(R.id.clear_button);
        //mSaveButton = (Button) findViewById(R.id.save_button);

        imgIdCard = (ImageView) findViewById(R.id.imgIdCard);
        imgHouseregis = (ImageView) findViewById(R.id.imgHouseregis);
        imgAutorize = (ImageView) findViewById(R.id.imgAutorize);
        imgAccountBank = (ImageView) findViewById(R.id.imgAccountBank);

        btnSave.setOnClickListener(this);
        btnPicIdCard.setOnClickListener(this);
        btnPicHouseregis.setOnClickListener(this);
        btnPicAutorize.setOnClickListener(this);
        btnPicAccountBank.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        mClearButton.setOnClickListener(this);
        //mSaveButton.setOnClickListener(this);


    }

    private void createGraData() {
        mGra.add("ประถมศึกษา");
        mGra.add("มัธยมศึกษา ปวช. ปวส.");
        mGra.add("ปริญญาตรี");
        mGra.add("สูงกว่าปริญญาตรี");
    }

    private void createAccData() {
        mAcc.add("เกษตรกร");
        mAcc.add("ข้าราชการ");
        mAcc.add("บริษัทเอกชน");
        mAcc.add("ธุรกิจส่วนตัว");
        mAcc.add("ไม่มี");
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisMemberActivity.this);
        builder.setMessage("ต้องการจะออกจากการบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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

    public void beforeSave(Context context) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisMemberActivity.this);
        builder.setMessage("ต้องการจะบันทึกข้อมูลหรือไม่ ?");
        builder.setCancelable(true);

        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                saveToSQLite(context);


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


    private void captureImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
        outState.clear();
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.fileUri = (Uri) savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            if (resultCode == -1) {

                if (numimage == 1) {

                    Uri selectedImageUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();

                    // อ่านและเก็บรูปภาพเป็นไบต์แอร์เรย์
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    this.picIdcard = stream.toByteArray();
                    Toast.makeText(getApplicationContext(), "ขนาด : " + picIdcard.length, Toast.LENGTH_SHORT).show();

                    // แสดงรูปภาพที่เลือก
                    ImageView imageView = findViewById(R.id.imgIdCard);
                    imageView.setImageBitmap(bitmap);


                } else if (numimage == 2) {


                    Uri selectedImageUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();

                    // อ่านและเก็บรูปภาพเป็นไบต์แอร์เรย์
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    this.picHouseregis = stream.toByteArray();
                    Toast.makeText(getApplicationContext(), "ขนาด : " + picHouseregis.length, Toast.LENGTH_SHORT).show();

                    // แสดงรูปภาพที่เลือก
                    ImageView imageView = findViewById(R.id.imgHouseregis);
                    imageView.setImageBitmap(bitmap);


                } else if (numimage == 3) {

                    Uri selectedImageUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();

                    // อ่านและเก็บรูปภาพเป็นไบต์แอร์เรย์
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    this.picAutorize = stream.toByteArray();
                    Toast.makeText(getApplicationContext(), "ขนาด : " + picAutorize.length, Toast.LENGTH_SHORT).show();

                    // แสดงรูปภาพที่เลือก
                    ImageView imageView = findViewById(R.id.imgAutorize);
                    imageView.setImageBitmap(bitmap);


                } else if (numimage == 4) {

                    Uri selectedImageUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();

                    // อ่านและเก็บรูปภาพเป็นไบต์แอร์เรย์
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    this.picAccountBank = stream.toByteArray();
                    Toast.makeText(getApplicationContext(), "ขนาด : " + picAutorize.length, Toast.LENGTH_SHORT).show();

                    // แสดงรูปภาพที่เลือก
                    ImageView imageView = findViewById(R.id.imgAccountBank);
                    imageView.setImageBitmap(bitmap);


                }
            } else if (resultCode != 0) {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkimage() {
        if (this.picIdcard != null && this.picAccountBank != null && this.picHouseregis != null && this.picAutorize != null) {
            this.numCountIMG++;
        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveToSQLite(Context context) {
        if (!addpngSignatureToGallery(this.mSignaturePad.getSignatureBitmap())) {
            Toast.makeText(context.getApplicationContext(), "Unable to store the signature", Toast.LENGTH_SHORT).show();
        }

        if (!addSvgSignatureToGallery(this.mSignaturePad.getSignatureSvg())) {
            Toast.makeText(context.getApplicationContext(), "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
        }

        this.DBFile = "REGIS_DB" + "-" + this.UserZone + ".sqlite";
        ExternalStorage_Member_DB_OpenHelper obj5 = new ExternalStorage_Member_DB_OpenHelper(context, DBFile);
        if (obj5.databaseFileExists()) {
            this.mydb = obj5.getWritableDatabase(); // เปิดฐานข้อมูลให้เป็นแบบเขียนได้
            long check = -1;
            try {
                this.mydb.beginTransaction();

                check = obj5.InsertDataRegisMember(
                        dateNow.trim(),
                        gender.getSelectedItem().toString(),
                        name.getText().toString().trim(),
                        lastname.getText().toString().trim(),
                        tel.getText().toString().trim(),
                        date.getText().toString().trim(),
                        idcard.getText().toString().trim(),
                        houseNo.getText().toString().trim(),
                        moobanNo.getText().toString().trim(),
                        moobanName.getText().toString().trim(),
                        tambol.getText().toString().trim(),
                        aumpher.getText().toString().trim(),
                        province.getText().toString().trim(),
                        idpost.getText().toString().trim(),
                        email.getText().toString().trim(),
                        memberFarm.getSelectedItem().toString(),
                        occuprime1.trim(),
                        occusecond1.trim(),
                        graduate1.trim(),
                        picIdcard,
                        picHouseregis,
                        picAutorize,
                        picAccountBank,
                        picSignature,
                        AccNum.getText().toString().trim(),
                        accBank.getSelectedItem().toString(),
                        AccBranc.getText().toString().trim(),
                        mydb
                );

                mydb.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("Check", "Failed to insert data into database: " + e.getMessage());
                Toast.makeText(context, "Failed to insert data into database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                mydb.endTransaction();
                obj5.close();
            }

            if (check != -1) {
                Toast.makeText(getApplicationContext(), "บันทึกข้อมูลเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, EvaMemberActivity.class));
            } else {
                Log.e("Check", "บันทึกข้อมูลล้มเหลว");
            }
        }
    }


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
