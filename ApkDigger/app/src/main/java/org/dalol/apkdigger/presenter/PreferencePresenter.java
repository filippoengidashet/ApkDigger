package org.dalol.apkdigger.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import org.dalol.apkdigger.model.callback.LifeCycleCallback;
import org.dalol.apkdigger.model.constants.Constant;
import org.dalol.apkdigger.model.utilities.SharedPreferenceUtils;
import org.dalol.apkdigger.presenter.base.BasePresenter;

/**
 * Created by Filippo-TheAppExpert on 7/27/2015.
 */
public class PreferencePresenter extends BasePresenter implements LifeCycleCallback {

    private PreferenceListener mListener;

    public PreferencePresenter(PreferenceListener listener) {
        mListener = listener;
    }

    @Override
    public void initialize() {

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

    public void saveShowSystemApp(boolean isChecked) {
        SharedPreferenceUtils.save(mListener.getPreferenceActivity(), Constant.SHOW_SYSTEM_APP_PREFERENCE, isChecked);
    }

    public void saveSetSorted(boolean isChecked) {
        SharedPreferenceUtils.save(mListener.getPreferenceActivity(), Constant.SORT_APPS_PREFERENCE, isChecked);
    }

    public void rate() {
        String url;
        try {
            mListener.getPreferenceActivity().getPackageManager().getPackageInfo("com.android.vending", 0);
            url = "market://details?id=" + Constant.PACKAGE_NAME;
        } catch (final Exception e) {
            url = Constant.LINK_TO_APP;
        }
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mListener.startImplicitIntent(intent);
    }

    public void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey check out Apk Digger App at: https://play.google.com/store/apps/details?id=" + Constant.PACKAGE_NAME;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "APK Digger Download Link");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        mListener.startImplicitIntent(sharingIntent);
    }

    public void sendEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"filippo.eng@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        mListener.startImplicitIntent(Intent.createChooser(emailIntent, "Send Email Message.."));
    }

    public void seeCode() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constant.GITHUB_LINK));
        mListener.startImplicitIntent(intent);
    }

    public interface PreferenceListener {

        Activity getPreferenceActivity();

        void startImplicitIntent(Intent intent);
    }
}
