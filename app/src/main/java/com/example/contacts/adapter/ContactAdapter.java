package com.example.contacts.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contacts.R;
import com.example.contacts.api.ApiService;
import com.example.contacts.model.Contact;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactAdapter extends
        RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> mContacts;
    private ItemClick mItemClick;

    // Pass in the contact array into the constructor
    public ContactAdapter(ItemClick itemClick) {
        mItemClick = itemClick;
        mContacts = new ArrayList<>();
    }

    public void setContacts(List<Contact> contacts) {
        this.mContacts = contacts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView numberTextView;
        public ImageView contactImage;
        public RelativeLayout contactContainer;
        private ItemClick mItemClick;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, ItemClick itemClick) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            mItemClick = itemClick;
            nameTextView = itemView.findViewById(R.id.item_contact_name);
            contactImage = itemView.findViewById(R.id.item_contact_image);
            numberTextView = itemView.findViewById(R.id.item_contact_number);
            contactContainer = itemView.findViewById(R.id.item_contact_holder);
        }

        public void bind(Contact contact) {
            // Set item views based on your views and data model
            nameTextView.setText(contact.getName());
            try {
                numberTextView.setText(contact.getPhones().get(0).getPhoneNumber());
                Glide.with(itemView.getContext()).load(contact.getPhoto()).into(contactImage);
            } catch (Exception e) {
                contactImage.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_person_vector));
                numberTextView.setText("");
            }
            nameTextView.setVisibility(View.VISIBLE);
            contactImage.setVisibility(View.VISIBLE);
            contactContainer.setVisibility(View.VISIBLE);
            contactContainer.setOnClickListener((v) -> {
                mItemClick.click(contact);
            });
            contactContainer.setOnLongClickListener(v -> {
                showPopupMenu(v, contact);
                return false;
            });
        }
    }

    private void showPopupMenu(View view, Contact contact) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.edit_contact) {
                    return true;
                } else if (i == R.id.delete_contact) {
                    //do something

                    deleteContact("Are you sure you want to proceed with the deletion ?", contact, view);
                    return true;
                } else {
                    return onMenuItemClick(item);
                }
            }
        });
        popup.show();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_contact_with_number, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView, mItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public interface ItemClick {
        void click(Contact contact);

        void delete(View view, Contact contact);
    }

    public void deleteContact(String message, Contact contact, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogCustom);
        builder.setMessage(message);
        builder.setNegativeButton("Yes", (dialogInterface, i) -> {

           ApiService.apiService.deleteContact(contact.getId()).enqueue(new Callback<Void>() {
               @Override
               public void onResponse(Call<Void> call, Response<Void> response) {
                   if(response.code()==200){
                       mItemClick.delete(v, contact);
                   }
               }

               @Override
               public void onFailure(Call<Void> call, Throwable t) {
                   showNotify("xóa thất bại!!!",v);
               }
           });
        });
        builder.setPositiveButton("No", (dialogInterface, i) -> {

        });
        AlertDialog alertDialog = builder.create(); //create
        alertDialog.show(); //Show it.
    }
    public void showNotify(String message, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogCustom);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        AlertDialog alertDialog = builder.create(); //create
        alertDialog.show(); //Show it.
    }
}
