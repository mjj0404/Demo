package com.jaej.demo.ui.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.jaej.demo.R;
import com.jaej.demo.util.Constants;
import com.jaej.demo.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private FragmentActivity fragmentActivity;
    private TextView mBirthdayTextView;
    private RadioGroup mPartitionByRadioGroup, mPartitionCharRadioGroup;
    private RadioButton mNoPartitionButton, mWeekButton, mMonthButton, mYearButton, mBlockButton,
            mBreakLineButton;
    private Spinner mStartSpinner, mEndSpinner, mGroupBySpinner;
    private SharedPreferences mSharedPreferences;

    private MaterialDatePicker<Long> datePicker;



    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setting, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mBirthdayTextView = view.findViewById(R.id.birthday_text_view);

        ImageButton mDatePickerButton = view.findViewById(R.id.birthday_picker);
        mStartSpinner = view.findViewById(R.id.start_range_spinner);
        mEndSpinner = view.findViewById(R.id.end_range_spinner);
        mGroupBySpinner = view.findViewById(R.id.view_group_by_spinner);
        mPartitionByRadioGroup = view.findViewById(R.id.partition_radio_group);
        mPartitionCharRadioGroup = view.findViewById(R.id.split_with_radio_group);
        mNoPartitionButton = view.findViewById(R.id.no_partition_radio_button);
        mWeekButton = view.findViewById(R.id.week_partition_radio_button);
        mMonthButton = view.findViewById(R.id.month_partition_radio_button);
        mYearButton = view.findViewById(R.id.year_partition_radio_button);
        mBlockButton = view.findViewById(R.id.split_with_block_radiobutton);
        mBreakLineButton = view.findViewById(R.id.split_with_line_break_radiobutton);



        ArrayAdapter<CharSequence> startRangeAdapter = ArrayAdapter.createFromResource(
                this.getContext(), R.array.start_range, R.layout.custom_spinner);
        startRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStartSpinner.setAdapter(startRangeAdapter);

        ArrayAdapter<CharSequence> endRangeAdapter = ArrayAdapter.createFromResource(
                this.getContext(), R.array.end_range, R.layout.custom_spinner);
        endRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEndSpinner.setAdapter(endRangeAdapter);

        ArrayAdapter<CharSequence> groupByAdapter = ArrayAdapter.createFromResource(
                this.getContext(), R.array.group_by, R.layout.custom_spinner);
        groupByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroupBySpinner.setAdapter(groupByAdapter);


        mDatePickerButton.setOnClickListener(this);
        dateSetter();

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        long birthday = mSharedPreferences.getLong(Constants.USER_BIRTHDAY, 0);
        int startRange = mSharedPreferences.getInt(Constants.USER_VIEW_RANGE_START, 0);
        int endRange = mSharedPreferences.getInt(Constants.USER_VIEW_RANGE_END, 0);
        int viewGroupBy = mSharedPreferences.getInt(Constants.USER_VIEW_GROUP_BY, 0);
        int partitionSelection = mSharedPreferences.getInt(Constants.USER_PARTITION_SELECTION, 0);
        int partitionChar = mSharedPreferences.getInt(Constants.USER_PARTITION_CHAR, 0);

        if (birthday == 0) {
            mBirthdayTextView.setText("Enter Your Birthday");
        }
        else {
            String dateString = new SimpleDateFormat("MM/dd/yyyy").format(birthday);
            mBirthdayTextView.setText(dateString);
        }
        mStartSpinner.setSelection(startRange);
        mEndSpinner.setSelection(endRange);
        mGroupBySpinner.setSelection(viewGroupBy);

        mGroupBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setAvailableRadioButton(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        partitionSelectionSetter(partitionSelection);
        partitionCharSetter(partitionChar);

        if (mNoPartitionButton.isChecked()) {
            mPartitionCharRadioGroup.setAlpha(0.3f);
            mBlockButton.setEnabled(false);
            mBreakLineButton.setEnabled(false);
        }

        mPartitionByRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.no_partition_radio_button) {
                    mPartitionCharRadioGroup.setAlpha(0.3f);
                    mBlockButton.setEnabled(false);
                    mBreakLineButton.setEnabled(false);
                }
                else {
                    mPartitionCharRadioGroup.setAlpha(1.0f);
                    mBlockButton.setEnabled(true);
                    mBreakLineButton.setEnabled(true);
                }
            }
        });

    }

    private void partitionSelectionSetter(int number) {
        switch (number) {
            case 0:
                mNoPartitionButton.setChecked(true);
                break;
            case 1:
                mWeekButton.setChecked(true);
                break;
            case 2:
                mMonthButton.setChecked(true);
                break;
            case 3:
                mYearButton.setChecked(true);
                break;
        }
    }

    private int partitionSelectionGetter() {
        if (mWeekButton.isChecked()) return 1;
        else if (mMonthButton.isChecked()) return 2;
        else if (mYearButton.isChecked()) return 3;
        else return 0;
    }

    private void partitionCharSetter (int number) {
        if (number == 0) mBlockButton.setChecked(true);
        else if (number == 1) mBreakLineButton.setChecked(true);
    }

    private int partitionCharGetter() {
        if (mBlockButton.isChecked()) return 0;
        else if (mBreakLineButton.isChecked()) return 1;
        else return 0;
    }

    private void setAvailableRadioButton(int position) {

        switch(position) {
            case 0:
                enableRadioButton(mWeekButton);
                enableRadioButton(mMonthButton);
                enableRadioButton(mYearButton);
                break;
            case 1:
                disableRadioButton(mWeekButton);
                enableRadioButton(mMonthButton);
                enableRadioButton(mYearButton);
                break;
            case 2:
                disableRadioButton(mWeekButton);
                disableRadioButton(mMonthButton);
                enableRadioButton(mYearButton);
                break;
        }
    }

    private void disableRadioButton(RadioButton button) {
        if (button.isChecked()) {
            button.setChecked(false);
            mNoPartitionButton.setChecked(true);
        }
        button.setAlpha(0.3f);
        button.setClickable(false);
    }

    private void enableRadioButton(RadioButton button) {
        button.setAlpha(1.0f);
        button.setClickable(true);
    }

    private void dateSetter() {

        MaterialDatePicker.Builder<Long> oneTimeBuilder = MaterialDatePicker.Builder.datePicker();

        CalendarConstraints.Builder constraintsBuilderFromToday = new CalendarConstraints.Builder();
        CalendarConstraints.DateValidator dateValidatorMax;
        dateValidatorMax = DateValidatorPointBackward.now();


        ArrayList<CalendarConstraints.DateValidator> dateValidators = new ArrayList<>();
        dateValidators.add(dateValidatorMax);
        CalendarConstraints.DateValidator dateValidator = CompositeDateValidator.allOf(dateValidators);
        constraintsBuilderFromToday.setValidator(dateValidator);

        oneTimeBuilder.setCalendarConstraints(constraintsBuilderFromToday.build());

        datePicker = oneTimeBuilder.build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                long localizedBirthday = Utility.localizedLong((Long)selection);

                String birthdayString = new SimpleDateFormat(
                        "MM/dd/yyyy",
                        Locale.getDefault()).format(localizedBirthday);

                mBirthdayTextView.setText(birthdayString);

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putLong(Constants.USER_BIRTHDAY, localizedBirthday);
                editor.apply();

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.birthday_picker) {
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            datePicker.show(fragmentManager, datePicker.toString());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentActivity = (FragmentActivity) context;
        super.onAttach(context);
    }



    @Override
    public void onDestroy() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.USER_VIEW_RANGE_START, mStartSpinner.getSelectedItemPosition());
        editor.putInt(Constants.USER_VIEW_RANGE_END, mEndSpinner.getSelectedItemPosition());
        editor.putInt(Constants.USER_VIEW_GROUP_BY, mGroupBySpinner.getSelectedItemPosition());
        editor.putInt(Constants.USER_PARTITION_SELECTION, partitionSelectionGetter());
        editor.putInt(Constants.USER_PARTITION_CHAR, partitionCharGetter());
        editor.apply();

        super.onDestroy();
    }
}
