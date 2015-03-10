package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
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

        TextView firstLine = (TextView) convertView.findViewById(R.id.item_update_log_firstLine);
        TextView secondLine = (TextView) convertView.findViewById(R.id.item_update_log_secondLine);
        TextView thirdLine = (TextView) convertView.findViewById(R.id.item_update_log_thirdLine);

        AppInfo appInfo = objects.get(position);

        firstLine.setText(appInfo.getPackageName());
        secondLine.setText(Integer.toString(appInfo.getVersionCode()));
        thirdLine.setText(new Date(appInfo.getLastUpdateTime()).toString());

        return convertView;
    }
}
