package com.example.bidv_demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import android.app.Dialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;


public class TransferStepTwoActivity extends AppCompatActivity {

    private Button btn_nextStepThree;
    private EditText amountEditText;
    private EditText messageEditText;
    private String numericValue = "";
    private User DatabaseUser;
    private DatabaseHelper dbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trasfer_step_two);

        dbHelper = new DatabaseHelper(this);
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("phoneNumber");

        UserNeedStransfer user = getUserNeedStransfer(phoneNumber);
        TextView nameTextView = findViewById(R.id.nameUserNeedTransfer);
        TextView phoneTextView = findViewById(R.id.phoneUserNeedTransfer);
        ImageView avatarImageView = findViewById(R.id.avatarUserNeedTransfer);
        ImageButton backButton = findViewById(R.id.backStepOne);
        amountEditText = findViewById(R.id.MoneyInput);
        btn_nextStepThree = findViewById(R.id.btn_nextStepThree);


        nameTextView.setText(user.getFirstName() + " " + user.getLastName());
        phoneTextView.setText(user.getPhone());
        String avatarName = user.getAvatar();
        if (avatarName != null && avatarName.contains(".")) {
            avatarName = avatarName.substring(0, avatarName.lastIndexOf('.'));
        }
        int avatarResId = getResources().getIdentifier(avatarName, "drawable", getPackageName());
        avatarImageView.setImageResource(avatarResId);

        //Nút quay lại stepOne
        backButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(TransferStepTwoActivity.this, TransferStepOneActivity.class);
            startActivity(intent1);
        });
        //Xử lý nhập tin nhắn từ tin nhắn có sẵn
        TextView inputText1 = findViewById(R.id.InputText1);
        TextView inputText2 = findViewById(R.id.InputText2);
        TextView inputText3 = findViewById(R.id.InputText3);
        TextView inputText4 = findViewById(R.id.InputText4);
        EditText messageInput = findViewById(R.id.messageInput);
        View.OnClickListener inputTextClickListener = v -> {
            TextView textView = (TextView) v;
            messageInput.setText(textView.getText().toString());
        };
        inputText1.setOnClickListener(inputTextClickListener);
        inputText2.setOnClickListener(inputTextClickListener);
        inputText3.setOnClickListener(inputTextClickListener);
        inputText4.setOnClickListener(inputTextClickListener);
        //Xử lý nhập tin nhắn từ danh sách tin nhắn có sẵn trong textview Khác
        TextView InputMore = findViewById(R.id.InputMore);
        InputMore.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_input_more, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            TextView option1 = bottomSheetView.findViewById(R.id.option1);
            TextView option2 = bottomSheetView.findViewById(R.id.option2);
            TextView option3 = bottomSheetView.findViewById(R.id.option3);
            // Add more options as needed

            View.OnClickListener optionClickListener = view -> {
                TextView textView = (TextView) view;
                messageInput.setText(textView.getText().toString());
                bottomSheetDialog.dismiss();
            };

            option1.setOnClickListener(optionClickListener);
            option2.setOnClickListener(optionClickListener);
            option3.setOnClickListener(optionClickListener);
            // Set click listeners for more options as needed

            bottomSheetDialog.show();
        });
        //Xử lý nhập tiền
        amountEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    amountEditText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getNumberInstance(Locale.US).format(parsed);

                        current = formatted;
                        numericValue = cleanString;
                        amountEditText.setText(formatted + " VNĐ");
                        amountEditText.setSelection(formatted.length());
                    } else {
                        current = "0";
                        numericValue = "0";
                        amountEditText.setText("0 VNĐ");
                        amountEditText.setSelection(1);
                    }

                    amountEditText.addTextChangedListener(this);
                }
            }
        });
        // sang step 3
        btn_nextStepThree.setOnClickListener(v -> {
            try {
            String amountStr = ((TextView) findViewById(R.id.MoneyInput)).getText().toString();
            String amountStr_save = amountStr;
            String message = ((TextView) findViewById(R.id.messageInput)).getText().toString();
            String phoneNeedTransfer = phoneNumber;
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String Checkusername = sharedPreferences.getString("username", "");
            String Checkpassword = sharedPreferences.getString("password", "");
            DatabaseUser = dbHelper.getUser(Checkusername, Checkpassword);


            if (amountStr.isEmpty()) {
                showErrorDialog("Vui lòng nhập số tiền");
                return;
            }

            amountStr = amountStr.replaceAll("[^\\d.]", "");
            if (!isValidDecimal(amountStr)) {
                return;
            }

            BigDecimal amount = new BigDecimal(amountStr);
            BigDecimal userBalanceBigDecimal = DatabaseUser.getBalance();
            double amountDouble = amount.doubleValue();
            double userBalance = userBalanceBigDecimal.doubleValue();

            if (amountDouble > userBalance) {
                showErrorDialog("Số dư không đủ để thực hiện giao dịch");
                return;
            }
            
                Intent intent2 = new Intent(TransferStepTwoActivity.this, TransferStepThreeActivity.class);
                intent2.putExtra("amount", amountStr_save);
                intent2.putExtra("message", message);
                intent2.putExtra("phoneNeedTransfer", phoneNeedTransfer);
                startActivity(intent2);
            } catch (Exception e) {
                showErrorDialog("Đã xảy ra lỗi, vui lòng thử lại sau");
            }
        });
    }


    private UserNeedStransfer getUserNeedStransfer(String phoneNumber) {
        User user = dbHelper.getUser(phoneNumber);
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

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
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

}
