package com.example.skyvc.remote;

import androidx.annotation.NonNull;

import com.example.skyvc.utils.DataModel;
import com.example.skyvc.utils.DataModelType;
import com.example.skyvc.utils.ErrorCallBack;
import com.example.skyvc.utils.NewEventCallback;
import com.example.skyvc.utils.SuccessCallBack;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Objects;

public class FirebaseClient {

    private final Gson gson = new Gson();
    private final DatabaseReference dref= FirebaseDatabase.getInstance().getReference();
    private String currentUsername;
    private static final String LATEST_EVENT_FIELD_NAME = "latest_event";

    public void login(String username, SuccessCallBack callback){
        dref.child(username).setValue("").addOnCompleteListener(task->{
            currentUsername=username;
            callback.onSuccess();
        });

    }
    public void sendMessageToOtherUser(DataModel dataModel, ErrorCallBack errorCallBack){
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(dataModel.getTarget()).exists()){
                    dref.child(dataModel.getTarget()).child(LATEST_EVENT_FIELD_NAME).setValue(gson.toJson(dataModel));
                }
                else errorCallBack.onError();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCallBack.onError();
            }
        });


    }
    public void observeIncomingLatestEvent(NewEventCallback callback){
            dref.child(currentUsername).child(LATEST_EVENT_FIELD_NAME).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                         String data = Objects.requireNonNull(snapshot.getValue()).toString();
                         DataModel dataModel = gson.fromJson(data, DataModel.class);
                         callback.onNewEventReceived(dataModel);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }


}
