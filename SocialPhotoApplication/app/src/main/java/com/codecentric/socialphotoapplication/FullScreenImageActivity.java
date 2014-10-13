package com.codecentric.socialphotoapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.io.File;


public class FullScreenImageActivity extends Activity implements View.OnCreateContextMenuListener {

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ActionBar mActionBar = getActionBar();
        mActionBar.hide();

        path = (String) getIntent().getExtras().get("path");
        ImageView picture = (ImageView) findViewById(R.id.picture);
        //picture.setImageBitmap(BitmapFactory.decodeFile(path));

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/1000, photoH/1000);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        picture.setImageBitmap(BitmapFactory.decodeFile(path, bmOptions));

        registerForContextMenu(picture);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.full_screen_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

        getMenuInflater().inflate(R.menu.full_screen_image, contextMenu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.mail_image:
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("application/image");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[0]);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, new File(path).getName());
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, new File(path).getName() + " sent from SocialPhoto");
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            default:
                return super.onContextItemSelected(item);
        }
    }

}
