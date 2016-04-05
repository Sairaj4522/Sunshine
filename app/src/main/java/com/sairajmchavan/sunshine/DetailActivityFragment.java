package com.sairajmchavan.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    private final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    private String _forecastStr;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null){
            _forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView) rootView.findViewById(R.id.detail_text)).setText(_forecastStr);
        }



        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(shareActionProvider != null){
            shareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, _forecastStr + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

}
