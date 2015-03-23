package no.ntnu.idi.watchdogprod;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import java.util.ArrayList;

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

        try {
            packageInfo = ApplicationHelper.getPackageInfo(applicationPackageName, this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            //ToDo implement error handling
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(ApplicationHelper.getApplicationName(packageInfo, this));
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.list_permissions);
        ArrayList<PermissionDescription> permissionDescriptions = PermissionHelper.getApplicationPermissionDescriptions(packageInfo.requestedPermissions, this);
        PermissionListAdapter adapter = new PermissionListAdapter(this, permissionDescriptions);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app_permissions, menu);
        return true;
    }
}
