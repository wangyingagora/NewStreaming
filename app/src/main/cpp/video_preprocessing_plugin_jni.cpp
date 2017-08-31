#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <agora/IAgoraMediaEngine.h>

#include "agora/IAgoraRtcEngine.h"
#include "agora/IAgoraMediaEngine.h"
#include <string.h>
#include "video_preprocessing_plugin_jni.h"
#include "io_agora_propeller_preprocessing_VideoPreProcessing.h"
#include <stdio.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <malloc.h>
#include <linux/kernel.h>
#include <math.h>
#include <stddef.h>
#include <assert.h>
#include <pthread.h>

#define min(X,Y) ((X) < (Y) ? (X) : (Y))
#define max(X,Y) ((X) > (Y) ? (X) : (Y))

jobject   mCallBack = NULL;
JNIEnv    *jni_env;
JavaVM    *mpVM;

class AgoraVideoFrameObserver : public agora::media::IVideoFrameObserver
{
public:
    AgoraVideoFrameObserver(){
        caputre_enable = false;
        mCallBack = NULL;
        jni_env = NULL;
    }

    ~AgoraVideoFrameObserver(){
        jni_env->DeleteGlobalRef(mCallBack);
    }
public:
    virtual bool onCaptureVideoFrame(VideoFrame& videoFrame) override
    {
       /* if(caputre_enable)
        {
            int width = videoFrame.width;
            int height = videoFrame.height;
            char *yuv = (char *)malloc(width*height*1.5);
            if(yuv != NULL){
                memcpy(yuv, videoFrame.yBuffer, width*height);
                memcpy(yuv + width*height, videoFrame.uBuffer, width*height/4);
                memcpy(yuv + width*height*5/4, videoFrame.vBuffer, width*height/4);

                jmethodID mid_method;
                jclass cls;

                JavaVMAttachArgs args = {JNI_VERSION_1_6, __FUNCTION__, __null };
                __android_log_print(ANDROID_LOG_ERROR, "adam", "before AttachCurrentThread");
                jint ret = mpVM->AttachCurrentThread(&jni_env, &args);
                __android_log_print(ANDROID_LOG_ERROR, "adam", "after AttachCurrentThread is %d", ret);
                cls = jni_env->GetObjectClass(mCallBack);
                __android_log_print(ANDROID_LOG_ERROR, "adam", "before onProcessYUV");
                mid_method = jni_env->GetMethodID(cls, "onProcessYUV", "([BIII)V");
                __android_log_print(ANDROID_LOG_ERROR, "adam", "after onProcessYUV");

                jbyteArray array = jni_env->NewByteArray (width*height*3/2);
                jni_env->SetByteArrayRegion(array, 0, (width*height*3/2), reinterpret_cast<jbyte*>(yuv));
                jni_env->CallVoidMethod(mCallBack, mid_method, array, (width*height*3/2), width, height);
                jni_env->DeleteLocalRef(cls);
                jni_env->DeleteLocalRef(array);
                mpVM->DetachCurrentThread();

            }

            free(yuv);
            yuv = NULL;
            caputre_enable = false;
        } */
      //  __android_log_print(ANDROID_LOG_ERROR, "adam", "onCaptureVideoFrame width(%d) height(%d)", videoFrame.width, videoFrame.height);
    //    __android_log_print(ANDROID_LOG_ERROR, "adam", "onCaptureVideoFrame uStride(%d) yStride(%d) vStride(%d)", videoFrame.yStride, videoFrame.uStride, videoFrame.vStride);
        return true;
	}

    virtual bool onRenderVideoFrame(unsigned int uid, VideoFrame& videoFrame) override
    {
        __android_log_print(ANDROID_LOG_ERROR, "adam", "onRenderVideoFrame width(%d) height(%d)", videoFrame.width, videoFrame.height);
        __android_log_print(ANDROID_LOG_ERROR, "adam", "onRenderVideoFrame uStride(%d) yStride(%d) vStride(%d)", videoFrame.yStride, videoFrame.uStride, videoFrame.vStride);

        if(caputre_enable)
        {
            int width = videoFrame.width;
            int height = videoFrame.height;
            char *yuv = (char *)malloc(width*height*1.5);
            if(yuv != NULL){
                memcpy(yuv, videoFrame.yBuffer, width*height);
                memcpy(yuv + width*height, videoFrame.uBuffer, width*height/4);
                memcpy(yuv + width*height*5/4, videoFrame.vBuffer, width*height/4);

                jmethodID mid_method;
                jclass cls;

                JavaVMAttachArgs args = {JNI_VERSION_1_6, __FUNCTION__, __null };
                __android_log_print(ANDROID_LOG_ERROR, "adam", "before AttachCurrentThread");
               jint ret = mpVM->AttachCurrentThread(&jni_env, &args);
                __android_log_print(ANDROID_LOG_ERROR, "adam", "after AttachCurrentThread is %d", ret);
                cls = jni_env->GetObjectClass(mCallBack);
                __android_log_print(ANDROID_LOG_ERROR, "adam", "before onProcessYUV");
                mid_method = jni_env->GetMethodID(cls, "onProcessYUV", "([BIII)V");
                __android_log_print(ANDROID_LOG_ERROR, "adam", "after onProcessYUV");

                jbyteArray array = jni_env->NewByteArray (width*height*3/2);
                jni_env->SetByteArrayRegion(array, 0, (width*height*3/2), reinterpret_cast<jbyte*>(yuv));
                jni_env->CallVoidMethod(mCallBack, mid_method, array, (width*height*3/2), width, height);
                jni_env->DeleteLocalRef(cls);
                jni_env->DeleteLocalRef(array);
                 mpVM->DetachCurrentThread();

            }

            free(yuv);
            yuv = NULL;
            caputre_enable = false;
        }
        return true;
    }


public:
    bool caputre_enable;

private:
    static int caputre_rgb_count;

};





int AgoraVideoFrameObserver::caputre_rgb_count = 0;

static AgoraVideoFrameObserver s_videoFrameObserver;
static agora::rtc::IRtcEngine* rtcEngine = NULL;

#ifdef __cplusplus
extern "C" {
#endif

int __attribute__((visibility("default"))) loadAgoraRtcEnginePlugin(agora::rtc::IRtcEngine* engine)
{
    __android_log_print(ANDROID_LOG_ERROR, "plugin", "plugin loadAgoraRtcEnginePlugin");
    rtcEngine = engine;
    return 0;
}

void __attribute__((visibility("default"))) unloadAgoraRtcEnginePlugin(agora::rtc::IRtcEngine* engine)
{
    __android_log_print(ANDROID_LOG_ERROR, "plugin", "plugin unloadAgoraRtcEnginePlugin");
    rtcEngine = NULL;
}


JNIEXPORT void JNICALL Java_io_agora_propeller_preprocessing_VideoPreProcessing_capture(JNIEnv *env, jobject obj, jobject callback){
    __android_log_print(ANDROID_LOG_ERROR, "adam", "Java_io_agora_propeller_preprocessing_VideoPreProcessing_capture");
    if(mCallBack == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "adam", "before NewGlobalRef");
        jni_env = env;

        mCallBack = jni_env->NewGlobalRef(callback);

        jni_env->GetJavaVM(&mpVM);
        __android_log_print(ANDROID_LOG_ERROR, "adam", "after NewGlobalRef");
    }

    s_videoFrameObserver.caputre_enable = true;
}

JNIEXPORT void JNICALL Java_io_agora_propeller_preprocessing_VideoPreProcessing_enablePreProcessing
  (JNIEnv *env, jobject obj, jboolean enable)
{
    if (!rtcEngine)
        return;
    agora::util::AutoPtr<agora::media::IMediaEngine> mediaEngine;
    mediaEngine.queryInterface(rtcEngine, agora::rtc::AGORA_IID_MEDIA_ENGINE);
    if (mediaEngine) {
        if (enable) {
            mediaEngine->registerVideoFrameObserver(&s_videoFrameObserver);
        } else {
            mediaEngine->registerVideoFrameObserver(NULL);
        }
    }
}

#ifdef __cplusplus
}
#endif
