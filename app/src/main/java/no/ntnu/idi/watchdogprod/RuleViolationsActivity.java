package no.ntnu.idi.watchdogprod;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class RuleViolationsActivity extends ActionBarActivity {
    private String applicationPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_violations);

        applicationPackageName = getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.rule_violations_list);

        PackageInfo packageInfo = null;

        try {
            packageInfo = ApplicationHelper.getPackageInfo(applicationPackageName, this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // ToDo implement error handling
        }

        ArrayList<Rule> violatedRules = RuleHelper.getViolatedRules(packageInfo.requestedPermissions, this);
        RuleListAdapter adapter = new RuleListAdapter(this, violatedRules);
        listView.setAdapter(adapter);
    }
}
