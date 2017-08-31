package io.agora.streaming.ex;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.agora.live.LiveChannelConfig;
import io.agora.live.LiveEngine;
import io.agora.live.LiveEngineHandler;
import io.agora.live.LivePublisher;
import io.agora.live.LivePublisherHandler;
import io.agora.live.LiveStats;
import io.agora.live.LiveSubscriber;
import io.agora.live.LiveSubscriberHandler;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.streaming.R;
import io.agora.streaming.model.ConstantApp;

/**
 * Created by eaglewangy on 30/08/2017.
 */

public class ChannelActivity extends AgoraBaseActivity {
    private final static String TAG = ChannelActivity.class.getSimpleName();
    private LiveEngine mLiveEngin;
    private LivePublisher mLivePublisher;
    private LiveSubscriber mSubscriber;
    private int mMyselfId = 0;

    private Map<Integer, UserInfo> mUserInfo = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel_ex);

        Intent intent = getIntent();

        boolean enableVideo = intent.getBooleanExtra(ConstantApp.ACTION_ENABLE_VIDEO, true);
        String channelName = intent.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);

        initEngine(channelName, enableVideo);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveEngin.leaveChannel();
    }

    private void initEngine(String channelName, boolean enableVideo) {
        mLiveEngin = LiveEngine.createLiveEngine(this, getString(R.string.agora_app_id), new LiveEngineHandler() {
            @Override
            public void onWarning(int warningCode) {
                Log.e(TAG, "onWarning: " + warningCode);
            }

            @Override
            public void onError(int errorCode) {
                Log.e(TAG, "onError: " + errorCode);
            }

            @Override
            public void onJoinChannel(String channel, int uid, int elapsed) {
                Log.e(TAG, "onJoinChannel: channel: " + channel + ", uid: " + uid);

                mMyselfId = uid;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createViewableUser(mMyselfId, true);
                    }
                });
            }

            @Override
            public void onLeaveChannel() {
                Log.e(TAG, "onLeaveChannel");
            }

            @Override
            public void onRejoinChannel(String channel, int uid, int elapsed) {
                Log.e(TAG, "onRejoinChannel");
            }

            @Override
            public void onReportLiveStats(LiveStats stats) {
                //Log.e(TAG, "onReportLiveStats");
            }

            @Override
            public void onConnectionInterrupted() {
                Log.e(TAG, "onConnectionInterrupted");
            }

            @Override
            public void onConnectionLost() {
                Log.e(TAG, "onConnectionLost");
            }

            @Override
            public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
               // Log.e(TAG, "onNetworkQuality");
            }

            @Override
            public void onRequestChannelKey() {
                Log.e(TAG, "onRequestChannelKey");
            }
        });

        mLivePublisher = new LivePublisher(mLiveEngin, new LivePublisherHandler() {
            @Override
            public void onPublishSuccess(String url) {
                Log.e(TAG, "onPublishSuccess: " + url);
            }

            @Override
            public void onPublishedFailed(String url, int errorCode) {
                Log.e(TAG, "onPublishedFailed: " + url + ", errorCode: " + errorCode);
            }

            @Override
            public void onUnpublished(String url) {
                Log.e(TAG, "onUnpublished: " + url);
            }

            @Override
            public void onPublisherTranscodingUpdated(LivePublisher publisher) {
                Log.e(TAG, "onPublisherTranscodingUpdated");
            }
        });

        LiveChannelConfig config = new LiveChannelConfig();
        config.videoEnabled = enableVideo;
        int result = mLiveEngin.joinChannel(channelName, null, config, mMyselfId);

        mSubscriber = new LiveSubscriber(mLiveEngin, new LiveSubscriberHandler() {
            @Override
            public void publishedByHostUid(int uid, int streamType) {
                Log.e(TAG, "publishedByHostUid: " + uid + ", streamType: " + streamType);
            }

            @Override
            public void unpublishedByHostUid(int uid) {
                Log.e(TAG, "unpublishedByHostUid: " + uid);
            }

            @Override
            public void onStreamTypeChangedTo(int streamType, int uid) {
                Log.e(TAG, "onStreamTypeChangedTo: " + streamType + ", uid: " + uid);
            }

            @Override
            public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
                Log.e(TAG, "onFirstRemoteVideoDecoded");
            }

            @Override
            public void onVideoSizeChanged(int uid, int width, int height, int rotation) {
                Log.e(TAG, "onVideoSizeChanged");
            }
        });
    }

    private void initView() {
        ImageView publishImageView = findViewForId(R.id.publish);
        publishImageView.setTag(false);
        publishImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPublish((ImageView) v);
            }
        });
    }

    private void doPublish(ImageView view) {
        UserInfo user = getLocalUser();
        if (user == null) {
            showToast("The local user dose not join channel");
            return;
        }

        boolean isPublish = !(boolean)view.getTag();
        view.setTag(isPublish);
        if (isPublish) {
            RelativeLayout containerView = findViewForId(R.id.container);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            containerView.addView(user.view, 0, params);
            mLiveEngin.getRtcEngine().setVideoProfile(Constants.VIDEO_PROFILE_360P_4, false);
            mLiveEngin.startPreview(user.view, Constants.RENDER_MODE_HIDDEN);
            mLivePublisher.publishWithPermissionKey("");
        } else {

        }
    }

    private UserInfo getLocalUser() {
        UserInfo user = null;
        Iterator iterator = mUserInfo.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Integer id = (Integer)entry.getKey();
            user = (UserInfo) entry.getValue();

            if (user.isLocal) {
                return user;
            }
        }
        return user;
    }

    private UserInfo createViewableUser(int uid, boolean isLocal) {
        if (uid == 0) {
            Log.e(TAG, "uid is 0, invalid user");
            return null;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getApplicationContext());
        surfaceView.setZOrderOnTop(false);
        surfaceView.setZOrderMediaOverlay(false);

        UserInfo user = new UserInfo();
        user.uid = uid;
        user.isLocal = isLocal;
        user.view = surfaceView;

        mUserInfo.put(uid, user);
        return user;
    }
}
