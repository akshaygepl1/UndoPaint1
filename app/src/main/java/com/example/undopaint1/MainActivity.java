package com.example.undopaint1;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    //DrawView img;
    private ImageView img;
    private final int CODE_IMG_GALLERY = 1;
    public static final int CAMERA_STORAGE_REQUEST_CODE = 611;
    private  final String SAMPLE_CROPPER_IMG_NAME = "sampleCropImg";
    Intent CamIntent;
    File file;
    Uri uri;
    Button btncamera, btndraw;
    private String currentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if(Build.VERSION.SDK_INT >= 23){

            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }




        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CameraOpen();

            }
        });

    }

    private void init() {

        //this.img = findViewById(R.id.imageView);
        this.btncamera = findViewById(R.id.btncamera);
        //this.btndraw = findViewById(R.id.btndraw);

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK)
        {

            Uri uri = Uri.parse(currentPhotoPath);
            startCrop(uri, uri);



        }else  if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK)
        {
            Uri imageUriResultCrop = UCrop.getOutput(data);
            File uriToFile;
            if (imageUriResultCrop != null){

                Intent drawintent = new Intent(MainActivity.this,DrawView.class);
                drawintent.putExtra("imageUri", imageUriResultCrop.toString());
                drawintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                drawintent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(drawintent);

            }
        }

    }

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        System.out.println(storageDir.getAbsolutePath());
        if (storageDir.exists())
            System.out.println("File exists");
        else
            System.out.println("File not exists");
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    private void startCrop(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorAccent));
        options.setCompressionQuality(100);
        options.setMaxBitmapSize(10000);
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(2f, 3f)
                .start(this);
    }

    private void CameraOpen() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file;
        try {
            file = getImageFile(); // 1
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            uri = Uri.fromFile(file); // 3
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
        startActivityForResult(pictureIntent, CODE_IMG_GALLERY);
    }

}
