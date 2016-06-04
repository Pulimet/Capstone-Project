package net.alexandroid.network.portwatcher.ui.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.data.DbContract;
import net.alexandroid.network.portwatcher.data.DbHelper;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.services.ScanService;
import net.alexandroid.network.portwatcher.services.ScheduleService;
import net.alexandroid.network.portwatcher.ui.fragments.EditFragment;
import net.alexandroid.network.portwatcher.ui.fragments.MainHistoryFragment;
import net.alexandroid.network.portwatcher.ui.fragments.ScanFragment;
import net.alexandroid.network.portwatcher.ui.fragments.ScheduleFragment;
import net.alexandroid.network.portwatcher.ui.fragments.WatchFragment;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        MainHistoryFragment.MainFragmentInteractionListener,
        ScanFragment.ScanFragmentInteractionListener,
        WatchFragment.WatchFragmentInteractionListener,
        EditFragment.EditFragmentInteractionListener,
        ScheduleFragment.ScheduleFragmentInteractionListener {

    public static final int FRAGMENT_MAIN_HISTORY = 0;
    public static final int FRAGMENT_SCAN = 1;
    public static final int FRAGMENT_EDIT = 2;
    public static final int FRAGMENT_WATCH = 3;
    public static final int FRAGMENT_SCHEDULE = 4;

    private static final int RC_PLAY_SERVICES = 123;

    public static String strLastQuery = "google.com";

    private static int selectedFragment;

    private SearchView mSearchView;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private NavigationView mNavigationView;
    private Fragment fragment;

    private GcmNetworkManager mGcmNetworkManager;

    private InterstitialAd mInterstitialAd;
    private StartAppAd startAppAd = new StartAppAd(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "205232173", false);
        setContentView(R.layout.activity_main);

        seToolBarAndNavigation();
        initialFab();
        showMainFragment(savedInstanceState);

        mGcmNetworkManager = GcmNetworkManager.getInstance(this);
        checkPlayServicesAvailable();

        initAdMob();
    }

    private void initAdMob() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("441009D6951E7B1833F74A291BCF74D7")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                onFabClick(v);
                break;
            case R.id.snackbar_action:
                onFabActionClick();
                break;
            default:
                MyLog.d("ID: " + v.toString());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startAppAd.onBackPressed();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setQueryHint(getString(R.string.hint_search_view));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onTextSubmit(query);
                return true;
                // return true if the query has been handled by the listener, false to let the SearchView perform the default action.
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mSearchView.onActionViewCollapsed();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_history:
                replaceFragmentWith(FRAGMENT_MAIN_HISTORY, false);
                mToolbar.setSubtitle(R.string.history);
                break;
            case R.id.nav_scan:
                replaceFragmentWith(FRAGMENT_SCAN, false);
                mToolbar.setSubtitle(R.string.scan);
                break;
            case R.id.nav_edit:
                mInterstitialAd.show();
                replaceFragmentWith(FRAGMENT_EDIT, false);
                mToolbar.setSubtitle(R.string.edit);
                break;
            case R.id.nav_watchlist:
                replaceFragmentWith(FRAGMENT_WATCH, false);
                mToolbar.setSubtitle(R.string.watchlist);
                break;
            case R.id.nav_schedule:
                replaceFragmentWith(FRAGMENT_SCHEDULE, false);
                mToolbar.setSubtitle(R.string.schedule);
                break;
            case R.id.nav_share:
                actionShareApp();
                break;
            case R.id.nav_rate:
                actionRate();
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }


    private void seToolBarAndNavigation() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.getMenu().getItem(0).setChecked(true);
        mToolbar.setSubtitle(R.string.history);
    }


    // Navigation menu actions
    private void actionRate() {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rate_url) + getPackageName()));
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(marketIntent);
    }

    private void actionShareApp() {
        share(getString(R.string.share_app_msg) + " " + Uri.parse(getString(R.string.share_url) + getPackageName()));
    }


    // Fragments control
    private void showMainFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fragment = new MainHistoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, "tag" + FRAGMENT_MAIN_HISTORY).commit();
        } else {
            fragment = getSupportFragmentManager().findFragmentByTag("tag" + selectedFragment);
        }
    }

    private void replaceFragmentWith(int fragmentInt, boolean addToBackStack) {
        selectedFragment = fragmentInt;
        setFab();

        fragment = getSupportFragmentManager().findFragmentByTag("tag" + fragmentInt);
        if (fragment == null) {
            //MyLog.d("replaceFragmentWith - fragment == null");
            switch (fragmentInt) {
                case FRAGMENT_MAIN_HISTORY:
                    fragment = new MainHistoryFragment();
                    break;
                case FRAGMENT_SCAN:
                    fragment = new ScanFragment();
                    break;
                case FRAGMENT_EDIT:
                    fragment = new EditFragment();
                    break;
                case FRAGMENT_WATCH:
                    fragment = new WatchFragment();
                    break;
                case FRAGMENT_SCHEDULE:
                    fragment = new ScheduleFragment();
                    break;
                default:
                    fragment = new MainHistoryFragment();
                    break;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Slide());
        }

        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction().addToBackStack(null)
                    .replace(R.id.fragment_container, fragment, "tag" + fragmentInt)
                    .commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, "tag" + fragmentInt)
                    .commitAllowingStateLoss();
        }
    }

    // Search view
    private void onTextSubmit(String pQuery) {
        MyLog.d("onTextSubmit: " + pQuery);
        strLastQuery = pQuery;
        mSearchView.onActionViewCollapsed();
        if (selectedFragment == FRAGMENT_SCAN && fragment instanceof ScanFragment) {
            ((ScanFragment) fragment).refresh();
        } else {
            selectSideMenuItem(FRAGMENT_SCAN);
        }
    }

    private void selectSideMenuItem(int fragment) {
        MenuItem menuItem = mNavigationView.getMenu().getItem(fragment);
        menuItem.setChecked(true);
        onNavigationItemSelected(menuItem);
    }

    // FAB Control
    private void initialFab() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
    }

    private void setFab() {
        switch (selectedFragment) {
            case FRAGMENT_MAIN_HISTORY:
                setFabVisibility(true);
                mFab.setImageResource(R.drawable.ic_delete);
                break;
            case FRAGMENT_WATCH:
            case FRAGMENT_EDIT:
            case FRAGMENT_SCHEDULE:
                setFabVisibility(true);
                mFab.setImageResource(R.drawable.ic_add_wh);
                break;
            default:
                setFabVisibility(false);
        }
    }

    private void setFabVisibility(boolean visible) {
        mFab.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void onFabClick(View v) {
        switch (selectedFragment) {
            case FRAGMENT_MAIN_HISTORY:
                Snackbar.make(v, R.string.clear_history, Snackbar.LENGTH_LONG).setAction(R.string.clear, MainActivity.this).show();
                break;
            case FRAGMENT_WATCH:
                if (fragment instanceof WatchFragment) {
                    ((WatchFragment) fragment).onFabClick();
                }
                break;
            case FRAGMENT_EDIT:
                if (fragment instanceof EditFragment) {
                    ((EditFragment) fragment).onFabClick();
                }
                break;
            case FRAGMENT_SCHEDULE:
                if (fragment instanceof ScheduleFragment) {
                    ((ScheduleFragment) fragment).onFabClick();
                }
                break;
        }
    }

    private void onFabActionClick() {
        MyLog.d("snackbar_action");
        switch (selectedFragment) {
            case FRAGMENT_MAIN_HISTORY:
                if (fragment instanceof MainHistoryFragment) {
                    ((MainHistoryFragment) fragment).clearHistory();
                }
                break;
        }
    }


    // Main history fragment callbacks (History)
    @Override
    public void onItemClick(ScanItem item) {
        MyLog.d("onItemClick");
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_SCAN_ITEM, item);
        startActivity(intent);
    }

    @Override
    public void onStarClick(ScanItem item) {
        MyLog.d("onStarClick");
        Snackbar.make(mSearchView, R.string.added_ro_watchlist, Snackbar.LENGTH_SHORT).show();
        MyLog.d("Add item to watchlist, host: " + item.getStrHost());

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        ContentValues contentValues =
                DbHelper.getWatchlistContentValues(item.getStrHost(), item.getStrPorts());
        contentResolver.insert(DbContract.WatchlistEntry.CONTENT_URI, contentValues);
    }

    @Override
    public void onShareClick(ScanItem item) {
        MyLog.d("onShareClick");
        StringBuilder str = new StringBuilder();
        str.append(getString(R.string.share1));
        str.append(item.getStrHost());
        str.append(getString(R.string.share2));
        str.append(item.getStrWereOpen());
        str.append(getString(R.string.share3));
        str.append(item.getStrPorts());
        str.append(getString(R.string.share4));
        share(str.toString());
    }

    private void share(String str) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, str);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));
    }


    // ScanFragment and Watchlist callback

    @Override
    public void onStartScan(ArrayList<Integer> pList, String host, int scanId) {
        Intent intent = new Intent(MainActivity.this, ScanService.class);
        intent.putExtra(ScanService.EXTRA_HOST, host);
        intent.putExtra(ScanService.EXTRA_SCAN_ID, scanId);
        intent.putIntegerArrayListExtra(ScanService.EXTRA_PORTS, pList);
        startService(intent);
    }

    // EditFragment callbacks
    @Override
    public void onEditItemClick(String title, String ports) {
        if (fragment instanceof EditFragment) {
            ((EditFragment) fragment).dialogAddFlag = false;
            ((EditFragment) fragment).showAddOrEditDialog(title, ports);
        }
    }

    // ScheduleFragment callbacks
    @Override
    public void onScheduleItemClick(String host, String ports, String interval) {
        if (fragment instanceof ScheduleFragment) {
            ((ScheduleFragment) fragment).dialogAddFlag = false;
            ((ScheduleFragment) fragment).showAddOrEditDialog(host, ports, interval);
        }
    }

    @Override
    public void addSchedule(String pHost, String pPorts, String pInterval) {
        MyLog.d("addSchedule, host: " + pHost + "  ports: " + pPorts + "   Interval: " + pInterval);

        Bundle bundle = new Bundle();
        bundle.putString(ScheduleService.EXTRA_HOST, pHost);
        bundle.putString(ScheduleService.EXTRA_PORT, pPorts);

        PeriodicTask task = new PeriodicTask.Builder()
                .setService(ScheduleService.class)
                .setExtras(bundle)
                .setPersisted(true)
                .setTag(Utils.createAlarmTag(pHost, pPorts, pInterval))
                .setPeriod(Long.valueOf(pInterval) / 1000)
                .build();

        mGcmNetworkManager.schedule(task);
    }

    @Override
    public void removeSchedule(String pHost, String pPorts, String pInterval) {
        MyLog.d("removeSchedule, host: " + pHost + "  ports: " + pPorts + "   Interval: " + pInterval);
        mGcmNetworkManager.cancelTask(Utils.createAlarmTag(pHost, pPorts, pInterval), ScheduleService.class);
    }

    @Override
    public void updateSchedule(String pHost, String pPorts, String pInterval, String pNewHost, String pCheckedPorts, String pNewInterval) {
        removeSchedule(pHost, pPorts, pInterval);
        addSchedule(pNewHost, pCheckedPorts, pNewInterval);
    }


    // Google play services

    private void checkPlayServicesAvailable() {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int resultCode = availability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (availability.isUserResolvableError(resultCode)) {
                // Show dialog to resolve the error.
                availability.getErrorDialog(this, resultCode, RC_PLAY_SERVICES).show();
            } else {
                // Unresolvable error
                Toast.makeText(this, "Google Play Services error", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
