package no.ntnu.idi.watchdogprod;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.domain.ProfileBehavior;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.AppInfo;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.ApplicationUpdatesDataSource;
import no.ntnu.idi.watchdogprod.services.DataUsagePosterService;
import no.ntnu.idi.watchdogprod.services.DataUsageService;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileDataSource;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileEvent;


public class MainActivity extends ActionBarActivity {
    public static final String KEY_INITIAL_LAUNCH = "initLaunch";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.profile_behavior_list);
        ProfileBehaviorListAdapter arrayAdapter = new ProfileBehaviorListAdapter(this, populateProfileBehaviorList());
        listView.setAdapter(arrayAdapter);
        listView.setScrollContainer(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                TextView textView = (TextView)view.findViewById(R.id.collapsable_behavior_text);
                ImageView imageView = (ImageView) view.findViewById(R.id.behavior_item_arrow);

                if(textView.getVisibility() == View.GONE) {
                    textView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.arrow_up_bold);
                } else {
                    textView.setVisibility(View.GONE);
                    imageView.setImageResource(R.drawable.ic_arrow_down_bold);
                }

            }
        });

//        TextView dbTest = (TextView) findViewById(R.id.database_test_profile);

        ArrayList<ProfileEvent> events = getUninstalledAppsHistory();
        Profile profile = getProfileValues();

        StringBuilder stringBuilder = new StringBuilder();
        for (ProfileEvent event : events) {
            stringBuilder.append(event.getPackageName() + " " + event.getEvent() + " " + event.getValue() + "\n");
        }
//        dbTest.setText("uninstalled apps: " + stringBuilder.toString() + "\nScreenLOCK: " + isDeviceSecured());


//        boolean answeredQuestions = checkAnsweredQuestionsState();
//        if(!answeredQuestions) {
//            Intent intent = new Intent(MainActivity.this, UserQuestionActivity.class);
//            startActivity(intent);
//        }
                   //=====================================///
//        setDataUsageSendingAlarm();
//        setDatabaseLoggingAlarm();

        Button privacyAnalysisBtn = (Button) findViewById(R.id.main_privacy_analysis_btn);
        privacyAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ApplicationListActivity.class);
                startActivity(i);
            }
        });

        if (isInitialLaunch()) {
            // ToDo bugfix at dette garanteres å gjøre før man kan gå inn i app list
            writeAllApplicationsToUpdateLog();
        }
    }

    private boolean isInitialLaunch() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean isInitialLaunch = sharedPref.getBoolean(KEY_INITIAL_LAUNCH, true);

        if (isInitialLaunch) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(KEY_INITIAL_LAUNCH, false);
            editor.commit();
            return true;
        }

        return false;
    }

    private void writeAllApplicationsToUpdateLog() {
        ArrayList<ExtendedPackageInfo> applications = ApplicationHelper.getThirdPartyApplications(this);

        ApplicationUpdatesDataSource dataSource = new ApplicationUpdatesDataSource(this);
        dataSource.open();

        for (ExtendedPackageInfo app : applications) {
            try {
                AppInfo appInfo = dataSource.insertApplicationUpdate(ApplicationHelper.getAppInfo(app.getPackageInfo().packageName, this));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        dataSource.close();

    }

    private void setDataUsageSendingAlarm() {

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), DataUsageService.class);
        intent.putExtra("dataUsageSendingAlarm", true);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1234, intent,0);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {

        }
        int timeForAlarm=5000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+timeForAlarm, timeForAlarm,pendingIntent);
    }

    private void setDatabaseLoggingAlarm() {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), DataUsagePosterService.class);
        intent.putExtra("databaseLoggingAlarm", true);
        PendingIntent   pendingIntent = PendingIntent.getService(this, 8888, intent,0);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {

        }
//        long timeForAlarm = AlarmManager.INTERVAL_HOUR;
        long timeForAlarm = 20000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ timeForAlarm, timeForAlarm,pendingIntent);
    }

    public ArrayList<ProfileBehavior> populateProfileBehaviorList() {
        ArrayList<ProfileBehavior> profileBehaviors = new ArrayList<>();
        profileBehaviors.add(new ProfileBehavior(null,"Nedgang i farlige apper", "tekst",2));
        profileBehaviors.add(new ProfileBehavior(null,"Økt trusselbilde", "tekst",1));
        profileBehaviors.add(new ProfileBehavior(null,"Mindre datatrafikk", "tekst",3));
        profileBehaviors.add(new ProfileBehavior(null,"Forståelse for tillatelser", "tekst",2));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }  else if(id == R.id.main_settings) {
            //
        }

        return super.onOptionsItemSelected(item);
    }
}
