package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.AppInfo;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.ApplicationUpdatesDataSource;


public class MainActivity extends ActionBarActivity {
    public static final String KEY_INITIAL_LAUNCH = "initLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button privacyAnalysisBtn = (Button) findViewById(R.id.main_privacy_analysis_btn);
        privacyAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ApplicationListActivity.class);
                startActivity(i);
            }
        });

        if (isInitialLaunch()) {
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
        ArrayList<PackageInfo> applications = ApplicationHelper.getThirdPartyApplications(this);

        ApplicationUpdatesDataSource dataSource = new ApplicationUpdatesDataSource(this);
        dataSource.open();

        for (PackageInfo app : applications) {
            try {
                AppInfo appInfo = dataSource.insertApplicationUpdate(ApplicationHelper.getAppInfo(app.packageName, this));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        dataSource.close();
    }
}
