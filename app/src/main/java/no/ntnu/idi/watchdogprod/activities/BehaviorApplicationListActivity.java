package no.ntnu.idi.watchdogprod.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PermissionGroupInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.sql.SQLException;
import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.AnalyticsHelper;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.adapters.BehaviorApplicationListAdapter;
import no.ntnu.idi.watchdogprod.domain.Answer;
import no.ntnu.idi.watchdogprod.domain.DisharomyApplication;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.domain.PermissionFact;
import no.ntnu.idi.watchdogprod.domain.PermissionAnswerPair;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;
import no.ntnu.idi.watchdogprod.sqlite.answers.AnswersDataSource;

public class BehaviorApplicationListActivity extends ActionBarActivity {
    private DisharomyApplication disharomyApplication;
    private Context context;

    ApplicationHelperSingleton applicationHelperSingleton;

    public static int APP_DELETED_CODE = 2017;

    private BehaviorApplicationListAdapter adapter;
    private ListView listView;

    private ArrayList<DisharomyApplication> disharomyApplications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior_application_list);

        context = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        applicationHelperSingleton = ApplicationHelperSingleton.getInstance(this.getApplicationContext());

        listView = (ListView) findViewById(R.id.list_disharmony);

        ArrayList<Answer> answers = getAllAnswers();
        ArrayList<ExtendedPackageInfo> extendedPackageInfos = applicationHelperSingleton.getApplications();

        disharomyApplications = createHarmonyApps(answers, extendedPackageInfos);

        adapter = new BehaviorApplicationListAdapter(this, disharomyApplications);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                disharomyApplication = disharomyApplications.get(position);
                showHarmonyDialog();
            }
        });

        // Get tracker.
        Tracker t = ((AnalyticsHelper) getApplication()).getTracker(
                AnalyticsHelper.TrackerName.APP_TRACKER);
    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(BehaviorApplicationListActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(BehaviorApplicationListActivity.this).reportActivityStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_behavior_application_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_behavior_info:
                showInformationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<PermissionAnswerPair> createQuestionAnswerPairs (String packageName, ArrayList<Answer> answers) {
        ArrayList<PermissionAnswerPair> questionAnswerPairs = new ArrayList<>();
        ArrayList<PermissionFact> permissionFacts = applicationHelperSingleton.getPermissionFactHelper().getPermissionFacts();

        for (Answer answer : answers) {
            if (packageName.equals(answer.getPackageName())) {
                for (PermissionFact permissionFact : permissionFacts) {
                    if (answer.getAnswerId() == permissionFact.getId()) {
                        String permission = permissionFact.getPermissions()[0];
                        PermissionDescription permissionDescription = applicationHelperSingleton.getPermissionHelper().getPermissionDescription(permission);
                        questionAnswerPairs.add(new PermissionAnswerPair(permissionDescription.getDesignation(), answer.getAnswer()));
                    }
                }
            }
        }
            return questionAnswerPairs;
    }

    private ArrayList<DisharomyApplication> createHarmonyApps(ArrayList<Answer> answers, ArrayList<ExtendedPackageInfo> extendedPackageInfos) {
        ArrayList<DisharomyApplication> disharomyApplications = new ArrayList<>();

        for(ExtendedPackageInfo extendedPackageInfo : extendedPackageInfos) {
            if(hasAnsweredQuestions(extendedPackageInfo, answers)) {
                disharomyApplications.add(new DisharomyApplication(extendedPackageInfo,createQuestionAnswerPairs(extendedPackageInfo.getPackageInfo().packageName,answers)));
            }
        }
        return disharomyApplications;
    }


    private boolean hasAnsweredQuestions(ExtendedPackageInfo extendedPackageInfo, ArrayList<Answer> answers) {
        for (Answer answer : answers) {
            if(answer.getPackageName().equals(extendedPackageInfo.getPackageInfo().packageName)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Answer> getAllAnswers() {
        ArrayList<Answer> answers = new ArrayList<>();
        AnswersDataSource answersDataSource = new AnswersDataSource(this);
        try {
            answersDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        answers = answersDataSource.getAllAnswers();
        answersDataSource.close();
        return answers;
    }

    private void showHarmonyDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_harmony_app, null);
        LinearLayout redLayout = (LinearLayout)dialogView.findViewById(R.id.harmony_dialog_color_red);
        LinearLayout yellowLayout = (LinearLayout) dialogView.findViewById(R.id.harmony_dialog_color_yellow);
        LinearLayout greenLayout = (LinearLayout) dialogView.findViewById(R.id.harmony_dialog_color_green);

        TextView haromy_dialog_feedback_title = (TextView)dialogView.findViewById(R.id.haromy_dialog_feedback);
        haromy_dialog_feedback_title.setText(getResources().getString(R.string.harmony_dialog_feedback_text) + " til " + applicationHelperSingleton.getApplicationName(context,disharomyApplication.getExtendedPackageInfo().getPackageInfo()) + "s tillatelser:");

        TextView redPermissions =(TextView) dialogView.findViewById(R.id.red_permissions);
        TextView yellowPermissions =(TextView) dialogView.findViewById(R.id.yellow_permissions);
        TextView greenPermissions =(TextView) dialogView.findViewById(R.id.green_permissions);

        ArrayList<PermissionAnswerPair> permissionAnswerPairs = disharomyApplication.getQuestionAnswerPairs();

        StringBuilder redPermissionsList = new StringBuilder();
        StringBuilder yellowPermissionsList = new StringBuilder();
        StringBuilder greenPermissionsList = new StringBuilder();

        for(PermissionAnswerPair permissionAnswerPair : permissionAnswerPairs) {
            if(permissionAnswerPair.getAnswer() == Answer.ANSWER_SAD) {
                redLayout.setVisibility(View.VISIBLE);
                redPermissionsList.append("- " + permissionAnswerPair.getPermissionDesignation() +"\n");
            } else if(permissionAnswerPair.getAnswer() == Answer.ANSWER_NEUTRAL) {
                yellowLayout.setVisibility(View.VISIBLE);
                yellowPermissionsList.append("- " + permissionAnswerPair.getPermissionDesignation()+"\n");
            } else if(permissionAnswerPair.getAnswer() == Answer.ANSWER_HAPPY) {
                greenLayout.setVisibility(View.VISIBLE);
                greenPermissionsList.append("- " + permissionAnswerPair.getPermissionDesignation()+"\n");
            }
        }

        redPermissions.setText(redPermissionsList.toString());
        yellowPermissions.setText(yellowPermissionsList.toString());
        greenPermissions.setText(greenPermissionsList.toString());

        builder.setView(dialogView);
        builder.setPositiveButton(R.string.go_to_app, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(context, ApplicationDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, disharomyApplication.getExtendedPackageInfo().getPackageInfo().packageName);
                i.putExtras(bundle);
                ((Activity) context).startActivityForResult(i, APP_DELETED_CODE);
//                context.startActivity(i);
            }
        });

        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.create();
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BehaviorApplicationListActivity.APP_DELETED_CODE) {

            String deletedPackage = "";

            if(data != null) {
                deletedPackage = data.getExtras().getString(ApplicationDetailActivity.APP_DELETED_INTENT_KEY, "");

                if (!deletedPackage.equals("")) {
                    removeApplication(deletedPackage);
                    applicationHelperSingleton.removeApplication(deletedPackage);
                    adapter = new BehaviorApplicationListAdapter(this, disharomyApplications);
                    listView.setAdapter(adapter);
                }
            }
        }
    }

    public void removeApplication(String deletedPackageName) {
        for(DisharomyApplication disharomyApplication : disharomyApplications) {
            if(disharomyApplication.getExtendedPackageInfo().getPackageInfo().packageName.equals(deletedPackageName)) {
                disharomyApplications.remove(disharomyApplication);
                break;
            }
        }
    }

    private void showInformationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_behavior_info_layout, null));
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
