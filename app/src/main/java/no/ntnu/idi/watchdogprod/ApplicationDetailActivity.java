package no.ntnu.idi.watchdogprod;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sigurdhf on 06.03.2015.
 */
public class ApplicationDetailActivity extends ActionBarActivity {
    private String applicationPackageName;
    private PackageInfo packageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        applicationPackageName = getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME);

        try {
            packageInfo = ApplicationHelper.getPackageInfo(applicationPackageName, this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            //ToDo implement error handling
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(ApplicationHelper.getApplicationName(packageInfo, this));
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button showRuleViolations = (Button) findViewById(R.id.app_detail_show_rules);
        Button showDataUsage = (Button) findViewById(R.id.app_detail_data_usage);
        Button showPermissionList = (Button) findViewById(R.id.app_detail_permissions);
        Button showUpdateLog = (Button) findViewById(R.id.app_detail_update_log);

        ButtonListener buttonListener = new ButtonListener();

        showRuleViolations.setOnClickListener(buttonListener);
        showDataUsage.setOnClickListener(buttonListener);
        showPermissionList.setOnClickListener(buttonListener);
        showUpdateLog.setOnClickListener(buttonListener);

        TextView privacyScore = (TextView) findViewById(R.id.app_detail_privacy_score);
        // ToDo
    }

    private class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.app_detail_show_rules) {
                Intent i = new Intent(ApplicationDetailActivity.this, RuleViolationsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.packageName);
                i.putExtras(bundle);

                startActivity(i);
            } else if (v.getId() == R.id.app_detail_permissions) {
                Intent i = new Intent(ApplicationDetailActivity.this, PermissionListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.packageName);
                i.putExtras(bundle);

                startActivity(i);
            } else if (v.getId() == R.id.app_detail_data_usage) {
//                Intent i = new Intent(ApplicationDetailActivity.this, DataUsageActivity.class);
                Intent i = new Intent(ApplicationDetailActivity.this, UserQuestionActivity.class);

//                Intent i = new Intent(Settings.);
//                PackageManager manager = getPackageManager();
//                i = manager.getLaunchIntentForPackage(applicationPackageName);
//                i.addCategory(Intent.CATEGORY_LAUNCHER);
//                startActivity(i);
                i.putExtra("packageName",applicationPackageName);
                i.putExtra("appName", ApplicationHelper.getApplicationName(packageInfo, getBaseContext()));
                startActivity(i);
            } else if (v.getId() == R.id.app_detail_update_log) {
                Intent i = new Intent(ApplicationDetailActivity.this, ApplicationUpdateLogActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.packageName);
                i.putExtras(bundle);

                startActivity(i);
            }
        }
    }
}
