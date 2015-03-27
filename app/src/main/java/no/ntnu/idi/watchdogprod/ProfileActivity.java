package no.ntnu.idi.watchdogprod;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.idi.watchdogprod.domain.ProfileBehavior;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileDataSource;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileEvent;


public class ProfileActivity extends ActionBarActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        listView = (ListView) findViewById(R.id.profile_behavior_list);
        ProfileBehaviorListAdapter arrayAdapter = new ProfileBehaviorListAdapter(this, populateProfileBehaviorList());
        listView.setAdapter(arrayAdapter);

//        TextView dbTest = (TextView) findViewById(R.id.database_test_profile);

        ArrayList<ProfileEvent> events = getUninstalledAppsHistory();
        Profile profile = getProfileValues();

        StringBuilder stringBuilder = new StringBuilder();
        for (ProfileEvent event : events) {
            stringBuilder.append(event.getPackageName() + " " + event.getEvent() + " " + event.getValue() + "\n");
        }
//        dbTest.setText("uninstalled apps: " + stringBuilder.toString() + "\nScreenLOCK: " + isDeviceSecured());


        boolean answeredQuestions = checkAnsweredQuestionsState();
        if(!answeredQuestions) {
            Intent intent = new Intent(ProfileActivity.this, UserQuestionActivity.class);
            startActivity(intent);
        }
    }


    public ArrayList<ProfileBehavior> populateProfileBehaviorList() {
        ArrayList<ProfileBehavior> profileBehaviors = new ArrayList<>();
        profileBehaviors.add(new ProfileBehavior(null,"tittel", "tekst",2));
        profileBehaviors.add(new ProfileBehavior(null,"tittel", "tekst",1));
        profileBehaviors.add(new ProfileBehavior(null,"tittel", "tekst",3));
        profileBehaviors.add(new ProfileBehavior(null,"tittel", "tekst",2));
        profileBehaviors.add(new ProfileBehavior(null,"tittel", "tekst",3));
        profileBehaviors.add(new ProfileBehavior(null,"tittel", "tekst",1));
        return profileBehaviors;
    }

    public boolean checkAnsweredQuestionsState() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getBoolean("answeredQuestions", false);
    }


    public ArrayList<ProfileEvent> getUninstalledAppsHistory() {
        ProfileDataSource profileDataSource = new ProfileDataSource(this);
        profileDataSource.open();
        ArrayList<ProfileEvent> events = profileDataSource.getSpecificEvents(Profile.UNINSTALLED_DANGEROUS_APP);
        profileDataSource.close();
        return events;
    }

    public Profile getProfileValues() {
        ProfileDataSource profileDataSource = new ProfileDataSource(this);
        profileDataSource.open();
        Profile profile = new Profile(profileDataSource.getLatestProfileValue(Profile.UNDERSTANDING_OF_PERMISSIONS),
                profileDataSource.getLatestProfileValue(Profile.INTEREST_IN_PRIVACY),
                profileDataSource.getLatestProfileValue(Profile.UTILITY_OVER_PRIVACY),
                profileDataSource.getLatestProfileValue(Profile.CONCERNED_FOR_LEAKS));
        profileDataSource.close();
        return profile;
    }

    private boolean isDeviceSecured() {
        String LOCKSCREEN_UTILS = "com.android.internal.widget.LockPatternUtils";
        try {
            Class<?> lockUtilsClass = Class.forName(LOCKSCREEN_UTILS);
            Object lockUtils = lockUtilsClass.getConstructor(Context.class).newInstance(this);

            Method method = lockUtilsClass.getMethod("getActivePasswordQuality");

            int lockProtectionLevel = Integer.valueOf(String.valueOf(method.invoke(lockUtils)));

            if (lockProtectionLevel >= DevicePolicyManager.PASSWORD_QUALITY_NUMERIC) {
                return true;
            }
        } catch (Exception e) {
            Log.e("reflectInternalUtils", "ex:" + e);
        }
        return false;
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
        if (id == R.id.profile_information) {
            return true;
        } else if(id == R.id.profile_tips) {
            showTipsDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTipsDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_profile_tips,null);

        TextView textView = (TextView)v.findViewById(R.id.profile_dialog_tips_text);
        boolean screenLock = isDeviceSecured();
        if(screenLock) {
            textView.setText("For å ytterligere øke sikkerheten på telefonen kan du: \n - Aktivere skjermlås. Dette hindrer fri adgang til telefonen hvis du for eksempel skulle miste den.");
        } else {
            textView.setText("Ingen tips tilgjengelig for øyeblikket");
        }

        builder.setView(v);
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
               dialog.dismiss();
            }
        });

        builder.create();
        builder.show();
    }
}
