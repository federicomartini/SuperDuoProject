package barqsoft.footballscores.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.widget.ScoresWidgetProvider;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class myFetchService extends IntentService
{
    public static final String LOG_TAG = myFetchService.class.getName();
    String[] mDate;

    public myFetchService()
    {
        super(myFetchService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        getData(getResources().getString(R.string.prev_days_matches_command_myfetchservice));
        getData(getResources().getString(R.string.next_days_matches_command_myfetchservice));

        Intent updateIntent = new Intent(this, ScoresWidgetProvider.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ScoresWidgetProvider.class));
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(updateIntent);

        return;
    }

    private void getData (String timeFrame)
    {
        //Creating fetch URL
        final String BASE_URL = getResources().getString(R.string.base_url_myfetchservice) + "/" + getString(R.string.fixtures_key_myfetchservice); //Base URL
        final String QUERY_TIME_FRAME = getResources().getString(R.string.query_timeframe_myfetchservice); //Time Frame parameter to determine days
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString()); //log spam
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod(getResources().getString(R.string.http_get_command_myfetchservice));
            m_connection.addRequestProperty(getResources().getString(R.string.auth_token_command_myfetchservice)
                    ,getString(R.string.api_key));
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"Exception here: " + e.getMessage());
        }
        finally {
            if(m_connection != null)
            {
                m_connection.disconnect();
            }
            if (reader != null)
            {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, "Error Closing Stream");
                }
            }
        }
        try {
            if (JSON_data != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(JSON_data).getJSONArray(getResources().getString(R.string.fixtures_key_myfetchservice));
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }


                processJSONdata(JSON_data, getApplicationContext(), true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }
    }
    private void processJSONdata (String JSONdata,Context mContext, boolean isReal)
    {
        //JSON data
        // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
        // be updated. Feel free to use the codes
        final String BUNDESLIGA1 = getString(R.string.bundensliga1_code_myfetchservice);
        final String BUNDESLIGA2 = getString(R.string.bundensliga2_code_myfetchservice);
        final String LIGUE1 = getString(R.string.ligue1_code_myfetchservice);
        final String LIGUE2 = getString(R.string.ligue2_code_myfetchservice);
        final String PREMIER_LEAGUE = getString(R.string.premierleague_code_myfetchservice);
        final String PRIMERA_DIVISION = getString(R.string.primeradivision_code_myfetchservice);
        final String SEGUNDA_DIVISION = getString(R.string.segundadivision_code_myfetchservice);
        final String SERIE_A = getString(R.string.seriea_code_myfetchservice);
        final String PRIMERA_LIGA = getString(R.string.primeraliga_code_myfetchservice);
        final String BUNDESLIGA3 = getString(R.string.bundesliga3_code_myfetchservice);
        final String EREDIVISIE = getString(R.string.eredivisie_code_myfetchservice);
        final String DUMMYLEAGUE = getString(R.string.dummyleague_code_myfetchservice);


        final String SEASON_LINK = getResources().getString(R.string.base_url_myfetchservice) + "/" + getString(R.string.soccerseason_key_myfetchservice) + "/";
        final String MATCH_LINK = getResources().getString(R.string.base_url_myfetchservice) + "/" + getString(R.string.fixtures_key_myfetchservice) + "/";
        final String FIXTURES = getString(R.string.fixtures_key_myfetchservice);
        final String LINKS = getString(R.string.links_key_myfetchservice);
        final String SOCCER_SEASON = getString(R.string.soccerseason_key_myfetchservice);
        final String SELF = getString(R.string.self_key_myfetchservice);
        final String MATCH_DATE = getString(R.string.date_key_myfetchservice);
        final String HOME_TEAM = getString(R.string.homeTeamName_key_myfetchservice);
        final String AWAY_TEAM = getString(R.string.awayTeamName_key_myfetchservice);
        final String RESULT = getString(R.string.result_key_myfetchservice);
        final String HOME_GOALS = getString(R.string.goalsHomeTeam_key_myfetchservice);
        final String AWAY_GOALS = getString(R.string.goalsAwayTeam_key_myfetchservice);;
        final String MATCH_DAY = getString(R.string.matchday_key_myfetchservice);

        //Match data
        String League = null;
        String mDate = null;
        String mTime = null;
        String Home = null;
        String Away = null;
        String Home_goals = null;
        String Away_goals = null;
        String match_id = null;
        String match_day = null;


        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);


            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());
            for(int i = 0;i < matches.length();i++)
            {

                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString("href");
                League = League.replace(SEASON_LINK,"");
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if(     League.equals(PREMIER_LEAGUE)       ||
                        League.equals(SERIE_A)              ||
                        League.equals(BUNDESLIGA1)          ||
                        League.equals(BUNDESLIGA2)          ||
                        League.equals(BUNDESLIGA3)          ||
                        League.equals(PRIMERA_DIVISION)     ||
                        League.equals(SEGUNDA_DIVISION)     ||
                        League.equals(LIGUE1)               ||
                        League.equals(LIGUE2)               ||
                        League.equals(PRIMERA_LIGA)         ||
                        League.equals(EREDIVISIE)
                        //League.equals(DUMMYLEAGUE)
                                                            )
                {
                    match_id = match_data.getJSONObject(LINKS).getJSONObject(SELF).
                            getString(getString(R.string.match_id_json_key_myfetchservice));
                    match_id = match_id.replace(MATCH_LINK, "");
                    if(!isReal){
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        match_id=match_id+Integer.toString(i);
                    }

                    mDate = match_data.getString(MATCH_DATE);
                    mTime = mDate.substring(mDate.indexOf(getString(R.string.begin_match_time_json_key)) + 1, mDate.indexOf(getString(R.string.end_match_time_json_key)));
                    mDate = mDate.substring(0,mDate.indexOf(getString(R.string.begin_match_time_json_key)));
                    SimpleDateFormat match_date = new SimpleDateFormat(getString(R.string.date_time_with_seconds_format));
                    match_date.setTimeZone(TimeZone.getTimeZone(getString(R.string.utc_key_myfetcservice)));
                    try {
                        Date parseddate = match_date.parse(mDate+mTime);
                        SimpleDateFormat new_date = new SimpleDateFormat(getString(R.string.date_time_format));
                        new_date.setTimeZone(TimeZone.getDefault());
                        mDate = new_date.format(parseddate);
                        mTime = mDate.substring(mDate.indexOf(getString(R.string.date_time_format_divider)) + 1);
                        mDate = mDate.substring(0,mDate.indexOf(getString(R.string.date_time_format_divider)));

                        if(!isReal){
                            Date fragmentdate;

                            if (i > 4) {
                                fragmentdate = new Date(System.currentTimeMillis());
                            } else {
                                fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
                            }
                            //This if statement changes the dummy data's date to match our current date range.
                            SimpleDateFormat mformat = new SimpleDateFormat(getString(R.string.date_time_format));
                            mDate=mformat.format(fragmentdate);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(LOG_TAG,e.getMessage());
                    }


                    Home = match_data.getString(HOME_TEAM);
                    Away = match_data.getString(AWAY_TEAM);
                    Home_goals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                    Away_goals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                    match_day = match_data.getString(MATCH_DAY);
                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.scores_table.MATCH_ID,match_id);
                    match_values.put(DatabaseContract.scores_table.DATE_COL,mDate);
                    match_values.put(DatabaseContract.scores_table.TIME_COL,mTime);
                    match_values.put(DatabaseContract.scores_table.HOME_COL,Home);
                    match_values.put(DatabaseContract.scores_table.AWAY_COL,Away);
                    match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL,Home_goals);
                    match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL,Away_goals);
                    match_values.put(DatabaseContract.scores_table.LEAGUE_COL,League);
                    match_values.put(DatabaseContract.scores_table.MATCH_DAY,match_day);
                    //log spam

                    //Log.v(LOG_TAG,match_id);
                    //Log.v(LOG_TAG,mDate);
                    //Log.v(LOG_TAG,mTime);
                    //Log.v(LOG_TAG,Home);
                    //Log.v(LOG_TAG,Away);
                    //Log.v(LOG_TAG,Home_goals);
                    //Log.v(LOG_TAG,Away_goals);

                    values.add(match_values);
                }
            }
            int inserted_data = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            inserted_data = mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI,insert_data);

            //Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(inserted_data));
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }

    }

}

