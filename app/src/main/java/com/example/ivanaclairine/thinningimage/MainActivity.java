package com.example.ivanaclairine.thinningimage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;


public class MainActivity extends Activity {

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageButton buttonLoadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLoadImage = (ImageButton) findViewById(R.id.buttonLoadPictureImage);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            final Bitmap originalBm = BitmapFactory.decodeFile(picturePath);
            int[] pix = new int[originalBm.getWidth() * originalBm.getHeight()];
            originalBm.getPixels(pix, 0, originalBm.getWidth(), 0, 0, originalBm.getWidth(), originalBm.getHeight());

            //hilangkan load button
            buttonLoadImage.setVisibility(View.INVISIBLE);
            TextView title = (TextView) findViewById(R.id.zhangSuen);
            title.setVisibility(View.VISIBLE);
            title.setBackgroundColor(Color.BLACK);
            title.setTextColor(Color.WHITE);
            title.setTextSize(20);

            //buat ditampilin
            final Bitmap bmTemp = Bitmap.createScaledBitmap(originalBm, 600, 800, true);
            Bitmap bmThin = Bitmap.createScaledBitmap(originalBm, 600, 800, true);

            final ImageView imageView = (ImageView) findViewById(R.id.originalPict);
            final ImageView imageView2 = (ImageView) findViewById(R.id.thinnedPict);

            imageView.setImageBitmap(bmTemp);
            imageView2.setImageBitmap(bmThin);

            ZhangSuen zhangSuen = new ZhangSuen(bmTemp);
            bmThin = zhangSuen.doSkeleton();
            imageView2.setImageBitmap(bmThin);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
