package com.example.admincollegeapp.faculty;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;
import com.squareup.picasso.Picasso;


import java.util.List;


public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewAdapter> {
    private List<TeacherData> teacherDataList;
    private Context context;
    private String category;

    public TeacherAdapter(List<TeacherData> teacherDataList, Context context,String category) {
        this.teacherDataList = teacherDataList;
        this.context = context;
        this.category=category;
    }

    @Override
    public TeacherViewAdapter onCreateViewHolder( ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.faculty_item_layout,parent,false);

        return new TeacherViewAdapter(view);
    }

    @Override
    public void onBindViewHolder( TeacherAdapter.TeacherViewAdapter holder, int position) {
        TeacherData item = teacherDataList.get(position);
        holder.name.setText(item.getName());
        holder.email.setText(item.getEmail());
        holder.post.setText(item.getPost());

        try {
            if (item.getImage().isEmpty()) {
                holder.imageView.setImageResource(R.drawable.teacher_person);
            } else{
                Picasso.get().load(item.getImage()).into(holder.imageView);
            }
//            Picasso.get().load(item.getImage()).into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.Update.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context,UpdateTeacherActivity.class);
        intent.putExtra("name",item.getName());
        intent.putExtra("email",item.getEmail());
        intent.putExtra("post",item.getPost());
        intent.putExtra("image",item.getImage());
        intent.putExtra("kay",item.getKey());
        intent.putExtra("category",category);
        context.startActivity(intent);


    }
});
    }

    @Override
    public int getItemCount() {
        return teacherDataList.size();
    }

    public  class TeacherViewAdapter extends RecyclerView.ViewHolder {
private TextView name,email,post;
private Button Update;
private ImageView imageView;

        public TeacherViewAdapter( View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.teacherNameId);
            email=itemView.findViewById(R.id.teacherEmailId);
            post=itemView.findViewById(R.id.teacherPostId);
            Update=itemView.findViewById(R.id.teacherUpdateInfoId);
            imageView=itemView.findViewById(R.id.teacherImageId);

        }
    }
}
