package com.developer.dinhduy.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button btn_Create;
    private TextInputLayout E_nickname,E_email,E_password;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private Animation AnimationRe;
    private ProgressDialog mprogress;
    private DatabaseReference mdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Anhxa();
        mAuth = FirebaseAuth.getInstance();
        toolbar=(Toolbar) findViewById(R.id.tool_bar_include);
      /*  AnimationRe= AnimationUtils.loadAnimation(this,R.anim.animlogin);*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create New User ");
        //return Home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get all String infomation  Editext
                String Nickname=E_nickname.getEditText().getText().toString();
                String Email=E_email.getEditText().getText().toString().trim();
                String Pass=E_password.getEditText().getText().toString();
                if(Nickname.equals("")||Email.equals("")||Pass.equals("")){
                    Toast.makeText(RegisterActivity.this, "Vui Lòng Điền Đủ thông tin", Toast.LENGTH_SHORT).show();
                }else {
                    mprogress=new ProgressDialog(RegisterActivity.this);
                    mprogress.setTitle("Creating Accout.....");
                    mprogress.setMessage(" Tài Khoản Đang Tạo Vui lòng Chờ trong giây lát ");
                    mprogress.show();
                    mprogress.setIcon(R.drawable.common_google_signin_btn_text_dark_normal);
                    mprogress.setCanceledOnTouchOutside(false);
                    Resister_Account(Nickname, Email, Pass);
                }
            }
        });
    }
    private void Anhxa(){
        btn_Create=(Button) findViewById(R.id.btn_Create_Account);
        E_nickname=(TextInputLayout) findViewById(R.id.reg_name);
        E_email=(TextInputLayout) findViewById(R.id.reg_email);
        E_password=(TextInputLayout) findViewById(R.id.reg_pass);
    }
    private  void Resister_Account(final String m_name, String m_email, String mpass) {
        mAuth.createUserWithEmailAndPassword(m_email, mpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){


                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                    String uid=firebaseUser.getUid();
                    mdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String,String> usermap=new HashMap<>();
                    usermap.put("name",m_name);
                    usermap.put("About","Hi ! Human Traidat I'm From Sao Hoa");
                    usermap.put("Picture","default");
                    usermap.put("ThumbPicture","default");
                    mdatabase.setValue(usermap);

                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                    finish();

                }else{
                    mprogress.cancel();
                    Toast.makeText(RegisterActivity.this, "Register Error !, please try Again,Check Connect to network", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
