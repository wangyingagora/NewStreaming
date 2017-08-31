package io.agora.streaming.ui;
import android.content.Context;
import android.support.annotation.IdRes;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;
import io.agora.streaming.R;

/**
 * Created by xdf20 on 2017/8/15.
 */

public class MyListViewAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    ArrayList<UrlData> list;
    private MySubscribeCallback subscribeCallback;
    public MyListViewAdapter(Context context,ArrayList list, MySubscribeCallback callback){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.subscribeCallback = callback;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public UrlData getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        final UrlData data  = list.get(position);
        if(convertView == null){
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_listview, null);
            holder.url = (TextView)convertView.findViewById(R.id.tv_item);
            holder.mCheckBox = (CheckBox)convertView.findViewById(R.id.cb_item);
            holder.mSetMediaType = (RadioGroup)convertView.findViewById(R.id.media_type);
            holder.mSetVideoType = (RadioGroup)convertView.findViewById(R.id.video_type);
            holder.mStreamType = (RadioGroup)convertView.findViewById(R.id.stream_type);

            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.mCheckBox.setOnClickListener(null);
        holder.mCheckBox.setChecked(list.get(position).mChecked);
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((CheckBox) v).isChecked()){
                    list.get(position).mChecked = false;
                    subscribeCallback.unSubscibe(Integer.valueOf(list.get(position).Url).intValue());
                } else {
                    long uid = (Integer.valueOf(list.get(position).Url).intValue() & 0xFFFFFFFFL);
                    Log.e("adam", "subscribeCallback uid is " + uid);
                    subscribeCallback.Subsccribe(Integer.valueOf(list.get(position).Url).intValue(), list.get(position).mMeidia ,list.get(position).mLayout, list.get(position).mFormat);
                    list.get(position).mChecked = true;
                }
            }
        });

        long uidL = (Integer.valueOf(data.Url).intValue() & 0xFFFFFFFFL);
        holder.url.setText(uidL + "");

        holder.mStreamType.setOnCheckedChangeListener(null);
        if(list.get(position).mFormat == StreamFormat.High) {
            holder.mStreamType.check(R.id.stream_type_high);
        } else if(list.get(position).mFormat == StreamFormat.Low){
            holder.mStreamType.check(R.id.stream_type_low);
        }

        holder.mStreamType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (R.id.stream_type){
                    case R.id.stream_type_high:
                        list.get(position).mFormat = StreamFormat.High;
                        break;
                    case R.id.stream_type_low:
                        list.get(position).mFormat = StreamFormat.Low;
                        break;
                    default:
                        break;
                }
                subscribeCallback.Subsccribe(Integer.valueOf(list.get(position).Url).intValue(), list.get(position).mMeidia ,list.get(position).mLayout, list.get(position).mFormat);
            }
        });



        holder.mSetMediaType.setOnCheckedChangeListener(null);

        if(list.get(position).mMeidia == Media.AV){
            holder.mSetMediaType.check(R.id.media_type_av);
        } else if(list.get(position).mMeidia == Media.AUDIO){
            holder.mSetMediaType.check(R.id.media_type_audio);
        } else if(list.get(position).mMeidia == Media.VIDEO) {
            holder.mSetMediaType.check(R.id.media_type_video);
        } else if(list.get(position).mMeidia == Media.NONIE){
            holder.mSetMediaType.check(R.id.media_type_none);
        }


        holder.mSetMediaType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.media_type_av:
                        list.get(position).mMeidia = Media.AV;
                        break;
                    case R.id.media_type_audio:
                        list.get(position).mMeidia = Media.AUDIO;
                        break;
                    case R.id.media_type_video:
                        list.get(position).mMeidia = Media.VIDEO;
                        break;
                    case R.id.media_type_none:
                        list.get(position).mMeidia = Media.NONIE;
                    default:
                        break;
                }
                subscribeCallback.Subsccribe(Integer.valueOf(list.get(position).Url).intValue(), list.get(position).mMeidia ,list.get(position).mLayout, list.get(position).mFormat);
            }

        });


        holder.mSetVideoType.setOnCheckedChangeListener(null);

        if(list.get(position).mLayout == VideoLayout.Fit){
            holder.mSetVideoType.check(R.id.video_type_fit);
        } else if(list.get(position).mLayout == VideoLayout.Adaptive){
            holder.mSetVideoType.check(R.id.video_type_adaptive);
        } else if(list.get(position).mLayout == VideoLayout.Hideden){
            holder.mSetVideoType.check(R.id.video_type_hidden);
        }

        holder.mSetVideoType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.video_type_hidden:
                        list.get(position).mLayout = VideoLayout.Hideden;
                        break;
                    case R.id.video_type_fit:
                        list.get(position).mLayout = VideoLayout.Fit;
                        break;
                    case R.id.video_type_adaptive:
                        list.get(position).mLayout = VideoLayout.Adaptive;
                        break;
                    default:
                        break;
                }
                subscribeCallback.Subsccribe(Integer.valueOf(list.get(position).Url).intValue(), list.get(position).mMeidia ,list.get(position).mLayout, list.get(position).mFormat);
            }
        });

        return convertView;
    }

    protected class Holder{
        TextView url;
        CheckBox mCheckBox;
        RadioGroup mSetMediaType;
        RadioGroup mSetVideoType;
        RadioGroup mStreamType;
    }


}
