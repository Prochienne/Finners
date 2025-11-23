package com.example.finners;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {

    private ListView lvActivityLog;
    private TextView tvActivityEmpty;

    public ActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        
        lvActivityLog = view.findViewById(R.id.lvActivityLog);
        tvActivityEmpty = view.findViewById(R.id.tvActivityEmpty);
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActivityLogs();
    }

    private void loadActivityLogs() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("FinnerPrefs", Context.MODE_PRIVATE);
        String logsJson = prefs.getString("activity_logs", "[]");
        List<String> logs = new ArrayList<>();
        
        try {
            JSONArray jsonArray = new JSONArray(logsJson);
            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                logs.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (logs.isEmpty()) {
            tvActivityEmpty.setVisibility(View.VISIBLE);
            lvActivityLog.setVisibility(View.GONE);
        } else {
            tvActivityEmpty.setVisibility(View.GONE);
            lvActivityLog.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, logs);
            lvActivityLog.setAdapter(adapter);
        }
    }
}
