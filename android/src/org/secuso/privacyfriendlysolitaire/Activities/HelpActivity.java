package org.secuso.privacyfriendlysolitaire.Activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.secuso.privacyfriendlysolitaire.R;
import android.view.View;

/**
 * Created by meric-doga on 25.11.16.
 */

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_help);
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new HelpFragment()).commit();

        overridePendingTransition(0, 0);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_help;
    }

    public static class HelpFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.help);
        }
    }

}

