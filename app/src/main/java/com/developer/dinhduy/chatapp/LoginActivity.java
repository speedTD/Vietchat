package com.developer.dinhduy.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    Button btn_login;
    private DatabaseReference mUserdatabase;
    private ProgressDialog mprogress;
    private TextInputLayout E_email,E_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth=FirebaseAuth.getInstance();
        mUserdatabase= FirebaseDatabase.getInstance().getReference().child("Users");


        toolbar=(Toolbar) findViewById(R.id.tool_bar_include);
        btn_login=(Button) findViewById(R.id.btn_loginok);
        E_email=(TextInputLayout) findViewById(R.id.reg_email);
        E_password=(TextInputLayout) findViewById(R.id.reg_pass);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        mprogress=new ProgressDialog(this);
        //return Home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Email=E_email.getEditText().getText().toString().trim();
                     String Pass=E_password.getEditText().getText().toString().trim();
                    if(Email.equals("")||Pass.equals("")){
                       Toast.makeText(LoginActivity.this, "vui Lòng Điền Đủ Thông Tin", Toast.LENGTH_SHORT).show();
                    }else {
                        mprogress.setTitle("Logging....");
                        mprogress.setMessage("Đang Kiểm Tra Thông Tin Đăng Nhập Vui lòng chờ ");
                        mprogress.show();
                        mprogress.setIcon(R.drawable.common_google_signin_btn_text_dark_normal);
                        mprogress.setCanceledOnTouchOutside(false);
                        checkLogin(Email, Pass);
                    }
                }
            });
        }

    private void checkLogin(String m_email,String m_password){
        mAuth.signInWithEmailAndPassword(m_email, m_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String Current_id=mAuth.getCurrentUser().getUid();
                    String Devicetoken= FirebaseInstanceId.getInstance().getToken();

                    mUserdatabase.child(Current_id).child("device_token").setValue(Devicetoken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mprogress.dismiss();
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent loginIT= new Intent(LoginActivity.this,MainActivity.class);
                            loginIT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIT);
                            finish();
                        }
                    });




                }else{
                    //đóng progess khi login faild
                    mprogress.cancel();
                    Toast.makeText(LoginActivity.this, "Login Faild,Please Check Connect or Username PassWord", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
