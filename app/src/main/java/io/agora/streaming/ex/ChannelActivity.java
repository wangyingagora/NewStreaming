package io.agora.streaming.ex;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import io.agora.streaming.model.Message;
import io.agora.streaming.model.User;
import io.agora.streaming.ui.InChannelMessageListAdapter;
import io.agora.streaming.ui.MessageListDecoration;
import io.agora.streaming.utils.Utils;

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

    private ArrayList<String> mListTranscode;
    private TranscodeListAdapter mTranscodeListAdapter;

    private RelativeLayout mContainerLayout;
    private LinearLayout mInnerLayout;
    private LinearLayout mOuterLayout;
    private RelativeLayout mOuterTopLayout;
    private RecyclerView mVideoListView;
    private VideoAdapter mVideoAdapter;

    private PublisherListAdapter mPublishersAdapter;

    private InChannelMessageListAdapter mMessageAdapter;
    private ArrayList<Message> mMessageList;

    private int mBigUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel_ex);

        mUserInfo = new HashMap<>();
        mListTranscode = new ArrayList<>();
        mTranscodeListAdapter = new TranscodeListAdapter(this, (ArrayList) mListTranscode);
        mTranscodeListAdapter.setOnRTMPItemDeleteListener(new TranscodeListAdapter.OnRTMPItemDeleteListener() {
            @Override
            public void onDelete(String url) {
                if (url == null) return;
                mLivePublisher.removeStreamUrl(url);
            }
        });

        mPublishersAdapter = new PublisherListAdapter(this, new ArrayList<SubscribeType>(), new SubscribeListener() {
            @Override
            public void subscribe(final int uid, final Media media, final VideoLayout layout, final StreamFormat format) {
                subscribePublisher(uid, media, layout, format);
            }

            @Override
            public void unsubscribe(int uid) {
                mSubscriber.unsubscribe(uid);
                //mUserInfo.remove(uid);
                mUserInfo.get(uid).hasSubscribed = false;
                //mVideoAdapter.updateVideoData(getSmallVideoUser());
                relayoutTranscoding(mCustomTranscoding.layout);
            }
        });

        Intent intent = getIntent();

        boolean enableVideo = intent.getBooleanExtra(AgoraConstans.ACTION_ENABLE_VIDEO, true);
        String channelName = intent.getStringExtra(AgoraConstans.ACTION_KEY_CHANNEL_NAME);

        int width = intent.getIntExtra(AgoraConstans.ACTION_TRANSFORM_WIDTH, 0);
        int height =  intent.getIntExtra(AgoraConstans.ACTION_TRANSFORM_HEIGHT , 0);
        int bitrate = intent.getIntExtra(AgoraConstans.ACTION_TRANSFORM_BITRATE, 0);
        initTranscoding(width, height, bitrate);

        initEngine(channelName, enableVideo);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveEngine.destroy();
    }

    private void initTranscoding(int width, int height, int bitrate) {
        mCustomTranscoding = new CustomTranscoding();
        mCustomTranscoding.width = width;
        mCustomTranscoding.height = height;
        mCustomTranscoding.bitrate = bitrate;
        mCustomTranscoding.layout = CustomTranscoding.LAYOUT_DEFAULT;
        if (mCustomTranscoding.framerate == 0) {
            mCustomTranscoding.framerate = 15;
        }
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
            public void onJoinChannel(final String channel, final int uid, int elapsed) {
                Log.e(TAG, "onJoinChannel: channel: " + channel + ", uid: " + uid);

                //mMyselfId = uid;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBigUserId = uid;
                        createViewableUser(uid, true);
                        updateUI(channel);
                    }
                });
                sendMessageOnMainThread(new Message(new User(uid, String.valueOf(uid)), new String("user " + uid + " joined " + channel)));
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
                sendMessageOnMainThread(new Message(new User(mMyselfId, String.valueOf(mMyselfId)), new String("connection interrupted")));
            }

            @Override
            public void onConnectionLost() {
                sendMessageOnMainThread(new Message(new User(mMyselfId, String.valueOf(mMyselfId)), new String("connection lost")));
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
                sendMessageOnMainThread(new Message(new User(mMyselfId, String.valueOf(mMyselfId)), new String("Publish success(pub)" )));
            }

            @Override
            public void onPublishedFailed(String url, int errorCode) {
                sendMessageOnMainThread(new Message(new User(mMyselfId, String.valueOf(mMyselfId)), new String("Publish failed(pub): " + errorCode)));
            }

            @Override
            public void onUnpublished(String url) {
                sendMessageOnMainThread(new Message(new User(mMyselfId, String.valueOf(mMyselfId)), new String("unpublished by(pub)")));
            }

            @Override
            public void onPublisherTranscodingUpdated(LivePublisher publisher) {
                sendMessageOnMainThread(new Message(new User(mMyselfId, String.valueOf(mMyselfId)), new String(new Date() + ": Publisher transcoding updated" )));
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
                sendMessageOnMainThread(new Message(new User(uid, String.valueOf(uid)), new String("Published(sub) by " + (uid & 0xFFFFFFFFL))));
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
                sendMessageOnMainThread(new Message(new User(uid, String.valueOf(uid)), new String("unpublished by(sub) " + (uid & 0xFFFFFFFFL))));
            }

            @Override
            public void onStreamTypeChangedTo(int streamType, int uid) {
                sendMessageOnMainThread(new Message(new User(uid, String.valueOf(uid)), new String("stream type changed to " + streamType)));
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

        ImageView publishersView = findViewForId(R.id.action_publishers);
        publishersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPublisherList();
            }
        });

        ImageView rtmpView = findViewForId(R.id.rtmp);
        rtmpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTranscodingDialog();
            }
        });

        ImageView mediaTypeView = findViewForId(R.id.action_media_type);
        mediaTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMediaTypeDialog();
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


        mVideoAdapter = new VideoAdapter(this, Utils.getSmallVideoUser(mUserInfo, mBigUserId));
        mVideoAdapter.setOnVideoViewClickedListener(new VideoAdapter.OnVideoViewClickedListener() {
            @Override
            public void onVideoViewClicked(int uid) {
                doVideoViewClicked(uid);
            }
        });
        mVideoListView.setLayoutManager(gridLayoutManager);
        mVideoListView.setAdapter(mVideoAdapter);

        initMessageList();
    }

    private void updateUI(String channelName) {
        TextView channelView = findViewForId(R.id.channel_name);
        channelView.setText(channelName);
    }

    private void initMessageList() {
        mMessageList = new ArrayList<>();
        RecyclerView msgListView = (RecyclerView) findViewById(R.id.msg_list);

        mMessageAdapter = new InChannelMessageListAdapter(this, mMessageList);
        mMessageAdapter.setHasStableIds(true);

        msgListView.setAdapter(mMessageAdapter);
        msgListView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        msgListView.addItemDecoration(new MessageListDecoration());
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
            if (localUser.view.getParent() != null) {
                Utils.removeViewFromParent(localUser.view);
            }
            mInnerLayout.addView(localUser.view);

            mLivePublisher.setVideoProfile(mCustomTranscoding.width, mCustomTranscoding.width, mCustomTranscoding.framerate, mCustomTranscoding.bitrate);
            mLiveEngine.startPreview(localUser.view, Constants.RENDER_MODE_HIDDEN);
            mLivePublisher.publishWithPermissionKey("");
        } else {
            view.setTag(false);
            view.clearColorFilter();
            mLivePublisher.unpublish();
            /*
            ArrayList<UserInfo> smallUsers = Utils.getSmallVideoUser(mUserInfo, mBigUserId);
            for (UserInfo user : smallUsers) {
                int uid = user.uid;
                mSubscriber.unsubscribe(user.uid);
                mUserInfo.get(uid).hasSubscribed = false;
            }
            */
            mLiveEngine.stopPreview();
            mInnerLayout.removeAllViews();
        }
    }

    private void endCall() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLivePublisher.unpublish();
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

    private void doVideoViewClicked(int uid) {
        if (mCustomTranscoding.layout == CustomTranscoding.LAYOUT_DEFAULT) {
            return;
        } else if(mCustomTranscoding.layout == CustomTranscoding.LAYOUT_FLOAT){
            mBigUserId = uid;
        } else if(mCustomTranscoding.layout == CustomTranscoding.LAYOUT_TITLE){
            mBigUserId = uid;
        } else if(mCustomTranscoding.layout == CustomTranscoding.LAYOUT_MATRIX){
            return;
        }

        setTranscoding(mCustomTranscoding);
    }

    /*
    private ArrayList<UserInfo> getSmallVideoUser() {
        ArrayList<UserInfo> users = new ArrayList<>();
        if (mCustomTranscoding.layout == CustomTranscoding.LAYOUT_DEFAULT) {
        } else if (mCustomTranscoding.layout == CustomTranscoding.LAYOUT_FLOAT) {
            users = Utils.getPublishers(mUserInfo, true);
        } else if (mCustomTranscoding.layout == CustomTranscoding.LAYOUT_TITLE) {
            users = Utils.getPublishers(mUserInfo, true);
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
    */

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
        ArrayList<UserInfo> smallVideoUsers = Utils.getSmallVideoUser(mUserInfo, mBigUserId);
        if (mTransCoding.layout == CustomTranscoding.LAYOUT_DEFAULT) {
            transcodingUsers = null;
        } else if(mTransCoding.layout == CustomTranscoding.LAYOUT_FLOAT){
            transcodingUsers = TranscodingLayoutEx.floatLayout(mBigUserId, smallVideoUsers, 0, mTransCoding.width, mTransCoding.height);
        } else if(mTransCoding.layout == CustomTranscoding.LAYOUT_TITLE){
            transcodingUsers = TranscodingLayoutEx.titleLayout(mBigUserId, smallVideoUsers, 0, mTransCoding.width, mTransCoding.height);
        } else if(mTransCoding.layout == CustomTranscoding.LAYOUT_MATRIX){
            transcodingUsers = TranscodingLayoutEx.martixLayout(mMyselfId, smallVideoUsers, 0, mTransCoding.width, mTransCoding.height);
        }

        mTransCoding.setUsers(transcodingUsers);
        //mVideoAdapter.updateVideoData(smallVideoUsers);
        mLivePublisher.setLiveTranscoding(mTransCoding);

        relayoutTranscoding(mTransCoding.layout);
    }

    private void relayoutTranscoding(int layout) {
        ArrayList<UserInfo> smallVideoUsers = new ArrayList<>();
        UserInfo bigUser = mUserInfo.get(mBigUserId);
        if (layout == CustomTranscoding.LAYOUT_DEFAULT) {
            mOuterTopLayout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            bigUser.view.setLayoutParams(params);
            bigUser.view.setZOrderOnTop(false);
            bigUser.view.setZOrderMediaOverlay(false);
            mInnerLayout.removeAllViews();
            if (bigUser.view.getParent() != null) {
                Utils.removeViewFromParent(bigUser.view);
            }
            mInnerLayout.addView(bigUser.view);
            Log.e(TAG, "Default small size: " + smallVideoUsers.size());
            ArrayList<UserInfo> tmp = Utils.getSmallVideoUser(mUserInfo, mBigUserId);
            for (int i = 0; i < tmp.size(); ++i) {
                Utils.removeViewFromParent(tmp.get(i).view);
            }
            mVideoListView.setVisibility(View.GONE);
            //mVideoAdapter.updateVideoData(smallVideoUsers);
        } else if(layout == CustomTranscoding.LAYOUT_FLOAT){
            mOuterTopLayout.setVisibility(View.GONE);
            mVideoListView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            bigUser.view.setLayoutParams(params);
            bigUser.view.setZOrderOnTop(false);
            bigUser.view.setZOrderMediaOverlay(false);
            mInnerLayout.removeAllViews();
            if (bigUser.view.getParent() != null) {
                Utils.removeViewFromParent(bigUser.view);
            }
            mInnerLayout.addView(bigUser.view);
            smallVideoUsers = Utils.getSmallVideoUser(mUserInfo, mBigUserId);
           // mVideoAdapter.updateVideoData(smallVideoUsers);
        } else if(layout == CustomTranscoding.LAYOUT_TITLE){
            mOuterTopLayout.setVisibility(View.VISIBLE);
            mVideoListView.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            bigUser.view.setLayoutParams(params);
            bigUser.view.setZOrderOnTop(false);
            bigUser.view.setZOrderMediaOverlay(false);
            mInnerLayout.removeAllViews();
            if (bigUser.view.getParent() != null) {
                Utils.removeViewFromParent(bigUser.view);
            }
            mOuterTopLayout.addView(bigUser.view);
            smallVideoUsers = Utils.getSmallVideoUser(mUserInfo, mBigUserId);
            //mVideoAdapter.updateVideoData(smallVideoUsers);
        } else if(layout == CustomTranscoding.LAYOUT_MATRIX){
            mVideoListView.setVisibility(View.VISIBLE);
            /*
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            bigUser.view.setLayoutParams(params);
            */
            bigUser.view.setZOrderOnTop(true);
            bigUser.view.setZOrderMediaOverlay(true);
            mInnerLayout.removeAllViews();
            mOuterTopLayout.removeAllViews();
            if (bigUser.view.getParent() != null) {
                Utils.removeViewFromParent(bigUser.view);
            }
            mOuterTopLayout.setVisibility(View.GONE);
            smallVideoUsers = Utils.getSmallVideoUser(mUserInfo, mBigUserId);
            smallVideoUsers.add(mUserInfo.get(mBigUserId));
           // mVideoAdapter.updateVideoData(smallVideoUsers);
        }

        for (int i = 0; i < smallVideoUsers.size(); ++i) {
            smallVideoUsers.get(i).view.setZOrderOnTop(true);
            smallVideoUsers.get(i).view.setZOrderMediaOverlay(true);
        }

        Log.e(TAG, "Small size: " + smallVideoUsers.size());
        mVideoAdapter.updateVideoData(smallVideoUsers);

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

    private void showMediaTypeDialog() {
        MediaTypeDialog dialog = new MediaTypeDialog();
        dialog.showDialog(this, new MediaTypeDialog.OnMediaTypeChangedListener() {
            @Override
            public void onTypeChanged(Media type) {
                setMediaType(type);
            }
        });
    }

    private void addNewPublisher(int uid, int streamType) {
        if (mUserInfo.get(uid) != null && (mUserInfo.get(uid).uid == uid)) return;

        UserInfo publisher = createViewableUser(uid, false);

        ArrayList<SubscribeType> publishers = new ArrayList<>();
        ArrayList<UserInfo> users = Utils.getPublishers(mUserInfo, false);
        for (UserInfo user : users) {
            SubscribeType data = new SubscribeType(user.uid + "", user.hasSubscribed, Media.AV, VideoLayout.Hidden, StreamFormat.High);
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
        } else if (media == Media.NONE) {
            mediaType = Constants.MEDIA_TYPE_NONE;
        }

        if (VideoLayout.Adaptive == layout) {
            videoLayout = 3;
        } else if (VideoLayout.Fit == layout) {
            videoLayout = 2;
        } else if (VideoLayout.Hidden == layout) {
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

        ArrayList<UserInfo> publishers = Utils.getSmallVideoUser(mUserInfo, mBigUserId);
//        Log.e(TAG, "start subscribe: " + publishers.size());
//        for (int i = 0; i < publishers.size(); ++i) {
//            Log.e(TAG, "uid: " + publishers.get(i).uid);
//        }
        Log.e(TAG, "all user size: " + mUserInfo.size());

        mVideoAdapter.updateVideoData(publishers);
        //user.view.setZOrderOnTop(true);
        mSubscriber.subscribe(uid, mediaType, user.view, videoLayout, streamType);
        //setTranscoding(mCustomTranscoding);
    }

    private void processUnPublishForSubscriber(int host) {
        mSubscriber.unsubscribe(host);
        mUserInfo.remove(host);
        //mVideoAdapter.updateVideoData(getSmallVideoUser());
        relayoutTranscoding(mCustomTranscoding.layout);

        ArrayList<SubscribeType> publishers = new ArrayList<>();
        ArrayList<UserInfo> users = Utils.getPublishers(mUserInfo, false);
        for (UserInfo user : users) {
            SubscribeType data = new SubscribeType(user.uid + "", user.hasSubscribed, Media.AV, VideoLayout.Hidden, StreamFormat.High);
            publishers.add(data);
        }
        mPublishersAdapter.updatePublishers(publishers);
    }

    private void setTranscodingDialog() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        View view = LayoutInflater.from(this).inflate(R.layout.layout_rtmp, null);

        Button transcodingButton = (Button) view.findViewById(R.id.btn_transcoding);
        Button noTranscodingButton = (Button) view.findViewById(R.id.btn_none_transcoding);
        final EditText editText = (EditText)view.findViewById(R.id.transcode_room);
        ListView listView = (ListView) view.findViewById(R.id.listview_transcodeing);
        listView.setAdapter(mTranscodeListAdapter);

        transcodingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //actionTranscodeDialog();
                addTranscodingUrl(editText.getText().toString().trim());
            }
        });

        noTranscodingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNoneTranscodingUrl(editText.getText().toString().trim());
            }
        });

        builder = new AlertDialog.Builder(this);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void addTranscodingUrl(String room) {
        if (TextUtils.isEmpty(room)) {
            return;
        }

        String url = Utils.getStreamUrl(room);

        if (Utils.hasExists(mListTranscode, url)) {
            Utils.showSimpleDialog(ChannelActivity.this, "Existed name");
            return;
        }
        mListTranscode.add(url);
        int result = mLivePublisher.addStreamUrl(url, true);

        mTranscodeListAdapter.notifyDataSetChanged();

        sendMessageOnMainThread(new Message(new User(mMyselfId, String.valueOf(mMyselfId)), new String("publish transcode url(" + result + "): " + url)));
    }

    private void addNoneTranscodingUrl(String room) {
        if (TextUtils.isEmpty(room)) {
            return;
        }

        String url = Utils.getStreamUrl(room);

        if (Utils.hasExists(mListTranscode, url)) {
            Utils.showSimpleDialog(ChannelActivity.this, "Existed name");
            return;
        }
        mListTranscode.add(url);
        int result = mLivePublisher.addStreamUrl(url, false);

        mTranscodeListAdapter.notifyDataSetChanged();

        sendMessageOnMainThread(new Message(new User(mMyselfId, String.valueOf(mMyselfId)), new String("publish none transcode url(" + result + "): " + url)));
    }

    private void sendMessage(Message msg) {
        mMessageList.add(msg);

        int MAX_MESSAGE_COUNT = 16;

        if (mMessageList.size() > MAX_MESSAGE_COUNT) {
            int toRemove = mMessageList.size() - MAX_MESSAGE_COUNT;
            for (int i = 0; i < toRemove; i++) {
                mMessageList.remove(i);
            }
        }

        mMessageAdapter.notifyDataSetChanged();
    }

    private void sendMessageOnMainThread(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendMessage(message);
            }
        });
    }

    private void setMediaType(Media type) {
        ImageView publishView = findViewForId(R.id.publish);
        boolean isPublish = (boolean)publishView.getTag();

        int mediaType;
        if (type == Media.AV) {
            mediaType = Constants.MEDIA_TYPE_AUDIO_AND_VIDEO;
        } else if (type == Media.AUDIO) {
            mediaType = Constants.MEDIA_TYPE_AUDIO_ONLY;
        } else if (type == Media.VIDEO) {
            mediaType = Constants.MEDIA_TYPE_VIDEO_ONLY;
        } else {
            mediaType = Constants.MEDIA_TYPE_NONE;
        }

        if (isPublish) {
            mLivePublisher.setMediaType(mediaType);
        }

        for (HashMap.Entry<Integer, UserInfo> entry : mUserInfo.entrySet()) {
            UserInfo user = entry.getValue();
            if (user.isLocal || !user.hasSubscribed) {
                continue;
            }
            mSubscriber.subscribe(user.uid, mediaType, user.view, user.renderMode, user.streamType);
        }
    }
}
