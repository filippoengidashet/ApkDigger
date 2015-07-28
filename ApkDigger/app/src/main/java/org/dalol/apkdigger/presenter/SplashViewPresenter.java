package org.dalol.apkdigger.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.dalol.apkdigger.activity.MainActivity;
import org.dalol.apkdigger.model.callback.LifeCycleCallback;
import org.dalol.apkdigger.presenter.base.BasePresenter;

/**
 * Created by Filippo-TheAppExpert on 7/27/2015.
 */
public class SplashViewPresenter extends BasePresenter implements LifeCycleCallback {

    private SplashView mContract;

    public SplashViewPresenter(SplashView contract) {
        mContract = contract;
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

    public void showSplashScreen(int timeout) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mContract.showActivity(new Intent(mContract.getActivity(), MainActivity.class));
            }
        }, timeout);
    }


    public interface SplashView {

        AppCompatActivity getActivity();

        void showActivity(Intent intent);
    }
}
