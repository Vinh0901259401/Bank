package com.example.bidv_demo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SavingStepTwo_01_Acitivity extends AppCompatActivity {
    private LinearLayout btn_chossemonth, btn_choosephuongthuc;
    private TextView TextMonth, TextLaiXuat;
    private String[] months = {"1 tháng", "2 tháng", "3 tháng", "4 tháng", "5 tháng", "6 tháng", "7 tháng", "8 tháng", "9 tháng", "10 tháng", "11 tháng", "12 tháng"};
    private double[] interestRates = {2.2, 2.6, 2.8, 3.1, 3.4, 3.6, 3.8, 4.0, 4.2, 4.4, 4.6, 5.0};
    private String[] PhuongThuc = {"Tại quầy", "Online + tại quầy"};
    private TextView phoneNumberTextView, accountBalanceTextView, TextPhuongThucRut, TextMessage;
    private User DatabaseUser;
    private DatabaseHelper dbHelper;
    private EditText moneyInput;
    private long SavingAccountId, NotificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_step_two_1);

        TextLaiXuat = findViewById(R.id.TextLaiXuat);
        btn_chossemonth = findViewById(R.id.ChooseMonth);
        btn_chossemonth.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SavingStepTwo_01_Acitivity.this);
            builder.setTitle("Chọn kỳ hạn");
            builder.setItems(months, (dialog, which) -> {
                TextMonth = findViewById(R.id.TextMonth);
                TextMonth.setText(months[which]);
                TextMonth.setVisibility(View.VISIBLE);
                TextLaiXuat.setText(String.format(Locale.US, "%.1f%%", interestRates[which]));
            });
            builder.show();
        });

        btn_choosephuongthuc = findViewById(R.id.ChoosePhuongThucRut);
        btn_choosephuongthuc.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SavingStepTwo_01_Acitivity.this);
            builder.setTitle("Chọn phương thức tiết kiệm");
            builder.setItems(PhuongThuc, (dialog, which) -> {
                TextMonth = findViewById(R.id.TextPhuongThucRut);
                TextMonth.setText(PhuongThuc[which]);
                TextMonth.setVisibility(View.VISIBLE);
            });
            builder.show();
        });

        moneyInput = findViewById(R.id.MoneyInput);
        moneyInput.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    moneyInput.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getNumberInstance(Locale.US).format(parsed) + " VND";
                        current = formatted;
                        moneyInput.setText(formatted);
                        moneyInput.setSelection(formatted.length() - 4);
                    }

                    moneyInput.addTextChangedListener(this);
                }

                Button btnOpenSaving = findViewById(R.id.btnOpenSaving);
                btnOpenSaving.setOnClickListener(v -> {
                    OpenSavingBagAccount();
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        moneyInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String cleanString = moneyInput.getText().toString().replaceAll("[^\\d]", "");
                if (!cleanString.isEmpty()) {
                    double parsed = Double.parseDouble(cleanString);
                    if (parsed < 1000000) {
                        showMinimumAmountDialog("Số tiền gửi tối thiểu là 1,000,000 VND");
                    }
                }
            }
            return false;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String Checkusername = sharedPreferences.getString("username", "");
        String Checkpassword = sharedPreferences.getString("password", "");

        dbHelper = new DatabaseHelper(this);
        DatabaseUser = dbHelper.getUser(Checkusername, Checkpassword);
        phoneNumberTextView = findViewById(R.id.phoneNumber);
        accountBalanceTextView = findViewById(R.id.accountBalance);

        phoneNumberTextView.setText(String.format("Số tài khoản: " + DatabaseUser.getPhone()));
        accountBalanceTextView.setText(String.format("Số dư: %s VND", formatBalance(DatabaseUser.getBalance())));

    }
    private String formatBalance(BigDecimal balance) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(balance);
    }
    private void showMinimumAmountDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage(message);
        // Cài đặt nút OK
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Lấy ra AlertDialog sau khi tạo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void OpenSavingBagAccount() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String Checkusername = sharedPreferences.getString("username", "");
            String Checkpassword = sharedPreferences.getString("password", "");

            dbHelper = new DatabaseHelper(this);
            DatabaseUser = dbHelper.getUser(Checkusername, Checkpassword);

            String money = moneyInput.getText().toString();
            String cleanString = moneyInput.getText().toString().replaceAll("[^\\d]", "");
            double savingBalance = Double.parseDouble(cleanString);

            TextPhuongThucRut = findViewById(R.id.TextPhuongThucRut);
            TextMessage = findViewById(R.id.MessageInput_saving);
            TextLaiXuat = findViewById(R.id.TextLaiXuat);
            String withdrawalMethod = TextPhuongThucRut.getText().toString();
            String note = TextMessage.getText().toString();
            String laiXuatString = TextLaiXuat.getText().toString().replace("%", "").trim();
            double interestRate = laiXuatString.isEmpty() ? 0 : Double.parseDouble(laiXuatString);

            TextMonth = findViewById(R.id.TextMonth);
            String selectedMonth = TextMonth.getText().toString().replace(" tháng", "").trim();
            int SelectMonth = selectedMonth.isEmpty() ? 0 : Integer.parseInt(selectedMonth);

            Calendar calendar = Calendar.getInstance();
            String startDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(calendar.getTime());
            calendar.add(Calendar.MONTH, SelectMonth + 1);
            String maturityDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(calendar.getTime());


            if (savingBalance > DatabaseUser.getBalance().doubleValue()) {
                showMinimumAmountDialog("Số dư không đủ");
            } else if (money.isEmpty() || savingBalance < 1000000) {
                showMinimumAmountDialog("Số tiền gửi tối thiểu là 1,000,000 VND");
            } else if (selectedMonth.isEmpty()) {
                showMinimumAmountDialog("Vui lòng chọn kỳ hạn");
            } else if (withdrawalMethod.isEmpty()) {
                showMinimumAmountDialog("Vui lòng chọn phương thức rút tiền");
            } else {
                // Insert new saving account into the database
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("user_id", DatabaseUser.getId());
                values.put("savings_balance", savingBalance);
                values.put("interest_rate", interestRate);
                values.put("start_date", startDate);
                values.put("maturity_date", maturityDate);
                values.put("is_active", 1);
                values.put("Withdrawal_method", withdrawalMethod);
                values.put("note", note);

                SavingAccountId = db.insert("Savings_Accounts", null, values);

                // Update user's balance
                ContentValues userValues = new ContentValues();
                userValues.put("balance", DatabaseUser.getBalance().subtract(BigDecimal.valueOf(savingBalance)).toString());
                db.update("Users", userValues, "id = ?", new String[]{String.valueOf(DatabaseUser.getId())});

                // Show success message and create notification
                Toast.makeText(this, "Tạo tài khoản tiết kiệm thành công", Toast.LENGTH_SHORT).show();
                createNotification(SavingAccountId);

                // Navigate to MainActivity
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } catch (Exception e) {
            // Show error dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("An error occurred: " + e.getMessage() + "\n\n" + Log.getStackTraceString(e));
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }


    //Tạo thông báo đăng ký tài khoản tiết kiệm thành công
    private void createNotification(long SavingAccountId) {
        // Create Notification
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues notificationValues = new ContentValues();
        notificationValues.put("SavingAccount_id", SavingAccountId);
        notificationValues.put("type", "Saving_Account");
        notificationValues.put("title", "Đã tạo thành công tài khoản tiết kiệm");
        notificationValues.put("created_at", new Timestamp(System.currentTimeMillis()).toString());

        db.insert("Notifications", null, notificationValues);

    }


}
