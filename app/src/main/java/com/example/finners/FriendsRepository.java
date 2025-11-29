package com.example.finners;

import java.util.ArrayList;
import java.util.List;

public class FriendsRepository {
    private static FriendsRepository instance;
    private List<Contact> friends;
    private java.util.Map<String, Double> balances;
    private static final String PREFS_NAME = "FinnerPrefs";
    private static final String KEY_FRIENDS = "friends_list";
    private static final String KEY_BALANCES = "friends_balances";
    private android.content.Context context;

    private FriendsRepository(android.content.Context context) {
        this.context = context.getApplicationContext();
        friends = new ArrayList<>();
        balances = new java.util.HashMap<>();
        loadFriends();
        loadBalances();
    }

    public static synchronized FriendsRepository getInstance(android.content.Context context) {
        if (instance == null) {
            instance = new FriendsRepository(context);
        }
        return instance;
    }

    public List<Contact> getFriends() {
        return new ArrayList<>(friends);
    }

    public void addFriend(Contact contact) {
        if (!isFriend(contact)) {
            friends.add(contact);
            saveFriends();
        }
    }

    public boolean isFriend(Contact contact) {
        for (Contact friend : friends) {
            if (friend.getId().equals(contact.getId())) {
                return true;
            }
        }
        return false;
    }

    public void removeFriend(Contact contact) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getId().equals(contact.getId())) {
                friends.remove(i);
                saveFriends();
                break;
            }
        }
    }

    public double getBalance(String friendId) {
        return balances.getOrDefault(friendId, 0.0);
    }

    public void updateBalance(String friendId, double amount) {
        double currentBalance = getBalance(friendId);
        balances.put(friendId, currentBalance + amount);
        saveBalances();
    }

    public void setBalance(String friendId, double amount) {
        balances.put(friendId, amount);
        saveBalances();
    }

    private void saveFriends() {
        android.content.SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        org.json.JSONArray jsonArray = new org.json.JSONArray();
        for (Contact friend : friends) {
            try {
                org.json.JSONObject jsonObject = new org.json.JSONObject();
                jsonObject.put("id", friend.getId());
                jsonObject.put("name", friend.getName());
                jsonObject.put("phoneNumber", friend.getPhoneNumber());
                jsonArray.put(jsonObject);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putString(KEY_FRIENDS, jsonArray.toString()).apply();
    }

    private void loadFriends() {
        android.content.SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_FRIENDS, null);
        if (jsonString != null) {
            try {
                org.json.JSONArray jsonArray = new org.json.JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    String phoneNumber = jsonObject.optString("phoneNumber", "");
                    friends.add(new Contact(id, name, phoneNumber));
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveBalances() {
        android.content.SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        org.json.JSONObject jsonObject = new org.json.JSONObject(balances);
        prefs.edit().putString(KEY_BALANCES, jsonObject.toString()).apply();
    }

    private void loadBalances() {
        android.content.SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_BALANCES, null);
        if (jsonString != null) {
            try {
                org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
                java.util.Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    balances.put(key, jsonObject.getDouble(key));
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
