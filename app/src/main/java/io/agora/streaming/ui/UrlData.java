package io.agora.streaming.ui;

/**
 * Created by xdf20 on 2017/8/15.
 */
enum Media {
    AV,
    AUDIO,
    VIDEO,
    NONIE
};

enum VideoLayout{
    Hideden,
    Fit,
    Adaptive
};

enum StreamFormat{
    High,
    Low
};

public class UrlData {
    String Url;
    boolean mChecked;
    Media mMeidia;
    VideoLayout mLayout;
    StreamFormat mFormat;
    UrlData(String url){
        Url = url;
    }
    UrlData(String url, boolean checked, Media media, VideoLayout layout, StreamFormat format){
        Url = url;
        mChecked = checked;
        mMeidia = media;
        mLayout = layout;
        mFormat = format;
    }
}
