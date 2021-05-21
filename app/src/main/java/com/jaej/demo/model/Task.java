package com.jaej.demo.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Entity(tableName = "task")
public class Task {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int taskID;

    @ColumnInfo(name = "task_name")
    private String taskName;

    @ColumnInfo(name = "days")
    private String days;

    @ColumnInfo(name = "start_date")
    private Date startDate;

    @ColumnInfo(name = "end_date")
    private Date endDate;

    @ColumnInfo(name = "timed")
    private boolean timed;

    @ColumnInfo(name = "task_time")
    private int taskTime;

    @ColumnInfo(name = "rest_time")
    private int restTime;

    @ColumnInfo(name = "task_repetition")
    private int taskRepetition;

    @ColumnInfo(name = "score")
    private double score;

    @ColumnInfo(name = "score_max")
    private double scoreMax;

    @ColumnInfo(name = "task_type")
    private int taskType;

    @ColumnInfo(name = "ringtone_index")
    private int ringtoneIndex;

    @ColumnInfo(name = "alarm_volume")
    private int alarmVolume;

    @ColumnInfo(name = "alarm_repetition")
    private int alarmRepetition;

    @ColumnInfo(name = "play_as_media")
    private boolean isPlayAsMedia;

    @Ignore
    public Task() {
        this.taskName = "";
        this.days = "";
        this.startDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.endDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.timed = false;
        this.taskTime = 3;
        this.restTime = 0;
        this.taskRepetition = 0;
        this.score = 0;
        this.scoreMax = 0;
        this.taskType = -1;
        this.ringtoneIndex = 0;
        this.alarmVolume = 50;
        this.alarmRepetition = 0;
        this.isPlayAsMedia = false;
    }

    public Task(int taskID, String taskName, String days, Date startDate, Date endDate,
                boolean timed, int taskTime, int restTime, int taskRepetition, double score,
                double scoreMax, int taskType, int ringtoneIndex, int alarmVolume,
                int alarmRepetition, boolean isPlayAsMedia) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.days = days;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timed = timed;
        this.taskTime = taskTime;
        this.restTime = restTime;
        this.taskRepetition = taskRepetition;
        this.score = score;
        this.scoreMax = scoreMax;
        this.taskType = taskType;
        this.ringtoneIndex = ringtoneIndex;
        this.alarmVolume = alarmVolume;
        this.alarmRepetition = alarmRepetition;
        this.isPlayAsMedia = isPlayAsMedia;
    }

    @Ignore
    public Task(String taskName, String days, Date startDate, Date endDate, boolean timed,
                int taskTime, int restTime, int taskRepetition, double score,
                double scoreMax, int taskType, int ringtoneIndex, int alarmVolume,
                int alarmRepetition, boolean isPlayAsMedia) {
        this.taskName = taskName;
        this.days = days;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timed = timed;
        this.taskTime = taskTime;
        this.restTime = restTime;
        this.taskRepetition = taskRepetition;
        this.score = score;
        this.scoreMax = scoreMax;
        this.taskType = taskType;
        this.ringtoneIndex = ringtoneIndex;
        this.alarmVolume = alarmVolume;
        this.alarmRepetition = alarmRepetition;
        this.isPlayAsMedia = isPlayAsMedia;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    public int getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(int taskTime) {
        this.taskTime = taskTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getTaskRepetition() {
        return taskRepetition;
    }

    public void setTaskRepetition(int taskRepetition) {
        this.taskRepetition = taskRepetition;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScoreMax() {
        return scoreMax;
    }

    public void setScoreMax(double scoreMax) {
        this.scoreMax = scoreMax;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getRingtoneIndex() {
        return ringtoneIndex;
    }

    public void setRingtoneIndex(int ringtoneIndex) {
        this.ringtoneIndex = ringtoneIndex;
    }

    public int getAlarmVolume() {
        return alarmVolume;
    }

    public void setAlarmVolume(int alarmVolume) {
        this.alarmVolume = alarmVolume;
    }

    public int getAlarmRepetition() {
        return alarmRepetition;
    }

    public void setAlarmRepetition(int alarmRepetition) {
        this.alarmRepetition = alarmRepetition;
    }

    public boolean isPlayAsMedia() {
        return isPlayAsMedia;
    }

    public void setPlayAsMedia(boolean playAsMedia) {
        isPlayAsMedia = playAsMedia;
    }
}