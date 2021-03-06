package no.ntnu.idi.watchdogprod.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.domain.Rule;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class RuleListAdapter extends ArrayAdapter<Rule> {
    private final Context context;
    private final ArrayList<Rule> objects;

    public RuleListAdapter(Context context, ArrayList<Rule> objects) {
        super(context, R.layout.list_item_rule_violation, objects);
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

        Rule rule = objects.get(position);

        firstLine.setText(rule.getName());
        secondLine.setText(rule.getDescription());

        return convertView;
    }
}
