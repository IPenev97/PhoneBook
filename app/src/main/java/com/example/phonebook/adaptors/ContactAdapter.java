package com.example.phonebook.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phonebook.R;
import com.example.phonebook.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder>{

    private List<Contact>contacts = new ArrayList<>();
    private OnClickContactListener onClickContactListener;
    private Context context;

    public ContactAdapter(OnClickContactListener onClickContactListener, Context context) {
        this.onClickContactListener = onClickContactListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ContactHolder(itemView, onClickContactListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        Contact currentContact = contacts.get(position);
        String mainText;
        String secondaryText;
        if(!currentContact.getNickname().isEmpty()){
            mainText = currentContact.getNickname();
            secondaryText = currentContact.getPhoneNumber();
        }
        else if(currentContact.getFirstName().isEmpty() && currentContact.getLastName().isEmpty() && currentContact.getNickname().isEmpty()){
            mainText = currentContact.getPhoneNumber();
            secondaryText = "";
        }
        else{
            mainText = currentContact.getFirstName() + " " + currentContact.getLastName();
            secondaryText = currentContact.getPhoneNumber();
        }
        holder.textViewMainText.setText(mainText);
        holder.textViewSecondaryText.setText(secondaryText);


        Glide.with(context)
                .load(contacts.get(position).getProfileImagePath())
                .placeholder(R.drawable.profileimage)
                .into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<Contact> contacts){
        this.contacts = contacts;
        notifyDataSetChanged();
    }







    protected class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewMainText;
        private TextView textViewSecondaryText;
        private ImageView imageView;
        private OnClickContactListener onClickContactListener;



        public ContactHolder(@NonNull View itemView, OnClickContactListener onClickContactListener) {
            super(itemView);
            this.textViewMainText = itemView.findViewById(R.id.main_text);
            this.textViewSecondaryText = itemView.findViewById(R.id.secondary_text);
            this.imageView = itemView.findViewById(R.id.recycler_view_profile_image);
            this.onClickContactListener = onClickContactListener;

            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {
          onClickContactListener.onContactClick(contacts.get(getAdapterPosition()));
        }
    }

    public interface OnClickContactListener {
        void onContactClick(Contact contact);
    }

}
