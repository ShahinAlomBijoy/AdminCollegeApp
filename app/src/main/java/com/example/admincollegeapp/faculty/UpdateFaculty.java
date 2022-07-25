package com.example.admincollegeapp.faculty;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.admincollegeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateFaculty extends AppCompatActivity {
    FloatingActionButton fab;
    private RecyclerView cseDepartment,bbaDepartment,hscDepartment;
    private LinearLayout csNoData,bbaNoData,hscNoData;
    private List<TeacherData> list1,list2,list3;
    private DatabaseReference databaseReference,dbRef;
    private TeacherAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);
        fab=findViewById(R.id.facId);
        cseDepartment=findViewById(R.id.csDepartment);
        bbaDepartment=findViewById(R.id.bbaDepartment);
        hscDepartment=findViewById(R.id.hscDepartment);

        csNoData=findViewById(R.id.csNoDataId);
        bbaNoData=findViewById(R.id.bbaDataId);
        hscNoData=findViewById(R.id.bbaDataId);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("teacher");


         cseDepartment();
         bbaDepartment();
         hscDepartment();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateFaculty.this,AddTeacher.class));
            }
        });
    }

    private void cseDepartment() {
        dbRef=databaseReference.child("Computer Science");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                list1= new ArrayList<>();
                if(!snapshot.exists()){
                         csNoData.setVisibility(View.VISIBLE);
                         cseDepartment.setVisibility(View.GONE);
                }else {



                    csNoData.setVisibility(View.GONE);
                    cseDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        TeacherData data =snapshot1.getValue(TeacherData.class);
                         list1.add(data);

                    }
                    cseDepartment.setHasFixedSize(true);
                    cseDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));


                    Adapter= new TeacherAdapter(list1,UpdateFaculty.this,"Computer Science");
                    cseDepartment.setAdapter(Adapter);




                }
            }

            @Override
            public void onCancelled( DatabaseError error) {
                Toast.makeText(UpdateFaculty.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bbaDepartment() {
        dbRef=databaseReference.child("BBA");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                list2= new ArrayList<>();
                if(!snapshot.exists()){
                    bbaNoData.setVisibility(View.VISIBLE);
                    bbaDepartment.setVisibility(View.GONE);
                }else {



                    bbaNoData.setVisibility(View.GONE);
                    bbaDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        TeacherData data =snapshot1.getValue(TeacherData.class);
                        list2.add(data);

                    }
                    bbaDepartment.setHasFixedSize(true);
                    bbaDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));


                    Adapter= new TeacherAdapter(list2,UpdateFaculty.this,"BBA");
                    bbaDepartment.setAdapter(Adapter);




                }
            }

            @Override
            public void onCancelled( DatabaseError error) {
                Toast.makeText(UpdateFaculty.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void hscDepartment() {
        dbRef=databaseReference.child("HSC");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                list3= new ArrayList<>();
                if(!snapshot.exists()){
                    hscNoData.setVisibility(View.VISIBLE);
                    hscDepartment.setVisibility(View.GONE);
                }else {



                    hscNoData.setVisibility(View.GONE);
                    hscDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        TeacherData data =snapshot1.getValue(TeacherData.class);
                        list3.add(data);

                    }
                    hscDepartment.setHasFixedSize(true);
                    hscDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));


                    Adapter= new TeacherAdapter(list3,UpdateFaculty.this,"HSC");
                    hscDepartment.setAdapter(Adapter);




                }
            }

            @Override
            public void onCancelled( DatabaseError error) {
                Toast.makeText(UpdateFaculty.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}