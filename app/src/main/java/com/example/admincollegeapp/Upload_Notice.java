package com.example.admincollegeapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Upload_Notice extends AppCompatActivity {
    private static final int REQ = 1;
    private CardView addImage;
    private Bitmap bitmap;
    private ImageView imageView;
    private EditText NoticeTitle;
    private Button uploadNoticeBtn;
    DatabaseReference databaseReference,dbRef;
    StorageReference storageReference;
    String downLoadUrl;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);
        addImage=findViewById(R.id.addNoticeImageId);
        imageView=findViewById(R.id.noticeImageViewId);
        NoticeTitle=findViewById(R.id.noticeTitleId);
        uploadNoticeBtn=findViewById(R.id.uploadNoticeBtnId);
        pd= new ProgressDialog(this);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }


        });
        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NoticeTitle.getText().toString().isEmpty()){
                    NoticeTitle.setError("Empty");
                    NoticeTitle.requestFocus();
                }else if(bitmap==null){
                       UploadData();
                }else {
                   uploadImage();
                }
            }
        });
    }

    private void uploadImage() {
        pd.setMessage("Uploading....");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimg =baos.toByteArray();
        final StorageReference fillepath;
        fillepath=storageReference.child("NoticeData").child(finalimg+"jpg");
        final UploadTask uploadTask =fillepath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(Upload_Notice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete( Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                  uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     fillepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             downLoadUrl=String.valueOf(uri);
                             UploadData();
                         }
                     });
                      }
                  });
                }else {
                    pd.dismiss();
                    Toast.makeText(Upload_Notice.this,"Something went wrong",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void UploadData() {
        dbRef=databaseReference.child("NoticeData");
final String key=dbRef.push().getKey();
String title =NoticeTitle.getText().toString();
        Calendar calForData = Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("dd--mm--yy");
        String date =currentDate.format(calForData.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime= new SimpleDateFormat("hh:mm a");
        String Time=currentTime.format(calForTime.getTime());

        NoticeData noticeData=new NoticeData(title,downLoadUrl,date,Time,key);



        dbRef.child(key).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
Toast.makeText(Upload_Notice.this,"Notice Upload",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure( Exception e) {
                pd.dismiss();
Toast.makeText(Upload_Notice.this,"Something went wrong",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void OpenGallery() {
        Intent pickImage = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,REQ);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ && resultCode==RESULT_OK){
            Uri uri=data.getData();
            try {
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
        }
    }
}