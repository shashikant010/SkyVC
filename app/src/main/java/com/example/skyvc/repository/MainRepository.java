package com.example.skyvc.repository;

import com.example.skyvc.remote.FirebaseClient;
import com.example.skyvc.utils.DataModel;
import com.example.skyvc.utils.DataModelType;
import com.example.skyvc.utils.ErrorCallBack;
import com.example.skyvc.utils.NewEventCallback;
import com.example.skyvc.utils.SuccessCallBack;

public class MainRepository  {

    FirebaseClient firebaseClient;
    private static MainRepository instace;

    private  String currentUsername;

    private void updateCurrentUsername(String username){
        this.currentUsername=username;
    }

    private MainRepository(){
        this.firebaseClient=new FirebaseClient();
    }


    public static MainRepository getInstance(){
        if(instace==null){
            instace=new MainRepository();
        }
        return instace;
    }

    public void login(String username, SuccessCallBack callBack){
        firebaseClient.login(username,()->{
            updateCurrentUsername(username);
            callBack.onSuccess();
        });


    }

    public  void sendCallrequest(String target, ErrorCallBack errorCallback){
        firebaseClient.sendMessageToOtherUser(new DataModel(target,currentUsername,"", DataModelType.StartCall),errorCallback);
    }


    public void subscribeForLatestEvent(NewEventCallback callback){
        firebaseClient.observeIncomingLatestEvent(model->{
            switch (model.getType()){
                case Offer:
                    break;
                case Answer:
                    break;
                case IceCandidate:
                    break;
                case StartCall:
                    callback.onNewEventReceived(model);
                    break;
            }
        });
    }
}
