package com.jaej.demo.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jaej.demo.model.Record;
import com.jaej.demo.model.Task;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface TaskDao {

    ////////////////////////////////GET////////////////////////////////

    ////////////////////////////////UPDATE////////////////////////////////

    @Update
    void updateTask(Task task);

    @Query("UPDATE record SET rep_count = :repetitionIncrement " +
            "WHERE task_id_fk = :id AND today_date = :date")
    void updateRecord(int id, Date date, int repetitionIncrement);

    @Update
    void updateRecord(Record record);

    ////////////////////////////////INSERT////////////////////////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRecord(Record record);


    ////////////////////////////////DELETE////////////////////////////////
    @Query("DELETE FROM task WHERE taskID = :id")
    void deleteTask(int id);

    @Query("DELETE FROM record WHERE (today_date = :date) AND task_id_fk = \n" +
            "(SELECT task_id_fk FROM record\n" +
            "WHERE today_date = :date\n" +
            "EXCEPT\n" +
            "SELECT taskID\n" +
            "FROM task\n" +
            "WHERE (:date BETWEEN start_date AND end_date)\n" +
            "\tAND (days LIKE '%' || :day || '%'))")
    void deleteInvalidatedRecord(Date date, String day);

    @Query("DELETE FROM record WHERE task_id_fk = :id AND today_date = :date")
    void deleteRecord(int id, Date date);

    @Query("DELETE FROM record")
    void deleteAllRecord();





    @Query("UPDATE task SET score = (SELECT AVG(CAST(rep_count as REAL)/CAST(rep_max as REAL)) " +
            "FROM record WHERE task_id_fk = :id) WHERE taskID = :id")
    void setCurrentScore(int id);

    @Query("UPDATE task SET score_max = (SELECT COUNT(*) FROM record WHERE task_id_fk = task.taskID)")
    void setCurrentScoreMax();


    @Query("SELECT rep_count FROM record WHERE task_id_fk = :id")
    int getRepRecord(int id);

    ////////////////////////////////////////////////////////////////////////////////////


    @Query("SELECT AVG(CASE WHEN (CAST(rep_count as REAL) / rep_max) > 1.0 THEN 1.0 " +
            "ELSE (CAST(rep_count as REAL) / rep_max) END) AS avg_score FROM record " +
            "WHERE today_date BETWEEN :date1 AND :date2")
    Single<Double> getAverageScoreInRange(Date date1, Date date2);


    @Query("SELECT EXISTS(SELECT * FROM record WHERE today_date BETWEEN :date1 AND :date2)")
    Single<Boolean> doesExists(Date date1, Date date2);



    @Query("SELECT * FROM record")
    Single<List<Record>> getAllRecordOneShot();


    ////////////////////////////////////////////////////////////////////////////////////
    @Query("SELECT * FROM record WHERE today_date = :date AND task_id_fk = :id")
    LiveData<Record> getSingleRecord(int id, Date date);

    @Query("SELECT * FROM task WHERE (:date BETWEEN start_date AND end_date)" +
            "AND (days LIKE '%' || :day || '%')")
    LiveData<List<Task>> getTodayTasks(Date date, String day);
    //GET
    @Query("SELECT * FROM task ORDER BY end_date DESC")
    LiveData<List<Task>> getAllTasks();

//    @Query("SELECT * FROM record WHERE today_date = :date ORDER BY task_type")
//    LiveData<List<Record>> getTodayRecords(Date date);

    @Query("SELECT * FROM record INNER JOIN task ON task.taskID = record.task_id_fk " +
            "WHERE today_date = :date ORDER BY task.end_date DESC")
    LiveData<List<Record>> getTodayRecords(Date date);

    @Query("SELECT * FROM record ORDER BY task_type")
    LiveData<List<Record>> getAllRecords();



//    @Query("SELECT * FROM sound_pref_table WHERE settingID = :id")
//    LiveData<SoundPref> getSoundPref(int id);

//    @Query("SELECT * FROM task WHERE task_type = :typeID")
//    LiveData<List<Task>> getTaskByType(int typeID);
}
