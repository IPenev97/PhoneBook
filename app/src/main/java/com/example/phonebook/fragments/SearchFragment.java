package com.example.phonebook.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonebook.AddEditContactActivity;
import com.example.phonebook.R;
import com.example.phonebook.adaptors.ContactAdapter;
import com.example.phonebook.adaptors.SearchContactAdapter;
import com.example.phonebook.models.Contact;
import com.example.phonebook.viewmodels.ContactViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchContactAdapter.OnClickSearchContactListener {
    private ContactViewModel contactViewModel;
    private SearchContactAdapter searchContactAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.search_contacts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactViewModel = new ViewModelProvider(getActivity()).get(ContactViewModel.class);
        List<Contact>contacts = contactViewModel.getAllContacts().getValue();

        searchContactAdapter = new SearchContactAdapter(contacts,this, this.getActivity());

        recyclerView.setAdapter(searchContactAdapter);
        recyclerView.setHasFixedSize(true);











    }







    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(getActivity(), AddEditContactActivity.class);
        intent.putExtra("action", "edit");
        intent.putExtra("contact", contact);
        startActivity(intent);


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_contact_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_contact);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchContactAdapter.getFilter().filter(newText);
                return false;

            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }
}
