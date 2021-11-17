package com.example.phonebook.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.phonebook.models.Contact;
import com.example.phonebook.models.daos.ContactDao;

import java.util.List;

public class ContactRepository {
    private ContactDao contactDao;
    private LiveData<List<Contact>> contacts;
    private LiveData<List<Contact>> favoriteContacts;

    public ContactRepository(Application application) {
        PhoneBookDatabase database = PhoneBookDatabase.getInstance(application);
        contactDao = database.contactDao();
        contacts = contactDao.getAllContacts();
        favoriteContacts = contactDao.getAllFavoriteContacts();
    }

    public void insert(Contact contact){
        new InsertContactAsyncTask(contactDao).execute(contact);

    }
    public void update(Contact contact){
        new UpdateContactAsyncTask(contactDao).execute(contact);
    }
    public void delete(Contact contact){
        new DeleteContactAsyncTask(contactDao).execute(contact);
    }
    public LiveData<List<Contact>> getAllContacts(){
        return contacts;
    }

    public LiveData<List<Contact>> getAllFavoriteContacts() {
        return favoriteContacts;
    }


    private static class InsertContactAsyncTask extends AsyncTask<Contact, Void, Void>{
        private ContactDao contactDao;

        private InsertContactAsyncTask(ContactDao contactDao){
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDao.insert(contacts[0]);
            return null;
        }
    }
    private static class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void>{
        private ContactDao contactDao;

        private UpdateContactAsyncTask(ContactDao contactDao){
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDao.update(contacts[0]);
            return null;
        }
    }
    private static class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void>{
        private ContactDao contactDao;

        private DeleteContactAsyncTask(ContactDao contactDao){
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDao.delete(contacts[0]);
            return null;
        }
    }

}
