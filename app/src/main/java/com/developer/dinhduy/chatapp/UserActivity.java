package com.developer.dinhduy.chatapp;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserActivity extends AppCompatActivity {
private Toolbar toolbar;
private RecyclerView listCycle;
private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        toolbar=(Toolbar) findViewById(R.id.bar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listCycle =(RecyclerView ) findViewById(R.id.Recycleview_user);
        listCycle.setHasFixedSize(true);
        listCycle.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        startListening();
    }
    public void startListening(){
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToFirst(50);
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_user, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UserViewHolder holder, int position, final User user) {
                // Bind the Chat object to the ChatHolder
                holder.setName(user.getName());
                holder.setAbout(user.getAbout());
                holder.setThumbPicture(user.getThumbPicture());
                Log.d("AAA", "onBindViewHolder: "+user.getThumbPicture());

                // get key để  khi click vào ai đó sẽ hiển thị profile

                final String keyid= getRef(position).getKey();
                // bắt sự kiện khi người dùng click vào 1 profile bất kỳ
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =new Intent(UserActivity.this,ProfileActivity.class);

                        intent.putExtra("profile_id",keyid);

                        startActivity(intent);



                    }
                });
                // ...
            }

        };
        listCycle.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
       public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.id_name);
            userNameView.setText(name);
        }
        public void setAbout(String about){
            TextView Userstatus = (TextView) mView.findViewById(R.id.id_statu);
            Userstatus.setText(about);
        }

        public void setThumbPicture(String thumbPicture) {
            CircleImageView cycle=(CircleImageView) mView.findViewById(R.id.cycle_imageview);
            Picasso.get()

                    .load(thumbPicture)
                    .into(cycle);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
