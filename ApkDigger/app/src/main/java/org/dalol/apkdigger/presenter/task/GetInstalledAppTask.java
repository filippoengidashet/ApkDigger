package org.dalol.apkdigger.presenter.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;

import org.dalol.apkdigger.model.constants.Constant;
import org.dalol.apkdigger.model.utilities.AppNameComparator;
import org.dalol.apkdigger.model.utilities.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Filippo-TheAppExpert on 5/26/2015.
 */
public class GetInstalledAppTask extends AsyncTask<Void, PackageInfo, List<PackageInfo>> {

    private Context mContext;
    private AppFoundListener mAppFoundListener;
    private List<PackageInfo> mPackageInfo;
    private List<PackageInfo> mCollectionPackageInfo = new ArrayList<>();
    private boolean mSortList;

    public GetInstalledAppTask(Context context, AppFoundListener appFoundListener) {
        mContext = context;
        mAppFoundListener = appFoundListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAppFoundListener.onStart();
    }

    @Override
    protected List<PackageInfo> doInBackground(Void... params) {

        final long start = System.nanoTime();

        boolean systemPackage = SharedPreferenceUtils.getValue(mContext, Constant.SHOW_SYSTEM_APP_PREFERENCE);
        mSortList = SharedPreferenceUtils.getValue(mContext, Constant.SORT_APPS_PREFERENCE);

        mPackageInfo = mContext.getPackageManager().getInstalledPackages(0);

        for (PackageInfo packageInfo : mPackageInfo) {

            if (!systemPackage) {
                int mask = packageInfo.applicationInfo.FLAG_SYSTEM | packageInfo.applicationInfo.FLAG_UPDATED_SYSTEM_APP;
                if ((packageInfo.applicationInfo.flags & mask) != 0) {
                    continue;
                }
            }

            if ((!systemPackage) && (packageInfo.versionName == null)) {
                continue;
            }

            if (!mSortList) {
                publishProgress(packageInfo);
            } else {
                mCollectionPackageInfo.add(packageInfo);
            }
        }

        if (mSortList) {
            Collections.sort(mCollectionPackageInfo, new AppNameComparator(mContext));
        }

        final long end = System.nanoTime();

        System.out.println("Took start 1: " + ((end - start) / 1000000) + " ms");
        return mCollectionPackageInfo;
    }

    @Override
    protected void onProgressUpdate(PackageInfo... values) {
        super.onProgressUpdate(values);
        mAppFoundListener.onApp(values[0]);
    }

    @Override
    protected void onPostExecute(List<PackageInfo> packageInfos) {
        super.onPostExecute(packageInfos);
        if (mSortList) {
            mAppFoundListener.onAppList(mCollectionPackageInfo);
        }
        mAppFoundListener.onComplete();
    }

    public interface AppFoundListener {

        void onStart();

        void onApp(PackageInfo packageInfo);

        void onAppList(List<PackageInfo> packageInfos);

        void onComplete();
    }
}
