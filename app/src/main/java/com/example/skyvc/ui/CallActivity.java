package com.example.skyvc.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skyvc.databinding.ActivityCallBinding;
import com.example.skyvc.repository.MainRepository;
import com.example.skyvc.utils.DataModelType;

public class CallActivity extends AppCompatActivity {

    private ActivityCallBinding views;
    private MainRepository mainRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        init();
    }

    private  void init(){
        mainRepository = MainRepository.getInstance();

        views.callBtn.setOnClickListener(v->{
            mainRepository.sendCallrequest(views.targetUserNameEt.getText().toString(),()->{
                Toast.makeText(this,"Could not find the user",Toast.LENGTH_SHORT).show();
            });
        });

        mainRepository.subscribeForLatestEvent(data->{
            if(data.getType()== DataModelType.StartCall){
                runOnUiThread(()->{
                    views.incomingNameTV.setText(data.getSender()+" is calling you");
                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    views.acceptButton.setOnClickListener(v->{
                        //start the call

                        views.incomingCallLayout.setVisibility(View.GONE);

                    });
                    views.acceptButton.setOnClickListener(v->{
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                });
            }
        });


    }

}
