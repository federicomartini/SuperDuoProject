package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;
    private String mEan;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    public static final String ABOUT_FRAGMENT = "ABOUT_FRAGMENT";
    public static final String ADD_BOOK_FRAGMENT = "ADD_BOOK_FRAGMENT";
    public static final String BOOKS_LIST_FRAGMENT = "BOOKS_LIST_FRAGMENT";
    public static final String BOOKS_DETAIL_FRAGMENT = "BOOK_DETAIL_FRAGMENT";
    public static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();

        if(IS_TABLET){
            setContentView(R.layout.activity_main_tablet);
            //If rotating from Portrait to Land
            if (savedInstanceState != null && findViewById(R.id.right_container) != null) {
                mEan = savedInstanceState.getString("EAN");
                if(mEan != null) {
                    getSupportFragmentManager().popBackStack(BOOKS_DETAIL_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Bundle args = new Bundle();
                    args.putString(BookDetail.EAN_KEY, mEan);

                    BookDetail fragmentBookDetail = new BookDetail();
                    fragmentBookDetail.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.right_container, fragmentBookDetail, BOOKS_DETAIL_FRAGMENT)
                            .addToBackStack(BOOKS_DETAIL_FRAGMENT)
                            .commit();
                }

            }
        }else {
            setContentView(R.layout.activity_main);
        }

        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever,filter);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;
        String fragmentTag;

        switch (position){
            default:
                fragmentTag = BOOKS_LIST_FRAGMENT;
                nextFragment = new ListOfBooks();
                mEan=null;
                break;
            case 1:
                fragmentTag = ADD_BOOK_FRAGMENT;
                nextFragment = new AddBook();
                mEan=null;
                break;
            case 2:
                fragmentTag = ABOUT_FRAGMENT;
                nextFragment = new About();
                mEan=null;
                break;

        }

        if (!fragmentManager.popBackStackImmediate(fragmentTag, 0)) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, nextFragment, fragmentTag)
                    .addToBackStack(fragmentTag)
                    .commit();
        }

        Log.d(LOG_TAG, "Back Stack Count: " + getSupportFragmentManager().getBackStackEntryCount());
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();

        mEan = ean;
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if(findViewById(R.id.right_container) != null){
            id = R.id.right_container;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment, BOOKS_DETAIL_FRAGMENT)
                .addToBackStack(BOOKS_DETAIL_FRAGMENT)
                .commit();

    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("EAN", mEan);
    }

    public void goBack(View view){
        getSupportFragmentManager().popBackStack();
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()<2){
            finish();
        }
        super.onBackPressed();
    }


}