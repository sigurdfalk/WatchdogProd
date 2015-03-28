package no.ntnu.idi.watchdogprod;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sigurdhf on 06.03.2015.
 */
public class ApplicationDetailActivity extends ActionBarActivity implements QuestionDialogFragment.QuestionDialogListener {
    private String applicationPackageName;
    private PackageInfo packageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

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

        Button showRuleViolations = (Button) findViewById(R.id.app_detail_show_rules);
        Button showDataUsage = (Button) findViewById(R.id.app_detail_data_usage);
        Button showPermissionList = (Button) findViewById(R.id.app_detail_permissions);
        Button showUpdateLog = (Button) findViewById(R.id.app_detail_update_log);
        Button showQuestionDialog = (Button) findViewById(R.id.app_detail_questions);

        ButtonListener buttonListener = new ButtonListener();

        showRuleViolations.setOnClickListener(buttonListener);
        showDataUsage.setOnClickListener(buttonListener);
        showPermissionList.setOnClickListener(buttonListener);
        showUpdateLog.setOnClickListener(buttonListener);
        showQuestionDialog.setOnClickListener(buttonListener);

        TextView privacyScore = (TextView) findViewById(R.id.app_detail_privacy_score);
        ExtendedPackageInfo extendedPackageInfo = ApplicationHelper.getExtendedPackageInfo(this, applicationPackageName);
        privacyScore.setText("Risikofaktor " + (int) extendedPackageInfo.getPrivacyScore() + "/" + PrivacyScoreCalculator.MAX_SCORE);
        setScoreBackgroundColor(privacyScore, extendedPackageInfo.getPrivacyScore());
        fillPermissionsCard(extendedPackageInfo);
    }

    private void fillPermissionsCard(ExtendedPackageInfo app) {
        TextView text = (TextView) findViewById(R.id.app_detail_permissions_text);

        ArrayList<PermissionDescription> permissions = app.getPermissionDescriptions();

        text.setText("Denne applikasjonen krever " + permissions.size() + " tillatelser, hvor " + getPermissionRiskCount(PrivacyScoreCalculator.RISK_HIGH, permissions) + " av disse har høy risikofaktor for ditt personvern.");
        setPermissionDiagram(permissions);
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
        params.width = getPixelsFromDp((int) width);
        diagramHigh.setLayoutParams(params);

        params = diagramMedium.getLayoutParams();
        width = (countMedium / countAll) * 100.0;
        params.width = getPixelsFromDp((int) width);
        diagramMedium.setLayoutParams(params);

        params = diagramLow.getLayoutParams();
        width = (countLow / countAll) * 100.0;
        params.width = getPixelsFromDp((int) width);
        diagramLow.setLayoutParams(params);

    }

    private int getPixelsFromDp(int dps) {
        System.out.println(dps);
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
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
                // ToDo show dialog
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showQuestionDialog() {
        QuestionDialogFragment dialog = new QuestionDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.packageName);

        dialog.setArguments(bundle);
        dialog.show(this.getFragmentManager(), "QuestionDialogFragment");
    }

    @Override
    public void onQuestionnaireFinished() {
        // ToDo implement
    }

    private void setScoreBackgroundColor(TextView textView, double score) {
        if (score > PrivacyScoreCalculator.HIGH_THRESHOLD) {
            textView.setBackgroundColor(this.getResources().getColor(R.color.risk_red));
        } else if (score > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            textView.setBackgroundColor(this.getResources().getColor(R.color.risk_yellow));
        } else {
            textView.setBackgroundColor(this.getResources().getColor(R.color.risk_green));
        }
    }

    private class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.app_detail_show_rules) {
                Intent i = new Intent(ApplicationDetailActivity.this, RuleViolationsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.packageName);
                i.putExtras(bundle);

                startActivity(i);
            } else if (v.getId() == R.id.app_detail_permissions) {
                Intent i = new Intent(ApplicationDetailActivity.this, PermissionListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.packageName);
                i.putExtras(bundle);

                startActivity(i);
            } else if (v.getId() == R.id.app_detail_data_usage) {
                Intent i = new Intent(ApplicationDetailActivity.this, DataUsageActivity.class);
//                Intent i = new Intent(ApplicationDetailActivity.this, UserQuestionActivity.class);

                i.putExtra("packageName",applicationPackageName);
                i.putExtra("appName", ApplicationHelper.getApplicationName(packageInfo, getBaseContext()));
                startActivity(i);


//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.setComponent(new ComponentName("com.android.settings",
//                        "com.android.settings.Settings$DataUsageSummaryActivity"));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            } else if (v.getId() == R.id.app_detail_update_log) {
                Intent i = new Intent(ApplicationDetailActivity.this, ApplicationUpdateLogActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.packageName);
                i.putExtras(bundle);

                startActivity(i);
            } else if (v.getId() == R.id.app_detail_questions) {
                showQuestionDialog();
            }
        }
    }
}
