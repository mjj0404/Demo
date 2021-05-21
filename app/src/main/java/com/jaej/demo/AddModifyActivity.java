package com.jaej.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.gson.Gson;
import com.jaej.demo.model.Task;
import com.jaej.demo.ui.task.TaskFragment;
import com.jaej.demo.util.Utility;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddModifyActivity extends AppCompatActivity
        implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener,
        SeekBar.OnSeekBarChangeListener {

    public static final String NEW_TASK_OBJECT = "NEW_TASK_OBJECT";
    public static final String MODIFIED_TASK_OBJECT = "MODIFIED_TASK_OBJECT";
    public static final String MOD_SOUND_SETTING = "MOD_SOUND_SETTING";
    private static final int SOUND_PREF_SETTING_CODE = 3;



    private EditText mTaskName;
    private Button mSundayButton, mMondayButton, mTuesdayButton, mWednesdayButton, mThursdayButton,
            mFridayButton, mSaturdayButton;
    private TextView mPickRangeTextView, mAddStartDateTextView, mAddEndDateTextView,
            mModifyStartDateTextView, mModifyEndDateTextView,
            mIsTimedTextView, mRepetitionTextView, mDash;
    private Switch mFinishMethodSwitch;
    private Spinner mTaskTimeSpinner, mRestTimeSpinner;
    private SeekBar mRepetitionSeekBar;
    private LinearLayout mTimeSessionLayout, mRepetitionLayout, mRestSessionLayout;
    private LinearLayout mSoundSettingLayout;
    private MaterialDatePicker<Long> datePicker;
    private MaterialDatePicker<Pair<Long, Long>> rangePicker;
    private boolean isAddingNewTask = false;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_modify);

        mTaskName = findViewById(R.id.edit_task);
        mSundayButton = findViewById(R.id.sunday_button);
        mMondayButton = findViewById(R.id.monday_button);
        mTuesdayButton = findViewById(R.id.tuesday_button);
        mWednesdayButton = findViewById(R.id.wednesday_button);
        mThursdayButton = findViewById(R.id.thursday_button);
        mFridayButton = findViewById(R.id.friday_button);
        mSaturdayButton = findViewById(R.id.saturday_button);
        ImageButton mSaveButton = findViewById(R.id.task_save);
        ImageButton mCloseButton = findViewById(R.id.task_close);
        ImageButton mSoundSettingButton = findViewById(R.id.sound_setting_button);
        mAddStartDateTextView = findViewById(R.id.start_date_text_view);
        mAddEndDateTextView = findViewById(R.id.end_date_text_view);
        mModifyStartDateTextView = findViewById(R.id.one_day_start_date_text_view);
        mModifyEndDateTextView = findViewById(R.id.one_day_end_date_text_view);
        mDash = findViewById(R.id.middle_dash);
        mPickRangeTextView = findViewById(R.id.pick_range_text_view);
        ImageButton mStartDatePickerImageButton = findViewById(R.id.one_day_picker);
        ImageButton mEndDatePickerImageButton = findViewById(R.id.range_picker);
        mIsTimedTextView = findViewById(R.id.is_timed_text_view);
        mRepetitionTextView = findViewById(R.id.task_repetition_text_view);
        mFinishMethodSwitch = findViewById(R.id.check_timed_switch);
        mTaskTimeSpinner = findViewById(R.id.task_mills_spinner);
        mRestTimeSpinner = findViewById(R.id.rest_mills_spinner);
        mRepetitionSeekBar = findViewById(R.id.task_repetition_seek_bar);
        mSoundSettingLayout = findViewById(R.id.sound_setting_layout);
        mRepetitionLayout = findViewById(R.id.repetition_layout);
        mTimeSessionLayout = findViewById(R.id.time_session_layout);
        mRestSessionLayout = findViewById(R.id.rest_session_layout);
        LinearLayout mRangePickerLayout = findViewById(R.id.range_picker_layout);
        LinearLayout mDatePickerLayout = findViewById(R.id.one_day_picker_layout);


        mFinishMethodSwitch.setOnCheckedChangeListener(this);
        mStartDatePickerImageButton.setOnClickListener(this);
        mEndDatePickerImageButton.setOnClickListener(this);
        mSundayButton.setOnClickListener(this);
        mMondayButton.setOnClickListener(this);
        mTuesdayButton.setOnClickListener(this);
        mWednesdayButton.setOnClickListener(this);
        mThursdayButton.setOnClickListener(this);
        mFridayButton.setOnClickListener(this);
        mSaturdayButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mCloseButton.setOnClickListener(this);

        //spinner setup
        ArrayAdapter<CharSequence> taskTimeAdapter = ArrayAdapter.createFromResource(
                this,R.array.task_minutes,R.layout.custom_spinner);
        taskTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTaskTimeSpinner.setAdapter(taskTimeAdapter);
        ArrayAdapter<CharSequence> restTimeAdapter = ArrayAdapter.createFromResource(
                this,R.array.rest_minute, R.layout.custom_spinner);
        restTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRestTimeSpinner.setAdapter(restTimeAdapter);

        mTaskTimeSpinner.setOnItemSelectedListener(this);
        mRestTimeSpinner.setOnItemSelectedListener(this);
        mRepetitionSeekBar.setOnSeekBarChangeListener(this);

        //getting task if modifying
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            Gson gson = new Gson();
            currentTask = gson.fromJson(getIntent().getStringExtra(
                    TaskFragment.TASK_TO_BE_MODIFIED), Task.class);
        }
        else {
            isAddingNewTask = true;
            currentTask = new Task();
        }

        //setup MaterialDatePicker UI
        dateSetter();

        if (isAddingNewTask) mDatePickerLayout.setVisibility(View.GONE);
        else mRangePickerLayout.setVisibility(View.GONE);

        //setting UI
        mTaskName.setText(currentTask.getTaskName());
        mRepetitionTextView.setText(String.valueOf(currentTask.getTaskRepetition()));
        mRepetitionSeekBar.setProgress(currentTask.getTaskRepetition());
        setDaysButton(currentTask.getDays());


        String startDateString = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(currentTask.getStartDate());
        String endDateString = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(currentTask.getEndDate());
        mModifyStartDateTextView.setText(startDateString);
        mModifyEndDateTextView.setText(endDateString);


        mTaskTimeSpinner.setSelection(currentTask.getTaskTime());
        mRestTimeSpinner.setSelection(currentTask.getRestTime());
        mRepetitionSeekBar.setProgress(currentTask.getTaskRepetition());


        if (currentTask.isTimed()) {
            mFinishMethodSwitch.setChecked(currentTask.isTimed());

        }
        else {
            mTimeSessionLayout.setVisibility(View.GONE);
            mSoundSettingLayout.setVisibility(View.GONE);
            mRepetitionLayout.setVisibility(View.GONE);
            mRestSessionLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sunday_button)
            mSundayButton.setSelected(!(mSundayButton.isSelected()));
        else if (v.getId() == R.id.monday_button)
            mMondayButton.setSelected(!(mMondayButton.isSelected()));
        else if (v.getId() == R.id.tuesday_button)
            mTuesdayButton.setSelected(!(mTuesdayButton.isSelected()));
        else if (v.getId() == R.id.wednesday_button)
            mWednesdayButton.setSelected(!(mWednesdayButton.isSelected()));
        else if (v.getId() == R.id.thursday_button)
            mThursdayButton.setSelected(!(mThursdayButton.isSelected()));
        else if (v.getId() == R.id.friday_button)
            mFridayButton.setSelected(!(mFridayButton.isSelected()));
        else if (v.getId() == R.id.saturday_button)
            mSaturdayButton.setSelected(!(mSaturdayButton.isSelected()));
        else if (v.getId() == R.id.one_day_picker)
            datePicker.show(getSupportFragmentManager(), datePicker.toString());
        else if (v.getId() == R.id.range_picker)
            rangePicker.show(getSupportFragmentManager(), rangePicker.toString());
        else if (v.getId() == R.id.sound_setting_button) {
            Intent soundPrefIntent = new Intent(
                    AddModifyActivity.this, SoundPrefActivity.class);
            Gson soundPrefGson = new Gson();
            String soundPrefTaskJson = soundPrefGson.toJson(currentTask);
            soundPrefIntent.putExtra(MOD_SOUND_SETTING, soundPrefTaskJson);
            startActivityForResult(soundPrefIntent, SOUND_PREF_SETTING_CODE);
        }
        else if (v.getId() == R.id.task_close) {
            Intent cancelIntent = new Intent();
            setResult(RESULT_CANCELED, cancelIntent);
            finish();
        }

        //shows toast when user must enter specific information
        else if (v.getId() == R.id.task_save) {
            Intent finalTaskIntent = new Intent();
            if (TextUtils.isEmpty(mTaskName.getText())) {
                Toast.makeText(this,
                        "Must Enter Task Name",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else if (pickedDays().isEmpty()) {
                Toast.makeText(this,
                        "Must Pick at least one day of week",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else if (mPickRangeTextView.toString().isEmpty()) {
                Toast.makeText(this,
                        "Must Pick Date Range",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                currentTask.setTaskName(mTaskName.getText().toString());
                currentTask.setDays(pickedDays());
                currentTask.setTimed(mFinishMethodSwitch.isChecked());
                currentTask.setTaskTime(mTaskTimeSpinner.getSelectedItemPosition());
                currentTask.setRestTime(mRestTimeSpinner.getSelectedItemPosition());
                currentTask.setTaskRepetition(mRepetitionSeekBar.getProgress());
                currentTask.setTaskType(taskTypeSetter(currentTask));

                Gson finalTaskGson = new Gson();
                String finalTaskStringJson = finalTaskGson.toJson(currentTask);

                if (isAddingNewTask) {
                    finalTaskIntent.putExtra(NEW_TASK_OBJECT, finalTaskStringJson);
                }
                else {
                    finalTaskIntent.putExtra(MODIFIED_TASK_OBJECT, finalTaskStringJson);
                }
                setResult(RESULT_OK, finalTaskIntent);
            }
            finish();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.check_timed_switch) {
            //shows additional UI needed for setting up timed tasks
            if (buttonView.isChecked()) {
                mTimeSessionLayout.setVisibility(View.VISIBLE);
                mSoundSettingLayout.setVisibility(View.VISIBLE);
                mRepetitionLayout.setVisibility(View.VISIBLE);
                mIsTimedTextView.setText("Timed");
                if (mRepetitionSeekBar.getProgress() == 0) {
                    mRestSessionLayout.setVisibility(View.GONE);
                    mRepetitionTextView.setText("No Repetition");
                }
                else {
                    mRestSessionLayout.setVisibility(View.VISIBLE);
                    mRepetitionTextView.setText(mRepetitionSeekBar.getProgress() + " Repetition");
                }

            }
            else {
                mTimeSessionLayout.setVisibility(View.GONE);
                mSoundSettingLayout.setVisibility(View.GONE);
                mRepetitionLayout.setVisibility(View.GONE);
                mRestSessionLayout.setVisibility(View.GONE);
                mIsTimedTextView.setText("Mark as Done");
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //shows additional UI needed for setting up repetitions for tasks and rest
        if (progress == 0) {
            mRestSessionLayout.setVisibility(View.GONE);
            mRepetitionTextView.setText("No Repetition");
        }
        else {
            mRestSessionLayout.setVisibility(View.VISIBLE);
            String reps = progress + " Repetition";
            mRepetitionTextView.setText(reps);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SOUND_PREF_SETTING_CODE && resultCode == RESULT_OK) {
            //get result from SoundSetting Activity
            assert data != null;

            Gson gson = new Gson();
            String newTaskJson = data.getStringExtra(SoundPrefActivity.SOUND_SETTING_OBJECT);
            currentTask = gson.fromJson(newTaskJson, Task.class);
        }
    }

    //MaterialDatePicker used when user adds new task
    //MaterialDateRangePicker used when user is modifying the existing task
    private void dateSetter() {
        MaterialDatePicker.Builder<Long> oneTimeBuilder = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker.Builder<Pair<Long, Long>> rangeBuilder = MaterialDatePicker.Builder.dateRangePicker();
        CalendarConstraints.Builder constraintsBuilderFromToday = new CalendarConstraints.Builder();
        CalendarConstraints.DateValidator dateValidatorMin;

        String startDateString = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(currentTask.getStartDate());
        String endDateString = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(currentTask.getEndDate());
        mModifyStartDateTextView.setText(startDateString);
        mModifyEndDateTextView.setText(endDateString);

        //detect if user is modifying task and setting DateValidator depending on results
        if (isAddingNewTask) dateValidatorMin = DateValidatorPointForward.from(
                Utility.unlocalizedLong(Utility.dateToTimestamp(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))));
        else dateValidatorMin = DateValidatorPointForward.from(
                Utility.unlocalizedLong(Utility.dateToTimestamp(currentTask.getStartDate())));

        //register DateValidators and set up the picker view
        ArrayList<CalendarConstraints.DateValidator> dateValidators = new ArrayList<>();
        dateValidators.add(dateValidatorMin);
        CalendarConstraints.DateValidator dateValidator = CompositeDateValidator.allOf(dateValidators);
        constraintsBuilderFromToday.setValidator(dateValidator);

        oneTimeBuilder.setCalendarConstraints(constraintsBuilderFromToday.build());
        rangeBuilder.setCalendarConstraints(constraintsBuilderFromToday.build());

        datePicker = oneTimeBuilder.build();
        rangePicker = rangeBuilder.build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {

                long localizedLong = Utility.localizedLong((Long)selection);
                String endDateString = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(localizedLong);

                currentTask.setEndDate(Utility.fromTimestamp(localizedLong));
                mModifyEndDateTextView.setText(endDateString);
            }
        });

        rangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {

                long localizedFirst = Utility.localizedLong(selection.first);
                long localizedSecond = Utility.localizedLong(selection.second);

                String startDateString = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(localizedFirst);
                String endDateString = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(localizedSecond);

                currentTask.setStartDate(Utility.fromTimestamp(localizedFirst));
                currentTask.setEndDate(Utility.fromTimestamp(localizedSecond));

                mAddStartDateTextView.setText(startDateString);
                mAddEndDateTextView.setText(endDateString);

                mDash.setVisibility(View.VISIBLE);
                mPickRangeTextView.setText("");
                mPickRangeTextView.setVisibility(View.GONE);
            }
        });
    }

    //get picked days depending on days buttons views selected
    //as buttons are customized to remain selected/unselected upon each user clicks
    private String pickedDays() {
        String pickedDays = "";
        if (mSundayButton.isSelected()) pickedDays += "0";
        if (mMondayButton.isSelected()) pickedDays += "1";
        if (mTuesdayButton.isSelected()) pickedDays += "2";
        if (mWednesdayButton.isSelected()) pickedDays += "3";
        if (mThursdayButton.isSelected()) pickedDays += "4";
        if (mFridayButton.isSelected()) pickedDays += "5";
        if (mSaturdayButton.isSelected()) pickedDays += "6";
        return pickedDays;
    }

    private void setDaysButton(String days) {
        for (char c : days.toCharArray()) {
            switch (c) {
                case '0':
                    mSundayButton.setSelected(true);
                    break;
                case '1':
                    mMondayButton.setSelected(true);
                    break;
                case '2':
                    mTuesdayButton.setSelected(true);
                    break;
                case '3':
                    mWednesdayButton.setSelected(true);
                    break;
                case '4':
                    mThursdayButton.setSelected(true);
                    break;
                case '5':
                    mFridayButton.setSelected(true);
                    break;
                case '6':
                    mSaturdayButton.setSelected(true);
                    break;
            }
        }
    }

    //task type defined to differ views between mark-as-done tasks and timed tasks
    private int taskTypeSetter(Task task) {
        //one day mark as done
        if ((currentTask.getStartDate().equals(currentTask.getEndDate())) &&
                !(mFinishMethodSwitch.isChecked())) {
            return 0;
        }
        //multi-days mark as done
        else if (!(currentTask.getStartDate().equals(currentTask.getEndDate())) &&
                !(mFinishMethodSwitch.isChecked())) {
            return 1;
        }
        //one day timed
        else if ((currentTask.getStartDate().equals(currentTask.getEndDate())) &&
                mFinishMethodSwitch.isChecked()) {
            return 2;
        }
        //multi-days continuous
        else if (!(currentTask.getStartDate().equals(currentTask.getEndDate())) &&
                mFinishMethodSwitch.isChecked()) {
            return 3;
        }
        else return -1;
    }
}