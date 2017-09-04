package io.agora.streaming.ex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import io.agora.streaming.R;
import io.agora.streaming.ui.VideoProfileAdapter;

public class SettingsActivity extends AgoraBaseActivity {
    private final static String TAG = SettingsActivity.class.getSimpleName();

    private VideoProfileAdapter mVideoProfileAdapter;
    private String GET_WIDTH_VAULE = "get width";
    private String GET_HEIGHT_VAULE = "get height";
    private String GET_BITRATE_VAULE ="get bitrate";
    private int mRetCode = 101;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initUi();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void initUi() {
        RecyclerView v_profiles = (RecyclerView) findViewById(R.id.profiles);
        //v_profiles.setHasFixedSize(true);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(AgoraConstans.PrefManager.PREF_PROPERTY_PROFILE_IDX, AgoraConstans.DEFAULT_PROFILE_IDX);

        mVideoProfileAdapter = new VideoProfileAdapter(this, prefIndex);
        //mVideoProfileAdapter.setHasStableIds(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        v_profiles.setLayoutManager(layoutManager);
        v_profiles.setAdapter(mVideoProfileAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.confirm:
                doSaveProfile();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void doSaveProfile() {
        int width = 0;
        int height = 0;
        int bitrate = 0;
        int profileIndex = mVideoProfileAdapter.getSelected();
        Log.e("adam", " profileIndex is " + profileIndex);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(AgoraConstans.PrefManager.PREF_PROPERTY_PROFILE_IDX, profileIndex);
        editor.apply();

        String resolution = getResources().getStringArray(R.array.string_array_resolutions)[profileIndex];
        String frameRate = getResources().getStringArray(R.array.string_array_frame_rate)[profileIndex];
        String bitRate = getResources().getStringArray(R.array.string_array_bit_rate)[profileIndex];
        String[] size = resolution.split("x");
        if (size.length != 2) {
            Log.e(TAG, "parse resolution failed");
            return;
        }
        width = Integer.parseInt(size[0]);
        height = Integer.parseInt(size[1]);
        bitrate = Integer.parseInt(bitRate);

        mIntent = new Intent();
        mIntent.putExtra(GET_WIDTH_VAULE, width);
        mIntent.putExtra(GET_HEIGHT_VAULE, height);
        mIntent.putExtra(GET_BITRATE_VAULE, bitrate);
        this.setResult(mRetCode, mIntent);
    }
}
