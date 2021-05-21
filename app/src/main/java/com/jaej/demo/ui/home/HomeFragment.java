package com.jaej.demo.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jaej.demo.R;
import com.jaej.demo.adapter.RecordRecyclerViewAdapter;
import com.jaej.demo.model.Record;
import com.jaej.demo.model.Task;
import com.jaej.demo.util.Constants;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel mHomeViewModel;
    private RecordRecyclerViewAdapter mRecordRecyclerViewAdapter;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        RecyclerView mRecordRecyclerView = view.findViewById(R.id.all_record_recyclerview);
        mRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecordRecyclerView.setHasFixedSize(true);
        mRecordRecyclerViewAdapter = new RecordRecyclerViewAdapter(this.getContext());
        mRecordRecyclerView.setAdapter(mRecordRecyclerViewAdapter);

        mRecordRecyclerViewAdapter.setOnItemClickListener(new RecordRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //if user clicks timed-task, direct to doTimedTaskFragment
                if (mHomeViewModel.getRecordByPos(position).getTaskType() == 2 ||
                        mHomeViewModel.getRecordByPos(position).getTaskType() == 3) {

                    Bundle args = new Bundle();
                    Gson gson = new Gson();
                    args.putString(
                            Constants.TASK_FROM_HOME,
                            gson.toJson(mHomeViewModel.getTaskById(
                                    mHomeViewModel.getRecordByPos(position).getTaskID())));
                    args.putString(
                            Constants.RECORD_FROM_HOME,
                            gson.toJson(mHomeViewModel.getRecordByPos(position)));
                    DoTimedTaskFragment timedTaskFragment = new DoTimedTaskFragment();
                    timedTaskFragment.setArguments(args);
                    Navigation.findNavController(view).navigate(R.id.doTimedTaskFragment, args);
                }
                //mark-as-done tasks, where user can just click and mark as done
                else if (mHomeViewModel.getRecordByPos(position).getTaskType() == 0 ||
                        mHomeViewModel.getRecordByPos(position).getTaskType() == 1) {
                    Record currentRecord = mHomeViewModel.getRecordByPos(position);
                    if (mHomeViewModel.getRecordByPos(position).getRepetitionCount() == 0) {
                        //if the task is not checked(0), increment repetition count
                        mHomeViewModel.updateRecord(currentRecord.getTaskID(),
                                currentRecord.getRepetitionCount() + 1);
                    }
                    else {
                        //if user want to uncheck, decrement repetition count
                        mHomeViewModel.updateRecord(currentRecord.getTaskID(),
                                currentRecord.getRepetitionCount() - 1);
                    }
                    //update score on task table
                    mHomeViewModel.setCurrentScore(currentRecord.getTaskID());
                }
            }
        });

        mHomeViewModel.getAllTodayTasks().observe(this.getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                //set today tasks
                mRecordRecyclerViewAdapter.setTodayTasks(tasks);
                //and delete invalid records in case when user modified task days, and today is not
                //one of the days.
                mHomeViewModel.deleteInvalidRecord();
                tasks.forEach(task -> {
                    Record record = new Record(
                            task.getTaskID(),
                            task.getTaskName(),
                            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            task.getTaskType(),
                            task.getTaskRepetition()+1);

                    //insertRecord is set to ignore conflict, so the ones being newly add will be added
                    mHomeViewModel.insertRecord(record);
                });
            }
        });

        mHomeViewModel.getTodayRecords().observe(this.getViewLifecycleOwner(), new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                mRecordRecyclerViewAdapter.setTodayRecords(records);
            }
        });
    }

}

