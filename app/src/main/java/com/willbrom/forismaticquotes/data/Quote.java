package com.willbrom.forismaticquotes.data;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class Quote {
    @PrimaryKey
    public int id;

    public String quoteText;
    public String quoteAuthor;
}
