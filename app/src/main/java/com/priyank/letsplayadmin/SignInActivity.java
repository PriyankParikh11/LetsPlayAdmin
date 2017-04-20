package com.priyank.letsplayadmin;
//First Activity

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by priyank on 21/2/17.
 */

public class SignInActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private EditText mEmailAddress;
    private EditText mPassword;
    private Button mSignIn;
    private TextView mSignUp;
    TextView mAutoFill, mWelcome;

    //Firebase auth object
    private FirebaseAuth mFirebaseAuth;

    //progress dialog
    private ProgressDialog mProgressDialog;

    String mPossibleEmail="";
    private Rect mRect = new Rect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mFirebaseAuth = FirebaseAuth.getInstance();

        //if getCurrentUser does not returns null
        if (mFirebaseAuth.getCurrentUser() != null) {
            //that means user is already logged in
            //so close this activity
            finish();
            //and open Participant activity
            startActivity(new Intent(SignInActivity.this, ParticipantActivity.class));
        }

        //Views for Email, password and Log in Button
        mEmailAddress = (EditText) findViewById(R.id.editTextEmail);
        mPassword = (EditText) findViewById(R.id.editTextPassword);
        mSignIn = (Button) findViewById(R.id.buttonSignIn);
        mAutoFill = (TextView) findViewById(R.id.email_autofill);

        Typeface mCustomFont = Typeface.createFromAsset(getAssets(), "fonts/FFF.ttf");
        mWelcome = (TextView) findViewById(R.id.welcome_title);
        mWelcome.setTypeface(mCustomFont);
        mWelcome.setTextSize(30);

        //Password Input type
        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final Typeface mCustomFont1 = Typeface.createFromAsset(getAssets(), "fonts/Playfair.otf");
        mPassword.setTypeface(mCustomFont1);
        mPassword.setTextSize(25);

        mEmailAddress.setTypeface(mCustomFont1);
        mEmailAddress.setTextSize(25);

        mEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(mEmailAddress.length() == 0) {
                    mAutoFill.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(mEmailAddress.length() > 0) {
                    mAutoFill.setVisibility(View.INVISIBLE);

                }else{
                    mAutoFill.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(mEmailAddress.length() > 0) {
                    mAutoFill.setVisibility(View.INVISIBLE);

                }else{
                    mAutoFill.setVisibility(View.VISIBLE);
                }
            }
        });

        mSignUp = (TextView) findViewById(R.id.textViewSignUp);
        mProgressDialog = new ProgressDialog(this);
        mSignIn.setOnClickListener(this);
        mSignUp.setOnClickListener(this);

        mAutoFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Autofill OnClick " + v);

                //Account Manager Permission request
                AccountManager mManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
                if (ActivityCompat.checkSelfPermission(SignInActivity.this,
                        android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                Account[] mAccounts = AccountManager.get(SignInActivity.this)
                        .getAccounts(); //.getAccountsByType("com.google");
                for (Account account : mAccounts){

                    // TODO: Check possibleEmail against an email regex or treat
                    // account.name as an email address only for certain account.type values.
                    mPossibleEmail = account.name;
                    Log.e(TAG, "Account " + account);
                }

                mEmailAddress.setText(mPossibleEmail);
                mEmailAddress.setTypeface(mCustomFont1);
                mEmailAddress.setTextSize(25);
                mAutoFill.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void registeredUser() {
        //getting email and password from edit texts
        String sEmail = mEmailAddress.getText().toString().trim();
        String sPassword = mPassword.getText().toString().trim();

        //Check if email address and/or password fields are empty

        if (TextUtils.isEmpty(sEmail) && !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddress.getText().
                toString()).matches()) {
            Toast.makeText(this, "Email cannot be Empty", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(sPassword)) {
            Toast.makeText(this, "Password cannot be Empty", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog
        mProgressDialog = new ProgressDialog(this, R.style.MyProgressDialog);
        mProgressDialog.setMessage("Signing In...");
        mProgressDialog.show();

        mFirebaseAuth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        mProgressDialog.dismiss();

                        //if Task is successful
                        if (task.isSuccessful()) {
                            //start Participant Activity
                            finish();
                            startActivity(new Intent(SignInActivity.this, ParticipantActivity.class));

                        } else {
                            Toast.makeText(SignInActivity.this, "Log In Failed! " +
                                    "Email or Password is Incorrect", Toast.LENGTH_LONG).show();

                            mPassword.setText("");
                        }
                    }
                });
    }

    @Override
    public void onClick(View view){
        if(view == mSignIn){
            registeredUser();
        }

        if(view == mSignUp){
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"priyank@mybms.in"});
            email.putExtra(Intent.EXTRA_SUBJECT, "LetsPlayAdmin - Log In Issue");
            email.putExtra(Intent.EXTRA_TEXT, " ");
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client: "));
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        int[] location = new int[2];
        mPassword.getLocationOnScreen(location);
        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mPassword.getWidth();
        mRect.bottom = location[1] + mPassword.getHeight();

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN && !mRect.contains(x, y)) {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }
}
