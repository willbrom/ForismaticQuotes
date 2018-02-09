package com.willbrom.forismaticquotes.data;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class Quote {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public final String quoteText;
    public final String quoteAuthor;

    public Quote(String quoteText, String quoteAuthor) {
        this.quoteText = quoteText;
        this.quoteAuthor = quoteAuthor;
    }
}
