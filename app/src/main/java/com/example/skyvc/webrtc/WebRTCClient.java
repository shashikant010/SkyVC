package com.example.skyvc.webrtc;

import android.content.Context;
import android.widget.LinearLayout;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Capturer;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class WebRTCClient {

    private final Context context;
    private final String username;

    private PeerConnectionFactory peerConnectionFactory;

    private List<PeerConnection.IceServer> iceServer = new ArrayList<>();

    private CameraVideoCapturer videoCapturer;

    private VideoSource localVideoSource;
    private AudioSource localAudioSource;
    private String localTrackId = "local_track";
    private String localStreamId = "local_stream";

    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;

    private MediaStream localStream;

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

        localVideoSource=peerConnectionFactory.createVideoSource(false);
        localAudioSource=peerConnectionFactory.createAudioSource(new MediaConstraints());


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


    public void initLocalSurfaceView(SurfaceViewRenderer view){
        initSurfaceViewRenderer(view);
        startLocalVideoStreaming(view);
    }

    private void startLocalVideoStreaming(SurfaceViewRenderer view) {
        SurfaceTextureHelper helper = SurfaceTextureHelper.create(Thread.currentThread().getName(),eglBaseContext);
        videoCapturer = getVideoCapturer();
        videoCapturer.initialize(helper,context,localVideoSource.getCapturerObserver());
        videoCapturer.startCapture(480,360,15);
        localVideoTrack = peerConnectionFactory.createVideoTrack(localTrackId+"_video",localVideoSource);

        localVideoTrack.addSink(view);

        localAudioTrack=peerConnectionFactory.createAudioTrack(localTrackId+"_audio",localAudioSource);

        localStream = peerConnectionFactory.createLocalMediaStream(localStreamId);
        localStream.addTrack(localAudioTrack);
        localStream.addTrack(localVideoTrack);
        peerConnection.addStream(localStream);


    }

    private CameraVideoCapturer getVideoCapturer(){
        Camera2Enumerator enumerator=new Camera2Enumerator(context);
        String[] deviceNames = enumerator.getDeviceNames();

        for(String device : deviceNames){
            if(enumerator.isFrontFacing(device)){
                return enumerator.createCapturer(device,null);
            }
        }
        throw new IllegalStateException("no front facing camera found");
    }
    public void initRemoteSurfaceView(SurfaceViewRenderer view){
        initSurfaceViewRenderer(view);

    }

    //negotiating sections like call and answer






}
