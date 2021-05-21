package com.jaej.demo.ui.task;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jaej.demo.AddModifyActivity;
import com.jaej.demo.R;
import com.jaej.demo.adapter.TaskRecyclerViewAdapter;
import com.jaej.demo.model.Task;

import java.util.List;

public class TaskFragment extends Fragment {

    public static final String TASK_TO_BE_MODIFIED = "TASK_TO_BE_MODIFIED";
    public static final int MODIFYING_TASK_REQUEST_CODE = 2;

    private TaskViewModel mTaskViewModel;
    private RecyclerView mTaskRecyclerView;
    private TaskRecyclerViewAdapter mTaskRecyclerViewAdapter;

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        mTaskRecyclerView = view.findViewById(R.id.all_task_recyclerview);
        mTaskRecyclerView.setHasFixedSize(true);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTaskRecyclerViewAdapter = new TaskRecyclerViewAdapter(this.getContext());
        mTaskRecyclerView.setAdapter(mTaskRecyclerViewAdapter);

        mTaskRecyclerViewAdapter.setOnItemClickListener(new TaskRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {}

            @Override
            public void onDeleteClick(int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to delete? All of the task's history will" +
                        "be deleted.");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTaskViewModel.deleteTask(mTaskViewModel.getTaskIDByPos(position));
                        mTaskRecyclerViewAdapter.notifyItemRemoved(position);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                if (!dialog.isShowing())
                    dialog.show();
            }

            //upon onEditClick, opens AddModifyActivity with current task data
            @Override
            public void onEditClick(int position) {
                Intent intent = new Intent(getActivity(), AddModifyActivity.class);
                Task taskToBeModified = mTaskViewModel.getTaskByPos(position);
                Gson gson = new Gson();
                String taskToBeModifiedJson = gson.toJson(taskToBeModified);
                intent.putExtra(TASK_TO_BE_MODIFIED, taskToBeModifiedJson);
                startActivityForResult(intent, MODIFYING_TASK_REQUEST_CODE);
            }
        });
        //calculate and set scoreMax for the task table
        mTaskViewModel.setCurrentScoreMax();
        mTaskViewModel.getAllTasks().observe(this.getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                mTaskRecyclerViewAdapter.setTasks(tasks);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MODIFYING_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            assert data != null;

            Gson gson = new Gson();
            String modifiedTaskStringJson = data.getStringExtra(AddModifyActivity.MODIFIED_TASK_OBJECT);
            Task modifiedTask = gson.fromJson(modifiedTaskStringJson, Task.class);
            mTaskViewModel.updateTask(modifiedTask);
            mTaskRecyclerView.setAdapter(mTaskRecyclerViewAdapter);


        }
    }
}