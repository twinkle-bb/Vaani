package com.twink.vaani.adapter;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.twink.vaani.MainActivity;
import com.twink.vaani.model.ImageModel;
import com.twink.vaani.R;
import com.twink.vaani.repository.ImageRepo;
import com.twink.vaani.upload.UploadImage;
import com.twink.vaani.utils.Constant;
import com.twink.vaani.utils.ThumbnailUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    private final List<ImageModel> images;

    private final Context context;

    private final TextToSpeech textToSpeech;

    private final ImageRepo imageRepo;


    public ImageAdapter(List<ImageModel> images, Context context, ImageRepo imageRepo) {
        this.images = images;
        this.context = context;
        this.imageRepo = imageRepo;

        textToSpeech = new TextToSpeech(this.context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if (i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(new Locale("en", "IN"));
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View imageView = inflater.inflate(R.layout.card_item, parent, false);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        ImageModel  image = images.get(position);
        holder.description.setText(image.getImg_desc());
        Bitmap bitmap = BitmapFactory.decodeByteArray(image.getImage(), 0, image.getImage().length);
        ThumbnailUtils.setThumbnail(bitmap, holder.image, context);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence description = ((TextView) v.findViewById(R.id.id_desc)).getText();
                textToSpeech.speak(description, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
       holder.deleteButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.d("==>","deleting record with Id and description: "+image.getId()+" "+image.getImg_desc());
               images.remove(position);
               imageRepo.deleteImage(image.getId());
               notifyItemRemoved(position);
           }
       });
       holder.updateButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
               // Get the layout inflater.
               LayoutInflater inflater =  LayoutInflater.from(v.getContext());

               View dialogView = inflater.inflate(R.layout.upload_image,null);
               EditText description = dialogView.findViewById(R.id.name);

               builder.setView(dialogView)
                       // Add action buttons
                       .setPositiveButton("update", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int id) {
                                if(!description.getText().toString().isEmpty()){
                                    updateDescription(description.getText().toString(),image,position);
                                }
                           }
                       })
                       .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               dialog.cancel();
                           }


                       }).show();
           }
       });
    }

    private void updateDescription(String description, ImageModel image, int position) {
        Log.d("==>","updating record with Id and description: "+image.getId()+" "+image.getImg_desc());
        imageRepo.update(image.getId(),description);
        image.setImg_desc(description);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void addImage(ImageModel image) {
        images.add(image);
        this.notifyItemInserted(images.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image;

        public TextView description;

        public ImageButton deleteButton;

        public ImageButton updateButton;

        public View card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.id_desc);
            image = (ImageView) itemView.findViewById(R.id.id_image);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
            updateButton = (ImageButton) itemView.findViewById(R.id.update_button);
            card = itemView;
        }
    }
}
