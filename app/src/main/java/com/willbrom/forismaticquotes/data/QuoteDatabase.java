package com.willbrom.forismaticquotes.data;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Quote.class}, version = 1)
public abstract class QuoteDatabase extends RoomDatabase {

    private static final String DB_NAME = "quoteDatabase.db";
    private static volatile QuoteDatabase instance;

    public static synchronized QuoteDatabase getInstance(Context context) {
        if (instance == null)
            instance = create(context);
        return instance;
    }

    private static QuoteDatabase create(final Context context) {
        return Room.databaseBuilder(context,QuoteDatabase.class,DB_NAME).build();
    }

    public abstract QuoteDao getQuoteDao();
}
