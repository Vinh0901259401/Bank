package com.example.bidv_demo;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Looper;
import android.widget.EditText;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.app.Dialog;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.os.Handler;
import android.view.MotionEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final long INACTIVITY_TIMEOUT = 300000; // 5 minutes
    private AppStateManager appStateManager;
    private LinearLayout loggedInLayout, buttonDangNhapAndDangKy, buttonThongTinTK, buttonDangNhapLai, btnTransfer, btnSaving;
    private static final String TAG = "MainActivity";
    private Button btnLogin, btnRegister, btnPause;
    private GridView gridView;
    private TextView userNameTextView;
    private DatabaseHelper dbHelper;
    private Handler inactivityHandler;
    private Runnable inactivityCallback;
    private User DatabaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Attempting to open database...");
        try (DatabaseHelper dbHelper = new DatabaseHelper(this);
             SQLiteDatabase db = dbHelper.getWritableDatabase()) {

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to create/open database", Toast.LENGTH_SHORT).show();
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Check chuyen trang thai
        appStateManager = new AppStateManager();
        appStateManager.setModeChangeListener(this::updateUIForMode);

        userNameTextView = findViewById(R.id.user_name);
        loggedInLayout = findViewById(R.id.loggedInLayout);
        buttonDangNhapAndDangKy = findViewById(R.id.buttonDangNhapAndDangKy);
        buttonThongTinTK = findViewById(R.id.buttonThongTinTK);
        buttonDangNhapLai = findViewById(R.id.buttonDangNhapLai);
        btnLogin = findViewById(R.id.buttonDangNhap);
        btnRegister = findViewById(R.id.btnRegister);
        btnPause = findViewById(R.id.btnPause);
        dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        boolean isPaused = sharedPreferences.getBoolean("isPaused", false);
        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", false);
        boolean hasReloaded = sharedPreferences.getBoolean("hasReloaded", false);


        // Kiểm tra 5p thì tự động cộng tiền vào tài khoản
        if (!isWorkScheduled()) {
            // Tạo và lên lịch công việc cập nhật số dư mỗi 15 phút
            PeriodicWorkRequest updateRequest = new PeriodicWorkRequest.Builder(UpdateSavingsWorker.class, 15, TimeUnit.MINUTES)
                    .build();

            // Đăng ký công việc
            WorkManager.getInstance(this).enqueue(updateRequest);
        }

        if (isLoggedIn && !isFirstLogin && !isPaused) {
            resetIsPaused();
        }


        // Bấm vào trang đăng ký
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        // Phần gridview chứa các mục chức năng
        gridView = findViewById(R.id.gridView_01);

        String[] itemNames = {"Tiết kiệm", "Đầu tư", "Vay vốn", "Bảo hiểm", "Thanh toán", "Chuyển tiền", "Giao thông", "Nạp tiền điện thoại", "Chuyển tiền quốc tế"};
        int[] itemImages = {R.drawable.money, R.drawable.money, R.drawable.money, R.drawable.money, R.drawable.money, R.drawable.money, R.drawable.money, R.drawable.money, R.drawable.money};

        GridAdapter adapter = new GridAdapter(this, itemNames, itemImages);
        gridView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    if (!(MainActivity.this instanceof MainActivity)) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else if (itemId == R.id.nav_rewards) {
                    Toast.makeText(MainActivity.this, "Đổi quà selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    if (appStateManager.getCurrentMode() == AppMode.NOT_LOGGED_IN) {
                        showLoginDialog();
                    } else if (appStateManager.getCurrentMode() == AppMode.PAUSED) {
                        showReLoginDialog();
                    } else {
                        Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                        startActivity(intent);
                        return true;
                    }
                } else if (itemId == R.id.nav_settings) {
                    if (appStateManager.getCurrentMode() == AppMode.NOT_LOGGED_IN) {
                        showLoginDialog();
                    } else if (appStateManager.getCurrentMode() == AppMode.PAUSED){
                        showReLoginDialog();
                    } else {
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });
        // Nút đăng nhập
        btnLogin.setOnClickListener(v -> showLoginDialog());
        // Nút đăng nhập lại
        btnPause.setOnClickListener(v -> showReLoginDialog());
        // Kiểm tra trạng thái đăng nhập
        String Checkusername = sharedPreferences.getString("username", "");
        String Checkpassword = sharedPreferences.getString("password", "");


        if (isPaused) {
            appStateManager.setCurrentMode(AppMode.PAUSED);
            DatabaseUser = dbHelper.getUser(Checkusername, Checkpassword);
            String fullName = dbHelper.getFullName(Checkusername, Checkpassword);
            updateUIForMode(appStateManager.getCurrentMode());
            userNameTextView.setText(fullName);
            updateUserInfo(DatabaseUser);
        } else if (isLoggedIn && !isPaused) {
            DatabaseUser = dbHelper.getUser(Checkusername, Checkpassword);
            String fullName = dbHelper.getFullName(Checkusername, Checkpassword);
            appStateManager.setCurrentMode(AppMode.ACTIVE);
            updateUIForMode(appStateManager.getCurrentMode());
            userNameTextView.setText(fullName);
            updateUserInfo(DatabaseUser);
        } else {
            appStateManager.setCurrentMode(AppMode.NOT_LOGGED_IN);
        }


        btnRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));

        inactivityHandler = new Handler(Looper.getMainLooper());
        inactivityCallback = () -> {
            if (appStateManager.getCurrentMode() == AppMode.ACTIVE) {
                appStateManager.setCurrentMode(AppMode.PAUSED);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isPaused", true);
                editor.apply();
                updateUIForMode(AppMode.PAUSED);
            }
        };


        resetInactivityTimer();

        //Chuyển sang trang chuyển tiền
        btnTransfer = findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TransferStepOneActivity.class)));

        btnSaving = findViewById(R.id.btnSaving);
        //Check DN trc khi chuyển trang button
        setButtonClickListeners();
    }



    private  void showLoginDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_login);
        dialog.show();

        EditText username = dialog.findViewById(R.id.editTextPhoneNumber);
        EditText password = dialog.findViewById(R.id.editTextPassword);
        Button btnLogin = dialog.findViewById(R.id.loginButton);
        TextView errorPhoneNumberText = dialog.findViewById(R.id.errorPhoneNumberText);
        TextView errorPasswordText = dialog.findViewById(R.id.errorPasswordText);

        btnLogin.setOnClickListener(v -> {
            String PhoneStr = username.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();

            boolean hasError = false;
            if (PhoneStr.isEmpty()) {
                errorPhoneNumberText.setText("Số điện thoại không được để trống");
                errorPhoneNumberText.setVisibility(View.VISIBLE);
                hasError = true;
            } else {
                errorPhoneNumberText.setVisibility(View.GONE);
            }

            if (passwordStr.isEmpty()) {
                errorPasswordText.setText("Mật khẩu không được để trống");
                errorPasswordText.setVisibility(View.VISIBLE);
                hasError = true;
            } else {
                errorPasswordText.setVisibility(View.GONE);
            }

            if (hasError) {
                return;
            }

            DatabaseUser = dbHelper.getUser(PhoneStr, passwordStr);
            if (dbHelper.checkUser(PhoneStr, passwordStr)) {
                Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putBoolean("isPaused", false);
                editor.putBoolean("isFirstLogin", true);
                editor.putString("username", PhoneStr);
                editor.putString("password", passwordStr);
                editor.apply();

                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(MainActivity.this, "Nhập lại mật khẩu hoặc số điện thoại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // hiển thị hộp thoại đăng nhập lại
    private void showReLoginDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_login_again);
        dialog.show();

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String Checkusername = sharedPreferences.getString("username", "");
        String Checkpassword = sharedPreferences.getString("password", "");

        DatabaseUser = dbHelper.getUser(Checkusername, Checkpassword);
        if (DatabaseUser != null) {
            String fullName = DatabaseUser.getFirstName() + " " + DatabaseUser.getLastName();
            String phoneNumber = DatabaseUser.getPhone();

            TextView nameUserLoginAgain = dialog.findViewById(R.id.NameUser_loginAgain);
            TextView phoneUserLoginAgain = dialog.findViewById(R.id.PhoneUser_loginAgain);

            nameUserLoginAgain.setText(fullName);
            phoneUserLoginAgain.setText(phoneNumber);
        }

        EditText password_again = dialog.findViewById(R.id.editTextPassword_again);
        Button btnLogin_again = dialog.findViewById(R.id.loginButton_again);

        btnLogin_again.setOnClickListener(v -> {
            String passwordStr = password_again.getText().toString();
            String storedPassword = sharedPreferences.getString("password", "");
            if (passwordStr.equals(storedPassword)) {
                Toast.makeText(MainActivity.this, "Đăng nhập lại thành công", Toast.LENGTH_SHORT).show();
                appStateManager.setCurrentMode(AppMode.ACTIVE);
                updateUIForMode(AppMode.ACTIVE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isPaused", false);
                editor.putBoolean("isFirstLogin", true);
                editor.apply();
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // cập nhật thông tin người dùng
    private void updateUserInfo(User DatabaseUser) {
        if (DatabaseUser != null) {
            TextView phoneNumberTextView = findViewById(R.id.phoneNumber);
            TextView accountBalanceTextView = findViewById(R.id.accountBalance);

            phoneNumberTextView.setText(String.format("Số tài khoản: " + DatabaseUser.getPhone()));
            accountBalanceTextView.setText(String.format("Số dư: %s VND", formatBalance(DatabaseUser.getBalance())));
        } else {
            // Handle the case where the user is not found
            TextView phoneNumberTextView = findViewById(R.id.phoneNumber);
            TextView accountBalanceTextView = findViewById(R.id.accountBalance);

            phoneNumberTextView.setText(R.string.SoTaiKhoangKhongTimThay);
            accountBalanceTextView.setText(R.string.SoDukhongTimThay);
        }
    }

    // định dạng số dư
    private String formatBalance(BigDecimal balance) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(balance);
    }

    // cập nhật giao diện người dùng cho chế độ ứng dụng
    private void updateUIForMode(AppMode mode) {
        switch (mode) {
            case NOT_LOGGED_IN:
                buttonDangNhapAndDangKy.setVisibility(View.VISIBLE);
                buttonThongTinTK.setVisibility(View.GONE);
                buttonDangNhapLai.setVisibility(View.GONE);
                break;
            case ACTIVE:
                buttonDangNhapAndDangKy.setVisibility(View.GONE);
                buttonThongTinTK.setVisibility(View.VISIBLE);
                buttonDangNhapLai.setVisibility(View.GONE);
                break;
            case PAUSED:
                buttonDangNhapAndDangKy.setVisibility(View.GONE);
                buttonThongTinTK.setVisibility(View.GONE);
                buttonDangNhapLai.setVisibility(View.VISIBLE);
                break;
        }
    }

    // tính thời gian chờ khi chuyển trang nhưng sẽ reset thời gian khi thực hiện sự kiện nào đó
    private void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityCallback);
        inactivityHandler.postDelayed(inactivityCallback, INACTIVITY_TIMEOUT);
    }

    // đặt thời thời gian không hoạt động
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        resetInactivityTimer();
        return super.dispatchTouchEvent(ev);
    }

    //kiểm tra trạng thái đăng nhập trước khi chuyển trang
    private void setButtonClickListeners() {
        View.OnClickListener buttonClickListener = v -> {
            if (appStateManager.getCurrentMode() == AppMode.NOT_LOGGED_IN) {
                showLoginDialog();
            } else if (appStateManager.getCurrentMode() == AppMode.ACTIVE) {
                if (v.getId() == R.id.btnTransfer) {
                    startActivity(new Intent(MainActivity.this, TransferStepOneActivity.class));
                } else if (v.getId() == R.id.btnSaving) {
                    startActivity(new Intent(MainActivity.this, SavingStepOneActivity.class));
                }
            } else if (appStateManager.getCurrentMode() == AppMode.PAUSED) {
                showReLoginDialog();
            }
        };

        btnLogin.setOnClickListener(buttonClickListener);
        btnTransfer.setOnClickListener(buttonClickListener);
        btnSaving.setOnClickListener(buttonClickListener);
        // Add other buttons here
    }

    private void resetIsPaused() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean hasReloaded = sharedPreferences.getBoolean("hasReloaded", false);
        boolean isPaused = sharedPreferences.getBoolean("isPaused", true);
        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isPaused", true);
        editor.apply();
        recreate();
    }

    private boolean isWorkScheduled() {
        // Kiểm tra trạng thái công việc đã được lên lịch chưa
        // Thực hiện kiểm tra logic ở đây nếu cần
        // Ví dụ: Trả về true nếu công việc đã được đăng ký trước đó
        return false;
    }
}