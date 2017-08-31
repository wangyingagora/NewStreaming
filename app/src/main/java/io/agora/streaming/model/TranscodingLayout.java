package io.agora.streaming.model;

import android.util.Log;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;

import io.agora.live.LiveTranscoding;
import io.agora.rtc.Constants;
import io.agora.streaming.ui.Subscriber;

/**
 * Created by eaglewangy on 26/08/2017.
 */

public class TranscodingLayout {
    public static ArrayList<LiveTranscoding.TranscodingUser> defaultLayout(int meId, int renderMode,  int canvasWidth, int canvasHeight) {
        LiveTranscoding.TranscodingUser user = new LiveTranscoding.TranscodingUser();
        ArrayList<LiveTranscoding.TranscodingUser> users = new ArrayList<>(1);
        user.uid = meId;
        user.alpha = 0.5f;
        user.zOrder = 0;
        user.x = 0;
        user.y = 0;
        user.width = canvasWidth;
        user.height = canvasHeight;
        users.add(user);
        return users;
    }

    public static ArrayList<LiveTranscoding.TranscodingUser> floatLayout(int meId,
                                                                         HashMap<Integer, SurfaceView> list,
                                                                         int renderMode,
                                                                         int canvasWidth,
                                                                         int canvasHeight){
        ArrayList<LiveTranscoding.TranscodingUser> users;
        int index = 0;
        float xIndex = 0;
        float yIndex = 0;
        int virtualWidth = canvasWidth;
        int virtualHeight = canvasHeight;
        int viewWidth = (int) (virtualWidth * 0.2);
        int viewHEdge = (int) (virtualWidth * 0.025);
        int viewHeight = viewWidth;
        int viewVEdge = viewHEdge;

        if (list.size() <= 7) {
            users = new ArrayList<>(list.size());
        } else {
            users = new ArrayList<>(7);
        }

        LiveTranscoding.TranscodingUser user0 = new LiveTranscoding.TranscodingUser();
        user0.uid = meId;
        user0.alpha = 1;
        user0.zOrder = 0;
        user0.x = 0;
        user0.y = 0;
        user0.width = canvasWidth;
        user0.height = canvasHeight;
        users.add(user0);

        for (HashMap.Entry<Integer, SurfaceView> entry : list.entrySet()){
            if(entry.getKey() == meId){
                continue;
            }

            if (index >= 6) {
                break;
            }

            xIndex = (float) (index % 3);
            yIndex = (float) (index / 3);
            LiveTranscoding.TranscodingUser tmpUser = new LiveTranscoding.TranscodingUser();
            tmpUser.uid = entry.getKey();
            tmpUser.x = (int) (xIndex * (viewWidth + viewHEdge) + viewHEdge);
            tmpUser.y = (int) (virtualHeight - (yIndex + 1) * (viewHeight + viewVEdge));
            tmpUser.width = viewWidth;
            tmpUser.height = viewHeight;
            tmpUser.zOrder = index + 1;
            tmpUser.alpha = (float) ((index % 2 == 0) ? 1 : 0.6);

            users.add(tmpUser);
            index++;

        }

        return users;
    }


   /* public static ArrayList<LiveTranscoding.TranscodingUser> floatLayout(int meId,
                                                                         HashMap<Integer, Subscriber> subscribers,
                                                                         int renderMode,
                                                                         int canvasWidth,
                                                                         int canvasHeight) {
        ArrayList<LiveTranscoding.TranscodingUser> users;

        int index = 0;
        float xIndex = 0;
        float yIndex = 0;
        int virtualWidth = canvasWidth;
        int virtualHeight = canvasHeight;
        int viewWidth = (int) (virtualWidth * 0.2);
        int viewHEdge = (int) (virtualWidth * 0.025);
        int viewHeight = viewWidth;
        int viewVEdge = viewHEdge;
        if (subscribers.size() <= 6) {
            users = new ArrayList<>(subscribers.size() + 1);
        } else {
            users = new ArrayList<>(7);
        }

        LiveTranscoding.TranscodingUser user0 = new LiveTranscoding.TranscodingUser();
        user0.uid = meId;
        user0.alpha = 1;
        user0.zOrder = 0;
        user0.x = 0;
        user0.y = 0;
        user0.width = canvasWidth;
        user0.height = canvasHeight;

        users.add(user0);

        for (HashMap.Entry<Integer, Subscriber> entry : subscribers.entrySet()) {
            if (index >= 6) {
                break;
            }
            xIndex = (float) (index % 3);
            yIndex = (float) (index / 3);
            LiveTranscoding.TranscodingUser tmpUser = new LiveTranscoding.TranscodingUser();
            tmpUser.uid = entry.getValue().mUid;
            tmpUser.x = (int) (xIndex * (viewWidth + viewHEdge) + viewHEdge);
            tmpUser.y = (int) (virtualHeight - (yIndex + 1) * (viewHeight + viewVEdge));
            tmpUser.width = viewWidth;
            tmpUser.height = viewHeight;
            tmpUser.zOrder = index + 1;
            tmpUser.alpha = (float) ((index % 2 == 0) ? 1 : 0.6);

            users.add(tmpUser);
            index++;

        }

        return users;
    } */

    public static ArrayList<LiveTranscoding.TranscodingUser> titleLayout(int meId,
                                                                         HashMap<Integer, SurfaceView> list,
                                                                         int renderMode,
                                                                         int canvasWidth,
                                                                         int canvasHeight){
        ArrayList<LiveTranscoding.TranscodingUser> users ;

        int index = 0;
        float xIndex = 0;
        float yIndex = 0;
        int virtualWidth = canvasWidth;
        int virtualHeight = canvasHeight;
        int viewWidth = (int) (virtualWidth * 0.2);
        int viewHEdge = (int) (virtualWidth * 0.025);
        int viewHeight = viewWidth;
        int viewVEdge = viewHEdge;
        int minY = 0;
        int fullViewHeight = virtualHeight;

        if (list.size() <= 7) {
            users = new ArrayList<>(list.size());
        } else {
            users = new ArrayList<>(7);
        }

        for (HashMap.Entry<Integer, SurfaceView> entry : list.entrySet()) {
            if(entry.getKey() == meId){
                continue;
            }

            if (index >= 6) {
                break;
            }

            LiveTranscoding.TranscodingUser tmpUser = new LiveTranscoding.TranscodingUser();

            xIndex = (float) (index % 3);
            yIndex = (float) (index / 3);

            tmpUser.uid = entry.getKey();
            tmpUser.x = (int) (xIndex * (viewWidth + viewHEdge) + viewHEdge);
            tmpUser.y = (int) (virtualHeight - (yIndex + 1) * (viewHeight + viewVEdge));
            tmpUser.width = viewWidth;
            tmpUser.height = viewHeight;
            tmpUser.zOrder = 0;
            tmpUser.alpha = 1;

            if(minY == 0 || minY > tmpUser.y){
                minY = tmpUser.y;
            }

            fullViewHeight = minY - viewVEdge;

            users.add(tmpUser);
            index++;

        }

        LiveTranscoding.TranscodingUser user0 = new LiveTranscoding.TranscodingUser();
        user0.uid = meId;
        user0.alpha = 1;
        user0.zOrder = 0;
        user0.x = 0;
        user0.y = 0;
        user0.width = virtualWidth;
        user0.height = fullViewHeight;
        users.add(user0);

        return users;
    }

  /*  public static ArrayList<LiveTranscoding.TranscodingUser> titleLayout(int meId,
                                                                         HashMap<Integer, Subscriber> subscribers,
                                                                         int renderMode,
                                                                         int canvasWidth,
                                                                         int canvasHeight) {

        ArrayList<LiveTranscoding.TranscodingUser> users;

        int index = 0;
        float xIndex = 0;
        float yIndex = 0;
        int virtualWidth = canvasWidth;
        int virtualHeight = canvasHeight;
        int viewWidth = (int) (virtualWidth * 0.2);
        int viewHEdge = (int) (virtualWidth * 0.025);
        int viewHeight = viewWidth;
        int viewVEdge = viewHEdge;
        int minY = 0;
        int fullViewHeight = virtualHeight;
        if (subscribers.size() <= 6) {
            users = new ArrayList<>(subscribers.size() + 1);
        } else {
            users = new ArrayList<>(7);
        }
        for (HashMap.Entry<Integer, Subscriber> entry : subscribers.entrySet()) {
            if (index >= 6) {
                break;
            }

            LiveTranscoding.TranscodingUser tmpUser = new LiveTranscoding.TranscodingUser();

            xIndex = (float) (index % 3);
            yIndex = (float) (index / 3);

            tmpUser.uid = entry.getValue().mUid;
            tmpUser.x = (int) (xIndex * (viewWidth + viewHEdge) + viewHEdge);
            tmpUser.y = (int) (virtualHeight - (yIndex + 1) * (viewHeight + viewVEdge));
            tmpUser.width = viewWidth;
            tmpUser.height = viewHeight;
            tmpUser.zOrder = 0;
            tmpUser.alpha = 1;

            if(minY == 0 || minY > tmpUser.y){
                minY = tmpUser.y;
            }

            fullViewHeight = minY - viewVEdge;

            users.add(tmpUser);
            index++;

        }

        LiveTranscoding.TranscodingUser user0 = new LiveTranscoding.TranscodingUser();
        user0.uid = meId;
        user0.alpha = 1;
        user0.zOrder = 0;
        user0.x = 0;
        user0.y = 0;
        user0.width = virtualWidth;
        user0.height = fullViewHeight;
        users.add(user0);

        return users;
    } */

    public static ArrayList<LiveTranscoding.TranscodingUser> martixLayout(int meId,
                                                                         HashMap<Integer, Subscriber> subscribers,
                                                                         int renderMode,
                                                                         int canvasWidth,
                                                                         int canvasHeight) {
        ArrayList<LiveTranscoding.TranscodingUser> users;
        Log.e("adam", " Enter martixLayout");
        int index = 1;
        float xIndex = 0;
        float yIndex = 0;
        int xOffset = 0;
        int virtualWidth = canvasWidth;
        int virtualHeight = canvasHeight;
        int viewWidth = (int) (virtualWidth * 0.2);
        int viewHEdge = (int) (virtualWidth * 0.025);
        int viewHeight = viewWidth;
        int viewVEdge = viewHEdge;
        if (subscribers.size() <= 8) {
            users = new ArrayList<>(subscribers.size() + 1);
        } else {
            users = new ArrayList<>(7);
        }

        LiveTranscoding.TranscodingUser user0 = new LiveTranscoding.TranscodingUser();

        user0.uid = meId;
        user0.alpha = 1;
        user0.zOrder = 0;
        user0.x = viewHEdge;
        user0.y = (virtualHeight - viewHeight) / 2 - (viewHeight + viewVEdge);
        user0.width = viewWidth;
        user0.height = viewHeight;


        Log.e("adam", "virtualWidth is " + virtualWidth);
        Log.e("adam", "viewHeight is " + viewHeight);
        Log.e("adam", "use0.y is " + user0.y);
        users.add(user0);

        for (HashMap.Entry<Integer, Subscriber> entry : subscribers.entrySet()) {
            if (index >= 8) {
                break;
            }

            LiveTranscoding.TranscodingUser tmpUser = new LiveTranscoding.TranscodingUser();
            xIndex = (float) (index % 3);
            yIndex = (float) (index / 3);

            tmpUser.uid = entry.getValue().mUid;
            tmpUser.x = (int) (xIndex * (viewWidth + viewHEdge) + viewHEdge);
            tmpUser.y = (int) ((virtualHeight - viewHeight) / 2 + (yIndex - 1) * (viewHeight + viewVEdge));
            Log.e("adam", "use0.y is  " + tmpUser.y);
            tmpUser.width = viewWidth;
            tmpUser.height = viewHeight;
            tmpUser.zOrder = 0;
            tmpUser.alpha = 1;

            users.add(tmpUser);

            index++;
        }

        return users;
    }
}
