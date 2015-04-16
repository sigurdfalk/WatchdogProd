package no.ntnu.idi.watchdogprod.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.domain.ProfileBehavior;

/**
 * Created by fredsten on 27.03.2015.
 */
public class ProfileBehaviorListAdapter extends ArrayAdapter<ProfileBehavior> {

    private final Context context;
    private final ArrayList<ProfileBehavior> objects;

    public ProfileBehaviorListAdapter(Context context, ArrayList<ProfileBehavior> objects) {
        super(context, R.layout.list_item_profile_behavior, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_profile_behavior, parent, false);
        }

//        ImageView icon = (ImageView) convertView.findViewById(R.id.behavior_item_image);
        TextView title = (TextView) convertView.findViewById(R.id.behavior_item_title);
        TextView text = (TextView) convertView.findViewById(R.id.collapsable_behavior_text);

        ProfileBehavior profileBehavior = objects.get(position);

        if(profileBehavior.getStatus() == 1) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.lighter_red));
        } else if(profileBehavior.getStatus() == 2) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.risk_yellow));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.lighter_green));
        }

        title.setText(profileBehavior.getTitle());
//        text.setText(profileBehavior.getText());

        return convertView;
    }
}
