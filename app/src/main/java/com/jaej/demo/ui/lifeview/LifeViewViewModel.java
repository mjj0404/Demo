package com.jaej.demo.ui.lifeview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jaej.demo.model.Record;
import com.jaej.demo.repo.TaskRepository;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class LifeViewViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;
    private final Single<List<Record>> allRecordsOneShot;

    public LifeViewViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        allRecordsOneShot = taskRepository.getAllRecordOneShot();
    }

    public Single<List<Record>> getAllRecordOneShot() {
        return allRecordsOneShot;
    }


}