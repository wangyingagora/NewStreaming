package io.agora.streaming.ui;

import android.view.SurfaceView;

/**
 * Created by xdf20 on 2017/8/23.
 */

public class Subscriber {
    public int mMediaType;
    public SurfaceView mSurfaceV;
    public int mRenderMode;
    public int mStreamType;
    public int mUid;
    Subscriber(int uid, int MediaType,  SurfaceView SurfaceV, int RenderMode,  int StreamType){
        this.mMediaType = MediaType;
        this.mSurfaceV = SurfaceV;
        this.mRenderMode = RenderMode;
        this.mStreamType = mStreamType;
        this.mUid = uid;
    }
}
