package com.codecentric.socialphotoapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.SurfaceView;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends Activity {

    private Camera cam;
    private CameraPreview camPreview;

    private byte[] object;


    public static final int MEDIA_TYPE_IMAGE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        cam = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        camPreview = new CameraPreview(this, cam);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(camPreview);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
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

    private boolean checkCameraHardware(Context context){
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }else{
            return false;
        }
    }

    private static Camera getCameraInstance(){
        Camera cam = null;
        try{
            cam = Camera.open();
            //cam = Camera.open();
        }catch(Exception e){

        }
        return cam;
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    private Camera.PictureCallback pic = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            object = data;



        }
    };





    public void capturePicture(View view){
            //Intent intent = new Intent(this, PhotoActivity.class);

            cam.takePicture(null, null, pic);
        Button save = (Button)findViewById(R.id.buttonSave);
        save.setVisibility(View.VISIBLE);
        Button cancel = (Button)findViewById(R.id.buttonCancel);
        cancel.setVisibility(View.VISIBLE);
        Button capture = (Button)findViewById(R.id.button_capture);
        capture.setVisibility(View.INVISIBLE);
        Button change = (Button)findViewById(R.id.swichCamBtn);
        change.setVisibility(View.INVISIBLE);
           // intent.putExtra("object", object);
       // intent.putExtra("file", fileOutputStream);
        //if (object == null){
       //     System.out.println("Nema slike");
        //}else{
          //  System.out.println("ima slike");
       // }
        //intent.putExtra("object", object);
            //startActivity(intent);


    }

    public void cancelPicture(View v) {
        Intent intentCancel = new Intent(this, CameraActivity.class);
        startActivity(intentCancel);
    }

    public void savePicture(View v) {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        if (pictureFile == null){
            //Log.d("Error creating media file, check storage permissions: ");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(object);
            fos.close();

        } catch (FileNotFoundException e) {
            //Log.d(TAG "File not found: " + e.getMessage());
        } catch (IOException e) {
            //Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        Intent intentSave = new Intent(this, MainActivity.class);
        startActivity(intentSave);
    }


    /*public void switchCamera(View v){
        Intent intent = new Intent(this, CameraActivity.class);
        if (cameraNumber == 0){
            cameraNumber = 1;
            cam.stopPreview();
            cam = getCameraInstance(cameraNumber);
            cam.startPreview();

            startActivity(intent);
        }else if (cameraNumber == 1){
            cameraNumber = 0;
            startActivity(intent);
        }
    }*/


    private void releaseCamera(){
        if (cam != null){
            cam.release();
            cam = null;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        releaseCamera();
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }



}
