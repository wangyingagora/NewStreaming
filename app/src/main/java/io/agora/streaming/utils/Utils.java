package io.agora.streaming.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.agora.streaming.ex.UserInfo;

/**
 * Created by eaglewangy on 25/08/2017.
 */

public class Utils {
    public static boolean hasExists(List<String> urls, String url) {
        if (TextUtils.isEmpty(url)) {
            return true;
        }

        for (int i = 0; i < urls.size(); ++i) {
            if (urls.get(i).equals(url)) {
                return true;
            }
        }
        return false;
    }

    public static void showSimpleDialog(Context context, String message) {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        builder = new AlertDialog.Builder(context)
                .setMessage(message).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static int randomColor() {
        Random rand =new Random(128);
        int red = rand.nextInt(255);
        int green = rand.nextInt(255);
        int blue = rand.nextInt(255);
        int backgroundColor = 0;
        backgroundColor = red << 16 | green << 8 | blue << 0;
        return backgroundColor;
    }

    public static String getStreamUrl(String room) {
        return "rtmp://vid-130451.push.fastweb.broadcastapp.agoraio.cn/live/" + room;
    }

    public static ArrayList<UserInfo> getPublishers(Map<Integer, UserInfo> userInfo, boolean isOnlySubscribed) {
        ArrayList<UserInfo> users = new ArrayList<>();
        Iterator<Map.Entry<Integer, UserInfo>> iterator = userInfo.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, UserInfo> entry = iterator.next();
            UserInfo user = entry.getValue();
            if (user.isLocal) {
                continue;
            }
            if (isOnlySubscribed) {
                if (!user.hasSubscribed) {
                    continue;
                }
            }
            users.add(user);
        }
        return users;
    }

    public static ArrayList<UserInfo> getSmallVideoUser(Map<Integer, UserInfo> userInfo, int bigUserId) {
        ArrayList<UserInfo> users = new ArrayList<>();
        Iterator<Map.Entry<Integer, UserInfo>> iterator = userInfo.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, UserInfo> entry = iterator.next();
            UserInfo user = entry.getValue();
            if (user.uid == bigUserId || !user.hasSubscribed) {
                continue;
            }

            users.add(user);
        }
        return users;
    }

    public static void removeViewFromParent(View view) {
        if (view.getParent() != null) {
            ((ViewManager)view.getParent()).removeView(view);
        }
    }
}
