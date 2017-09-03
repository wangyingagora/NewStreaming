package io.agora.streaming.ex;

/**
 * Created by xdf20 on 2017/8/23.
 */

public interface SubscribeListener {
    void subscribe(int uid, Media media, VideoLayout layout, StreamFormat format);
    void unsubscribe(int uid);
}

