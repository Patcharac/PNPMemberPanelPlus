package com.panelplus.pnpmember.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.database.ExternalStorage_Center_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Member_DB_OpenHelper;
import com.panelplus.pnpmember.database.ExternalStorage_Visit_DB_OpenHelper;

import java.io.File;

public class DataTable extends AppCompatActivity {

    private static final String TAG = "DataTable";
    public static final String EXTRA_DATA = "extra_data";
    private static final String DATABASE_CENTER_DB = "CENTER_DB";
    private static final String DATABASE_COOR_HOUSE_DB = "COOR_HOUSE_DB";
    private static final String DATABASE_COOR_IMPORTANCE_DB = "COOR_IMPORTANCE_DB";
    private static final String DATABASE_EDIT_FARM_DB = "EDITFARM_DB";
    private static final String DATABASE_EVACON_DB = "EVACON_DB";
    private static final String DATABASE_FARMGEO_DB = "FARMGEO_DB";
    private static final String DATABASE_REC_GIRTH_DB = "REC_GIRTH_DB";
    private static final String DATABASE_REGIS_DB = "REGIS_DB";
    private static final String DATABASE_VISIT_DB = "VISIT_DB";
    private String data;
    private String UserEmpID;
    public String UserZone;
    private String Username;
    private TableLayout table1;
    private TableLayout table2;
    private TableRow dataRow;
    private TableRow dataRow2;
    private TextView dataText1;
    private TextView dataText2;
    private TextView dataText3;

    private TextView dataText4;
    private TextView dataText5;
    private TextView dataText6;
    SQLiteDatabase db;
    File Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);

        initWidget();
        data = getIntent().getStringExtra(EXTRA_DATA);
        if (data != null) {
            Log.e(TAG, "Received data: " + data);

        }
        this.UserZone = getUserZone(this);
        this.UserEmpID = getEmpID(this);
        this.Username = getUsername(this);

        initData(this);
    }

    @SuppressLint("WrongViewCast")
    private void initWidget() {
        table1 = findViewById(R.id.table1);
        dataRow = findViewById(R.id.dataRow);
        dataText1 = findViewById(R.id.dataText1);
        dataText2 = findViewById(R.id.dataText2);
        dataText3 = findViewById(R.id.dataText3);


        table2 = findViewById(R.id.table2);
        dataRow2 = findViewById(R.id.dataRow2);
        dataText4 = findViewById(R.id.dataText4);
        dataText5 = findViewById(R.id.dataText5);
        dataText6 = findViewById(R.id.dataText6);
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

    private boolean isTableExists(SQLiteDatabase db, String tableName) {
        String query = "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{tableName});
        boolean tableExists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return tableExists;
    }

    private void initData(Context context) {

        File folder = new File(getFilesDir() + "/MapDB/");
        boolean success = true;
        if (!folder.exists()) {
            Toast.makeText(context.getApplicationContext(), "Not Create File", Toast.LENGTH_SHORT).show();
        }
        if (success) {
            Toast.makeText(context.getApplicationContext(), "Success Open File", Toast.LENGTH_SHORT).show();

        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("ที่เก็บข้อมูลไม่สำเร็จ  " + folder.getAbsolutePath() + "ที่เก็บข้อมูลไม่สำเร็จ  " + folder.getPath());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        this.Path = folder;
        Log.e("Check", "Path2" + Path);

        Log.e("Check", "Check data : " + data);
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


        if (data.equals(DelPathMemRegis.toString())) {


        } else if (data.equals(DelPathEditFarmRegis.toString())) {

        } else if (data.equals(DelPathEvaCon.toString())) {

        } else if (data.equals(DelPathVisit.toString())) {
            String DBFile = DATABASE_VISIT_DB + "-" + this.UserZone + ".sqlite";
            Log.e("Check", "DataBase : Exception" + DBFile);
            ExternalStorage_Visit_DB_OpenHelper obj = new ExternalStorage_Visit_DB_OpenHelper(context, DBFile);
            if (obj.databaseFileExists()) {
                db = obj.getWritableDatabase();

                try {
                    String[] memberColumns = {"qouta_ID", "VisitMem_Date", "Emp_ID"};
                    String[] farmColumns = {"VisitFarm_Date", "Farm_ID", "Emp_ID"};

                    if (isTableExists(db, "visit_member_data")) {
                        Cursor memberDataCursor = db.query("visit_member_data", memberColumns, null, null, null, null, null);

                        if (memberDataCursor != null && memberDataCursor.moveToFirst()) {
                            do {
                                // Read data from Cursor
                                int visitDateIndex = memberDataCursor.getColumnIndex("VisitMem_Date");
                                int quotaIDIndex = memberDataCursor.getColumnIndex("qouta_ID");
                                int empIDIndex = memberDataCursor.getColumnIndex("Emp_ID");
                                Log.e("Check", "Check If quotaIDIndex: " + quotaIDIndex);
                                Log.e("Check", "Check If visitDateIndex: " + visitDateIndex);
                                Log.e("Check", "Check If empIDIndex: " + empIDIndex);

                                if (quotaIDIndex != -1 && visitDateIndex != -1 && empIDIndex != -1) {
                                    Log.e("Check", "Check If quotaIDIndex");
                                    String quotaID = memberDataCursor.getString(quotaIDIndex);
                                    String visitDate = memberDataCursor.getString(visitDateIndex);
                                    String empID = memberDataCursor.getString(empIDIndex);

                                    // Create new TableRow
                                    TableRow dataRow = new TableRow(this);
                                    dataRow.setBackgroundColor(Color.parseColor("#EEEEEE"));

                                    // Create TextView for quota ID
                                    TextView dataText1 = new TextView(this);
                                    dataText1.setPadding(10, 10, 10, 10);
                                    dataText1.setText(visitDate);
                                    dataRow.addView(dataText1);

                                    // Create TextView for visit date
                                    TextView dataText2 = new TextView(this);
                                    dataText2.setPadding(10, 10, 10, 10);
                                    dataText2.setText(quotaID);
                                    dataRow.addView(dataText2);

                                    TextView dataText3 = new TextView(this);
                                    dataText3.setPadding(10, 10, 10, 10);
                                    dataText3.setText(empID);
                                    dataRow.addView(dataText3);

                                    table1.addView(dataRow);
                                }

                            } while (memberDataCursor.moveToNext());
                        } else {
                            // No data in memberDataCursor
                            TextView noDataTextView = new TextView(this);
                            noDataTextView.setText("ไม่มีข้อมูล");
                            table1.addView(noDataTextView);
                        }

                        if (memberDataCursor != null) {
                            memberDataCursor.close();
                        }
                    } else {
                        // No visit_member_data table
                        TextView noDataTextView = new TextView(this);
                        noDataTextView.setText("ไม่มีตาราง ตรวจเยี่ยมสมาชิก");
                        table1.addView(noDataTextView);
                    }

                    if (isTableExists(db, "visit_farm_data")) {
                        Cursor farmDataCursor = db.query("visit_farm_data", farmColumns, null, null, null, null, null);

                        if (farmDataCursor != null && farmDataCursor.moveToFirst()) {
                            do {
                                // Read data from Cursor
                                int visitFarmDateIndex = farmDataCursor.getColumnIndex("VisitFarm_Date");
                                int farmIDIndex = farmDataCursor.getColumnIndex("Farm_ID");
                                int empIDIndex = farmDataCursor.getColumnIndex("Emp_ID");
                                Log.e("Check", "Check If visitFarmDateIndex: " + visitFarmDateIndex);
                                Log.e("Check", "Check If farmIDIndex: " + farmIDIndex);
                                Log.e("Check", "Check If empIDIndex: " + empIDIndex);

                                if (visitFarmDateIndex != -1 && farmIDIndex != -1 && empIDIndex != -1) {
                                    Log.e("Check", "Check If farmIDIndex");
                                    String visitFarmDate = farmDataCursor.getString(visitFarmDateIndex);
                                    String farmID = farmDataCursor.getString(farmIDIndex);
                                    String empID = farmDataCursor.getString(empIDIndex);

                                    // Create new TableRow for farm data
                                    TableRow dataRow2 = new TableRow(this);
                                    dataRow2.setBackgroundColor(Color.parseColor("#EEEEEE"));

                                    // Create TextView for visit farm date
                                    TextView dataText4 = new TextView(this);
                                    dataText4.setPadding(10, 10, 10, 10);
                                    dataText4.setText(visitFarmDate);
                                    dataRow2.addView(dataText4);

                                    // Create TextView for farm ID
                                    TextView dataText5 = new TextView(this);
                                    dataText5.setPadding(10, 10, 10, 10);
                                    dataText5.setText(farmID);
                                    dataRow2.addView(dataText5);

                                    // Create TextView for employee ID
                                    TextView dataText6 = new TextView(this);
                                    dataText6.setPadding(10, 10, 10, 10);
                                    dataText6.setText(empID);
                                    dataRow2.addView(dataText6);

                                    table2.addView(dataRow2);
                                }
                            } while (farmDataCursor.moveToNext());
                        } else {
                            // No data in farmDataCursor
                            TextView noDataTextView = new TextView(this);
                            noDataTextView.setText("ไม่มีข้อมูล");
                            table2.addView(noDataTextView);
                        }

                        if (farmDataCursor != null) {
                            farmDataCursor.close();
                        }
                    } else {
                        // No visit_farm_data table
                        TextView noDataTextView = new TextView(this);
                        noDataTextView.setText("ไม่มีตาราง ตรวจเยี่ยมแปลงสมาชิก");
                        table2.addView(noDataTextView);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e("Check", "Check Error Ex : " + e.getMessage());
                } finally {
                    if (db != null) {
                        db.close();
                    }
                    obj.close();
                }
            } else {
                Log.e("Check", "Database file does not exist");
            }


        } else if (data.equals(DelPathFarmGeo.toString())) {

        } else if (data.equals(DelPathRecGirth.toString())) {

        } else if (data.equals(DelPathCoorHouse.toString())) {

        } else if (data.equals(DelPathCoorImportance.toString())) {

        }


    }

}
