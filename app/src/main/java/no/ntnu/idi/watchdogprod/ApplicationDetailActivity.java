package no.ntnu.idi.watchdogprod;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by sigurdhf on 06.03.2015.
 */
public class ApplicationDetailActivity extends ActionBarActivity {
    private String applicationPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        applicationPackageName = getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(applicationPackageName);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
