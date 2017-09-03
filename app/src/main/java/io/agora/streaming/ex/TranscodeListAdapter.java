package io.agora.streaming.ex;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.agora.streaming.R;

/**
 * Created by xdf20 on 2017/8/20.
 */

public class TranscodeListAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    private ArrayList<String> list;
    private OnRTMPItemDeleteListener mListener;

    public TranscodeListAdapter(Context context,ArrayList list){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TranscodeListAdapter.Holder holder;
        final String data = list.get(position);
        if(convertView == null){
            holder = new TranscodeListAdapter.Holder();
            convertView = inflater.inflate(R.layout.layout_transcode_list, null);
            holder.url = (TextView)convertView.findViewById(R.id.transcodoe_item);
            convertView.setTag(holder);
        }else{
            holder = (TranscodeListAdapter.Holder) convertView.getTag();
        }

        holder.url.setText(data);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveDialog(position);
            }
        });

        return convertView;
    }

    protected class Holder{
        TextView url;
    }

    private void showRemoveDialog(final int position) {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;


        builder = new AlertDialog.Builder(context)
                .setMessage("Delete this item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem(int position) {
        if (list == null || list.size() < position) return;

        String url = list.get(position);
        list.remove(position);
        notifyDataSetChanged();

        if (mListener != null) {
            mListener.onDelete(url);
        }
    }

    public void setOnRTMPItemDeleteListener(OnRTMPItemDeleteListener listener) {
        mListener = listener;
    }

    interface OnRTMPItemDeleteListener {
        public void onDelete(String url);
    }
}
