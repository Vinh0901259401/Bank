package com.example.bidv_demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.os.Handler;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BankDB.db";
    private static final int DATABASE_VERSION = 12;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAllTables(db);
        onCreate(db);
    }




    private void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS Staffs");
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Contacts_Logs");
        db.execSQL("DROP TABLE IF EXISTS Staffs_Logs");
        db.execSQL("DROP TABLE IF EXISTS Users_Requests");
        db.execSQL("DROP TABLE IF EXISTS Transactions");
        db.execSQL("DROP TABLE IF EXISTS Savings_Accounts");
        db.execSQL("DROP TABLE IF EXISTS Loans");
        db.execSQL("DROP TABLE IF EXISTS Notifications");
        db.execSQL("DROP TABLE IF EXISTS Notifications_Logs");
        db.execSQL("DROP TABLE IF EXISTS Bank");
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Staffs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "email TEXT UNIQUE," +
                "phone TEXT UNIQUE," +
                "address TEXT," +
                "password TEXT," +
                "avatar TEXT," +
                "birth_year DATE," +
                "is_locked INTEGER DEFAULT 0," +
                "role TEXT CHECK(role IN ('Teller', 'Manager', 'Auditor', 'SuperAdmin'))," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "update_at TIMESTAMP," +
                "first_name TEXT," +
                "last_name TEXT," +
                "description TEXT" +
                ");");

        db.execSQL("CREATE TABLE Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "email TEXT UNIQUE," +
                "phone TEXT UNIQUE," +
                "address TEXT," +
                "password TEXT," +
                "avatar TEXT," +
                "birth_year DATE," +
                "is_locked INTEGER DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "update_at TIMESTAMP," +
                "first_name TEXT," +
                "last_name TEXT," +
                "balance DECIMAL(15, 2)," +
                "description TEXT" +
                ");");

        db.execSQL("CREATE TABLE Contacts_Logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "staff_id INTEGER," +
                "user_id INTEGER," +
                "created_at TIMESTAMP," +
                "body TEXT," +
                "description TEXT," +
                "FOREIGN KEY (staff_id) REFERENCES Staffs (id)," +
                "FOREIGN KEY (user_id) REFERENCES Users (id)" +
                ");");

        db.execSQL("CREATE TABLE Staffs_Logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "staff_id INTEGER," +
                "user_id INTEGER," +
                "created_at TIMESTAMP," +
                "type TEXT CHECK(type IN ('Create_User', 'Lock_User', 'Update_User', 'Delete_User', 'Other'))," +
                "description TEXT," +
                "FOREIGN KEY (staff_id) REFERENCES Staffs (id)," +
                "FOREIGN KEY (user_id) REFERENCES Users (id)" +
                ");");

        db.execSQL("CREATE TABLE Users_Requests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "staff_id INTEGER," +
                "user_id INTEGER," +
                "created_at TIMESTAMP," +
                "type TEXT CHECK(type IN ('Loan_Request', 'Other'))," +
                "description TEXT," +
                "status TEXT CHECK(status IN ('Deny', 'Approval'))," +
                "FOREIGN KEY (staff_id) REFERENCES Staffs (id)," +
                "FOREIGN KEY (user_id) REFERENCES Users (id)" +
                ");");

        db.execSQL("CREATE TABLE Transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "from_user_id INTEGER," +
                "to_user_id INTEGER," +
                "type TEXT CHECK(type IN ('Transfer', 'School_Fees', 'Bill_Payment', 'Deposit', 'Withdrawal'))," +
                "amount DECIMAL(15, 2)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "status TEXT CHECK(status IN ('Pending', 'Completed', 'Failed')) DEFAULT 'Completed'," +
                "description TEXT," +
                "FOREIGN KEY (from_user_id) REFERENCES Users (id)," +
                "FOREIGN KEY (to_user_id) REFERENCES Users (id)" +
                ");");

        db.execSQL("CREATE TABLE Savings_Accounts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "savings_balance DECIMAL(15, 2)," +
                "interest_rate DECIMAL(5, 2)," +
                "start_date DATETIME," +
                "maturity_date DATETIME," +
                "is_active INTEGER DEFAULT 1," +
                "Withdrawal_method TEXT," +
                "note TEXT," +
                "FOREIGN KEY (user_id) REFERENCES Users (id)" +
                ");");

        db.execSQL("CREATE TABLE Loans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "loan_amount DECIMAL(15, 2)," +
                "interest_rate DECIMAL(5, 2)," +
                "start_date DATETIME," +
                "end_date DATETIME," +
                "status TEXT CHECK(status IN ('Active', 'Paid', 'Defaulted')) DEFAULT 'Active'," +
                "FOREIGN KEY (user_id) REFERENCES Users (id)" +
                ");");

        db.execSQL("CREATE TABLE Notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "staff_id INTEGER," +
                "SavingAccount_id INTEGER," +
                "type TEXT CHECK(type IN ('Transaction', 'Bill_Due', 'Account_Update', 'Other', 'Saving_Account'))," +
                "title TEXT," +
                "body TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "description TEXT," +
                "FOREIGN KEY (staff_id) REFERENCES Staffs (id)" +
                ");");

        db.execSQL("CREATE TABLE Notifications_Logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "notification_id INTEGER," +
                "user_id INTEGER," +
                "transaction_id INTEGER," +
                "current_balance DECIMAL(15, 2)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "description TEXT," +
                "title TEXT," +
                "FOREIGN KEY (notification_id) REFERENCES Notifications (id)," +
                "FOREIGN KEY (user_id) REFERENCES Users (id)" +
                ");");

        db.execSQL("CREATE TABLE Bank (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "balance DECIMAL(15, 2)," +
                "balance_investment DECIMAL(15, 2)" +
                ");");
    }
    //check user
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Users", new String[]{"id"},
                "phone=? AND password=?",
                new String[]{username, password}, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    public String getFullName(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Users", new String[]{"first_name", "last_name"},
                "phone=? AND password=?",
                new String[]{username, password}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int firstNameIndex = cursor.getColumnIndex("first_name");
            int lastNameIndex = cursor.getColumnIndex("last_name");
            if (firstNameIndex >= 0 && lastNameIndex >= 0) {
                String firstName = cursor.getString(firstNameIndex);
                String lastName = cursor.getString(lastNameIndex);
                cursor.close();
                db.close();
                return firstName + " " + lastName;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    public String getFullNamebyPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Users", new String[]{"first_name", "last_name"},
                "phone=?",
                new String[]{phone}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int firstNameIndex = cursor.getColumnIndex("first_name");
            int lastNameIndex = cursor.getColumnIndex("last_name");
            if (firstNameIndex >= 0 && lastNameIndex >= 0) {
                String firstName = cursor.getString(firstNameIndex);
                String lastName = cursor.getString(lastNameIndex);
                cursor.close();
                db.close();
                return firstName + " " + lastName;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    public User getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("DatabaseHelper", "Querying user with username: " + username + " and password: " + password);
        Cursor cursor = db.query("Users", null, "phone=? AND password=?", new String[]{username, password}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d("DatabaseHelper", "User found in database");

            int idIndex = cursor.getColumnIndex("id");
            int usernameIndex = cursor.getColumnIndex("username");
            int emailIndex = cursor.getColumnIndex("email");
            int phoneIndex = cursor.getColumnIndex("phone");
            int addressIndex = cursor.getColumnIndex("address");
            int passwordIndex = cursor.getColumnIndex("password");
            int avatarIndex = cursor.getColumnIndex("avatar");
            int birthYearIndex = cursor.getColumnIndex("birth_year");
            int isLockedIndex = cursor.getColumnIndex("is_locked");
            int createdAtIndex = cursor.getColumnIndex("created_at");
            int updatedAtIndex = cursor.getColumnIndex("update_at");
            int firstNameIndex = cursor.getColumnIndex("first_name");
            int lastNameIndex = cursor.getColumnIndex("last_name");
            int balanceIndex = cursor.getColumnIndex("balance");
            int descriptionIndex = cursor.getColumnIndex("description");

            if (idIndex >= 0 && usernameIndex >= 0 && emailIndex >= 0 && phoneIndex >= 0 && addressIndex >= 0 && passwordIndex >= 0 && avatarIndex >= 0 && birthYearIndex >= 0 && isLockedIndex >= 0 && createdAtIndex >= 0 && updatedAtIndex >= 0 && firstNameIndex >= 0 && lastNameIndex >= 0 && balanceIndex >= 0 && descriptionIndex >= 0) {
                User user = new User(
                        cursor.getInt(idIndex),
                        cursor.getString(usernameIndex),
                        cursor.getString(emailIndex),
                        cursor.getString(phoneIndex),
                        cursor.getString(addressIndex),
                        cursor.getString(passwordIndex),
                        cursor.getString(avatarIndex),
                        new java.util.Date(cursor.getLong(birthYearIndex)),
                        cursor.getInt(isLockedIndex) > 0,
                        new java.sql.Timestamp(cursor.getLong(createdAtIndex)),
                        new java.sql.Timestamp(cursor.getLong(updatedAtIndex)),
                        cursor.getString(firstNameIndex),
                        cursor.getString(lastNameIndex),
                        new java.math.BigDecimal(cursor.getString(balanceIndex)),
                        cursor.getString(descriptionIndex)
                );
                cursor.close();
                db.close();
                return user;
            } else {
                Log.d("DatabaseHelper", "User data columns not found");
            }
        } else {
            Log.d("DatabaseHelper", "User not found in database");
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    public User getUser(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("DatabaseHelper", "Querying user with phone: " + phone);
        Cursor cursor = db.query("Users", null, "phone=?", new String[]{phone}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("DatabaseHelper", "User found in database");

            int idIndex = cursor.getColumnIndex("id");
            int usernameIndex = cursor.getColumnIndex("username");
            int emailIndex = cursor.getColumnIndex("email");
            int phoneIndex = cursor.getColumnIndex("phone");
            int addressIndex = cursor.getColumnIndex("address");
            int passwordIndex = cursor.getColumnIndex("password");
            int avatarIndex = cursor.getColumnIndex("avatar");
            int birthYearIndex = cursor.getColumnIndex("birth_year");
            int isLockedIndex = cursor.getColumnIndex("is_locked");
            int createdAtIndex = cursor.getColumnIndex("created_at");
            int updatedAtIndex = cursor.getColumnIndex("update_at");
            int firstNameIndex = cursor.getColumnIndex("first_name");
            int lastNameIndex = cursor.getColumnIndex("last_name");
            int balanceIndex = cursor.getColumnIndex("balance");
            int descriptionIndex = cursor.getColumnIndex("description");

            if (idIndex >= 0 && usernameIndex >= 0 && emailIndex >= 0 && phoneIndex >= 0 && addressIndex >= 0 && passwordIndex >= 0 && avatarIndex >= 0 && birthYearIndex >= 0 && isLockedIndex >= 0 && createdAtIndex >= 0 && updatedAtIndex >= 0 && firstNameIndex >= 0 && lastNameIndex >= 0 && balanceIndex >= 0 && descriptionIndex >= 0) {
                User user = new User(
                        cursor.getInt(idIndex),
                        cursor.getString(usernameIndex),
                        cursor.getString(emailIndex),
                        cursor.getString(phoneIndex),
                        cursor.getString(addressIndex),
                        cursor.getString(passwordIndex),
                        cursor.getString(avatarIndex),
                        new java.util.Date(cursor.getLong(birthYearIndex)),
                        cursor.getInt(isLockedIndex) > 0,
                        new java.sql.Timestamp(cursor.getLong(createdAtIndex)),
                        new java.sql.Timestamp(cursor.getLong(updatedAtIndex)),
                        cursor.getString(firstNameIndex),
                        cursor.getString(lastNameIndex),
                        new java.math.BigDecimal(cursor.getString(balanceIndex)),
                        cursor.getString(descriptionIndex)
                );
                cursor.close();
                db.close();
                return user;
            } else {
                Log.d("DatabaseHelper", "User data columns not found");
            }
        } else {
            Log.d("DatabaseHelper", "User not found in database");
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }


    public int getUserIdByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Users", new String[]{"id"}, "phone=?", new String[]{phone}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.close();
            return userId;
        }
        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }
    // cập nhật tiền lãi
    public void updateSavingsBalance(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            // Lấy thông tin tài khoản tiết kiệm của người dùng đang hoạt động
            cursor = db.query("Savings_Accounts", null, "user_id=? AND is_active=1", new String[]{String.valueOf(userId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    BigDecimal savingsBalance = new BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow("savings_balance")));
                    double interestRate = cursor.getDouble(cursor.getColumnIndexOrThrow("interest_rate"));
                    String startDateStr = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
                    String maturityDateStr = cursor.getString(cursor.getColumnIndexOrThrow("maturity_date"));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    Date startDate = sdf.parse(startDateStr);
                    Date maturityDate = sdf.parse(maturityDateStr);
                    Date currentDate = new Date();

                    // Nếu đã đến ngày đáo hạn, đánh dấu tài khoản không hoạt động
                    if (currentDate.after(maturityDate)) {
                        ContentValues values = new ContentValues();
                        values.put("is_active", 0);
                        db.update("Savings_Accounts", values, "id=?", new String[]{String.valueOf(id)});
                        continue;
                    }

                    // Tính tổng số ngày trong kỳ hạn
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);
                    int selectedTermMonths = 0;
                    while (calendar.getTime().before(maturityDate)) {
                        selectedTermMonths++;
                        calendar.add(Calendar.MONTH, 1);
                    }

                    calendar.setTime(startDate);
                    int totalDaysInSelectedTerm = 0;
                    for (int i = 0; i < selectedTermMonths; i++) {
                        totalDaysInSelectedTerm += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        calendar.add(Calendar.MONTH, 1);
                    }

                    // Tính số ngày từ lần cập nhật cuối đến hiện tại
                    long diffInMillies = Math.abs(currentDate.getTime() - startDate.getTime());
                    long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    if (diffInDays > 0) {
                        // Tính lãi hàng ngày dựa trên kỳ hạn
                        BigDecimal dailyInterestRate = BigDecimal.valueOf(interestRate / 100 / totalDaysInSelectedTerm);
                        BigDecimal interest = savingsBalance.multiply(dailyInterestRate).multiply(BigDecimal.valueOf(diffInDays));

                        // Cộng dồn lãi vào số dư tiết kiệm
                        savingsBalance = savingsBalance.add(interest);

                        // Cập nhật lại thông tin, KHÔNG cập nhật start_date
                        ContentValues values = new ContentValues();
                        values.put("savings_balance", savingsBalance.toString());
                        db.update("Savings_Accounts", values, "id=?", new String[]{String.valueOf(id)});
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating savings balance", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public void updateSavingsBalance01() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        Cursor cursorNotifications = null;
        int cursorNotificationID = -1;
        try {
            // Lấy tất cả các tài khoản đang hoạt động (is_active = 1)
            cursor = db.query("Savings_Accounts", null, "is_active = 1", null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int SavingAccountid = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    BigDecimal savingsBalance = new BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow("savings_balance")));
                    double interestRate = cursor.getDouble(cursor.getColumnIndexOrThrow("interest_rate"));
                    String startDateStr = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
                    String maturityDateStr = cursor.getString(cursor.getColumnIndexOrThrow("maturity_date"));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    Date startDate = sdf.parse(startDateStr);
                    Date maturityDate = sdf.parse(maturityDateStr);
                    Date currentDate = new Date();

                    // Kiểm tra nếu hiện tại đã qua ngày đáo hạn (maturityDate)
                    if (currentDate.after(maturityDate)) {
                        ContentValues values = new ContentValues();
                        values.put("is_active", 0); // Đánh dấu tài khoản là không còn hoạt động
                        db.update("Savings_Accounts", values, "id=?", new String[]{String.valueOf(SavingAccountid)});
                        continue;
                    }

                    // Tính lãi suất hàng ngày
                    BigDecimal dailyInterestRate = BigDecimal.valueOf(interestRate / 100 / 365); // Lãi suất hàng ngày
                    BigDecimal interest = savingsBalance.multiply(dailyInterestRate); // Lãi của 1 ngày
                    interest = interest.setScale(2, RoundingMode.HALF_UP);

                    // Cộng lãi vào savings_balance
                    savingsBalance = savingsBalance.add(interest);

                    // Cập nhật lại savings_balance trong cơ sở dữ liệu
                    ContentValues values = new ContentValues();
                    values.put("savings_balance", savingsBalance.toString());
                    int rowsUpdated = db.update("Savings_Accounts", values, "id=?", new String[]{String.valueOf(SavingAccountid)});
                    //In thông báo nhận tiền hoa hồng
                    cursorNotifications = db.rawQuery("SELECT id FROM Notifications WHERE SavingAccount_id = ?", new String[]{String.valueOf(SavingAccountid)});
                    if (cursorNotifications != null && cursorNotifications.moveToFirst()) {
                        cursorNotificationID = cursorNotifications.getInt(cursorNotifications.getColumnIndexOrThrow("id"));
                        cursorNotifications.close();
                    }
                    createNotificationLogForInterest(cursorNotificationID, SavingAccountid, savingsBalance, interest);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating savings balance", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    private void createNotificationLogForInterest(int NotificationId,int SavingAccountID, BigDecimal newBalance, BigDecimal interestAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String currentDateCreateAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        // Get the user associated with the Savings Account
        Cursor cursor = db.rawQuery("SELECT user_id FROM Savings_Accounts WHERE id = ?", new String[]{String.valueOf(SavingAccountID)});
        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            cursor.close();
        }

        // Create the notification log entry
        ContentValues logValues = new ContentValues();
        logValues.put("notification_id", NotificationId);
        logValues.put("user_id", userId);
        logValues.put("current_balance", newBalance.toString());
        logValues.put("created_at", currentDateCreateAt);
        logValues.put("description", "Bạn đã nhận được số tiền lãi là " + interestAmount.toString());
        logValues.put("title", "Tiền lãi xuất ngày " + currentDate + " đã chuyển vào tài khoản tiết kiệm thành công");
        db.insert("Notifications_Logs", null, logValues);
    }

}



