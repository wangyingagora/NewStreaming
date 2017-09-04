package io.agora.streaming.ex;

import android.view.SurfaceView;

/**
 * Created by eaglewangy on 31/08/2017.
 */

public class UserInfo {
    public int uid;
    public boolean isLocal;
    public SurfaceView view;
    public boolean hasSubscribed;
    public int renderMode;
    public int streamType;

    public boolean isLocal() {
        return isLocal;
    }


}
