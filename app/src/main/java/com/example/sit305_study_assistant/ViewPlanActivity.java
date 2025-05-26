package com.example.sit305_study_assistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.view.View;
import android.util.Log;
import androidx.room.Room;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.widget.Toolbar;

public class ViewPlanActivity extends AppCompatActivity {
    private TextView tvTasks;
    private TextView tvExplanation;
    private PieChart pieChart;
    private Button btnHelpful;
    private Button btnNotHelpful;
    private Button btnSave;
    private Button btnRegenerate;
    private JSONObject currentPlanJson; // 保存当前完整计划
    private TextView tvResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Initialize views
        tvTasks = findViewById(R.id.tvTasks);
        tvExplanation = findViewById(R.id.tvExplanation);
        pieChart = findViewById(R.id.pieChart);
        btnHelpful = findViewById(R.id.btnHelpful);
        btnNotHelpful = findViewById(R.id.btnNotHelpful);
        btnSave = findViewById(R.id.btnSave);
        btnRegenerate = findViewById(R.id.btnRegenerate);
        tvResources = findViewById(R.id.tvResources); // 新增资源推荐TextView

        // Get plan data from intent
        String planJson = getIntent().getStringExtra("plan");
        if (planJson != null) {
            try {
                JSONObject plan = new JSONObject(planJson);
                displayPlan(plan);
            } catch (JSONException e) {
                Toast.makeText(this, "Error parsing plan data", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No plan data received", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up feedback buttons
        btnHelpful.setOnClickListener(v -> submitFeedback(true));
        btnNotHelpful.setOnClickListener(v -> submitFeedback(false));

        // Set up action buttons
        btnSave.setOnClickListener(v -> savePlan());
        btnRegenerate.setOnClickListener(v -> regeneratePlan());
    }

    private void displayPlan(JSONObject plan) throws JSONException {
        // 1. 每日学习任务
        JSONObject dailyTasks = plan.optJSONObject("dailyTasks");
        StringBuilder tasksBuilder = new StringBuilder();
        if (dailyTasks != null) {
            Iterator<String> days = dailyTasks.keys();
            while (days.hasNext()) {
                String day = days.next();
                tasksBuilder.append(day).append(": ").append(dailyTasks.getString(day)).append("\n");
            }
        }
        tvTasks.setText(tasksBuilder.toString());

        // 2. AI 解释
        String explanation = plan.optString("explanation", "No explanation available");
        tvExplanation.setText(explanation);

        // 3. 学习资源
        JSONArray resources = plan.optJSONArray("resources");
        StringBuilder resBuilder = new StringBuilder();
        if (resources != null) {
            for (int i = 0; i < resources.length(); i++) {
                resBuilder.append(resources.getString(i)).append("\n");
            }
        }
        tvResources.setText(resBuilder.toString());

        // 4. 学习时间分配饼状图
        JSONObject timeDistribution = plan.optJSONObject("time_distribution");
        if (timeDistribution != null && timeDistribution.length() > 0) {
            List<PieEntry> entries = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();
            Iterator<String> keys = timeDistribution.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                float hours = (float) timeDistribution.optDouble(key, 0);
                entries.add(new PieEntry(hours, key));
                colors.add(ColorTemplate.MATERIAL_COLORS[entries.size() % ColorTemplate.MATERIAL_COLORS.length]);
            }
            PieDataSet dataSet = new PieDataSet(entries, "Study Time Distribution");
            dataSet.setColors(colors);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.WHITE);
            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(pieChart) {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.1fh", value);
                }
            });
            pieChart.setData(data);
            pieChart.getDescription().setEnabled(false);
            pieChart.setEntryLabelTextSize(12f);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleRadius(30f);
            pieChart.setHoleRadius(30f);
            pieChart.animateY(1000);
            pieChart.setVisibility(View.VISIBLE);
            pieChart.invalidate();
        } else {
            pieChart.setVisibility(View.GONE);
        }

        // 5. 保存当前计划
        currentPlanJson = plan;
    }

    private void submitFeedback(boolean isHelpful) {
        // TODO: Implement feedback submission to backend
        Toast.makeText(this, 
            isHelpful ? "Thank you for your positive feedback!" : "Thank you for your feedback. We'll work to improve.",
            Toast.LENGTH_SHORT).show();
    }

    private void savePlan() {
        if (currentPlanJson == null) return;
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "studyplan-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        StudyPlan plan = new StudyPlan();
        plan.planJson = currentPlanJson.toString();
        plan.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        plan.learningGoal = currentPlanJson.optString("learningGoal", "");
        plan.courses = currentPlanJson.optString("courses", "");
        db.studyPlanDao().insert(plan);
        Toast.makeText(this, "Plan saved successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HistoryPlansActivity.class);
        startActivity(intent);
        finish();
    }

    private void regeneratePlan() {
        // Return to create plan activity
        Intent intent = new Intent(this, CreatePlanActivity.class);
        startActivity(intent);
        finish();
    }
} 