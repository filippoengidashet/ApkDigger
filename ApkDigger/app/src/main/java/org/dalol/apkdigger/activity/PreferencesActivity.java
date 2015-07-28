package org.dalol.apkdigger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.dalol.apkdigger.R;
import org.dalol.apkdigger.activity.base.BaseActivity;
import org.dalol.apkdigger.model.constants.Constant;
import org.dalol.apkdigger.model.utilities.SharedPreferenceUtils;
import org.dalol.apkdigger.presenter.PreferencePresenter;


/**
 * Created by Filippo-TheAppExpert on 7/27/2015.
 */
public class PreferencesActivity extends BaseActivity<Integer> {

    private Toolbar mToolbar;
    private boolean mShowSystemAppsFlag, mShowSortedList;

    @Override
    protected void configViews(Intent intent, Bundle savedInstanceState) {

        mShowSystemAppsFlag = SharedPreferenceUtils.getValue(getApplicationContext(), Constant.SHOW_SYSTEM_APP_PREFERENCE);
        mShowSortedList = SharedPreferenceUtils.getValue(getApplicationContext(), Constant.SORT_APPS_PREFERENCE);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsPreference())
                .commit();
    }

    @Override
    protected Integer getViewResource() {
        return R.layout.activity_preference;
    }

    @Override
    public Toolbar getToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(Constant.PREFERENCES);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        return mToolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            boolean show = SharedPreferenceUtils.getValue(getApplicationContext(), Constant.SHOW_SYSTEM_APP_PREFERENCE);
            boolean sort = SharedPreferenceUtils.getValue(getApplicationContext(), Constant.SORT_APPS_PREFERENCE);

            if ((show != mShowSystemAppsFlag) || (sort != mShowSortedList)) {
                setResult(RESULT_OK);
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsPreference extends PreferenceFragment implements Preference.OnPreferenceClickListener, PreferencePresenter.PreferenceListener {

        private PreferencePresenter mPresenter;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            mPresenter = new PreferencePresenter(SettingsPreference.this);
            mPresenter.initialize();
            configPreferences();
        }

        private void configPreferences() {
            CheckBoxPreference showSystemAppsOption = (CheckBoxPreference) findPreference(Constant.SHOW_SYSTEM_APP_PREFERENCE);
            boolean optionShowSystemApps = SharedPreferenceUtils.getValue(getActivity(), Constant.SHOW_SYSTEM_APP_PREFERENCE);
            showSystemAppsOption.setChecked(optionShowSystemApps);
            showSystemAppsOption.setOnPreferenceClickListener(this);

            CheckBoxPreference sortAppsList = (CheckBoxPreference) findPreference(Constant.SORT_APPS_PREFERENCE);
            boolean optionSort = SharedPreferenceUtils.getValue(getActivity(), Constant.SORT_APPS_PREFERENCE);
            sortAppsList.setChecked(optionSort);
            sortAppsList.setOnPreferenceClickListener(this);

            findPreference(Constant.RATE_PREFERENCE).setOnPreferenceClickListener(this);
            findPreference(Constant.SHARE_PREFERENCE).setOnPreferenceClickListener(this);
            findPreference(Constant.SEE_CODE_PREFERENCE).setOnPreferenceClickListener(this);
            findPreference(Constant.ABOUT_PREFERENCE).setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {

            switch (preference.getKey()) {
                case Constant.SHOW_SYSTEM_APP_PREFERENCE:
                    CheckBoxPreference showSystemAppsOption = (CheckBoxPreference) preference;
                    mPresenter.saveShowSystemApp(showSystemAppsOption.isChecked());
                    break;
                case Constant.SORT_APPS_PREFERENCE:
                    CheckBoxPreference sortAppsList = (CheckBoxPreference) preference;
                    mPresenter.saveSetSorted(sortAppsList.isChecked());
                    break;
                case Constant.RATE_PREFERENCE:
                    mPresenter.rate();
                    break;
                case Constant.SHARE_PREFERENCE:
                    mPresenter.share();
                    break;
                case Constant.SEE_CODE_PREFERENCE:
                    mPresenter.seeCode();
                    break;
                case Constant.ABOUT_PREFERENCE:
                    showAbout();
                    break;
                default:
                    Toast.makeText(getActivity(), "Other", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }

        private void showAbout() {

            View dialog = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.about_layout, null);

            Button sendEmail = (Button) dialog.findViewById(R.id.sendEmail);
            sendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.sendEmail();
                }
            });

            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setCancelable(true)
                    .setView(dialog)
                    .create();
            Button close = (Button) dialog.findViewById(R.id.close_button);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertDialog.show();
        }


        @Override
        public Activity getPreferenceActivity() {
            return getActivity();
        }

        @Override
        public void startImplicitIntent(Intent intent) {
            startActivity(intent);
        }
    }
}
