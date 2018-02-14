package com.willbrom.forismaticquotes.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.willbrom.forismaticquotes.R;
import com.willbrom.forismaticquotes.data.Quote;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.FavoriteItemViewHolder> {

    private List<Quote> quoteList;
    private OnFavoriteItemListener mListener;

    public interface OnFavoriteItemListener {
        void onClickUnFavorite(Quote quote);
        void onClickShare(Quote quote);
    }

    public FavoriteListAdapter(OnFavoriteItemListener listener) {
        mListener = listener;
    }

    @Override
    public FavoriteItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.favorite_item_layout, parent, false);
        return new FavoriteItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavoriteItemViewHolder holder, final int position) {
        final Quote quote = quoteList.get(position);
        String quoteText = quote.quoteText;
        String quoteAuthor = quote.quoteAuthor;

        holder.favQuoteTextView.setText(quoteText);
        holder.favQuoteAuthorTextView.setText(quoteAuthor);

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onClickUnFavorite(quote);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onClickShare(quote);
            }
        });
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
        @BindView(R.id.heart)
        ImageView heart;
        @BindView(R.id.share_imageView)
        ImageView share;

        private Context context;

        public FavoriteItemViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
            setCustomFonts();
        }

        private void setCustomFonts() {
            favQuoteTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Merienda-Bold.ttf"));
            favQuoteAuthorTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Rancho-Regular.ttf"));
//            titleTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/AmaticSC-Bold.ttf"));
        }
    }
}
