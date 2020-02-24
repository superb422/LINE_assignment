package com.example.line_assignment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;


import com.example.line_assignment.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    DatabaseHelper databaseHelper;
    public static final String IMAGE_ID = "IMG_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        ListInsert();
    }


    public void ListInsert(){
        List<MemoData> memodata = new ArrayList();

        File file=new File("data/data/com.example.line_assignment/files/");
        FileInputStream fis = null;

        if(file.exists()) {
            try {
                File[] fileList = file.listFiles();
                String[] fileNames = new String[fileList.length];
                for (int i = 0; i < fileNames.length; i++) {
                    String title = null;
                    String content = null;
                    StringBuffer buffer = new StringBuffer();
                    fis = openFileInput(fileList[i].getName());
                    BufferedReader iReader = new BufferedReader(new InputStreamReader((fis)));

                    title = iReader.readLine();
                    content = iReader.readLine();

                    while (content != null) {
                        buffer.append(content + "\n");
                        content = iReader.readLine();
                    }

                    buffer.append("\n");
                    iReader.close();

                    databaseHelper = new DatabaseHelper(this,fileList[i].getName());
                    ImageHelper imageHelper = new ImageHelper();
                    imageHelper = databaseHelper.getThumnail(IMAGE_ID);
                    byte[] bytes = imageHelper.getImageByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    memodata.add(new MemoData(title, buffer.toString(), fileList[i].getName(),bitmap));
                }
                setRecyclerView(memodata);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void onMemoWrite(View view){
        Intent intent = new Intent(MainActivity.this,EditActivity.class);
        intent.putExtra("Option","Write");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void setRecyclerView(List<MemoData> memos){
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.mainRecyclerview.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        RecyclerAdapter adapter = new RecyclerAdapter(memos,this);
        binding.mainRecyclerview.setAdapter(adapter);
    }

}
