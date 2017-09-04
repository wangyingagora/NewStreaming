package io.agora.streaming.ex;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import io.agora.streaming.R;

public class MainActivity extends AgoraBaseActivity {
    private CheckBox mEnableBox;
    private boolean mEnableVideo;
    private int mRetCode = 101;
    private int mWidth = 320;
    private int mHeight = 240;
    private int mBitRate = 400;
    private String GET_WIDTH_VAULE = "get width";
    private String GET_HEIGHT_VAULE = "get height";
    private String GET_BITRATE_VAULE ="get bitrate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUIandEvent();
    }

    protected void initUIandEvent() {
        mEnableBox = (CheckBox)findViewById(R.id.enable_video);
        mEnableVideo = true;
        mEnableBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(!((CheckBox) v).isChecked()){
                     Log.e("adam", " disable video");
                     mEnableVideo = false;
                 } else {
                     mEnableVideo = true;
                     Log.e("adam", "disable video");
                 }
            }
        });

        mEnableBox.setChecked(true);
        final EditText v_channel = (EditText) findViewById(R.id.channel_name);
        v_channel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //mChannelName = v_channel.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = TextUtils.isEmpty(s.toString());
                findViewById(R.id.button_join).setEnabled(!isEmpty);
            }
        });

        Spinner encryptionSpinner = (Spinner) findViewById(R.id.encryption_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.encryption_mode_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        encryptionSpinner.setAdapter(adapter);

        encryptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //vSettings().mEncryptionModeIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //encryptionSpinner.setSelection(vSettings().mEncryptionModeIndex);

//        String lastChannelName = vSettings().mChannelName;
//        if (!TextUtils.isEmpty(lastChannelName)) {
//            v_channel.setText(lastChannelName);
//            v_channel.setSelection(lastChannelName.length());
//        }

        EditText v_encryption_key = (EditText) findViewById(R.id.encryption_key);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                forwardToSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickJoin(View view) {
        forwardToRoom();
    }

    public void forwardToRoom() {
        EditText v_channel = (EditText) findViewById(R.id.channel_name);
        String channel = v_channel.getText().toString().trim();
        if (TextUtils.isEmpty(channel)) {
            showToast("Channel name cannot be empty");
            return;
        }

        EditText v_encryption_key = (EditText) findViewById(R.id.encryption_key);
        String encryption = v_encryption_key.getText().toString();
        //vSettings().mEncryptionKey = encryption;

        Intent i = new Intent(MainActivity.this, ChannelActivity.class);
        i.putExtra(AgoraConstans.ACTION_KEY_CHANNEL_NAME, channel);
        i.putExtra(AgoraConstans.ACTION_KEY_ENCRYPTION_KEY, encryption);
        //i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, getResources().getStringArray(R.array.encryption_mode_values)[vSettings().mEncryptionModeIndex]);
        i.putExtra(AgoraConstans.ACTION_ENABLE_VIDEO , mEnableVideo);
        i.putExtra(AgoraConstans.ACTION_TRANSFORM_WIDTH, mWidth);
        i.putExtra(AgoraConstans.ACTION_TRANSFORM_HEIGHT, mHeight);
        i.putExtra(AgoraConstans.ACTION_TRANSFORM_BITRATE, mBitRate);
        startActivity(i);
    }

    public void forwardToSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, mRetCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
              if(data != null) {
                  mWidth = data.getIntExtra(GET_WIDTH_VAULE, 0);
                  mHeight = data.getIntExtra(GET_HEIGHT_VAULE, 0);
                  mBitRate = data.getIntExtra(GET_BITRATE_VAULE, 0);
                  Log.e("adam", " mWidth is " + mWidth);
                  Log.e("adam", "  mHeight is " + mHeight);
              }
    }
}
