package no.ntnu.idi.watchdogprod.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelper;
import no.ntnu.idi.watchdogprod.adapters.ApplicationListAdapter;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelper;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;

/**
 * Created by sigurdhf on 05.03.2015.
 */
public class ApplicationListActivity extends ActionBarActivity {
    public static final String PACKAGE_NAME = "packageName";

    private RecyclerView list;
    private ApplicationListAdapter adapter;

    private View popupView;
    private PopupWindow popupWindow;

    private ArrayList<ExtendedPackageInfo> apps;
    private ArrayList<ExtendedPackageInfo> filteredApps;
    private ArrayList<CheckBox> permissionsCheckBoxes;

    private Button showFilteredApplications;
    private Button clearCheckBoxes;

    private CheckBox filterRiskHigh;
    private CheckBox filterRiskMedium;
    private CheckBox filterRiskLow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        apps = ApplicationHelper.getThirdPartyApplications(this);
        permissionsCheckBoxes = new ArrayList<>();
        filteredApps = new ArrayList<>();

        list = (RecyclerView)findViewById(R.id.applications_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setItemAnimator(new DefaultItemAnimator());

        adapter = new ApplicationListAdapter(this, apps);
        list.setAdapter(adapter);

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.activity_application_list_search_popup, null, false);
        filterRiskHigh = (CheckBox) popupView.findViewById(R.id.search_popup_checkbox_risk_high);
        filterRiskMedium = (CheckBox) popupView.findViewById(R.id.search_popup_checkbox_risk_medium);
        filterRiskLow = (CheckBox) popupView.findViewById(R.id.search_popup_checkbox_risk_low);
        showFilteredApplications = (Button) popupView.findViewById(R.id.search_popup_show_filtered_apps);
        showFilteredApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new ApplicationListAdapter(ApplicationListActivity.this, filteredApps);
                list.setAdapter(adapter);
                popupWindow.dismiss();
                printAppList(filteredApps);
            }
        });
        clearCheckBoxes = (Button) popupView.findViewById(R.id.search_popup_clear_checkboxes);
        clearCheckBoxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllPermissionCheckBoxes();
            }
        });
        populateSearchPopupWindowPermissionCheckboxes(popupView);
    }

    private void clearAllPermissionCheckBoxes() {
        for (CheckBox checkBox : permissionsCheckBoxes) {
            checkBox.setChecked(false);
        }
    }

    private void showSearchPopupWindow(View v) {
        showFilteredApplications.setText(String.format(getResources().getString(R.string.show_filteres_applications_count), filteredApps.size()));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        popupWindow = new PopupWindow(popupView, size.x,size.y - getSupportActionBar().getHeight(), true );
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.AnimationPopup);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    private void populateSearchPopupWindowPermissionCheckboxes(View view) {
        LinearLayout wrapper = (LinearLayout) view.findViewById(R.id.search_popup_permission_checkbox_wrapper);
        ArrayList<PermissionDescription> permissions = PermissionHelper.getAllPermissionDescriptions(this);

        for (PermissionDescription permission : permissions) {
            if (permission.getRisk() == PrivacyScoreCalculator.RISK_HIGH) {
                CheckBox cb = new CheckBox(this);
                cb.setText(permission.getDesignation());
                cb.setTextColor(getResources().getColor(R.color.text_secondary));
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        filterApps();
                    }
                });
                permissionsCheckBoxes.add(cb);
                wrapper.addView(cb);
            }
        }
    }

    private void printAppList(ArrayList<ExtendedPackageInfo> applications) {
        for (ExtendedPackageInfo extendedPackageInfo : applications) {
            System.out.println(extendedPackageInfo.getPackageInfo().packageName);
        }
    }

    private void filterAppsByPermissions() {
        ArrayList<String> checkedPermissions = new ArrayList<>();

        for (CheckBox cb : permissionsCheckBoxes) {
            if (cb.isChecked()) {
                checkedPermissions.add(cb.getText().toString());
            }
        }

        for (ExtendedPackageInfo app : apps) {
            ArrayList<String> reqPermissions = new ArrayList<>();

            for (PermissionDescription reqPermission : app.getPermissionDescriptions()) {
                reqPermissions.add(reqPermission.getDesignation());
            }

            if (!reqPermissions.containsAll(checkedPermissions)) {
                filteredApps.remove(app);
            }
        }
    }

    private void filterAppsByRiskFactor() {
        for (ExtendedPackageInfo app : apps) {
            boolean remove = true;

            if (filterRiskHigh.isChecked() && getAppRiskFactor(app) == PrivacyScoreCalculator.RISK_HIGH) {
                remove = false;
            }

            if (filterRiskMedium.isChecked() && getAppRiskFactor(app) == PrivacyScoreCalculator.RISK_MEDIUM) {
                remove = false;
            }

            if (filterRiskLow.isChecked() && getAppRiskFactor(app) == PrivacyScoreCalculator.RISK_LOW) {
                remove = false;
            }

            if (!filterRiskHigh.isChecked() && !filterRiskMedium.isChecked() && !filterRiskLow.isChecked()) {
                remove = false;
            }

            if (remove) {
                filteredApps.remove(app);
            }
        }
    }

    private int getAppRiskFactor(ExtendedPackageInfo app) {
        if (app.getPrivacyScore() > PrivacyScoreCalculator.HIGH_THRESHOLD) {
            return PrivacyScoreCalculator.RISK_HIGH;
        } else if (app.getPrivacyScore() > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            return PrivacyScoreCalculator.RISK_MEDIUM;
        } else {
            return PrivacyScoreCalculator.RISK_LOW;
        }
    }

    private void filterApps() {
        filteredApps = (ArrayList<ExtendedPackageInfo>) apps.clone();

        filterAppsByPermissions();
        filterAppsByRiskFactor();

        if (filteredApps.size() > 0) {
            showFilteredApplications.setEnabled(true);
            showFilteredApplications.setText(String.format(getResources().getString(R.string.show_filteres_applications_count), filteredApps.size()));
        } else {
            showFilteredApplications.setEnabled(false);
            showFilteredApplications.setText(getResources().getString(R.string.no_matches));
        }
    }

    public void onCheckboxClicked(View view) {
        switch(view.getId()) {
            case R.id.search_popup_checkbox_risk_high:
                filterApps();
                break;
            case R.id.search_popup_checkbox_risk_medium:
                filterApps();
                break;
            case R.id.search_popup_checkbox_risk_low:
                filterApps();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_applications, menu);

       // SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
       // searchView.setQueryHint(this.getString(R.string.search));

        return true;
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            clearAllPermissionCheckBoxes();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchPopupWindow(getWindow().getDecorView().findViewById(android.R.id.content));
                return true;
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

        builder.setView(inflater.inflate(R.layout.dialog_appliaction_list_info, null));
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // not implemented
            }
        });

        builder.create();
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
