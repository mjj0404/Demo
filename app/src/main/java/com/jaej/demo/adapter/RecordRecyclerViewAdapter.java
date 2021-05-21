package com.jaej.demo.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaej.demo.R;
import com.jaej.demo.model.Record;
import com.jaej.demo.model.Task;
import com.jaej.demo.util.Utility;

import java.util.List;

public class RecordRecyclerViewAdapter extends RecyclerView.Adapter<RecordRecyclerViewAdapter.RecordViewHolder> {

    private Context context;
    private final LayoutInflater recordInflater;
    private List<Record> todayRecordList;
    private List<Task> todayTaskList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public class RecordViewHolder extends RecyclerView.ViewHolder {


        public final TextView mTaskNameTextView, mTaskStatusTextView;
        public final CheckBox mCheckBox;
        public final ImageView mImageView;
        public final ProgressBar mTaskProgressBar;

        public RecordViewHolder(@NonNull View itemView, Context ctx, final OnItemClickListener listener) {
            super(itemView);
            context = ctx;
            mTaskNameTextView = itemView.findViewById(R.id.text_view);
            mTaskStatusTextView = itemView.findViewById(R.id.status_text_view);
            mCheckBox = itemView.findViewById(R.id.is_checked_check_box);
            mImageView = itemView.findViewById(R.id.is_timed_image_view);
            mTaskProgressBar = itemView.findViewById(R.id.task_progressBar);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //recyclerview item listener
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            mCheckBox.setChecked(!mCheckBox.isChecked());
                        }
                    }
                }
            });
        }
    }


    public RecordRecyclerViewAdapter(Context context) {
        this.context = context;
        recordInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = recordInflater.inflate(R.layout.today_task_item, parent, false);
        return new RecordViewHolder(view, context, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {

        holder.mCheckBox.setVisibility(View.GONE);
        holder.mImageView.setVisibility(View.GONE);

        if (todayRecordList != null) {
            final Record currentRecord = todayRecordList.get(position);
            holder.mTaskNameTextView.setText(String.valueOf(currentRecord.getTaskName()));

            int progressInt;
            String progress;

            progressInt = currentRecord.getRepetitionCount();
            progress = String.valueOf(progressInt);

            //adding color for the progressbar for timed task
            double progressDouble = (double) progressInt / currentRecord.getRepetitionMax();
            holder.mTaskProgressBar.setProgressTintList(ColorStateList.valueOf(
                    (Color.parseColor(Utility.doubleToColorString(progressDouble)))
            ));
            holder.mTaskProgressBar.setMax(currentRecord.getRepetitionMax());
            holder.mTaskProgressBar.setProgress(currentRecord.getRepetitionCount());

            progress = progress + "/" + currentRecord.getRepetitionMax();

            //for mark-as-done tasks, checkbox inside item must follow the current repetitionCount
            if (currentRecord.getTaskType() == 0 || currentRecord.getTaskType() == 1) {
                if (progressInt == 1) {
                    holder.mCheckBox.setChecked(true);
                }
            }
            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (currentRecord.getTaskType() == 0 || currentRecord.getTaskType() == 1) {
                        //handle when only the checkbox inside the recyclerview is clicked
                        if (currentRecord.getRepetitionCount() == 0) {
                            //update data
                            currentRecord.setRepetitionCount(currentRecord.getRepetitionCount()+1);
                        }
                        else {
                            //update data
                            currentRecord.setRepetitionCount(currentRecord.getRepetitionCount()-1);
                        }
                    }
                }
            });
            holder.mTaskStatusTextView.setText(progress);


            switch (currentRecord.getTaskType()) {
                case 0:
                case 1:
                    //mark as done tasks
                    holder.mCheckBox.setVisibility(View.VISIBLE);
                    break;
                case 2:
                case 3:
                    //timed tasks
                    holder.mImageView.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    public void setTodayRecords(List<Record> records) {
        todayRecordList = records;
        notifyDataSetChanged();
    }

    public void setTodayTasks(List<Task> tasks) {
        todayTaskList = tasks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (todayRecordList != null) {
            return todayRecordList.size();
        } else {
            return 0;
        }
    }
}
