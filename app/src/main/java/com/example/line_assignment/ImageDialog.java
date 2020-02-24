package com.example.line_assignment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ImageDialog extends Dialog implements View.OnClickListener{

    public ImageDialogListener imageDialogListener;

    public ImageDialog(Context context) {
        super(context);
    }

    public interface ImageDialogListener{
        void onDeleteClicked();
        void onPhotoClicked();
        void onAlbumClicked();
        void ExternalClicked(String Url);
    }

    //호출할 리스너 초기화
    public void ImageDialogListener(ImageDialogListener imageDialogListener){
        this.imageDialogListener = imageDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);   //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.

        setContentView(R.layout.image_dialog);

        findViewById(R.id.dialog_delete).setOnClickListener(this);
        findViewById(R.id.dialog_photo).setOnClickListener(this);
        findViewById(R.id.dialog_album).setOnClickListener(this);
        findViewById(R.id.dialog_external).setOnClickListener(this);
        findViewById(R.id.dialog_cancel).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_delete: //확인 버튼을 눌렀을 때
                imageDialogListener.onDeleteClicked();
                dismiss();
                break;
            case R.id.dialog_photo: //취소 버튼을 눌렀을 때
                imageDialogListener.onPhotoClicked();
                dismiss();
                break;
            case R.id.dialog_album:
                imageDialogListener.onAlbumClicked();
                dismiss();
                break;
            case R.id.dialog_external:
                EditText editText = findViewById(R.id.insert_url);
                imageDialogListener.ExternalClicked(editText.getText().toString());
                dismiss();
                break;
            case R.id.dialog_cancel:
                dismiss();
                break;
        }
    }
}
