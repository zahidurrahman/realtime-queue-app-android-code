package com.stoken.stoken.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.stoken.stoken.R;
import com.stoken.stoken.api.ApiRequestData;
import com.stoken.stoken.api.Retroserver;
import com.stoken.stoken.model.Queue;
import com.stoken.stoken.model.ResponsModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stoken.stoken.utils.DetectInternet;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.stoken.stoken.utils.MyClasses.noInternetDialog;

public class MainActivity extends AppCompatActivity {

    // toolbar
    ActionBar actionBar;
    Toolbar toolbar;

    // internet check
    Boolean isInternetPresent = false;
    DetectInternet detectInternet;

    private static final String TAG = "MainActivity";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference queueDatabaseReference, totalQueueDatabaseReference;

    LinearLayout lytDefault, lytQueueInfo, lytAppointLoader, lytCancelLoader;
    TextView tvLoggedStudentName, tvLoggedStudentID;
    TextView tvTokenNumber, tvName, tvStudentID, tvCounter, tvTotalQueue, tvMyPosition;
    CardView cvAppoint, cvCancel, cvDocuments, cvLogout;

    String activeFirebaseKey, myCounter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initToolbar
        initToolbar();

        // initComponent
        initComponent();
    }

    // components
    private void initComponent() {
        // connect to xml variables
        cvAppoint = findViewById(R.id.cvAppoint);
        tvLoggedStudentName = findViewById(R.id.tvLoggedStudentName);
        tvLoggedStudentID = findViewById(R.id.tvLoggedStudentID);
        lytDefault = findViewById(R.id.lytDefault);
        lytQueueInfo = findViewById(R.id.lytQueueInfo);
        tvTokenNumber = findViewById(R.id.tvTokenNumber);
        tvName = findViewById(R.id.tvName);
        tvStudentID = findViewById(R.id.tvStudentID);
        tvCounter = findViewById(R.id.tvCounter);
        tvTotalQueue = findViewById(R.id.tvTotalQueue);
        tvMyPosition = findViewById(R.id.tvMyPosition);
        tvCounter = findViewById(R.id.tvCounter);
        cvCancel = findViewById(R.id.cvCancel);
        cvDocuments = findViewById(R.id.cvDocuments);
        cvLogout = findViewById(R.id.cvLogout);
        lytAppointLoader = findViewById(R.id.lytAppointLoader);
        lytCancelLoader = findViewById(R.id.lytCancelLoader);

        // check for internet connection
        detectInternet = new DetectInternet(getApplicationContext());
        isInternetPresent = detectInternet.isConnectingToInternet();
        if (!isInternetPresent) {
            noInternetDialog(this);
        }

        // loggedin user data
        SharedPreferences prefs = getSharedPreferences("SF_PREF", MODE_PRIVATE);
        final String userID = prefs.getString("userID", null);
        final String studentID = prefs.getString("studentID", null);
        final String name = prefs.getString("name", null);
        final String firebaseKey = prefs.getString("firebaseKey", null);
        activeFirebaseKey = firebaseKey;

        // check logged in status
        if (userID == null || userID.isEmpty()) {
            // redirect to login
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        } else {
            tvLoggedStudentName.setText("Name: " + name);
            tvLoggedStudentID.setText("Student ID: " + studentID);
        }

        // check exist token
        if (activeFirebaseKey != null && !activeFirebaseKey.isEmpty()) {
            lytQueueInfo.setVisibility(View.VISIBLE);
            lytDefault.setVisibility(View.GONE);
        }

        // do appoint
        cvAppoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lytAppointLoader.setVisibility(View.VISIBLE);
                // generate new firebase key
                String generateFirebaseKey = queueDatabaseReference.push().getKey();
                appoint(userID, generateFirebaseKey);
            }
        });

        // cancel appoint
        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lytCancelLoader.setVisibility(View.VISIBLE);
                cancelAppoint(activeFirebaseKey);
            }
        });

        // documents
        cvDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DocumentsListActivity.class);
                startActivity(intent);
            }
        });

        // user logout
        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        // firebase instance
        firebaseDatabase = FirebaseDatabase.getInstance();

        // set reference nodes
        queueDatabaseReference = firebaseDatabase.getReference("queue");
        totalQueueDatabaseReference = firebaseDatabase.getReference("total_queue");

        // get token stats
        if (activeFirebaseKey != null && !activeFirebaseKey.isEmpty()) {
            getRealtimeData(activeFirebaseKey);
        }
    }

    // appoint
    private void appoint(String user_id, String firebase_key) {
        ApiRequestData api = Retroserver.getClient().create(ApiRequestData.class);
        Call<ResponsModel> getdata = api.appoint(user_id, firebase_key);
        getdata.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {

                Boolean success = response.body().getSuccess();
                String message = response.body().getMessage();

                if (success) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = getSharedPreferences("SF_PREF", MODE_PRIVATE).edit();
                    editor.putString("firebaseKey", firebase_key);
                    editor.apply();

                    activeFirebaseKey = firebase_key;
                    getRealtimeData(activeFirebaseKey);
                } else {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                lytAppointLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // cancel appoint
    private void cancelAppoint(String firebase_key) {
        ApiRequestData api = Retroserver.getClient().create(ApiRequestData.class);
        Call<ResponsModel> getdata = api.cancelAppoint(firebase_key);
        getdata.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                Boolean success = response.body().getSuccess();
                String message = response.body().getMessage();
                if (!success) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get realtime data
    private void getRealtimeData(String firebase_key) {
        queueDatabaseReference.child(firebase_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Queue queue = dataSnapshot.getValue(Queue.class);

                // update values
                if (queue != null) {
                    tvTokenNumber.setText(queue.getToken());
                    tvName.setText("Name: " + queue.getStudentName());
                    tvStudentID.setText("Student ID: " + queue.getStudentID());
                    tvCounter.setText(queue.getCounter());
                    tvMyPosition.setText(queue.getPosition());
                    myCounter = queue.getCounter();

                    getTotalQueueData(myCounter);

                    // hide/visible
                    lytDefault.setVisibility(View.GONE);
                    lytQueueInfo.setVisibility(View.VISIBLE);
                } else {
                    // delete firebaseKey from storage
                    SharedPreferences.Editor editor = getSharedPreferences("SF_PREF", MODE_PRIVATE).edit();
                    editor.putString("firebaseKey", null);
                    editor.apply();

                    // redirect to report
                    Intent intent = new Intent(MainActivity.this, ThankYouActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // get total queue data
    private void getTotalQueueData(String myCounter) {
        totalQueueDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<Integer, String> totalQueue = (HashMap<Integer, String>) dataSnapshot.getValue();

                if (!myCounter.isEmpty()) {
                    String total_queue_in_counter = String.valueOf(totalQueue.get("counter_" + myCounter));
                    tvTotalQueue.setText(total_queue_in_counter);

                    Log.e(TAG, myCounter);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "" + error.toException());
            }
        });
    }

    // logout
    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences("SF_PREF", MODE_PRIVATE).edit();
        editor.putString("userID", null);
        editor.putString("studentID", null);
        editor.putString("name", null);
        editor.apply();

        Toast.makeText(MainActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();

        // redirect to login
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    // toolbar
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }
}
