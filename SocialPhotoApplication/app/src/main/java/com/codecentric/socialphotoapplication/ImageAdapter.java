package com.codecentric.socialphotoapplication;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;

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

        picture.setImageBitmap(Utils.decodeFile(path, 200));

        return picture;
    }

    public void toggleSorting() {
        asc = !asc;
    }
}