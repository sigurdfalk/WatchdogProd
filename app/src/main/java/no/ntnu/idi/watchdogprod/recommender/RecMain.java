package no.ntnu.idi.watchdogprod.recommender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.adapters.ApplicationListAdapter;
import no.ntnu.idi.watchdogprod.adapters.ResponseListAdapter;
import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelper;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;

/**
 * Created by Wschive on 08/04/15.
 */
public class RecMain extends ActionBarActivity{
    private final String TAG = "recmain";
    Context context;
    String packageName;
    String appName;
    ArrayList<ResponseApp> apps;
    Profile profile;
    private ResponseListAdapter adapter;
    int originalRisk;


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

        apps = stringToArrayList((String) getIntent().getExtras().getSerializable("response"));
        profile = Profile.getInstance();
        originalRisk = getIntent().getExtras().getInt("originalScore");
        purgeBadApps();
        Collections.sort(apps);
        if(apps.isEmpty()){
            DialogFragment dialog = new NoReplacementsDialog();
            dialog.show(getFragmentManager(), "noBetterApps");
        }else{
            adapter = new ResponseListAdapter(this,apps);
            mRecyclerView.setAdapter(adapter);
        }

    }
    public ArrayList<PermissionDescription> parseArray(String[] array){
        ArrayList<PermissionDescription> list = new ArrayList<>();
        for (String s : array) {

            try {
                list.add(PermissionHelper.getPermissionDescription(context, s));
            } catch (Exception e) {
                Log.e(TAG, "Permissiondescription of permission " + s + " do not exist!");
            }
        }
        return list;
    }
    private void purgeBadApps() {
        double score = 99999;
        ArrayList<ResponseApp> temp = new ArrayList<ResponseApp>();
        for (ResponseApp app : apps) {
            ArrayList<PermissionDescription> permissionDescriptions = parseArray(app.getPermissions());
            score = PrivacyScoreCalculator.calculateScore(permissionDescriptions);
            String name = app.getName();
            app.setPrivacyScore(score);
            if(originalRisk < score){
                temp.add(app);
            }
            else{
                app.setPrivacyScore(score);
            }
        }
        this.apps.removeAll(temp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_applications, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ArrayList<ResponseApp> stringToArrayList(String jsonString){
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<ResponseApp>>(){}.getType();
        ArrayList<ResponseApp> posts = gson.fromJson(jsonString, collectionType);
        for (ResponseApp app : posts) {
            app.calculate();
        }
        return posts;
    }
    public class NoReplacementsDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.noReplacements)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            return builder.create();
        }
    }

}
