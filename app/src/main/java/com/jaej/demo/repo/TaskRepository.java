package com.jaej.demo.repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.jaej.demo.data.TaskDao;
import com.jaej.demo.data.TaskRoomDatabase;
import com.jaej.demo.model.Record;
import com.jaej.demo.model.Task;
import com.jaej.demo.util.Constants;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.core.Single;

public class TaskRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;
    private final LiveData<List<Record>> todayRecords;
    private final LiveData<List<Record>> allRecords;
    private final LiveData<List<Task>> allTodayTasks;

    private final Single<List<Record>> allRecordOneShot;


    public TaskRepository(Application application) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
        todayRecords = taskDao.getTodayRecords(
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        allRecords = taskDao.getAllRecords();
        allTodayTasks = taskDao.getTodayTasks(
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Constants.TODAY_DAY_OF_WEEK);
        allRecordOneShot = taskDao.getAllRecordOneShot();

    }

    ////////////////////////////////GET////////////////////////////////
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Record>> getTodayRecords() {
        return todayRecords;
    }

    public LiveData<List<Task>> getAllTodayTasks() {
        return allTodayTasks;
    }

    public LiveData<List<Record>> getAllRecords() {
        return allRecords;
    }

    public LiveData<Record> getSingleRecord(int id) {
        return taskDao.getSingleRecord(id, Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public Single<List<Record>> getAllRecordOneShot() {
        return allRecordOneShot;
    }

    public void setCurrentScore(int id) {
        TaskRoomDatabase.databaseWriteExecutor.execute(()-> {
            taskDao.setCurrentScore(id);
        });
    }

    public void setCurrentScoreMax() {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.setCurrentScoreMax();
        });
    }



    ////////////////////////////////UPDATE////////////////////////////////
    public void updateTask(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.updateTask(task);
        });
    }


    public void updateRecord(int id, int repCount) {
        TaskRoomDatabase.databaseWriteExecutor.execute(()-> {
            taskDao.updateRecord(id, Constants.TODAY, repCount);
        });
    }

    public void updateRecord(Record record) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.updateRecord(record);
        });
    }

    ////////////////////////////////INSERT////////////////////////////////
    public void insertTask(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.insertTask(task);
        });
    }

    public void insertRecord(Record record) {
        TaskRoomDatabase.databaseWriteExecutor.execute(()-> {
            taskDao.insertRecord(record);
        });
    }

    ////////////////////////////////DELETE////////////////////////////////
    public void deleteTask(int taskID) {
        TaskRoomDatabase.databaseWriteExecutor.execute(()-> {
            taskDao.deleteTask(taskID);
        });
    }


    public void deleteAllRecord() {
        TaskRoomDatabase.databaseWriteExecutor.execute(()-> {
            taskDao.deleteAllRecord();
        });
    }

    public void deleteInvalidatedRecord(Date date, String day) {
        TaskRoomDatabase.databaseWriteExecutor.execute(()-> {
            taskDao.deleteInvalidatedRecord(date, day);
        });
    }


}
