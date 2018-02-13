package com.willbrom.forismaticquotes;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.willbrom.forismaticquotes.data.QuoteDatabase;
import com.willbrom.forismaticquotes.data.Quote;
import com.willbrom.forismaticquotes.fragments.MainFragment;
import com.willbrom.forismaticquotes.fragments.FavoriteFragment;
import com.willbrom.forismaticquotes.utilities.JsonUtils;
import com.willbrom.forismaticquotes.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements MainFragment.OnMainFragmentInteractionListener, FavoriteFragment.OnFavoriteFragmentInteractionListener, NetworkUtils.VollyCallbackListener, ViewPager.OnPageChangeListener {

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
    @BindView(R.id.fab_share)
    FloatingActionButton fabShare;
    @BindView(R.id.fab_next_progressCircle)
    FABProgressCircle fabNextProgressCircle;
//    @BindView(R.id.heart)
//    ImageView heart;
    private boolean dataReceived = true;
    private MainFragment mainFragment = new MainFragment();
    private FavoriteFragment favoriteFragment = new FavoriteFragment();
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

        viewPager.addOnPageChangeListener(this);
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
        adapter.addFragment(favoriteFragment, "");
        viewPager.setAdapter(adapter);
    }

    @OnClick(R.id.fab_next)
    void onClickNextQuote() {
        fabNext.setEnabled(false);
        fabNextProgressCircle.show();
        URL url = NetworkUtils.getQuoteUrl("");
        Log.d(TAG, url.toString());
        NetworkUtils.getHttpResponse(this, this, url);
    }

    @OnClick(R.id.fab_share)
    void onClickShare() {
        if (quoteData != null) {
            ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle("Quote")
                    .setType("text/plain")
                    .setText(quoteData.get(0) + "- " + getAuthor(quoteData.get(1))).startChooser();
        }
    }

    private String getAuthor(String author) {
        if (!author.equals(""))
            return author;
        return "Unknown";
    }

    @Override
    public void onSuccess(String response) {
        fabNext.setEnabled(true);
        fabNextProgressCircle.hide();
        quoteData = JsonUtils.parseJson(response);
        if (mainFragment != null)
            mainFragment.displayQuote(quoteData);
    }

    @Override
    public void onFailure(String error) {
        fabNextProgressCircle.hide();
        fabNext.setEnabled(true);
        Toast.makeText(this, "this is the error " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickQuoteFav(Quote... quote) {
        new DbInsertFavAsyncTask().execute(new Pair(this, quote[0]));
        Toast.makeText(this, "fav", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetFavoriteQuotes() {
        new DbSelectFavAsyncTask().execute(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "This be the position: " + position);
        if (position == 1)
            favoriteFragment.startListener();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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

    public class DbInsertFavAsyncTask extends AsyncTask<Pair<Context, Quote>, Void ,Void> {

        @Override
        protected Void doInBackground(Pair<Context, Quote>[] pairs) {
            Context context = pairs[0].first;
            Quote quote = pairs[0].second;
            QuoteDatabase.getInstance(context).getQuoteDao().insert(quote);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public class DbSelectFavAsyncTask extends AsyncTask<Context, Void, List<Quote>> {

        @Override
        protected List<Quote> doInBackground(Context... context) {
            return QuoteDatabase.getInstance(context[0]).getQuoteDao().getAllQuotes();
        }

        @Override
        protected void onPostExecute(List<Quote> quote) {
            favoriteFragment.showQuote(quote);
        }
    }
}
