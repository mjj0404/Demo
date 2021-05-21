package com.jaej.demo.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jaej.demo.model.Record;
import com.jaej.demo.model.Task;
import com.jaej.demo.repo.TaskRepository;
import com.jaej.demo.util.Constants;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.core.Single;

public class HomeViewModel extends AndroidViewModel {
    private final TaskRepository taskRepository;
    private final LiveData<List<Record>> todayRecords;
    private final LiveData<List<Task>> allTodayTasks;
    private final LiveData<List<Record>> allRecords;

    public HomeViewModel(Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        todayRecords = taskRepository.getTodayRecords();
        allTodayTasks = taskRepository.getAllTodayTasks();
        allRecords = taskRepository.getAllRecords();
    }

    public LiveData<List<Record>> getTodayRecords() {
        return todayRecords;
    }


    public LiveData<List<Task>> getAllTodayTasks() {
        return allTodayTasks;
    }

    public Record getRecordByPos(int position) {
        return todayRecords.getValue().get(position);
    }

    public Task getTaskById(int id) {
        AtomicReference<Task> newTask = new AtomicReference<>(new Task());
        allTodayTasks.getValue().forEach(task -> {
            if (task.getTaskID() == id)
                newTask.set(task);
        });
        return newTask.get();
    }

    public void insertRecord(Record record) {
        taskRepository.insertRecord(record);
    }

    public void setCurrentScore(int id) {
        taskRepository.setCurrentScore(id);
    }

    public void updateRecord(int id, int repCount) {
        taskRepository.updateRecord(id, repCount);
    }

    public void updateRecord(Record record) {
        taskRepository.updateRecord(record);
    }

    public void deleteAllRecord() {
        taskRepository.deleteAllRecord();
    }

    public void deleteInvalidRecord() {
        taskRepository.deleteInvalidatedRecord(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Constants.TODAY_DAY_OF_WEEK);
    }

    public LiveData<Record> getSingleRecord(int id) {
        return taskRepository.getSingleRecord(id);
    }
}
