package com.example.skyvc.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skyvc.R;
import com.example.skyvc.databinding.ActivityLoginBinding;
import com.example.skyvc.repository.MainRepository;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding views;
    private MainRepository mainRepository;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(views.getRoot());

        init();


    }

    private  void init(){
        mainRepository=MainRepository.getInstance();
        views.enterBtn.setOnClickListener(v->{
        //login the user
        mainRepository.login(views.username.getText().toString(),()->{
            startActivity(new Intent(this,CallActivity.class));
        });

            //if success move to call activity

        });
    }
}