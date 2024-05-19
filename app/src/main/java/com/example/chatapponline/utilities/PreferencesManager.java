package com.example.chatapponline.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    //lưu trữ và truy xuất các cặp key-value một cách liên tục.
    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constans.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    //lưu trữ giá trị boolen với key đã cho vào sharepreferene
    public void putBoolen(String key, Boolean vaule) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, vaule);
        editor.apply();
    }

    //truy xuất ra 1 giá trị boolen liên kết với key đã cho từ sharefer . Nếu key không tồn tại trả về null;
    public Boolean getBoolen(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String vaule) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, vaule);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    //xóa tất cả các cặp key và vaule
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
