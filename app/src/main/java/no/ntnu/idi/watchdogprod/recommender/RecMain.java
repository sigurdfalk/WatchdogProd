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
import android.view.LayoutInflater;
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
import no.ntnu.idi.watchdogprod.domain.Rule;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelperSingleton;
import no.ntnu.idi.watchdogprod.helpers.RuleHelperSingleton;
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
    double originalRisk;
    PermissionHelperSingleton permissionHelper;
    PrivacyScoreCalculator privacyScoreCalculator;
    RuleHelperSingleton ruleHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();


        setContentView(R.layout.activity_application_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.applications_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        permissionHelper = PermissionHelperSingleton.getInstance(context);
        privacyScoreCalculator = PrivacyScoreCalculator.getInstance(context);
        ruleHelper = RuleHelperSingleton.getInstance(context);

        packageName = getIntent().getExtras().getString("packageName");
        appName = getIntent().getExtras().getString("appName");

        apps = stringToArrayList((String) getIntent().getExtras().getSerializable("response"));
        profile = Profile.getInstance();
        originalRisk = getIntent().getExtras().getDouble("originalScore");
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

    private void purgeBadApps() {
        double score = 99999;
        ArrayList<ResponseApp> temp = new ArrayList<ResponseApp>();
        for (ResponseApp app : apps) {
            ArrayList<PermissionDescription> permissionDescriptions = permissionHelper.parseArray(app.getPermissions());
            ArrayList<Rule> rules = ruleHelper.getViolatedRules(app.getPermissions());
            score = privacyScoreCalculator.calculatePrivacyScore(permissionDescriptions, rules);
            String name = app.getName();
            app.setPrivacyScore(score);
            if(originalRisk < score){
                temp.add(app);
            }
            else{
                app.setViolatedRules(rules);
                app.setPrivacyScore(score);
            }
        }
        this.apps.removeAll(temp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_response, menu);


        return true;
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_applications_info:
                showInformationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInformationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_response_list_info, null));
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // not implemented
            }
        });

        builder.create();
        builder.show();
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
