package com.example.phonebook.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phonebook.R;
import com.example.phonebook.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.SearchContactViewHolder> implements Filterable {
    private List<Contact> searchContacts;
    private List<Contact> contactsFull;
    private OnClickSearchContactListener onClickSearchContactListener;
    private Context context;

    class SearchContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewMainText;
        private TextView textViewSecondaryText;
        private ImageView imageView;
        private SearchContactAdapter.OnClickSearchContactListener onClickSearchContactListener;

        SearchContactViewHolder(View itemView, OnClickSearchContactListener onClickSearchContactListener) {
            super(itemView);
            this.textViewMainText = itemView.findViewById(R.id.main_text);
            this.textViewSecondaryText = itemView.findViewById(R.id.secondary_text);
            this.imageView = itemView.findViewById(R.id.recycler_view_profile_image);
            this.onClickSearchContactListener = onClickSearchContactListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickSearchContactListener.onContactClick(searchContacts.get(getAdapterPosition()));
        }
    }

    public SearchContactAdapter(List<Contact>contacts, OnClickSearchContactListener onClickSearchContactListener, Context context) {
        this.searchContacts = contacts;
        this.contactsFull = new ArrayList<>(contacts);
        this.onClickSearchContactListener = onClickSearchContactListener;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,
                parent, false);
        return new SearchContactViewHolder(v, onClickSearchContactListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchContactViewHolder holder, int position) {
        Contact currentContact = searchContacts.get(position);
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
                .load(searchContacts.get(position).getProfileImagePath())
                .placeholder(R.drawable.profileimage)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return searchContacts.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Contact> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(contactsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Contact item : contactsFull) {
                    if (item.getNickname().toLowerCase().contains(filterPattern)
                            || item.getFirstName().contains(filterPattern)
                            || item.getPhoneNumber().contains(filterPattern)
                            || item.getLastName().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            searchContacts.clear();
            searchContacts.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };
    public interface OnClickSearchContactListener {
        void onContactClick(Contact contact);
    }


}
