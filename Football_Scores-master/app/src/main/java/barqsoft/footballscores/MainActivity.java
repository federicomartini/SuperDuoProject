package barqsoft.footballscores;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements PagerFragment.PagerFragmentCallback
{
    public static final String MATCH_DETAIL = "com.example.android.mainactivity.MATCH_DETAIL";
    public static int selected_match_id;
    public static int current_fragment = 2;
    public static String LOG_TAG = MainActivity.class.getName();
    private final String save_tag = "Save Test";
    private PagerFragment my_main;
    private boolean mStartFragment = false;
    private static MainActivity mInstance;
    private boolean mWasCalled = false;

    public MainActivity() {
        mInstance = this;
    }

    @Override
    public void onPositionChange(int position) {
        current_fragment = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!mInstance.mWasCalled) {
            if (savedInstanceState == null) {
                my_main = new PagerFragment();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, my_main)
                        .commit();

            } else {
                current_fragment = savedInstanceState.getInt("Pager_Current");
                selected_match_id = savedInstanceState.getInt("Selected_match");
                my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, "my_main");
            }

            mInstance.mWasCalled = true;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();

        if (intent.hasExtra(MATCH_DETAIL)) {
            double matchId = intent.getDoubleExtra(MATCH_DETAIL, 0);
            current_fragment = 2;
            selected_match_id = (int) matchId;
            intent.removeExtra(MATCH_DETAIL);

            getSupportFragmentManager().beginTransaction()
                    .detach(my_main)
                    .attach(my_main)
                    .commitAllowingStateLoss();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.v(save_tag,"will save");
        Log.v(save_tag,"fragment: "+String.valueOf(my_main.mPagerHandler.getCurrentItem()));
        Log.v(save_tag,"selected id: "+selected_match_id);
        outState.putInt("Pager_Current",my_main.mPagerHandler.getCurrentItem());
        outState.putInt("Selected_match",selected_match_id);
        getSupportFragmentManager().putFragment(outState, "my_main", my_main);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.v(save_tag,"will retrive");
        Log.v(save_tag,"fragment: "+String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(save_tag,"selected id: "+savedInstanceState.getInt("Selected_match"));
        current_fragment = savedInstanceState.getInt("Pager_Current");
        selected_match_id = savedInstanceState.getInt("Selected_match");
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,"my_main");
        super.onRestoreInstanceState(savedInstanceState);
    }

}
