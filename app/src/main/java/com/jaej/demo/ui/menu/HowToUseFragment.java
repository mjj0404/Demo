package com.jaej.demo.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaej.demo.R;
import com.jaej.demo.adapter.AboutRecyclerViewAdapter;
import com.jaej.demo.model.About;

import java.util.ArrayList;

public class HowToUseFragment extends Fragment {

    private final ArrayList<About> aboutArrayList = new ArrayList<>();

    public static HowToUseFragment newInstance() {
        return new HowToUseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_how_to_use, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mAboutRecyclerView = view.findViewById(R.id.about_recycler_view);
        mAboutRecyclerView.setHasFixedSize(true);
        mAboutRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //About model takes boolean parameter to decide its view in recyclerview

        aboutArrayList.add(new About(true,"Brief OverView"));
        aboutArrayList.add(new About(false,"Today's Task Tab\t\t-\tShows all today's tasks." +
                "All Tasks Tab\t\t-\tShows all tasks (old and new) with the overall color score of the specific task." +
                "Life ColorView Tab\t\t-\tBoxed view of range between time.In Setting\t\t-\t" +
                "set options for your birthday, view range, view grouping options, and more."));
        aboutArrayList.add(new About(true,"Why Life ColorBook?"));
        aboutArrayList.add(new About(false,"\t\t\tFirst of all, this application is " +
                "greatly inspired by Tim Urban's speech during the TED conference, 2016. At the end" +
                " of his speech, he shows the calendar he calls, 'Life Calendar' - a screen full of" +
                " empty boxes, where boxes represent numbers of weeks in 90 years. Life ColorBook is" +
                " created to help people visualize where they are in their lives, realize how short" +
                " their lives are, and hopefully manage their time and tasks a little better."));
        aboutArrayList.add(new About(false,"\t\t\tWilliam Shakespeare once said, \"Life" +
                " is too short, so live your life to the fullest..every second of your life " +
                "just treasure it.\"Video Link to Tim Urban's TED conference: " +
                "https://www.ted.com/talks/tim_urban_inside_the_mind_of_a_master_" +
                "procrastinator?referrer=playlist-talks_for_procrastinators"));

        aboutArrayList.add(new About(true,"Timer for Tasks and Resting"));
        aboutArrayList.add(new About(false,"\t\t\tDepending on your ability to stay" +
                " focused on a task, and your task's difficulty, adjust your timer in minutes. " +
                "The options for task timers vary from 10 minutes to 120 minutes. Not only try " +
                "to finish your daily goal but also try to increase time spent on task! Timer for " +
                "resting is optional."));
        aboutArrayList.add(new About(true,"What's Life ColorView?"));
        aboutArrayList.add(new About(false,"\t\t\tIn the Life ColorView tab, you'll" +
                " find boxes, representing a day, a week, or a month for the time span you set in" +
                " from the Setting tab. As of 2018 in the U.S., the average life expectancy is" +
                " 78.54, and it's been always increasing, thus added an option for a life of" +
                " 100 years."));
        aboutArrayList.add(new About(false,"\t\t\tNote that when each box is" +
                " representing a week, and the view range is the beginning of the year," +
                " you might see a box dangling. It is because each week is calculated from Sunday" +
                " to Saturday, and the box will belong to the month or year that Sunday belongs."));
        aboutArrayList.add(new About(false,"\t\t\tFor example, if your view range" +
                " starts at 1/1/2021 and each box represents a week, the first box will be" +
                " considered the week of the December, 2020, representing 12/27/2020 - 1/2/2021."));
        aboutArrayList.add(new About(true,"Score System and Color span"));
        aboutArrayList.add(new About(false,"\t\t\tThe internal score system is 0-100" +
                " based on your activity from the 'Today's Task' tab, where 0 is red and 100" +
                " is green."));

        AboutRecyclerViewAdapter mAboutAdapter = new AboutRecyclerViewAdapter(requireActivity(), aboutArrayList);
        mAboutRecyclerView.setAdapter(mAboutAdapter);

    }

}