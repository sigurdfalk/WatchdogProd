package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sigurdhf on 27.03.2015.
 */
public class ApplicationListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ExtendedPackageInfo> objects;
    private ArrayList<ExtendedPackageInfo> newlyUpdatedObjects;

    public ApplicationListAdapter(Context context, ArrayList<ExtendedPackageInfo> objects) {
        this.context = context;
        this.objects = objects;
        this.newlyUpdatedObjects = new ArrayList<>();
        populateNewlyUpdatedObjects();
        Collections.sort(newlyUpdatedObjects);
    }

    @Override
    public int getCount() {
        return objects.size() + newlyUpdatedObjects.size();
    }

    @Override
    public ExtendedPackageInfo getItem(int position) {
        if (position < newlyUpdatedObjects.size() + 1) {
            return newlyUpdatedObjects.get(position - 1);
        }

        return objects.get(position - (newlyUpdatedObjects.size() + 2));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        if (newlyUpdatedObjects.size() > 0) {
            if (position == 0 || position == newlyUpdatedObjects.size() + 1) {
                return false;
            }

            return true;
        }

        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (newlyUpdatedObjects.size() > 0) {
            if (position == 0) {
                convertView = inflater.inflate(R.layout.list_item_application_header, null);
                fillHeader(context.getString(R.string.recently_updated), newlyUpdatedObjects.size(), convertView);

            } else if (position == newlyUpdatedObjects.size() + 1) {
                convertView = inflater.inflate(R.layout.list_item_application_header, null);
                fillHeader(context.getString(R.string.installed), objects.size(), convertView);
            } else {
                ExtendedPackageInfo extendedPackageInfo = null;

                if (position < newlyUpdatedObjects.size() + 1) {
                    extendedPackageInfo = newlyUpdatedObjects.get(position - 1);
                } else {
                    extendedPackageInfo = objects.get(position - (newlyUpdatedObjects.size() + 2));
                }

                convertView = inflater.inflate(R.layout.list_item_application, null);
                fillListItem(extendedPackageInfo, convertView);
            }
        } else {
            ExtendedPackageInfo extendedPackageInfo = objects.get(position);
            convertView = inflater.inflate(R.layout.list_item_application, null);
            fillListItem(extendedPackageInfo, convertView);
        }

        return convertView;
    }

    private void fillHeader(String title, int count, View convertView) {
        TextView titleView = (TextView) convertView.findViewById(R.id.list_applications_header);
        TextView countView = (TextView) convertView.findViewById(R.id.list_applications_count);

        titleView.setText(title);
        countView.setText(Integer.toString(count));
    }

    private void fillListItem(ExtendedPackageInfo extendedPackageInfo, View convertView) {
        ImageView icon = (ImageView) convertView.findViewById(R.id.list_applications_icon);
        TextView firstLine = (TextView) convertView.findViewById(R.id.list_applications_firstLine);
        TextView secondLine = (TextView) convertView.findViewById(R.id.list_applications_secondLine);
        TextView riskView = (TextView) convertView.findViewById(R.id.list_applications_risk_score);

        setRiskColor(riskView, extendedPackageInfo.getPrivacyScore());

        icon.setImageDrawable(extendedPackageInfo.getPackageInfo().applicationInfo.loadIcon(context.getPackageManager()));
        firstLine.setText(ApplicationHelper.getApplicationName(extendedPackageInfo.getPackageInfo(), context));

        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        secondLine.setText("Sist oppdatert: " + dt.format(new Date(extendedPackageInfo.getUpdateLog().get(0).getLastUpdateTime())));
    }

    private void setRiskColor(TextView riskView, double riskScore) {
        if (riskScore > PrivacyScoreCalculator.HIGH_THRESHOLD) {
            riskView.setBackgroundColor(context.getResources().getColor(R.color.risk_red));
        } else if (riskScore > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            riskView.setBackgroundColor(context.getResources().getColor(R.color.risk_yellow));
        } else {
            riskView.setBackgroundColor(context.getResources().getColor(R.color.risk_green));
        }
    }

    private void populateNewlyUpdatedObjects() {
        Date now = new Date();
        final int hoursInDay = 24;

        for (int i = 0; i < objects.size(); i++) {
            ExtendedPackageInfo packageInfo = objects.get(i);
            long diff = now.getTime() - packageInfo.getUpdateLog().get(0).getLastUpdateTime();

            if (TimeUnit.MILLISECONDS.toHours(diff) < (hoursInDay * 14)) {
                newlyUpdatedObjects.add(objects.remove(i));
            }
        }
    }
}
