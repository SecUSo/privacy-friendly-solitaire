package org.secuso.privacyfriendlysolitaire.Activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;

import org.secuso.privacyfriendlysolitaire.game.Application_maybe_later_Controller;
import org.secuso.privacyfriendlysolitaire.R;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.game_layout);

		// just for demo-reasons
		final Button testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast toast = Toast.makeText(getApplicationContext(), "Reaktion", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
			}
		});

		GLSurfaceView20 gameView =
				(GLSurfaceView20) initializeForView(new Application_maybe_later_Controller(), new AndroidApplicationConfiguration());

		LinearLayout outerLayout = (LinearLayout) findViewById(R.id.outer);
		outerLayout.addView(gameView);

		// skip basic_layout and show game on full screen
//		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		initialize(new TestGame(), config);
	}
}
