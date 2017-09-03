package io.agora.streaming.ex;

/**
 * Created by xdf20 on 2017/8/15.
 */
enum Media {
    AV,
    AUDIO,
    VIDEO,
    NONE
};

enum VideoLayout{
    Hidden,
    Fit,
    Adaptive
};

enum StreamFormat{
    High,
    Low
};

public class SubscribeType {
    String url;
    boolean isChecked;
    Media mMeidia;
    VideoLayout mLayout;
    StreamFormat mFormat;
    SubscribeType(String url){
        this.url = url;
    }
    SubscribeType(String url, boolean checked, Media media, VideoLayout layout, StreamFormat format){
        this.url = url;
        isChecked = checked;
        mMeidia = media;
        mLayout = layout;
        mFormat = format;
    }
}
