package com.jaej.demo.ui.task;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jaej.demo.model.Task;
import com.jaej.demo.repo.TaskRepository;

import java.util.List;


public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository taskRepository;
    private final LiveData<List<Task>> allTasks;

    public TaskViewModel(Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        allTasks = taskRepository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }


    public int getTaskIDByPos(int pos) {
        return allTasks.getValue().get(pos).getTaskID();
    }

    public Task getTaskByPos(int position) {
        return allTasks.getValue().get(position);
    }

    public void insertTask(Task task) {
        taskRepository.insertTask(task);
    }

    public void deleteTask(int taskID) {
        taskRepository.deleteTask(taskID);
    }

    public void updateTask(Task task) {
        taskRepository.updateTask(task);
    }

    public void setCurrentScoreMax() {
        taskRepository.setCurrentScoreMax();
    }

}
