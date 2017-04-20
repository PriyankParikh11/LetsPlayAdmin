package com.priyank.letsplayadmin;
//Sixth B Rejected Activity
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.firebase.client.Firebase;
import com.firebase.client.core.Context;
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
 * Created by priyank on 1/3/17.
 */

public class RejectedListDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ParticipantDetails ";

    TextView mEmail, mFName, mLName, mAddress, mContact, mEContact,
            mGender, mGames, mAgeGroup, mLoggedInEmailAddress;
    Button mRemove, mHome;

    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    String base64Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rejected_details);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Connect to Firebase Database
        mDatabase = FirebaseDatabase.getInstance();

        Firebase.setAndroidContext(RejectedListDetailActivity.this);

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
        mLoggedInEmailAddress = (TextView) findViewById(R.id.logged_in_email_address);

        mHome = (Button) findViewById(R.id.undo_reject);
        mRemove = (Button) findViewById(R.id.remove);

        mHome.setOnClickListener(this);
        mRemove.setOnClickListener(this);

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
        String sLoggedInEmailAddress = intent.getStringExtra("EmailAddress");

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
        mLoggedInEmailAddress.setText(sLoggedInEmailAddress);

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
        mLoggedInEmailAddress.setTypeface(mCustomFont3);

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
        mLoggedInEmailAddress.setTextSize(25);

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
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(), "Profile image download Failed!" + exception, Toast.LENGTH_LONG).show();
            }
        });

        Glide.with(RejectedListDetailActivity.this /* context */)
                .using(new FirebaseImageLoader())
                .load(mProfileRef)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .into(mProfile);

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Log.d(TAG, "Profile Image Clicked");

                Intent profileIntent = new Intent(RejectedListDetailActivity.this, FullImageActivity.class);

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
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(), "Certificate image download Failed!" + exception, Toast.LENGTH_LONG).show();
            }
        });

        Glide.with(RejectedListDetailActivity.this /* context */)
                .using(new FirebaseImageLoader())
                .load(mCertificateRef)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .into(mCertificate);

        mCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Log.d(TAG, "Certificate Image Clicked");

                Intent certificateIntent = new Intent(RejectedListDetailActivity.this, FullImageActivity.class);

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

        Intent intent = getIntent();
        final String sFirstName = intent.getStringExtra("fname");
        final String sLastName = intent.getStringExtra("lname");

        if (view == mRemove) {

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                    (RejectedListDetailActivity.this, R.style.AlertDialogCustom));

            builder.setTitle("Remove Participant?");
            builder.setIcon(R.mipmap.remove);
            builder.setMessage("Are you sure you want to permanently Remove this Entry?");

            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    DatabaseReference mNameRef = mDatabase.getReference("Rejected Participants");
                    //Log.e(TAG, "Reject Name Ref " + mNameRef);

                    mNameRef.child(sFirstName + " " + sLastName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //Log.e(TAG, "Data snapshot " + dataSnapshot);
                            dataSnapshot.getRef().removeValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //Delete Profile and Certificate Images from Firebase Storage
                    Intent intent1 = getIntent();

                    String sContact = intent1.getStringExtra("contact");
                    //Storage reference from our app
                    StorageReference mStorageRef = mStorage.getReferenceFromUrl("gs://letsplay-6cc97.appspot.com/");

                    StorageReference mProfileRef = mStorageRef.child(sFirstName + " " + sLastName + " (" + sContact + ")").child("profile.jpg");
                    //Log.e(TAG, "Profile Reference " + mProfileRef);

                    mProfileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });

                    StorageReference mCertificateRef = mStorageRef.child(sFirstName + " " + sLastName + " (" + sContact + ")").child("Certificate.jpg");

                    mCertificateRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });

                    closeRejectedActivity();
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

        if(view == mHome){

            closeRejectedActivity();
        }
    }

    public void closeRejectedActivity(){

        Intent closeIntent = new Intent(RejectedListDetailActivity.this, ParticipantActivity.class);
        closeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(closeIntent);
    }
}
