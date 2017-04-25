package com.priyank.letsplayadmin;
//Third Activity

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by priyank on 15/2/17.
 */

public class ParticipantNames extends AppCompatActivity {

    private static final String TAG = "ParticipantNames";

    FirebaseDatabase mDatabase;
    String sFirstName, sLastName, sEmail, sGender, sAgeGroup, sGames, sHomeAddress,
            sEmergencyNumber, sContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.details_of_participants);

        Log.v(TAG, "ParticipantNamesActivity Started");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent emailIntent = getIntent();
        final String mLoggedInEmailAddress = emailIntent.getStringExtra("EmailAddress");
        Log.i(TAG, "Logged in Email Address: " + mLoggedInEmailAddress);

        final Bundle parameters = getIntent().getExtras();

        if (parameters != null) {

            if (parameters.containsKey("Age6Years")) {
                setContentView(parameters.getInt("Age6Years"));
                selectedAgeGroup6Years();

            } else if (parameters.containsKey("Age12Years")) {
                setContentView(parameters.getInt("Age12Years"));
                selectedAgeGroup12Years();

            } else if (parameters.containsKey("Age16Years")) {
                setContentView(parameters.getInt("Age16Years"));
                selectedAgeGroup16Years();

            } else if (parameters.containsKey("Age40Years")) {
                setContentView(parameters.getInt("Age40Years"));
                selectedAgeGroup40Years();

            } else if (parameters.containsKey("Accepted")) {
                setContentView(parameters.getInt("Accepted"));
                acceptedList();

            } else if (parameters.containsKey("Rejected")) {
                setContentView(parameters.getInt("Rejected"));
                rejectedList();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectedAgeGroup6Years(){

        mDatabase = FirebaseDatabase.getInstance(); //Connect to Firebase Database

        final DatabaseReference mYear6Ref = mDatabase.getReference("Up to 6 Years/");

        //Create a New Adapter
        final ArrayAdapter<String> mAdapter1 = new ArrayAdapter<>(this,
                R.layout.simple_list_item_1, android.R.id.text1);

        mYear6Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for(final DataSnapshot yearSnapshot: dataSnapshot.getChildren()){

                    String mFullName = String.valueOf(yearSnapshot.getKey());

                    //String mFullName.length() >= 4 ? mFullName.substring(0, mFullName.length() - 5): "";

                    //Log.v(TAG, "Full Name " + mFullName);

                    //ListView View
                    final ListView mNameListView1 = (ListView) findViewById(R.id.name_list);

                    //Set Adapter
                    mNameListView1.setAdapter(mAdapter1);

                    //Add data to ListView
                    mAdapter1.add(mFullName);
                    mNameListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            TextView textView = (TextView) view;

                            String mSelectedName = textView.getText().toString();
                            //Log.v(TAG, "Clicked " + mSelectedName);

                            final Query mDetailRef = mDatabase.getReference("Up to 6 Years/" + mSelectedName);
                            //Log.v(TAG, "Details " + mDetailRef);

                            mDetailRef.orderByChild(mSelectedName).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    if(dataSnapshot.getKey().startsWith("FirstName")){
                                        sFirstName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "First Name: " + sFirstName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("LastName")){
                                        sLastName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Last Name: " + sLastName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Email")){
                                        sEmail = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Email: " + sEmail);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Gender")){
                                        sGender = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Gender: " + sGender);
                                    }

                                    if(dataSnapshot.getKey().startsWith("AgeGroup")){
                                        sAgeGroup = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Age Group: " + sAgeGroup);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Games")){
                                        sGames = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Games: " + sGames);
                                    }

                                    if(dataSnapshot.getKey().startsWith("HomeAddress")){
                                        sHomeAddress = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Home Address: " + sHomeAddress);
                                    }

                                    if(dataSnapshot.getKey().startsWith("EmergencyNumber")){
                                        sEmergencyNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Emergency Number: " + sEmergencyNumber);
                                    }

                                    if(dataSnapshot.getKey().startsWith("ContactNumber")){
                                        sContactNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Contact Number: " + sContactNumber);
                                    }

                                    Intent emailIntent = getIntent();
                                    final String sLoggedInEmailAddress = emailIntent.getStringExtra("EmailAddress");
                                    //Log.i(TAG, "Logged in Email Address: " + sLoggedInEmailAddress);

                                    Intent intent6Years = new Intent(ParticipantNames.this, ParticipantDetails.class);

                                    intent6Years.putExtra("fname", sFirstName);
                                    intent6Years.putExtra("lname", sLastName);
                                    intent6Years.putExtra("email", sEmail);
                                    intent6Years.putExtra("gender", sGender);
                                    intent6Years.putExtra("games", sGames);
                                    intent6Years.putExtra("address", sHomeAddress);
                                    intent6Years.putExtra("contact", sContactNumber);
                                    intent6Years.putExtra("econtact", sEmergencyNumber);
                                    intent6Years.putExtra("selectedAgeGroup", sAgeGroup);
                                    intent6Years.putExtra("EmailAddress", sLoggedInEmailAddress);

                                    //Clearing activity in case 'Back pressed' on ParticipantDetails Activity
                                    intent6Years.setFlags(intent6Years.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent6Years);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "Firebase error on details from Up to 6 Years: " + databaseError.getDetails());
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error for Up to 6 Years: " + databaseError.getDetails());
            }
        });
    }

    private void selectedAgeGroup12Years(){

        mDatabase = FirebaseDatabase.getInstance(); //Connect to Firebase Database

        final DatabaseReference mYear12Ref = mDatabase.getReference("6 to 12 Years");

        //Create a New Adapter
        final ArrayAdapter<String> mAdapter1 = new ArrayAdapter<>(this,
                R.layout.simple_list_item_1, android.R.id.text1);

        mYear12Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(final DataSnapshot yearSnapshot: dataSnapshot.getChildren()){
                    String mFullName = String.valueOf(yearSnapshot.getKey());
                    //Log.i(TAG, "Full Name " + mFullName);

                    //ListView View
                    final ListView mNameListView2 = (ListView) findViewById(R.id.name_list);

                    //Set Adapter
                    mNameListView2.setAdapter(mAdapter1);

                    //Add data to ListView
                    mAdapter1.add(mFullName);

                    mNameListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            TextView textView = (TextView) view;

                            String mSelectedName = textView.getText().toString();
                            //Log.i(TAG, "Clicked " + mSelectedName);

                            final Query mDetailRef = mDatabase.getReference("6 to 12 Years/" + mSelectedName);
                            //Log.i(TAG, "Details " + mDetailRef);

                            mDetailRef.orderByChild(mSelectedName).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    if(dataSnapshot.getKey().startsWith("FirstName")){
                                        sFirstName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "First Name: " + sFirstName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("LastName")){
                                        sLastName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Last Name: " + sLastName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Email")){
                                        sEmail = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Email: " + sEmail);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Gender")){
                                        sGender = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Gender: " + gender);
                                    }

                                    if(dataSnapshot.getKey().startsWith("AgeGroup")) {
                                        sAgeGroup = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Age Group: " + sAgeGroup);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Games")){
                                        sGames = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Games: " + sGames);
                                    }

                                    if(dataSnapshot.getKey().startsWith("HomeAddress")){
                                        sHomeAddress = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Home Address: " + sHomeAddress);
                                    }

                                    if(dataSnapshot.getKey().startsWith("EmergencyNumber")){
                                        sEmergencyNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Emergency Number: " + sEmergencyNumber);
                                    }

                                    if(dataSnapshot.getKey().startsWith("ContactNumber")){
                                        sContactNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Contact Number: " + sContactNumber);
                                    }

                                    Intent emailIntent = getIntent();
                                    final String sLoggedInEmailAddress = emailIntent.getStringExtra("EmailAddress");
                                    //Log.i(TAG, "Logged in Email Address: " + sLoggedInEmailAddress);

                                    //Profile Image

                                    Intent intent12Years = new Intent(ParticipantNames.this, ParticipantDetails.class);

                                    intent12Years.putExtra("fname", sFirstName);
                                    intent12Years.putExtra("lname", sLastName);
                                    intent12Years.putExtra("email", sEmail);
                                    intent12Years.putExtra("gender", sGender);
                                    intent12Years.putExtra("games", sGames);
                                    intent12Years.putExtra("address", sHomeAddress);
                                    intent12Years.putExtra("contact", sContactNumber);
                                    intent12Years.putExtra("econtact", sEmergencyNumber);
                                    intent12Years.putExtra("selectedAgeGroup", sAgeGroup);
                                    intent12Years.putExtra("EmailAddress", sLoggedInEmailAddress);

                                    //Clearing activity in case 'Back pressed' on ParticipantDetails Activity
                                    intent12Years.setFlags(intent12Years.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent12Years);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "Firebase error on details from 6 to 12 Years: " + databaseError.getDetails());

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error for 6 to 12 Years: " + databaseError.getDetails());
            }
        });
    }

    private void selectedAgeGroup16Years() {

        mDatabase = FirebaseDatabase.getInstance(); //Connect to Firebase Database

        final DatabaseReference mYear16Ref = mDatabase.getReference("13 to 16 Years");

        final ArrayAdapter<String> mAdapter1 = new ArrayAdapter<>(this,
                R.layout.simple_list_item_1, android.R.id.text1);

        mYear16Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(final DataSnapshot yearSnapshot: dataSnapshot.getChildren()){
                    String mFullName = String.valueOf(yearSnapshot.getKey());
                    //Log.i(TAG, "Full Name " + mFullName);

                    //ListView View
                    final ListView mNameListView3 = (ListView) findViewById(R.id.name_list);

                    //Set Adapter
                    mNameListView3.setAdapter(mAdapter1);

                    //Add data to ListView
                    mAdapter1.add(mFullName);

                    mNameListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            TextView textView = (TextView) view;

                            String mSelectedName = textView.getText().toString();
                            //Log.i(TAG, "Clicked " + mSelectedName);

                            final Query mDetailRef1 = mDatabase.getReference("13 to 16 Years/" + mSelectedName);
                            //Log.i(TAG, "Details " + mDetailRef);

                            mDetailRef1.orderByChild(mSelectedName).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if(dataSnapshot.getKey().startsWith("FirstName")){
                                        sFirstName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "First Name: " + sFirstName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("LastName")){
                                        sLastName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Last Name: " + sLastName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Email")){
                                        sEmail = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Email: " + email);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Gender")){
                                        sGender = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Gender: " + gender);
                                    }

                                    if(dataSnapshot.getKey().startsWith("AgeGroup")) {
                                        sAgeGroup = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Age Group: " + sAgeGroup);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Games")){
                                        sGames = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Games: " + games);
                                    }

                                    if(dataSnapshot.getKey().startsWith("HomeAddress")){
                                        sHomeAddress = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Home Address: " + sHomeAddress);
                                    }

                                    if(dataSnapshot.getKey().startsWith("EmergencyNumber")){
                                        sEmergencyNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Emergency Number: " + sEmergencyNumber);
                                    }

                                    if(dataSnapshot.getKey().startsWith("ContactNumber")){
                                        sContactNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Contact Number: " + sContactNumber);
                                    }

                                    Intent emailIntent = getIntent();
                                    final String sLoggedInEmailAddress = emailIntent.getStringExtra("EmailAddress");
                                    //Log.i(TAG, "Logged in Email Address: " + sLoggedInEmailAddress);

                                    Intent intent16Years = new Intent(ParticipantNames.this, ParticipantDetails.class);

                                    intent16Years.putExtra("fname", sFirstName);
                                    intent16Years.putExtra("lname", sLastName);
                                    intent16Years.putExtra("email", sEmail);
                                    intent16Years.putExtra("gender", sGender);
                                    intent16Years.putExtra("games", sGames);
                                    intent16Years.putExtra("address", sHomeAddress);
                                    intent16Years.putExtra("contact", sContactNumber);
                                    intent16Years.putExtra("econtact", sEmergencyNumber);
                                    intent16Years.putExtra("selectedAgeGroup", sAgeGroup);
                                    intent16Years.putExtra("EmailAddress", sLoggedInEmailAddress);

                                    //Clearing activity in case 'Back pressed' on ParticipantDetails Activity
                                    intent16Years.setFlags(intent16Years.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent16Years);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "Firebase error on details from 13 to 16 Years: " + databaseError.getDetails());

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error for 13 to 16 Years: " + databaseError.getDetails());
            }
        });
    }

    private void selectedAgeGroup40Years(){

        mDatabase = FirebaseDatabase.getInstance(); //Connect to Firebase Database
        final DatabaseReference mYear40Ref = mDatabase.getReference("Above 40 Years");

        final ArrayAdapter<String> mAdapter1 = new ArrayAdapter<>(this,
                R.layout.simple_list_item_1, android.R.id.text1);

        mYear40Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot yearSnapshot: dataSnapshot.getChildren()){

                    String mFullName = String.valueOf(yearSnapshot.getKey());
                    //Log.i(TAG, "Full Name " + mFullName);

                    //ListView View
                    final ListView mNameListView4 = (ListView) findViewById(R.id.name_list);

                    //Set Adapter
                    mNameListView4.setAdapter(mAdapter1);

                    //Add data to ListView
                    mAdapter1.add(mFullName);

                    mNameListView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            TextView textView = (TextView) view;

                            String mSelectedName = textView.getText().toString();
                            //Log.i(TAG, "Clicked " + mSelectedName);

                            final Query mDetailRef1 = mDatabase.getReference("Above 40 Years/" + mSelectedName);
                            //Log.i(TAG, "Details " + mDetailRef);

                            mDetailRef1.orderByChild(mSelectedName).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    if(dataSnapshot.getKey().startsWith("FirstName")){
                                        sFirstName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "First Name: " + sFirstName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("LastName")){
                                        sLastName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Last Name: " + sLastName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Email")){
                                        sEmail = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Email: " + sEmail);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Gender")){
                                        sGender = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Gender: " + gender);
                                    }

                                    if(dataSnapshot.getKey().startsWith("AgeGroup")) {
                                        sAgeGroup = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Age Group: " + sAgeGroup);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Games")){
                                        sGames = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Games: " + sGames);
                                    }

                                    if(dataSnapshot.getKey().startsWith("HomeAddress")){
                                        sHomeAddress = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Home Address: " + sHomeAddress);
                                    }

                                    if(dataSnapshot.getKey().startsWith("EmergencyNumber")){
                                        sEmergencyNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Emergency Number: " + sEmergencyNumber);
                                    }

                                    if(dataSnapshot.getKey().startsWith("ContactNumber")){
                                        sContactNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Contact Number: " + sContactNumber);
                                    }

                                    Intent emailIntent = getIntent();
                                    final String sLoggedInEmailAddress = emailIntent.getStringExtra("EmailAddress");
                                    //Log.i(TAG, "Logged in Email Address: " + sLoggedInEmailAddress);

                                    Intent intent40Years = new Intent(ParticipantNames.this, ParticipantDetails.class);

                                    intent40Years.putExtra("fname", sFirstName);
                                    intent40Years.putExtra("lname", sLastName);
                                    intent40Years.putExtra("email", sEmail);
                                    intent40Years.putExtra("gender", sGender);
                                    intent40Years.putExtra("selectedAgeGroup", sAgeGroup);
                                    intent40Years.putExtra("games", sGames);
                                    intent40Years.putExtra("address", sHomeAddress);
                                    intent40Years.putExtra("contact", sContactNumber);
                                    intent40Years.putExtra("econtact", sEmergencyNumber);
                                    intent40Years.putExtra("EmailAddress", sLoggedInEmailAddress);

                                    //Clearing activity in case 'Back pressed' on ParticipantDetails Activity
                                    intent40Years.setFlags(intent40Years.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent40Years);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "Firebase error on details from Above 40 Years: " + databaseError.getDetails());
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error for Above 40 Years: " + databaseError.getDetails());
            }
        });
    }

    private void acceptedList(){

        mDatabase = FirebaseDatabase.getInstance(); //Connect to Firebase Database

        final DatabaseReference mAcceptedRef = mDatabase.getReference("Accepted Participants");

        final ArrayAdapter<String> mAdapter1 = new ArrayAdapter<>(this,
                R.layout.simple_list_item_1, android.R.id.text1);

        mAcceptedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot acceptedSnapshot: dataSnapshot.getChildren()){

                    String mFullName = String.valueOf(acceptedSnapshot.getKey());
                    //Log.i(TAG, "Full Name " + mFullName);

                    //ListView View
                    final ListView mAcceptedListView = (ListView) findViewById(R.id.name_list);

                    //Set Adapter
                    mAcceptedListView.setAdapter(mAdapter1);

                    //Add data to ListView
                    mAdapter1.add(mFullName);

                    mAcceptedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            TextView textView = (TextView) view;

                            String mSelectedName = textView.getText().toString();
                            //Log.i(TAG, "Clicked " + mSelectedName);

                            final Query mAcceptedRef = mDatabase.getReference("Accepted Participants/" + mSelectedName);
                            //Log.i(TAG, "Details " + mAcceptedRef);

                            mAcceptedRef.orderByChild(mSelectedName).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    if(dataSnapshot.getKey().startsWith("FirstName")){
                                        sFirstName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "First Name: " + sFirstName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("LastName")){
                                        sLastName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Last Name: " + sLastName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Email")){
                                        sEmail = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Email: " + sEmail);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Gender")){
                                        sGender = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Gender: " + gender);
                                    }

                                    if(dataSnapshot.getKey().startsWith("AgeGroup")) {
                                        sAgeGroup = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Age Group: " + sAgeGroup);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Games")){
                                        sGames = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Games: " + sGames);
                                    }

                                    if(dataSnapshot.getKey().startsWith("HomeAddress")){
                                        sHomeAddress = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Home Address: " + sHomeAddress);
                                    }

                                    if(dataSnapshot.getKey().startsWith("EmergencyNumber")){
                                        sEmergencyNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Emergency Number: " + sEmergencyNumber);
                                    }

                                    if(dataSnapshot.getKey().startsWith("ContactNumber")){
                                        sContactNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Contact Number: " + sContactNumber);
                                    }

                                    Intent emailIntent = getIntent();
                                    final String sLoggedInEmailAddress = emailIntent.getStringExtra("EmailAddress");
                                    //Log.e(TAG, "Logged in Email Address: " + sLoggedInEmailAddress);

                                    Intent acceptedListIntent = new Intent(ParticipantNames.this, AcceptedListDetailActivity.class);

                                    acceptedListIntent.putExtra("fname", sFirstName);
                                    acceptedListIntent.putExtra("lname", sLastName);
                                    acceptedListIntent.putExtra("email", sEmail);
                                    acceptedListIntent.putExtra("gender", sGender);
                                    acceptedListIntent.putExtra("games", sGames);
                                    acceptedListIntent.putExtra("address", sHomeAddress);
                                    acceptedListIntent.putExtra("contact", sContactNumber);
                                    acceptedListIntent.putExtra("econtact", sEmergencyNumber);
                                    acceptedListIntent.putExtra("selectedAgeGroup", sAgeGroup);
                                    acceptedListIntent.putExtra("EmailAddress", sLoggedInEmailAddress);

                                    //Clearing activity in case 'Back pressed' on ParticipantDetails Activity
                                    acceptedListIntent.setFlags(acceptedListIntent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(acceptedListIntent);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "Firebase error on details from Accepted Participants: " + databaseError.getDetails());
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error for Accepted Participants: " + databaseError.getDetails());
            }
        });
    }

    private void rejectedList() {

        mDatabase = FirebaseDatabase.getInstance(); //Connect to Firebase Database

        final DatabaseReference mRejectedRef = mDatabase.getReference("Rejected Participants");
        Log.e(TAG, "Rejected Database Ref " + mRejectedRef);

        final ArrayAdapter<String> mAdapter1 = new ArrayAdapter<>(this,
                R.layout.simple_list_item_1, android.R.id.text1);

        mRejectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot rejectedSnapshot: dataSnapshot.getChildren()){

                    String mFullName = String.valueOf(rejectedSnapshot.getKey());
                    //Log.i(TAG, "Full Name " + mFullName);

                    //ListView View
                    final ListView mRejectedListView = (ListView) findViewById(R.id.name_list);

                    //Set Adapter
                    mRejectedListView.setAdapter(mAdapter1);

                    //Add data to ListView
                    mAdapter1.add(mFullName);

                    mRejectedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            TextView textView = (TextView) view;

                            String mSelectedName = textView.getText().toString();
                            //Log.i(TAG, "Clicked " + mSelectedName);

                            final Query mRejectedRef = mDatabase.getReference("Rejected Participants/" + mSelectedName);
                            //Log.i(TAG, "Details " + mRejectedRef);

                            mRejectedRef.orderByChild(mSelectedName).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    if(dataSnapshot.getKey().startsWith("FirstName")){
                                        sFirstName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "First Name: " + sFirstName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("LastName")){
                                        sLastName = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Last Name: " + sLastName);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Email")){
                                        sEmail = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Email: " + sEmail);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Gender")){
                                        sGender = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Gender: " + gender);
                                    }

                                    if(dataSnapshot.getKey().startsWith("AgeGroup")) {
                                        sAgeGroup = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Age Group: " + sAgeGroup);
                                    }

                                    if(dataSnapshot.getKey().startsWith("Games")){
                                        sGames = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Games: " + sGames);
                                    }

                                    if(dataSnapshot.getKey().startsWith("HomeAddress")){
                                        sHomeAddress = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Home Address: " + sHomeAddress);
                                    }

                                    if(dataSnapshot.getKey().startsWith("EmergencyNumber")){
                                        sEmergencyNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Emergency Number: " + sEmergencyNumber);
                                    }

                                    if(dataSnapshot.getKey().startsWith("ContactNumber")){
                                        sContactNumber = dataSnapshot.getValue().toString();
                                        //Log.i(TAG, "Contact Number: " + sContactNumber);
                                    }

                                    Intent emailIntent = getIntent();
                                    final String sLoggedInEmailAddress = emailIntent.getStringExtra("EmailAddress");
                                    //Log.e(TAG, "Logged in Email Address: " + sLoggedInEmailAddress);

                                    Intent rejectedListIntent = new Intent(ParticipantNames.this, RejectedListDetailActivity.class);

                                    rejectedListIntent.putExtra("fname", sFirstName);
                                    rejectedListIntent.putExtra("lname", sLastName);
                                    rejectedListIntent.putExtra("email", sEmail);
                                    rejectedListIntent.putExtra("gender", sGender);
                                    rejectedListIntent.putExtra("games", sGames);
                                    rejectedListIntent.putExtra("address", sHomeAddress);
                                    rejectedListIntent.putExtra("contact", sContactNumber);
                                    rejectedListIntent.putExtra("econtact", sEmergencyNumber);
                                    rejectedListIntent.putExtra("selectedAgeGroup", sAgeGroup);
                                    rejectedListIntent.putExtra("EmailAddress", sLoggedInEmailAddress);
                                    
                                    //Clearing activity in case 'Back pressed' on ParticipantDetails Activity
                                    rejectedListIntent.setFlags(rejectedListIntent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(rejectedListIntent);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "Firebase error on details from Rejected Participants: " + databaseError.getDetails());
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error for Rejected Participants: " + databaseError.getDetails());
            }
        });
    }
}
