package com.jaej.demo;


import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jaej.demo.model.Task;


public class SoundPrefActivity extends AppCompatActivity implements
        View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener
{

    public static final String SOUND_SETTING_OBJECT = "SOUND_SETTING_OBJECT";

    private Spinner mRingtoneSpinner;
    private SeekBar mVolumeSeekBar, mRepetitionSeekBar;
    private TextView mVolumeTextView, mRepetitionTextView;
    private Switch mPlayAsMediaSwitch;
    private Task currentTask;

    private int sound;
    private SoundPool alarmSoundPool, mediaSoundPool;
    //two different attributes declared to be able to play both with a test button
    private AudioAttributes alarmAttribute, mediaAttribute;

    public SoundPrefActivity() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sound_setting_popup);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mRingtoneSpinner = findViewById(R.id.ringtone_spinner);
        mVolumeSeekBar = findViewById(R.id.volume_seek_bar);
        mRepetitionSeekBar = findViewById(R.id.repetition_seek_bar);
        mVolumeTextView = findViewById(R.id.volume_text_view);
        mRepetitionTextView = findViewById(R.id.repetition_text_view);
        mPlayAsMediaSwitch = findViewById(R.id.play_media_switch);
        ImageButton mTestButton = findViewById(R.id.test_button);
        ImageButton mBackButton = findViewById(R.id.close_button);
        ImageButton mSaveButton = findViewById(R.id.save_button);

        mTestButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mVolumeSeekBar.setOnSeekBarChangeListener(this);

        ArrayAdapter<CharSequence> ringtoneAdapter = ArrayAdapter.createFromResource(
                this, R.array.ringtone, R.layout.custom_spinner);
        ringtoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRingtoneSpinner.setAdapter(ringtoneAdapter);
        mRingtoneSpinner.setOnItemSelectedListener(this);

        mediaAttribute = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        alarmAttribute = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        Gson gson = new Gson();
        currentTask = gson.fromJson(
                getIntent().getStringExtra(AddModifyActivity.MOD_SOUND_SETTING), Task.class);


        mediaSoundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(mediaAttribute)
                .build();
        alarmSoundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(alarmAttribute)
                .build();



        mRingtoneSpinner.setSelection(currentTask.getRingtoneIndex());
        mVolumeSeekBar.setProgress(currentTask.getAlarmVolume());
        mVolumeTextView.setText(String.valueOf(mVolumeSeekBar.getProgress()));
        mRepetitionSeekBar.setProgress(currentTask.getAlarmRepetition());
        mRepetitionTextView.setText(String.valueOf(mRepetitionSeekBar.getProgress()));
        mPlayAsMediaSwitch.setChecked(currentTask.isPlayAsMedia());



    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.test_button) {
            float floatVolume = (float) currentTask.getAlarmVolume()/100;
            if (mPlayAsMediaSwitch.isChecked())
                mediaSoundPool.play(sound, floatVolume, floatVolume,
                        0, currentTask.getAlarmRepetition(), 1);
            else
                alarmSoundPool.play(sound, floatVolume, floatVolume,
                        0, currentTask.getAlarmRepetition(), 1);
        }
        else if (v.getId() == R.id.close_button) {
            alarmSoundPool.release();
            mediaSoundPool.release();
            alarmSoundPool = null;
            mediaSoundPool = null;
            setResult(RESULT_CANCELED);
            finish();
        }
        else if (v.getId() == R.id.save_button) {
            currentTask.setRingtoneIndex(mRingtoneSpinner.getSelectedItemPosition());
            currentTask.setAlarmVolume(mVolumeSeekBar.getProgress());
            currentTask.setAlarmRepetition(mRepetitionSeekBar.getProgress());
            currentTask.setPlayAsMedia(mPlayAsMediaSwitch.isChecked());

            alarmSoundPool.release();
            mediaSoundPool.release();
            alarmSoundPool = null;
            mediaSoundPool = null;

            Intent soundPrefIntent = new Intent();
            Gson gson = new Gson();
            String soundPrefStringJson = gson.toJson(currentTask);
            soundPrefIntent.putExtra(SOUND_SETTING_OBJECT, soundPrefStringJson);
            setResult(RESULT_OK, soundPrefIntent);
            finish();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //set progressbar result to the view and the record data
        if (seekBar.getId() == R.id.volume_seek_bar) {
            currentTask.setAlarmVolume(progress);
            mVolumeTextView.setText(String.valueOf(progress));
        }
        else if (seekBar.getId() == R.id.repetition_seek_bar) {
            currentTask.setAlarmRepetition(progress);
            mRepetitionTextView.setText(String.valueOf(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                sound = mediaSoundPool.load(SoundPrefActivity.this, R.raw.a_modern_notification, 1);
                sound = alarmSoundPool.load(SoundPrefActivity.this, R.raw.a_modern_notification, 1);
                break;
            case 1:
                sound = mediaSoundPool.load(SoundPrefActivity.this, R.raw.b_airplane_bell, 1);
                sound = alarmSoundPool.load(SoundPrefActivity.this, R.raw.b_airplane_bell, 1);
                break;
            case 2:
                sound = mediaSoundPool.load(SoundPrefActivity.this, R.raw.c_triangle, 1);
                sound = alarmSoundPool.load(SoundPrefActivity.this, R.raw.c_triangle, 1);
                break;
            case 3:
                sound = mediaSoundPool.load(SoundPrefActivity.this, R.raw.d_marimba1, 1);
                sound = alarmSoundPool.load(SoundPrefActivity.this, R.raw.d_marimba1, 1);
                break;
            case 4:
                sound = mediaSoundPool.load(SoundPrefActivity.this, R.raw.e_marimba2, 1);
                sound = alarmSoundPool.load(SoundPrefActivity.this, R.raw.e_marimba2, 1);
                break;
            case 5:
                sound = mediaSoundPool.load(SoundPrefActivity.this, R.raw.f_arcade, 1);
                sound = alarmSoundPool.load(SoundPrefActivity.this, R.raw.f_arcade, 1);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alarmSoundPool != null) {
            //releasing soundpool
            alarmSoundPool.release();
            mediaSoundPool.release();
            alarmSoundPool = null;
            mediaSoundPool = null;
        }
    }



}

