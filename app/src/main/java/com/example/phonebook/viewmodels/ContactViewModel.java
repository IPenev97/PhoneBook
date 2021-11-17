package com.example.phonebook.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.phonebook.models.Contact;
import com.example.phonebook.repositories.ContactRepository;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private ContactRepository contactRepository;
    private LiveData<List<Contact>> contacts;
    private LiveData<List<Contact>> favoriteContacts;
    public ContactViewModel(@NonNull Application application) {
        super(application);
        contactRepository = new ContactRepository(application);
        contacts = contactRepository.getAllContacts();
        favoriteContacts = contactRepository.getAllFavoriteContacts();
    }

    public void insert(Contact contact){
        contactRepository.insert(contact);
    }
    public void update(Contact contact){
        contactRepository.update(contact);
    }
    public void delete(Contact contact){
        contactRepository.delete(contact);
    }

    public LiveData<List<Contact>> getAllFavoriteContacts(){
        return favoriteContacts;
    }
    public LiveData<List<Contact>>getAllContacts(){
        return contacts;
    }
}
