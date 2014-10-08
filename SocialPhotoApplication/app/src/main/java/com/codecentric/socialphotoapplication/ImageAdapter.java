package com.codecentric.socialphotoapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnold on 10/8/2014.
 */
public class ImageAdapter extends BaseAdapter
{

    File mediaStorageDir;
    private boolean asc;

    public ImageAdapter(Context context, boolean asc)
    {

            this.asc = asc;
            //It have to be matched with the directory in SDCard
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
            System.out.println("Directory stuff");
            System.out.println(mediaStorageDir.isDirectory());
    }

    @Override
    public int getCount() {
        return mediaStorageDir.listFiles().length;
    }

    @Override
    public Object getItem(int i)
    {
        return mediaStorageDir.listFiles()[(asc)? i : getCount() - i - 1].getAbsolutePath();
    }

    @Override
    public long getItemId(int i)
    {
        return (asc)? i : getCount() - i - 1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        ImageView picture = ((view == null)? new ImageView(viewGroup.getContext()) : (ImageView) view);

        String path = (String) getItem(i);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/200, photoH/200);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        picture.setImageBitmap(BitmapFactory.decodeFile(path, bmOptions));

        return picture;
    }

    public void toggleSorting() {
        asc = !asc;
    }
}