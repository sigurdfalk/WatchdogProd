package no.ntnu.idi.watchdogprod;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.privacyProfile.Profile;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileDataSource;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileEvent;


public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView dbTest = (TextView)findViewById(R.id.database_test_profile);

        ProfileDataSource profileDataSource = new ProfileDataSource(this);
        profileDataSource.open();
        ArrayList<ProfileEvent> events = profileDataSource.getSpecificEvents(Profile.UNINSTALLED_DANGEROUS_APP);
        profileDataSource.close();

        StringBuilder stringBuilder = new StringBuilder();
        for(ProfileEvent event : events) {
            stringBuilder.append(event.getPackageName() + " " + event.getEvent() + " " + event.getValue() +"\n");
        }
        dbTest.setText(stringBuilder.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
