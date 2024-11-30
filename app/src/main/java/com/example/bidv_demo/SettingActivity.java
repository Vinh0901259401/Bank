package com.example.bidv_demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingActivity extends AppCompatActivity {

    private Button ChangeAvatar, goToBack, Exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_rewards) {
                    Toast.makeText(SettingActivity.this, "Đổi quà selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_qr) {
                    Toast.makeText(SettingActivity.this, "Quét QR selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    Intent intent = new Intent(SettingActivity.this, NotificationActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    if (!(SettingActivity.this instanceof SettingActivity)) {
                        Intent intent = new Intent(SettingActivity.this, SettingActivity.class);
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);


        // Find the logout button
        Exit = findViewById(R.id.btn_ExitAccount);
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear login state
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putBoolean("isPaused", false);
                editor.putString("username", "");
                editor.apply();

                // Navigate to MainActivity
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity
            }
        });

    }
}
