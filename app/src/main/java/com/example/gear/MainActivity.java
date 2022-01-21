package com.example.gear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.log4j.chainsaw.Main;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    AutoCompleteTextView editSite;
    AppCompatButton mDownload,mViewonMap, mAddPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        editSite= findViewById(R.id.site);
        String []option = {"Semua Site","ABKL", "ARIA", "ASMI", "BAYA", "BEKB", "BRCB", "BRCG",
                "INDO", "KIDE", "KPCB","KPCS","KPCT","MTBU","SMMS","TCMM"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_label, option);
        editSite.setText(arrayAdapter.getItem(0).toString(), false);
        editSite.setAdapter(arrayAdapter);

        mDownload= findViewById(R.id.download);
        mViewonMap = findViewById(R.id.viewonmap);

        mAddPlan = findViewById(R.id.addplan);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nv_home);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.AllPlan:
                        startActivity(new Intent(getApplicationContext(), AllPlanActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    public void ClickNav(View view){
        openDrawer(drawerLayout);
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }


    public void Keluar(View view){
        logout(this);
    }

    private void logout(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Keluar");
        builder.setMessage("Apakah anda yakin untuk keluar?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finishAffinity();
                //System.exit(0);
                startActivity(new Intent(MainActivity.this,Login.class));
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void ViewonMap(View view) {
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    public void AddPlan(View view) {
        startActivity(new Intent(MainActivity.this, AddPlanActivity1.class));
    }
}