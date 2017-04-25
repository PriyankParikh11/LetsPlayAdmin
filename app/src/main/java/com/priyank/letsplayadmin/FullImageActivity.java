package com.priyank.letsplayadmin;
//Fifth Activity

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by priyank on 16/2/17.
 */

public class FullImageActivity extends AppCompatActivity {

    private static final String TAG = "ParticipantFullImage";
    ImageView mFullImage;
    Bitmap bitmap;
    OutputStream output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_view);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFullImage = (ImageView) findViewById(R.id.full_image);

        String mImage = ModelBase64.base64Image;
        bitmap = decodeImage(mImage);

        mFullImage.setImageBitmap(bitmap);
        mFullImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        mFullImage.setLongClickable(true);
        //mFullImage.setClickable(true);

        //First name and last intent for saved image folder name
        Intent intent = getIntent();
        final String mFirstName = intent.getStringExtra("fname");
        //Log.i(TAG, "First Name " + mFirstName);

        final String mLastName = intent.getStringExtra("lname");
        //Log.i(TAG, "Last Name " + mLastName);

        mFullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                        (FullImageActivity.this, R.style.AlertDialogCustom));

                builder.setTitle("Save?");
                builder.setIcon(R.mipmap.save);
                builder.setMessage("Save Image to Device?");
                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        saveImage(getWindow().getDecorView().findViewById(android.R.id.content));

                        Toast.makeText(getApplicationContext(),
                                "Saved Image to Storage/..../LetsPlay/" + mFirstName + " " + mLastName + "/",
                                Toast.LENGTH_LONG).show();
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

    private Bitmap decodeImage(String data) {
        byte[] b = Base64.decode(data, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }

    //Saving Images to Device under individual full named folder
    public void saveImage(View v) {

        Intent intent = getIntent();
        String mFirstName = intent.getStringExtra("fname");
        //Log.i(TAG, "First Name " + mFirstName);

        String mLastName = intent.getStringExtra("lname");
        //Log.i(TAG, "Last Name " + mLastName);

        View content = findViewById(R.id.full_image);
        content.setDrawingCacheEnabled(true);
        Bitmap bitmap = content.getDrawingCache();

        File mFilepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File mDir = new File(mFilepath.getAbsolutePath()
                + "/LetsPlay/" + mFirstName + " " + mLastName);

        //mDir.mkdirs();
        boolean wasSuccessful = mDir.mkdirs();
        if (!wasSuccessful) {
            Log.d(TAG, "Unable to create Dir");
        }

        // Create a name for the saved image
        File mFile = new File(mDir, mFirstName + " " + mLastName + ".jpg");

        try {
            output = new FileOutputStream(mFile);
            // Compress into jpg format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



