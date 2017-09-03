package io.agora.streaming.ex;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
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
import io.agora.live.LiveTranscoding;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.streaming.R;
import io.agora.streaming.model.ConstantApp;

/**
 * Created by eaglewangy on 30/08/2017.
 */

public class ChannelActivity extends AgoraBaseActivity {
    private final static String TAG = ChannelActivity.class.getSimpleName();
    private LiveEngine mLiveEngine;
    private LivePublisher mLivePublisher;
    private LiveSubscriber mSubscriber;
    private int mMyselfId = 0;

    private Map<Integer, UserInfo> mUserInfo;
    private CustomTranscoding mCustomTranscoding;

    private RelativeLayout mContainerLayout;
    private LinearLayout mInnerLayout;
    private LinearLayout mOuterLayout;
    private RelativeLayout mOuterTopLayout;
    private RecyclerView mVideoListView;
    private VideoAdapter mVideoAdapter;

    //private ArrayList<UrlData> mAllPublishers;
    private PublisherListAdapter mPublishersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel_ex);

        mUserInfo = new HashMap<>();
        //mAllPublishers = new ArrayList<>();
        mPublishersAdapter = new PublisherListAdapter(this, new ArrayList<UrlData>(), new SubscribeListener() {
            @Override
            public void subscribe(final int uid, final Media media, final VideoLayout layout, final StreamFormat format) {
                Log.e(TAG, "subscribe to: " + uid);
                subscribePublisher(uid, media, layout, format);

                //mVideoAdapter.updateVideoData(getSmallVideoUser());
            }

            @Override
            public void unsubscribe(int uid) {
                Log.e(TAG, "unsubscribe to: " + uid);
                mSubscriber.unsubscribe(uid);
                //mUserInfo.remove(uid);
                mUserInfo.get(uid).hasSubscribed = false;
                mVideoAdapter.updateVideoData(getSmallVideoUser());
            }
        });

        Intent intent = getIntent();

        boolean enableVideo = intent.getBooleanExtra(ConstantApp.ACTION_ENABLE_VIDEO, true);
        String channelName = intent.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);

        int width = intent.getIntExtra(ConstantApp.ACTION_TRANSFORM_WIDTH, 0);
        int height =  intent.getIntExtra(ConstantApp.ACTION_TRANSFORM_HEIGHT , 0);
        int bitrate = intent.getIntExtra(ConstantApp.ACTION_TRANSFORM_BITRATE, 0);
        initTranscoding(width, height, bitrate);

        initEngine(channelName, enableVideo);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initTranscoding(int width, int height, int bitrate) {
        mCustomTranscoding = new CustomTranscoding();
        mCustomTranscoding.width = width;
        mCustomTranscoding.height = height;
        mCustomTranscoding.bitrate = bitrate;
        mCustomTranscoding.layout = CustomTranscoding.LAYOUT_DEFAULT;
    }

    private void initEngine(String channelName, boolean enableVideo) {
        mLiveEngine = LiveEngine.createLiveEngine(this, getString(R.string.agora_app_id), new LiveEngineHandler() {
            @Override
            public void onWarning(int warningCode) {
                Log.e(TAG, "onWarning: " + warningCode);
            }

            @Override
            public void onError(int errorCode) {
                Log.e(TAG, "onError: " + errorCode);
            }

            @Override
            public void onJoinChannel(String channel, final int uid, int elapsed) {
                Log.e(TAG, "onJoinChannel: channel: " + channel + ", uid: " + uid);

                //mMyselfId = uid;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createViewableUser(uid, true);
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

        mLivePublisher = new LivePublisher(mLiveEngine, new LivePublisherHandler() {
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
        int result = mLiveEngine.joinChannel(channelName, null, config, mMyselfId);

        mSubscriber = new LiveSubscriber(mLiveEngine, new LiveSubscriberHandler() {
            @Override
            public void publishedByHostUid(final int uid, final int streamType) {
                Log.e(TAG, "publishedByHostUid: " + uid + ", streamType: " + streamType);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addNewPublisher(uid, streamType);
                    }
                });
            }

            @Override
            public void unpublishedByHostUid(final int uid) {
                Log.e(TAG, "unpublishedByHostUid: " + uid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processUnPublishForSubscriber(uid);
                    }
                });
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
        mContainerLayout = findViewForId(R.id.container_layout);
        mInnerLayout = findViewForId(R.id.inner_container);
        mOuterLayout = findViewForId(R.id.outer_container);
        mOuterTopLayout = findViewForId(R.id.top_layout);
        mVideoListView = findViewForId(R.id.video_view_list);

        ImageView publishImageView = findViewForId(R.id.publish);
        publishImageView.setTag(false);
        publishImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPublish((ImageView) v);
            }
        });

        ImageView customTrancodingView = findViewForId(R.id.custom_settings);
        customTrancodingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransCodingSettingDialog();
            }
        });

        ImageView publisersView = findViewForId(R.id.action_publishers);
        publisersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPublisherList();
            }
        });

        ImageView endCallView = findViewForId(R.id.end_call);
        endCallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        //recycleview list item cannot be reused
        mVideoListView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, AgoraConstans.VIDEO_COLUMNS);
        /*
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
            }
        });

        mVideoListView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .colorResId(R.color.colorAccent)
                        .sizeResId(R.dimen.video_item_divider)
                        .margin(0, 0)
                        .build());
                        ArrayList<UserInfo> users = new ArrayList<>();
        for (int i = 0; i < 32; ++i) {
            UserInfo user = new UserInfo();
            user.uid = i;
            user.view = RtcEngine.CreateRendererView(this);
            user.view.setZOrderOnTop(false);
            user.view.setZOrderMediaOverlay(false);

            users.add(user);
        }
        */


        mVideoAdapter = new VideoAdapter(this, getSmallVideoUser());
        mVideoListView.setLayoutManager(gridLayoutManager);
        mVideoListView.setAdapter(mVideoAdapter);

    }

    private void doPublish(ImageView view) {
        UserInfo localUser = getLocalUser();
        if (localUser == null) {
            showToast("The local user dose not join channel");
            return;
        }

        boolean isPublish = (boolean)view.getTag();

        if (!isPublish) {
            view.setTag(true);
            view.setColorFilter(ContextCompat.getColor(this, R.color.agora_blue), PorterDuff.Mode.MULTIPLY);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            localUser.view.setLayoutParams(params);
            localUser.view.setZOrderOnTop(false);
            mInnerLayout.addView(localUser.view);
            mLiveEngine.startPreview(localUser.view, Constants.RENDER_MODE_HIDDEN);
            mLivePublisher.publishWithPermissionKey("");
        } else {
            view.setTag(false);
            view.clearColorFilter();
            mLivePublisher.unpublish();
            mLiveEngine.stopPreview();
            //RelativeLayout containerView = findViewForId(R.id.container);
            //containerView.removeAllViews();
            mInnerLayout.removeAllViews();
        }
    }

    private void endCall() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLiveEngine.leaveChannel();
            }
        });

        mUserInfo.clear();
        finish();
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

    private boolean removeUser(int uid) {
        Iterator iterator = mUserInfo.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Integer id = (Integer)entry.getKey();
            if (id.intValue() == uid) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    private UserInfo createViewableUser(int uid, boolean isLocal) {
        if (uid == 0) {
            Log.e(TAG, "uid is 0, invalid user");
            return null;
        }

        if (isLocal) {
            mMyselfId = uid;
        }
        //UserInfo user = getLocalUser();
        SurfaceView surfaceView = RtcEngine.CreateRendererView(this);
        surfaceView.setZOrderOnTop(false);
        surfaceView.setZOrderMediaOverlay(false);

        UserInfo user = new UserInfo();
        user.uid = uid;
        user.isLocal = isLocal;
        user.hasSubscribed = false;
        user.view = surfaceView;

        mUserInfo.put(uid, user);

        for (Integer key : mUserInfo.keySet()) {
            UserInfo u = mUserInfo.get(key);
            Log.e(TAG, "User: " + u.uid + ", " + u.isLocal + ", " + u.hasSubscribed);
        }
        Log.e(TAG, "total size: " + mUserInfo.size());

        return user;
    }

    private ArrayList<UserInfo> getSmallVideoUser() {
        ArrayList<UserInfo> users = new ArrayList<>();
        if (mCustomTranscoding.layout == CustomTranscoding.LAYOUT_DEFAULT) {
        } else if (mCustomTranscoding.layout == CustomTranscoding.LAYOUT_FLOAT) {
            users = getPublishers(true);
        } else if (mCustomTranscoding.layout == CustomTranscoding.LAYOUT_TITLE) {
            users = getPublishers(true);
        } else if (mCustomTranscoding.layout == CustomTranscoding.LAYOUT_MATRIX) {
            Iterator<Map.Entry<Integer, UserInfo>> itrMap = mUserInfo.entrySet().iterator();
            while (itrMap.hasNext()) {
                Map.Entry<Integer, UserInfo> entry = itrMap.next();
                UserInfo user = entry.getValue();
                if (!user.isLocal && !user.hasSubscribed) {
                    continue;
                }
                users.add(user);
            }
        }
        return users;
    }

    private ArrayList<UserInfo> getPublishers(boolean isOnlySubscribed) {
        ArrayList<UserInfo> users = new ArrayList<>();
        Iterator<Map.Entry<Integer, UserInfo>> iterator = mUserInfo.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, UserInfo> entry = iterator.next();
            UserInfo user = entry.getValue();
            if (user.isLocal) {
                continue;
            }
            if (isOnlySubscribed) {
                if (!user.hasSubscribed) {
                    continue;
                }
            }
            users.add(user);
        }
        return users;
    }

    private void showTransCodingSettingDialog() {
        CustomTranscodingDialog dialog = new CustomTranscodingDialog(this, mCustomTranscoding);
        dialog.setOnUpdateTranscodingListener(new CustomTranscodingDialog.OnUpdateTranscodingListener() {
            @Override
            public void onUpdateTranscoding(CustomTranscoding customTranscoding) {
                setTranscoding(customTranscoding);
            }
        });
        dialog.showDialog();
    }

    private void setTranscoding(CustomTranscoding mTransCoding) {
        if (!mTransCoding.isEnabled) {
            showToast("Transcoding is disabled");
            return;
        }
        ArrayList<LiveTranscoding.TranscodingUser> transcodingUsers = null;
        ArrayList<UserInfo> smallVideoUsers = getSmallVideoUser();
        if (mTransCoding.layout == CustomTranscoding.LAYOUT_DEFAULT) {
            transcodingUsers = null;
        } else if(mTransCoding.layout == CustomTranscoding.LAYOUT_FLOAT){
            transcodingUsers = TranscodingLayoutEx.floatLayout(mMyselfId, smallVideoUsers, 0, mTransCoding.width, mTransCoding.height);
        } else if(mTransCoding.layout == CustomTranscoding.LAYOUT_TITLE){
            transcodingUsers = TranscodingLayoutEx.titleLayout(mMyselfId, smallVideoUsers, 0, mTransCoding.width, mTransCoding.height);
        } else if(mTransCoding.layout == CustomTranscoding.LAYOUT_MATRIX){
            transcodingUsers = TranscodingLayoutEx.martixLayout(mMyselfId, smallVideoUsers, 0, mTransCoding.width, mTransCoding.height);
        }

        mTransCoding.setUsers(transcodingUsers);

        mVideoAdapter.updateVideoData(smallVideoUsers);

        mLivePublisher.setLiveTranscoding(mTransCoding);
    }

    private void showPublisherList() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.myview, null);
        ListView publisherListView = (ListView) layout.findViewById(R.id.mylistview);
        publisherListView.setAdapter(mPublishersAdapter);

        builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void addNewPublisher(int uid, int streamType) {
        if (mUserInfo.get(uid) != null && (mUserInfo.get(uid).uid == uid)) return;

        UserInfo publisher = createViewableUser(uid, false);

        ArrayList<UrlData> publishers = new ArrayList<>();
        ArrayList<UserInfo> users = getPublishers(false);
        for (UserInfo user : users) {
            UrlData data = new UrlData(user.uid + "", user.hasSubscribed, Media.AV, VideoLayout.Hideden, StreamFormat.High);
            publishers.add(data);
        }
        mPublishersAdapter.updatePublishers(publishers);
    }
    
    public void subscribePublisher(int uid, Media media, VideoLayout layout, StreamFormat format) {
        int mediaType = Constants.MEDIA_TYPE_NONE;
        int videoLayout = 0;
        int streamType = 0;

        if (media == Media.AV) {
            mediaType = Constants.MEDIA_TYPE_AUDIO_AND_VIDEO;
        } else if (media == Media.AUDIO) {
            mediaType = Constants.MEDIA_TYPE_AUDIO_ONLY;
        } else if (media == Media.VIDEO) {
            mediaType = Constants.MEDIA_TYPE_VIDEO_ONLY;
        } else if (media == Media.NONIE) {
            mediaType = Constants.MEDIA_TYPE_NONE;
        }

        if (VideoLayout.Adaptive == layout) {
            videoLayout = 3;
        } else if (VideoLayout.Fit == layout) {
            videoLayout = 2;
        } else if (VideoLayout.Hideden == layout) {
            videoLayout = 1;
        }

        if (StreamFormat.High == format) {
            streamType = 0;
        } else if (StreamFormat.Low == format) {
            streamType = 1;
        }

        UserInfo user = mUserInfo.get(uid);
        user.hasSubscribed = true;
        user.view.setZOrderOnTop(true);
        user.view.setZOrderMediaOverlay(true);

        ArrayList<UserInfo> publishers = getSmallVideoUser();
        Log.e(TAG, "start subscribe: " + publishers.size());
        for (int i = 0; i < publishers.size(); ++i) {
            Log.e(TAG, "uid: " + publishers.get(i).uid);
        }
        Log.e(TAG, "all user size: " + mUserInfo.size());

        mVideoAdapter.updateVideoData(publishers);
        //user.view.setZOrderOnTop(true);
        mSubscriber.subscribe(uid, mediaType, user.view, videoLayout, streamType);
        //setTranscoding(mCustomTranscoding);
    }

    private void processUnPublishForSubscriber(int host) {
        mSubscriber.unsubscribe(host);
        mUserInfo.remove(host);
        mVideoAdapter.updateVideoData(getSmallVideoUser());

        ArrayList<UrlData> publishers = new ArrayList<>();
        ArrayList<UserInfo> users = getPublishers(false);
        for (UserInfo user : users) {
            UrlData data = new UrlData(user.uid + "", user.hasSubscribed, Media.AV, VideoLayout.Hideden, StreamFormat.High);
            publishers.add(data);
        }
        mPublishersAdapter.updatePublishers(publishers);
    }
}
