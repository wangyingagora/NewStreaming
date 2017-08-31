package io.agora.streaming.ui;

/**
 * Created by xdf20 on 2017/8/23.
 */

public interface MySubscribeCallback {
    void Subsccribe(int Uid, Media media, VideoLayout layout, StreamFormat format);
    void unSubscibe(int Uid);
}

