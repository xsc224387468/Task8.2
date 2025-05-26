package com.example.sit305_study_assistant;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Iterator;

public class PlanDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        TextView tvDetail = findViewById(R.id.tvPlanDetail);
        String planJson = getIntent().getStringExtra("planJson");
        StringBuilder detail = new StringBuilder();
        try {
            JSONObject plan = new JSONObject(planJson);
            detail.append("Learning Goal: ").append(plan.optString("learningGoal", "")).append("\n");
            detail.append("Courses: ").append(plan.optString("courses", "")).append("\n\n");
            detail.append("Explanation: ").append(plan.optString("explanation", "")).append("\n\n");
            detail.append("Resources:\n");
            JSONArray resources = plan.optJSONArray("resources");
            if (resources != null) {
                for (int i = 0; i < resources.length(); i++) {
                    detail.append("- ").append(resources.getString(i)).append("\n");
                }
            }
            detail.append("\nDaily Tasks:\n");
            JSONObject dailyTasks = plan.optJSONObject("dailyTasks");
            if (dailyTasks != null) {
                Iterator<String> days = dailyTasks.keys();
                while (days.hasNext()) {
                    String day = days.next();
                    detail.append(day).append(": ").append(dailyTasks.getString(day)).append("\n");
                }
            }
        } catch (Exception e) {
            detail.append("[Invalid Data]");
        }
        tvDetail.setText(detail.toString());
    }
} 