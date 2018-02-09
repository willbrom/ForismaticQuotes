package com.willbrom.forismaticquotes.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface QuoteDao {

    @Query("SELECT * FROM Quote")
    List<Quote> getAllQuotes();

    @Insert
    void insert(Quote... quotes);

    @Delete
    void delete(Quote quote);
}
