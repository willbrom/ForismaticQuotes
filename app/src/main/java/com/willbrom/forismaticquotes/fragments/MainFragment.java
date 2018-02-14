package com.willbrom.forismaticquotes.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.willbrom.forismaticquotes.R;
import com.willbrom.forismaticquotes.data.Quote;

import java.util.ArrayList;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.title_textView)
    TextView titleTextView;
    @BindView(R.id.quoteText_textView)
    TextView quoteTextView;
    @BindView(R.id.quoteAuthor_textView)
    TextView quoteAuthorTextView;
    @BindView(R.id.heart)
    ImageView heart;
    @BindView(R.id.quote_cardView)
    CardView quoteCardView;

    private String mParam1;
    private String mParam2;
    private Context context;
    private boolean isFavorite;

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
        setCustomFonts();
        return rootView;
    }

    private void setCustomFonts() {
        quoteTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Merienda-Bold.ttf"));
        quoteAuthorTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Rancho-Regular.ttf"));
        titleTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/AmaticSC-Bold.ttf"));
    }

    @OnClick(R.id.heart)
    void onFavButtonPressed() {
        if (!isFavorite) {
            isFavorite = true;
            String quote = quoteTextView.getText().toString();
            String quoteAuthor = quoteAuthorTextView.getText().toString();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            isChecked = !isChecked;
                final int[] stateSet = {android.R.attr.state_checked * (true ? 1 : -1)};
                heart.setImageState(stateSet, true);
            } else {
                heart.setImageResource(R.drawable.ic_heart_red);
            }

            if (mListener != null) {
                mListener.onClickQuoteFav(new Quote(quote, quoteAuthor));
            }
        }
    }

    public void resetHeart() {
        isFavorite = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            isChecked = !isChecked;
            final int[] stateSet = {android.R.attr.state_checked * (false ? 1 : -1)};
            heart.setImageState(stateSet, true);
        } else {
            heart.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentInteractionListener) {
            mListener = (OnMainFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFavoriteFragmentInteractionListener");
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

    public void displayQuote(ArrayList<String> quoteData) {
        if (quoteData != null) {
            quoteTextView.setText(quoteData.get(0));
            if (!quoteData.get(1).equals(""))
                quoteAuthorTextView.setText(quoteData.get(1));
            else
                quoteAuthorTextView.setText("Unknown");
        }
    }

    public interface OnMainFragmentInteractionListener {
        void onClickQuoteFav(Quote... quote);
    }
}
