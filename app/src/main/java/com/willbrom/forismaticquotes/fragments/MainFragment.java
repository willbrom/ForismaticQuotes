package com.willbrom.forismaticquotes.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.willbrom.forismaticquotes.R;
import com.willbrom.forismaticquotes.utilities.JsonUtils;
import com.willbrom.forismaticquotes.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainFragment extends Fragment implements View.OnClickListener, NetworkUtils.VollyCallbackListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.title_textView)
    TextView titleTextView;
    @BindView(R.id.quoteText_textView)
    TextView quoteTextView;
    @BindView(R.id.quoteAuthor_textView)
    TextView quoteAuthorTextView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
//    @BindView(R.id.favorite_fab)
//    FloatingActionButton favFab;
    @BindView(R.id.quote_cardView)
    CardView quoteCardView;
    @BindView(R.id.fab_progressCircle)
    FABProgressCircle fabProgressCircle;

    private String mParam1;
    private String mParam2;
    private Context context;

    private OnMainFragmentInteractionListener mListener;
    private static final String TAG = MainFragment.class.getSimpleName();

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        context = getActivity();
        fab.setOnClickListener(this);
        setCustomFonts();

        return rootView;
    }

    private void setCustomFonts() {
        quoteTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Merienda-Bold.ttf"));
        quoteAuthorTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Rancho-Regular.ttf"));
        titleTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/AmaticSC-Bold.ttf"));
    }

    public void onNextButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onNextButtonFragmentInteraction(null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentInteractionListener) {
            mListener = (OnMainFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener2");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                onClickNextQuote();
        }
    }

    private void onClickNextQuote() {
        fab.setEnabled(false);
        fabProgressCircle.show();
        URL url = NetworkUtils.getQuoteUrl("");
        mListener.onNextButtonFragmentInteraction(url);
    }

    private void displayQuote(ArrayList<String> quoteData) {
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
        fab.setEnabled(true);
        fabProgressCircle.hide();
        displayQuote(JsonUtils.parseJson(response));
    }

    @Override
    public void onFailure(String error) {
        fabProgressCircle.hide();
        fab.setEnabled(true);
        Toast.makeText(context, "this is the error " + error, Toast.LENGTH_SHORT).show();
    }

    public interface OnMainFragmentInteractionListener {
        void onNextButtonFragmentInteraction(URL url);
    }
}
