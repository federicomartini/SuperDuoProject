package barqsoft.footballscores;

import android.app.Activity;
import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 5;
    public ViewPager mPagerHandler;
    private myPageAdapter mPagerAdapter;
    private MainScreenFragment[] viewFragments;
    public static final String LOG_TAG = PagerFragment.class.getName();
    public PagerFragmentCallback mCallback;
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            Log.d(LOG_TAG, "Position changed");
            mCallback.onPositionChange(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (PagerFragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PagerFragmentCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        viewFragments = new MainScreenFragment[NUM_PAGES];
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());

        for (int i = 0;i < NUM_PAGES;i++)
        {
            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            SimpleDateFormat mformat = new SimpleDateFormat(getString(R.string.date_format));

            if (ViewCompat.getLayoutDirection(rootView) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                viewFragments[NUM_PAGES-i-1] = new MainScreenFragment();
                viewFragments[NUM_PAGES-i-1].setFragmentDate(mformat.format(fragmentdate));
            } else {
                viewFragments[i] = new MainScreenFragment();
                viewFragments[i].setFragmentDate(mformat.format(fragmentdate));
            }

        }

        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setOnPageChangeListener(pageChangeListener);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }


    private class myPageAdapter extends FragmentStatePagerAdapter
    {
        public final String LOG_TAG = myPageAdapter.class.getName();

        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm)
        {
            super(fm);
        }
        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position)
        {
            return getDayName(getActivity(),System.currentTimeMillis()+((position-2)*86400000));
        }
        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return context.getString(R.string.yesterday);
            }
            else
            {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat(context.getString(R.string.day_format_pagerfragment));
                return dayFormat.format(dateInMillis);
            }
        }
    }

    public interface PagerFragmentCallback {
        void onPositionChange(int position);
    }
}
