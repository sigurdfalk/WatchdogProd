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
import java.util.List;

/**
 * Created by sigurdhf on 05.03.2015.
 */
public class ApplicationListAdapter extends ArrayAdapter<PackageInfo> {
    private final Context context;
    private final ArrayList<PackageInfo> objects;

    public ApplicationListAdapter(Context context, ArrayList<PackageInfo> objects) {
        super(context, R.layout.list_item_application, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_application, parent, false);
        }

        /*LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_application, parent, false);*/

        ImageView icon = (ImageView) convertView.findViewById(R.id.list_applications_icon);
        TextView firstLine = (TextView) convertView.findViewById(R.id.list_applications_firstLine);
        TextView secondLine = (TextView) convertView.findViewById(R.id.list_applications_secondLine);

        PackageInfo packageInfo = objects.get(position);

        icon.setImageDrawable(packageInfo.applicationInfo.loadIcon(context.getPackageManager()));
        firstLine.setText(ApplicationHelper.getApplicationName(packageInfo, context));
        secondLine.setText(packageInfo.packageName);

        return convertView;
    }
}
