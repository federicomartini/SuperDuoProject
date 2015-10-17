package barqsoft.footballscores.widget;


import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresProvider;
import barqsoft.footballscores.TeamMatch;
import barqsoft.footballscores.scoresAdapter;

public class ScoresCollectionWidgetService extends RemoteViewsService {

    private final static String LOG_TAG = ScoresCollectionWidgetService.class.getName();
    int mWidgetId;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.INVALID_APPWIDGET_ID);
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    class ListRemoteViewsFactory implements RemoteViewsFactory{

        private String[] mDate = new String[1];
        private Context context;
        private ArrayList<TeamMatch> teamMatches;

        public ListRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
        }


        @Override
        public void onCreate() {
            teamMatches = new ArrayList<TeamMatch>();
            updateMatches();
        }

        public void updateMatches() {
            Date fragmentdate = new Date(System.currentTimeMillis());
            SimpleDateFormat mformat = new SimpleDateFormat(getString(R.string.date_format));
            mDate[0] = mformat.format(fragmentdate);

            Cursor cursor = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(), null, null, mDate, null);
            Log.d(LOG_TAG, "Cursor size: " + cursor.getCount());
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        TeamMatch teamMatch = new TeamMatch();

                        teamMatch.setHomeTeam(cursor.getString(scoresAdapter.COL_HOME));
                        teamMatch.setHomeScores(cursor.getString(scoresAdapter.COL_HOME_GOALS));
                        teamMatch.setAwayTeam(cursor.getString(scoresAdapter.COL_AWAY));
                        teamMatch.setAwayScores(cursor.getString(scoresAdapter.COL_AWAY_GOALS));
                        teamMatch.setMatchId(cursor.getDouble(scoresAdapter.COL_ID));
                        teamMatch.setData(cursor.getString(scoresAdapter.COL_MATCHTIME));

                        teamMatches.add(teamMatch);

                        Log.d(LOG_TAG, teamMatch.getHomeTeam() + " " + teamMatch.getHomeScores() + " - " + teamMatch.getAwayScores() + " " + teamMatch.getAwayTeam());
                        cursor.moveToNext();
                    }
                }
                cursor.close();
            }

            Log.d(LOG_TAG, "Widget Data Updated");
            //AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(mWidgetId, R.layout.scores_widget_layout);

        }

        @Override
        public int getCount() {
            return teamMatches.size();
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            teamMatches = null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remote = new RemoteViews(context.getPackageName(), R.layout.scores_widget_item_layout);
            remote.setTextViewText(R.id.home_name, teamMatches.get(position).getHomeTeam());
            String homeScores = (Integer.parseInt(teamMatches.get(position).getHomeScores()) >= 0) ? teamMatches.get(position).getHomeScores() : "";
            String awayScores = (Integer.parseInt(teamMatches.get(position).getAwayScores()) >= 0) ? teamMatches.get(position).getAwayScores() : "";
            remote.setTextViewText(R.id.score_textview, homeScores + " - " + awayScores);
            remote.setTextViewText(R.id.away_name, teamMatches.get(position).getAwayTeam());
            remote.setTextViewText(R.id.data_textview, teamMatches.get(position).getData());

            Bundle extras = new Bundle();
            extras.putDouble(ScoresWidgetProvider.EXTRA_ITEM, teamMatches.get(position).getMatchId());
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            // Make it possible to distinguish the individual on-click
            // action of a given item
            remote.setOnClickFillInIntent(R.id.match_layout, fillInIntent);

            return remote;
        }

    }

}
