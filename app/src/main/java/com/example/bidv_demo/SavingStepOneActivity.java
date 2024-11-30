package com.example.bidv_demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SavingStepOneActivity extends AppCompatActivity {
    private LinearLayout btn_step2_saving_1;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_step_one);

        dbHelper = new DatabaseHelper(this);


        btn_step2_saving_1 = findViewById(R.id.btn_open_saving);
        btn_step2_saving_1.setOnClickListener(v -> {
            Intent intent = new Intent(SavingStepOneActivity.this, SavingStepTwo_01_Acitivity.class);
            startActivity(intent);
        });
    }
}
