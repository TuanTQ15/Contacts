package com.example.contacts.ui.management;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.MyApplication;
import com.example.contacts.R;
import com.example.contacts.adapter.ContactAdapter;
import com.example.contacts.api.ApiService;
import com.example.contacts.dao.AppDatabase;
import com.example.contacts.dao.userLoginDAO;
import com.example.contacts.model.Contact;
import com.example.contacts.model.Phone;
import com.example.contacts.model.LoginInfo;
import com.example.contacts.ui.login.LoginActivity;
import com.example.contacts.ui.login.RegisterActivity;
import com.example.contacts.ui.statistic.StatisticActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private AppDatabase db= MyApplication.getDb();
    private userLoginDAO userDao = db.userDao();
    private LoginInfo loginInfo = userDao.getLogin();
    private ContactAdapter contactAdapter;
    private Intent intent;
    private ProgressDialog dialog;
    private MenuItem searchMenuItem;
    List<Contact> contacts;
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState );
        setContentView(R.layout.activity_main);
        fab =findViewById(R.id.fab);
        recyclerView = findViewById(R.id.list_contact);
        setApdater();
        setAddContact();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getContacts(loginInfo.getId());
    }
    private void setApdater(){
        intent = new Intent(this, ContactInfoActivity.class);

        contactAdapter = new ContactAdapter(new ContactAdapter.ItemClick() {
            @Override
            public void click(Contact contact) {
                intent.putExtra("contact", contact);
                startActivity(intent);
            }

            @Override
            public void delete(View view, Contact contact) {
                contacts.remove(contact);
                setContactAdapter(contacts);
                showNotify("Xóa thành công",view );
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(contactAdapter);
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
    private void setAddContact(){
        fab =findViewById(R.id.fab);
        fab.setOnClickListener(v -> launchAddContact());
    }
    private void getContacts(int accountId){
        dialog= ProgressDialog.show(this, "",
                "Loading", true);
        ApiService.apiService.getAllContact(accountId).enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                int code =response.code();
                contacts = response.body();
                if(code==200&&contacts!=null){
                    dialog.cancel();
                    setContactAdapter(contacts);
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                displayAlert("Lỗi kết nối thử lại sau");
                dialog.cancel();
            }
        });
    }
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {

        });
        AlertDialog alertDialog = builder.create(); //create
        alertDialog.show(); //Show it.
    }
    private void setContactAdapter(List<Contact> contacts){
        if (contacts.size() > 0) {
            Collections.sort(contacts, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        }
        contactAdapter.setContacts(contacts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSearch(menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_statistic:
                Intent intent = new Intent(this, StatisticActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
                return true;

            case R.id.log_out:
                logOut();
                return true;
            case R.id.action_search:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }
    private void logOut() {
        userDao.deleteAllFromTable();
        finish();
        launchLogin();
    }
    private void launchLogin() {
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    private void launchAddContact() {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra("action","new_contact");
        startActivity(intent);
    }
    SearchView searchView = null;
    private void setupSearch(Menu menu) {
        SearchManager searchManager = (SearchManager) ContactActivity.this.getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);

        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(ContactActivity.this.getComponentName()));
            searchView.setQueryHint("Type here to search");
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //result khen click search btn
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContact(newText);
                return true;
            }
        });

    }
    private void launchEditContact(Contact contact) {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra("action","edit_contact");
        intent.putExtra("contact", contact);
        startActivity(intent);
    }
    private void filterContact(String strSearch){
        List<Contact> filter = this.contacts.stream().filter(contact -> {
            if(contact == null || contact.getName().isEmpty())
            {return true;}else {

                return (contact.getName().toLowerCase().contains(strSearch.toLowerCase())||
                        filterPhone(contact.getPhones(),strSearch));
            }
        }).collect(Collectors.toList());
        setContactAdapter(filter);
    }
    private boolean filterPhone(List<Phone>phoneList, String strSearch){
        for(Phone phone:phoneList){
           if( phone.getPhoneNumber().contains(strSearch)){
               return true;
           }
        }
        return false;
    }
}
