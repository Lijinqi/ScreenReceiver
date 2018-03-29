/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lijinqi.screenreceiver.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.lijinqi.screenreceiver.R;
import com.lijinqi.screenreceiver.application.Settings;
import com.lijinqi.screenreceiver.media.AndroidMediaController;
import com.lijinqi.screenreceiver.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";
    IjkMediaPlayer mediaPlayer;
    private String mVideoPath;
    private Uri    mVideoUri;

    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private Button mBtnPlay;
    private EditText mEtUrl;

    private Settings mSettings;
    private boolean mBackPressed;

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        mSettings = new Settings(this);
        

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mBtnPlay = (Button) findViewById(R.id.btn_start);
        mEtUrl = (EditText) findViewById(R.id.et_url);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoView.setMediaController(mMediaController);

//        mVideoPath = Environment.getExternalStorageDirectory()+"/Movies/as.mp4";
//        mVideoUri = Uri.parse("http://192.168.0.109:8080");
//        mVideoUri = Uri.parse("udp://@:28888");

        mEtUrl.setText(getSharedPreferences("default", 0).getString("url", ""));

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoUri = Uri.parse(mEtUrl.getText().toString().trim());
                Log.e("27","Uri :"+mVideoUri);
                getSharedPreferences("default", 0).edit().putString("url", mEtUrl.getText().toString().trim()).apply();

                // prefer mVideoPath
                if (mVideoPath != null){
                    Log.e(TAG,"play with path"+mVideoPath);
                    mVideoView.setVideoPath(mVideoPath);
                }
                else if (mVideoUri != null)
                    mVideoView.setVideoURI(mVideoUri);
                else {
                    Log.e(TAG, "Null Data Source\n");
                    finish();
                    return;
                }
                mVideoView.start();
            }
        });

    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;

        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }


}
