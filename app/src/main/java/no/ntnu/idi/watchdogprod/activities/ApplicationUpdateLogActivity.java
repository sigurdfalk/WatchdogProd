package no.ntnu.idi.watchdogprod.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collections;

import no.ntnu.idi.watchdogprod.AnalyticsHelper;
import no.ntnu.idi.watchdogprod.adapters.ApplicationUpdateLogListAdapter;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.ApplicationUpdatesDataSource;

/**
 * Created by sigurdhf on 10.03.2015.
 */
public class ApplicationUpdateLogActivity extends ActionBarActivity {


    private String applicationPackageName;
    private ApplicationUpdatesDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_update_log);

        applicationPackageName = getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME);

        dataSource = new ApplicationUpdatesDataSource(this);
        dataSource.open();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.application_update_log_list);
        ArrayList<AppInfo> updateLog = dataSource.getApplicationUpdatesByPackageName(applicationPackageName);
        Collections.sort(updateLog);
        ApplicationUpdateLogListAdapter adapter = new ApplicationUpdateLogListAdapter(this, updateLog);
        listView.setAdapter(adapter);

        // Get tracker.
        Tracker t = ((AnalyticsHelper) getApplication()).getTracker(
                AnalyticsHelper.TrackerName.APP_TRACKER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(ApplicationUpdateLogActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(ApplicationUpdateLogActivity.this).reportActivityStop(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}
