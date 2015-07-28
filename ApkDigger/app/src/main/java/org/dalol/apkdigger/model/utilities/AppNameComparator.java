package org.dalol.apkdigger.model.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;

import java.util.Comparator;

/**
 * Created by Filippo-TheAppExpert on 7/27/2015.
 */
public class AppNameComparator implements Comparator<PackageInfo>
{
    private Context mContext;

    public AppNameComparator(Context context) {
        mContext = context;
    }

    public int compare(PackageInfo o1, PackageInfo o2)
    {
        return o1.applicationInfo.loadLabel(mContext.getPackageManager()).toString().compareTo(o2.applicationInfo.loadLabel(mContext.getPackageManager()).toString());
    }
}
