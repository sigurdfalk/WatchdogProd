package no.ntnu.idi.watchdogprod.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.domain.AppInfo;

/**
 * Created by sigurdhf on 10.03.2015.
 */
public class ApplicationUpdateLogListAdapter extends ArrayAdapter<AppInfo> {
    private final Context context;
    private final ArrayList<AppInfo> objects;

    public ApplicationUpdateLogListAdapter(Context context, ArrayList<AppInfo> objects) {
        super(context, R.layout.list_item_application_update_log, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_application_update_log, parent, false);


        TextView date = (TextView) convertView.findViewById(R.id.item_update_log_date);
        TextView time = (TextView) convertView.findViewById(R.id.item_update_log_time);
        TextView version = (TextView) convertView.findViewById(R.id.item_update_log_version);
        TextView riskFactor = (TextView) convertView.findViewById(R.id.item_update_log_risk_factor);
        LinearLayout addedPermissionsView = (LinearLayout) convertView.findViewById(R.id.item_update_log_container_added);
        LinearLayout removedPermissionsView = (LinearLayout) convertView.findViewById(R.id.item_update_log_container_removed);

        AppInfo appInfo = objects.get(position);
        ApplicationHelperSingleton applicationHelperSingleton = ApplicationHelperSingleton.getInstance(context.getApplicationContext());

        int riskScore = (int) applicationHelperSingleton.getApplicationByPackageName(appInfo.getPackageName()).getPrivacyScore();

        if (position == objects.size() - 1 || objects.size() == 1) {
            addedPermissionsView.setVisibility(View.GONE);
            removedPermissionsView.setVisibility(View.GONE);
        } else {
            AppInfo oldAppInfo = objects.get(position + 1);
            ArrayList<PermissionDescription> newPermissions = applicationHelperSingleton.getPermissionHelper().newRequestedPermissions(oldAppInfo.getPermissions(), appInfo.getPermissions());
            ArrayList<PermissionDescription> removedPermissions = applicationHelperSingleton.getPermissionHelper().removedPermissions(oldAppInfo.getPermissions(), appInfo.getPermissions());

            if (newPermissions.size() > 0) {

                ViewGroup viewGroup = (ViewGroup) convertView.findViewById(R.id.item_update_log_container_added_inner);

                for (PermissionDescription newPermission : newPermissions) {
                    View permissionView = inflater.inflate(R.layout.list_item_application_update_log_permission, parent, false);
                    TextView name = (TextView) permissionView.findViewById(R.id.update_log_permission_text);
                    ImageView colorCode = (ImageView) permissionView.findViewById(R.id.update_log_permission_color);
                    setImageColorFromRiskLevel(colorCode, newPermission);
                    name.setText(newPermission.getDesignation());
                    viewGroup.addView(permissionView);
                }
            } else {
                addedPermissionsView.setVisibility(View.GONE);
            }

            if (removedPermissions.size() > 0) {

                ViewGroup viewGroup = (ViewGroup) convertView.findViewById(R.id.item_update_log_container_removed_inner);

                for (PermissionDescription removedPermission : removedPermissions) {
                    View permissionView = inflater.inflate(R.layout.list_item_application_update_log_permission, parent, false);
                    TextView name = (TextView) permissionView.findViewById(R.id.update_log_permission_text);
                    ImageView colorCode = (ImageView) permissionView.findViewById(R.id.update_log_permission_color);
                    setImageColorFromRiskLevel(colorCode, removedPermission);
                    name.setText(removedPermission.getDesignation());
                    viewGroup.addView(permissionView);
                }
            } else {
                removedPermissionsView.setVisibility(View.GONE);
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        date.setText(dateFormat.format(new Date(appInfo.getLastUpdateTime())));
        time.setText(timeFormat.format(new Date(appInfo.getLastUpdateTime())));
        version.setText("Versjon " + appInfo.getVersionCode());
        riskFactor.setText("Risikofaktor " + riskScore + "/" + PrivacyScoreCalculator.MAX_SCORE);

        return convertView;
    }

    private void setImageColorFromRiskLevel(ImageView imageView, PermissionDescription permissionDescription) {

        switch (permissionDescription.getRisk()) {
            case PrivacyScoreCalculator.RISK_LOW:
                imageView.setBackgroundColor(context.getResources().getColor(R.color.risk_green));
                break;
            case PrivacyScoreCalculator.RISK_MEDIUM:
                imageView.setBackgroundColor(context.getResources().getColor(R.color.risk_yellow));
                break;
            case PrivacyScoreCalculator.RISK_HIGH:
                imageView.setBackgroundColor(context.getResources().getColor(R.color.risk_red));
                break;
        }
    }
}
