package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class allComplainAdapter  extends RecyclerView.Adapter<allComplainAdapter.ViewHolder>{
    private Context mContext;
    private List<Complain> mUsers;
    String theLastMessage;
    public allComplainAdapter(Context mContext,List<Complain> mUsers)
    {
        this.mUsers = mUsers;
        this.mContext =mContext;


    }

    @NonNull
    @Override
    public allComplainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.displaycomplain,parent,false);


        return new allComplainAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final allComplainAdapter.ViewHolder holder, int position) {

        final Complain complain = mUsers.get(position);
        holder.complain_text.setText(complain.getContent());
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference refpost = FirebaseDatabase.getInstance().getReference("Post");
        refpost.child(complain.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userid = dataSnapshot.child("uid").getValue().toString();
                reference.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String user_name = dataSnapshot.child("username").getValue().toString();
                        String img = dataSnapshot.child("ImageUrl").getValue().toString();
                        holder.username.setText(user_name);
                        Glide.with(mContext).load(img).into(holder.profile_img);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child(complain.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String user_name = dataSnapshot.child("username").getValue().toString();
                holder.complain_username.setText(user_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,InfoActivity.class);
                intent.putExtra("userid",user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class  ViewHolder extends  RecyclerView.ViewHolder{

        public TextView username,complain_text,complain_username;

        public ImageView profile_img;



        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.complain_post_username);
            complain_text = itemView.findViewById(R.id.complain_text);
            complain_username = itemView.findViewById(R.id.complain_username);
            profile_img = itemView.findViewById(R.id.complain_profile_image);




        }
    }
}
