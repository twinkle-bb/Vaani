package com.twink.vaani.repository.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.twink.vaani.model.ImageModel;
import com.twink.vaani.repository.ImageRepo;

import java.util.ArrayList;
import java.util.List;

public class ImageRepoImpl extends SQLiteOpenHelper implements ImageRepo {

    public static final String TABLE_NAME = "Images";

    // Table columns
    public static final String _ID = "_id";
    public static final String IMAGE = "image";
    public static final String DESC = "description";

    // Database Information
    static final String DB_NAME = "Vaani.DB";

    // database version
    static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            " " + DESC + " TEXT NOT NULL, " +
            " " + IMAGE + " BLOB);";

    public ImageRepoImpl(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    @Override
    public long addImage(ImageModel image) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DESC, image.getImg_desc());
        cv.put(IMAGE, image.getImage());
        long key= database.insert( TABLE_NAME, null, cv );
        return key;
    }

    @SuppressLint("Range")
    @Override
    public List<ImageModel> getAllImages() {
        List<ImageModel> images = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery(
                String.format("SELECT * FROM %s", TABLE_NAME), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                ImageModel image = new ImageModel();
                image.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
                image.setImg_desc(cursor.getString(cursor.getColumnIndex(DESC)));
                if(cursor.getBlob(cursor.getColumnIndex(IMAGE)) !=null){
                    image.setImage(cursor.getBlob(cursor.getColumnIndex(IMAGE)));
                    images.add(image);
                }


                cursor.moveToNext();
            }
        }

        cursor.close();
        return images;
    }

    @Override
    public void deleteImage(Long id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, _ID + "=" + id, null);
    }

    public int update(Long _id ,String description) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DESC, description);
        int i = database.update(TABLE_NAME, contentValues, _ID + " = " + _id, null);
        return i;
    }
}
