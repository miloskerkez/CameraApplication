package com.codecentric.socialphotoapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import eu.janmuller.android.simplecropimage.CropImage;


public class CameraActivity extends Activity {

    private Camera cam;
    private CameraPreview camPreview;
    private static int numCam;
    private File tempFile;
    private byte[] pictureInByteArray;
    private boolean flashLight = false;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String TAG = "CAMERA ACTIVITY";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ActionBar mActionBar = getActionBar();
        mActionBar.hide();

        ImageButton switchCam = (ImageButton) findViewById(R.id.switchCamBtn);
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras < 2) {
            switchCam.setVisibility(View.INVISIBLE);
        }

        ImageButton flashButton = (ImageButton) findViewById(R.id.flashBTN);
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashOnOff();
                if (flashLight) {
                    Toast.makeText(getApplicationContext(), "flash light is on", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "flash light is off", Toast.LENGTH_SHORT).show();
                }
            }
        });

       /* Intent i = getIntent();
        int number = i.getIntExtra("camera", Camera.CameraInfo.CAMERA_FACING_BACK);
        numCam = number;*/
        //System.out.println(number);
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

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private static Camera getCameraInstance(int camID) {
        Camera cam = null;
        try {
            //cam = Camera.open();
            cam = Camera.open(camID);
        } catch (Exception e) {
             e.printStackTrace();
        }
        return cam;
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    private Camera.PictureCallback pic = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            pictureInByteArray = data;

            Button save = (Button) findViewById(R.id.buttonSave);
            save.setVisibility(View.VISIBLE);
            Button crop = (Button) findViewById(R.id.buttonCrop);
            crop.setVisibility(View.VISIBLE);
            Button cancel = (Button) findViewById(R.id.buttonCancel);
            cancel.setVisibility(View.VISIBLE);
            Button capture = (Button) findViewById(R.id.button_capture);
            capture.setVisibility(View.INVISIBLE);
            ImageButton change = (ImageButton) findViewById(R.id.switchCamBtn);
            change.setVisibility(View.INVISIBLE);
            ImageButton flash = (ImageButton) findViewById(R.id.flashBTN);
            flash.setVisibility(View.INVISIBLE);

        }
    };


    public void capturePicture(View view) {
        Camera.Parameters params = cam.getParameters();
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            if (flashLight) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
        }
        cam.setParameters(params);

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
        Intent intent = new Intent(CameraActivity.this, CropImage.class);

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
        new ReturnToCamera().execute();
    }


    public void cropPicture(View v) {

        savePictureForCropping();

        runCropImage();

        /*Intent intentSave = new Intent(this, MainActivity.class);
        startActivity(intentSave);*/
    }

    public void savePicture(View v) {
       new SavingPicture().execute();
     }

    public void savePictureForCropping(){
        tempFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(pictureInByteArray);
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }


    public void switchCamera(View v) {
        new SwitchingCamera().execute();
    }


    private void releaseCamera() {
        if (cam != null) {
            cam.release();
            cam = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        cam = getCameraInstance(numCam);

        Intent i = getIntent();
        numCam = i.getIntExtra("camera", Camera.CameraInfo.CAMERA_FACING_BACK);
        flashLight = i.getBooleanExtra("flashLight", false);

        try {
            Camera.Parameters params = cam.getParameters();
      /*   List<String> focusModes = params.getSupportedFocusModes();
        for (String s: focusModes){
            System.out.println("*************" +s);
        }*/

            params.setFocusMode("auto");


        /*if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
        {

            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }*/


            cam.setParameters(params);
        }catch (Exception e){
            Log.i("Camera", "Focus mode \"auto\" not supported");
        }
        camPreview = new CameraPreview(this, cam);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeAllViews();
        preview.addView(camPreview);
    }

    private void flashOnOff() {
        if (!flashLight) {
            flashLight = true;
        } else {
            flashLight = false;
        }
    }


   private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }


    private class ReturnToCamera extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog Dialog = new ProgressDialog(CameraActivity.this);

        @Override
        protected Integer doInBackground(Void... params) {
            Intent intentCancel = new Intent(CameraActivity.this, CameraActivity.class);
            intentCancel.putExtra("flashLight", flashLight);
            startActivity(intentCancel);
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

    private class SavingPicture extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog Dialog = new ProgressDialog(CameraActivity.this);

        @Override
        protected Integer doInBackground(Void... params) {
           tempFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

            try {
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(pictureInByteArray);
                fos.close();

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            Intent i = new Intent(CameraActivity.this, MainActivity.class);
            startActivity(i);
            return null;
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Saving picture...");
            Dialog.show();
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            Dialog.dismiss();
        }
    }

    private class SwitchingCamera extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog Dialog = new ProgressDialog(CameraActivity.this);

        @Override
        protected Integer doInBackground(Void... params) {
            Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
            if (numCam == Camera.CameraInfo.CAMERA_FACING_BACK) {
                numCam = Camera.CameraInfo.CAMERA_FACING_FRONT;
                intent.putExtra("camera", numCam);
                intent.putExtra("flashLight", flashLight);
                startActivity(intent);
            } else {
                numCam = Camera.CameraInfo.CAMERA_FACING_BACK;
                intent.putExtra("camera", numCam);
                intent.putExtra("flashLight", flashLight);
                startActivity(intent);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Switching camera...");
            Dialog.show();
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            Dialog.dismiss();
        }
    }



}