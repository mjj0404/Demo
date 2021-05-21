package com.jaej.demo.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaej.demo.R;
import com.jaej.demo.model.About;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AboutRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<About> mAllAboutList;

    public AboutRecyclerViewAdapter(Activity activity, ArrayList<About> storeAboutArrayList) {
        this.context = activity;
        this.mAllAboutList = storeAboutArrayList;

    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paragraph_item,
                parent, false);
        return new AboutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        final About item = getValueAt(position);
        AboutRecyclerViewAdapter.AboutViewHolder AboutViewHolder = (AboutRecyclerViewAdapter.AboutViewHolder)holder;
        if (item != null) {
            setTextInAdapterItem(AboutViewHolder, item);
        }
    }

    private About getValueAt(int position) {
        return mAllAboutList.get(position);
    }

    @Override
    public int getItemCount() {
        return mAllAboutList.size();
    }

    private void setTextInAdapterItem(AboutRecyclerViewAdapter.AboutViewHolder itemHolder, About about) {
        if (about != null) {
            itemHolder.mParagraphTextView.setText(about.getParagraph());
            if (about.isHead()) {
                //if the text is marked as heading
                itemHolder.mParagraphTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
                itemHolder.mParagraphTextView.setTextColor(ContextCompat.getColor(this.context, R.color.light_text));
                itemHolder.mParagraphTextView.setPadding(0,10,0,10);
            }
            else {
                //if the text is marked as paragraph
                itemHolder.mParagraphTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                itemHolder.mParagraphTextView.setTextColor(ContextCompat.getColor(this.context, R.color.dark_text));

            }
        }
    }

    public class AboutViewHolder extends RecyclerView.ViewHolder {

        public TextView mParagraphTextView;

        public AboutViewHolder(View itemView) {
            super(itemView);

            mParagraphTextView = itemView.findViewById(R.id.paragraph_text_view);

        }
    }
}
