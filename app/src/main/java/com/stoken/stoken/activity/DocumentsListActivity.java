package com.stoken.stoken.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stoken.stoken.R;
import com.stoken.stoken.adapter.Adapter;
import com.stoken.stoken.api.ApiRequestData;
import com.stoken.stoken.api.Retroserver;
import com.stoken.stoken.model.DataModel;
import com.stoken.stoken.model.ResponsModel;
import com.stoken.stoken.utils.DetectInternet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.stoken.stoken.utils.MyClasses.noInternetDialog;

public class DocumentsListActivity extends AppCompatActivity {

    // internet check
    Boolean isInternetPresent = false;
    DetectInternet detectInternet;

    private RecyclerView mRecycler;
    private Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    private List<DataModel> mItems;

    LinearLayout noitem, loading;
    TextView showNotice;

    ActionBar actionBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        initToolbar();

        // check for internet connection
        detectInternet = new DetectInternet(getApplicationContext());
        isInternetPresent = detectInternet.isConnectingToInternet();
        if (!isInternetPresent) {
            noInternetDialog(this);
        }

        // Recycler
        mRecycler = findViewById(R.id.recyclerTemp);
        mRecycler.setHasFixedSize(true);
        mManager = new GridLayoutManager(this, 1);
        mRecycler.setLayoutManager(mManager);
        mRecycler.setItemAnimator(new DefaultItemAnimator());

        // connect to xml
        noitem = findViewById(R.id.lyt_noitem);
        loading = findViewById(R.id.lyt_loading);
        showNotice = findViewById(R.id.showNotice);

        // document list
        documentList();
    }

    // load document list
    private void documentList() {
        ApiRequestData api = Retroserver.getClient().create(ApiRequestData.class);
        Call<ResponsModel> getdata = api.documentList();
        getdata.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {

                loading.setVisibility(View.GONE);

                Boolean success = response.body().getSuccess();
                String message = response.body().getMessage();

                if (success) {
                    // view data
                    mItems = response.body().getResults();
                    mAdapter = new Adapter(DocumentsListActivity.this, mItems);
                    mRecycler.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    showNotice.setText(message);
                    noitem.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                showNotice.setText("Something went wrong !");
                noitem.setVisibility(View.VISIBLE);
            }
        });
    }

    // toolbar
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Document List");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
