package no.ntnu.idi.watchdogprod.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.helpers.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.R;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class PermissionListAdapter extends ArrayAdapter<PermissionDescription> {
    private final Context context;
    private final ArrayList<PermissionDescription> objects;

    public PermissionListAdapter(Context context, ArrayList<PermissionDescription> objects) {
        super(context, R.layout.list_item_permissions, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_permissions, parent, false);
        }

        TextView firstLine = (TextView) convertView.findViewById(R.id.item_permissions_firstLine);
        TextView secondLine = (TextView) convertView.findViewById(R.id.item_permissions_secondLine);
        ImageView image = (ImageView) convertView.findViewById(R.id.item_permissions_image);

        PermissionDescription permissionDescription = objects.get(position);

        firstLine.setText(permissionDescription.getDesignation());
        secondLine.setText(permissionDescription.getDescription());
        setImageColorFromRiskLevel(image, permissionDescription);

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
