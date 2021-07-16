package com.example.contacts.ui.management;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.contacts.R;
import com.example.contacts.api.ApiService;
import com.example.contacts.model.Contact;
import com.example.contacts.model.Email;
import com.example.contacts.model.Phone;
import com.example.contacts.ui.login.LoginActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.simplemobiletools.commons.views.MyTextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactInfoActivity extends AppCompatActivity {
    private Contact contact;
    private MyTextView contactName;
    LinearLayout  contactNumbersHolder;
    LinearLayout  contactEmailsHolder;
    LinearLayout  contactAddressesHolder;
    private ImageView contactNameImage;
    private ImageView contactNumberImage;
    private ImageView contactEmailImage;
    private ImageView contactAddressImage;
    private ImageView contactPhoto;
    private ImageView contactSendSms,contactStartCall,contactSendEmail;
    RelativeLayout contactWrapper;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);
        setControl();
        contactWrapper.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getSupportActionBar().hide();
    }
    @Override
    protected void onResume() {
        super.onResume();
        setControl();
    }
    private void setControl(){
        if (getIntent().getExtras() != null) {
            contact = (Contact) getIntent().getSerializableExtra("contact");
        }
        contactName=findViewById(R.id.contact_name);
        contactNameImage=findViewById(R.id.contact_name_image);
        contactWrapper=findViewById(R.id.contact_wrapper);
        contactNumberImage=findViewById(R.id.contact_numbers_image);
        contactEmailImage=findViewById(R.id.contact_emails_image);
        contactAddressImage=findViewById(R.id.contact_addresses_image);
        contactPhoto=findViewById(R.id.contact_photo);
        contactNumbersHolder=findViewById(R.id.contact_numbers_holder);
        contactEmailsHolder=findViewById(R.id.contact_emails_holder);
        contactAddressesHolder=findViewById(R.id.contact_addresses_holder);
        contactSendSms=findViewById(R.id.contact_send_sms);
        contactStartCall=findViewById(R.id.contact_start_call);
        contactSendEmail=findViewById(R.id.contact_send_email);
        setupViewContact();
    }
    private void setupViewContact(){
        if(contact!=null){
            trySetPhoto();
            setName();
            setNumber();
            setupEmails();
            setupAddresses();
            setUpMenu();
            contactSendSms.setOnClickListener(v -> trySendSMS());
            contactStartCall.setOnClickListener(v -> tryStartCall());
            contactSendEmail.setOnClickListener(v -> trySendEmail());
        }
    }
    private  void tryStartCall(){
        List <Phone> numbers=contact.getPhones();
        if(numbers.size()==1){
            String uri ="tel:"+ numbers.get(0).getPhoneNumber().trim();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }else if(numbers.size()>1) {
            showRadioButtonDialog(numbers,"tel");
        }

    }
    private void trySendSMS() {
        List<Phone> numbers = contact.getPhones();
        if (numbers.size() == 1) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.fromParts("smsto", numbers.get(0).getPhoneNumber().trim(), null));
            startActivity(intent);
        } else if (numbers.size() > 1) {
            showRadioButtonDialog(numbers,"smsto");
        }
    }

    private void trySendEmail() {
        List<Email> emails = contact.getEmails();
        if (emails.size() == 1) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.fromParts("mailto", emails.get(0).getEmailAddress().trim(),null));
            startActivity(intent);
        } else if (emails.size() > 1) {
            showRadioButtonDialog(emails);
        }
    }
    private void setNumber() {
        contactNumbersHolder.removeAllViews();
        List<Phone> phoneList;
        phoneList=contact.getPhones();
        MyTextView contactNumber,contactType;
        for (Phone phone : phoneList) {
            if(!phone.getPhoneNumber().equals("")){
                View view = LayoutInflater.from(this).inflate(R.layout.item_view_phone_number, contactNumbersHolder, false);
                contactNumbersHolder.addView(view);
                contactNumber=view.findViewById(R.id.detail);
                contactType=view.findViewById(R.id.type);
                contactNumber.setText(phone.getPhoneNumber());
                contactType.setText(phone.getType());
                String uri ="tel:"+ phone.getPhoneNumber().trim();
                copyOnLongClick(view,phone.getPhoneNumber());
                contactNumber.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                });
            }
        }
        contactNumberImage.setVisibility(View.VISIBLE);
        contactNumbersHolder.setVisibility(View.VISIBLE);
    }
    private void setName(){
        contactName.setText(contact.getName());
        if(!contact.getName().isEmpty()){
            copyOnLongClick(contactName,contact.getName());
            contactName.setVisibility(View.VISIBLE);
            contactNameImage.setVisibility(View.VISIBLE);
        }

    }
    private int statusBarHeight(Context context){
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
    private void setupEmails() {
        contactEmailsHolder.removeAllViews();
        List<Email> emails = contact.getEmails();
        if (!emails.isEmpty()) {
            for (Email email : emails)  {
                View view = LayoutInflater.from(this).inflate(R.layout.item_view_email, contactEmailsHolder, false);
                contactEmailsHolder.addView(view);
                MyTextView contactEmail =view.findViewById(R.id.detail);
                MyTextView contactEmailType =view.findViewById(R.id.type);
                contactEmail.setText(email.getEmailAddress());
                contactEmailType.setText(email.getType());
                copyOnLongClick(view,email.getEmailAddress());
                String uri =email.getEmailAddress().trim();
                contactEmail.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.fromParts("mailto",uri,null));
                    startActivity(intent);
                });
            }
            contactEmailImage.setVisibility(View.VISIBLE);
            contactEmailsHolder.setVisibility(View.VISIBLE);
        } else {
            contactEmailImage.setVisibility(View.GONE);
            contactEmailsHolder.setVisibility(View.GONE);
        }
    }
    private void setupAddresses() {
        contactAddressesHolder.removeAllViews();
        String address =contact.getAddress();
        if(!address.equals("")){
            View view = LayoutInflater.from(this).inflate(R.layout.item_view_address, contactAddressesHolder, false);
            contactAddressesHolder.addView(view);
            MyTextView contactAddress=findViewById(R.id.contact_address);
            MyTextView contactAddressType=findViewById(R.id.contact_address_type);
            contactAddress.setText(address);
            contactAddressType.setText("Home");
            copyOnLongClick(view,address);
            contactAddressesHolder.setVisibility(View.VISIBLE);
            contactAddressImage.setVisibility(View.VISIBLE);
        } else {
            contactAddressesHolder.setVisibility(View.GONE);
            contactAddressImage.setVisibility(View.GONE);
        }
    }
    private void setUpMenu(){
        AppBarLayout contactAppbar =findViewById(R.id.contact_appbar);
        MaterialToolbar contactToolbar = findViewById(R.id.contact_toolbar);
        ((RelativeLayout.LayoutParams)contactAppbar.getLayoutParams()).topMargin = statusBarHeight(this);
        contactToolbar.getMenu().findItem(R.id.share).setOnMenuItemClickListener(item -> {
            //shareContact(fullContact!!)
            return false;
        });

        contactToolbar.getMenu().findItem(R.id.edit).setOnMenuItemClickListener(item -> {
            launchEditContact(contact);
            finish();
            return false;
        });
        contactToolbar.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(item -> {
            deleteContact();
            return false;
        });
        contactToolbar.getMenu().findItem(R.id.log_out).setOnMenuItemClickListener(item -> {
            logOut();
            return false;
        });
        contactToolbar.setNavigationOnClickListener(v -> finish());
    }
    private void logOut() {
        launchLogin();
    }
    private void launchLogin() {
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    private void deleteContact() {
        displayAlert("Are you sure you want to proceed with the deletion ?");
    }
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        builder.setMessage(message);
        builder.setNegativeButton("Yes",(dialogInterface,i)->{
            ApiService.apiService.deleteContact(this.contact.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    int code=response.code();
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
            launchViewContact();
        });
        builder.setPositiveButton("No",(dialogInterface,i)->{

        });
        AlertDialog alertDialog = builder.create(); //create
        alertDialog.show(); //Show it.
    }
    private void launchViewContact() {
        finish();
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }
    private void launchEditContact(Contact contact) {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra("action","edit_contact");
        intent.putExtra("contact", contact);
        startActivity(intent);
    }
    private void copyOnLongClick(View view, String number){
        view.setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("phone", number);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ContactInfoActivity.this, "Copy to clipboard", Toast.LENGTH_SHORT).show();
            return false;
        });
    }
    private void showRadioButtonDialog(List<Email> emails) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        RadioGroup rg = dialog.findViewById(R.id.radio_group);
        for(int i=0;i<emails.size();i++){
            RadioButton rb=new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(emails.get(i).getEmailAddress());
            rg.addView(rb);
            rg.setOnCheckedChangeListener((group, checkedId) -> {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.fromParts("mailto",btn.getText().toString().trim() ,null));
                        startActivity(intent);
                        dialog.cancel();
                        return;
                    }
                }
            });
        }
        dialog.show();
    }
    private void showRadioButtonDialog(List<Phone> phoneList,String action) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        RadioGroup rg = dialog.findViewById(R.id.radio_group);
        for(int i=0;i<phoneList.size();i++){
            RadioButton rb=new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(phoneList.get(i).getPhoneNumber());
            rg.addView(rb);
            rg.setOnCheckedChangeListener((group, checkedId) -> {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        if(action.equals("smsto")){
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.fromParts("smsto", btn.getText().toString().trim(), null));
                            startActivity(intent);

                        }else if(action.equals("tel")){
                            String uri ="tel:"+ btn.getText().toString().trim();
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        }
                        dialog.cancel();
                        return;
                    }
                }
            });
        }
        dialog.show();
    }
    private void trySetPhoto(){
        try {
            Glide.with(contactPhoto.getContext()).load(contact.getPhoto()).into(contactPhoto);
        }catch (Exception e){
            contactPhoto.setImageDrawable(contactPhoto.getContext().getDrawable(R.drawable.ic_person_vector));
        }
    }
}
