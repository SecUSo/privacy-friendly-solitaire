package org.secuso.privacyfriendlysolitaire.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
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
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.Utils.Config;
import org.secuso.privacyfriendlysolitaire.game.Application;
import org.secuso.privacyfriendlysolitaire.R;
import org.secuso.privacyfriendlysolitaire.game.Constants;


public class Solitaire extends AndroidApplication implements NavigationView.OnNavigationItemSelectedListener, CallBackListener {


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


    private AppCompatDelegate mDelegate;


    //private AppCompatDelegate delegate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //    getDelegate().installViewFactory();
        //  getDelegate().onCreate(savedInstanceState);

        setContentView(R.layout.game_layout);


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mHandler = new Handler();
        overridePendingTransition(0, 0);


        final Application application = new Application();
        application.registerCallBackListener(this);

//        Config config = new Config(getApplicationContext());
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;

        cfg.useGLSurfaceView20API18 = false;

        final GLSurfaceView20 gameView = (GLSurfaceView20) initializeForView(application, cfg);


        LinearLayout outerLayout = (LinearLayout) findViewById(R.id.outer);
        outerLayout.addView(gameView);


        if (graphics.getView() instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) graphics.getView();
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }


        // TODO: get from settings/config
        int cardDrawMode = Constants.MODE_ONE_CARD_DEALT;
        int scoreMode = Constants.MODE_STANDARD;
        application.customConstructor(cardDrawMode, scoreMode);


        final boolean sound = mSharedPreferences.getBoolean("pref_sound_switch", true);
        final boolean shake = mSharedPreferences.getBoolean("pref_shake_switch", true);
        final boolean waste = mSharedPreferences.getBoolean("pref_waste", true);
        final boolean points = mSharedPreferences.getBoolean("pref_count_point", true);


        if (mSharedPreferences != null && sound) {
            //TODO: Sound an schalten
        } else {
            //TODO: Sound aus schalten
        }

        if (mSharedPreferences != null && shake) {
            //TODO: Shake animation on
        } else {
            //TODO: Shake animation off
        }

        if (mSharedPreferences != null && waste) {
            //TODO: waste 3 Karten
        } else {
            //TODO: waste 1 Karte
        }


        if (mSharedPreferences != null && points) {
            //TODO: Points Vegas
        } else {
            //TODO: Points Standard
        }


        ImageButton undo = (ImageButton) findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Button für undo

            }
        });

        ImageButton redo = (ImageButton) findViewById(R.id.redo);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Button für redo

            }
        });
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

    //  protected int getNavigationDrawerID() {return 0;}

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

//        TODO: nevigation_drawer erweitern
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

        //TODO: macht es einen Unterschied, wenn diese If-Bedinung nicht drin ist?
        //if (getSupportActionBar() == null) {
        //     setSupportActionBar(toolbar);
        // }

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
                // TODO: ordentliche Reaktion auf Gewinn :D
                Toast toast = Toast.makeText(getApplicationContext(), "You won", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        });
    }
}
