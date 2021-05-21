package com.jaej.demo.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.jaej.demo.MainActivity;
import com.jaej.demo.R;
import com.jaej.demo.model.Record;
import com.jaej.demo.model.Task;
import com.jaej.demo.util.App;
import com.jaej.demo.util.Constants;
import com.jaej.demo.util.Utility;

import org.jetbrains.annotations.NotNull;


public class DoTimedTaskFragment extends Fragment implements
        View.OnClickListener
{

    private TextView mTimerTextView, mTaskCounterTextView;
    private ProgressBar mTimerProgressBar;
    private ImageButton mTakeRestButton, mGiveUpButton, mBeginButton;
    private HomeViewModel mHomeViewModel;

    private Task currentTask;
    private Record currentRecord;
    private View mView;

    private boolean isTimerRunning;

    private AudioAttributes audioAttributes;
    private SoundPool soundPool;
    private int streamID;
    private boolean playNow = false;

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Gson gson = new Gson();
            currentTask = gson.fromJson(savedInstanceState.getString(Constants.STATE_TASK), Task.class);
            currentRecord = gson.fromJson(savedInstanceState.getString(Constants.STATE_RECORD), Record.class);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Gson gson = new Gson();
            currentTask = gson.fromJson(savedInstanceState.getString(Constants.STATE_TASK), Task.class);
            currentRecord = gson.fromJson(savedInstanceState.getString(Constants.STATE_RECORD), Record.class);
        }

        setHasOptionsMenu(true);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //in case user wants to navigate up while task is ongoing
                //same behavior/alertdialog as clicking navigate up item button
                if (isTimerRunning) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Can't go back when there's ongoing task.");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    if (isEnabled())
                        setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_do_timed_task, container, false);
        mView = view;
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Gson gson = new Gson();
        outState.putString(Constants.STATE_TASK, gson.toJson(currentTask));
        outState.putString(Constants.STATE_RECORD, gson.toJson(currentRecord));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (MainActivity.isFabShowing()) {
            MainActivity.hideFAB();
        }
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        isTimerRunning = false;

        TextView mTaskNameTextView = view.findViewById(R.id.task_name);
        mTimerTextView = view.findViewById(R.id.remaining_time);
        mTaskCounterTextView = view.findViewById(R.id.task_remaining);
        TextView mTakeRestTextView = view.findViewById(R.id.take_rest_minute_text_view);
        mTimerProgressBar = view.findViewById(R.id.countdown_progress_bar);
        mTakeRestButton = view.findViewById(R.id.break_image_button);
        mGiveUpButton = view.findViewById(R.id.give_up_image_button);
        mBeginButton = view.findViewById(R.id.start_task_image_button);

        //stop button disabled by default when there is no ongoing task
        setButtons(false, true, true);
        mTakeRestButton.setOnClickListener(this);
        mGiveUpButton.setOnClickListener(this);
        mBeginButton.setOnClickListener(this);


        Gson gson = new Gson();
        Bundle args = getArguments();
        if (args != null) {
            if (gson.fromJson(args.getString(Constants.TASK_FROM_HOME), Task.class) != null) {
                //getting data from HomeFragment
                currentTask = gson.fromJson(args.getString(Constants.TASK_FROM_HOME), Task.class);
                currentRecord = gson.fromJson(args.getString(Constants.RECORD_FROM_HOME), Record.class);
            }
            else if (gson.fromJson(args.getString(Constants.TASK_FROM_ACTIVITY), Task.class) != null &&
            args.getBoolean(Constants.IS_TIMER_RUNNING)) {
                //getting ongoing data from MainActivity when relaunched
                currentTask = gson.fromJson(args.getString(Constants.TASK_FROM_ACTIVITY), Task.class);
                currentRecord = gson.fromJson(args.getString(Constants.TASK_FROM_ACTIVITY), Record.class);
                isTimerRunning = true;
                setButtons(true, false, false);

            }
            else if (gson.fromJson(args.getString(Constants.TASK_FROM_ACTIVITY), Task.class) != null &&
                    !args.getBoolean(Constants.IS_TIMER_RUNNING)) {
                //getting data from MainActivity for finished tasks
                currentTask = gson.fromJson(args.getString(Constants.TASK_FROM_ACTIVITY), Task.class);
                currentRecord = gson.fromJson(args.getString(Constants.TASK_FROM_ACTIVITY), Record.class);
                isTimerRunning = false;
                setButtons(false, true, true);
                playNow = true;
            }
        }

        //play as media attribute
        if (currentTask.isPlayAsMedia()) {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
        }
        //alarm attribute
        else {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
        }
        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes)
                .build();

        streamID = getRingtoneSound(currentTask.getRingtoneIndex());

        if (playNow) {
            //soundpool often don't play sound for the first time due to the time it takes
            //to load its sound, thus delayed for 1 second to make sure the sound is played.
            new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    soundPool.play(streamID,
                            (float) currentTask.getAlarmVolume() / 100,
                            (float) currentTask.getAlarmVolume() / 100,
                            0,
                            currentTask.getAlarmRepetition(),
                            1);
                }
            }.run();
        }

        mTaskNameTextView.setText(currentTask.getTaskName());

        String[] taskMinuteList = getResources().getStringArray(R.array.task_minutes);
        String[] restMinuteList = getResources().getStringArray(R.array.rest_minute);
        mTakeRestTextView.setText("Take " + restMinuteList[currentTask.getRestTime()]);

        mTimerTextView.setText(Utility.minuteStringFormatHelper(taskMinuteList[currentTask.getTaskTime()]));
        mTimerProgressBar.setMax(Integer.parseInt(taskMinuteList[currentTask.getTaskTime()])*60);

        mTimerProgressBar.setProgress(Integer.parseInt(taskMinuteList[currentTask.getTaskTime()])*60);




        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.COUNTER);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer integerTime = intent.getIntExtra(Constants.TIMER_TIME_REMAINING, 0);
                boolean isTimerFinished = intent.getBooleanExtra(Constants.TIMER_FINISHED, false);
                boolean isTaskTimer = intent.getBooleanExtra(Constants.TIMER_BROADCAST, true);
                mTimerTextView.setText(Utility.secondStringFormatHelper(integerTime));
                mTimerProgressBar.setProgress(integerTime.intValue());

                if (integerTime == 0 && !isTimerFinished) {
                    //timer force stopped
                    isTimerRunning = false;
                    setButtons(false, true, true);
                    Intent endIntent = new Intent(context, CountdownService.class);
                    endIntent.setAction(Constants.STOP_FOREGROUND);
                    //stop the service
                    context.startService(endIntent);
                    //reset the views
                    mTimerTextView.setText(Utility.minuteStringFormatHelper(taskMinuteList[currentTask.getTaskTime()]));
                    mTimerProgressBar.setProgress(Integer.parseInt(taskMinuteList[currentTask.getTaskTime()])*60);
                }
                else if (integerTime == 0 && isTimerFinished && isTaskTimer && isTimerRunning) {
                    //task timer finished
                    activateAppWhenInBackground(context, intent);

                    isTimerRunning = false;

                    if (App.isActivityVisible()) {
                        soundPool.play(streamID,
                                (float) currentTask.getAlarmVolume() / 100,
                                (float) currentTask.getAlarmVolume() / 100,
                                0,
                                currentTask.getAlarmRepetition(),
                                1);
                    }

                    setButtons(false, true, true);

                    if (currentRecord.getRepetitionCount() < currentRecord.getRepetitionMax()) {
                        //if repetition count is less than max, increment counter and update the record
                        currentRecord.setRepetitionCount(currentRecord.getRepetitionCount()+1);
                        mHomeViewModel.updateRecord(currentRecord);
                    }

                    String toastString;
                    if (currentRecord.getRepetitionMax() == currentRecord.getRepetitionCount()) {
                        //when daily repetition counter reaches its max
                        toastString = "Good Job! You're done with this task for today. You can keep " +
                                "working on this task, but any progress on this task will not " +
                                "be saved/counted.";
                    }
                    else {
                        //fire long toast with remaining repetition for the day
                        String[] numberArray = {"Zero", "One", "Two", "Three", "Four", "Five","Six",
                                                "Seven", "Eight", "Nine"};
                        toastString = numberArray[currentRecord.getRepetitionCount()] + " Down, " +
                                numberArray[currentRecord.getRepetitionMax() -
                                        currentRecord.getRepetitionCount()] + " to Go.";
                    }
                    Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
                    //update score
                    mHomeViewModel.setCurrentScore(currentRecord.getTaskID());
                    mTimerTextView.setText(Utility.minuteStringFormatHelper(taskMinuteList[currentTask.getTaskTime()]));
                    mTimerProgressBar.setProgress(Integer.parseInt(taskMinuteList[currentTask.getTaskTime()]) * 60);
                }

                else if (integerTime == 0 && isTimerFinished && !isTaskTimer && isTimerRunning) {
                    //rest timer finished
                    activateAppWhenInBackground(context, intent);
                    isTimerRunning = false;
                    soundPool.play(streamID,
                            (float) currentTask.getAlarmVolume() / 100,
                            (float) currentTask.getAlarmVolume() / 100,
                            0,
                            currentTask.getAlarmRepetition(),
                            1);
                    setButtons(false, true, true);
                    Toast.makeText(context, "Rest Timer Finished!", Toast.LENGTH_SHORT).show();
                    mTimerTextView.setText(Utility.minuteStringFormatHelper(taskMinuteList[currentTask.getTaskTime()]));
                    mTimerProgressBar.setProgress(Integer.parseInt(taskMinuteList[currentTask.getTaskTime()]) * 60);
                }
            }
        };
        getActivity().registerReceiver(broadcastReceiver, intentFilter);

        //livedata used for the Textview showing the numbers of repetition finished and to be done
        mHomeViewModel.getSingleRecord(currentRecord.getTaskID()).observe(getViewLifecycleOwner(), new Observer<Record>() {
            @Override
            public void onChanged(Record record) {
                mTaskCounterTextView.setText(Html.fromHtml(
                        remainingTaskRepetition(record.getRepetitionCount(), record.getRepetitionMax()),
                        Html.FROM_HTML_MODE_LEGACY));
            }
        });

    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.give_up_image_button) {
            //make sure user will lose all its progress with dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Cancel ongoing timer? Any progress will not be saved.");
            builder.setPositiveButton("give up", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //stop the service and disable buttons
                    setButtons(false, true, true);
                    isTimerRunning = false;
                    Intent endIntent = new Intent(requireContext(), CountdownService.class);
                    endIntent.setAction(Constants.STOP_FOREGROUND);
                    getContext().startService(endIntent);
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (v.getId() == R.id.break_image_button) {
            if (!isTimerRunning) {
                isTimerRunning = true;
                setButtons(true, false, false);
                Intent startIntent = new Intent(requireContext(), CountdownService.class);
                startIntent.setAction(Constants.START_REST_FOREGROUND);

                String[] restMinuteList = getResources().getStringArray(R.array.rest_minute);

                Integer integerTimeSet = Integer.parseInt(restMinuteList[currentTask.getRestTime()])*60;

                startIntent.putExtra(Constants.TIMER_TIME_VALUE, integerTimeSet);
                startIntent.putExtra(Constants.TIMER_TASK_NAME, currentTask.getTaskName());
                startIntent.putExtra(Constants.TIMER_TASK_TIMER, false);

                Gson gson = new Gson();
                String currentTaskJsonString = gson.toJson(currentTask);
                String currentRecordJsonString = gson.toJson(currentRecord);
                startIntent.putExtra(Constants.TASK_FROM_FRAGMENT, currentTaskJsonString);
                startIntent.putExtra(Constants.RECORD_FROM_FRAGMENT, currentRecordJsonString);
                getActivity().startService(startIntent);
            }
        }

        else if (v.getId() == R.id.start_task_image_button) {
            if (!isTimerRunning) {
                isTimerRunning = true;
                setButtons(true, false, false);
                Intent startIntent = new Intent(requireContext(), CountdownService.class);
                startIntent.setAction(Constants.START_TASK_FOREGROUND);

                String[] taskMinuteList = getResources().getStringArray(R.array.task_minutes);

                Integer integerTimeSet = Integer.parseInt(taskMinuteList[currentTask.getTaskTime()])-Integer.parseInt(taskMinuteList[currentTask.getTaskTime()]) + 3;
//                    Integer integerTimeSet = Integer.valueOf(minuteList[currentTask.getTaskTime()]) * 60;

                startIntent.putExtra(Constants.TIMER_TIME_VALUE, integerTimeSet);
                startIntent.putExtra(Constants.TIMER_TASK_NAME, currentTask.getTaskName());
                startIntent.putExtra(Constants.TIMER_TASK_TIMER, true);

                Gson gson = new Gson();
                String currentTaskJsonString = gson.toJson(currentTask);
                String currentRecordJsonString = gson.toJson(currentRecord);
                startIntent.putExtra(Constants.TASK_FROM_FRAGMENT, currentTaskJsonString);
                startIntent.putExtra(Constants.RECORD_FROM_FRAGMENT, currentRecordJsonString);

                getActivity().startService(startIntent);
            }
        }
    }



    //disable navigate up button while task is ongoing, and notify the user
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(mView);
        if (isTimerRunning) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Can't go back when there's ongoing timer.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        else {
            //when there isn't ongoing task, navigate up
            return NavigationUI.onNavDestinationSelected(item, navController) ||
                    super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        //set public static boolean activityVisible false
        App.activityPaused();
        super.onPause();
    }

    @Override
    public void onResume() {
        //set public static boolean activityVisible true
        App.activityResumed();
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //release soundpool before leaving the fragment
        soundPool.release();
        soundPool = null;
    }

    //append colored blocks depending on numbers of remaining repetition count
    private String remainingTaskRepetition(int rep, int repMax) {
        String doneBlock = "<font color=#008000>\u25A0</font>";
        String toBeDoneBlock = "<font color=#ff0000>\u25A0</font>";
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < repMax; i++) {
            if (i < rep) {
                buffer.append(doneBlock);
            }
            else {
                buffer.append(toBeDoneBlock);
            }
            buffer.append("  ");
        }
        return buffer.toString().trim();
    }

    //disable buttons depending on the input boolean values
    private void setButtons(boolean stop, boolean takeRest, boolean begin) {
        
        mGiveUpButton.setEnabled(false);
        mGiveUpButton.setAlpha(0.2f);
        mTakeRestButton.setEnabled(false);
        mTakeRestButton.setAlpha(0.2f);
        mBeginButton.setEnabled(false);
        mBeginButton.setAlpha(0.2f);
        
        if (stop) {
            mGiveUpButton.setEnabled(true);
            mGiveUpButton.setAlpha(1f);
        }
        if (takeRest) {
            mTakeRestButton.setEnabled(true);
            mTakeRestButton.setAlpha(1f);
        }
        if (begin) {
            mBeginButton.setEnabled(true);
            mBeginButton.setAlpha(1f);
        }
    }

    //choose sound amongst sound resources
    private int getRingtoneSound(int index) {
        int soundStreamID;
        switch (index) {
            case 1:
                soundStreamID = soundPool.load(this.getContext(), R.raw.b_airplane_bell, 1);
                break;
            case 2:
                soundStreamID = soundPool.load(this.getContext(), R.raw.c_triangle, 1);
                break;
            case 3:
                soundStreamID = soundPool.load(this.getContext(), R.raw.d_marimba1, 1);
                break;
            case 4:
                soundStreamID = soundPool.load(this.getContext(), R.raw.e_marimba2, 1);
                break;
            case 5:
                soundStreamID = soundPool.load(this.getContext(), R.raw.f_arcade, 1);
                break;
            case 0:
            default:
                soundStreamID = soundPool.load(this.getContext(), R.raw.a_modern_notification, 1);
        }

        return soundStreamID;
    }


    //when the app is not visible and in background, the method relaunch MainActivity
    //to restart/recreate fragments and views
    private void activateAppWhenInBackground(Context context, Intent intent) {
        if (!App.isActivityVisible()) {

            Intent endIntent = context
                    .getPackageManager()
                    .getLaunchIntentForPackage(context.getPackageName())
                    .setClass(context, MainActivity.class)
                    .setAction(Constants.ACTIVATE_INTENT_FROM_FRAGMENT);

            String taskString = intent.getStringExtra(Constants.TIMER_TASK);
            String recordString = intent.getStringExtra(Constants.TIMER_RECORD);

            endIntent.putExtra(Constants.TASK_FROM_FRAGMENT, taskString);
            endIntent.putExtra(Constants.RECORD_FROM_FRAGMENT, recordString);

            endIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(endIntent);

        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }


}