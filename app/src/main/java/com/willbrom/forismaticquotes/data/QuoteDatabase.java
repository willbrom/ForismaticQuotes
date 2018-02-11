package com.willbrom.forismaticquotes.data;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Quote.class}, version = 1)
public abstract class QuoteDatabase extends RoomDatabase {

    private static final String DB_NAME = "quoteDatabase.db";

    public static QuoteDatabase getInstance(Context context) {
        return RoomDatabaseInstance.create(context, DB_NAME);
    }

    private static class RoomDatabaseInstance {
        private static QuoteDatabase instance;
        private static QuoteDatabase create(Context context, String dbName) {
            if (instance == null)
                instance = Room.databaseBuilder(context, QuoteDatabase.class, dbName).build();
            return instance;
        }
    }

    public abstract QuoteDao getQuoteDao();
}
