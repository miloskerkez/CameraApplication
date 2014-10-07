package com.codecentric.socialphotoapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = (GridView)findViewById(R.id.gridView);
        gridView.setAdapter(new MyAdapter(this));
    }

    private class MyAdapter extends BaseAdapter
    {
        private List<Item> items = new ArrayList<Item>();

        public MyAdapter(Context context)
        {

            List<String> tFileList = new ArrayList<String>();

            //It have to be matched with the directory in SDCard
            /*File f = new File("/SD kartica/DCIM/100MEDIA");

            File[] files=f.listFiles();

            for(int i=0; i<files.length; i++)
            {
                File file = files[i];
              *//*It's assumed that all file in the path
                are in supported type*//*
                items.add(new Item(i, file.getAbsolutePath()));
            }*/

            /*items.add(new Item("Image 1", R.drawable.ic_launcher));
            items.add(new Item("Image 2", R.drawable.ic_launcher));
            items.add(new Item("Image 3", R.drawable.ic_launcher));
            items.add(new Item("Image 4", R.drawable.ic_launcher));
            items.add(new Item("Image 5", R.drawable.ic_launcher));
            items.add(new Item("Image 6", R.drawable.ic_launcher));
            items.add(new Item("Image 7", R.drawable.ic_launcher));
            items.add(new Item("Image 8", R.drawable.ic_launcher));
            items.add(new Item("Image 9", R.drawable.ic_launcher));
            items.add(new Item("Image 10", R.drawable.ic_launcher));
            items.add(new Item("Image 11", R.drawable.ic_launcher));
            items.add(new Item("Image 12", R.drawable.ic_launcher));
            items.add(new Item("Image 13", R.drawable.ic_launcher));
            items.add(new Item("Image 14", R.drawable.ic_launcher));
            items.add(new Item("Image 15", R.drawable.ic_launcher));
            items.add(new Item("Image 1", R.drawable.ic_launcher));
            items.add(new Item("Image 2", R.drawable.ic_launcher));
            items.add(new Item("Image 3", R.drawable.ic_launcher));
            items.add(new Item("Image 4", R.drawable.ic_launcher));
            items.add(new Item("Image 5", R.drawable.ic_launcher));
            items.add(new Item("Image 6", R.drawable.ic_launcher));
            items.add(new Item("Image 7", R.drawable.ic_launcher));
            items.add(new Item("Image 8", R.drawable.ic_launcher));
            items.add(new Item("Image 9", R.drawable.ic_launcher));
            items.add(new Item("Image 10", R.drawable.ic_launcher));
            items.add(new Item("Image 11", R.drawable.ic_launcher));
            items.add(new Item("Image 12", R.drawable.ic_launcher));
            items.add(new Item("Image 13", R.drawable.ic_launcher));
            items.add(new Item("Image 14", R.drawable.ic_launcher));
            items.add(new Item("Image 15", R.drawable.ic_launcher));*/
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i)
        {
            return items.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return items.get(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            ImageView picture = new ImageView(MainActivity.this);

            Item item = (Item)getItem(i);

            picture.setImageBitmap(BitmapFactory.decodeFile(item.path));

            return picture;
        }

        private class Item
        {
            final int id;
            final String path;

            Item(int id, String path)
            {
                this.id = id;
                this.path = path;
            }
        }
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

    public void startCamera(View v){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}
