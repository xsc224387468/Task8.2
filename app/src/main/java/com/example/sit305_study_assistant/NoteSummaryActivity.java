package com.example.sit305_study_assistant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NoteSummaryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_summary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        EditText etNotes = findViewById(R.id.etNotes);
        Button btnSummarize = findViewById(R.id.btnSummarize);
        TextView tvSummary = findViewById(R.id.tvSummary);

        btnSummarize.setOnClickListener(v -> {
            String notes = etNotes.getText().toString().trim();
            if (notes.isEmpty()) {
                Toast.makeText(this, "请输入笔记内容", Toast.LENGTH_SHORT).show();
                return;
            }

            tvSummary.setText("正在生成摘要...");
            Llama2Api.summarizeNotes(notes, new Llama2Api.ApiCallback() {
                @Override
                public void onSuccess(String result) {
                    tvSummary.setText(result);
                }

                @Override
                public void onError(String error) {
                    tvSummary.setText("错误: " + error);
                }
            });
        });
    }
} 