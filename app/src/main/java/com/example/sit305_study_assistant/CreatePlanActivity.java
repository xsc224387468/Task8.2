package com.example.sit305_study_assistant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class CreatePlanActivity extends AppCompatActivity {
    private TextInputEditText etGoals;
    private TextInputEditText etCourses;
    private TextInputEditText etTime;
    private TextInputEditText etStartDate;
    private TextInputEditText etEndDate;
    private Calendar startDate;
    private Calendar endDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

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
        etGoals = findViewById(R.id.etGoals);
        etCourses = findViewById(R.id.etCourses);
        etTime = findViewById(R.id.etTime);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        Button btnGenerate = findViewById(R.id.btnGenerate);

        // Initialize date format and calendars
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

        // Set up date pickers
        setupDatePicker(etStartDate, startDate, "Start Date");
        setupDatePicker(etEndDate, endDate, "End Date");

        // Set up generate button
        btnGenerate.setOnClickListener(v -> generatePlan());
    }

    private void setupDatePicker(TextInputEditText editText, Calendar calendar, String title) {
        editText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    editText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.setTitle(title);
            datePickerDialog.show();
        });
    }

    private void generatePlan() {
        String goals = etGoals.getText().toString().trim();
        String courses = etCourses.getText().toString().trim();
        String timeStr = etTime.getText().toString().trim();
        String startDateStr = etStartDate.getText().toString().trim();
        String endDateStr = etEndDate.getText().toString().trim();

        // Validate inputs
        if (goals.isEmpty()) {
            Toast.makeText(this, "Please enter your learning goals", Toast.LENGTH_SHORT).show();
            return;
        }
        if (courses.isEmpty()) {
            Toast.makeText(this, "Please enter your courses", Toast.LENGTH_SHORT).show();
            return;
        }
        if (timeStr.isEmpty()) {
            Toast.makeText(this, "Please enter daily study time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startDateStr.isEmpty()) {
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endDateStr.isEmpty()) {
            Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate dates
        if (startDate.after(endDate)) {
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse daily study time
        double dailyTime;
        try {
            dailyTime = Double.parseDouble(timeStr);
            if (dailyTime <= 0) {
                Toast.makeText(this, "Daily study time must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for daily study time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create plan request
        JSONObject planRequest = new JSONObject();
        try {
            planRequest.put("goals", goals);
            planRequest.put("courses", courses);
            planRequest.put("daily_time", dailyTime);
            planRequest.put("start_date", startDateStr);
            planRequest.put("end_date", endDateStr);

            // Show loading state
            Toast.makeText(this, "Generating your study plan...", Toast.LENGTH_SHORT).show();

            // Call API to generate plan
            Llama2Api.generatePlan(planRequest, new Llama2Api.ApiCallback() {
                @Override
                public void onSuccess(String result) {
                    // Start ViewPlanActivity with the generated plan
                    Intent intent = new Intent(CreatePlanActivity.this, ViewPlanActivity.class);
                    intent.putExtra("plan", result);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(CreatePlanActivity.this, 
                        "Error generating plan: " + error, 
                        Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating plan request", Toast.LENGTH_SHORT).show();
        }
    }
} 