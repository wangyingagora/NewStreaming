package io.agora.streaming.ex;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import io.agora.live.LiveTranscoding;
import io.agora.rtc.Constants;
import io.agora.streaming.R;

/**
 * Created by eaglewangy on 02/09/2017.
 */

public class CustomTranscodingDialog {
    private CustomTranscoding mTransCoding;
    private Context mContext;
    private OnUpdateTranscodingListener mListener;

    public CustomTranscodingDialog(Context context, CustomTranscoding transcoding) {
        mContext = context;
        mTransCoding = transcoding;
    }

    public void showDialog() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_custom_transcoding_setting, null);
        final CheckBox customSettingCheckbox = (CheckBox) layout.findViewById(R.id.enable_custom_setting);
        customSettingCheckbox.setOnClickListener(null);

        customSettingCheckbox.setChecked(mTransCoding.isEnabled);

        customSettingCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTransCoding.isEnabled = customSettingCheckbox.isChecked();
            }
        });

        SeekBar widthSeekBar = (SeekBar) layout.findViewById(R.id.text_set_width);
        final TextView widthTextView = (TextView) layout.findViewById(R.id.show_width);
        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }

                widthTextView.setText(progress + "");
                mTransCoding.width = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
        widthSeekBar.setProgress(mTransCoding.width);

        SeekBar heightSeekBar = (SeekBar) layout.findViewById(R.id.text_set_height);
        final TextView heightTextView = (TextView) layout.findViewById(R.id.show_height);
        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }

                heightTextView.setText(progress + "");
                mTransCoding.height = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
        heightSeekBar.setProgress(mTransCoding.height);
        //ViewGroup.LayoutParams lp_height = setWidth.getLayoutParams();
        //setHight.setLayoutParams(lp_height);
        //setHight.setProgress(mCustomLiveTransCoding.height);
        //showHeight.setText(mCustomLiveTransCoding.height + "");

        final SeekBar bitrateSeekBar = (SeekBar) layout.findViewById(R.id.text_set_bitrate);
        final TextView bitRateTextView = (TextView) layout.findViewById(R.id.show_bitrate);
        bitrateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }

                bitRateTextView.setText(progress + "");
                mTransCoding.bitrate = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        bitrateSeekBar.setProgress(mTransCoding.bitrate);
        //bitrateSeekBar.setMax(1800);
        //ViewGroup.LayoutParams lp_bitrate = bitrateSeekBar.getLayoutParams();
        //lp_bitrate.width = 400;
        //bitrateSeekBar.setLayoutParams(lp_bitrate);
        //setBitrate.setProgress(mCustomLiveTransCoding.bitrate);
        //showBitRate.setText(mCustomLiveTransCoding.bitrate + "");

        SeekBar fpsSeekBar = (SeekBar) layout.findViewById(R.id.text_set_fps);
        final TextView fpsTextView = (TextView) layout.findViewById(R.id.show_fps);
        fpsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }

                fpsTextView.setText(progress + "");
                mTransCoding.framerate = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
        fpsSeekBar.setProgress(mTransCoding.framerate);
        //setFPS.setMax(60);
        //ViewGroup.LayoutParams lp_setFPS = setBitrate.getLayoutParams();
        //lp_setFPS.width = 400;
        //setFPS.setLayoutParams(lp_setFPS);
        //setFPS.setProgress(mCustomLiveTransCoding.framerate);
        //showFPS.setText(mCustomLiveTransCoding.framerate + "");

        final CheckBox latencyCheckBox = (CheckBox) layout.findViewById(R.id.cb_low_latency);
        latencyCheckBox.setOnClickListener(null);
        latencyCheckBox.setChecked(mTransCoding.lowLatency);
        latencyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTransCoding.lowLatency = latencyCheckBox.isChecked();
            }
        });

        CheckBox backGroundCheckBox = (CheckBox) layout.findViewById(R.id.cb_enable_color);
        //mBackground.setChecked(mBackgroundFlag);
        backGroundCheckBox.setEnabled(false);
        backGroundCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        SeekBar gopSeekBar = (SeekBar) layout.findViewById(R.id.text_set_gop);
        final TextView gopTextView = (TextView) layout.findViewById(R.id.show_gop);
        gopSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }

                mTransCoding.gop = progress;
                gopTextView.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
        gopSeekBar.setProgress(mTransCoding.gop);
        //setGop.setMax(60);
        //ViewGroup.LayoutParams Goplp = setGop.getLayoutParams();
        //Goplp.width = 400;
        //showGop.setLayoutParams(Goplp);
        //setGop.setProgress(mCustomLiveTransCoding.gop);
        //showGop.setText("" +  mCustomLiveTransCoding.gop);

        RadioGroup videoCodec = (RadioGroup) layout.findViewById(R.id.codec);
        if(mTransCoding.videoCodecProfile == Constants.VIDEO_CODEC_PROFILE_TYPE_BASELINE){
            RadioButton button =  (RadioButton) layout.findViewById(R.id.codec_baseline);
            button.setChecked(true);
        } else if(mTransCoding.videoCodecProfile == Constants.VIDEO_CODEC_PROFILE_TYPE_HIGH){
            RadioButton button =  (RadioButton) layout.findViewById(R.id.codec_high);
            button.setChecked(true);
        } else if(mTransCoding.videoCodecProfile == Constants.VIDEO_CODEC_PROFILE_TYPE_MAIN){
            RadioButton button = (RadioButton) layout.findViewById(R.id.codec_main);
            button.setChecked(true);
        }

        videoCodec.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.codec_baseline:
                        mTransCoding.videoCodecProfile = Constants.VIDEO_CODEC_PROFILE_TYPE_BASELINE;
                        break;
                    case R.id.codec_high:
                        mTransCoding.videoCodecProfile = Constants.VIDEO_CODEC_PROFILE_TYPE_HIGH;
                        break;
                    case R.id.codec_main:
                        mTransCoding.videoCodecProfile = Constants.VIDEO_CODEC_PROFILE_TYPE_MAIN;

                        break;
                    default:
                        break;
                }
            }
        });

        if(mTransCoding.sampleRate == Constants.AUDIO_SAMPLE_RATE_TYPE_32000){
            RadioButton button =  (RadioButton) layout.findViewById(R.id.samplerate_32);
            button.setChecked(true);
        } else if(mTransCoding.sampleRate == Constants.AUDIO_SAMPLE_RATE_TYPE_44100){
            RadioButton button =  (RadioButton) layout.findViewById(R.id.samplerate_44_1);
            button.setChecked(true);
        } else if(mTransCoding.sampleRate == Constants.AUDIO_SAMPLE_RATE_TYPE_48000){
            RadioButton button = (RadioButton) layout.findViewById(R.id.samplerate_48);
            button.setChecked(true);
        }

        RadioGroup sample = (RadioGroup) layout.findViewById(R.id.samplerate);
        sample.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.samplerate_32:
                        mTransCoding.sampleRate = Constants.AUDIO_SAMPLE_RATE_TYPE_32000;
                        break;
                    case R.id.samplerate_44_1:
                        mTransCoding.sampleRate = Constants.AUDIO_SAMPLE_RATE_TYPE_44100;
                        break;
                    case R.id.samplerate_48:
                        mTransCoding.sampleRate = Constants.AUDIO_SAMPLE_RATE_TYPE_48000;
                        break;
                }
            }
        });

        SeekBar redSeekBar = (SeekBar) layout.findViewById(R.id.set_red);
        final TextView redTextView = (TextView) layout.findViewById(R.id.set_red_vaule);
        //redSeekBar.setMax(255);
        //ViewGroup.LayoutParams Redlp = setRed.getLayoutParams();
        //Redlp.width = 400;
        //setRed.setLayoutParams(Redlp);
        //setRed.setProgress(mCustomLiveTransCoding.getRed());
        //showRed.setText(mCustomLiveTransCoding.getRed() + "");
        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                redTextView.setText(progress + "");
                mTransCoding.setRed(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        redSeekBar.setProgress(mTransCoding.getRed());

        SeekBar greenSeekBar = (SeekBar) layout.findViewById(R.id.set_green);
        final TextView greenTextView = (TextView) layout.findViewById(R.id.set_green_vaule);
        //setGreen.setMax(255);
        //ViewGroup.LayoutParams Greenlp = setRed.getLayoutParams();
        //Greenlp.width = 400;
        //setGreen.setLayoutParams(Greenlp);
        //setGreen.setProgress(mCustomLiveTransCoding.getGreen());
        //showGreen.setText(mCustomLiveTransCoding.getGreen() + "");
        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                greenTextView.setText(progress + "");
                mTransCoding.setGreen(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        greenSeekBar.setProgress(mTransCoding.getGreen());

        SeekBar blueSeekBar = (SeekBar) layout.findViewById(R.id.set_blue);
        final TextView blueTextView = (TextView) layout.findViewById(R.id.set_blue_vaule);
        //setBlue.setMax(255);
        //ViewGroup.LayoutParams Bluelp = setRed.getLayoutParams();
        //Bluelp.width = 400;
        //setBlue.setLayoutParams(Bluelp);
        //setBlue.setProgress(mCustomLiveTransCoding.getBlue());
        //showBlue.setText(mCustomLiveTransCoding.getBlue() + "");
        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blueTextView.setText(progress + "");
                mTransCoding.setBlue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        blueSeekBar.setProgress(mTransCoding.getBlue());

        if(mTransCoding.layout == CustomTranscoding.LAYOUT_DEFAULT){
            RadioButton button =  (RadioButton) layout.findViewById(R.id.transcode_default);
            button.setChecked(true);
        } else if(mTransCoding.layout == CustomTranscoding.LAYOUT_FLOAT){
            RadioButton button =  (RadioButton) layout.findViewById(R.id.transcode_float);
            button.setChecked(true);
        } else if(mTransCoding.layout == CustomTranscoding.LAYOUT_TITLE){
            RadioButton button = (RadioButton) layout.findViewById(R.id.transcode_title);
            button.setChecked(true);
        } else if(mTransCoding.layout == CustomTranscoding.LAYOUT_MATRIX){
            RadioButton button = (RadioButton) layout.findViewById(R.id.transcode_martix);
            button.setChecked(true);
        }

        RadioGroup layoutRadioGroup = (RadioGroup) layout.findViewById(R.id.transcdde_layout);
        layoutRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.transcode_default:
                        mTransCoding.layout = CustomTranscoding.LAYOUT_DEFAULT;
                        break;
                    case R.id.transcode_float: {
                        mTransCoding.layout = CustomTranscoding.LAYOUT_FLOAT;
                        break;
                    }
                    case R.id.transcode_title:
                        mTransCoding.layout = CustomTranscoding.LAYOUT_TITLE;
                        break;
                    case R.id.transcode_martix:
                        mTransCoding.layout = CustomTranscoding.LAYOUT_MATRIX;
                        break;
                    default:
                        break;

                }

                //setTranscodingLayout(layout_transform);
            }
        });

        builder = new AlertDialog.Builder(mContext);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mListener != null) {
                    mListener.onUpdateTranscoding(mTransCoding);
                }
            }
        });
        alertDialog.show();
    }

    public void setOnUpdateTranscodingListener(OnUpdateTranscodingListener listener) {
        mListener = listener;
    }

    interface OnUpdateTranscodingListener {
        public void onUpdateTranscoding(CustomTranscoding customTranscoding);
    }

}
