package com.developer.dinhduy.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    private Button mResiger,mlogin;
    Animation topdown,trai,phai,Animlogin;
    TextView nameapp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mResiger=(Button) findViewById(R.id.btn_Register);
        mlogin=(Button) findViewById(R.id.btn_Have_accout);
        nameapp=(TextView) findViewById(R.id.Name_App);
        topdown=AnimationUtils.loadAnimation(this,R.anim.anim);
        trai=AnimationUtils.loadAnimation(this,R.anim.righttoleft_anim);
        phai=AnimationUtils.loadAnimation(this,R.anim.toup);
        Animlogin=AnimationUtils.loadAnimation(this,R.anim.animlogin);
        nameapp.setAnimation(topdown);
        mResiger.setAnimation(trai);
        mlogin.setAnimation(phai);

       // set click go to RegisterActivity
        mResiger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent Mystart=new Intent(StartActivity.this,RegisterActivity.class);
                view.startAnimation(Animlogin);
              startActivity(Mystart);
            }
        });
        //set click go to loginActivity

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Mystart=new Intent(StartActivity.this,LoginActivity.class);
                view.startAnimation(Animlogin);
                startActivity(Mystart);

            }
        });
    }


}
