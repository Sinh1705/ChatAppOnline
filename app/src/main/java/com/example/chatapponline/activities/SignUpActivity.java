package com.example.chatapponline.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.chatapponline.R;
import com.example.chatapponline.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private String encodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener() {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.btnSignUp.setOnClickListener(v -> {
            if(isVaidSignUpDatails()){
                signUp();
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    private void signUp(){
        
    }
    
    private Boolean isVaidSignUpDatails(){
        if(encodeImage == null){
            showToast("select profile image");
            return  false;
        } else if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Enter name");
            return  false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return  false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputPassword.getText().toString()).matches()) {
            showToast("Enter valid email address");
            return  false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter confirm password");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("Password & confirm pass must be same");
            return  false;
        }else {
            return true;
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.btnSignUp.setVisibility(View.INVISIBLE);
            binding.processBar.setVisibility(View.VISIBLE);
        }else {
            binding.processBar.setVisibility(View.INVISIBLE);
            binding.btnSignUp.setVisibility(View.VISIBLE);
        }
    }
}