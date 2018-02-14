package com.willbrom.forismaticquotes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    @BindView(R.id.fab_next_progressCircle)
    FABProgressCircle fabNextProgressCircle;
//    @BindView(R.id.heart)
//    ImageView heart;
    private boolean dataReceived = true;
    private MainFragment mainFragment = new MainFragment();
    private FavoriteFragment favoriteFragment = new FavoriteFragment();
    private boolean isChecked;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewPager.addOnPageChangeListener(this);
        setSupportActionBar(toolbar);
        setupViewHolder(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setTabIcon();
        initializeQuoteData();
    }

    private void initializeQuoteData() {
        quoteData.add(getString(R.string.initial_quote));
        quoteData.add(getString(R.string.initial_author));
    }

    private void setTabIcon() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_white_24px);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite_white_24px);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_share:
                onShare(quoteData);
                return true;
            case R.id.item_info:
                showInfoDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        final Dialog infoDialog = new Dialog(this);
        infoDialog.setContentView(R.layout.dialog_info);
        infoDialog.setTitle(getString(R.string.info_dialog_title));

        TextView infoText = (TextView) infoDialog.findViewById(R.id.info_textView);
        TextView infoText1 = (TextView) infoDialog.findViewById(R.id.info_textView1);

        infoText.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/JustAnotherHand-Regular.ttf"));
        infoText1.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/JustAnotherHand-Regular.ttf"));

        Button cancelBtn = (Button) infoDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog.dismiss();
            }
        });

        infoDialog.show();
    }

    private void setupViewHolder(ViewPager viewPager) {
        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mainFragment, "");
        adapter.addFragment(favoriteFragment, "");
        viewPager.setAdapter(adapter);
    }

    @OnClick(R.id.fab_next)
    void onClickNextQuote() {
        isLoading = true;
        fabNext.setEnabled(false);
        fabNextProgressCircle.show();
        URL url = NetworkUtils.getQuoteUrl("");
        Log.d(TAG, url.toString());
        NetworkUtils.getHttpResponse(this, this, url);
    }

    private void onShare(List<String> quoteData) {
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
        Log.d(TAG, response);
        isLoading = false;
        resetFab();
        mainFragment.resetHeart();
        quoteData = JsonUtils.parseJson(response);
        if (mainFragment != null)
            mainFragment.displayQuote(quoteData);
    }

    @Override
    public void onFailure(String error) {
        isLoading = false;
        resetFab();
        Snackbar.make(parentViewGroup, getString(R.string.no_internet_con), Snackbar.LENGTH_SHORT).show();
    }

    private void resetFab() {
        fabNextProgressCircle.hide();
        fabNext.setEnabled(true);
    }

    @Override
    public void onClickQuoteFav(Quote... quote) {
        new DbInsertFavAsyncTask().execute(new Pair(this, quote[0]));
    }

    @Override
    public void onGetFavoriteQuotes() {
        new DbSelectFavAsyncTask().execute(this);
    }

    @Override
    public void onDeleteFavoriteQuote(Quote quote) {
        new DbDeleteFavAsyncTask().execute(new Pair<Context, Quote>(this, quote));
    }

    @Override
    public void onShareFavoriteQuote(Quote quote) {
        List<String> quoteDate = new ArrayList<>();
        quoteDate.add(quote.quoteText);
        quoteDate.add(quote.quoteAuthor);
        onShare(quoteDate);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "This be the position: " + position);
        if (position == 1) {
            hideFab(true);
            favoriteFragment.startListener();
        } else {
            hideFab(false);
        }
    }

    private void hideFab(boolean hide) {
        if (hide) {
            fabNextProgressCircle.setVisibility(View.INVISIBLE);
            fabNext.hide();
        } else {
            fabNextProgressCircle.setVisibility(View.VISIBLE);
            if (isLoading)
                fabNextProgressCircle.show();
            fabNext.show();
        }
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

    public class DbInsertFavAsyncTask extends AsyncTask<Pair<Context, Quote>, Void , Void> {

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

    public class DbDeleteFavAsyncTask extends AsyncTask<Pair<Context, Quote>, Void, Void> {

        @Override
        protected Void doInBackground(Pair<Context, Quote>[] pairs) {
            Context context = pairs[0].first;
            Quote quote = pairs[0].second;
            QuoteDatabase.getInstance(context).getQuoteDao().delete(quote);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            favoriteFragment.startListener();
        }
    }
}
