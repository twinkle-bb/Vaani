package com.twink.vaani;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twink.vaani.adapter.ImageAdapter;
import com.twink.vaani.model.ImageModel;
import com.twink.vaani.repository.ImageRepo;
import com.twink.vaani.repository.impl.ImageRepoImpl;
import com.twink.vaani.upload.UploadImage;
import com.twink.vaani.utils.Constant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GridView imageGrid;

    private static ImageRepo imageRepo;
    private static ImageAdapter imageAdapter;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1000;

    private static final int REQUEST_PIC = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkAndRequestPermissions();
        imageRepo = new ImageRepoImpl(getApplicationContext());
        List<ImageModel> images = imageRepo.getAllImages();
        imageAdapter = new ImageAdapter(images, getApplicationContext(), imageRepo);

        RecyclerView rcvImages = (RecyclerView) findViewById(R.id.main_view);
        rcvImages.setAdapter(imageAdapter);
        rcvImages.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.addButton){
            uploadImage();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }


    private void uploadImage( ) {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Initialize Gallery Intent
        Intent pickPhoto = new Intent( Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Choose between Camera and Gallery
        Intent chooser = Intent.createChooser(pickPhoto, "Select from:");

        Intent[] intentArray = { takePicture };
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);


        startActivityForResult(chooser, REQUEST_PIC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;

        if (requestCode == REQUEST_PIC) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else if (data.getExtras() != null) {
                    bm = (Bitmap) data.getExtras().get("data");
                }

            } else {
                  Log.d("==>","Operation canceled!");
                Toast.makeText(this, "Please Select Image again", Toast.LENGTH_SHORT).show();
            }

            if (bm != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Bundle bundle = new Bundle();
                bundle.putByteArray(Constant.UPLOAD_IMAGE_PATH, byteArray);

                UploadImage uploadFragment = new UploadImage(imageRepo,imageAdapter);
                uploadFragment.setArguments(bundle);
                uploadFragment.show(getSupportFragmentManager(), Constant.TAG);
            }
        }

    }



    // Handled permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                                    "Vaani Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Vaani Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.this.finish();
                }
                break;
        }
    }

    public  boolean checkAndRequestPermissions() {
        int WExtstorePermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}