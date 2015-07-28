package org.dalol.apkdigger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Random;

import org.dalol.apkdigger.R;
import org.dalol.apkdigger.activity.base.BaseActivity;
import org.dalol.apkdigger.presenter.SplashViewPresenter;

/*
 * @author Filippo Engidashet
 * @version 1.0
 * @date 7/27/2015
 *
 * SplashActivity.java: {@link BaseActivity} extending the properties to Splash Activity.
 * This class shows a splash view for a random few seconds
 */
public class SplashActivity extends BaseActivity<Integer> implements SplashViewPresenter.SplashView {

    private SplashViewPresenter mPresenter;
    private static int SPLASH_TIME_OUT = new Random().nextInt(1500);

    @Override
    protected Integer getViewResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void configViews(Intent intent, Bundle savedInstanceState) {
        mPresenter = new SplashViewPresenter(SplashActivity.this);
        mPresenter.initialize();
        mPresenter.showSplashScreen(SPLASH_TIME_OUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public AppCompatActivity getActivity() {
        return SplashActivity.this;
    }

    @Override
    public void showActivity(Intent intent) {
        startActivity(intent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
