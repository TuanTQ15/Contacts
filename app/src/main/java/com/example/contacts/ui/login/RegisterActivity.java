package com.example.contacts.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contacts.R;
import com.example.contacts.api.ApiService;
import com.example.contacts.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {
    private  EditText email,password,name;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();
        getSupportActionBar().hide();
        email =findViewById(R.id.editTextEmail);
        password =findViewById(R.id.editTextPassword);
        name =findViewById(R.id.editTextName);
        progressBar =findViewById(R.id.loading);
    }
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }
    public void onLoginClick(View view){
        finish();
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
    public void onRegisterClick(View view){
        progressBar.setVisibility(View.VISIBLE);
        String strName,strEmail,strPassword;
        strName=name.getText().toString().trim();
        strEmail=email.getText().toString().trim();
        strPassword=password.getText().toString().trim();
        if(!strName.equals("")&!strEmail.equals("")&!strPassword.equals("")){
            User user = new User(0,strName,strEmail,strPassword);
            ApiService.apiService.register(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    int code=response.code();
                    if(code==200){
                        showNotify("Tạo tài khoản thành công",true);
                    }else if(code ==403){
                        showNotify("Email này đã tồn tại",false);
                    }else if(code==400){
                        showNotify("Sai định dạng email",false);
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {

                    showNotify("Tạo tài khoản thất bại, kiểm tra lại kết nối!",false);
                }
            });

        }else {
            showNotify("Vui lòng điền đầy đủ thông tin",false);
            
        }
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
    public void showNotify(String message, boolean isSuccess) {
        progressBar.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        builder.setMessage(message);

        builder.setPositiveButton("OK",(dialogInterface,i)->{
            if(isSuccess){
                Intent intent= new Intent(this,LoginActivity.class);
                finish();
                startActivity(intent);
            }else {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create(); //create
        alertDialog.show(); //Show it.
    }
}
