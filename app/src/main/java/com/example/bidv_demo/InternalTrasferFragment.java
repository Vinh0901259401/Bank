package com.example.bidv_demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InternalTrasferFragment extends Fragment {

    private DatabaseHelper dnHelper;
    private EditText editTextPhoneNumber;
    private TextView textViewUserName;
    private String loggedInPhoneNumber;
    private Button btnNextStepTwo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_internal_transfer, container, false);

        dnHelper = new DatabaseHelper(getContext());
        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumberInternal);
        textViewUserName = view.findViewById(R.id.textViewUserName);
        loggedInPhoneNumber = getLoggedInUserPhone();





        editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchUserName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnNextStepTwo = view.findViewById(R.id.buttonContinue_step_two);
        btnNextStepTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextPhoneNumber.getText().toString();
                String UserName = textViewUserName.getText().toString();

                if (phoneNumber.isEmpty()) {
                    showAlertDialog("Error", "Chưa nhận được người cần giao dịch.");
                }else if(phoneNumber.equals(loggedInPhoneNumber)) {
                    showAlertDialog("Error", "Không được giao dịch với chính mình.");
                }else if(UserName.equals("Không tìm thấy tên người dùng")) {
                    showAlertDialog("Error", "Số điện thoại không tồn tại.");
                } else {
                    Intent intent = new Intent(requireContext(), TransferStepTwoActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                }
            }
        });

        return view;



    }


    private void SearchUserName(String Phonenumber) {
        String userName = dnHelper.getFullNamebyPhone(Phonenumber);
        if (userName != null){
            textViewUserName.setText(userName);
        } else {
            textViewUserName.setText("Không tìm thấy tên người dùng");
        }
    }


    private String getLoggedInUserPhone() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", "");
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
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
}