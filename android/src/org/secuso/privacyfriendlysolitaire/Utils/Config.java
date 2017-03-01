package org.secuso.privacyfriendlysolitaire.Utils;
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

        // first call of app
        return isFirstCall;
    }

    // alerbox warning, if player wants to leave game
    public boolean showWarningWhenLeavingGame() {
        return settings.getBoolean(Constant.SHOW_WARNING, true);
    }


    public void setShowWarningWhenLeavingGame(boolean show_warning) {
        editor.putBoolean(Constant.SHOW_WARNING, show_warning);
        editor.apply();
    }

}
