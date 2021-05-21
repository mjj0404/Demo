package com.jaej.demo.ui.lifeview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jaej.demo.R;
import com.jaej.demo.model.Record;
import com.jaej.demo.util.Constants;
import com.jaej.demo.util.Utility;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

public class LifeViewFragment extends Fragment implements View.OnClickListener {

    private TextView mLifeTextView;
    private TextView mPartitionTextView;
    private ConstraintLayout mConstraintLayout;
    private LinearLayout mPartitionLayout;
    private SharedPreferences mSharedPreferences;
    private SpannableStringBuilder builder;

    private LocalDate startRangeLocalDate, endRangeLocalDate, localDateCounter = LocalDate.now();
    private int partitionSelectionInt;
    private final TemporalAdjuster firstDayOfWeekAdjuster = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
    private final TemporalAdjuster lastDayOfWeekAdjuster = TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY);
    private final ArrayList<Record> allRecords = new ArrayList<>();
    private Disposable disposable;

    public static LifeViewFragment newInstance() {
        return new LifeViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_life_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifeViewViewModel mLifeViewModel = new ViewModelProvider(this).get(LifeViewViewModel.class);
        mLifeTextView = view.findViewById(R.id.life_text_view);
        TextView mBirthdayTextView = view.findViewById(R.id.birthday_view_text_view);
        TextView mViewStartTextView = view.findViewById(R.id.view_from_text_view);
        TextView mViewEndTextView = view.findViewById(R.id.view_to_text_view);
        TextView mViewGroupByTextView = view.findViewById(R.id.view_group_by_text_view);
        mPartitionTextView = view.findViewById(R.id.partition_view_text_view);
        CardView mCardView = view.findViewById(R.id.life_view_card_view);
        mPartitionLayout = view.findViewById(R.id.partition_layout);
        mConstraintLayout = view.findViewById(R.id.card_view_constraint_layout);
        Button mViewButton = view.findViewById(R.id.life_view_button);

        mViewButton.setOnClickListener(this);

        builder = new SpannableStringBuilder();

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        long birthdayLong = mSharedPreferences.getLong(Constants.USER_BIRTHDAY, 0);
        int startRangeInt = mSharedPreferences.getInt(Constants.USER_VIEW_RANGE_START, 0);
        int endRangeInt = mSharedPreferences.getInt(Constants.USER_VIEW_RANGE_END, 0);
        int viewGroupByInt = mSharedPreferences.getInt(Constants.USER_VIEW_GROUP_BY, 0);
        partitionSelectionInt = mSharedPreferences.getInt(Constants.USER_PARTITION_SELECTION, 0);


        endRangeLocalDate = endRangeGetter(endRangeInt);

        mViewStartTextView.setText(getResources().getStringArray(R.array.start_range)[startRangeInt]);
        mViewEndTextView.setText(getResources().getStringArray(R.array.end_range)[endRangeInt]);
        mViewGroupByTextView.append(getResources().getStringArray(R.array.group_by)[viewGroupByInt]);
        //mPartitionTextView.setText(getResources().getStringArray(R.array.partition)[partitionInt]);


        if (birthdayLong == 0) {
            mCardView.setAlpha(0.5f);
            mViewButton.setClickable(false);
            mBirthdayTextView.setText("N/A");

            Toast.makeText(getContext(), "Must Configure Birthday / Preference to use." +
                    " Go to Setting Tab on Upper Left Corner", Toast.LENGTH_LONG).show();
        }
        else {
            String birthdayString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(birthdayLong));
            mBirthdayTextView.setText(birthdayString);
        }

        //Observing RxJava3 Single object and getting data one-time
        disposable = mLifeViewModel.getAllRecordOneShot()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Consumer<List<Record>>) allRecords::addAll);
        partitionViewSetter(partitionSelectionInt);
    }


    @Override
    public void onClick(View v) {

        //Outer Loop - loop through from the start range to the end range
        //Inner Loop - lifeViewSetter() that calculates the average score of the input range
        //Note that Inner Loop will be called only when the input range is active days
        //i.e. start range - 1/1/1980, end range - 1/1/2021, active days range - 1/1/2020 through 12/31/2020
        //Outer Loop - 1/1/1980 through 12/31/2019
        //Inner Loop - 1/1/2020 through 12/31/2020
        if (v.getId() == R.id.life_view_button) {
            int groupByInt = mSharedPreferences.getInt(Constants.USER_VIEW_GROUP_BY, 0);
            Date firstRecordDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
            allRecords.forEach(record -> {
                if (record.getDate().before(firstRecordDate)) {
                    firstRecordDate.setTime(record.getDate().getTime());
                }
            });
            LocalDate firstRecordLocalDate = firstRecordDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate lastRecordLocalDate = LocalDate.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()));

            Date startRangeDate = Date.from(firstRecordLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putLong(Constants.FIRST_DAY, startRangeDate.getTime());
            editor.apply();
            startRangeLocalDate = startRangeGetter(mSharedPreferences.getInt(Constants.USER_VIEW_RANGE_START, 0));

            if (groupByInt == 0) {

                lastRecordLocalDate = lastRecordLocalDate.plusDays(1);

                for (LocalDate date = startRangeLocalDate;
                     date.isBefore(endRangeLocalDate) || date.isEqual(endRangeLocalDate);
                     date = date.plusDays(1)) {

                    if (date.isBefore(firstRecordLocalDate)) {
                        lifeViewSetter(date, groupByInt, false, true);
                    }
                    else lifeViewSetter(date, groupByInt,
                        !date.isAfter(lastRecordLocalDate) &&
                                !date.isEqual(lastRecordLocalDate),
                        false);
                }
            }
            //group by Weeks
            else if (groupByInt == 1) {

                firstRecordLocalDate = LocalDate.from(firstRecordLocalDate.with(firstDayOfWeekAdjuster).atStartOfDay(ZoneId.systemDefault()));
                lastRecordLocalDate = LocalDate.from(lastRecordLocalDate.with(firstDayOfWeekAdjuster).atStartOfDay(ZoneId.systemDefault()));
                lastRecordLocalDate = lastRecordLocalDate.plusWeeks(1);


                for (LocalDate date = startRangeLocalDate.with(firstDayOfWeekAdjuster);
                     date.isBefore(endRangeLocalDate) || date.isEqual(endRangeLocalDate);
                     date = date.plusWeeks(1)) {
                    localDateCounter = date.plusWeeks(1);
                    if (date.isBefore(firstRecordLocalDate))
                        lifeViewSetter(date, groupByInt, false, true);
                    else lifeViewSetter(date, groupByInt,
                        !date.isAfter(lastRecordLocalDate) &&
                                !date.isEqual(lastRecordLocalDate),
                        false);
                }
            }
            //group by Month
            else if (groupByInt == 2) {

                firstRecordLocalDate = LocalDate.from(firstRecordLocalDate.with(firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()));
                lastRecordLocalDate = LocalDate.from(lastRecordLocalDate.with(firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()));
                lastRecordLocalDate = lastRecordLocalDate.plusMonths(1);

                for (LocalDate date = startRangeLocalDate.with(firstDayOfMonth());
                     date.isBefore(endRangeLocalDate) || date.isEqual(endRangeLocalDate);
                     date = date.plusMonths(1)) {
                    localDateCounter = date.plusMonths(1);
                    if (date.isBefore(firstRecordLocalDate))
                        lifeViewSetter(date, groupByInt, false, true);
                    else lifeViewSetter(date, groupByInt,
                        !date.isAfter(lastRecordLocalDate) &&
                                !date.isEqual(lastRecordLocalDate),
                        false);
                }
            }
            mConstraintLayout.setVisibility(View.GONE);
            mLifeTextView.setVisibility(View.VISIBLE);
            mLifeTextView.setText(builder, TextView.BufferType.SPANNABLE);
            mLifeTextView.setMovementMethod(new ScrollingMovementMethod());
        }
    }


    //group by
    //0 - day
    //1 - week
    //2 - month

    //partition
    //0 - no partition
    //1 - week
    //2 - month
    //3 - year
    private void lifeViewSetter(LocalDate searchStartLocalDate, int groupBy, boolean isActiveDays, boolean isPast) {
        int partitionChar = mSharedPreferences.getInt(Constants.USER_PARTITION_CHAR, 0);
        if (!isActiveDays && isPast) {
            SpannableString spannableString = new SpannableString("\u25A0 ");
            spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.gray3, null)),
                    0,
                    spannableString.length(),
                    0);
            builder.append(spannableString);
        }
        else if (!isActiveDays) {
            SpannableString spannableString = new SpannableString("\u25A0 ");
            spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.gray4, null)),
                    0,
                    spannableString.length(),
                    0);
            builder.append(spannableString);
        }
        else {
            AtomicInteger dividend = new AtomicInteger();
            AtomicInteger divisor = new AtomicInteger();
            // dividend / divisor
            allRecords.forEach(record -> {
                LocalDate recordLocalDate = record.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (groupBy == 0) {
                    if (recordLocalDate.isEqual(searchStartLocalDate)) {
                        //day
                        dividend.set(dividend.get() + record.getRepetitionCount());
                        divisor.set(divisor.get() + record.getRepetitionMax());
                    }
                }
                else if (groupBy == 1) {
                    if (recordLocalDate.with(firstDayOfWeekAdjuster).isEqual(searchStartLocalDate)) {
                        //week
                        dividend.set(dividend.get() + record.getRepetitionCount());
                        divisor.set(divisor.get() + record.getRepetitionMax());
                    }

                }
                else if (groupBy == 2) {
                    if (recordLocalDate.with(firstDayOfMonth()).isEqual(searchStartLocalDate)) {
                        //month
                        dividend.set(dividend.get() + record.getRepetitionCount());
                        divisor.set(divisor.get() + record.getRepetitionMax());
                    }
                }
            });
            if (!(divisor.equals(0))) {
                double finalScore = dividend.doubleValue() / divisor.doubleValue();

                SpannableString spannableString = new SpannableString("\u25A0 ");
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(Utility.doubleToColorString(finalScore))),
                        0,
                        spannableString.length(),
                        0);
                builder.append(spannableString);
            }
        }
        //week
        if (partitionSelectionInt == 1 && searchStartLocalDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            builder.append(appendPartitionChar(partitionChar));
        }

        else if (partitionSelectionInt == 2) {
            //split every month
            if (groupBy == 0 && (searchStartLocalDate.plusDays(1).getMonth() != searchStartLocalDate.getMonth())) {
                builder.append(appendPartitionChar(partitionChar));
            }
            else if (groupBy == 1 &&
                        (searchStartLocalDate.getMonth() != localDateCounter.getMonth())) {
                builder.append(appendPartitionChar(partitionChar));
            }
        }

        else if (partitionSelectionInt == 3) {
            //split every year
            if (groupBy == 0 && searchStartLocalDate.with(lastDayOfYear()).isEqual(startRangeLocalDate))
                builder.append(appendPartitionChar(partitionChar));
            else if (groupBy == 1 &&
                    (searchStartLocalDate.getYear() != localDateCounter.getYear())) {
                //grouped by month
                builder.append(appendPartitionChar(partitionChar));
            }
            else if (groupBy == 2 &&
                    (searchStartLocalDate.getYear() != localDateCounter.getYear())) {
                //grouped by year
                builder.append(appendPartitionChar(partitionChar));
            }

        }
    }


    private SpannableString appendPartitionChar(int partitionChar) {
        SpannableString spannableString;
        if (partitionChar == 0) {
            spannableString = new SpannableString("\u25A0");
            spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(),
                    R.color.light_background, null)),
                    0,
                    spannableString.length(),
                    0);
        }
        else {
            spannableString = new SpannableString("\n");
        }
        return spannableString;
    }



    private LocalDate startRangeGetter(int selection) {
        LocalDate localDate = LocalDate.now();
        long birthday = mSharedPreferences.getLong(Constants.USER_BIRTHDAY, 0);
        switch(selection) {
            case 0:
                //birthday
                localDate = Instant.ofEpochMilli(birthday).atZone(ZoneId.systemDefault()).toLocalDate();
                break;
            case 1:
                //past 1 year
                localDate = LocalDate.from(localDate.minusYears(1).atStartOfDay().atZone(ZoneId.systemDefault()));
                break;
            case 2:
                //this year start
                localDate = LocalDate.from(localDate.with(firstDayOfYear()).atStartOfDay());
                break;
            case 3:
                //entire app history
                mSharedPreferences.getLong(Constants.FIRST_DAY, 0);
                localDate = Instant.
                        ofEpochMilli(mSharedPreferences.getLong(Constants.FIRST_DAY, 0))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                break;
        }
        return localDate;
    }
    private LocalDate endRangeGetter(int selection) {
        LocalDate localDate = LocalDate.now();
        long birthday = mSharedPreferences.getLong(Constants.USER_BIRTHDAY, 0);

        switch(selection) {
            case 0:
                //today
                localDate = LocalDate.from(localDate.atStartOfDay());
                break;
            case 1:
                //this year end
                localDate = LocalDate.from(localDate.with(lastDayOfYear()).atStartOfDay());
                break;
            case 2:
                //age 80
                localDate = Instant.ofEpochMilli(birthday).atZone(ZoneId.systemDefault()).toLocalDate();
                localDate = LocalDate.from(localDate.plusYears(80).atStartOfDay());
                break;
            case 3:
                //age 100
                localDate = Instant.ofEpochMilli(birthday).atZone(ZoneId.systemDefault()).toLocalDate();
                localDate = LocalDate.from(localDate.plusYears(100).atStartOfDay());
                break;
        }
        return localDate;
    }

    private void partitionViewSetter(int partitionNumber) {
        switch(partitionNumber) {
            case 0:
                //no part
                mPartitionLayout.setVisibility(View.GONE);
                break;
            case 1:
                mPartitionTextView.setText("Week");
                //week
                break;
            case 2:
                mPartitionTextView.setText("Month");
                break;
            case 3:
                mPartitionTextView.setText("Year");
                break;
        }
    }

    @Override
    public void onStop() {
        disposable.dispose();
        super.onStop();
    }

}