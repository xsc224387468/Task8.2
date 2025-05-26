package com.example.sit305_study_assistant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import androidx.room.Room;
import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;
import android.content.Intent;

public class HistoryPlansActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_plans);

        ListView listView = findViewById(R.id.listViewPlans);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "studyplan-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        List<StudyPlan> plans = db.studyPlanDao().getAllPlans();
        ArrayList<String> planSummaries = new ArrayList<>();
        for (StudyPlan plan : plans) {
            planSummaries.add(plan.createdAt + " | " + plan.learningGoal + " | " + plan.courses);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, planSummaries);
        listView.setAdapter(adapter);
        // 点击跳转到详情页
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                StudyPlan selectedPlan = plans.get(position);
                Intent intent = new Intent(HistoryPlansActivity.this, PlanDetailActivity.class);
                intent.putExtra("planJson", selectedPlan.planJson);
                startActivity(intent);
            }
        });
    }
} 