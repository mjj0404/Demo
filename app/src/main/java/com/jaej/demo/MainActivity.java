package com.jaej.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.jaej.demo.data.QuoteSingleton;
import com.jaej.demo.model.Task;
import com.jaej.demo.ui.home.DoTimedTaskFragment;
import com.jaej.demo.ui.task.TaskViewModel;
import com.jaej.demo.util.App;
import com.jaej.demo.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int ADDING_TASK_REQUEST_CODE = 1;

    private TextView mQuoteTextView;

    private AppBarConfiguration mAppBarConfiguration;
    public static FloatingActionButton fab;
    private TaskViewModel mTaskViewModel;

    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        //stores first day of the app to help LifeView loop less
        SharedPreferences mSharedPreference = MainActivity.this.getPreferences(Context.MODE_PRIVATE);

        if (mSharedPreference.getLong(Constants.FIRST_DAY, 0) == 0) {
            SharedPreferences.Editor editor = mSharedPreference.edit();
            editor.putLong(Constants.FIRST_DAY, Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
            editor.apply();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddModifyActivity.class);
                startActivityForResult(intent, ADDING_TASK_REQUEST_CODE);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_task, R.id.nav_life_view)
                .setOpenableLayout(drawer)
                .build();


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination,
                                             @Nullable Bundle arguments) {
                if (getIntent().hasCategory(Constants.TASK_NOTIFICATION)) {
                    //direct to doTimedTaskFragment when task notification is clicked
                    getIntent().removeCategory(Constants.TASK_NOTIFICATION);

                    String currentTaskString = getIntent().getStringExtra(Constants.TASK_FROM_SERVICE);
                    String currentRecordString = getIntent().getStringExtra(Constants.RECORD_FROM_SERVICE);

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.TASK_FROM_ACTIVITY, currentTaskString);
                    bundle.putString(Constants.RECORD_FROM_ACTIVITY, currentRecordString);
                    bundle.putBoolean(Constants.IS_TIMER_RUNNING, true);

                    DoTimedTaskFragment timedTaskFragment = new DoTimedTaskFragment();
                    timedTaskFragment.setArguments(bundle);
                    controller.navigate(R.id.doTimedTaskFragment, bundle);
                }
                else if (getIntent().hasCategory(Constants.REST_NOTIFICATION)) {
                    //direct to doTimedTaskFragment when rest notification is clicked
                    getIntent().removeCategory(Constants.REST_NOTIFICATION);

                    String currentTaskString = getIntent().getStringExtra(Constants.TASK_FROM_SERVICE);
                    String currentRecordString = getIntent().getStringExtra(Constants.RECORD_FROM_SERVICE);

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.TASK_FROM_ACTIVITY, currentTaskString);
                    bundle.putString(Constants.RECORD_FROM_ACTIVITY, currentRecordString);
                    bundle.putBoolean(Constants.IS_TIMER_RUNNING, true);

                    DoTimedTaskFragment timedTaskFragment = new DoTimedTaskFragment();
                    timedTaskFragment.setArguments(bundle);
                    controller.navigate(R.id.doTimedTaskFragment, bundle);
                }
                else if (getIntent().getAction().equals(Constants.ACTIVATE_INTENT_FROM_FRAGMENT)) {
                    //direct to doTimedTaskFragment when app is in background
                    getIntent().setAction("");
                    String currentTaskString = getIntent().getStringExtra(Constants.TASK_FROM_FRAGMENT);
                    String currentRecordString = getIntent().getStringExtra(Constants.RECORD_FROM_FRAGMENT);

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.TASK_FROM_ACTIVITY, currentTaskString);
                    bundle.putString(Constants.RECORD_FROM_ACTIVITY, currentRecordString);
                    bundle.putBoolean(Constants.IS_TIMER_RUNNING, false);

                    DoTimedTaskFragment timedTaskFragment = new DoTimedTaskFragment();
                    timedTaskFragment.setArguments(bundle);
                    controller.navigate(R.id.doTimedTaskFragment, bundle);
                }
                if (destination.getId() == R.id.nav_home ||
                        destination.getId() == R.id.nav_task) {
                    showFAB();
                }
                else {
                    hideFAB();
                }
            }
        });

        //request a random quote json from website with api key
//        View headerView = navigationView.getHeaderView(0);
//        mQuoteTextView = headerView.findViewById(R.id.quote_text_view);
//
//        final String url = "https://zenquotes.io/api/random/[API_KEY]";
//
//        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url,
//                null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    JSONObject object = response.getJSONObject(0);
//                    mQuoteTextView.append(object.getString("q") + "\n");
//                    mQuoteTextView.append("  - " + object.getString("a") + " -\n");
//                    mQuoteTextView.append("  - provided by zenquotes.io/");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("VLY", "onErrorResponse: " + error.getCause());
//            }
//        });
//        //add to the queue
//        QuoteSingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDING_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;

            Gson gson = new Gson();
            String newTaskStringJson = data.getStringExtra(AddModifyActivity.NEW_TASK_OBJECT);
            Task newTask = gson.fromJson(newTaskStringJson, Task.class);
            mTaskViewModel.insertTask(newTask);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        App.activityPaused();
    }

    @Override
    public void onResume() {
        super.onResume();
        App.activityResumed();
    }

    public static void hideFAB() {
        fab.hide();
    }

    public static void showFAB() {
        fab.show();
    }

    public static boolean isFabShowing() {
        return fab.isOrWillBeShown();
    }

}