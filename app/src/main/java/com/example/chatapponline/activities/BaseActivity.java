package com.example.chatapponline.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapponline.utilities.Constans;
import com.example.chatapponline.utilities.PreferencesManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager preferencesManager = new PreferencesManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constans.KEY_COLLECTION_USERS)
                .document(preferencesManager.getString(Constans.KEY_USER_ID));

    }

    //thuực hiện thêm vào collection


    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Constans.KEY_AVAILABILITY, 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constans.KEY_AVAILABILITY, 1);
    }
}
