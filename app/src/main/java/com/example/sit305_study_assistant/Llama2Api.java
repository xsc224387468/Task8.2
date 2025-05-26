package com.example.sit305_study_assistant;

import android.os.Handler;
import android.os.Looper;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Llama2Api {
    private static final String BASE_URL = "http://10.0.2.2:5000/api/";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();

    public interface ApiCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    public static void summarizeNotes(String notes, ApiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("text", notes);
            post("summarize", json, "summary", callback);
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public static void askQuestion(String context, String question, ApiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("context", context);
            json.put("question", question);
            post("qa", json, "answer", callback);
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public static void generatePlan(JSONObject planRequest, ApiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("learningGoal", planRequest.optString("goals"));
            json.put("courses", planRequest.optString("courses"));
            json.put("dailyTime", planRequest.optDouble("daily_time"));
            String deadline = planRequest.optString("start_date") + " ~ " + planRequest.optString("end_date");
            json.put("deadline", deadline);
            post("study-plan", json, "plan", callback);
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private static void post(String endpoint, JSONObject json, String resultKey, ApiCallback callback) {
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String resp = response.body().string();
                    JSONObject obj = new JSONObject(resp);
                    String result = obj.optString(resultKey, resp);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }
} 