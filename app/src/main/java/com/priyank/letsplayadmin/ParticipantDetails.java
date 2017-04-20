package com.priyank.letsplayadmin;
//Fourth Activity

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.firebase.client.Firebase;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by priyank on 15/2/17.
 */

public class ParticipantDetails extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ParticipantDetails ";

    TextView mEmail, mFName, mLName, mAddress, mContact, mEContact, mGender, mGames, mAgeGroup;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    private Firebase mFirebaseRef;
    Button mAccept, mDecline;
    String base64Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_of_participants);
        Log.e(TAG, "ParticipantDetailsActivity Started");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Connect to Firebase Database
        mDatabase = FirebaseDatabase.getInstance();

        //TextViews
        mEmail = (TextView) findViewById(R.id.email_address);
        mFName = (TextView) findViewById(R.id.first_name);
        mLName = (TextView) findViewById(R.id.last_name);
        mAddress = (TextView) findViewById(R.id.home_address);

        mContact = (TextView) findViewById(R.id.contact_number);
        mEContact = (TextView) findViewById(R.id.emergency_contact_number);
        mAgeGroup = (TextView) findViewById(R.id.age_group_title);
        mGames = (TextView) findViewById(R.id.selected_games);
        mGender = (TextView) findViewById(R.id.gender);

        //Buttons
        mAccept = (Button) findViewById(R.id.accept);
        mDecline = (Button) findViewById(R.id.reject);

        mAccept.setOnClickListener(this);
        mDecline.setOnClickListener(this);

        //Imageview
        final ImageView mProfile = (ImageView) findViewById(R.id.profile);
        final ImageView mCertificate = (ImageView) findViewById(R.id.certificate);

        Intent intent = getIntent();

        //Get String Intents from ParticipantNames Activity
        final String sFirstName = intent.getStringExtra("fname");
        final String sLastName = intent.getStringExtra("lname");
        String sEmailAddress = intent.getStringExtra("email");
        String sParticipantGender = intent.getStringExtra("gender");

        String sAgeGroup = intent.getStringExtra("selectedAgeGroup");
        String sSelectedGames = intent.getStringExtra("games");
        String sHomeAddress = intent.getStringExtra("address");
        String sContact = intent.getStringExtra("contact");
        String sEmergencyContact = intent.getStringExtra("econtact");

        //Log.d(TAG, "Intent Age Group " + sAgeGroup);

        //Set Values from Strings
        mFName.setText(sFirstName);
        mLName.setText(sLastName);
        mGender.setText(sParticipantGender);
        mEmail.setText(sEmailAddress);

        mAgeGroup.setText(sAgeGroup);
        mGames.setText(sSelectedGames);
        mAddress.setText(sHomeAddress);
        mContact.setText(sContact);
        mEContact.setText(sEmergencyContact);

        //Set fonts
        Typeface mCustomFont3 = Typeface.createFromAsset(getAssets(), "fonts/Monts.ttf");
        mFName.setTypeface(mCustomFont3);
        mLName.setTypeface(mCustomFont3);
        mGender.setTypeface(mCustomFont3);
        mEmail.setTypeface(mCustomFont3);

        mAgeGroup.setTypeface(mCustomFont3);
        mGames.setTypeface(mCustomFont3);
        mAddress.setTypeface(mCustomFont3);
        mContact.setTypeface(mCustomFont3);
        mEContact.setTypeface(mCustomFont3);

        //TextSize
        mFName.setTextSize(25);
        mLName.setTextSize(25);
        mGender.setTextSize(25);
        mEmail.setTextSize(25);

        mAgeGroup.setTextSize(20);
        mGames.setTextSize(20);
        mAddress.setTextSize(25);
        mEContact.setTextSize(25);
        mContact.setTextSize(25);

        mStorage = FirebaseStorage.getInstance();

        // Profile Image retrieve from Firebase Storage
        //Storage reference from our app
        StorageReference mStorageRef = mStorage.getReferenceFromUrl("gs://letsplay-6cc97.appspot.com/");
        StorageReference mProfileRef = mStorageRef.child(sFirstName + " " + sLastName + " (" + sContact + ")").child("profile.jpg");
        //Log.e(TAG, "Profile Reference " + mProfileRef);

        File localFile = null;
        try {
            localFile = File.createTempFile("profile", ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert localFile != null;
        mProfileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Profile image download Failed! " +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Glide.with(ParticipantDetails.this /* context */)
                .using(new FirebaseImageLoader())
                .load(mProfileRef)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .into(mProfile);

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Profile Image Clicked");

                Intent profileIntent = new Intent(ParticipantDetails.this, FullImageActivity.class);

                if (mProfile.getDrawable() == null){
                  Toast.makeText(getApplicationContext(), "Profile Image loading, please wait...", Toast.LENGTH_SHORT).show();
                }

                else {
                    Bitmap profileBitmap = ((GlideBitmapDrawable) mProfile.getDrawable().getCurrent()).getBitmap();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    profileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] profileImageInByte = stream.toByteArray();
                    //ByteArrayInputStream bis = new ByteArrayInputStream(profileImageInByte);

                    base64Image = Base64.encodeToString(profileImageInByte, Base64.DEFAULT);
                    //Log.i(TAG, "Profile Image length: " + profileImageInByte.length);

                    ModelBase64.base64Image = base64Image;
                    profileIntent.putExtra("fname", sFirstName);
                    profileIntent.putExtra("lname", sLastName);
                    startActivity(profileIntent);
                }
            }
        });

        // Certificate Image retrieve from Firebase Storage
        StorageReference mCertificateRef = mStorageRef.child(sFirstName + " " + sLastName + " (" + sContact + ")").child("Certificate.jpg");
        //Log.e(TAG, "Certificate Reference " + mCertificateRef);

        File localFile1 = null;
        try {
            localFile1 = File.createTempFile("Certificate", ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert localFile1 != null;
        mCertificateRef.getFile(localFile1).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Certificate image download Failed! " +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Glide.with(ParticipantDetails.this /* context */)
                .using(new FirebaseImageLoader())
                .load(mCertificateRef)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .into(mCertificate);

        mCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Certificate Image Clicked");

                Intent certificateIntent = new Intent(ParticipantDetails.this, FullImageActivity.class);

                if (mCertificate.getDrawable() == null){
                    Toast.makeText(getApplicationContext(), "Certificate Image loading, please wait...", Toast.LENGTH_SHORT).show();
                }

                else {
                    Bitmap certificateBitmap = ((GlideBitmapDrawable) mCertificate.getDrawable().getCurrent()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    certificateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] certificateImageInByte = stream.toByteArray();

                    base64Image = Base64.encodeToString(certificateImageInByte, Base64.DEFAULT);
                    //Log.i(TAG, "Profile Image length: " + certificateImageInByte.length);

                    ModelBase64.base64Image = base64Image;
                    certificateIntent.putExtra("fname", sFirstName);
                    certificateIntent.putExtra("lname", sLastName);
                    //certificateIntent.setFlags(certificateIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(certificateIntent);
                }
            }
        });
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

    @Override
    public void onClick(View view) {

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase("https://letsplay-6cc97.firebaseio.com/");

        final Intent intent = getIntent();

        if (view == mAccept) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                    (ParticipantDetails.this, R.style.AlertDialogCustom));
            builder.setTitle("Accept?");
            builder.setIcon(R.mipmap.accept_icon);
            builder.setMessage("Are you sure you want to ACCEPT this Application?");
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    closeAcceptedActivity();

                    //Get String Intents from ParticipantNames Activity
                    final String sFirstName = intent.getStringExtra("fname");
                    final String sLastName = intent.getStringExtra("lname");
                    String sEmailAddress = intent.getStringExtra("email");
                    String sParticipantGender = intent.getStringExtra("gender");

                    String sAgeGroup = intent.getStringExtra("selectedAgeGroup");
                    String sSelectedGames = intent.getStringExtra("games");
                    String sHomeAddress = intent.getStringExtra("address");
                    String sContact = intent.getStringExtra("contact");
                    String sEmergencyContact = intent.getStringExtra("econtact");
                    final String sLoggedInEmailAddress = intent.getStringExtra("EmailAddress");
                    Log.e(TAG, "Admin Logged in Email Address: " + sLoggedInEmailAddress);

                    Intent accept = new Intent(Intent.ACTION_SEND);
                    accept.putExtra(Intent.EXTRA_EMAIL, new String[]{sEmailAddress});
                    Log.i(TAG, "Send to Email Address: " + sEmailAddress);

                    accept.putExtra(Intent.EXTRA_SUBJECT, "LetsPlay - Sports Tournament");
                    accept.putExtra(Intent.EXTRA_TEXT,
                        "Hello " + sFirstName + "," +
                            "\n" +
                            "\n" +
                            "We are pleased to inform you that your application form " +
                            "for the sports tournament has been Accepted. \n" +
                            "\n" +
                            "Thank you for taking the time to fill the application Form. \n" +
                            "\n" +
                            "Administrator.");
                    accept.setType("message/rfc822");
                    startActivity(Intent.createChooser(accept, "Choose an Email client :"));

                    Firebase participants = mFirebaseRef.child("Accepted Participants");
                    Map<String, Object> participant = new HashMap<>();

                    participant.put("Email ", sEmailAddress);
                    participant.put("FirstName ", sFirstName);
                    participant.put("LastName ", sLastName);
                    participant.put("HomeAddress ", sHomeAddress);
                    participant.put("ContactNumber ", sContact);
                    participant.put("EmergencyNumber ", sEmergencyContact);
                    participant.put("Gender ", sParticipantGender);
                    participant.put("Games ", sSelectedGames);
                    participant.put("AgeGroup ", sAgeGroup);
                    participant.put("FullName ", sFirstName + " " + sLastName);
                    participant.put("Accepted by ", sLoggedInEmailAddress);

                    participants.child(sFirstName + " " + sLastName).setValue(participant);
                    {
                        DatabaseReference mNameRef = mDatabase.getReference(sAgeGroup);//.child(sFirstName + " " + sLastName);
                        Log.e(TAG, "Accept Name Ref " + mNameRef);

                        mNameRef.child(sFirstName + " " + sLastName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.e(TAG, "Datasnapshot " + dataSnapshot);
                                dataSnapshot.getRef().removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            TextView textView = (TextView) alert.findViewById(android.R.id.message);
            assert textView != null;
            textView.setTextSize(20);
        }

        if (view == mDecline) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                    (ParticipantDetails.this, R.style.AlertDialogCustom));

            builder.setTitle("Reject?");
            builder.setIcon(R.mipmap.reject_icon);
            builder.setMessage("Are you sure you want to REJECT this Application?");
            builder.setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    closeAcceptedActivity();
                    //Get String Intents from ParticipantNames Activity
                    final String sFirstName = intent.getStringExtra("fname");
                    final String sLastName = intent.getStringExtra("lname");
                    String sEmailAddress = intent.getStringExtra("email");
                    String sParticipantGender = intent.getStringExtra("gender");

                    String sAgeGroup = intent.getStringExtra("selectedAgeGroup");
                    String sSelectedGames = intent.getStringExtra("games");
                    String sHomeAddress = intent.getStringExtra("address");
                    String sContact = intent.getStringExtra("contact");
                    String sEmergencyContact = intent.getStringExtra("econtact");

                    final String sLoggedInEmailAddress = intent.getStringExtra("EmailAddress");
                    Log.e(TAG, "Logged in Email Address: " + sLoggedInEmailAddress);

                    Intent reject = new Intent(Intent.ACTION_SEND);
                    reject.putExtra(Intent.EXTRA_EMAIL, new String[]{sEmailAddress});
                    reject.putExtra(Intent.EXTRA_SUBJECT, "LetsPlay - Sports Tournament");

                    reject.putExtra(Intent.EXTRA_TEXT,
                            "Hello " + sFirstName + "," +
                                    "\n" +
                                    "\n" +
                                    "We are sorry to inform you that your application form " +
                                    "for the sports tournament has been Rejected. " +
                                    "The reason for rejection is: \n" +
                                    "\n" +
                                    "\n" +
                                    "Thank you for taking the time to fill in the application Form. \n" +
                                    "\n" +
                                    "Administrator.");
                    reject.setType("message/rfc822");

                    startActivity(Intent.createChooser(reject, "Choose an Email client :"));
                    Firebase participants = mFirebaseRef.child("Rejected Participants");

                    Map<String, Object> participant = new HashMap<>();

                    participant.put("Email ", sEmailAddress);
                    participant.put("FirstName ", sFirstName);
                    participant.put("LastName ", sLastName);
                    participant.put("HomeAddress ", sHomeAddress);
                    participant.put("ContactNumber ", sContact);
                    participant.put("EmergencyNumber ", sEmergencyContact);
                    participant.put("Gender ", sParticipantGender);
                    participant.put("Games ", sSelectedGames);
                    participant.put("AgeGroup ", sAgeGroup);
                    participant.put("FullName ", sFirstName + " " + sLastName);
                    participant.put("Rejected by ", sLoggedInEmailAddress);

                    participants.child(sFirstName + " " + sLastName).setValue(participant);
                    {
                        DatabaseReference mNameRef = mDatabase.getReference(sAgeGroup);
                        Log.e(TAG, "Reject Name Ref " + mNameRef);

                        mNameRef.child(sFirstName + " " + sLastName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.e(TAG, "Datasnapshot " + dataSnapshot);
                                dataSnapshot.getRef().removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            TextView textView = (TextView) alert.findViewById(android.R.id.message);
            assert textView != null;
            textView.setTextSize(20);
        }
    }

    public void closeAcceptedActivity() {
        Intent closeIntent = new Intent(ParticipantDetails.this, ParticipantActivity.class);
        closeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(closeIntent);
    }
}
