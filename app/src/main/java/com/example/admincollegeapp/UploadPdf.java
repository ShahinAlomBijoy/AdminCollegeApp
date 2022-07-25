 package com.example.admincollegeapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;

import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
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
import java.io.File;
import java.util.HashMap;


public class UploadPdf extends AppCompatActivity {
    private static final int REQ = 1;
    private CardView addPdf;
    private Uri pdfData;
    private EditText pdfTitle;
    private TextView pdfTextView;
    private Button uploadPdfBtn;
    private String pdfName,title;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String downloadUrl=" ";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        pdfTextView=findViewById(R.id.pdfTextViewId);
        addPdf=findViewById(R.id.addPdfImageId);
        pdfTitle=findViewById(R.id.pdfTitleId);
        uploadPdfBtn=findViewById(R.id.uploadPdfBtnId);
        pd= new ProgressDialog(this);


        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }


        });
        uploadPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=pdfTitle.getText().toString();
                if (title.isEmpty()){
                    pdfTitle.setError("Empty");
                    pdfTitle.requestFocus();
                }else  if(pdfData==null){
                    Toast.makeText(UploadPdf.this,"upload Pdf Data",Toast.LENGTH_LONG).show();
                }else {
                    uploadPdf();
                }
            }
        });

    }

    private  void uploadPdf() {
        pd.setTitle("Please wait....");
        pd.setMessage("Uploading Pdf.....");
        pd.show();
       StorageReference reference= storageReference.child("pdf/"+pdfName+"-"+System.currentTimeMillis()+".pdf");
         reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                  while (uriTask.isComplete()){
                      Uri uri=uriTask.getResult();
                      uploadData(String.valueOf(uri));
                  }
                  pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure( Exception e) {
            pd.dismiss();
Toast.makeText(UploadPdf.this,"Something went wrong",Toast.LENGTH_LONG).show();
        }
    });



    }

    private  void uploadData(String  downloadUrl) {
        String uniqueKey=databaseReference.child("pdf").push().getKey();
        HashMap data = new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfUrl",downloadUrl);
        databaseReference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( Task<Void> task) {
                pd.dismiss();
        Toast.makeText(UploadPdf.this,"Pdf upload successfully",Toast.LENGTH_LONG).show();
         pdfTitle.setText(" ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {
                pd.dismiss();
                  Toast.makeText(UploadPdf.this,"Failed upload pdf",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void OpenGallery() {
 Intent intent= new Intent();
 intent.setType("pdf/docs/ppt");
 intent.setAction(Intent.ACTION_GET_CONTENT);
 startActivityForResult(Intent.createChooser(intent,"Select Pdf File"),REQ);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ && resultCode==RESULT_OK){
          pdfData=data.getData();
            if(pdfData.toString().startsWith("content://")){
                Cursor cursor =null;
                try {
                    cursor=  UploadPdf.this.getContentResolver().query(pdfData,null,null,null,null,null);
                    if (cursor!=null&&cursor.moveToFirst()){
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(pdfData.toString().startsWith("fill://")){
                pdfName = new File(pdfData.toString()).getName();
            }
            pdfTextView.setText(pdfName);

        }
    }
}