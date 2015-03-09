package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

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



        return convertView;
    }
}
