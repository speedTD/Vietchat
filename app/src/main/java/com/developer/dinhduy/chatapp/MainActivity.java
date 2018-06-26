package com.developer.dinhduy.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.developer.dinhduy.chatapp.R.menu.main_menu_custom;

public class MainActivity extends AppCompatActivity {
   FirebaseAuth mAuth;
   private Toolbar toolbar;
   private ViewPager mviewPager;
   private DatabaseReference mUserRef;
   private  SelectionAdapter madapterselect;
   private TabLayout mtablayout;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        toolbar=(Toolbar) findViewById(R.id.tool_bar_include);


        mviewPager=(ViewPager) findViewById(R.id.tab_viewpaper);
        madapterselect=new SelectionAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(madapterselect);


        mtablayout=(TabLayout) findViewById(R.id.main_tab);
        mtablayout.setupWithViewPager(mviewPager);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Life Book");

        if (mAuth.getCurrentUser() == null) {
            //Go to login
            FirebaseAuth.getInstance().signOut();
            Intent loginIT= new Intent(getApplicationContext(),StartActivity.class);
            loginIT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIT);


        }else {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

    }
    @Override
    protected void onStart() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null){
          Sendtostart();
        }else{
             mUserRef.child("online").setValue(true);
        }
        super.onStart();
    }
    private void Sendtostart(){
        Intent startIntent=new Intent(MainActivity.this,StartActivity.class);

        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onStop() {
     //   mUserRef.child("online").setValue(false);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_custom, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_logout){
            //When singout  , return Main Login
         FirebaseAuth.getInstance().signOut();
         Sendtostart();
        }
        if(item.getItemId()==R.id.menu_accout){
            Intent startIntent=new Intent(MainActivity.this,SettingAcountActivity.class);
            startActivity(startIntent);
        }
        if(item.getItemId()==R.id.menu_alluser){
            Intent startIntent=new Intent(MainActivity.this,UserActivity.class);
            startActivity(startIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
