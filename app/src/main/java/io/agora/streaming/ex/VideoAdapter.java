package io.agora.streaming.ex;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.FrameLayout;

import java.util.ArrayList;

import io.agora.streaming.R;
import io.agora.streaming.model.User;
import io.agora.streaming.utils.DeviceUtils;
import io.agora.streaming.utils.Utils;

/**
 * Created by eaglewangy on 02/09/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private final static String TAG = VideoAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<UserInfo> mUsers;

    public VideoAdapter(Context context, ArrayList<UserInfo> users) {
        mContext = context;
        mUsers = users;
        if (mUsers == null) {
            mUsers = new ArrayList<>();
        }
    }

    public void updateVideoData(ArrayList<UserInfo> users) {
        mUsers = users;
        if (mUsers == null) {
            mUsers = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.small_video_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        //holder.rootView.setBackgroundColor(114343);
        int size = DeviceUtils.getScreenWidth(view.getContext()) / AgoraConstans.VIDEO_COLUMNS;
        int padding = DeviceUtils.dpToPx(view.getContext(), 8);
        view.getLayoutParams().width = size;
        view.getLayoutParams().height = size;
        view.setPadding(padding, padding, padding, padding);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserInfo user = mUsers.get(position);
        SurfaceView surfaceView = user.view;
        if (surfaceView.getParent() != null) {
            ((ViewManager)surfaceView.getParent()).removeView(surfaceView);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        surfaceView.setLayoutParams(params);
        holder.rootView.addView(surfaceView);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        Log.e(TAG, "onDetachedFromRecyclerView");
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        //holder.rootView.removeAllViews();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = (FrameLayout) itemView;
            rootView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        }
    }
}
