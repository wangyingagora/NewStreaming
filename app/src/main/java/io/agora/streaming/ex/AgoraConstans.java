package io.agora.streaming.ex;

/**
 * Created by eaglewangy on 03/09/2017.
 */

public class AgoraConstans {
    public final static int VIDEO_COLUMNS = 3;

    public static final int BASE_VALUE_PERMISSION = 0X0001;
    public static final int PERMISSION_REQ_ID_RECORD_AUDIO = BASE_VALUE_PERMISSION + 1;
    public static final int PERMISSION_REQ_ID_CAMERA = BASE_VALUE_PERMISSION + 2;
    public static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = BASE_VALUE_PERMISSION + 3;

    public static final int MAX_PEER_COUNT = 9;

    public static final int DEFAULT_PROFILE_IDX = 3;

    public static class PrefManager {
        public static final String PREF_PROPERTY_PROFILE_IDX = "pref_profile_index";
        public static final String PREF_PROPERTY_UID = "pOCXx_uid";
    }

    public static final String ACTION_KEY_CHANNEL_NAME = "ecHANEL";
    public static final String ACTION_KEY_ENCRYPTION_KEY = "xdL_encr_key_";
    public static final String ACTION_KEY_ENCRYPTION_MODE = "tOK_edsx_Mode";
    public static final String ACTION_ENABLE_VIDEO = "enable_video";
    public static final String ACTION_TRANSFORM_WIDTH = "transform_width";
    public static final String ACTION_TRANSFORM_HEIGHT = "transform_height";
    public static final String ACTION_TRANSFORM_BITRATE = "transform_bitrate";

}
