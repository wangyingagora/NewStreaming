package io.agora.streaming.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import io.agora.streaming.R;

/**
 * Created by xdf20 on 2017/8/18.
 */

public class MediaTypeListViewAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    private ArrayList<MediaInfo> list;
    private MediaTypeCallback callback;
    private int prePositon = -1;
    public MediaTypeListViewAdapter(Context context,ArrayList list,  MediaTypeCallback callback){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MediaInfo getItem(int position) {
        return  list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       /* MediaTypeListViewAdapter.Holder holder;
        final MediaInfo data = list.get(position);
        if(convertView == null){
            holder = new MediaTypeListViewAdapter.Holder();
            convertView = inflater.inflate(R.layout.layout_mediainfo, null);
            holder.url = (TextView)convertView.findViewById(R.id.tv_mediainfo_item);
            holder.mCheckBox = (CheckBox)convertView.findViewById(R.id.cb_mediainfo_item);
            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     if(((CheckBox)v).isChecked()){
                         list.get(position).mChecked  = true;
                         if(list.get(position).mMediaInfo.equals("Audio and Video")){
                             Log.e("adam", " Audio and Video  onClick ");
                             callback.setAVMode();
                         } else if(list.get(position).mMediaInfo.equals("Audio Only")){
                             Log.e("adam", " Audio Only ");
                             callback.setAudioMode();
                         } else if(list.get(position).mMediaInfo.equals("Video Only")){
                             Log.e("adam", " Video Only ");
                             callback.setVideoMode();
                         } else if(list.get(position).mMediaInfo.equals("None")){
                             Log.e("adam", "None");
                             callback.setNoneMode();
                         }
                     }
                }
            });
            convertView.setTag(holder);
        }else{
              holder = (MediaTypeListViewAdapter.Holder) convertView.getTag();
        }

        holder.url.setText(data.mMediaInfo);
        if(prePositon != -1){
            holder.mCheckBox.setChecked(false);
        }
        prePositon = position; */
        return convertView;
    }

    protected class Holder{
        TextView url;
        CheckBox mCheckBox;
    }

}
