package com.codecentric.socialphotoapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends Activity implements View.OnCreateContextMenuListener, AdapterView.OnItemClickListener {

    GridView gridView;
    ImageAdapter imageAdapter;
    boolean asc = true;

    private UiLifecycleHelper uiHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar, null);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        uiHelper = new UiLifecycleHelper(this, Utils.callback);
        uiHelper.onCreate(savedInstanceState);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.codecentric.socialphotoapplication",  //Replace your package name here
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();

    }

    private void refresh() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        try {
            if (mediaStorageDir.listFiles().length > 0) {
                setContentView(R.layout.main_grid);
                gridView = (GridView) findViewById(R.id.gridView);

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                imageAdapter = new ImageAdapter(this, true, width / 2 - 10);
                registerForContextMenu(gridView);
                try {
                    gridView.setAdapter(imageAdapter);
                    gridView.setOnItemClickListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                setContentView(R.layout.text);
            }
        } catch (Exception e) {
            setContentView(R.layout.text);
        }
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
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void startCamera(View v) {
           new CameraOpenTask().execute();
    }



    public void toggleSorting(View v) {
        imageAdapter.toggleSorting();
        gridView.invalidateViews();
        System.out.println("sort");
        ImageButton btn = (ImageButton) v;
        asc = !asc;
        if (asc) {
            btn.setImageResource(R.drawable.arrow_down);
    }
        else {
            btn.setImageResource(R.drawable.arrow_up);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

        getMenuInflater().inflate(R.menu.grid_item_menu, contextMenu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_image:
                File f = new File((String)imageAdapter.getItem(info.position));
                f.delete();
                refresh();
                return true;
            case R.id.mail_image:
                new MailTask().execute(info);
                return true;
            case R.id.fb_image:
                new FacebookTask().execute(info);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("path", (String) imageAdapter.getItem(i));
        startActivity(intent);
    }

    private void loginAndPostOnFacebook(final String picPath) {
        Utils.openFacebookSessionAndPost(picPath, this, uiHelper);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(MainActivity.this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, Utils.dialogCallback);
    }


    private class CameraOpenTask extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected Integer doInBackground(Void... params) {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
            return null;
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Starting camera...");
            Dialog.show();
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            Dialog.dismiss();
        }
    }

    private class MailTask extends AsyncTask<AdapterView.AdapterContextMenuInfo, Void, Integer> {
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected Integer doInBackground(AdapterView.AdapterContextMenuInfo... params) {
            AdapterView.AdapterContextMenuInfo info = params[0];
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("application/image");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[0]);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, new File((String)imageAdapter.getItem(info.position)).getName());
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, new File((String)imageAdapter.getItem(info.position)).getName() + " sent from SocialPhoto");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + imageAdapter.getItem(info.position)));
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            return null;
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Please wait...");
            Dialog.show();
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            Dialog.dismiss();
        }
    }

    private class FacebookTask extends AsyncTask<AdapterView.AdapterContextMenuInfo, Void, Integer> {
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected Integer doInBackground(AdapterView.AdapterContextMenuInfo... params) {
            AdapterView.AdapterContextMenuInfo info = params[0];
            loginAndPostOnFacebook((String) imageAdapter.getItem(info.position));
            return null;
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Please wait...");
            Dialog.show();
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            Dialog.dismiss();
        }
    }

}
