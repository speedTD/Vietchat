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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class StatusActivity extends AppCompatActivity {
   private Toolbar Status_toolbar;
   private Button btn_updatenow;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
   private ProgressDialog progressDialog;
   private TextInputLayout txt_input_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Status_toolbar=(Toolbar) findViewById(R.id.tool_bar_layout);
        setSupportActionBar(Status_toolbar);
        getSupportActionBar().setTitle("Cập Nhật Trạng Thái ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().show();
        btn_updatenow=(Button)findViewById(R.id.btn_update_status);
        txt_input_status=(TextInputLayout) findViewById(R.id.txt_stausinput);
        //Prossecer Data
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String curent_user=firebaseUser.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(curent_user);



        btn_updatenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog=new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Đang Xử Lý.......");
                progressDialog.setMessage("Vui lòng chờ trong giây lát nhé hihi :)");
                progressDialog.show();

              String Status=txt_input_status.getEditText().getText().toString().trim();
              databaseReference.child("About").setValue(Status).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                          progressDialog.dismiss();
                          Toast.makeText(getApplicationContext(), "Thành công ", Toast.LENGTH_SHORT).show();
                          startActivity(new Intent(StatusActivity.this,SettingAcountActivity.class));
                          finish();
                      }else{
                          Toast.makeText(getApplicationContext(), "Cập nhật lỗi , vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                      }
                  }
              });



            }
        });

    }
}
