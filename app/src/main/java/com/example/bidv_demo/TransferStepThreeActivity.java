package com.example.bidv_demo;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransferStepThreeActivity extends AppCompatActivity {

    private TextView MoneyNeedTransfer, NameUserNeedTransfer, PhoneUserNeedTransfer, SumMoneyNeedTransfer, MessageUserNeedTransfer;
    private DatabaseHelper dnHelper;
    private String loggedInPhoneNumber;
    private Button transferButton;
    private long TransactionId, NotificationId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trasfer_step_three);
        dnHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        String amount = intent.getStringExtra("amount");
        String Message = intent.getStringExtra("message");
        String PhoneNeedTransfer = intent.getStringExtra("phoneNeedTransfer");

        MoneyNeedTransfer = findViewById(R.id.MoneyNeedTransfer);
        NameUserNeedTransfer = findViewById(R.id.nameUserNeedTransfer);
        PhoneUserNeedTransfer = findViewById(R.id.phoneNeedTransfer);
        MessageUserNeedTransfer = findViewById(R.id.messageNeedTransfer);
        SumMoneyNeedTransfer = findViewById(R.id.SumMoneyNeedTransfer);
        UserNeedStransfer user = getUserNeedStransfer(PhoneNeedTransfer);
        loggedInPhoneNumber = getLoggedInUserPhone();



        MoneyNeedTransfer.setText(amount);
        MessageUserNeedTransfer.setText(Message);
        PhoneUserNeedTransfer.setText(PhoneNeedTransfer);
        NameUserNeedTransfer.setText(user.getFirstName() + " " + user.getLastName());
        SumMoneyNeedTransfer.setText(amount);


        //Chức năng chuyển tiền
        transferButton = findViewById(R.id.transferButton);
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String phoneNumber = getIntent().getStringExtra("phoneNeedTransfer");
                    if (phoneNumber == null) {
                        throw new NullPointerException("phoneNeedTransfer is null");
                    }

                    String amountStr = ((TextView) findViewById(R.id.SumMoneyNeedTransfer)).getText().toString();
                    // Remove non-numeric characters
                    amountStr = amountStr.replaceAll("[^\\d.]", "");
                    if (!isValidDecimal(amountStr)) {
                        showAlertDialog("Error", "Số tiền không hợp lệ.");
                        return;
                    }

                    BigDecimal amount = new BigDecimal(amountStr);
                    String description = Message;
                    int fromUserId = dnHelper.getUserIdByPhone(loggedInPhoneNumber);
                    int toUserId = dnHelper.getUserIdByPhone(phoneNumber);

                    if (fromUserId == -1 || toUserId == -1) {
                        showAlertDialog("Error", "Không tìm thấy người dùng.");
                        return;
                    }

                    createTransaction(fromUserId, toUserId, amount, description);
                    updateBalances(fromUserId, toUserId, amount);
                    createNotification();
                    createNotificationLogFormID(fromUserId, NotificationId, TransactionId, amount);
                    createNotificationLogToID(toUserId,fromUserId, NotificationId, TransactionId, amount);


                    new AlertDialog.Builder(TransferStepThreeActivity.this)
                            .setTitle("Success")
                            .setMessage("Giao dịch thành công")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(TransferStepThreeActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlertDialog("Error", "Đã xảy ra lỗi: " + e.getMessage());
                }
            }
        });
    }

    private UserNeedStransfer getUserNeedStransfer(String phoneNumber) {
        User user = dnHelper.getUser(phoneNumber);
        if (user != null) {
            return new UserNeedStransfer(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getAddress(),
                    user.getPassword(),
                    user.getAvatar(),
                    user.getBirthYear(),
                    user.isLocked(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getBalance(),
                    user.getDescription()
            );
        }
        return null;
    }

    private String getLoggedInUserPhone() {
        SharedPreferences prefs = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", "");
    }

    private void createTransaction(int fromUserId, int toUserId, BigDecimal amount, String description) {
        SQLiteDatabase db = dnHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("from_user_id", fromUserId);
        values.put("to_user_id", toUserId);
        values.put("type", "Transfer");
        values.put("amount", amount.toString());
        values.put("created_at", new Timestamp(System.currentTimeMillis()).toString());
        values.put("status", "Completed");
        values.put("description", description.isEmpty() ? "Không có lời nhắn" : description);
        db.insert("Transactions", null, values);
        TransactionId = db.insert("Transactions", null, values);
    }

    private void updateBalances(int fromUserId, int toUserId, BigDecimal amount) {
        SQLiteDatabase db = dnHelper.getWritableDatabase();
        db.execSQL("UPDATE Users SET balance = balance - ? WHERE id = ?", new Object[]{amount, fromUserId});
        db.execSQL("UPDATE Users SET balance = balance + ? WHERE id = ?", new Object[]{amount, toUserId});
    }

    private boolean isValidDecimal(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void createNotification() {
        // Create Notification
        SQLiteDatabase db = dnHelper.getWritableDatabase();
        ContentValues notificationValues = new ContentValues();
        notificationValues.put("type", "Transaction");
        notificationValues.put("title", "Chuyển tiền/nhận tiền thành công");
        notificationValues.put("created_at", new Timestamp(System.currentTimeMillis()).toString());

        NotificationId = db.insert("Notifications", null, notificationValues);

    }

    private void createNotificationLogFormID(int fromUserId, long notificationId, long transactionId, BigDecimal amount) {
        SQLiteDatabase db = dnHelper.getWritableDatabase();
        // Get current balance of the user
        Cursor cursor = db.rawQuery("SELECT balance FROM Users WHERE id = ?", new String[]{String.valueOf(fromUserId)});
        BigDecimal currentBalance = BigDecimal.ZERO;
        if (cursor != null && cursor.moveToFirst()) {
            currentBalance = new BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow("balance")));
            cursor.close();
        }

        // Create Notification Log
        ContentValues logValues = new ContentValues();
        logValues.put("notification_id", notificationId);
        logValues.put("user_id", fromUserId);
        logValues.put("transaction_id", transactionId);
        logValues.put("current_balance", currentBalance.toString());
        logValues.put("description", "Bạn đã giao dịch thành công số tiền " + amount.toString());
        logValues.put("title", "Chuyển khoản thành công cho SDT " + getIntent().getStringExtra("phoneNeedTransfer"));
        db.insert("Notifications_Logs", null, logValues);
    }

    private void createNotificationLogToID(int toUserId, int fromUserId, long notificationId, long transactionId, BigDecimal amount) {
        SQLiteDatabase db = dnHelper.getWritableDatabase();
        // Get current balance and phone number of the user
        Cursor cursor = db.rawQuery("SELECT balance, phone FROM Users WHERE id = ?", new String[]{String.valueOf(toUserId)});
        Cursor cursor2 = db.rawQuery("SELECT phone FROM Users WHERE id = ?", new String[]{String.valueOf(fromUserId)});
        BigDecimal currentBalance = BigDecimal.ZERO;
        String phoneNumber = "";
        if (cursor != null && cursor.moveToFirst()) {
            currentBalance = new BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow("balance")));
            cursor.close();
        }
        if (cursor2 != null && cursor2.moveToFirst()) {
            phoneNumber = cursor2.getString(cursor2.getColumnIndexOrThrow("phone"));
            cursor2.close();
        }

        // Create Notification Log
        ContentValues logValues = new ContentValues();
        logValues.put("notification_id", notificationId);
        logValues.put("user_id", toUserId);
        logValues.put("transaction_id", transactionId);
        logValues.put("current_balance", currentBalance.toString());
        logValues.put("description", "Bạn đã nhận được số tiền " + amount.toString());
        logValues.put("title", "Nhận tiền thành công từ SDT " + phoneNumber);
        db.insert("Notifications_Logs", null, logValues);
    }

}