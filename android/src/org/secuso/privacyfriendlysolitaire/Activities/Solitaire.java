package org.secuso.privacyfriendlysolitaire.Activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;

import org.secuso.privacyfriendlysolitaire.Utils.Config;
import org.secuso.privacyfriendlysolitaire.game.Application;
import org.secuso.privacyfriendlysolitaire.R;
import org.secuso.privacyfriendlysolitaire.game.Constants;

public class Solitaire extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_layout);

        final Application application = new Application();

        Config config = new Config(getApplicationContext());

//		// just for demo-reasons
//		final Button testButton = (Button) findViewById(R.id.test_button);
//		testButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Toast toast = Toast.makeText(getApplicationContext(), "Reaktion", Toast.LENGTH_SHORT);
//				toast.setGravity(Gravity.BOTTOM, 0, 0);
//				toast.show();
//
//                application.printToConsole();
//			}
//		});

        GLSurfaceView20 gameView =
                (GLSurfaceView20) initializeForView(application, new AndroidApplicationConfiguration());


        LinearLayout outerLayout = (LinearLayout) findViewById(R.id.outer);
        outerLayout.addView(gameView);

        // TODO: get from settings/config
        int mode = Constants.MODE_ONE_CARD_DEALT;
        application.customConstructor(mode);


        // skip basic_layout and show game on full screen
//		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		initialize(new TestGame(), config);
    }
}
