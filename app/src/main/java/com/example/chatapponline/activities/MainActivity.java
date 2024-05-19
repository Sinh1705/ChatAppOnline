package com.example.chatapponline.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.widget.Toast;

import com.example.chatapponline.R;
import com.example.chatapponline.adapters.RecentCoversationsAdapter;
import com.example.chatapponline.databinding.ActivityMainBinding;
import com.example.chatapponline.models.ChatMessage;
import com.example.chatapponline.utilities.Constans;
import com.example.chatapponline.utilities.PreferencesManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferencesManager preferencesManager;
    private List<ChatMessage> conversations;
    private RecentCoversationsAdapter conversationAdapter;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencesManager = new PreferencesManager(getApplicationContext());
        loadUserDetails();
        getToken();
        setListener();
        init();
    }

    private void init(){
        conversations = new ArrayList<>();
        conversationAdapter = new RecentCoversationsAdapter(conversations);
        binding.coversationRecycalView.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();
    }
    private void setListener(){
        binding.imageSignout.setOnClickListener(v-> signOut());
        binding.fabNewChat.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(),UsersActivity.class))
        );
    }

    private void loadUserDetails(){
        //lấy giá trị lưu từ preferences để gán cho textname , image
        binding.textName.setText(preferencesManager.getString(Constans.KEY_NAME));
        byte[] bytes = Base64.decode(preferencesManager.getString(Constans.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);

    }

    private void showToast(String massage){
        Toast.makeText(getApplicationContext(),massage,Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constans.KEY_COLLECTION_USERS).document(
                        preferencesManager.getString(Constans.KEY_USER_ID)
                );
        documentReference.update(Constans.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(unused -> showToast("Token update successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    private void signOut(){
        showToast("Sign out");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constans.KEY_COLLECTION_USERS).document(
                        preferencesManager.getString(Constans.KEY_USER_ID)
                );
        HashMap<String , Object> updates = new HashMap<>();
        updates.put(Constans.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferencesManager.clear();
                    startActivities(new Intent[]{new Intent(getApplicationContext(), SignInActivity.class)});
                    finish();
                })
                .addOnFailureListener(e -> {
                    showToast("SignOut fail");
                });

    }
}