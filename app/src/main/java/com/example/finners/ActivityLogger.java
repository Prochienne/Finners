package com.example.finners;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

public class ActivityLogger {

    public static void log(Context context, String message) {
        log(context, message, null);
    }

    public static void log(Context context, String message, String subMessage) {
        if (context == null) return;

        // Save to local prefs (backup/offline)
        try {
            SharedPreferences prefs = context.getSharedPreferences("FinnerPrefs", Context.MODE_PRIVATE);
            String logsJson = prefs.getString("activity_logs", "[]");
            JSONArray jsonArray = new JSONArray(logsJson);
            String fullMessage = message;
            if (subMessage != null && !subMessage.isEmpty()) {
                fullMessage += "|" + subMessage;
            }
            jsonArray.put(fullMessage);
            prefs.edit().putString("activity_logs", jsonArray.toString()).apply();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // Save to Firestore
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> log = new HashMap<>();
            log.put("message", message);
            if (subMessage != null) {
                log.put("subMessage", subMessage);
            }
            log.put("timestamp", System.currentTimeMillis());

            db.collection("activity_logs")
                .add(log)
                .addOnSuccessListener(documentReference -> {
                    // Log added successfully
                })
                .addOnFailureListener(e -> {
                    // Failed to sync activity log
                });
        } catch (Throwable e) {
            // Firebase not available or other error, ignore
        }
    }
}
