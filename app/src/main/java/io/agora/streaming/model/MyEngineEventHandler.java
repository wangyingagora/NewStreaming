package io.agora.streaming.model;

import android.content.Context;

import io.agora.live.*;
import io.agora.rtc.RtcEngine;
import io.agora.streaming.ui.LiveEngineEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class MyEngineEventHandler {
    public MyEngineEventHandler(Context ctx, EngineConfig config) {
        this.mContext = ctx;
        this.mConfig = config;
    }

    private LiveEngineEventListener mLiveEngineEventListener;
    private final EngineConfig mConfig;

    private final Context mContext;

    private final ConcurrentHashMap<AGEventHandler, Integer> mEventHandlerList = new ConcurrentHashMap<>();

    public void setEngineEventCallback(LiveEngineEventListener listener){
             this.mLiveEngineEventListener = listener;
    }

    public void addEventHandler(AGEventHandler handler) {
        this.mEventHandlerList.put(handler, 0);
    }

    public void removeEventHandler(AGEventHandler handler) {
        this.mEventHandlerList.remove(handler);
    }

    final LiveEngineHandler mRtcEventHandler = new LiveEngineHandler() {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        public void onLeaveChannel() {

        }

        @Override
        public void onError(int error) {
            log.debug("onError " + error + " " + RtcEngine.getErrorDescription(error));

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR, error, RtcEngine.getErrorDescription(error));
            }
        }

//        @Override
//        public void onStreamMessage(int uid, int streamId, byte[] data) {
//            log.debug("onStreamMessage " + (uid & 0xFFFFFFFFL) + " " + streamId + " " + Arrays.toString(data));
//
//            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
//            while (it.hasNext()) {
//                AGEventHandler handler = it.next();
//                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_DATA_CHANNEL_MSG, uid, data);
//            }
//        }

        public void onStreamMessageError(int uid, int streamId, int error, int missed, int cached) {
            log.warn("onStreamMessageError " + (uid & 0xFFFFFFFFL) + " " + streamId + " " + error + " " + missed + " " + cached);

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR, error, "on stream msg error " + (uid & 0xFFFFFFFFL) + " " + missed + " " + cached);
            }
        }

        @Override
        public void onConnectionLost() {
            log.debug("onConnectionLost");

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_APP_ERROR, ConstantApp.AppError.NO_NETWORK_CONNECTION);
            }
        }

        @Override
        public void onConnectionInterrupted() {
            log.debug("onConnectionInterrupted");

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_APP_ERROR, ConstantApp.AppError.NO_NETWORK_CONNECTION);
            }
        }

        @Override
        public void onJoinChannel(String channel, int uid, int elapsed) {
            log.debug("onJoinChannel " + channel + " " + uid + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);

            mConfig.mUid = uid;

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onJoinChannelSuccess(channel, uid, elapsed);
            }
        }

        @Override
        public void onRejoinChannel(String channel, int uid, int elapsed) {
            log.debug("onRejoinChannel " + channel + " " + uid + " " + elapsed);
        }

//        @Override
//        public void onAudioRouteChanged(int routing) {
//            log.debug("onAudioRouteChanged " + routing);
//
//            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
//            while (it.hasNext()) {
//                AGEventHandler handler = it.next();
//                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED, routing);
//            }
//        }

        public void onWarning(int warn) {
            log.debug("onWarning " + warn);
        }
    };

    final LivePublisherHandler mLivePublisherHandler = new LivePublisherHandler() {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        public void onPublishSuccess(String url) {
            log.debug("onPublishSuccess " + url);
            mLiveEngineEventListener.onPublishSuccessByPublisher(url);
        }

        @Override
        public void onPublishedFailed(String url, int errorCode) {
            log.debug("onPublishedFailed " + url);
            mLiveEngineEventListener.onPublishedFailedByPublisher(url, errorCode);
        }

        @Override
        public void onUnpublished(String url) {
            log.debug("onUnpublished " + url);
            mLiveEngineEventListener.onUnpublishedByPublisher(url);
        }

        @Override
        public void onPublisherTranscodingUpdated(LivePublisher publisher) {
            log.debug("onPublisherTranscodingUpdated " + publisher);
            mLiveEngineEventListener.onPublisherTranscodingUpdated();
        }
    };

    final LiveSubscriberHandler mLiveSubscriberHandler = new LiveSubscriberHandler() {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        public void publishedByHostUid(int uid, int streamType) {
            mLiveEngineEventListener.onPublishedForSubscriber(uid);
            log.debug("publishedByHostUid " + (uid & 0xFFFFFFFFL) + ", streamType " + streamType);
        }

        @Override
        public void unpublishedByHostUid(int uid) {
            mLiveEngineEventListener.onUnPublishedForScriber(uid);
            log.debug("unpublishedByHostUid " + (uid & 0xFFFFFFFFL));
        }

        @Override
        public void onStreamTypeChangedTo(int streamType, int uid) {
            log.debug("onStreamTypeChangedTo " + (uid & 0xFFFFFFFFL) + ", streamType " + streamType);
        }

        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            log.debug("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + elapsed);
        }

        @Override
        public void onVideoSizeChanged(int uid, int width, int height, int rotation) {
            log.debug("onVideoSizeChanged " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + rotation);
        }
    };

}
