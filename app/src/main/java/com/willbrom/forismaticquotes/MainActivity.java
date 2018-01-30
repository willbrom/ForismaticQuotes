package com.willbrom.forismaticquotes;

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

    private int QuoteKeyNumber = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> quoteData = new ArrayList<>();
    @BindView(R.id.quoteText_textView) TextView quoteTextView;
    @BindView(R.id.quoteAuthor_textView) TextView quoteAuthorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public void onClickNextQuote(View view) {
        URL url = NetworkUtils.getQuoteUrl(String.valueOf(QuoteKeyNumber));
        NetworkUtils.getHttpResponse(this, this, url);
        Log.d(TAG, url.toString());
        QuoteKeyNumber++;
    }

    private void displayQuote() {
        if (quoteData != null) {
            quoteTextView.setText(quoteData.get(0));
            quoteAuthorTextView.setText(quoteData.get(1));
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
