package org.secuso.privacyfriendlysolitaire.Activities;
/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.StringBuilder;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.Utils.Config;
import org.secuso.privacyfriendlysolitaire.game.Application;
import org.secuso.privacyfriendlysolitaire.R;
import org.secuso.privacyfriendlysolitaire.game.Constants;

import java.util.Timer;
import java.util.TimerTask;


public class Solitaire extends AndroidApplication implements
        NavigationView.OnNavigationItemSelectedListener, CallBackListener {

    //set color values for backgroundcolor of game field, which can be selected in the settings of the App
    public static final Color GRAY_SOL = new Color(0.75f, 0.75f, 0.75f, 1);
    public static final Color GREEN_SOL = new Color(143 / 255.0f, 188 / 255.0f, 143 / 255.0f, 1f);
    public static final Color BLUE_SOL = new Color(176 / 255.0f, 196 / 255.0f, 222 / 255.0f, 1);
    public static final Color LILA_SOL = new Color(216 / 255.0f, 191 / 255.0f, 216 / 255.0f, 1);
    public static final Color WHITE_SOL = new Color(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1);
    com.badlogic.gdx.graphics.Color c;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    // declare the attributes for time, which can be counted in a game
    Timer timer;
    TimerTask timerTask;
    TextView timerView;
    TextView pointsView;


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
    private Config config;

    // SHAKE
    Application application;
    boolean countTime = false;
    boolean showPoints = false;
    static boolean stillLeave = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_layout);
        config = new Config(getApplicationContext());
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mHandler = new Handler();
        overridePendingTransition(0, 0);

        application = new Application();
        application.registerCallBackListener(this);

        //initialize game view an functions, which are implemented in th core package with LibGDX
        final GLSurfaceView20 gameView =
                (GLSurfaceView20) initializeForView(application, new AndroidApplicationConfiguration());

        LinearLayout outerLayout = (LinearLayout) findViewById(R.id.outer);
        outerLayout.addView(gameView);

        // settings, which were set by the player,
        // if the setting could not be found, set it to false
        final boolean sound = mSharedPreferences.getBoolean(getString(R.string.pref_sound_switch), false);
        final boolean shake = mSharedPreferences.getBoolean(getString(R.string.pref_shake_switch), false);
        final boolean time = mSharedPreferences.getBoolean(getString(R.string.pref_time), false);
        final boolean draganddrop = mSharedPreferences.getBoolean(getString(R.string.pref_dnd_switch), false);
        countTime = time;

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if (shake) {
                    application.autoFoundations();
                }
            }
        });


        //start timer for game if it is selected in setting by the player
        timerView = (TextView) findViewById(R.id.timerView);
        if (time) {
            startTimer();
        } else {
            // hide timer
            timerView.setVisibility(View.INVISIBLE);
        }


        // default modes for cardDraw and score
        int scoreMode = Constants.MODE_STANDARD;
        int cardDrawMode = Constants.MODE_ONE_CARD_DEALT;

        String waste_sel = mSharedPreferences.getString(getString(R.string.pref_waste), "1");

        // settings-> waste
        if (waste_sel.equals("1")) {
            cardDrawMode = Constants.MODE_ONE_CARD_DEALT;
        } else if (waste_sel.equals("2")) {
            cardDrawMode = Constants.MODE_THREE_CARDS_DEALT;
        }

        //pointsView && select point counting mode in settings
        pointsView = (TextView) findViewById(R.id.points);
        String point_count = mSharedPreferences.getString(getString(R.string.pref_count_point), "1");

        switch (point_count) {
            case "1":
                scoreMode = Constants.MODE_NONE;
                // hide points text and value
                pointsView.setVisibility(View.INVISIBLE);
                findViewById(R.id.point).setVisibility(View.INVISIBLE);
                break;
            case "2":
                scoreMode = Constants.MODE_STANDARD;
                showPoints = true;
                break;
            case "3":
                scoreMode = Constants.MODE_VEGAS;
                showPoints = true;
                break;
        }

        // Set the background color of the game panel
        String color = mSharedPreferences.getString(getString(R.string.sp_key_background_color), "1");

        switch (color) {
            case "1":
                c = GREEN_SOL;
                break;
            case "2":
                c = BLUE_SOL;
                break;
            case "3":
                c = GRAY_SOL;
                break;
            case "4":
                c = LILA_SOL;
                break;
            case "5":
                c = WHITE_SOL;
                break;
            default:
                c = GREEN_SOL;
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
        application.customConstructor(cardDrawMode, scoreMode, sound, c, draganddrop);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
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

    //display time in Alertbox
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

    //Alert box for winning a game which prints the total time and the reached points
    public void alertBoxWonMessage() {

        WonDialog dia = new WonDialog();
        Bundle args = new Bundle();

        // put necessary arguments to build correct alertBox
        args.putString("timeForAlert", timeForAlert(time));
        args.putString("pointsString", pointsView.getText().toString());
        dia.setArguments(args);
        dia.show(getFragmentManager(), "WonDialog");
    }

    //Alert box for losing a game which prints the total time and the reached points
    public void alertBoxLostMessage() {
        LostDialog dia = new LostDialog();
        dia.show(getFragmentManager(), "WonDialog");
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
        TaskStackBuilder builder = TaskStackBuilder.create(this);
        builder.addNextIntentWithParentStack(intent);
        builder.startActivities();
    }

    private void callDrawerItem(final int itemId) {
        Intent intent;

        if (config.showWarningWhenLeavingGame()) {
            WarningDialog dia = new WarningDialog();
            Bundle args = new Bundle();

            // put itemId, so when the user makes his choice, we can call this method again and
            // potentially change the activity
            args.putInt("itemId", itemId);
            dia.setArguments(args);
            dia.show(getFragmentManager(), "WarningDialog");
        } else {
            stillLeave = true;
        }

        if (stillLeave) {
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

            stillLeave = false;
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

    @Override
    public void onWon() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stoptimertask(timerView);
                alertBoxWonMessage();
            }
        });
    }

    @Override
    public void onLost() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stoptimertask(timerView);
                alertBoxLostMessage();
            }
        });
    }

    @Override
    public void isUndoRedoPossible(final boolean canUndo, final boolean canRedo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //change undo-button and redo-button images here so that user knows if action is possible
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


    // if we did make this dialog static, we could not close the surrounding activity
    @SuppressLint("ValidFragment")
    public class WonDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            String timeForAlert = getArguments().getString("timeForAlert");
            String pointsString = getArguments().getString("pointsString");

            StringBuilder sb = new StringBuilder();
            sb.append("\n\n").append(getString(R.string.alert_box_won_generic_message)).append("\n\n");
            if (countTime) {
                sb.append(getString(R.string.alert_box_won_time)).append(" ").append(timeForAlert).append("\n");
            }
            if (showPoints) {
                sb.append(getString(R.string.alert_box_won_points)).append(" ").append(pointsString);
            }

            String message = sb.toString();


            LayoutInflater i = getActivity().getLayoutInflater();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setView(i.inflate(R.layout.custom_dialog, null))
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getActivity().getString(R.string.alert_box_won))
                    .setMessage(message)
                    .setCancelable(false)
                    // go back to main menu
                    .setNegativeButton(getString(R.string.alert_box_won_lost_main), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close current activity
                            dialog.dismiss();

                            Solitaire.this.finish();
                        }
                    })
                    // or start another game
                    .setPositiveButton(getString(R.string.alert_box_won_lost_another), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, start current activity anew
                            dialog.dismiss();
                            Solitaire.this.recreate();
                        }
                    });

            return builder.create();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            alertBoxWonMessage();
        }
    }


    // if we did make this dialog static, we could not access callDrawerItem
    @SuppressLint("ValidFragment")
    public class WarningDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final int itemId = getArguments().getInt("itemId");

            //selectable checkbox if player do not want, that the message box is shown agin next time
            LayoutInflater i = getActivity().getLayoutInflater();
            View view = i.inflate(R.layout.custom_dialog, null);
            final CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            checkbox.setVisibility(View.VISIBLE);
            checkbox.setText(getString(R.string.warning_box_show_future));


            //warning message, if player wants to leave game
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setView(view)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getActivity().getString(R.string.warning_box_title))
                    .setMessage(getString(R.string.warning_box_message))
                    .setCancelable(false)
                    // stay
                    .setNegativeButton(getString(R.string.warning_box_negative_answer), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (checkbox.isChecked()) {
                                setDoNotShowWarningInFuture();
                            }
                            dialog.dismiss();
                        }
                    })
                    // leave the current activity
                    .setPositiveButton(getString(R.string.warning_box_positive_answer), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            stillLeave = true;
                            if (checkbox.isChecked()) {
                                setDoNotShowWarningInFuture();
                            }
                            callDrawerItem(itemId);
                        }
                    });

            return builder.create();
        }
    }

    // if we did make this dialog static, we could not close the surrounding activity
    @SuppressLint("ValidFragment")
    public class LostDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            StringBuilder sb = new StringBuilder();
            sb.append("\n\n" + getString(R.string.alert_box_lost_generic_message) + "\n\n");
            String message = sb.toString();


            LayoutInflater i = getActivity().getLayoutInflater();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setView(i.inflate(R.layout.custom_dialog, null))
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getActivity().getString(R.string.alert_box_lost))
                    .setMessage(message)
                    .setCancelable(false)
                    // go back to main menu
                    .setNegativeButton(getString(R.string.alert_box_won_lost_main), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close current activity
                            dialog.dismiss();

                            Solitaire.this.finish();
                        }
                    })
                    // or start another game
                    .setPositiveButton(getString(R.string.alert_box_won_lost_another), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, start current activity anew
                            dialog.dismiss();
                            Solitaire.this.recreate();
                        }
                    });

            return builder.create();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            alertBoxLostMessage();
        }
    }

    private void setDoNotShowWarningInFuture() {
        config.setShowWarningWhenLeavingGame(false);
    }
}