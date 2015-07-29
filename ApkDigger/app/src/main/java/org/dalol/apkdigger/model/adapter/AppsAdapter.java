package org.dalol.apkdigger.model.adapter;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import org.dalol.apkdigger.R;
import org.dalol.apkdigger.model.exception.ApkDiggerException;
import org.dalol.apkdigger.model.utilities.AppAdditionalPropUtils;
import org.dalol.apkdigger.presenter.MainViewPresenter.MainViewListener;

/**
 * Created by Filippo-TheAppExpert on 7/27/2015.
 */
public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> implements Filterable {

    private List<PackageInfo> mPackageInfoList;
    private List<PackageInfo> mItems = new ArrayList<>();
    private PackageManager packageManager;
    private MainViewListener mListener;

    public AppsAdapter(MainViewListener listener, List<PackageInfo> packageInfoList) {
        mListener = listener;
        mPackageInfoList = packageInfoList;
        mItems = packageInfoList;
        packageManager = listener.getMainContext().getPackageManager();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View mView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mImageView = (ImageView) v.findViewById(R.id.appOption);

            v.setOnClickListener(this);
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.appOption:
                    mListener.showFilterPopup(view, getPosition());
                    break;
                default:
                    try {
                        PackageInfo currentPackageInfo = mPackageInfoList.get(getPosition());
                        String packageName = currentPackageInfo.packageName;
                        mListener.startImplicitApp(packageName);
                    } catch (Exception ex) {
                        mListener.onException(new ApkDiggerException("ApkDiggerException :: " + ex));
                    }
            }
        }
    }

    @Override
    public AppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_row, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        View rowView = holder.mView;
        PackageInfo currentPackageInfo = mPackageInfoList.get(position);
        ApplicationInfo appInfo = currentPackageInfo.applicationInfo;
        String appFile = appInfo.sourceDir;

        ImageView appIcon = (ImageView) rowView.findViewById(R.id.appIcon);
        TextView appName = (TextView) rowView.findViewById(R.id.appName);
        TextView appPackageName = (TextView) rowView.findViewById(R.id.appPackageName);
        TextView appSize = (TextView) rowView.findViewById(R.id.appSize);
        TextView appModifiedDate = (TextView) rowView.findViewById(R.id.appInstalledDate);
//        TextView appVersionName = (TextView) rowView.findViewById(R.id.appVersionName);
//        TextView appVersionCode = (TextView) rowView.findViewById(R.id.appVersionCode);

        appIcon.setImageDrawable(appInfo.loadIcon(packageManager));
        appName.setText(appInfo.loadLabel(packageManager));
        appPackageName.setText(currentPackageInfo.packageName);
//        appVersionName.setText(currentPackageInfo.versionName);
//        appVersionCode.setText(Integer.toString(currentPackageInfo.versionCode));

        new AppAdditionalPropUtils(appSize, appModifiedDate, appFile);
    }

    @Override
    public int getItemCount() {
        return mPackageInfoList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<PackageInfo> results = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    oReturn.values = mItems;
                    oReturn.count = mItems.size();
                } else {
                    mPackageInfoList = mItems;
                    oReturn.count = mPackageInfoList.size();
                    if (mPackageInfoList != null & mPackageInfoList.size() > 0) {
                        for (final PackageInfo packageInfo : mPackageInfoList) {
                            if (packageInfo.applicationInfo.loadLabel(packageManager).toString().toLowerCase().contains(constraint.toString())) {
                                results.add(packageInfo);
                            }
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mPackageInfoList = (ArrayList<PackageInfo>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
