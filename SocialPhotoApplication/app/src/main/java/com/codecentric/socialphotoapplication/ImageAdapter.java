package com.codecentric.socialphotoapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Arnold on 10/8/2014.
 */
public class ImageAdapter extends BaseAdapter
{

    private final int size;
    File mediaStorageDir;
    private boolean asc;
    private Context context;
    private LruCache<String, Bitmap> memoryCache;



    /*private class GridComponent extends View {

        ViewGroup.LayoutParams layPam;
        ImageView v;

        public GridComponent(Context context) {
            super(context);
            v = new ImageView(context);
        }

        public void setImageBitmap(Bitmap bm) {
            v.setImageBitmap(bm);
            setLayoutParams(new ViewGroup.LayoutParams(bm.getWidth(), bm.getHeight()));
        }



    }*/

    public ImageAdapter(Context context, boolean asc, int size)
    {
            this.asc = asc;
            this.context = context;
            this.size = size;
            //It have to be matched with the directory in SDCard
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
            System.out.println("Directory stuff");
            System.out.println(mediaStorageDir.isDirectory());

            // Get max available VM memory, exceeding this amount will throw an
            // OutOfMemory exception. Stored in kilobytes as LruCache takes an
            // int in its constructor.
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            memoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };

    }

    public Bitmap getBitmapFromMemoryCache(String path) {
        Bitmap bmp = memoryCache.get(path);
        if (bmp == null) {
            bmp = Utils.decodeFile(path, 200);
            memoryCache.put(path, bmp);
            return bmp;
        }
        return bmp;
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
        //final GridComponent picture = ((view == null)? new GridComponent(viewGroup.getContext()) : (GridComponent) view);
        final ImageView picture = ((view == null)? new ImageView(viewGroup.getContext()): (ImageView) view);
        
        picture.setPadding(5,5,5,5);

        final String path = (String) getItem(i);
        new Thread() {
            @Override
            public void run() {
                final Bitmap bmp = getBitmapFromMemoryCache(path);
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        picture.setImageBitmap(bmp);
                        picture.setLayoutParams(new AbsListView.LayoutParams(size, size));
                    }
                });
            }
        }.start();


        return picture;
    }

    public void toggleSorting() {
        asc = !asc;
    }
}