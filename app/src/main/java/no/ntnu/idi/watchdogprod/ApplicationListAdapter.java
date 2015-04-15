package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sigurd on 27/03/15.
 */
public class ApplicationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private ArrayList<ExtendedPackageInfo> applications;
    private ArrayList<ExtendedPackageInfo> recentlyUpdatedApplications;

    private Context context;

    public ApplicationListAdapter(Context context, ArrayList<ExtendedPackageInfo> allApplications) {
        this.context = context;
        applications = new ArrayList<>();
        recentlyUpdatedApplications = new ArrayList<>();
        populateLists(allApplications);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_application, parent, false);
            return new ItemViewHolder(view, new ItemViewHolder.ItemViewHolderOnClickListener() {
                @Override
                public void onItemClick(View caller, int position) {

                    ExtendedPackageInfo extendedPackageInfo = getItem(position);
                    Intent i = new Intent(context, ApplicationDetailActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString(ApplicationListActivity.PACKAGE_NAME, extendedPackageInfo.getPackageInfo().packageName);
                    i.putExtras(bundle);

                    context.startActivity(i);
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
            ExtendedPackageInfo app = getItem(position);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            fillListItem(app, itemViewHolder);
        } else if (holder instanceof HeaderViewHolder) {
            String title = null;
            int count = 0;

            if (position == 0) {
                title = context.getString(R.string.recently_updated);
                count = recentlyUpdatedApplications.size();
            } else {
                title = context.getString(R.string.installed);
                count = applications.size();
            }

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            fillListHeader(title, count, headerViewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return applications.size() + recentlyUpdatedApplications.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == recentlyUpdatedApplications.size() + 1) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    private ExtendedPackageInfo getItem(int position) {
        if (position < recentlyUpdatedApplications.size() + 1) {
            return recentlyUpdatedApplications.get(position - 1);
        } else {
            return applications.get(position - (recentlyUpdatedApplications.size() + 2));
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

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView count;

        public HeaderViewHolder(View headerView) {
            super(headerView);
            title = (TextView) headerView.findViewById(R.id.list_applications_header);
            count = (TextView) headerView.findViewById(R.id.list_applications_count);
        }
    }

    private void populateLists(ArrayList<ExtendedPackageInfo> allApplications) {
        Date now = new Date();
        final int hoursInDay = 24;

        int[] totalVector = new int[9];

        for (int i = 0; i < allApplications.size(); i++) {
            ExtendedPackageInfo packageInfo = allApplications.get(i);
            //TODO: add vector to extendedPackageInfo. Add all vectors, then take average
            long diff = now.getTime() - packageInfo.getUpdateLog().get(0).getLastUpdateTime();

            if (TimeUnit.MILLISECONDS.toHours(diff) < (hoursInDay * 3)) {
                recentlyUpdatedApplications.add(packageInfo);
            } else {
                applications.add(packageInfo);
            }
        }
    }

    private void fillListItem(ExtendedPackageInfo extendedPackageInfo, ItemViewHolder itemViewHolder) {
        setRiskColor(itemViewHolder, extendedPackageInfo.getPrivacyScore());

        itemViewHolder.itemIcon.setImageDrawable(extendedPackageInfo.getPackageInfo().applicationInfo.loadIcon(context.getPackageManager()));
        itemViewHolder.firstLine.setText(ApplicationHelper.getApplicationName(extendedPackageInfo.getPackageInfo(), context));

        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        //itemViewHolder.secondLine.setText("Sist oppdatert: " + dt.format(new Date(extendedPackageInfo.getUpdateLog().get(0).getLastUpdateTime())));
        itemViewHolder.secondLine.setText("Versjon " + extendedPackageInfo.getPackageInfo().versionName);
    }

    private void fillListHeader(String title, int count, HeaderViewHolder headerViewHolder) {
        headerViewHolder.title.setText(title);
        headerViewHolder.count.setText(Integer.toString(count));
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
}
