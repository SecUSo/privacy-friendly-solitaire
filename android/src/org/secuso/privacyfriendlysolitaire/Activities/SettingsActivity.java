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
/**
 * Created by meric-doga on 27.11.16.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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

        //initiate SharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit = mSharedPreferences.edit();


    }

    //Do not open new Settings Activity if in drawer is Settings selected again
    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_settings;
    }


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

            //set selection of the settings in Backgroundcolor, Waste, Point counts in the Game
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
                    } else if (waste_list.getValue().equals("3")) {
                        edit.putString("pref_points", "vegas");
                        edit.commit();
                    } else {
                        edit.putString("pref_points", "none");
                        edit.commit();
                    }
                    return true;
                }
            });
        }
    }
}
