package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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

        ImageView icon = (ImageView) convertView.findViewById(R.id.behavior_item_image);
        TextView title = (TextView) convertView.findViewById(R.id.behavior_item_title);
        TextView text = (TextView) convertView.findViewById(R.id.behavior_item_text);

        ProfileBehavior profileBehavior = objects.get(position);

        if(profileBehavior.getStatus() == 1) {
            icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.selector_checkbox_sad));
        } else if(profileBehavior.getStatus() == 2) {
            icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.selector_chackbox_neutral));
        } else {
            icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.selector_checkbox_happy));
        }

        title.setText(profileBehavior.getTitle());
//        text.setText(profileBehavior.getText());

        return convertView;
    }
}
