package com.example.line_assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private final String TAG = "DatabaseHelperClass";
    private static int databaseVersion = 1;
    public String databaseName;
    private static final String TABLE_IMAGE = "ImageTable";

    // Image Table Columns names
    private static final String COL_ID = "col_id";
    private static final String IMAGE_ID = "image_id";
    private static final String IMAGE_BITMAP = "image_bitmap";

    public DatabaseHelper(Context context,String convertdbname) {
        super(context, convertdbname, null, databaseVersion);
        databaseName = convertdbname;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
                + COL_ID + " INTEGER PRIMARY KEY ,"
                + IMAGE_ID + " TEXT,"
                + IMAGE_BITMAP + " TEXT )";
        sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        onCreate(sqLiteDatabase);
    }

    /*public void insetImage(Drawable dbDrawable, String imageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_ID, imageId);
        BitmapData bitmap = ((BitmapDrawable)dbDrawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(BitmapData.CompressFormat.PNG, 100, stream);
        values.put(IMAGE_BITMAP, stream.toByteArray());
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }*/

    public void insetImage(Bitmap bitmap, String imageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_ID, imageId);
        //BitmapData bitmap = ((BitmapDrawable)dbDrawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        values.put(IMAGE_BITMAP, stream.toByteArray());
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    public List<ImageHelper> getImage(String imageId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor2 = db.query(TABLE_IMAGE,
                new String[] {COL_ID, IMAGE_ID, IMAGE_BITMAP},IMAGE_ID
                        +" LIKE '"+imageId+"%'", null, null, null, null);
        List<ImageHelper> imageHelpers = new ArrayList();

        if (cursor2.moveToFirst()) {
            int i = -1;
            do {
                imageHelpers.add(new ImageHelper(cursor2.getString(1),cursor2.getBlob(2)));
            } while (cursor2.moveToNext());
        }

        cursor2.close();
        db.close();
        return imageHelpers;
    }

    public ImageHelper getThumnail(String imageId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor2 = db.query(TABLE_IMAGE,
                new String[] {COL_ID, IMAGE_ID, IMAGE_BITMAP},IMAGE_ID
                        +" LIKE '"+imageId+"%'", null, null, null, null);
        ImageHelper imageHelper = new ImageHelper();

        if (cursor2.moveToFirst()) {
            imageHelper.setImageId(cursor2.getString(1));
            imageHelper.setImageByteArray(cursor2.getBlob(2));
        }
        cursor2.close();
        db.close();
        return imageHelper;
    }



}
