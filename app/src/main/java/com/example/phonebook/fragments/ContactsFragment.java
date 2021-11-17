package com.example.phonebook.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.phonebook.models.Contact;
import com.example.phonebook.viewmodels.ContactViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ContactsFragment extends Fragment implements ContactAdapter.OnClickContactListener {

    private ContactViewModel contactViewModel;
    private ContactAdapter contactAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_contacts, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFloatingActionButton(view);
        initRecyclerView(view);




    }




    private void initFloatingActionButton(View view){
        FloatingActionButton buttonAddContact = view.findViewById(R.id.button_add_contact);
        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditContactActivity.class);
                intent.putExtra("action", "add");
                startActivity(intent);
            }
        });

    }



    private void initRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.contacts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter(this, this.getActivity());
        recyclerView.setAdapter(contactAdapter);
        recyclerView.setHasFixedSize(true);


        contactViewModel = new ViewModelProvider(getActivity()).get(ContactViewModel.class);
        contactViewModel.getAllContacts().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                contactAdapter.setContacts(contacts);
            }
        });

    }

    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(getActivity(), AddEditContactActivity.class);
        intent.putExtra("action", "edit");
        intent.putExtra("contact", contact);
        startActivity(intent);


    }
}
