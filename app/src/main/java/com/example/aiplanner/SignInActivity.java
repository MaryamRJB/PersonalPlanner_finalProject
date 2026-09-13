package com.example.aiplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.database.room.AppDatabase;
import com.example.personalplanner.database.entity.User;
import com.example.personalplanner.database.dao.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignInActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";

    private EditText etPhone;
    private Button btnConfirm;

    private UserDao userDao;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        ImageView btnBackArrow = findViewById(R.id.btnBackArrow);
        etPhone = findViewById(R.id.etPhone);
        btnConfirm = findViewById(R.id.btnConfirm);

        // دسترسی به دیتابیس
        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
        userDao = database.userDao();

        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        btnConfirm.setOnClickListener(v -> {

            String phone = etPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                etPhone.setError("لطفاً شماره تلفن را وارد کنید");
                etPhone.requestFocus();
                return;
            }

            executorService.execute(() -> {

                // بررسی اینکه این شماره قبلاً ثبت شده یا نه
                User user = userDao.getUserByPhone(phone);

                if (user == null) {

                    // کاربر جدید
                    user = new User(
                            "",
                            phone,
                            "",
                            "",
                            ""
                    );

                    long userId = userDao.insert(user);

                    user.setId((int) userId);

                } else {

                    // کاربر قبلاً وجود داشته
                    // شماره همان شماره قبلی است
                }

                // شناسه کاربر فعلی را ذخیره می‌کنیم
                int finalUserId = user.getId();

                prefs.edit()
                        .putBoolean(KEY_IS_LOGGED_IN, true)
                        .putInt(KEY_USER_ID, finalUserId)
                        .apply();

                runOnUiThread(() -> {

                    Toast.makeText(
                            SignInActivity.this,
                            "ورود با موفقیت انجام شد",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            SignInActivity.this,
                            ProfileActivity.class
                    );

                    startActivity(intent);
                    finish();
                });
            });
        });

        if (btnBackArrow != null) {
            btnBackArrow.setOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}