package com.example.sit305_study_assistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize feature cards
        CardView studyPlanCard = findViewById(R.id.studyPlanCard);
        CardView qaCard = findViewById(R.id.qaCard);
        CardView noteSummaryCard = findViewById(R.id.noteSummaryCard);
        CardView profileCard = findViewById(R.id.profileCard);

        // Set click listeners
        studyPlanCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreatePlanActivity.class);
            startActivity(intent);
        });

        qaCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AIQAActivity.class);
            startActivity(intent);
        });

        noteSummaryCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteSummaryActivity.class);
            startActivity(intent);
        });

        profileCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button historyPlansBtn = findViewById(R.id.btnHistoryPlans);
        historyPlansBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryPlansActivity.class);
            startActivity(intent);
        });
    }
}