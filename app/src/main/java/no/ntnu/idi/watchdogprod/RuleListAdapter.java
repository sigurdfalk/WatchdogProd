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
import java.util.Arrays;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class RuleListAdapter extends ArrayAdapter<Rule> {
    private final Context context;
    private final ArrayList<Rule> objects;

    public RuleListAdapter(Context context, ArrayList<Rule> objects) {
        super(context, R.layout.list_item_application, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_rule_violation, parent, false);
        }

        TextView firstLine = (TextView) convertView.findViewById(R.id.item_rule_firstLine);
        TextView secondLine = (TextView) convertView.findViewById(R.id.item_rule_secondLine);
        ImageView image = (ImageView) convertView.findViewById(R.id.item_rule_image);

        Rule rule = objects.get(position);

        firstLine.setText(rule.getName());
        secondLine.setText(Arrays.toString(rule.getPermissions()));
        setImageColorFromRiskLevel(image, rule);

        return convertView;
    }

    private void setImageColorFromRiskLevel(ImageView imageView, Rule rule) {
        switch (rule.getRiskLevel()) {
            case Rule.RISK_LEVEL_LOW:
                imageView.setBackgroundColor(context.getResources().getColor(R.color.risk_yellow));
                break;
            case Rule.RISK_LEVEL_MEDIUM:
                imageView.setBackgroundColor(context.getResources().getColor(R.color.risk_orange));
                break;
            case Rule.RISK_LEVEL_HIGH:
                imageView.setBackgroundColor(context.getResources().getColor(R.color.risk_red));
                break;
        }
    }
}
