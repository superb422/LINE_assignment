package com.example.line_assignment;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.line_assignment.databinding.ActivityEditBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EditActivity extends AppCompatActivity {
    ActivityEditBinding binding;
    public static final String IMAGE_ID = "IMG_ID";
    public String Mode = "NOT edit";
    Intent getintent;
    DatabaseHelper databaseHelper;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;
    Uri photoUri;
    List<BitmapData> bitmapData,empty;
    Drawable dbDrawable;
    Handler handler = new Handler();  // 외부쓰레드 에서 메인 UI화면을 그릴때 사용



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit);
        binding.setEditactivity(this);

        checkPermissions();

        bitmapData = new ArrayList();

        getintent = getIntent();

        if((getintent.getExtras().getString("Option")).equals("Detail")){ // 상세 보기
            binding.memoTitleTxt.setText("메모 내용");
            binding.editMemoTitle.setVisibility(View.GONE);
            binding.editMemoContent.setVisibility(View.GONE);
            binding.viewMemoTitle.setVisibility(View.VISIBLE);
            binding.viewMemoContent.setVisibility(View.VISIBLE);
            binding.memoComplete.setVisibility(View.GONE);
            binding.memoDelete.setVisibility(View.VISIBLE);
            binding.memoModify.setVisibility(View.VISIBLE);
            binding.viewMemoTitle.setText(getintent.getExtras().getString("title"));
            binding.viewMemoContent.setText(getintent.getExtras().getString("content"));
            binding.editMemoTitle.setText(getintent.getExtras().getString("title"));
            binding.editMemoContent.setText(getintent.getExtras().getString("content"));

            DatabaseHelper databaseHelper = new DatabaseHelper(this,getintent.getExtras().getString("fileName"));
            List<ImageHelper> imageHelper = new ArrayList();
            imageHelper = databaseHelper.getImage(IMAGE_ID);
            for(int i=0;i<imageHelper.size();i++) {
                byte[] bytes = imageHelper.get(i).getImageByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmapData.add(new BitmapData(bitmap));
            }
            setRecyclerView(bitmapData);
        } else
            EmptyThumbnail();


    }
    public void EmptyThumbnail(){
        empty = new ArrayList();
        dbDrawable = getResources().getDrawable(R.drawable.insert_icon);
        Bitmap bitmap = ((BitmapDrawable)dbDrawable).getBitmap();
        empty.add(new BitmapData(bitmap));
        setRecyclerView(empty);
    }

    public void onImageLoad(int position){
        ImageDialog imageDialog = new ImageDialog(this);
        imageDialog.ImageDialogListener(new ImageDialog.ImageDialogListener() {
            @Override
            public void onDeleteClicked() {
                if(position==bitmapData.size()-1) {
                    if(bitmapData.size() == 0)
                        Toast.makeText(getApplicationContext(), "사진을 첨부해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    if((bitmapData.size() ==0)){
                        Toast.makeText(getApplicationContext(), "사진을 첨부해주세요", Toast.LENGTH_SHORT).show();
                    }else {
                        bitmapData.remove(position);
                        setRecyclerView(bitmapData);
                    }
                }
            }

            @Override
            public void onPhotoClicked() {
                takePhoto();
            }

            @Override
            public void onAlbumClicked() {
                goToAlbum();
            }

            @Override
            public void ExternalClicked(String insert_url) {
                if(!insert_url.isEmpty()){
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try{
                                URL url = new URL(insert_url);
                                InputStream is = url.openStream();
                                Bitmap bm = BitmapFactory.decodeStream(is);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"이미지 업로드 성공",Toast.LENGTH_SHORT).show();
                                        Bitmap empty_picture = ((BitmapDrawable)dbDrawable).getBitmap();
                                        if(!bitmapData.isEmpty())
                                            bitmapData.remove(bitmapData.size()-1);
                                        bitmapData.add(new BitmapData(bm));
                                        bitmapData.add(new BitmapData(empty_picture));
                                        setRecyclerView(bitmapData);
                                    }
                                });
                            } catch(Exception e){

                            }

                        }
                    });
                    t.start();
                }
            }
        });
        imageDialog.show();
    }

    public void onModify(View view){
        Mode = "Edit";

        binding.memoTitleTxt.setText("메모 작성");
        binding.memoComplete.setVisibility(View.VISIBLE);
        binding.editMemoTitle.setVisibility(View.VISIBLE);
        binding.editMemoContent.setVisibility(View.VISIBLE);
        binding.viewMemoTitle.setVisibility(View.GONE);
        binding.viewMemoContent.setVisibility(View.GONE);

        binding.memoDelete.setVisibility(View.GONE);
        binding.memoModify.setVisibility(View.GONE);

        dbDrawable = getResources().getDrawable(R.drawable.insert_icon);
        Bitmap empty_picture = ((BitmapDrawable)dbDrawable).getBitmap();
        bitmapData.add(new BitmapData(empty_picture));
        setRecyclerView(bitmapData);
    }

    public void onDelete(View view){
        // 해당 파일의 이름을 알아야함
        boolean deleteFile = deleteFile(getintent.getExtras().getString("fileName"));

        if(deleteFile){
            Intent intent = new Intent(EditActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(getApplicationContext(), "delete 실패", Toast.LENGTH_LONG).show();
        }

    }

    public void onComplete(View view){

        if((getintent.getExtras().getString("Option")).equals("Detail"))
            onDelete(getCurrentFocus());
        String title = binding.editMemoTitle.getText().toString()+"\n";
        String content = binding.editMemoContent.getText().toString();
        if(title.isEmpty() || content.isEmpty()) {
            Toast.makeText(getApplicationContext(), "내용을 채워주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if((bitmapData.isEmpty())||(bitmapData.size()<2)) {
            Toast.makeText(getApplicationContext(), "사진을 넣어주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        long now = System.currentTimeMillis(); // 국제적인 표준 시각 UTC 인 1970년 1월 1일 자정부터 현재까지 카운트된 시간을 ms(milliseconds) 단위로 표시

        DatabaseHelper databaseHelper = new DatabaseHelper(this,String.valueOf(now)); // 비트맵 id을 현재 시간으로 저장
        for(int i=0;i<bitmapData.size()-1;i++){
            databaseHelper.insetImage(bitmapData.get(i).bitmap,IMAGE_ID);
        }


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

        Intent intent = new Intent(EditActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onBackbtn(View view){
        onBackPressed();
    }

    /* --------------------------------- 사진 기능 메소드---------------------------------------- */
    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void takePhoto() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = FileProvider.getUriForFile(this,
                    "com.example.line_assignment.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "nostest_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) { // 앨범에서 가져오기
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) { // 사진 찍기
            cropImage();
            // 갤러리에 나타나게
            MediaScannerConnection.scanFile(this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            try { //저는 bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    Bitmap empty_picture = ((BitmapDrawable)dbDrawable).getBitmap();
                    if(!bitmapData.isEmpty())
                        bitmapData.remove(bitmapData.size()-1);
                    bitmapData.add(new BitmapData(bitmap));
                    bitmapData.add(new BitmapData(empty_picture));
                    setRecyclerView(bitmapData);

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }
    }

    public void cropImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File folder = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());
            photoUri = FileProvider.getUriForFile(EditActivity.this,
                    "com.example.line_assignment.provider", tempFile);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            grantUriPermission(res.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }
    public void setRecyclerView(List<BitmapData> bitmapData){
        binding.imageRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.imageRecycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));

        BitmapRecyclerAdapter adapter = new BitmapRecyclerAdapter(bitmapData,this);
        binding.imageRecycler.setAdapter(adapter);
        adapter.onBind = (viewHolder, position) -> {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        if((Mode.equals("Edit")) || (getintent.getExtras().getString("Option")).equals("Write"))
                            onImageLoad(position);

                }
            });
        };
    }
}
