package com.example.bidv_demo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.widget.Toast;


public class NotificationActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private TextView tvDate;
    private TextView tvNotificationContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        tabLayout = findViewById(R.id.tabLayout);
        tvDate = findViewById(R.id.tv_date);
        tvNotificationContent = findViewById(R.id.tv_notification_content);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tvNotificationContent.setText("Nội dung khuyến mãi...");
                        break;
                    case 1:
                        tvNotificationContent.setText("Nội dung biến động...");
                        break;
                    case 2:
                        tvNotificationContent.setText("Tin khác...");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_rewards) {
                    Toast.makeText(NotificationActivity.this, "Đổi quà selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_qr) {
                    Toast.makeText(NotificationActivity.this, "Quét QR selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    if (!(NotificationActivity.this instanceof NotificationActivity)) {
                        Intent intent = new Intent(NotificationActivity.this, NotificationActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    Intent intent = new Intent(NotificationActivity.this, SettingActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_notifications);

    }
}
