package org.secuso.privacyfriendlysolitaire.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.badlogic.gdx.graphics.Color;

import org.secuso.privacyfriendlysolitaire.R;

import java.util.Map;
import java.util.Set;

/**
 * Created by meric-doga on 24.01.17.
 */

public class BackgroundActivity extends AppCompatActivity {

    public RadioButton green, blue, grey, brown, selecdetColor;

    Boolean gr, bl, gra, bro;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backgroundcolor);

        final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final SharedPreferences.Editor edit = mSharedPreferences.edit();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(BaseActivity.MAIN_CONTENT_FADEIN_DURATION);
        }

        overridePendingTransition(0, 0);

        //set backgroundcolor in Game
        green = (RadioButton) findViewById(R.id.checkbox_green);
        blue = (RadioButton) findViewById(R.id.checkbox_blue);
        grey = (RadioButton) findViewById(R.id.checkbox_grey);
        brown = (RadioButton) findViewById(R.id.checkbox_brown);


        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setChecked(true);
                blue.setChecked(false);
                grey.setChecked(false);
                brown.setChecked(false);
                edit.putString("pref_color", "green");
                edit.commit();
            }
        });

        grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setChecked(false);
                blue.setChecked(false);
                grey.setChecked(true);
                brown.setChecked(false);
                edit.putString("pref_color", "gray");
                edit.commit();
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setChecked(false);
                blue.setChecked(true);
                grey.setChecked(false);
                brown.setChecked(false);
                edit.putString("pref_color", "blue");
                edit.commit();
            }
        });

        brown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setChecked(false);
                blue.setChecked(false);
                grey.setChecked(false);
                brown.setChecked(true);
                edit.putString("pref_color", "brown");
                edit.commit();
            }
        });


//TODO: checkbox checks müssen noch hier gesetzt werden
       if(green.isEnabled()){
           green.setChecked(true);
           brown.setChecked(false);
           blue.setChecked(false);
           grey.setChecked(false);
        }
        else if(blue.isEnabled()){
           blue.setChecked(true);
           green.setChecked(false);
           brown.setChecked(false);
           grey.setChecked(false);
       }else if(grey.isEnabled()){
           grey.setChecked(true);
           green.setChecked(false);
           blue.setChecked(false);
           brown.setChecked(false);
       }else if(brown.isEnabled()){
           brown.setChecked(true);
           green.setChecked(false);
           blue.setChecked(false);
           grey.setChecked(false);
       }
    }

}
