package com.example.chatapponline.activities;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.chatapponline.R;
import com.example.chatapponline.adapters.RecentCoversationsAdapter;
import com.example.chatapponline.databinding.ActivityMainBinding;
import com.example.chatapponline.listener.ConversionListener;
import com.example.chatapponline.models.ChatMessage;
import com.example.chatapponline.models.User;
import com.example.chatapponline.utilities.Constans;
import com.example.chatapponline.utilities.PreferencesManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements ConversionListener {
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
        listenConversations();
    }

    private void init() {
        conversations = new ArrayList<>();
        conversationAdapter = new RecentCoversationsAdapter(conversations,this);
        binding.coversationRecycalView.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListener() {
        binding.imageSignout.setOnClickListener(v -> signOut());
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class))
        );
    }

    private void loadUserDetails() {
        //lấy giá trị lưu từ preferences để gán cho textname , image
        binding.textName.setText(preferencesManager.getString(Constans.KEY_NAME));
        byte[] bytes = Base64.decode(preferencesManager.getString(Constans.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);

    }

    private void listenConversations(){
        database.collection(Constans.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constans.KEY_SENDER_ID, preferencesManager.getString(Constans.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constans.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constans.KEY_RECEIVER_ID,preferencesManager.getString(Constans.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        //nêếu có lỗi trả về, không làm gì cả
        if (error != null) {
            return;
        }
        //nếu giá trị không null tiêến haành xử lý
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                //nêếu ccos dữ liệu được thêm
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constans.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constans.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = senderId;
                    chatMessage.receiverId = receiverId;
                    //thuc hieen kiem tra theo id vaf gan du lieu
                    if (preferencesManager.getString(Constans.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constans.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constans.KEY_RECEIVER_NAME);
                        chatMessage.converionId = documentChange.getDocument().getString(Constans.KEY_RECEIVER_ID);
                    } else {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constans.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constans.KEY_SENDER_NAME);
                        chatMessage.converionId = documentChange.getDocument().getString(Constans.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constans.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constans.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                    //nêếu có tài liệu được sửa đổi
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constans.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constans.KEY_RECEIVER_ID);
                        if (conversations.get(i).senderID.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)) {
                            conversations.get(i).message = documentChange.getDocument().getString(Constans.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constans.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            //sắp xếp các cuộc trò chuyện theo thơời gian
            Collections.sort(conversations, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            conversationAdapter.notifyDataSetChanged();
            binding.coversationRecycalView.smoothScrollToPosition(0);
            binding.coversationRecycalView.setVisibility(View.VISIBLE);
            binding.processBar.setVisibility(View.GONE);
        }
    };

    private void showToast(String massage) {
        Toast.makeText(getApplicationContext(), massage, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constans.KEY_COLLECTION_USERS).document(
                        preferencesManager.getString(Constans.KEY_USER_ID)
                );
        documentReference.update(Constans.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token update successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    private void signOut() {
        showToast("Sign out");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constans.KEY_COLLECTION_USERS).document(
                        preferencesManager.getString(Constans.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
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

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constans.KEY_USER, user);
        startActivity(intent);
    }
}