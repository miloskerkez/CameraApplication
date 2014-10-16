package com.codecentric.socialphotoapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.janmuller.android.simplecropimage.CropImage;


public class CameraActivity extends Activity {

    private Camera cam;
    private CameraPreview camPreview;
    private static int numCam;
    private File tempFile;

    private byte[] object;
    private Uri  picUri;
    private Uri  picUri2;
    private String stringPicture;


    public static final int MEDIA_TYPE_IMAGE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ActionBar mActionBar = getActionBar();
        mActionBar.hide();

        ImageButton switchCam = (ImageButton)findViewById(R.id.switchCamBtn);
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras < 2) {
            switchCam.setVisibility(View.INVISIBLE);
        }

        Intent i = getIntent();
        int number = i.getIntExtra("camera", Camera.CameraInfo.CAMERA_FACING_BACK);
        numCam = number;
        System.out.println(number);
        // Create an instance of Camera

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

    private static Camera getCameraInstance(int camID){
        Camera cam = null;
        try{
            //cam = Camera.open();
            cam = Camera.open(camID);
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

            Button save = (Button)findViewById(R.id.buttonSave);
            save.setVisibility(View.VISIBLE);
            Button crop = (Button)findViewById(R.id.buttonCrop);
            crop.setVisibility(View.VISIBLE);
            Button cancel = (Button)findViewById(R.id.buttonCancel);
            cancel.setVisibility(View.VISIBLE);
            Button capture = (Button)findViewById(R.id.button_capture);
            capture.setVisibility(View.INVISIBLE);
            ImageButton change = (ImageButton)findViewById(R.id.switchCamBtn);
            change.setVisibility(View.INVISIBLE);

        }
    };





    public void capturePicture(View view){
        cam.autoFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                cam.takePicture(null, null, pic);
            }
        });


    }

    private static final int REQUEST_CODE_CROP_IMAGE = 0x3;

    private void runCropImage() {

        System.out.println("CameraActivity says: " + tempFile.getAbsolutePath());
        System.out.println("Uri:" + Uri.fromFile(new File(tempFile.getAbsolutePath())));

        // create explicit intent
        Intent intent = new Intent(this, CropImage.class);

        // tell CropImage activity to look for image to crop
        String filePath = tempFile.getAbsolutePath();
        intent.putExtra(CropImage.IMAGE_PATH, filePath);

        // allow CropImage activity to rescale image
        intent.putExtra(CropImage.SCALE, true);

        // if the aspect ratio is fixed to ratio 3/2
        intent.putExtra(CropImage.ASPECT_X, 3);
        intent.putExtra(CropImage.ASPECT_Y, 2);

        // start activity CropImage with certain request code and listen
        // for result
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {

            return;
        }

        switch (requestCode) {

            case REQUEST_CODE_CROP_IMAGE:

                String path = data.getStringExtra(CropImage.IMAGE_PATH);

                // if nothing received
                if (path == null) {

                    return;
                }

                // cropped bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());

                Intent i = new Intent(CameraActivity.this, FullScreenImageActivity.class);
                i.putExtra("path", tempFile.getAbsolutePath());
                startActivity(i);

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void cancelPicture(View v) {
        Intent intentCancel = new Intent(this, CameraActivity.class);
        startActivity(intentCancel);
    }

    public void cropPicture(View v) {

        savePicture(v);

        runCropImage();

        /*Intent intentSave = new Intent(this, MainActivity.class);
        startActivity(intentSave);*/
    }

    public void savePicture(View v) {

        tempFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(object);
            fos.close();

        } catch (FileNotFoundException e) {
            //Log.d(TAG "File not found: " + e.getMessage());
        } catch (IOException e) {
            //Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }


    public void switchCamera(View v){
        Intent intent = new Intent(this, CameraActivity.class);
        if (numCam == Camera.CameraInfo.CAMERA_FACING_BACK) {
            numCam = Camera.CameraInfo.CAMERA_FACING_FRONT;
            intent.putExtra("camera", numCam);
            startActivity(intent);
        }else{
            numCam = Camera.CameraInfo.CAMERA_FACING_BACK;
            intent.putExtra("camera", numCam);
            startActivity(intent);
        }


    }


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

    @Override
    protected void onResume(){
        super.onResume();
        cam = getCameraInstance(numCam);

        Camera.Parameters params = cam.getParameters();
        try {
            params.setFocusMode("continuous-picture");

            if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }


            cam.setParameters(params);
        }
        catch (Exception e) {
            Log.i("CameraActivity", "Failed to set parameters, front camera does not have flash");
        }
        camPreview = new CameraPreview(this, cam);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeAllViews();
        preview.addView(camPreview);
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }



}
