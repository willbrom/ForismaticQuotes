package com.willbrom.forismaticquotes.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.willbrom.forismaticquotes.R;
import com.willbrom.forismaticquotes.adapters.FavoriteListAdapter;
import com.willbrom.forismaticquotes.data.Quote;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = FavoriteFragment.class.getSimpleName();

    private String mParam1;
    private String mParam2;

    private OnFavoriteFragmentInteractionListener mListener;

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.favRecyclerView)
    RecyclerView favRecyclerView;

    private FavoriteListAdapter adapter;
    private List<Quote> quoteList = new ArrayList<>();

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);;
        ButterKnife.bind(this, rootView);
        adapter = new FavoriteListAdapter();
        favRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        favRecyclerView.setAdapter(adapter);
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onGetFavoriteQuotes();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFavoriteFragmentInteractionListener) {
            mListener = (OnFavoriteFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFavoriteFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void startListener() {
        textView.setText("");
        adapter.setQuoteList(null);
        mListener.onGetFavoriteQuotes();
    }

    public void showQuote(List<Quote> quoteList) {
        adapter.setQuoteList(quoteList);
        if (quoteList != null) {
            for (int i = 0; i < quoteList.size(); i++) {
                textView.append(quoteList.get(i).quoteText.toString());
            }
        }
    }

    public interface OnFavoriteFragmentInteractionListener {
        void onGetFavoriteQuotes();
    }
}