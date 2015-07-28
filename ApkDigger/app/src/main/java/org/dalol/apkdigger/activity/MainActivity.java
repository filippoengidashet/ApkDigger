package org.dalol.apkdigger.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.dalol.apkdigger.R;
import org.dalol.apkdigger.activity.base.BaseActivity;
import org.dalol.apkdigger.model.adapter.AppsAdapter;
import org.dalol.apkdigger.model.callback.HidingScrollListener;
import org.dalol.apkdigger.model.constants.Constant;
import org.dalol.apkdigger.model.exception.ApkDiggerException;
import org.dalol.apkdigger.presenter.MainViewPresenter;

/*
 * @author Filippo Engidashet
 * @version 1.0
 * @date 7/27/2015
 *
 * MainActivity.java: {@link BaseActivity} extending the properties to Main Activity.
 * This class displays the installed app in a recyclerview {@link RecyclerView} with various callbacks
 */
public class MainActivity extends BaseActivity<Integer> implements MainViewPresenter.MainViewListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    public static final int REQUEST_CODE = 100;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppsAdapter mAdapter;
    private List<PackageInfo> mPackageInfoList;
    private ProgressDialog mProgressDialog;
    private MainViewPresenter mPresenter;

    @Override
    public Toolbar getToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(Constant.APK_DIGGER);
        mToolbar.setNavigationIcon(R.mipmap.ic_launcher);
        return mToolbar;
    }

    @Override
    protected void configViews(Intent intent, Bundle savedInstanceState) {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.appListRecycleView);

        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideToolbar();
            }

            @Override
            public void onShow() {
                showToolbar();
            }
        });

        mPackageInfoList = new ArrayList<>();
        mAdapter = new AppsAdapter(MainActivity.this, mPackageInfoList);
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new MainViewPresenter(MainActivity.this);
        mPresenter.initialize();

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.green), getResources().getColor(R.color.orange), getResources().getColor(R.color.blue));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadInstalledApps();
            }
        });
        mPresenter.loadInstalledApps();
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
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    private void showToolbar() {
        mToolbar.animate()
                .setDuration(300l)
                .translationY(0)
                .setInterpolator(DECELERATE_INTERPOLATOR)
                .start();
    }

    private void hideToolbar() {
        mToolbar.animate()
                .setDuration(400l)
                .setInterpolator(DECELERATE_INTERPOLATOR)
                .translationY(-mToolbar.getHeight())
                .start();
    }

    @Override
    protected Integer getViewResource() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(MainActivity.this, PreferencesActivity.class), REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mPresenter.loadInstalledApps();
                }
                break;
        }
    }

    @Override
    public Context getMainContext() {
        return getApplicationContext();
    }

    @Override
    public PackageManager getPkgManager() {
        return getPackageManager();
    }

    @Override
    public void onShowDialog() {
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setMessage("Retrieving installed applications...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    @Override
    public void onPublishApp(PackageInfo packageInfo) {
        mPackageInfoList.add(packageInfo);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHideDialog() {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressDialog.dismiss();
    }

    @Override
    public void showFilterPopup(View view, final int position) {
        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.popup_filters, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_export:
                        mPresenter.shareApp(position);
                        return true;
                    case R.id.menu_info:
                        mPresenter.showApplicationInfo(position);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    @Override
    public List<PackageInfo> getPackageInfoList() {
        return mPackageInfoList;
    }

    @Override
    public AppsAdapter getAppsAdapter() {
        return mAdapter;
    }


    @Override
    public void startAppIntent(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void onException(ApkDiggerException exception) {
        Toast.makeText(getApplicationContext(), "Exception in :: " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void startImplicitApp(String packageName) {
        mPresenter.startImplicitApp(packageName);
    }

    @Override
    public void onPublishSortedAppList(List<PackageInfo> packageInfos) {
        mPackageInfoList.addAll(packageInfos);
        mAdapter.notifyDataSetChanged();
    }
}
