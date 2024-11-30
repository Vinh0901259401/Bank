package com.example.bidv_demo;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPhone, etAddress, etPassword, etFirstName, etLastName, etBirthYear;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etBirthYear = findViewById(R.id.etBirthYear);
        btnRegister = findViewById(R.id.btnRegister);

        etBirthYear.setOnClickListener(v -> showDatePickerDialog());

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
            etBirthYear.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void registerUser() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();
        String password = etPassword.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String birthYear = etBirthYear.getText().toString();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || birthYear.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ các trường", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("phone", phone);
        values.put("address", address);
        values.put("password", password);
        values.put("avatar", "user.png"); // Default avatar
        values.put("birth_year", birthYear);
        values.put("is_locked", 0); // Default value
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("balance", 0.00); // Default balance
        values.put("description", "Hãy nhập ghi chú của bạn"); // Default description

        long newRowId = db.insert("Users", null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show();
        }
    }
}