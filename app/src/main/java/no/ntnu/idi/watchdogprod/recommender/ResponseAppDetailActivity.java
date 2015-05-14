package no.ntnu.idi.watchdogprod.recommender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.activities.ApplicationListActivity;
import no.ntnu.idi.watchdogprod.activities.ApplicationUpdateLogActivity;
import no.ntnu.idi.watchdogprod.activities.PermissionListActivity;
import no.ntnu.idi.watchdogprod.activities.RuleViolationsActivity;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.domain.Rule;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelperSingleton;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.sqlite.answers.AnswersDataSource;

/**
 * Created by Wschive on 30/04/15.
 */
public class ResponseAppDetailActivity extends ActionBarActivity {
    public static final String FROM_NOTIFICATION = "fromNotification";

    private String applicationPackageName;
    private static final String TAG = "ResponseDetailActivity";

    private ResponseApp app;

    private ApplicationHelperSingleton applicationHelperSingleton;
    private PrivacyScoreCalculator privacyScoreCalculator;

    private TextView infoHeader;
    private TextView infoFact;
    private int currentPermissionFact;
    private LinearLayout install;

    private AnswersDataSource answersDataSource;

    private double riskScore;

    private LinearLayout riskScoreBackground;
    private TextView riskScoreText;
    private ImageView riskScoreIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);
        answersDataSource = new AnswersDataSource(this);
        applicationHelperSingleton = ApplicationHelperSingleton.getInstance(this.getApplicationContext());


        app = jsonToResponseApp(getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME));
        currentPermissionFact = 0;


        riskScore = app.getPrivacyScore();

        riskScoreBackground = (LinearLayout) findViewById(R.id.app_detail_privacy_score_background);
        riskScoreText = (TextView) findViewById(R.id.app_detail_privacy_score);
        riskScoreIndicator = (ImageView) findViewById(R.id.app_detail_privacy_score_indicator);

        //riskScoreText.setText("Risikofaktor " + (int) packageInfo.getPrivacyScore() + "/" + PrivacyScoreCalculator.MAX_SCORE);
        riskScoreText.setText("Risikofaktor " + (int) riskScore + "/" + PrivacyScoreCalculator.MAX_SCORE);
        setScoreBackgroundColor(riskScoreBackground, riskScore);

        ButtonListener buttonListener = new ButtonListener();

        Button install = (Button) findViewById(R.id.app_detail_install);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(app.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);


        LinearLayout permissionsInfoWrapper = (LinearLayout) findViewById(R.id.app_detail_permissions_wrapper);
        LinearLayout indicatorsWrapper = (LinearLayout) findViewById(R.id.app_detail_indicators_wrapper);

        infoHeader = (TextView) findViewById(R.id.permission_fact_header);
        infoFact = (TextView) findViewById(R.id.permission_fact_fact);



        permissionsInfoWrapper.setOnClickListener(buttonListener);
        indicatorsWrapper.setOnClickListener(buttonListener);
        install.setOnClickListener(buttonListener);


        fillPermissionsCard();
        fillIndicatorsCard();
        hideUseless();
    }
    private void hideUseless(){
        LinearLayout updatesInfoWrapper = (LinearLayout) findViewById(R.id.app_detail_updates_wrapper);
        updatesInfoWrapper.setVisibility(View.GONE);

        View permissionFactWrapper = findViewById(R.id.permission_fact_wrapper);
        permissionFactWrapper.setVisibility(View.GONE);

        Button replace = (Button) findViewById(R.id.app_detail_replace);
        replace.setVisibility(View.GONE);

        ImageView warningSign = (ImageView) findViewById(R.id.uninstallX);
        warningSign.setVisibility(View.GONE);

    }
    private ResponseApp jsonToResponseApp(String jsonString){
        Gson gson = new Gson();
        ResponseApp app = gson.fromJson(jsonString, ResponseApp.class);

        return app;
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

        builder.setView(inflater.inflate(R.layout.dialog_response_detail_info, null));
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // not implemented
            }
        });

        builder.create();
        builder.show();
    }
    private void fillPermissionsCard() {
        TextView text = (TextView) findViewById(R.id.app_detail_permissions_text);
        PermissionHelperSingleton permissionHelper = PermissionHelperSingleton.getInstance(getApplicationContext());
        ArrayList<PermissionDescription> permissions = permissionHelper.parseArray(app.getPermissions());

        text.setText("Denne applikasjonen krever " + permissions.size() + " tillatelser, hvor " + getPermissionRiskCount(PrivacyScoreCalculator.RISK_HIGH, permissions) + " av disse har høy risikofaktor for ditt personvern.");
        setPermissionDiagram(permissions);
    }

    private void fillIndicatorsCard() {
        TextView text = (TextView) findViewById(R.id.app_detail_indicators_text);
        TextView count = (TextView) findViewById(R.id.app_detail_indicators_count);
        ArrayList<Rule> indicators = app.getViolatedRules();

        String stringCount = null;

        if (indicators.size() == 0) {
            stringCount = "ingen";
            count.setBackgroundColor(getResources().getColor(R.color.risk_green));
        } else {
            stringCount = Integer.toString(indicators.size());
            count.setBackgroundColor(getResources().getColor(R.color.risk_red));
        }

        count.setText(Integer.toString(indicators.size()));
        text.setText("Denne applikasjonen har " + stringCount + " indikatorer på farlig adferd");
    }

    private void setScoreBackgroundColor(LinearLayout background, double score) {
        if (score > PrivacyScoreCalculator.HIGH_THRESHOLD) {
            background.setBackgroundColor(this.getResources().getColor(R.color.risk_red));
        } else if (score > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            background.setBackgroundColor(this.getResources().getColor(R.color.risk_yellow));
        } else {
            background.setBackgroundColor(this.getResources().getColor(R.color.risk_green));
        }
    }
    private void setPermissionDiagram(ArrayList<PermissionDescription> permissions) {
        TextView diagramHigh = (TextView) findViewById(R.id.app_detail_permissions_high);
        TextView diagramMedium = (TextView) findViewById(R.id.app_detail_permissions_medium);
        TextView diagramLow = (TextView) findViewById(R.id.app_detail_permissions_low);

        double countHigh = getPermissionRiskCount(PrivacyScoreCalculator.RISK_HIGH, permissions);
        double countMedium = getPermissionRiskCount(PrivacyScoreCalculator.RISK_MEDIUM, permissions);
        double countLow = getPermissionRiskCount(PrivacyScoreCalculator.RISK_LOW, permissions);
        double countAll = permissions.size();

        ViewGroup.LayoutParams params = diagramHigh.getLayoutParams();
        double width = (countHigh / countAll) * 100.0;
        int pix = getPixelsFromDp((int) width);
        params.height = (pix != 0) ? pix : 2;
        diagramHigh.setLayoutParams(params);

        params = diagramMedium.getLayoutParams();
        width = (countMedium / countAll) * 100.0;
        pix = getPixelsFromDp((int) width);
        params.height = (pix != 0) ? pix : 2;
        diagramMedium.setLayoutParams(params);

        params = diagramLow.getLayoutParams();
        width = (countLow / countAll) * 100.0;
        pix = getPixelsFromDp((int) width);
        params.height = (pix != 0) ? pix : 2;
        diagramLow.setLayoutParams(params);

    }
    private int getPermissionRiskCount(int riskLevel, ArrayList<PermissionDescription> permissions) {
        int count = 0;

        for (PermissionDescription permissionDescription : permissions) {
            if (permissionDescription.getRisk() == riskLevel) {
                count++;
            }
        }

        return count;
    }
    private int getPixelsFromDp(int dps) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
    private class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.app_detail_indicators_wrapper) {
                Intent i = new Intent(ResponseAppDetailActivity.this, RuleViolationsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putStringArray("permissions", app.getPermissions());
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, app.getName());
                i.putExtras(bundle);

                startActivity(i);
            } else if (v.getId() == R.id.app_detail_permissions_wrapper) {
                Intent i = new Intent(ResponseAppDetailActivity.this, PermissionListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, app.getName());
                bundle.putStringArray("permissions", app.getPermissions());
                i.putExtras(bundle);

                startActivity(i);

            } else if (v.getId() == R.id.app_detail_install){
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app.getPackageName())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app.getPackageName())));
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Connect to the internet to install", Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
