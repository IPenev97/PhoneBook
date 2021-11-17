package com.example.phonebook.repositories;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.phonebook.models.Contact;
import com.example.phonebook.models.daos.ContactDao;

@Database(entities = {Contact.class}, version = 3)
public abstract class PhoneBookDatabase extends RoomDatabase {

    private static PhoneBookDatabase instance;

    public abstract ContactDao contactDao();

    public static synchronized PhoneBookDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PhoneBookDatabase.class, "phone_book_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };


}
