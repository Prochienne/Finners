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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        view.findViewById(R.id.btnAddExpense).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), AddExpenseActivity.class);
            startActivity(intent);
        });
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActivityLogs();
    }

    private void loadActivityLogs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("activity_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    return;
                }

                List<String> logs = new ArrayList<>();
                if (snapshots != null) {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String message = doc.getString("message");
                        String subMessage = doc.getString("subMessage");
                        if (message != null) {
                            if (subMessage != null && !subMessage.isEmpty()) {
                                logs.add(message + "|" + subMessage);
                            } else {
                                logs.add(message);
                            }
                        }
                    }
                }

                if (logs.isEmpty()) {
                    tvActivityEmpty.setVisibility(View.VISIBLE);
                    lvActivityLog.setVisibility(View.GONE);
                } else {
                    tvActivityEmpty.setVisibility(View.GONE);
                    lvActivityLog.setVisibility(View.VISIBLE);
                    ActivityLogAdapter adapter = new ActivityLogAdapter(requireContext(), logs);
                    lvActivityLog.setAdapter(adapter);
                }
            });
    }

    private static class ActivityLogAdapter extends ArrayAdapter<String> {
        public ActivityLogAdapter(Context context, List<String> logs) {
            super(context, 0, logs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            String log = getItem(position);
            TextView text1 = convertView.findViewById(android.R.id.text1);
            TextView text2 = convertView.findViewById(android.R.id.text2);

            if (log != null) {
                if (log.contains("|")) {
                    String[] parts = log.split("\\|", 2);
                    text1.setText(parts[0]);
                    text2.setText(parts[1]);
                    text2.setVisibility(View.VISIBLE);
                } else {
                    text1.setText(log);
                    text2.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }
}
