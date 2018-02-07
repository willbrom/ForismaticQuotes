package com.willbrom.forismaticquotes;

import android.arch.persistence.room.Room;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.willbrom.forismaticquotes.data.AppDatabase;
import com.willbrom.forismaticquotes.data.Quote;
import com.willbrom.forismaticquotes.utilities.JsonUtils;
import com.willbrom.forismaticquotes.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements NetworkUtils.VollyCallbackListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String QUOTE_KEY = "quote_key";
    private static final String QUOTE_AUTHOR_KEY = "quote_author_key";
    private static final String  DATA_RECEIVED_KEY = "data_received_key";
    private static boolean showData;
    private ArrayList<String> quoteData = new ArrayList<>();
    @BindView(R.id.title_textView)
    TextView titleTextView;
    @BindView(R.id.quoteText_textView)
    TextView quoteTextView;
    @BindView(R.id.quoteAuthor_textView)
    TextView quoteAuthorTextView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.quote_cardView)
    CardView quoteCardView;
    @BindView(R.id.fab_progressCircle)
    FABProgressCircle fabProgressCircle;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.textView)
    TextView textView;
    private boolean dataReceived = true;
    private AppDatabase db;
    private Quote quote;
    private int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        quoteTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Merienda-Bold.ttf"));
        quoteAuthorTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Rancho-Regular.ttf"));
        titleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AmaticSC-Bold.ttf"));

        if (savedInstanceState != null) {
            quoteTextView.setText(savedInstanceState.getString(QUOTE_KEY));
            quoteAuthorTextView.setText(savedInstanceState.getString(QUOTE_AUTHOR_KEY));
            dataReceived = savedInstanceState.getBoolean(DATA_RECEIVED_KEY);
        }

        quote = new Quote();
        db = Room.databaseBuilder(this, AppDatabase.class, "quote-database").build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showData = true;
                new DbAsyncTask().execute(quote);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            if (dataReceived)
                fabProgressCircle.clearFocus();
            else
                fabProgressCircle.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(QUOTE_KEY, quoteTextView.getText().toString());
        outState.putString(QUOTE_AUTHOR_KEY, quoteAuthorTextView.getText().toString());
        outState.putBoolean(DATA_RECEIVED_KEY, dataReceived);
        super.onSaveInstanceState(outState);
    }

    public void onClickNextQuote(View view) {
        fab.setEnabled(false);
        fabProgressCircle.show();
        URL url = NetworkUtils.getQuoteUrl("");
        NetworkUtils.getHttpResponse(this, this, url);
        Log.d(TAG, url.toString());
    }

    private void displayQuote() {
        if (quoteData != null) {
            quote.id = id;
            quote.quoteText = quoteData.get(0);
            quote.quoteAuthor = quoteData.get(1);
            id++;

            new DbAsyncTask().execute(quote);

            quoteTextView.setText(quoteData.get(0));
            if (!quoteData.get(1).equals(""))
                quoteAuthorTextView.setText(quoteData.get(1));
            else
                quoteAuthorTextView.setText("Unknown");
        }
    }

    @Override
    public void onSuccess(String response) {
        fab.setEnabled(true);
        dataReceived = true;
        fabProgressCircle.hide();
        quoteData = JsonUtils.parseJson(response);
        displayQuote();
    }

    @Override
    public void onFailure(String error) {
        dataReceived = false;
        fabProgressCircle.hide();
        fab.setEnabled(true);
        Toast.makeText(this, "this is the error " + error, Toast.LENGTH_SHORT).show();
    }

    public class DbAsyncTask extends AsyncTask<Quote, Void ,Void> {

        String data = "";
        @Override
        protected Void doInBackground(Quote... quotes) {
            if (MainActivity.showData) {
                for (int i = 0; i < db.quoteDao().getAll().size(); i++) {
                    data += db.quoteDao().getAll().get(i).quoteText;
                }
            } else
                db.quoteDao().insertAll(quote);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (MainActivity.showData) {
                textView.setText(data);
                showData = false;
            }
        }
    }
}
