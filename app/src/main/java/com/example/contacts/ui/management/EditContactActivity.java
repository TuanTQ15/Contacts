package com.example.contacts.ui.management;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.contacts.MyApplication;
import com.example.contacts.R;
import com.example.contacts.api.ApiService;
import com.example.contacts.dao.AppDatabase;
import com.example.contacts.dao.userLoginDAO;
import com.example.contacts.model.Contact;
import com.example.contacts.model.Email;
import com.example.contacts.model.LoginInfo;
import com.example.contacts.model.Phone;
import com.example.contacts.ui.login.LoginActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.simplemobiletools.commons.views.MyEditText;
import com.simplemobiletools.commons.views.MyTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditContactActivity extends AppCompatActivity {
    private Contact contact;
    private MyEditText contactEditName;
    private LinearLayout contactNumbersHolder,contactAddressHolder,contactEmailHolder;
    private RelativeLayout contactWrapper;
    private ImageView contactEditNameImage,contactNumbersAddNew,contactEmailsAddNew,contactChangePhoto,contactPhoto;
    private ScrollView scrollView;
    private static int REQUEST_CODE = 1999;
    private AppDatabase db= MyApplication.getDb();
    private userLoginDAO userDao;
    private LoginInfo loginInfo;
    private String imageBase64=null;
    private String action="";
    private  ProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        setControl();
        setEvent();
        setUpMenu();
        contactWrapper.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getSupportActionBar().hide();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setEvent() {
        contactNumbersAddNew.setOnClickListener(v -> {
            addNewPhoneNumberField();
        });
        contactEmailsAddNew.setOnClickListener(v -> {
            addNewEmailField();
        });
        contactChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri imageUri = Objects.requireNonNull(data).getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                contactPhoto.setImageBitmap(bitmap);
                imageBase64="data:image/png;base64,";
                imageBase64+=convertBase64(bitmap);

                 // actual conversion to Base64 String Image
                //base64Text.setText(encodedImage); // display the Base64 String Image encoded text
            }
            // overlayTest.setVisibility(View.INVISIBLE);
            //base64TextOverlay.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(this, "Bạn chưa chọn ảnh", Toast.LENGTH_LONG).show();
        }
    }
    private byte[] imageToByteArray(Bitmap bitmapImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        return baos.toByteArray();
    }
    private String convertBase64(Bitmap bitmap){
        byte[] imageBytes = imageToByteArray(bitmap);
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private void trySetPhoto(){
        if(contact.getPhoto() == null){
            contactPhoto.setImageDrawable(contactPhoto.getContext().getDrawable(R.drawable.ic_person_vector));
        }else {
            Glide.with(contactPhoto.getContext()).load(contact.getPhoto()).into(contactPhoto);
        }
    }
    private void setupNewContact() {
        scrollView.setVisibility(View.VISIBLE);
        contactEditName.setVisibility(View.VISIBLE);
        contactNumbersHolder.setVisibility(View.VISIBLE);
        contactEmailHolder.setVisibility(View.VISIBLE);
        contactAddressHolder.setVisibility(View.VISIBLE);
        setupNewPhoneNumbers();
        setupNewEmails();
    }
    private void setupNewEmails() {
        View viewEmail = contactEmailHolder.getChildAt(0);
        setupTypePicker(viewEmail.findViewById(R.id.type));
    }
    private void setupNewPhoneNumbers(){
        View viewPhone = contactNumbersHolder.getChildAt(0);
        setupTypePicker(viewPhone.findViewById(R.id.type));
    }
    private void setupEditContact() {
        scrollView.setVisibility(View.VISIBLE);
        setupNames();
        setupPhoneNumbers();
        setupEmails();
        setupAddresses();
        trySetPhoto();
    }
    private void setControl() {
        userDao=db.userDao();
        loginInfo= userDao.getLogin();
        if (getIntent().getExtras() != null) {
            contact = (Contact) getIntent().getSerializableExtra("contact");
        }
        contactWrapper=findViewById(R.id.contact_wrapper);
        scrollView=findViewById(R.id.contact_scrollview);
        contactNumbersHolder=findViewById(R.id.contact_numbers_holder);
        contactEmailHolder=findViewById(R.id.contact_emails_holder);
        contactAddressHolder=findViewById(R.id.contact_addresses_holder);
        contactEditName=findViewById(R.id.contact_edit_name);
        contactEditNameImage=findViewById(R.id.contact_name_image);
        contactNumbersAddNew=findViewById(R.id.contact_numbers_add_new);
        contactEmailsAddNew=findViewById(R.id.contact_emails_add_new);
        contactChangePhoto=findViewById(R.id.contact_change_photo);
        contactPhoto=findViewById(R.id.contact_photo);
        if (getIntent().getExtras() != null) {
            action=String.valueOf(getIntent().getStringExtra("action"));
            if(action.equals("edit_contact")){
                setupEditContact();
            }else {
                setupNewContact();
            }
        }
    }
    private void setupNames() {
        if(!contact.getName().isEmpty()){
            contactEditName.setText(contact.getName());
            contactEditName.setVisibility(View.VISIBLE);
            contactEditNameImage.setVisibility(View.VISIBLE);
        }
    }
    private void setupPhoneNumbers() {
        List<Phone> phones=contact.getPhones();

        for(int i=0;i < phones.size();i++){
            View view;
            if(i==0){
                view = contactNumbersHolder.getChildAt(i);
            }else {
                view = LayoutInflater.from(this).inflate(R.layout.item_edit_phone_number, contactNumbersHolder, false);
                contactNumbersHolder.addView(view);
            }
            Phone phone = phones.get(i);
            setUpViewContactOrEmail(view, phone.getPhoneNumber(), phone.getType());
        }
        contactNumbersHolder.setVisibility(View.VISIBLE);
    }
    private void setupEmails() {
        List<Email> emails=contact.getEmails();

        for(int i=0;i < emails.size();i++){
            View view;
            if(i==0){
                view = contactEmailHolder.getChildAt(i);
            }else {
                view = LayoutInflater.from(this).inflate(R.layout.item_edit_email, contactEmailHolder, false);
                contactEmailHolder.addView(view);
            }
            Email email = emails.get(i);
            setUpViewContactOrEmail(view, email.getEmailAddress(), email.getType());
        }
        contactEmailHolder.setVisibility(View.VISIBLE);
    }
    private void setUpViewContactOrEmail(View view, String text, String type){
        MyEditText detail = view.findViewById(R.id.detail);
        MyTextView typeView=view.findViewById(R.id.type);
        detail.setText(text);
        typeView.setText(type);
        setupTypePicker(typeView);
    }
    private void setupAddresses() {
        View view = contactAddressHolder.getChildAt(0);
        MyEditText contactAddress =view.findViewById(R.id.contact_address);
        MyTextView contactAddressType =view.findViewById(R.id.contact_address_type);
        contactAddress.setText(contact.getAddress());
        contactAddressType.setText("");
    }
    private int statusBarHeight(Context context){
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    private void setUpMenu(){
        AppBarLayout contactAppbar =findViewById(R.id.contact_appbar);
        MaterialToolbar contactToolbar = findViewById(R.id.contact_toolbar);
        if(action.equals("new_contact")){
            contactAppbar.findViewById(R.id.delete).setVisibility(View.GONE);
            contactAppbar.findViewById(R.id.share).setVisibility(View.GONE);
        }
        ((RelativeLayout.LayoutParams)contactAppbar.getLayoutParams()).topMargin = statusBarHeight(this);
        contactToolbar.getMenu().findItem(R.id.save).setOnMenuItemClickListener(item -> {
            if(!isFinishing()){
                dialog = ProgressDialog.show(this, "","Loading. Please wait...", true);
            }
            saveContact(contact);
            return false;
        });
        contactToolbar.getMenu().findItem(R.id.share).setOnMenuItemClickListener(item -> {
            return false;
        });
        contactToolbar.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(item -> {
            confirmDelete();
            return false;
        });
        contactToolbar.getMenu().findItem(R.id.log_out).setOnMenuItemClickListener(item -> {
            logOut();
            return false;
        });
        contactToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void logOut() {
        finish();
        launchLogin();
    }
    private void launchLogin() {
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    private void saveContact(Contact contactPre) {
        Contact myContact=setUpContact(contactPre);
        if(myContact!=null){
            this.contact=myContact;
            if(action.equals("edit_contact")){
                ApiService.apiService.putContact(loginInfo.getId(),contactPre.getId(),myContact).enqueue(new Callback<Contact>() {
                    @Override
                    public void onResponse(Call<Contact> call, Response<Contact> response) {
                        int code=response.code();
                        if(code==200){
                            contact=response.body();
                            Log.e("cccc","thanh cong");
                            launchContactInfo();
                        }
                        else {
                            dialog.cancel();
                            displayAlertValidation("Nhập sai định dạng email hoặc số điện thoại!");
                        }
                    }

                    @Override
                    public void onFailure(Call<Contact> call, Throwable t) {
                        Toast.makeText(EditContactActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                        //displayAlert("Kết nối thất bại, thử lại sau");
                    }
                });
            }else if(action.equals("new_contact")){
                ApiService.apiService.postContact(loginInfo.getId(),myContact).enqueue(new Callback<Contact>() {
                    @Override
                    public void onResponse(Call<Contact> call, Response<Contact> response) {
                        if(response.code()==200&response.body()!=null){
                            Log.e("cccc","thanh cong");
                            contact=response.body();
                            launchContactInfo();
                        }else {
                            dialog.cancel();
                            displayAlertValidation("Nhập sai định dạng email hoặc số điện thoại!");
                        }
                    }
                    @Override
                    public void onFailure(Call<Contact> call, Throwable t) {
                        Log.e("cccc","that bai");
                    }
                });
            }
        }else {
            displayAlertValidation("Thông tin không hoặc hợp lệ hoặc chưa đủ!!");
        }
    }
    private void launchContactInfo(){
        dialog.cancel();
        Intent intent=new Intent(this,ContactInfoActivity.class);
        intent.putExtra("contact",contact);
        finish();
        startActivity(intent);
    }
    private Contact setUpContact(Contact contact){
        MyEditText address= contactAddressHolder.findViewById(R.id.contact_address);
        Contact contactUpdate = new Contact();
        if(!address.getText().toString().equals("")&&!contactEditName.getText().toString().equals("")&&
        checkHolder(contactNumbersHolder)&&checkHolder(contactEmailHolder))
        {
            String stringPhoto;
            if(action.equals("edit_contact")){
                contactUpdate.setId(contact.getId());
            }
            try {
                stringPhoto=  contact.getPhoto();
                contactUpdate.setPhoto(stringPhoto);
            }catch (Exception e){
                contactUpdate.setPhoto("");
            }
            if(imageBase64!=null){
                contactUpdate.setPhoto(imageBase64);
                Log.e("strbase64",imageBase64);
            }
            contactUpdate.setName(contactEditName.getText().toString());
            contactUpdate.setAddress(address.getText().toString());


            contactUpdate.setPhones(getFilledPhoneNumbers());
            contactUpdate.setEmails(getFilledEmails());

            if(action.equals("edit_contact")){
                try {
                    int nPhone=contact.getPhones().size(),nEmail=contact.getEmails().size();
                    if(contactUpdate.getPhones()!=null&contactUpdate.getEmails()!=null&&contactUpdate.getPhones().size()>0
                            &&contactUpdate.getEmails().size()>0&&checkPreStatus(contactUpdate,nPhone,nEmail)){
                        return contactUpdate;
                    }
                }catch (Exception e){
                }
            }else {
                return contactUpdate;
            }

        }
        contactUpdate=null;
        return contactUpdate;
    }
    private boolean checkPreStatus(Contact contactUpdate,int nPhone,int nEmail){
        if(nPhone!=contactUpdate.getPhones().size()||nEmail!=contactUpdate.getPhones().size()){
            for(Phone phone: contactUpdate.getPhones()){
                if(phone.getPhoneNumber().length()<10){
                    return false;
                }
            }
            for (Email email:contactUpdate.getEmails()){
                if(!email.getEmailAddress().contains("@")){
                    return false;
                }
            }
        }else {
            return true;
        }
        return true;
    }
    private boolean checkHolder(LinearLayout contactHolder){
        int n=contactHolder.getChildCount();
        if(n==0){
            return  false;
        }else {
            MyEditText detail= contactHolder.findViewById(R.id.detail);
            if(detail.getText().toString().equals("")){
                return false;
            }else {
                return true;
            }
        }
    }
    void confirmDelete() {
        deleteContact("Are you sure you want to proceed with the deletion ?");
    }
    public void displayAlertValidation(String message) {
        dialog.cancel();
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        builder.setMessage(message);
        builder.setPositiveButton("OK",(dialogInterface,i)->{


        });
        AlertDialog alertDialog = builder.create(); //create
        alertDialog.show(); //Show it.
    }
    public void deleteContact(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        builder.setMessage(message);
        builder.setNegativeButton("Yes",(dialogInterface,i)->{
            ApiService.apiService.deleteContact(this.contact.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code()==200){
                        launchViewContact();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    showNotify("Xóa thất bại, kiểm tra lại kết nối");
                }
            });

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
    private List<Phone>  getFilledPhoneNumbers(){
        List<Phone> phoneNumbers = new ArrayList<>();
        int numbersCount = contactNumbersHolder.getChildCount();
        for (int i =0;i<numbersCount;i++) {
            View numberHolder = contactNumbersHolder.getChildAt(i);
            MyEditText editTextNumber =numberHolder.findViewById(R.id.detail);
            MyTextView numberType = numberHolder.findViewById(R.id.type);
            String phone =editTextNumber.getText().toString().trim();
            if(!phone.equals("")){
                phoneNumbers.add(new Phone(phone,numberType.getText().toString().trim()));
            }else {
                continue;
            }

        }
        return phoneNumbers;
    }
    private List<Email>  getFilledEmails(){
        List<Email> emails = new ArrayList<>();
        int numbersCount = contactEmailHolder.getChildCount();
        for (int i =0;i<numbersCount;i++) {
            View emailHolder = contactEmailHolder.getChildAt(i);
            MyEditText editTextEmail =emailHolder.findViewById(R.id.detail);
            MyTextView numberType = emailHolder.findViewById(R.id.type);
            String strMail =editTextEmail.getText().toString().trim();
            if(!strMail.equals("")){
                emails.add(new Email(strMail,numberType.getText().toString().trim()));
            }else {
                continue;
            }
        }
        return emails;
    }
    private void setupTypePicker(MyTextView typeField) {
        typeField.setOnClickListener(v -> {
          showRadioButtonDialog(typeField);
        });
    }
    private void showRadioButtonDialog(MyTextView typeField) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_radio_button);

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        switch (typeField.getText().toString()){
            case "":
                break;
            case "personal":
                ((RadioButton)dialog.findViewById(R.id.personal)).setChecked(true);
               break;
            case "company":
                ((RadioButton)dialog.findViewById(R.id.company)).setChecked(true);
                break;
            case "education":
                ((RadioButton)dialog.findViewById(R.id.education)).setChecked(true);
                break;
            case "other":
                ((RadioButton)dialog.findViewById(R.id.other)).setChecked(true);
                break;
            default:
                break;
        }
            rg.setOnCheckedChangeListener((group, checkedId) -> {
            int childCount = group.getChildCount();
            for (int x = 0; x < childCount; x++) {
                RadioButton btn = (RadioButton) group.getChildAt(x);
                if (btn.getId() == checkedId) {
                    typeField.setText(btn.getText().toString());
                    dialog.hide();
                    return ;
                }
            }
        });
        dialog.show();
    }
    private void addNewPhoneNumberField() {
        View numberHolder = LayoutInflater.from(this).inflate(R.layout.item_edit_phone_number, contactNumbersHolder, false);
        setupTypePicker(numberHolder.findViewById(R.id.type));
        contactNumbersHolder.addView(numberHolder);
        contactNumbersHolder.requestFocus(R.id.detail);
        showKeyBoard(numberHolder.findViewById(R.id.detail));
    }
    private void showKeyBoard(MyEditText myEditText){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(myEditText, InputMethodManager.SHOW_IMPLICIT);
    }
    private void addNewEmailField() {
        View emailHolder = LayoutInflater.from(this).inflate(R.layout.item_edit_email, contactEmailHolder, false);
        setupTypePicker(emailHolder.findViewById(R.id.type));
        contactEmailHolder.addView(emailHolder);
        contactEmailHolder.requestFocus(R.id.detail);
        showKeyBoard(emailHolder.findViewById(R.id.detail));
    }
    public void showNotify(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        AlertDialog alertDialog = builder.create(); //create
        alertDialog.show(); //Show it.
    }
}
