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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;

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

    private ApplicationListAdapter adapter;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ArrayList<ExtendedPackageInfo> apps = ApplicationHelper.getThirdPartyApplications(this);

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.applications_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new ApplicationListAdapter(this, apps);
        mRecyclerView.setAdapter(adapter);
    }

    private void showSearchPopupWindow(View v) {
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflatedView = layoutInflater.inflate(R.layout.activity_application_list_search_popup, null, false);
        populateSearchPopupWindowPermissionCheckboxes(inflatedView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        popupWindow = new PopupWindow(inflatedView, size.x,size.y - getSupportActionBar().getHeight(), true );
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
                wrapper.addView(cb);
            }
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
        System.out.println("on back pressed");
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
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
                // sign in the user ...
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
