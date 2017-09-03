package io.agora.streaming.ex;

import android.view.SurfaceView;

/**
 * Created by eaglewangy on 31/08/2017.
 */

public class UserInfo {
    int uid;
    boolean isLocal;
    SurfaceView view;
    boolean hasSubscribed;
    int renderMode;
    int streamType;

    public boolean isLocal() {
        return isLocal;
    }


}
