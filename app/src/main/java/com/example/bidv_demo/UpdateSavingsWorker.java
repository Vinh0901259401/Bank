package com.example.bidv_demo;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;

public class UpdateSavingsWorker extends Worker {

    private DatabaseHelper dbHelper;
    public UpdateSavingsWorker(Context context, WorkerParameters params) {
        super(context, params);
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public Result doWork() {
        try {
            // Thực hiện công việc cập nhật số dư tại đây
            Log.d("UpdateSavingsWorker", "Đang cập nhật số dư...");

            // Giả sử bạn muốn cập nhật số dư và lưu lại thời gian
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            long lastRunTime = sharedPreferences.getLong("lastRunTime", 0);
            long currentTime = System.currentTimeMillis();
            long fiveMinutesInMillis = 5 * 60 * 1000;

            if (currentTime - lastRunTime >= fiveMinutesInMillis) {
                dbHelper.updateSavingsBalance01();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("lastRunTime", currentTime);
                editor.apply();
            }

            // Trả về kết quả thành công
            return Result.success();
        } catch (Exception e) {
            Log.e("UpdateSavingsWorker", "Lỗi khi cập nhật số dư", e);
            return Result.failure();
        }
    }
}