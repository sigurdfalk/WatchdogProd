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
public class ApplicationListAdapter extends ArrayAdapter<ExtendedPackageInfo> {
    private final Context context;
    private final ArrayList<ExtendedPackageInfo> objects;

    public ApplicationListAdapter(Context context, ArrayList<ExtendedPackageInfo> objects) {
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

        ExtendedPackageInfo extendedPackageInfo = objects.get(position);

        icon.setImageDrawable(extendedPackageInfo.getPackageInfo().applicationInfo.loadIcon(context.getPackageManager()));
        firstLine.setText(ApplicationHelper.getApplicationName(extendedPackageInfo.getPackageInfo(), context));
        secondLine.setText("Privacy Score: " + extendedPackageInfo.getPrivacyScore());

        return convertView;
    }
}
