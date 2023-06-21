package com.panelplus.pnpmember.activity;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.panelplus.pnpmember.R;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivityFarmActivity extends AppCompatActivity implements View.OnClickListener {

    String Farm_ID = null;
    Button btnVisitFarm,btnEditFarm,btnRecCir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_activity_farm);

        InitWidget();

        Bundle b = getIntent().getExtras();
        Farm_ID = b.getString("Farm_ID");
        Log.d("Farm_ID: ",Farm_ID);

        setTitle("กิจกรรมแปลงยางพารา (รหัสแปลง : "+Farm_ID+")");
    }

    private void InitWidget() {
        btnEditFarm = (Button) findViewById(R.id.btnEditFarm);
        btnVisitFarm = (Button) findViewById(R.id.btnVisitFarm);
        btnRecCir = (Button) findViewById(R.id.btnRecCir);

        btnEditFarm.setOnClickListener(this);
        btnVisitFarm.setOnClickListener(this);
        btnRecCir.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnEditFarm:
                //TODO Edit Farm
                Intent editFarmPage = new Intent(MenuActivityFarmActivity.this,
                        EditFarmActivity.class);

                Bundle b1 = new Bundle();
                b1.putString("Farm_ID",Farm_ID);
                editFarmPage.putExtras(b1);

                startActivity(editFarmPage);
                break;

            case R.id.btnVisitFarm:
                Intent visitFarmPage = new Intent(MenuActivityFarmActivity.this, VisitFarmActivity.class);
                Bundle b2 = new Bundle();
                b2.putString("Farm_ID",Farm_ID);
                visitFarmPage.putExtras(b2);

                startActivity(visitFarmPage);
                break;

            case  R.id.btnRecCir:
                Intent recGirthPage = new Intent(MenuActivityFarmActivity.this, RecGirthActivity.class);
                Bundle b3 = new Bundle();
                b3.putString("Farm_ID",Farm_ID);
                recGirthPage.putExtras(b3);
                startActivity(recGirthPage);

                break;

        }

    }
}
