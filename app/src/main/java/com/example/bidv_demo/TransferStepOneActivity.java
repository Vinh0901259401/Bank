package com.example.bidv_demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TransferStepOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trasfer_step_one);

        ImageView iconBack = findViewById(R.id.icon_back_transfer);
        iconBack.setOnClickListener(v -> {
            Intent intent = new Intent(TransferStepOneActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout_transfer);
        ViewPager2 viewPager = findViewById(R.id.viewPager_transfer);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Nội bộ");
                    break;
                case 1:
                    tab.setText("Ngoại bộ");
                    break;
            }
        }).attach();
    }
}
