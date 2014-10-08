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

    public ImageAdapter(Context context)
    {

            //It have to be matched with the directory in SDCard
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

    }

    @Override
    public int getCount() {
        return mediaStorageDir.listFiles().length;
    }

    @Override
    public Object getItem(int i)
    {

        return mediaStorageDir.listFiles()[i].getAbsolutePath();

    }

    @Override
    public long getItemId(int i)
    {
        return i;
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
        int scaleFactor = Math.min(photoW/100, photoH/100);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        picture.setImageBitmap(BitmapFactory.decodeFile(path, bmOptions));

        return picture;
    }

}

class LazyImageAdapter extends BaseAdapter implements ImageLoader.ImageLoadListener {
    private Context mContext = null;
    private View.OnClickListener mItemClickListener;
    private Handler mHandler;
    private ImageLoader mImageLoader = null;
    private File mDirectory;

    /**
     * Lazy loading image adapter
     *
     * @param aContext
     * @param lClickListener click listener to attach to each item
     * @param lPath          the path where the images are located
     * @throws Exception when path can't be read from or is not a valid directory
     */
    public LazyImageAdapter(Context aContext, View.OnClickListener lClickListener, String lPath) throws Exception {
        mContext = aContext;
        mItemClickListener = lClickListener;
        mDirectory = new File(lPath);
        // Do some error checking
        if (!mDirectory.canRead()) {
            throw new Exception("Can't read this path");
        } else if (!mDirectory.isDirectory()) {
            throw new Exception("Path is a not a directory");
        }
        mImageLoader = new ImageLoader(this);
        mImageLoader.start();
        mHandler = new Handler();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // stop the thread we started
        mImageLoader.stopThread();
    }

    public int getCount() {
        return mDirectory.listFiles().length;
    }

    public Object getItem(int aPosition) {
        String lPath = null;
        File[] lFiles = mDirectory.listFiles();
        if (aPosition < lFiles.length) {
            lPath = mDirectory.listFiles()[aPosition].getAbsolutePath();
        }
        return lPath;
    }

    public long getItemId(int arg0) {
// TODO Auto-generated method stub
        return 0;
    }

    public View getView(final int aPosition, View aConvertView, ViewGroup parent) {
        ImageView picture = ((aConvertView == null)? new ImageView(parent.getContext()) : (ImageView) aConvertView);

        String path = (String)getItem(aPosition);

        /*BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/100, photoH/100);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        picture.setImageBitmap(BitmapFactory.decodeFile(path, bmOptions));*/

        mImageLoader.queueImageLoad(path, picture);

        return picture;
    }

    public void handleImageLoaded(final ImageView aImageView,final Bitmap aBitmap) {
        // The enqueue the following in the UI thread
        mHandler.post(new Runnable() {
            public void run() {
                // set the bitmap in the ImageView
                aImageView.setImageBitmap(aBitmap);
            }
        });
    }
}

/*public View getView(final int aPosition, View aConvertView, ViewGroup parent) {
        final ViewSwitcher lViewSwitcher;
        String lPath = (String) getItem(aPosition);
// logic for conserving resources see google video on making your ui fast
// and responsive
        if (null == aConvertView) {
            lViewSwitcher = new ViewSwitcher(mContext);
            lViewSwitcher.setPadding(8, 8, 8, 8);
            ProgressBar lProgress = new ProgressBar(mContext);
            lProgress.setLayoutParams(new ViewSwitcher.LayoutParams(80, 80));
            lViewSwitcher.addView(lProgress);
            ImageView lImage = new ImageView(mContext);
            lImage.setLayoutParams(new ViewSwitcher.LayoutParams(100, 100));
            lViewSwitcher.addView(lImage);
// attach the onclick listener
            lViewSwitcher.setOnClickListener(mItemClickListener);
        } else {
            lViewSwitcher = (ViewSwitcher) aConvertView;
        }
       *//* ViewTagInformation lTagHolder = (ViewTagInformation) lViewSwitcher
                .getTag();
        if (lTagHolder == null ||
                !lTagHolder.aImagePath.equals(lPath)) {
// The Tagholder is null meaning this is a first time load
// or this view is being recycled with a different image
// Create a ViewTag to store information for later
            ViewTagInformation lNewTag = new ViewTagInformation();
            lNewTag.aImagePath = lPath;
            lViewSwitcher.setTag(lNewTag);*//*
// Grab the image view
// Have the progress bar display
// Then queue the image loading
        ImageView lImageView = (ImageView) lViewSwitcher.getChildAt(1);
        lViewSwitcher.setDisplayedChild(PROGRESSBARINDEX);
        mImageLoader.queueImageLoad(lPath, lImageView, lViewSwitcher);
    }

    return lViewSwitcher;
}*/
