package com.example.admincollegeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class Upload_Image extends AppCompatActivity {
    private static final int REQ =1 ;
    private Spinner ImageCategory;
    private CardView ImageSelect;
    private Button ImageUploadBtn;
    private ImageView GalleryImage;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
String category;
    Bitmap bitmap;
    String downLoadUrl;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        ImageCategory=findViewById(R.id.imageCategory);
        ImageSelect=findViewById(R.id.addGalleryId);
        ImageUploadBtn=findViewById(R.id.uploadImageBtnId);
        GalleryImage=findViewById(R.id.galleryImageViewId);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        pd= new ProgressDialog(this);

        String[] items= new String[]{"Select Category","Convocation","Independence","Other Events"};
        ImageCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));




      ImageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              category=ImageCategory.getSelectedItem().toString();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {

          }
      });


      ImageSelect.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              openGallery();
          }
      });
      ImageUploadBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (bitmap==null){
                  Toast.makeText(Upload_Image.this,"Please upolad image",Toast.LENGTH_LONG).show();
              }else if (category.equals("Select Category")){
            Toast.makeText(Upload_Image.this,"Please Select Image Category",Toast.LENGTH_LONG).show();
              }else {
pd.setMessage("Uploading....");
pd.show();
  uploadImage();
              }
          }
      });

    }

    private void uploadImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimg =baos.toByteArray();
        final StorageReference fillepath;
        fillepath=storageReference.child(finalimg+"jpg");
        final UploadTask uploadTask =fillepath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(Upload_Image.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                    Toast.makeText(Upload_Image.this,"Something went wrong",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void UploadData() {
        databaseReference=databaseReference.child("gallery").child(category);
        final String uniqueKey=databaseReference.push().getKey();
        databaseReference.child(uniqueKey).setValue(downLoadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
Toast.makeText(Upload_Image.this,"Image uploaded Successfully ",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {
                pd.dismiss();
                Toast.makeText(Upload_Image.this,"Something went wrong",Toast.LENGTH_LONG).show();
            }
        });


    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,REQ);
    }
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ && resultCode==RESULT_OK){
            Uri uri=data.getData();

            try {
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            GalleryImage.setImageBitmap(bitmap);
        }
    }
}