package org.dalol.apkdigger.presenter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.dalol.apkdigger.model.adapter.AppsAdapter;
import org.dalol.apkdigger.model.exception.ApkDiggerException;
import org.dalol.apkdigger.model.callback.LifeCycleCallback;
import org.dalol.apkdigger.presenter.base.BasePresenter;
import org.dalol.apkdigger.presenter.task.GetInstalledAppTask;

/**
 * Created by Filippo-TheAppExpert on 7/27/2015.
 */
public class MainViewPresenter extends BasePresenter implements LifeCycleCallback, GetInstalledAppTask.AppFoundListener {

    private MainViewListener mListener;

    public MainViewPresenter(MainViewListener listener) {
        mListener = listener;
    }

    @Override
    public void initialize() {
    }

    public void loadInstalledApps() {
        if (!mListener.getPackageInfoList().isEmpty()) {
            mListener.getPackageInfoList().clear();
            mListener.getAppsAdapter().notifyDataSetChanged();
        }
        new GetInstalledAppTask(mListener.getMainContext(), MainViewPresenter.this).execute();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onActivityCreated(@Nullable Activity activity) {

    }

    @Override
    public void onStart() {
        mListener.onShowDialog();
    }

    @Override
    public void onApp(PackageInfo packageInfo) {
        mListener.onPublishApp(packageInfo);
    }

    @Override
    public void onAppList(List<PackageInfo> packageInfos) {
        mListener.onPublishSortedAppList(packageInfos);
    }

    @Override
    public void onComplete() {
        mListener.onHideDialog();
    }

    public void showApplicationInfo(int position) {

        PackageInfo currentPackageInfo = mListener.getPackageInfoList().get(position);
        String packageName = currentPackageInfo.packageName;

        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            mListener.startAppIntent(intent);

        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            mListener.startAppIntent(intent);
        }
    }

    public void shareApp(int position) {

        PackageInfo currentPackageInfo = mListener.getPackageInfoList().get(position);
        File file = new File(currentPackageInfo.applicationInfo.publicSourceDir);
        writeToStorage(file, currentPackageInfo);
    }

    private void writeToStorage(File outputFile, PackageInfo currentPackageInfo) {

        try {
            String file_name = currentPackageInfo.applicationInfo.loadLabel(mListener.getPkgManager()).toString();

            File apkFileCopy = new File(Environment.getExternalStorageDirectory().toString() + "/APK-Digger");
            apkFileCopy.mkdirs();
            apkFileCopy = new File(apkFileCopy.getPath() + "/" + file_name + ".apk");
            apkFileCopy.createNewFile();

            InputStream in = new FileInputStream(outputFile);
            OutputStream out = new FileOutputStream(apkFileCopy);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(
                    android.content.Intent.EXTRA_SUBJECT, "APK Digger");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    "Sharing " + currentPackageInfo.applicationInfo.loadLabel(mListener.getPkgManager()).toString() + " app.");
            apkFileCopy.setReadable(true, false);


            shareIntent.setType("*/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(apkFileCopy));
            shareIntent.addCategory("android.intent.category.DEFAULT");
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mListener.startAppIntent(Intent.createChooser(shareIntent, "Share " + apkFileCopy.getName()));

        } catch (FileNotFoundException ex) {
            mListener.onException(new ApkDiggerException("FileNotFoundException :: " + ex));
            System.out.println(ex.getMessage() + " in the specified directory.");
        } catch (IOException e) {
            mListener.onException(new ApkDiggerException("IOException :: " + e));
            System.out.println(e.getMessage());
        }
    }

    public void startImplicitApp(String packageName) {
        try {
            mListener.startAppIntent(mListener.getPkgManager().getLaunchIntentForPackage(packageName));
        } catch (Exception ex) {
            mListener.onException(new ApkDiggerException("ApkDiggerException :: " + ex));
        }
    }

    public interface MainViewListener {

        Context getMainContext();

        PackageManager getPkgManager();

        void onShowDialog();

        void onPublishApp(PackageInfo packageInfo);

        void onHideDialog();

        void showFilterPopup(View view, int position);

        List<PackageInfo> getPackageInfoList();

        AppsAdapter getAppsAdapter();

        void startAppIntent(Intent intent);

        void onException(ApkDiggerException exception);

        void startImplicitApp(String packageName);

        void onPublishSortedAppList(List<PackageInfo> packageInfos);
    }
}
