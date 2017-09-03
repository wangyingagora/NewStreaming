package io.agora.streaming.ex;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import io.agora.streaming.R;

/**
 * Created by eaglewangy on 03/09/2017.
 */

public class MediaTypeDialog {
    private Media media = Media.NONE;

    public void showDialog(Context context, final OnMediaTypeChangedListener listener) {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        View view = LayoutInflater.from(context).inflate(R.layout.layout_mediainfo, null);
        RadioGroup mediaRadioGroup = (RadioGroup) view.findViewById(R.id.set_media_type);
        mediaRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.set_media_av:
                        //setAVMode();
                        media = Media.AV;
                        break;
                    case R.id.set_media_v:
                        //setVideoMode();
                        media = Media.VIDEO;
                        break;
                    case R.id.set_media_a:
                        //setAudioMode();
                        media = Media.AUDIO;
                        break;
                    case R.id.set_media_none:
                        //setNoneMode();
                        media = Media.NONE;
                        break;
                }
            }
        });

        builder = new AlertDialog.Builder(context);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null) {
                    listener.onTypeChanged(media);
                }
            }
        });
        alertDialog.show();
    }

    public interface OnMediaTypeChangedListener {
        public void onTypeChanged(Media type);
    }
}
