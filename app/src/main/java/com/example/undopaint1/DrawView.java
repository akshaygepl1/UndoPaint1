package com.example.undopaint1;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DrawView extends AppCompatActivity {

    private CustomView mCustomView;
    Uri uri;
    File file;
    Drawable myDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_view);

        mCustomView = (CustomView)findViewById(R.id.custom_view);
        //mCustomView.setBackground(mCustomView,R.drawable.one);
        //mCustomView.setBackgroundResource();
        Bundle extras = getIntent().getExtras();
        uri = Uri.parse(extras.getString("imageUri"));

       // try {
            //InputStream inputStream = getContentResolver().openInputStream(uri);
            //myDrawable = Drawable.createFromStream(inputStream, uri.toString() );

          //@  String path = uri.getPath();
            //@Drawable d = Drawable.createFromPath(path);

            //d = new ScaleDrawable(d, 0, 50, 50).getDrawable();
            //d.setBounds(0, 0, 50, 50);

       //@ Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
// Scale it to 50 x 50
        //@ d = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(bitmap,50, 50, true));
            mCustomView.setBackgroundResource(uri);
        //} /*catch (FileNotFoundException e) {
           // myDrawable = getResources().getDrawable(R.drawable.one);
      //  }

       /* try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.undo:
                mCustomView.onClickUndo();
                return true;
            case R.id.save:
                saveThisDrawing();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveThisDrawing()
    {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String path = Environment.getExternalStorageDirectory().toString();
            path = path + "/" + getString(R.string.app_name);
            File dir = new File(path);
            //save drawing
            mCustomView.setDrawingCacheEnabled(true);

            //attempt to save
            String imTitle = "Drawing" + "_" + System.currentTimeMillis() + ".png";
            String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(), mCustomView.getDrawingCache(),
                    imTitle, "a drawing");

            try {
                if (!dir.isDirectory() || !dir.exists()) {
                    dir.mkdirs();
                }
                mCustomView.setDrawingCacheEnabled(true);
                File file = new File(dir, imTitle);
                FileOutputStream fOut = new FileOutputStream(file);
                Bitmap bm = mCustomView.getDrawingCache();
                bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Uh Oh!");
                alert.setMessage("Oops! Image could not be saved. Do you have enough space in your device?1");
                alert.setPositiveButton("OK", null);
                alert.show();

            }

            if (imgSaved != null) {
                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            }

            mCustomView.destroyDrawingCache();
        }
    }
    private boolean requestPermission(String writeExternalStorage) {
        if(Build.VERSION.SDK_INT >= 24){

            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
            return true;
        }
        return true;
    }


}
