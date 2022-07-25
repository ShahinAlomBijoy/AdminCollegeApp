package com.example.admincollegeapp.faculty;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admincollegeapp.NoticeData;
import com.example.admincollegeapp.R;
import com.example.admincollegeapp.Upload_Notice;
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

public class AddTeacher extends AppCompatActivity {
    private static final int REQ = 1;
    private ImageView addTeacherImageView;
   private EditText addTeacherName,addTeacherEmail,addTeacherPost;
   private Spinner addTeacherCategory;
private    Button  addTeacherBtn;
private Bitmap bitmap=null;
private  String category;
private String name,email,post,downloadUrl=" ";
private ProgressDialog pd;
private DatabaseReference databaseReference,dbRef;
private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        addTeacherImageView=findViewById(R.id.addTeacherImageId);
        addTeacherName=findViewById(R.id.addTeacherNameId);
        addTeacherEmail=findViewById(R.id.addTeacherEmailId);
        addTeacherPost=findViewById(R.id.addTeacherPostId);
        addTeacherCategory=findViewById(R.id.teacherCategoryId);
        addTeacherBtn=findViewById(R.id.addTeacherBtnId);
        pd= new ProgressDialog(this);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference= FirebaseStorage.getInstance().getReference();
        String[] items= new String[]{"Select Category","Computer Science","BBA","HSC","Mathematics","Other Events"};
        addTeacherCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));




        addTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=addTeacherCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        addTeacherImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });



     addTeacherBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        checkValidation();
    }
});

    }

    private void checkValidation() {
        name = addTeacherName.getText().toString();
        email = addTeacherEmail.getText().toString();
        post = addTeacherPost.getText().toString();
        if (name.isEmpty()) {
            addTeacherName.setError("Empty");
            addTeacherName.requestFocus();
        } else if (email.isEmpty()) {
            addTeacherEmail.requestFocus();
            addTeacherEmail.setError("Empty");

        } else if (post.isEmpty()) {
            addTeacherPost.setError("Empty");
        } else if (category.equals("Select Category")) {
            Toast.makeText(AddTeacher.this, "Please provide teacher Category", Toast.LENGTH_LONG);

        } else if (bitmap==null) {
            InsertData();

        }else {
            uploadImage();
        }
    

    }

    private void uploadImage() {
        pd.setMessage("Uploading....");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimg =baos.toByteArray();
        final StorageReference fillepath;
        fillepath=storageReference.child("teacher").child(finalimg+"jpg");
        final UploadTask uploadTask =fillepath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(AddTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete( Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fillepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);
                                    InsertData();
                                }
                            });
                        }
                    });
                }else {
                    pd.dismiss();
                    Toast.makeText(AddTeacher.this,"Something went wrong",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void InsertData() {
        dbRef=databaseReference.child(category);
        final String key=dbRef.push().getKey();

        TeacherData teacherData=new TeacherData(name,email,post,downloadUrl,key);



        dbRef.child(key).setValue(teacherData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this,"Teacher Add",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure( Exception e) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this,"Something went wrong",Toast.LENGTH_LONG).show();

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
            addTeacherImageView.setImageBitmap(bitmap);
        }
    }
}