package com.willbrom.forismaticquotes;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.willbrom.forismaticquotes.data.QuoteDatabase;
import com.willbrom.forismaticquotes.data.Quote;
import com.willbrom.forismaticquotes.fragments.MainFragment;
import com.willbrom.forismaticquotes.fragments.BlankFragment2;
import com.willbrom.forismaticquotes.utilities.JsonUtils;
import com.willbrom.forismaticquotes.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements MainFragment.OnMainFragmentInteractionListener, BlankFragment2.OnFragmentInteractionListener2, NetworkUtils.VollyCallbackListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String QUOTE_KEY = "quote_key";
    private static final String QUOTE_AUTHOR_KEY = "quote_author_key";
    private static final String  DATA_RECEIVED_KEY = "data_received_key";
    private static boolean showData;
    private ArrayList<String> quoteData = new ArrayList<>();

    @BindView(R.id.parent)
    CoordinatorLayout parentViewGroup;
    @BindView(R.id.toolBar)
    Toolbar toolbar;
    @BindView(R.id.tab)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.fab_next)
    FloatingActionButton fabNext;
    @BindView(R.id.fab_next_progressCircle)
    FABProgressCircle fabNextProgressCircle;
//    @BindView(R.id.heart)
//    ImageView heart;
    private boolean dataReceived = true;
    private MainFragment mainFragment = new MainFragment();
    private boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        if (savedInstanceState != null) {
//            quoteTextView.setText(savedInstanceState.getString(QUOTE_KEY));
//            quoteAuthorTextView.setText(savedInstanceState.getString(QUOTE_AUTHOR_KEY));
//            dataReceived = savedInstanceState.getBoolean(DATA_RECEIVED_KEY);
//        }

        setSupportActionBar(toolbar);
        setupViewHolder(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setTabIcon();
    }

    private void setTabIcon() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_school_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite_black_24dp);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
//        if (hasFocus) {
//            if (dataReceived)
//                fabProgressCircle.clearFocus();
//            else
//                fabProgressCircle.show();
//        }
    }

//    @OnClick(R.id.heart)
//    void onClickHeart() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            isChecked = !isChecked;
//            final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
//            heart.setImageState(stateSet, true);
//        } else {
//            heart.setImageResource(R.drawable.ic_heart_red);
//        }
//
//        Toast.makeText(this, "hohoho", Toast.LENGTH_SHORT).show();
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putString(QUOTE_KEY, quoteTextView.getText().toString());
//        outState.putString(QUOTE_AUTHOR_KEY, quoteAuthorTextView.getText().toString());
//        outState.putBoolean(DATA_RECEIVED_KEY, dataReceived);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupViewHolder(ViewPager viewPager) {
        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mainFragment, "");
        adapter.addFragment(new BlankFragment2(), "");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @OnClick(R.id.fab_next)
    void onClickNextQuote() {
        fabNext.setEnabled(false);
        fabNextProgressCircle.show();
        URL url = NetworkUtils.getQuoteUrl("");
        Log.d(TAG, url.toString());
        NetworkUtils.getHttpResponse(this, this, url);
    }

    @Override
    public void onSuccess(String response) {
        fabNext.setEnabled(true);
        fabNextProgressCircle.hide();
        if (mainFragment != null)
            mainFragment.displayQuote(JsonUtils.parseJson(response));
    }

    @Override
    public void onFailure(String error) {
        fabNextProgressCircle.hide();
        fabNext.setEnabled(true);
        Toast.makeText(this, "this is the error " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickQuoteFav(String... quote) {

    }

    class ViewpagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentListTitle = new ArrayList<>();

        public ViewpagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentListTitle.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentListTitle.add(title);
        }
    }

//    public void onClickFavorite(View view) {
//        fabFavProgressCircle.show();
//        new DbAsyncTask().execute(new Quote(quoteTextView.getText().toString(), quoteAuthorTextView.getText().toString()));
//    }

    public class DbAsyncTask extends AsyncTask<Quote, Void ,Void> {
        @Override
        protected Void doInBackground(Quote... quotes) {
            QuoteDatabase.getInstance(getApplicationContext()).getQuoteDao().insert(quotes[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            fabFavProgressCircle.beginFinalAnimation();
        }
    }
}
