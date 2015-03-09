package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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

        firstLine.setText(permissionDescription.getName());
        secondLine.setText(permissionDescription.getDescription());
        setImageColorFromRiskLevel(image, permissionDescription);

        return convertView;
    }

    private void setImageColorFromRiskLevel(ImageView imageView, PermissionDescription permissionDescription) {
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        switch (randomNum) {
            case Rule.RISK_LEVEL_LOW:
                imageView.setBackgroundColor(context.getResources().getColor(R.color.risk_green));
                break;
            case Rule.RISK_LEVEL_MEDIUM:
                imageView.setBackgroundColor(context.getResources().getColor(R.color.risk_yellow));
                break;
            case Rule.RISK_LEVEL_HIGH:
                imageView.setBackgroundColor(context.getResources().getColor(R.color.risk_red));
                break;
        }
    }
}
