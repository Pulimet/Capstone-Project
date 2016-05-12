package net.alexandroid.network.portwatcher.ui.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import net.alexandroid.network.portwatcher.ui.fragments.MainHistoryFragment;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        MainHistoryFragment.OnListOfMainFragmentInteractionListener {


    public static final int FRAGMENT_MAIN = 0;

    private  SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seToolBArAndNavigation();
        setFab();
        showMainFragment(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "Clear history?", Snackbar.LENGTH_LONG)
                        .setAction("Clear", MainActivity.this).show();
                break;
            case R.id.snackbar_action:
                MyLog.d("snackbar_action");
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
                break;
            case R.id.nav_scan:
                break;
            case R.id.nav_edit:
                break;
            case R.id.nav_watchlist:
                break;
            case R.id.nav_schedule:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_rate:
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void seToolBArAndNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

    }

    private void showMainFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new MainHistoryFragment(), "tag" + FRAGMENT_MAIN).commit();
        }
    }


    private void onTextSubmit(String pQuery) {
        mSearchView.clearFocus();
        MyLog.d("onTextSubmit: " + pQuery);
    }

    // FAB Control
    private void setFab() {
        findViewById(R.id.fab).setOnClickListener(this);
    }


    // Main fragment callbacks (History)
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
