package barqsoft.footballscores.widget;

import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.myFetchService;


public class ScoresWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "com.example.android.scorewidget.EXTRA_ITEM";
    public static final String CLICK_ACTION = "com.example.android.scorewidget.CLICK_ACTION";
    public static final String EXTRA_REFRESH = "com.example.android.scorewidget.EXTRA_REFRESH";

    private static final String LOG_TAG = ScoresWidgetProvider.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ScoresWidgetProvider.CLICK_ACTION)) {
            double matchId = intent.getDoubleExtra(EXTRA_ITEM, 0);
            Intent matchDetailsIntent = new Intent(context, MainActivity.class);
            matchDetailsIntent.putExtra(MainActivity.MATCH_DETAIL, matchId);
            matchDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(matchDetailsIntent);
        } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) &&
                intent.hasExtra(EXTRA_REFRESH)) {

            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, ScoresWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.list_view);
            // Update remote view

            // Update list content of the widget
            // This will call onDataSetChanged() method of WidgetDisplay class
        }

        super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        String[] mDate = new String[1];
        Date fragmentdate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat(context.getString(R.string.date_time_format));
        mDate[0] = mformat.format(fragmentdate);

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {

            Intent intent = new Intent(context, ScoresCollectionWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.scores_widget_layout);

            Intent intentSync = new Intent(context, myFetchService.class);
            intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE); //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.
            //intentSync.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intentSync.putExtra(EXTRA_REFRESH, 0);
            PendingIntent pendingSync = PendingIntent.getService(context, 0, intentSync, 0); //You need to specify a proper flag for the intent. Or else the intent will become deleted.
            rv.setOnClickPendingIntent(R.id.reload_icon_view, pendingSync);

            rv.setTextViewText(R.id.header_view, context.getString(R.string.widget_header_title));
            rv.setTextViewText(R.id.day_view, mDate[0]);

            Intent clickIntent = new Intent(context, ScoresWidgetProvider.class);
            clickIntent.setAction(ScoresWidgetProvider.CLICK_ACTION);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_view, clickPendingIntent);

            rv.setRemoteAdapter(R.id.list_view, intent);
            rv.setEmptyView(R.id.list_view, R.id.empty_view);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
    }

}
