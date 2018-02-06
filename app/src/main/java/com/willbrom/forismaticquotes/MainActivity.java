package com.willbrom.forismaticquotes;

import android.graphics.Typeface;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.willbrom.forismaticquotes.utilities.JsonUtils;
import com.willbrom.forismaticquotes.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NetworkUtils.VollyCallbackListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String QUOTE_KEY = "quote_key";
    private static final String QUOTE_AUTHOR_KEY = "quote_author_key";
    private ArrayList<String> quoteData = new ArrayList<>();
    @BindView(R.id.title_textView) TextView titleTextView;
    @BindView(R.id.quoteText_textView) TextView quoteTextView;
    @BindView(R.id.quoteAuthor_textView) TextView quoteAuthorTextView;

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
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(QUOTE_KEY, quoteTextView.getText().toString());
        outState.putString(QUOTE_AUTHOR_KEY, quoteAuthorTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void onClickNextQuote(View view) {
        URL url = NetworkUtils.getQuoteUrl("");
        NetworkUtils.getHttpResponse(this, this, url);
        Log.d(TAG, url.toString());
    }

    private void displayQuote() {
        if (quoteData != null) {
            quoteTextView.setText(quoteData.get(0));
            if (!quoteData.get(1).equals(""))
                quoteAuthorTextView.setText(quoteData.get(1));
            else
                quoteAuthorTextView.setText("Unknown");
        }
    }

    @Override
    public void onSuccess(String response) {
        quoteData = JsonUtils.parseJson(response);
        displayQuote();
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(this, "this is the error " + error, Toast.LENGTH_SHORT).show();
    }
}
