package no.ntnu.idi.watchdogprod;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by sigurdhf on 05.03.2015.
 */
public class ApplicationListActivity extends ActionBarActivity {
    public static final String PACKAGE_NAME = "packageName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final ApplicationListAdapter adapter = new ApplicationListAdapter(this, ApplicationHelper.getThirdPartyApplications(this));
        ListView listView = (ListView) findViewById(R.id.applications_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PackageInfo packageInfo = adapter.getItem(position);
                Intent i = new Intent(ApplicationListActivity.this, ApplicationDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(PACKAGE_NAME, packageInfo.packageName);
                i.putExtras(bundle);

                startActivity(i);
            }
        });
    }
}
