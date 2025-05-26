package com.example.sit305_study_assistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AIQAActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_qa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        EditText etContext = findViewById(R.id.etContext);
        EditText etQuestion = findViewById(R.id.etQuestion);
        Button btnAsk = findViewById(R.id.btnAsk);
        TextView tvAnswer = findViewById(R.id.tvAnswer);

        btnAsk.setOnClickListener(v -> {
            String context = etContext.getText().toString().trim();
            String question = etQuestion.getText().toString().trim();

            if (question.isEmpty()) {
                Toast.makeText(this, "Please enter your question", Toast.LENGTH_SHORT).show();
                return;
            }

            tvAnswer.setText("Thinking...");
            Llama2Api.askQuestion(context, question, new Llama2Api.ApiCallback() {
                @Override
                public void onSuccess(String result) {
                    tvAnswer.setText(result);
                }

                @Override
                public void onError(String error) {
                    tvAnswer.setText("Error: " + error);
                }
            });
        });
    }
} 