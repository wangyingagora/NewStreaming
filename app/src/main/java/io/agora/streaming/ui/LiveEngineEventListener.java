package io.agora.streaming.ui;

/**
 * Created by xdf20 on 2017/8/22.
 */

public interface LiveEngineEventListener {
    void onPublishedForSubscriber(int uid);
    void onUnPublishedForScriber(int uid);
    void onPublishSuccessByPublisher(String url);
    void onPublishedFailedByPublisher(String url, int errorCode);
    void onUnpublishedByPublisher(String url);
    void onPublisherTranscodingUpdated();
}
