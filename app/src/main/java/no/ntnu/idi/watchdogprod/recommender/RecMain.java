package no.ntnu.idi.watchdogprod.recommender;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.RandomAccessSparseVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.adapters.ApplicationListAdapter;
import no.ntnu.idi.watchdogprod.adapters.ResponseListAdapter;
import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;

/**
 * Created by Wschive on 08/04/15.
 */
public class RecMain extends ActionBarActivity{
    Context context;
    String packageName;
    String appName;
    ArrayList<ResponseApp> apps;
    Profile profile;
    private ResponseListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.applications_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        packageName = getIntent().getExtras().getString("packageName");
        appName = getIntent().getExtras().getString("appName");
        apps = (ArrayList<ResponseApp>) getIntent().getExtras().getSerializable("response");
        profile = Profile.getInstance();
        Collections.sort(apps);
        adapter = new ResponseListAdapter(this,apps);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_applications, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(this.getString(R.string.search));

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


//        ArrayList<PermissionDescription> permissionDescriptions = PermissionHelper.getApplicationPermissionDescriptions(appInfo.getPermissions(), context);
//        int riskScore = (int) PrivacyScoreCalculator.calculateScore(permissionDescriptions);

}
