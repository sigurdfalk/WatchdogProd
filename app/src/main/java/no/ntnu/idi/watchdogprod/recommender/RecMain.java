package no.ntnu.idi.watchdogprod.recommender;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.AppInfo;

/**
 * Created by Wschive on 08/04/15.
 */
public class RecMain extends ActionBarActivity{
    Context context;
    String packageName;
    String appName;
    ArrayList<AppInfo> appInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_usage);

        context = getApplicationContext();

        packageName = getIntent().getExtras().getString("packageName");
        appName = getIntent().getExtras().getString("appName");
        appInfos = (ArrayList<AppInfo>) getIntent().getExtras().getSerializable("response");

    }
//        ArrayList<PermissionDescription> permissionDescriptions = PermissionHelper.getApplicationPermissionDescriptions(appInfo.getPermissions(), context);
//        int riskScore = (int) PrivacyScoreCalculator.calculateScore(permissionDescriptions);

}
