package com.example.admincollegeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.admincollegeapp.faculty.UpdateFaculty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView uploadNotice,uploadImage,uploadPdf,upDateFaculty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadNotice=findViewById(R.id.addNoticeId);
        uploadImage=findViewById(R.id.addImageId);
        uploadPdf=findViewById(R.id.addEbookId);
        upDateFaculty=findViewById(R.id.addFacultyId);



        uploadNotice.setOnClickListener(this);
        uploadImage.setOnClickListener(this);
        uploadPdf.setOnClickListener(this);
        upDateFaculty.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent;
switch (v.getId()){
    case R.id.addNoticeId:
         intent =new Intent(MainActivity.this,Upload_Notice.class);
        startActivity(intent);
        break;
    case R.id.addImageId:
        intent= new Intent(MainActivity.this,Upload_Image.class);
        startActivity(intent);
        break;
    case R.id.addEbookId:
        intent= new Intent(MainActivity.this,UploadPdf.class);
        startActivity(intent);
        break;
    case R.id.addFacultyId:
        intent= new Intent(MainActivity.this, UpdateFaculty.class);
        startActivity(intent);
        break;
}
    }
}