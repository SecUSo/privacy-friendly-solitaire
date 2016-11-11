package org.secuso.privacyfriendlysolitaire.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

//    TODO: irgendwo Hinweis einfügen, dass wir Code von der example App übernommen haben. Details sollten in der Lizenz stehen.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }
}
