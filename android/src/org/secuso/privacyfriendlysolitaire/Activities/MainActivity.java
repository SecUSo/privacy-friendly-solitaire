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


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.secuso.privacyfriendlysolitaire.R;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //set NEW GAME button and the function to start a new game on Main Activity
        Button button = (Button) findViewById(R.id.game_button_start);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Solitaire.class));
            }
        });

        overridePendingTransition(0, 0);

    }

//do not open new activity by selecting Main Activity again
    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_example;
    }

//    public void onClick(View view) {
//        switch (view.getId()) {
//            default:
//        }
//    }
}
