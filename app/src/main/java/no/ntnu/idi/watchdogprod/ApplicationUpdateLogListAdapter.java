package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.AppInfo;

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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_application_update_log, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.item_update_log_date);
        TextView version = (TextView) convertView.findViewById(R.id.item_update_log_version);
        LinearLayout addedPermissionsView = (LinearLayout) convertView.findViewById(R.id.item_update_log_container_added);
        LinearLayout removedPermissionsView = (LinearLayout) convertView.findViewById(R.id.item_update_log_container_removed);

        AppInfo appInfo = objects.get(position);

        if (position == objects.size() - 1 || objects.size() == 1) {
            addedPermissionsView.setVisibility(View.GONE);
            removedPermissionsView.setVisibility(View.GONE);
        } else {
            AppInfo oldAppInfo = objects.get(position + 1);
            ArrayList<String> newPermissions = PermissionHelper.newRequestedPermissions(oldAppInfo.getPermissions(), appInfo.getPermissions());
            ArrayList<String> removedPermissions = PermissionHelper.removedPermissions(oldAppInfo.getPermissions(), appInfo.getPermissions());

            if (newPermissions.size() > 0) {
                TextView newPermissionsTextView = (TextView) convertView.findViewById(R.id.item_update_log_added_permissions);
                newPermissionsTextView.setText(Arrays.toString(newPermissions.toArray()));
            } else {
                addedPermissionsView.setVisibility(View.GONE);
            }

            if (removedPermissions.size() > 0) {
                TextView removedPermissionsTextView = (TextView) convertView.findViewById(R.id.item_update_log_removed_permissions);
                removedPermissionsTextView.setText(Arrays.toString(removedPermissions.toArray()));
            } else {
                removedPermissionsView.setVisibility(View.GONE);
            }
        }

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date.setText(dt.format(new Date(appInfo.getLastUpdateTime())));
        version.setText("v." + appInfo.getVersionCode());

        return convertView;
    }
}
