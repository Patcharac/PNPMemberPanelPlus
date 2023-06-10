package com.panelplus.pnpmember.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.panelplus.pnpmember.R;

import androidx.appcompat.app.AppCompatActivity;

public class MenuFarmActivity extends AppCompatActivity implements View.OnClickListener {

    //Button btnAddFarm;
    Button btnViewFarm;
    Button btnSketchFarm;
    Button btnRecCir;
    Button btnCoHouse;
    Button btnCoImportance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_farm);

        InitWidget();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.btnAddFarm :
//                Intent regisFarmPage= new Intent(MenuFarmActivity.this,
//                        RegisFarmActivity.class);
//                startActivity(regisFarmPage);
//                break;


            case R.id.btnViewFarm :
                Intent viewFarmPage= new Intent(MenuFarmActivity.this,
                        ViewFarmActivity.class);
                startActivity(viewFarmPage);
                break;

            case R.id.btnSketchFarm :
                Intent sketchFarmPage= new Intent(MenuFarmActivity.this,
                        RegisFarmActivity.class);
                startActivity(sketchFarmPage);
                break;

            case R.id.RecCir:
                Intent visitRecCirPage = new Intent(MenuFarmActivity.this,
                        RecGirthActivity.class);
                Bundle b = new Bundle();
                b.putString("Status","NewMember");
                visitRecCirPage.putExtras(b);

                startActivity(visitRecCirPage);

                break;

            case R.id.btnCoHouse:
                Intent coordinateHousePage = new Intent(MenuFarmActivity.this,
                        CoordinateHouseActivity.class);

                startActivity(coordinateHousePage);
                break;

            case R.id.btnCoImportance:
                Intent coordinateImportancePage = new Intent(MenuFarmActivity.this,
                        CoordinateImportantActivity.class);

                startActivity(coordinateImportancePage );
                break;



        }
    }

    private void InitWidget() {
        //btnAddFarm = (Button) findViewById(R.id.btnAddFarm);
        btnViewFarm = (Button) findViewById(R.id.btnViewFarm);
        btnSketchFarm = (Button) findViewById(R.id.btnSketchFarm);
        btnRecCir = (Button) findViewById(R.id.RecCir);
        btnCoHouse = (Button) findViewById(R.id.btnCoHouse);
        btnCoImportance = (Button) findViewById(R.id.btnCoImportance);


        //btnAddFarm.setOnClickListener(this);
        btnViewFarm.setOnClickListener(this);
        btnSketchFarm.setOnClickListener(this);
        btnRecCir.setOnClickListener(this);
        btnCoHouse.setOnClickListener(this);
        btnCoImportance.setOnClickListener(this);

    }

    @Override
    public void onResume(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        super.onResume();

    }
}
