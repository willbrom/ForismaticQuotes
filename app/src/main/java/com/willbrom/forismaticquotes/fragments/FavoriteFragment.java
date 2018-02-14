package com.willbrom.forismaticquotes.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.willbrom.forismaticquotes.R;
import com.willbrom.forismaticquotes.adapters.FavoriteListAdapter;
import com.willbrom.forismaticquotes.data.Quote;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteFragment extends Fragment implements FavoriteListAdapter.OnFavoriteItemListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = FavoriteFragment.class.getSimpleName();

    private String mParam1;
    private String mParam2;

    private OnFavoriteFragmentInteractionListener mListener;

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
        adapter = new FavoriteListAdapter(this);
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
        if (adapter != null)
            adapter.setQuoteList(null);
        if (mListener != null)
            mListener.onGetFavoriteQuotes();
    }

    public void showQuote(List<Quote> quoteList) {
        if (adapter != null)
            adapter.setQuoteList(quoteList);
    }

    @Override
    public void onClickUnFavorite(final Quote quote) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Remove favorite?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null)
                            mListener.onDeleteFavoriteQuote(quote);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();
    }

    @Override
    public void onClickShare(Quote quote) {
        if (mListener != null)
            mListener.onShareFavoriteQuote(quote);
    }

    public interface OnFavoriteFragmentInteractionListener {
        void onGetFavoriteQuotes();
        void onDeleteFavoriteQuote(Quote quote);
        void onShareFavoriteQuote(Quote quote);
    }
}
