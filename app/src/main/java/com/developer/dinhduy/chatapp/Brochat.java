package com.developer.dinhduy.chatapp;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import io.reactivex.annotations.NonNull;

public class Brochat extends Application {

    private DatabaseReference mUserdatabse;
    private FirebaseAuth mAuth;


    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth=FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            //Go to login
            FirebaseAuth.getInstance().signOut();
            Intent loginIT= new Intent(getApplicationContext(),StartActivity.class);
            loginIT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIT);
        }else {
            mUserdatabse = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        /* pircaso*/
        Picasso.Builder build = new Picasso.Builder(this);
        build.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso xay=build.build();
        xay.setLoggingEnabled(true);
        xay.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(xay);

        if (mAuth.getCurrentUser() == null) {
            //Go to login
            FirebaseAuth.getInstance().signOut();
            Intent loginIT= new Intent(getApplicationContext(),StartActivity.class);
            loginIT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIT);
        }else {
            mUserdatabse.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {

                        mUserdatabse.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);


                    } else {
                        // mUserdatabse.child("online").setValue(false);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
