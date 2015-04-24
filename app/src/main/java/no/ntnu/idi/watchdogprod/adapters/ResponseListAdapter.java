package no.ntnu.idi.watchdogprod.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelper;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.recommender.ResponseApp;

/**
 * Created by Wschive on 23/04/15.
 */
public class ResponseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private ArrayList<ResponseApp> applications;

    private Context context;

    public ResponseListAdapter(Context context, ArrayList<ResponseApp> allApplications) {
        this.context = context;
        applications = allApplications;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_application, parent, false);
            return new ItemViewHolder(view, new ItemViewHolder.ItemViewHolderOnClickListener() {
                @Override
                public void onItemClick(View caller, int position) {

                    ResponseApp responseApp = getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + responseApp.getName()));

                    context.startActivity(intent);
                }
            });
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_application_header, parent, false);
            return new HeaderViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ResponseApp app = getItem(position);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            fillListItem(app, itemViewHolder);
        } else if (holder instanceof HeaderViewHolder) {
            String title = context.getString(R.string.similar_apps);
            int count = applications.size();

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            fillListHeader(title, count, headerViewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView count;

        public HeaderViewHolder(View headerView) {
            super(headerView);
            title = (TextView) headerView.findViewById(R.id.list_applications_header);
            count = (TextView) headerView.findViewById(R.id.list_applications_count);
        }
    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView itemIcon;
        public TextView firstLine;
        public TextView secondLine;
        public TextView riskColor;
        public LinearLayout wrapper;

        public ItemViewHolderOnClickListener listener;

        public ItemViewHolder(View itemView, ItemViewHolderOnClickListener listener) {
            super(itemView);
            this.listener = listener;
            this.itemIcon = (ImageView) itemView.findViewById(R.id.list_applications_icon);
            this.firstLine = (TextView) itemView.findViewById(R.id.list_applications_firstLine);
            this.secondLine = (TextView) itemView.findViewById(R.id.list_applications_secondLine);
            this.riskColor = (TextView) itemView.findViewById(R.id.list_applications_risk_score);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.list_applications_wrapper);

            wrapper.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, this.getPosition());
        }

        public static interface ItemViewHolderOnClickListener {
            public void onItemClick(View caller, int position);
        }
    }
    private ResponseApp getItem(int position) {
            return applications.get(position);
        }

    public ArrayList<PermissionDescription> parseArray(String[] array){
        ArrayList<PermissionDescription> list = new ArrayList<>();
        for (String s : array) {
            list.add(PermissionHelper.getPermissionDescription(context, s));
        }
        return list;
    }

    private void fillListItem(ResponseApp app, ItemViewHolder itemViewHolder) {

        ArrayList<PermissionDescription> permissionDescriptions = parseArray(app.getPermissions());
        double score = PrivacyScoreCalculator.calculateScore(permissionDescriptions);
        setRiskColor(itemViewHolder, score);

        new DownloadImageTask(itemViewHolder.itemIcon).execute(app.getLogo());

        itemViewHolder.firstLine.setText(app.getInfoLine());

        itemViewHolder.secondLine.setText("Score: " + score);

    }
    private void setRiskColor(ItemViewHolder itemViewHolder, double riskScore) {
        if (riskScore > PrivacyScoreCalculator.HIGH_THRESHOLD) {
            itemViewHolder.riskColor.setBackgroundColor(context.getResources().getColor(R.color.risk_red));
        } else if (riskScore > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            itemViewHolder.riskColor.setBackgroundColor(context.getResources().getColor(R.color.risk_yellow));
        } else {
            itemViewHolder.riskColor.setBackgroundColor(context.getResources().getColor(R.color.risk_green));
        }
    }
    private void fillListHeader(String title, int count, HeaderViewHolder headerViewHolder) {
        headerViewHolder.title.setText(title);
        headerViewHolder.count.setText(Integer.toString(count));
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
