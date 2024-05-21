package com.example.chatapponline.activities;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatapponline.R;
import com.example.chatapponline.adapters.UserAdapter;
import com.example.chatapponline.databinding.ActivityUsersBinding;
import com.example.chatapponline.listener.UserListener;
import com.example.chatapponline.models.User;
import com.example.chatapponline.utilities.Constans;
import com.example.chatapponline.utilities.PreferencesManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {
    private ActivityUsersBinding binding;
    private PreferencesManager preferencesManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencesManager = new PreferencesManager(getApplicationContext());
        getUsers();
        setListeners();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
    }

    private void getUsers() {
        loading(true);
        //thực hiện truy vấn trong firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constans.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferencesManager.getString(Constans.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        //tạo 1 danh sách user
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            //khởi tạo đối tượng thực hiện gán giá trị của từng thược tính cho đối tượng
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constans.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constans.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constans.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constans.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            UserAdapter userAdapter = new UserAdapter(users,this);
                            binding.userRecycalview.setAdapter(userAdapter);
                            binding.userRecycalview.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        //set trạng thái thanh processBar khi chờ load
        if(isLoading){
            binding.processBar.setVisibility(View.VISIBLE);
        }else {
            binding.processBar.setVisibility(View.INVISIBLE);
        }
    }

    public void onUserClicked(User user){
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constans.KEY_USER, user);
        startActivity(intent);
        finish();
    }

}