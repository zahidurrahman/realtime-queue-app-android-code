package com.stoken.stoken.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.stoken.stoken.R;
import com.stoken.stoken.api.ApiRequestData;
import com.stoken.stoken.api.Retroserver;
import com.stoken.stoken.model.ResponsModel;
import com.stoken.stoken.utils.Tools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    TextInputEditText email, password;
    Button btn_signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Tools.setSystemBarColor(this, R.color.red_300);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_signin = findViewById(R.id.btn_signin);

        // perform signin
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if no view has focus:
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                String getEmail = email.getText().toString();
                String getPassword = password.getText().toString();

                // validate
                if (getEmail.isEmpty() || getPassword.isEmpty())
                    Toast.makeText(SignInActivity.this, "Please fill-up the form", Toast.LENGTH_SHORT).show();
                else
                    signIn(getEmail, getPassword);
            }
        });

    }

    // perform sign in
    private void signIn(String email, String password) {
        ApiRequestData api = Retroserver.getClient().create(ApiRequestData.class);
        Call<ResponsModel> getdata = api.login(email, password);
        getdata.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {

                Boolean success = response.body().getSuccess();
                String message = response.body().getMessage();

                if (success) {
                    Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();

                    String user_id = response.body().getUserID();
                    String student_id = response.body().getStudentID();
                    String name = response.body().getName();

                    SharedPreferences.Editor editor = getSharedPreferences("SF_PREF", MODE_PRIVATE).edit();
                    editor.putString("userID", user_id);
                    editor.putString("studentID", student_id);
                    editor.putString("name", name);
                    editor.apply();

                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(i);

                } else {
                    Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                Toast.makeText(SignInActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
