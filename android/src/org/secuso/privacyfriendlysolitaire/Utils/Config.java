package org.secuso.privacyfriendlysolitaire.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    private final SharedPreferences settings;
    private final SharedPreferences.Editor editor;

    public Config(Context applicationContext) {
        settings = applicationContext.getSharedPreferences("settings", 0);
        editor = settings.edit();
    }

    public boolean isFirstCall() {
        // check whether this is the first call of this app,
        // if no record exists, it probably is (return true)
        boolean isFirstCall = settings.getBoolean(Constant.FIRST_CALL, true);

        // after the first call, we have to toggle the state (we can skip this later)
        if (isFirstCall) {
            editor.putBoolean(Constant.FIRST_CALL, false);
            editor.apply();
        }

        return isFirstCall;
    }
}
