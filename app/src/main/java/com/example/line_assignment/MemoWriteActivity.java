package com.example.line_assignment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.line_assignment.databinding.ActivityMemoWriteBinding;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MemoWriteActivity extends AppCompatActivity {
    ActivityMemoWriteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_memo_write);
        binding.setMemoWriteactivity(this);


    }

    public void onComplete(View view){
        String title = binding.memoTitle.getText().toString()+"\n";
        String content = binding.memoContent.getText().toString();
        long now = System.currentTimeMillis(); // 국제적인 표준 시각 UTC 인 1970년 1월 1일 자정부터 현재까지 카운트된 시간을 ms(milliseconds) 단위로 표시

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(String.valueOf(now), Context.MODE_PRIVATE); // /data/data/[project명]/[now 변수].txt
            fos.write(title.getBytes());
            fos.write(content.getBytes());
            fos.close();;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(MemoWriteActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onBackbtn(View view){
        onBackPressed();
    }

}
