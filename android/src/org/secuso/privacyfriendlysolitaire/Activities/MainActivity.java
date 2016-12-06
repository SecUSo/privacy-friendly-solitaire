package org.secuso.privacyfriendlysolitaire.Activities;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import org.secuso.privacyfriendlysolitaire.R;
import org.secuso.privacyfriendlysolitaire.Utils.Config;

public class MainActivity extends BaseActivity {
    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        config = new Config(getApplicationContext());
        // the method will
        if (config.isFirstCall()) {
            WelcomeDialog welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(getFragmentManager(), "WelcomeDialog");
        }

        Button button=(Button)findViewById(R.id.game_button_start);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Solitaire.class));
            }
        });
    }


    public static class WelcomeDialog extends DialogFragment {

        // method onAttach removed, was deprecated

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater i = getActivity().getLayoutInflater();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(i.inflate(R.layout.welcome_dialog, null));
            builder.setIcon(R.mipmap.icon);
            builder.setTitle(getActivity().getString(R.string.welcome));
            builder.setPositiveButton(getActivity().getString(R.string.okay), null);
            builder.setNegativeButton(getActivity().getString(R.string.viewhelp), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity) getActivity()).goToNavigationItem(R.id.nav_help);
                }
            });

            return builder.create();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            // do something with all these buttons?
            default:
        }
    }
}
