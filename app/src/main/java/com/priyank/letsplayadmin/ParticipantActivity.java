package com.priyank.letsplayadmin;
//Second  Activity
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ParticipantActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ParticipantActivity";

    FirebaseDatabase mDatabase;
    private FirebaseAuth mFirebaseAuth;
    TextView mAdmin;
    private Button mSignOut, mRefresh;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);
        Log.e(TAG, "ParticipantActivity Started");

        //Sign Out
        mFirebaseAuth = FirebaseAuth.getInstance();

        if(mFirebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(ParticipantActivity.this, SignInActivity.class));
        }

        FirebaseUser mFirebaseAdmin = mFirebaseAuth.getCurrentUser();

        mAdmin = (TextView) findViewById(R.id.admin);
        mSignOut = (Button) findViewById(R.id.sign_out);
        //Button mRefresh = (Button) findViewById(R.id.refreshButton);
        //mRefresh.setOnClickListener(this);

        //displaying logged in user name
        assert mFirebaseAdmin != null;
        mAdmin.setText("" + mFirebaseAdmin.getEmail());

        final String mLoggedInEmailAddress = mAdmin.getText().toString();
        mSignOut.setOnClickListener(this);

        //Participant Details

        //Get listView object from XML
        final ListView mAgeListView = (ListView) findViewById(R.id.ageGroup_list);
        //Create a New Adapter

        final ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this,
                R.layout.simple_list_item_1, android.R.id.text1);

        mRefresh = (Button) findViewById(R.id.action_refresh);
        mRefresh.setOnClickListener(this);

        //Assign Adapter to ListView
        mAgeListView.setAdapter(mAdapter);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 15s = 15000ms
                mAgeListView.setEmptyView(findViewById(R.id.emptyElement));

                /**
                mRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = getIntent();
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(intent);
                        overridePendingTransition( 0, 0);
                    }
                });
                **/
            }
        }, 15000);

        //Connect to Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        //Log.e(TAG, "Firebase Instance " + mDatabase);

        // Get a reference to the Participants child items it the database
        DatabaseReference mAgeRef = mDatabase.getReference();
        //Log.e(TAG, "Database Reference " + mAgeRef);

        // Assign a listener to detect changes to the child items
        // of the database reference.
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: " + dataSnapshot);

                for(DataSnapshot ageGroupSnapshot: dataSnapshot.getChildren()) {
                    final String mAgeGroup = ageGroupSnapshot.getKey();
                    Log.i(TAG, "Age Groups: " + mAgeGroup);

                    mAdapter.add(mAgeGroup);
                    //for(DataSnapshot mKeySnapshot: dataSnapshot.getChildren()) {
                    //final String mKey = mKeySnapshot.getKey();
                    //Log.i(TAG, "Key: " + mKey);
                    mAgeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            TextView textView = (TextView) view;

                            String mPosition = textView.getText().toString();
                            //Log.i(TAG, "Item Position: " + mPosition);

                            if (Objects.equals(mPosition, "Accepted Participants")) {
                                //Log.i(TAG, "Selected Age Group: " + mPosition);

                                Intent acceptedIntent = new Intent(ParticipantActivity.this, ParticipantNames.class);
                                acceptedIntent.putExtra("Accepted", R.layout.names_of_participants);
                                acceptedIntent.putExtra("EmailAddress", mLoggedInEmailAddress);
                                startActivity(acceptedIntent);
                            }

                            if (Objects.equals(mPosition, "Rejected Participants")) {
                                //Log.i(TAG, "Selected Age Group: " + mPosition);

                                Intent rejectedIntent = new Intent(ParticipantActivity.this, ParticipantNames.class);
                                rejectedIntent.putExtra("Rejected", R.layout.names_of_participants);
                                rejectedIntent.putExtra("EmailAddress", mLoggedInEmailAddress);
                                startActivity(rejectedIntent);
                            }

                            if (Objects.equals(mPosition, "Up to 6 Years")) {
                                //Log.i(TAG, "Selected Age Group: " + mPosition);

                                Intent year6Intent = new Intent(ParticipantActivity.this, ParticipantNames.class);
                                year6Intent.putExtra("Age6Years", R.layout.names_of_participants);
                                year6Intent.putExtra("EmailAddress", mLoggedInEmailAddress);
                                startActivity(year6Intent);
                            }

                            if (Objects.equals(mPosition, "6 to 12 Years")) {
                                //Log.i(TAG, "Selected Age Group is: "  + mPosition);

                                Intent year12Intent = new Intent(ParticipantActivity.this, ParticipantNames.class);
                                year12Intent.putExtra("Age12Years", R.layout.names_of_participants);
                                year12Intent.putExtra("EmailAddress", mLoggedInEmailAddress);
                                startActivity(year12Intent);
                            }

                            if (Objects.equals(mPosition, "13 to 16 Years")) {
                                //Log.i(TAG, "Selected Age Group is: " + mPosition);

                                Intent year16Intent = new Intent(ParticipantActivity.this, ParticipantNames.class);
                                year16Intent.putExtra("Age16Years", R.layout.names_of_participants);
                                year16Intent.putExtra("EmailAddress", mLoggedInEmailAddress);
                                startActivity(year16Intent);
                            }

                            if (Objects.equals(mPosition, "Above 40 Years")) {
                                //Log.i(TAG, "Selected Age Group is: " + mPosition);

                                Intent year40Intent = new Intent(ParticipantActivity.this, ParticipantNames.class);
                                year40Intent.putExtra("Age40Years", R.layout.names_of_participants);
                                year40Intent.putExtra("EmailAddress", mLoggedInEmailAddress);
                                startActivity(year40Intent);
                            }
                        }

                        @SuppressWarnings("unused")
                        public void onClick(View v) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled");
            }
        };

        mAgeRef.addValueEventListener(listener);
    }

    @Override
    public void onClick(View view) {
        //if SignOut is pressed
        if(view == mSignOut){

            //Signing out the user
            mFirebaseAuth.signOut();
            //closing activity
            finish();

            //starting SignIn activity
            startActivity(new Intent(ParticipantActivity.this, SignInActivity.class));
        }

        if(view == mRefresh){
            Intent intent = getIntent();
            finish();
            overridePendingTransition( 0, 0);
            startActivity(intent);
            overridePendingTransition( 0, 0);
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "++ ON START ++");
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.v(TAG, "- ON RESTART -");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "+ ON RESUME +");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "- ON DESTROY -");
    }
}
