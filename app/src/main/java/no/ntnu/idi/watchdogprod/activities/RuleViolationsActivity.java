package no.ntnu.idi.watchdogprod.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.adapters.RuleListAdapter;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.Rule;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class RuleViolationsActivity extends ActionBarActivity {
    private String applicationPackageName;
    private ApplicationHelperSingleton applicationHelperSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_violations);

        applicationPackageName = getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME);
        applicationHelperSingleton = ApplicationHelperSingleton.getInstance(this.getApplicationContext());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.rule_violations_list);
        View emptyView = findViewById(R.id.rule_violations_empty);
        listView.setEmptyView(emptyView);

        ExtendedPackageInfo packageInfo = applicationHelperSingleton.getApplicationByPackageName(applicationPackageName);

        ArrayList<Rule> violatedRules = applicationHelperSingleton.getRuleHelper().getViolatedRules(packageInfo.getPackageInfo().requestedPermissions);
        Collections.sort(violatedRules);
        RuleListAdapter adapter = new RuleListAdapter(this, violatedRules);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_information_info:
                showInformationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInformationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_risk_indikators_info, null));
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // not implemented
            }
        });

        builder.create();
        builder.show();
    }
}
