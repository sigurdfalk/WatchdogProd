package no.ntnu.idi.watchdogprod.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.domain.Answer;
import no.ntnu.idi.watchdogprod.domain.DisharomyApplication;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelper;

/**
 * Created by fredsten on 27.04.2015.
 */
public class BehaviorApplicationListAdapter extends ArrayAdapter<DisharomyApplication> {
    private final Context context;
    private final ArrayList<DisharomyApplication> objects;

    public BehaviorApplicationListAdapter(Context context, ArrayList<DisharomyApplication> objects) {
        super(context, R.layout.list_item_permissions, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_disharmony, parent, false);
        }

        TextView firstLine = (TextView) convertView.findViewById(R.id.item_harmony_firstLine);
        ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);

        TextView sadCount = (TextView)convertView.findViewById(R.id.item_sad_count);
        TextView neutralCount = (TextView)convertView.findViewById(R.id.item_neutral_count);
        TextView happyCount = (TextView)convertView.findViewById(R.id.item_happy_count);

        DisharomyApplication disharomyApplication = objects.get(position);
        firstLine.setText(ApplicationHelper.getApplicationName(disharomyApplication.getExtendedPackageInfo().getPackageInfo(), context) + "");
        icon.setImageDrawable(disharomyApplication.getExtendedPackageInfo().getPackageInfo().applicationInfo.loadIcon(context.getPackageManager()));

        sadCount.setText("- " + context.getResources().getString(R.string.disharmony_sad_text) + " " + disharomyApplication.getCount(Answer.ANSWER_SAD) + " " +(disharomyApplication.getCount(Answer.ANSWER_SAD) == 1? "tillatelse":"tillatelser"));
        neutralCount.setText("- " + context.getResources().getString(R.string.disharmony_neutral_text) + " "  + disharomyApplication.getCount(Answer.ANSWER_NEUTRAL) + " " + (disharomyApplication.getCount(Answer.ANSWER_NEUTRAL) == 1? "tillatelse":"tillatelser"));
        happyCount.setText("- positiv til " + disharomyApplication.getCount(Answer.ANSWER_HAPPY) + " " + (disharomyApplication.getCount(Answer.ANSWER_HAPPY) == 1? "tillatelse":"tillatelser"));

        return convertView;
    }
}
