package io.agora.propeller.preprocessing;

public class VideoPreProcessing {
    static {
        System.loadLibrary("apm-plugin-video-preprocessing");
    }

    public interface ProgressCallback{
        void onProcessYUV(byte[] yuv, int length, int width, int height);
    }

    public void capFile(ProgressCallback callback){
        capture(callback);
    }

    public native void capture(ProgressCallback callback);
    public native void enablePreProcessing(boolean enable);
}
