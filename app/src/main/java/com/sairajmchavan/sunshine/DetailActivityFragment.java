package com.sairajmchavan.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sairajmchavan.sunshine.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int LOADER_ID = 1;

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    private final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    private String mForecast;
    private ShareActionProvider mShareActionProvider;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_PRESSURE = 6;
    static final int COL_WEATHER_WIND_SPEED = 7;
    static final int COL_WEATHER_DEGREES = 8;
    static final int COL_WEATHER_CONDITION_ID = 9;

    private TextView mDayView;
    private TextView mDateView;
    private ImageView mIconView;
    private TextView mDescView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mPressureView;
    private TextView mWindView;


    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDayView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDescView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_temp_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_temp_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(mForecast != null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");

        Intent intent = getActivity().getIntent();

        if(intent == null){
            return  null;
        }

        return new CursorLoader(getContext(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");

        if(data != null && data.moveToNext()) {

            long dateInMillis = data.getLong(COL_WEATHER_DATE);
            String dateText = Utility.getFormattedMonthDay(getActivity(),dateInMillis);

            mDayView.setText(Utility.getDayName(getContext(), dateInMillis));
            mDateView.setText(dateText);

            // Read weather id from cursor and update icon in the view
            int iconID = data.getInt(COL_WEATHER_CONDITION_ID);
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(iconID));

            String shortDesc = data.getString(COL_WEATHER_DESC);
            mDescView.setText(shortDesc);

            // Read user selected units from preference
            boolean isMetric = Utility.isMetric(getActivity());

            // Read high temperature from cursor and update view
            String high = Utility.formatTemperature(getContext(), data.getLong(COL_WEATHER_MAX_TEMP),
                            isMetric);
            mHighTempView.setText(high);

            // Read low temperature from cursor and update view
            String low = Utility.formatTemperature(getContext(), data.getLong(COL_WEATHER_MIN_TEMP),
                            isMetric);
            mLowTempView.setText(low);

            // Read humidity from cursor and update view
            float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

            // Read wind speed and wind direction from cursor and update view
            float windSpeed = data.getLong(COL_WEATHER_WIND_SPEED);
            float windDir = data.getFloat(COL_WEATHER_DEGREES);
            mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeed, windDir));

            // Read pressure from cursor and update view
            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
            mPressureView.setText(getActivity().getString(R.string.format_pressure,pressure));

            // We still need this for the share intent
            mForecast = String.format("%s - %s - %s/%s", dateText, shortDesc, high, low);

            // If onCreateOptionsMenu already happened, we need to update share intent now
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }
}
