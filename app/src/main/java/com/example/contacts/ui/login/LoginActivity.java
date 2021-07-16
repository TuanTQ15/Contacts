package com.example.contacts.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.contacts.MyApplication;
import com.example.contacts.R;
import com.example.contacts.api.ApiService;
import com.example.contacts.dao.AppDatabase;
import com.example.contacts.dao.userLoginDAO;
import com.example.contacts.databinding.ActivityLoginBinding;
import com.example.contacts.model.LoginInfo;
import com.example.contacts.model.User;
import com.example.contacts.ui.management.ContactActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private AppDatabase db= MyApplication.getDb();
    private userLoginDAO userDao;
    private TextView txtViewRegister;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);
        userDao= db.userDao();
        //ánh xạ view
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        txtViewRegister =binding.register;
        //set username password mặc định
        usernameEditText.setText("truongquoctuan61@gmail.com");
        passwordEditText.setText("123456");
        this.getSupportActionBar().hide();
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        //udpate data from edit Text
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        //set su kien cho text view dang ky
        txtViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
        });
        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            callAPILogin(usernameEditText.getText().toString(), passwordEditText.getText().toString(),loadingProgressBar);
            overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        });
    }
    private void callAPILogin(String username, String password,ProgressBar loadingProgressBar){

        User loginRequest = new User(0,"",username,password);
        ApiService.apiService.login(loginRequest).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int code=response.code();
                loadingProgressBar.setVisibility(View.GONE);
                if(code ==200){
                    User userLogin =response.body();
                    LoginInfo loginInfo = new LoginInfo(userLogin.getId(),userLogin.getFullName(),userLogin.getEmail());
                    userDao.deleteAllFromTable();
                    userDao.insertAll(loginInfo);
                    launchViewContact();
                } else if (code==401) {
                    showNotifyLogin("Sai tài khoản mật khẩu");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                showNotifyLogin("Đăng nhập thất bại, kiểm tra lại kết nối");
            }
        });

    }
    private void launchViewContact() {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }
    public void showNotifyLogin(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        builder.setMessage(message);
        builder.setPositiveButton("OK",(dialogInterface,i)->{
            dialogInterface.dismiss();
        });
        AlertDialog alertDialog = builder.create(); //create
        alertDialog.show(); //Show it.
    }

}