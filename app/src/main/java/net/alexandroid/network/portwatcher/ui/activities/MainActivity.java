package net.alexandroid.network.portwatcher.ui.activities;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.ui.fragments.EditFragment;
import net.alexandroid.network.portwatcher.ui.fragments.MainHistoryFragment;
import net.alexandroid.network.portwatcher.ui.fragments.ScanFragment;
import net.alexandroid.network.portwatcher.ui.fragments.ScheduleFragment;
import net.alexandroid.network.portwatcher.ui.fragments.WatchFragment;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        MainHistoryFragment.OnListOfMainFragmentInteractionListener {


    public static final int FRAGMENT_MAIN_HISTORY = 0;
    public static final int FRAGMENT_SCAN = 1;
    public static final int FRAGMENT_EDIT = 2;
    public static final int FRAGMENT_WATCH = 3;
    public static final int FRAGMENT_SCHEDULE = 4;

    private static int selectedFragment;
    private static String strLastQuery;

    private SearchView mSearchView;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seToolBarAndNavigation();
        initialFab();
        showMainFragment(savedInstanceState);
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
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
            return true;
        }

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
                MyLog.d("nav_share");
                // TODO share action
                break;
            case R.id.nav_rate:
                MyLog.d("nav_rate");
                // TODO rate action
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


    // Fragments control
    private void showMainFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new MainHistoryFragment(), "tag" + FRAGMENT_MAIN_HISTORY).commit();
        }
    }

    private void replaceFragmentWith(int fragmentInt, boolean addToBackStack) {
        selectedFragment = fragmentInt;
        setFab();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("tag" + fragmentInt);
        if (fragment == null) {
            MyLog.d("replaceFragmentWith - fragment == null");
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

        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction().addToBackStack(null)
                    .replace(R.id.fragment_container, fragment, "tag" + fragmentInt).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, "tag" + fragmentInt).commitAllowingStateLoss();
        }
    }

    // Search view
    private void onTextSubmit(String pQuery) {
        MyLog.d("onTextSubmit: " + pQuery);
        strLastQuery = pQuery;
        mSearchView.onActionViewCollapsed();
        MenuItem menuItem = mNavigationView.getMenu().getItem(FRAGMENT_SCAN);
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
            case FRAGMENT_SCAN:
                setFabVisibility(true);
                mFab.setImageResource(R.drawable.ic_menu_search_white);
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
        }
    }

    private void onFabActionClick() {
        switch (selectedFragment) {
            case FRAGMENT_MAIN_HISTORY:
                MyLog.d("snackbar_action");
                break;
        }
    }


    // Main history fragment callbacks (History)
    @Override
    public void onItemClick(ScanItem item) {
        MyLog.d("onItemClick");
        // TODO Show dialog window with scan results and option to rescan
    }

    @Override
    public void onStarClick(ScanItem item) {
        // TODO Add Scan to watchlist
        MyLog.d("onStarClick");
    }

    @Override
    public void onShareClick(ScanItem item) {
        // TODO Share results of scan
        MyLog.d("onShareClick");
    }
}
