package com.sairajmchavan.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {
    private Uri mUri;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mUri != null){
            outState.putParcelable(DetailFragment.DETAIL_URI, mUri);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState == null){

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            mUri = getIntent().getData();

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, mUri);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, detailFragment)
                    .commit();
        } else if(savedInstanceState.containsKey(DetailFragment.DETAIL_URI)){
            mUri = savedInstanceState.getParcelable(DetailFragment.DETAIL_URI);

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, mUri);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.weather_detail_container, detailFragment)
                                       .commit();
        }

        //Setup up button in action bar to enable in app navigation
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this,com.sairajmchavan.sunshine.SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
