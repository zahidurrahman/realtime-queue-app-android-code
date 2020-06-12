package com.stoken.stoken.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stoken.stoken.R;

public class ThankYouActivity extends AppCompatActivity {

    // toolbar
    ActionBar actionBar;
    Toolbar toolbar;

    AppCompatButton btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        //initToolbar
        initToolbar();

        btnDone = findViewById(R.id.btnDone);

        // do appoint
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // redirect to report
                Intent intent = new Intent(ThankYouActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // toolbar
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.thank_you);
    }

    @Override
    public void onBackPressed() {
        //
    }
}
