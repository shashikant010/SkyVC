package com.example.skyvc.webrtc;

import android.content.Context;
import android.widget.LinearLayout;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;

import java.util.ArrayList;
import java.util.List;

public class WebRTCClient {

    private final Context context;
    private final String username;

    private PeerConnectionFactory peerConnectionFactory;

    private List<PeerConnection.IceServer> iceServer = new ArrayList<>();

    private PeerConnection peerConnection;

    private EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
    public WebRTCClient(Context context, PeerConnection.Observer observer , String username){
        this.context = context;
        this.username = username;
        initPeerConnectionFactory();
        peerConnectionFactory = createPeerConnectionFactory();
        iceServer.add(PeerConnection.IceServer.builder( "turn:a.relay.metered.ca:443?transport=tcp")
        .setUsername("83eebabf8b4cce9d5dbcb649")
                .setPassword("2D7JvfkOQtBdYW3R").createIceServer());
        peerConnection = createPeerConnection(observer);

    }

    private void initPeerConnectionFactory(){
        PeerConnectionFactory.InitializationOptions options = PeerConnectionFactory.InitializationOptions.builder(context).setFieldTrials("webRTC-H264HighProfile/Enabled/")
                .setEnableInternalTracer(true).createInitializationOptions();
        PeerConnectionFactory.initialize(options);

    }


    private PeerConnectionFactory createPeerConnectionFactory(){
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        options.disableEncryption=false;
        options.disableNetworkMonitor=false;
        return PeerConnectionFactory.builder()
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBaseContext,true,true))
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBaseContext))
                .setOptions(options).createPeerConnectionFactory();

    }

    private PeerConnection createPeerConnection (PeerConnection.Observer observer){
        return peerConnectionFactory.createPeerConnection(iceServer,observer);
    }


//initialize ui like surface view renderers

    public void initSurfaceViewRenderer(SurfaceViewRenderer viewRenderer){
            viewRenderer.setEnableHardwareScaler(true);
            viewRenderer.setMirror(true);
            viewRenderer.init(eglBaseContext,null);

    }



    //negotiating sections like call and answer






}
