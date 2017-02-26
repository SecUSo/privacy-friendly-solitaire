package org.secuso.privacyfriendlysolitaire.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.graphics.Color;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.game.Application;
import org.secuso.privacyfriendlysolitaire.R;
import org.secuso.privacyfriendlysolitaire.game.Constants;

import java.util.Timer;
import java.util.TimerTask;


public class Solitaire extends AndroidApplication implements
        NavigationView.OnNavigationItemSelectedListener, CallBackListener, SensorListener {

    public static final Color GRAY_SOL = new Color(0.75f, 0.75f, 0.75f, 1);
    public static final Color GREEN_SOL = new Color(143 / 255.0f, 188 / 255.0f, 143 / 255.0f, 1f);
    public static final Color BLUE_SOL = new Color(176 / 255.0f, 196 / 255.0f, 222 / 255.0f, 1);
    public static final Color LILA_SOL = new Color(216 / 255.0f, 191 / 255.0f, 216 / 255.0f, 1);

    Timer timer;
    TimerTask timerTask;
    TextView timerView;
    TextView pointsView;
    final Context context = this;
    com.badlogic.gdx.graphics.Color c;


    // delay to launch nav drawer item, to allow close animation to play
    static final int NAVDRAWER_LAUNCH_DELAY = 250;
    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    // Helper
    private Handler mHandler;
    protected SharedPreferences mSharedPreferences;

    // SHAKE
    private SensorManager sensorMgr;
    private long lastUpdate;
    private static final int SHAKE_THRESHOLD = 800;
    float last_x, last_y, last_z;
    Application application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        // TODO: deprecated ersetzen durch aktuelle Methode(n)
        sensorMgr.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);

        setContentView(R.layout.game_layout);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mHandler = new Handler();
        overridePendingTransition(0, 0);


        application = new Application();
        application.registerCallBackListener(this);

        final GLSurfaceView20 gameView =
                (GLSurfaceView20) initializeForView(application, new AndroidApplicationConfiguration());

        LinearLayout outerLayout = (LinearLayout) findViewById(R.id.outer);
        outerLayout.addView(gameView);

        // settings, which were set by the player,
        // if the setting could not be found, set it to false
        final boolean sound = mSharedPreferences.getBoolean(getString(R.string.pref_sound_switch), false);
        final boolean shake = mSharedPreferences.getBoolean(getString(R.string.pref_shake_switch), false);
        final boolean time = mSharedPreferences.getBoolean(getString(R.string.pref_time), false);

        //set sound in settings
        if (sound) {
            //TODO: Sound an schalten
        } else {
            //TODO: Sound aus schalten
        }

        //set shake function in settings
        if (shake) {
            //TODO: Shake animation on
        } else {
            //TODO: Shake animation off
        }

        //start timer for game
        timerView = (TextView) findViewById(R.id.timerView);
        if (time)
            startTimer();


        // default modes for cardDraw and score
        int scoreMode = Constants.MODE_STANDARD;
        int cardDrawMode = Constants.MODE_ONE_CARD_DEALT;

        if (mSharedPreferences.getString("pref_waste", "one").equals("one")) {
            scoreMode = Constants.MODE_ONE_CARD_DEALT;
        } else if (mSharedPreferences.getString("pref_waste", "three").equals("three")) {
            cardDrawMode = Constants.MODE_THREE_CARDS_DEALT;
        }


        //pointsView && select point counting mode in settings
        pointsView = (TextView) findViewById(R.id.points);

        if (mSharedPreferences.getString("pref_points", "none").equals("none")) {
            scoreMode = Constants.MODE_NONE;
        } else if (mSharedPreferences.getString("pref_points", "standard").equals("standard")) {
            scoreMode = Constants.MODE_STANDARD;
        } else if (mSharedPreferences.getString("pref_points", "vegas").equals("vegas")) {
            scoreMode = Constants.MODE_VEGAS;
        }

        // Set the background color of the game panel
        if (mSharedPreferences.getString("pref_col", "green").equals("green")) {
            c = GREEN_SOL;
        } else if (mSharedPreferences.getString("pref_col", "grey").equals("grey")) {
            c = GRAY_SOL;
        } else if (mSharedPreferences.getString("pref_col", "blue").equals("blue")) {
            c = BLUE_SOL;
        } else if (mSharedPreferences.getString("pref_col", "brown").equals("brown")) {
            c = LILA_SOL;
        }

        //undo Button in game panel
        ImageButton undo = (ImageButton) findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application.undo();
            }
        });

        //redo button in game panel
        ImageButton redo = (ImageButton) findViewById(R.id.redo);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application.redo();
            }
        });

        //hint button in game panel
        ImageButton hint = (ImageButton) findViewById(R.id.hint);
        hint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                application.autoMove();
            }
        });

        // start game
        application.customConstructor(cardDrawMode, scoreMode, c);

    }

    //Timer
    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 0, 1000); //
    }

    int time = 0;

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        time = (time + 1);
                        if (((time % 60) < 10)) {
                            timerView.setText(String.valueOf(time / 60) + ":" + "0" + String.valueOf(time % 60));
                        } else {
                            timerView.setText(String.valueOf(time / 60) + ":" + String.valueOf(time % 60));
                        }
                    }
                });
            }
        };
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    //display time   in Alertbox
    String timeAlert;

    public String timeForAlert(int t) {

        if (t == 0) {
            timeAlert = String.valueOf(t);
        } else if ((t < 60) && (t % 60 < 10)) {
            timeAlert = String.valueOf("0:0" + t);
        } else if ((t < 60) && (t % 60 > 10)) {
            timeAlert = String.valueOf("0:" + t);
        } else if ((t > 60) && (t % 60 > 10)) {
            timeAlert = String.valueOf(((t - (t % 60)) / 60) + ":" + (t % 60));
        } else if ((t > 60) && (t % 60 < 10)) {
            timeAlert = String.valueOf(((t - (t % 60)) / 60) + ":0" + (t % 60));
        }
        return timeAlert;
    }

    //Alert box for won a game which prints the total time and the reached points
    public void alertBoxWonMessage() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.alert_box_won));
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.alert_box_won_time) + timeForAlert(time) +
                        "\n" +
                        getString(R.string.alert_box_won_points) + pointsView.getText().toString())
                .setCancelable(true)
                // go back to main menu
                .setNegativeButton(getString(R.string.alert_box_won_main), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close current activity
                        dialog.dismiss();
                        Solitaire.this.finish();
                    }
                })
                // or start another game
                .setPositiveButton(getString(R.string.alert_box_won_another), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, start current activity anew
                        dialog.dismiss();
                        Solitaire.this.recreate();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        return goToNavigationItem(itemId);
    }

    protected boolean goToNavigationItem(final int itemId) {

        if (itemId == getNavigationDrawerID()) {
            // just close drawer because we are already in this activity
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        // delay transition so the drawer can close
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callDrawerItem(itemId);
            }
        }, NAVDRAWER_LAUNCH_DELAY);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        selectNavigationItem(itemId);

        // fade out the active activity
        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }
        return true;
    }

    // set active navigation item
    private void selectNavigationItem(int itemId) {
        for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
            boolean b = itemId == mNavigationView.getMenu().getItem(i).getItemId();
            mNavigationView.getMenu().getItem(i).setChecked(b);
        }
    }

    /**
     * Enables back navigation for activities that are launched from the NavBar. See
     * {@code AndroidManifest.xml} to find out the parent activity names for each activity.
     *
     * @param intent
     */
    private void createBackStack(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder builder = TaskStackBuilder.create(this);
            builder.addNextIntentWithParentStack(intent);
            builder.startActivities();
        } else {
            startActivity(intent);
            finish();
        }
    }

    private void callDrawerItem(final int itemId) {
        Intent intent;

        switch (itemId) {
            case R.id.nav_example:
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_game:
                intent = new Intent(this, Solitaire.class);
                createBackStack(intent);
                break;
            case R.id.nav_about:
                intent = new Intent(this, AboutActivity.class);
                createBackStack(intent);
                break;
            case R.id.nav_help:
                intent = new Intent(this, HelpActivity.class);
                createBackStack(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                createBackStack(intent);
                break;
            default:
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        selectNavigationItem(getNavigationDrawerID());

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }
    }


    protected int getNavigationDrawerID() {
        return R.id.nav_game;
    }

    Boolean alert_box_ok = false;

    @Override
    public void onWon() {
        if (!alert_box_ok) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alert_box_ok = true;
                    stoptimertask(timerView);
                    alertBoxWonMessage();
                }
            });
        }

    }

    @Override
    public void isUndoRedoPossible(final boolean canUndo, final boolean canRedo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO change undo-button and redo-button images here so that user knows if action is possible
            }
        });
    }


    @Override
    public void score(final int score) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pointsView.setText(String.valueOf(score));

            }
        });
    }


    @Override
    public void onSensorChanged(int sensor, float[] values) {
        // TODO: deprecated ersetzen durch aktuelle Methode(n)
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = values[SensorManager.DATA_X];
                float y = values[SensorManager.DATA_Y];
                float z = values[SensorManager.DATA_Z];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    application.autoFoundations();
                    Log.d("sensor", "shake detected w/ speed: " + speed);
                    Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}
