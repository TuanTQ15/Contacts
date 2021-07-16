package com.example.contacts.ui.statistic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contacts.MyApplication;
import com.example.contacts.R;
import com.example.contacts.api.ApiService;
import com.example.contacts.dao.AppDatabase;
import com.example.contacts.dao.userLoginDAO;
import com.example.contacts.model.Contact;
import com.example.contacts.model.LoginInfo;
import com.example.contacts.model.Phone;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticActivity extends AppCompatActivity {
    TextView tvR, tvPython, tvCPP, tvJava;
    PieChart pieChart;
    private ProgressDialog dialog;
    List<Contact> contacts;
    private AppDatabase db= MyApplication.getDb();
    private userLoginDAO userDao = db.userDao();
    private LoginInfo loginInfo = userDao.getLogin();
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        getSupportActionBar().hide();
        tvR = findViewById(R.id.tvR);
        tvPython = findViewById(R.id.tvPython);
        tvCPP = findViewById(R.id.tvCPP);
        tvJava = findViewById(R.id.tvJava);
        pieChart = findViewById(R.id.piechart);
        getContacts(loginInfo.getId());

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setData(){
        int personal=0,company=0,education=0,other=0;
        for(Contact contact:contacts){
            try {
                for (Phone phone :contact.getPhones()){
                    switch (phone.getType()){
                        case "personal":
                            personal++;
                            break;
                        case "company":
                            company++;
                            break;
                        case "education":
                            education++;
                            break;
                        case "other":
                            other++;
                            break;
                    }
                }
            }catch (Exception e){

            }
        }
        tvR.setText(String.valueOf(personal));
        tvPython.setText(String.valueOf(other));
        tvCPP.setText(String.valueOf(company));
        tvJava.setText(String.valueOf(education));
        pieChart.addPieSlice(
                new PieModel(
                        "R",
                        Integer.parseInt(tvR.getText().toString()),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Python",
                        Integer.parseInt(tvPython.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "C++",
                        Integer.parseInt(tvCPP.getText().toString()),
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        "Java",
                        Integer.parseInt(tvJava.getText().toString()),
                        Color.parseColor("#29B6F6")));

        pieChart.startAnimation();
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
                    setData();
                    dialog.cancel();
                }
            }
            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                displayAlert("Lỗi kết nối, vui lòng thử lại sau");
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
}
