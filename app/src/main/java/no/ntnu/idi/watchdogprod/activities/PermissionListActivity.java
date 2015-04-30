package no.ntnu.idi.watchdogprod.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelperSingleton;
import no.ntnu.idi.watchdogprod.adapters.PermissionListAdapter;
import no.ntnu.idi.watchdogprod.R;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class PermissionListActivity extends ActionBarActivity {
    private String applicationPackageName;
    private ExtendedPackageInfo packageInfo;
    private ApplicationHelperSingleton applicationHelperSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_list);

        applicationPackageName = getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME);
        applicationHelperSingleton = ApplicationHelperSingleton.getInstance(this.getApplicationContext());

        ListView listView = (ListView) findViewById(R.id.list_permissions);
        View emptyView = findViewById(R.id.list_item_permissions_empty);
        listView.setEmptyView(emptyView);

        if(applicationPackageName.equals(PermissionHelperSingleton.ALL_PERMISSIONS_KEY)) {

            ArrayList<PermissionDescription> permissionDescriptions = applicationHelperSingleton.getPermissionHelper().getPermissionDescriptions();
            Collections.sort(permissionDescriptions);
            PermissionListAdapter adapter = new PermissionListAdapter(this,permissionDescriptions);
            listView.setAdapter(adapter);

        } else {
            ArrayList<PermissionDescription> permissionDescriptions;
            try {
                packageInfo = applicationHelperSingleton.getApplicationByPackageName(applicationPackageName);
                permissionDescriptions = applicationHelperSingleton.getPermissionHelper().getApplicationPermissionDescriptions(packageInfo.getPackageInfo().requestedPermissions);
            } catch (Exception e) {
                permissionDescriptions = applicationHelperSingleton.getPermissionHelper().parseArray(getIntent().getStringArrayExtra("permissions"));
            }

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            Collections.sort(permissionDescriptions);
            PermissionListAdapter adapter = new PermissionListAdapter(this, permissionDescriptions);
            listView.setAdapter(adapter);
        }
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

        builder.setView(inflater.inflate(R.layout.dialog_permission_information, null));
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
            }
        });

        builder.create();
        builder.show();
    }
}
