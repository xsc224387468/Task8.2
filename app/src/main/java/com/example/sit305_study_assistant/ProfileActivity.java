package com.example.sit305_study_assistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String email = prefs.getString("email", "");
        String major = prefs.getString("major", "");
        TextView tvName = findViewById(R.id.tvName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvMajor = findViewById(R.id.tvMajor);
        tvName.setText("Name: " + name);
        tvEmail.setText("Email: " + email);
        tvMajor.setText("Major: " + major);
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        // TODO: 实现用户信息、偏好、主题切换、清除历史等
        // 可选：添加返回首页按钮
        // findViewById(R.id.btnBackHome).setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
    }
} 