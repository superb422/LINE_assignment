package com.example.line_assignment;

import android.graphics.Bitmap;

public class MemoData {
    public final String title,content,filename;
    public final Bitmap bitmap;

    public MemoData(String title, String content, String filename, Bitmap bitmap) {
        this.title = title;
        this.content = content;
        this.filename = filename;
        this.bitmap = bitmap;
    }
}
