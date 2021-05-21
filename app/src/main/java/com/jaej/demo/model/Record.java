package com.jaej.demo.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.io.Serializable;
import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "record",
        foreignKeys = {@ForeignKey(onDelete = CASCADE,
                entity = Task.class,
                parentColumns = "taskID",
                childColumns = "task_id_fk")},
        primaryKeys = {"task_id_fk", "today_date"})
public class Record implements Serializable {

    @ColumnInfo(name = "task_id_fk")
    private int taskID;

    @ColumnInfo(name = "task_name")
    private String taskName;

    @NonNull
    @ColumnInfo(name = "today_date")
    private Date date;

    @ColumnInfo(name = "task_type")
    private int taskType;

    @ColumnInfo(name = "rep_count")
    private int repetitionCount;

    @ColumnInfo(name = "rep_max")
    private int repetitionMax;



    @Ignore
    public Record(int taskID, String taskName, @NonNull Date date, int taskType, int repetitionCount, int repetitionMax) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.date = date;
        this.taskType = taskType;
        this.repetitionCount = repetitionCount;
        this.repetitionMax = repetitionMax;
    }

    public Record(int taskID, String taskName, @NonNull Date date, int taskType, int repetitionMax) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.date = date;
        this.taskType = taskType;
        this.repetitionCount = 0;
        this.repetitionMax = repetitionMax;
    }

    @Ignore
    public Record() {
        this.repetitionCount = 0;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRepetitionCount() {
        return repetitionCount;
    }

    public void setRepetitionCount(int repetitionCount) {
        this.repetitionCount = repetitionCount;
    }

    public int getRepetitionMax() {
        return repetitionMax;
    }

    public void setRepetitionMax(int repetitionMax) {
        this.repetitionMax = repetitionMax;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }
}
