package io.agora.streaming.ex;

import io.agora.live.LiveTranscoding;

/**
 * Created by eaglewangy on 02/09/2017.
 */

public class CustomTranscoding extends LiveTranscoding {
    public static final int LAYOUT_DEFAULT = 0;
    public static final int LAYOUT_FLOAT = 1;
    public static final int LAYOUT_TITLE = 2;
    public static final int LAYOUT_MATRIX = 3;

    public boolean isEnabled;
    public int layout;

    public CustomTranscoding() {
        super();
        layout = LAYOUT_DEFAULT;
        isEnabled = false;
    }
}
