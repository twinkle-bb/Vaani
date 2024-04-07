package com.twink.vaani.upload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.twink.vaani.MainActivity;
import com.twink.vaani.R;
import com.twink.vaani.adapter.ImageAdapter;
import com.twink.vaani.model.ImageModel;
import com.twink.vaani.repository.ImageRepo;
import com.twink.vaani.utils.Constant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UploadImage extends DialogFragment {


    ImageModel image = new ImageModel();

    EditText description;

    private ImageRepo imageRepo;
    private ImageAdapter imageAdaptor;

    public static String TAG = "UploadFragment";


    public UploadImage(ImageRepo imageRepo, ImageAdapter imageAdaptor) {
        this.imageRepo = imageRepo;
        this.imageAdaptor = imageAdaptor;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final byte[] imageData = getArguments() != null ?
                getArguments().getByteArray(Constant.UPLOAD_IMAGE_PATH) : null;


        //image.setImage(imageData);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Log.i(TAG, "execution started");
            Bitmap scaledImage = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageData,
                    0, imageData.length), 400, 400, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            scaledImage.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            scaledImage.recycle();
            image.setImage(stream.toByteArray());
            Log.i(TAG, "successfully compressed image");
        });

        executor.shutdown();
        try {
            Log.i(TAG, "waiting for image compression to be completed");
            executor.awaitTermination(100, TimeUnit.SECONDS);
            Log.i(TAG, "image compression completed");
        } catch (InterruptedException e) {
            Log.e(TAG, "error occurred while compressing image", e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater.
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.upload_image, null);

        description = dialogView.findViewById(R.id.name);
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }


                });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Do stuff, possibly set wantToCloseDialog to true then...
                    if (description.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), Constant.ADD_DESCRIPTION, Toast.LENGTH_SHORT).show();
                    } else {
                        String imageDesc = description.getText().toString();
                        image.setImg_desc(imageDesc);
                        long id = imageRepo.addImage(image);
                        image.setId(id);
                        imageAdaptor.addImage(image);
                        d.cancel();
                    }
                }

            });
        }
    }


}
