package com.example.gear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.log4j.chainsaw.Main;

import java.util.ArrayList;
import java.util.List;

public class AddPlanActivity1 extends AppCompatActivity {
    CircleImageView btn_back;
    private LinearLayout parentLinearLayout;
    TextView number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan1);

        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddPlanActivity1.this, MainActivity.class));
            }
        });


    }

    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.add_equitment_layout, null);

        int childcount = parentLinearLayout.getChildCount()-10;
        for (int i = 0; i <= childcount; i++) {

            number = (TextView) view.findViewById(R.id.number_of_equipment);
            number.setText("Equipment " + i);

        }
        parentLinearLayout.addView(view, parentLinearLayout.getChildCount() - 1);
    }
}
