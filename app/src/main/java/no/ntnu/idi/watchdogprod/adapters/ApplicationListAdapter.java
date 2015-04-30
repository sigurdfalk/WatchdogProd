package no.ntnu.idi.watchdogprod.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.activities.ApplicationDetailActivity;
import no.ntnu.idi.watchdogprod.activities.ApplicationListActivity;
import no.ntnu.idi.watchdogprod.helpers.SharedPreferencesHelper;

/**
 * Created by Sigurd on 27/03/15.
 */
public class ApplicationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private ArrayList<ExtendedPackageInfo> applications;
    private ArrayList<ExtendedPackageInfo> recentlyUpdatedApplications;

    public static int APP_DELETED_CODE = 2016;

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

                    ((Activity) context).startActivityForResult(i,APP_DELETED_CODE);
//                    context.startActivity(i);
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

            if (position == 0 && recentlyUpdatedApplications.size() > 0) {
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
        int count = 0;

        if (applications.size() > 0) {
            count += 1;
        }

        if (recentlyUpdatedApplications.size() > 0) {
            count += 1;
        }

        return count + applications.size() + recentlyUpdatedApplications.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (recentlyUpdatedApplications.size() > 0) {
            if (position == recentlyUpdatedApplications.size() + 1) {
                return TYPE_HEADER;
            }
        }

        if (position == 0) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    private ExtendedPackageInfo getItem(int position) {
        if (recentlyUpdatedApplications.size() > 0) {
            if (position < recentlyUpdatedApplications.size() + 1) {
                return recentlyUpdatedApplications.get(position - 1);
            } else {
                return applications.get(position - (recentlyUpdatedApplications.size() + 2));
            }
        }

        return applications.get(position - 1);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView itemIcon;
        public TextView firstLine;
        public TextView secondLine;
        public TextView riskColor;
        public ImageView errorSign;
        public ImageView infoSign;
        public LinearLayout wrapper;

        public ItemViewHolderOnClickListener listener;

        public ItemViewHolder(View itemView, ItemViewHolderOnClickListener listener) {
            super(itemView);
            this.listener = listener;
            this.itemIcon = (ImageView) itemView.findViewById(R.id.list_applications_icon);
            this.firstLine = (TextView) itemView.findViewById(R.id.list_applications_firstLine);
            this.secondLine = (TextView) itemView.findViewById(R.id.list_applications_secondLine);
            this.riskColor = (TextView) itemView.findViewById(R.id.list_applications_risk_score);
            this.errorSign = (ImageView) itemView.findViewById(R.id.list_applications_warning);
            this.infoSign = (ImageView) itemView.findViewById(R.id.list_applications_info);
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
            long diff = 0;
            try {
                diff = now.getTime() - packageInfo.getUpdateLog().get(0).getLastUpdateTime();
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        itemViewHolder.firstLine.setText(ApplicationHelperSingleton.getApplicationName(context, extendedPackageInfo.getPackageInfo()));

        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        //itemViewHolder.secondLine.setText("Sist oppdatert: " + dt.format(new Date(extendedPackageInfo.getUpdateLog().get(0).getLastUpdateTime())));
        /*try {
            double score = PrivacyScoreCalculator.calculateScore(context, extendedPackageInfo);
            itemViewHolder.secondLine.setText("Score: " + score);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        //itemViewHolder.secondLine.setText("Versjon " + extendedPackageInfo.getPackageInfo().versionName);

        PrivacyScoreCalculator privacyScoreCalculator = PrivacyScoreCalculator.getInstance(context);
        double score = privacyScoreCalculator.calculatePrivacyScore(extendedPackageInfo);
        itemViewHolder.secondLine.setText("Score: " + score);

        if (SharedPreferencesHelper.doShowAppWarningSign(context, extendedPackageInfo.getPackageInfo().packageName)) {
            itemViewHolder.errorSign.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.errorSign.setVisibility(View.GONE);
        }

        if (SharedPreferencesHelper.doShowAppInfoSign(context, extendedPackageInfo.getPackageInfo().packageName) && extendedPackageInfo.getPermissionFacts().size() != 0) {
            itemViewHolder.infoSign.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.infoSign.setVisibility(View.GONE);
        }
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
