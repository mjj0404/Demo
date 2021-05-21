package com.jaej.demo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaej.demo.R;
import com.jaej.demo.model.Task;
import com.jaej.demo.util.Utility;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    private Context context;
    private final LayoutInflater taskInflater;
    private List<Task> taskList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onEditClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTaskTextView, mSunTextView, mMonTextView, mTueTextView, mWedTextView,
                        mThrTextView, mFriTextView, mSatTextView, mOverallColorTextView;
        public final ImageButton mEditButton;
        public final ImageButton mDeleteButton;
        public final CardView mTaskItemCardView;

        public TaskViewHolder(@NonNull View itemView, Context ctx, final OnItemClickListener listener) {
            super(itemView);
            context = ctx;
            mTaskTextView = itemView.findViewById(R.id.text_view);
            mSunTextView = itemView.findViewById(R.id.sunday_text_view_recyclerview);
            mMonTextView = itemView.findViewById(R.id.monday_text_view_recyclerview);
            mTueTextView = itemView.findViewById(R.id.tuesday_text_view_recyclerview);
            mWedTextView = itemView.findViewById(R.id.wednesday_text_view_recyclerview);
            mThrTextView = itemView.findViewById(R.id.thursday_text_view_recyclerview);
            mFriTextView = itemView.findViewById(R.id.friday_text_view_recyclerview);
            mSatTextView = itemView.findViewById(R.id.saturday_text_view_recyclerview);
            mOverallColorTextView = itemView.findViewById(R.id.overall_color_text_view);
            mEditButton = itemView.findViewById(R.id.task_edit_button);
            mDeleteButton = itemView.findViewById(R.id.task_delete_button);
            mTaskItemCardView = itemView.findViewById(R.id.task_item_card_view);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {

                            listener.onItemClick(position);

                        }
                    }
                }
            });
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                }
            });
        }
    }



    public TaskRecyclerViewAdapter(Context context) {
        this.context = context;
        taskInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = taskInflater.inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view, context, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (taskList != null) {
            Task current = taskList.get(position);
            holder.mTaskTextView.setText(String.valueOf(current.getTaskName()));

            //setting active days
            getDaysTextView(holder, current.getDays());
            //adding the task's color based on its score for the recyclerview item
            holder.mOverallColorTextView.setTextColor(Color.parseColor(Utility.doubleToColorString(
                    current.getScore(), current.getScoreMax()
            )));

            if (current.getEndDate().before(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())) &&
                    !(current.getEndDate().getTime() + 86400000 ==
                            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                                    .toInstant())
                                    .getTime())) {
                //set low alpha for old, non-active tasks
                holder.mTaskItemCardView.setAlpha(0.4f);
            }
            else {
                holder.mTaskItemCardView.setAlpha(1.0f);
            }
        }
    }

    public void setTasks(List<Task> tasks) {
        taskList = tasks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (taskList != null) {
            return taskList.size();
        } else {
            return 0;
        }
    }

    //set the TextViews for the days based on the task object 'days' return
    private void getDaysTextView(@NonNull TaskViewHolder holder, String days) {
        for (char c : days.toCharArray()) {
            switch (c) {
                case '0':
                    holder.mSunTextView.getBackground().setTint(ContextCompat.getColor(context, R.color.sky));
                    break;
                case '1':
                    holder.mMonTextView.getBackground().setTint(ContextCompat.getColor(context, R.color.sky));
                    break;
                case '2':
                    holder.mTueTextView.getBackground().setTint(ContextCompat.getColor(context, R.color.sky));
                    break;
                case '3':
                    holder.mWedTextView.getBackground().setTint(ContextCompat.getColor(context, R.color.sky));
                    break;
                case '4':
                    holder.mThrTextView.getBackground().setTint(ContextCompat.getColor(context, R.color.sky));
                    break;
                case '5':
                    holder.mFriTextView.getBackground().setTint(ContextCompat.getColor(context, R.color.sky));
                    break;
                case '6':
                    holder.mSatTextView.getBackground().setTint(ContextCompat.getColor(context, R.color.sky));
                    break;
            }
        }
    }




}
