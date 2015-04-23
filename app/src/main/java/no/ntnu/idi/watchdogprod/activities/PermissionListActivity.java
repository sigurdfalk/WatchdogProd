package no.ntnu.idi.watchdogprod.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import no.ntnu.idi.watchdogprod.helpers.ApplicationHelper;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelper;
import no.ntnu.idi.watchdogprod.adapters.PermissionListAdapter;
import no.ntnu.idi.watchdogprod.R;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class PermissionListActivity extends ActionBarActivity {
    private String applicationPackageName;
    private PackageInfo packageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_list);

        applicationPackageName = getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME);

        ListView listView = (ListView) findViewById(R.id.list_permissions);
        View emptyView = findViewById(R.id.list_item_permissions_empty);
        listView.setEmptyView(emptyView);

        if(applicationPackageName.equals(PermissionHelper.ALL_PERMISSIONS_KEY)) {

            ArrayList<PermissionDescription> permissionDescriptions = PermissionHelper.getAllPermissionDescriptions(this);
            PermissionListAdapter adapter = new PermissionListAdapter(this,permissionDescriptions);
            listView.setAdapter(adapter);

        } else {


            try {
                packageInfo = ApplicationHelper.getPackageInfo(applicationPackageName, this);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //ToDo implement error handling
            }

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            ArrayList<PermissionDescription> permissionDescriptions = PermissionHelper.getApplicationPermissionDescriptions(packageInfo.requestedPermissions, this);
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
