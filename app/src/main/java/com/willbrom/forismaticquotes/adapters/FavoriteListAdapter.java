package com.willbrom.forismaticquotes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.willbrom.forismaticquotes.R;
import com.willbrom.forismaticquotes.data.Quote;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.FavoriteItemViewHolder> {

    private List<Quote> quoteList;

    @Override
    public FavoriteItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.favorite_item_layout, parent, false);
        return new FavoriteItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteItemViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        String quoteText = quoteList.get(position).quoteText;
        String quoteAuthor = quoteList.get(position).quoteAuthor;

        holder.favQuoteTextView.setText(quoteText);
        holder.favQuoteAuthorTextView.setText(quoteAuthor);
    }

    @Override
    public int getItemCount() {
        if (quoteList == null) return 0;
        return quoteList.size();
    }

    public void setQuoteList(List<Quote> quoteList) {
        this.quoteList = quoteList;
        notifyDataSetChanged();
    }

    public class FavoriteItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fav_quote_textView)
        TextView favQuoteTextView;
        @BindView(R.id.fav_quote_author_textView)
        TextView favQuoteAuthorTextView;

        public FavoriteItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
