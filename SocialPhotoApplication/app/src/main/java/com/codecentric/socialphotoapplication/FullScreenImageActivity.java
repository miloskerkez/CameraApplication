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

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;

import java.io.File;


public class FullScreenImageActivity extends Activity implements View.OnCreateContextMenuListener {

    String path;

    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ActionBar mActionBar = getActionBar();
        mActionBar.hide();

        path = (String) getIntent().getExtras().get("path");
        ImageView picture = (ImageView) findViewById(R.id.picture);

        picture.setImageBitmap(Utils.decodeFile(path, 1000));

        registerForContextMenu(picture);

        uiHelper = new UiLifecycleHelper(this, Utils.callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
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
            case R.id.fb_image:
                loginAndPostOnFacebook(path);
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void loginAndPostOnFacebook(final String picPath) {
        Utils.openFacebookSessionAndPost(picPath, FullScreenImageActivity.this, uiHelper);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(FullScreenImageActivity.this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, Utils.dialogCallback);
    }

}
