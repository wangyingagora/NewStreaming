package io.agora.streaming.ex;
import android.content.Context;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;
import io.agora.streaming.R;

/**
 * Created by xdf20 on 2017/8/15.
 */

public class PublisherListAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    ArrayList<UrlData> mData;
    private SubscribeListener subscribeCallback;
    public PublisherListAdapter(Context context, ArrayList<UrlData> data, SubscribeListener callback){
        this.context = context;
        inflater = LayoutInflater.from(context);
        mData = data;
        if (mData == null) {
            mData = new ArrayList<>();
        }
        this.subscribeCallback = callback;
    }

    public void updatePublishers(ArrayList<UrlData> publishers) {
        if (publishers == null) {
            publishers = new ArrayList<>();
        }
        mData = publishers;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public UrlData getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        final UrlData data  = mData.get(position);
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
        holder.mCheckBox.setChecked(mData.get(position).mChecked);
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((CheckBox) v).isChecked()){
                    mData.get(position).mChecked = false;
                    subscribeCallback.unsubscribe(Integer.valueOf(mData.get(position).Url).intValue());
                } else {
                    long uid = (Integer.valueOf(mData.get(position).Url).intValue() & 0xFFFFFFFFL);
                    //Log.e("adam", "subscribeCallback uid is " + uid);
                    subscribeCallback.subscribe(Integer.valueOf(mData.get(position).Url).intValue(), mData.get(position).mMeidia ,mData.get(position).mLayout, mData.get(position).mFormat);
                    mData.get(position).mChecked = true;
                }
            }
        });

        long uidL = (Integer.valueOf(data.Url).intValue() & 0xFFFFFFFFL);
        holder.url.setText(uidL + "");

        holder.mStreamType.setOnCheckedChangeListener(null);
        if(mData.get(position).mFormat == StreamFormat.High) {
            holder.mStreamType.check(R.id.stream_type_high);
        } else if(mData.get(position).mFormat == StreamFormat.Low){
            holder.mStreamType.check(R.id.stream_type_low);
        }

        holder.mStreamType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (R.id.stream_type){
                    case R.id.stream_type_high:
                        mData.get(position).mFormat = StreamFormat.High;
                        break;
                    case R.id.stream_type_low:
                        mData.get(position).mFormat = StreamFormat.Low;
                        break;
                    default:
                        break;
                }
                subscribeCallback.subscribe(Integer.valueOf(mData.get(position).Url).intValue(), mData.get(position).mMeidia, mData.get(position).mLayout, mData.get(position).mFormat);
            }
        });

        holder.mSetMediaType.setOnCheckedChangeListener(null);

        if(mData.get(position).mMeidia == Media.AV){
            holder.mSetMediaType.check(R.id.media_type_av);
        } else if(mData.get(position).mMeidia == Media.AUDIO){
            holder.mSetMediaType.check(R.id.media_type_audio);
        } else if(mData.get(position).mMeidia == Media.VIDEO) {
            holder.mSetMediaType.check(R.id.media_type_video);
        } else if(mData.get(position).mMeidia == Media.NONIE){
            holder.mSetMediaType.check(R.id.media_type_none);
        }

        holder.mSetMediaType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.media_type_av:
                        mData.get(position).mMeidia = Media.AV;
                        break;
                    case R.id.media_type_audio:
                        mData.get(position).mMeidia = Media.AUDIO;
                        break;
                    case R.id.media_type_video:
                        mData.get(position).mMeidia = Media.VIDEO;
                        break;
                    case R.id.media_type_none:
                        mData.get(position).mMeidia = Media.NONIE;
                    default:
                        break;
                }
                subscribeCallback.subscribe(Integer.valueOf(mData.get(position).Url).intValue(), mData.get(position).mMeidia ,mData.get(position).mLayout, mData.get(position).mFormat);
            }

        });


        holder.mSetVideoType.setOnCheckedChangeListener(null);

        if(mData.get(position).mLayout == VideoLayout.Fit){
            holder.mSetVideoType.check(R.id.video_type_fit);
        } else if(mData.get(position).mLayout == VideoLayout.Adaptive){
            holder.mSetVideoType.check(R.id.video_type_adaptive);
        } else if(mData.get(position).mLayout == VideoLayout.Hideden){
            holder.mSetVideoType.check(R.id.video_type_hidden);
        }

        holder.mSetVideoType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.video_type_hidden:
                        mData.get(position).mLayout = VideoLayout.Hideden;
                        break;
                    case R.id.video_type_fit:
                        mData.get(position).mLayout = VideoLayout.Fit;
                        break;
                    case R.id.video_type_adaptive:
                        mData.get(position).mLayout = VideoLayout.Adaptive;
                        break;
                    default:
                        break;
                }
                subscribeCallback.subscribe(Integer.valueOf(mData.get(position).Url).intValue(), mData.get(position).mMeidia ,mData.get(position).mLayout, mData.get(position).mFormat);
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
