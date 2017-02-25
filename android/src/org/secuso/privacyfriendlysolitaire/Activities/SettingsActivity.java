package org.secuso.privacyfriendlysolitaire.Activities;

/**
 * Created by meric-doga on 27.11.16.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlysolitaire.R;

public class SettingsActivity extends BaseActivity {
    final Context context = this;
    static SharedPreferences mSharedPreferences;
    static SharedPreferences.Editor edit;

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        overridePendingTransition(0, 0);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit = mSharedPreferences.edit();


    }


    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_settings;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    /*private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }*/

    /*@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
            // (!super.onMenuItemSelected(featureId, item)) {
            //    NavUtils.navigateUpFromSameTask(this);
            //}
            //return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }*/

    /**
     * {@inheritDoc}
     */
    /*@Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }*/

    /**
     * {@inheritDoc}
     */
    /*@Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }*/

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            final ListPreference color_list = (ListPreference) findPreference(getString(R.string.sp_key_background_color));


            color_list.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (color_list.getValue() == null) {
                        edit.putString("pref_col", "green");
                        edit.commit();
                    } else if (color_list.getValue().equals("1")) {
                        edit.putString("pref_col", "green");
                        edit.commit();
                    } else if (color_list.getValue().equals("2")) {
                        edit.putString("pref_col", "blue");
                        edit.commit();
                    } else if (color_list.getValue().equals("3")) {
                        edit.putString("pref_col", "grey");
                        edit.commit();
                    } else if (color_list.getValue().equals("4")) {
                        edit.putString("pref_col", "brown");
                        edit.commit();
                    } else {
                        edit.putString("pref_col", "green");
                        edit.commit();
                    }

                    return true;
                }
            });

            final ListPreference waste_list = (ListPreference) findPreference(getString(R.string.pref_waste));
            waste_list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (waste_list.getValue() == null) {
                        edit.putString("pref_waste", "one");
                        edit.commit();
                    } else if (waste_list.getValue().equals("1")) {
                        edit.putString("pref_waste", "one");
                        edit.commit();
                    } else if (waste_list.getValue().equals("2")) {
                        edit.putString("pref_waste", "three");
                        edit.commit();
                    } else {
                        edit.putString("pref_waste", "one");
                        edit.commit();
                    }
                    return true;
                }
            });

            final ListPreference points_list = (ListPreference) findPreference(getString(R.string.pref_count_point));

            points_list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (points_list.getValue() == null) {
                        edit.putString("pref_points", "none");
                        edit.commit();
                    } else if (waste_list.getValue().equals("1")) {
                        edit.putString("pref_points", "none");
                        edit.commit();
                    } else if (waste_list.getValue().equals("2")) {
                        edit.putString("pref_points", "standard");
                        edit.commit();
                    } else if(waste_list.getValue().equals("3")){
                        edit.putString("pref_points", "vegas");
                        edit.commit();
                    }else{
                        edit.putString("pref_points", "none");
                        edit.commit();
                    }
                    return true;
                }
            });
        }

    }


}
