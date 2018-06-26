package com.developer.dinhduy.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class SettingAcountActivity extends AppCompatActivity {
    Button btn_update_status,Btn_update_Picture;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private TextView txt_Aboutx,txt_name;
    private ImageView PictureProfie;
    private static final int REQUEST_CODE_HINHANH=1;
    private StorageReference mStorageRef;
    String curent_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_acount);
        btn_update_status=(Button) findViewById(R.id.btn_doiten);
        Btn_update_Picture=(Button) findViewById(R.id.btn_doianh);
        txt_Aboutx=(TextView) findViewById(R.id.txt_About);
        txt_name=(TextView) findViewById(R.id.txt_name_user);
        PictureProfie=(ImageView) findViewById(R.id.circleImageView) ;

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        curent_user=firebaseUser.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(curent_user);
        // giữ cho dữ liệu đã được load không phải load lại
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String About= dataSnapshot.child("About").getValue().toString();
                String Picture =dataSnapshot.child("Picture").getValue().toString();
                String thumb_picture=dataSnapshot.child("ThumbPicture").toString();

                txt_Aboutx.setText(About);
                txt_name.setText(name);
                if(!Picture.equals("default")){
                    Picasso.get()
                            .load(Picture)
                            .into(PictureProfie);
                }

               // Picasso.get().load(Picture).into(PictureProfie);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


        btn_update_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingAcountActivity.this,StatusActivity.class));

            }
        });

     Btn_update_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent hinhanh= new Intent();
                hinhanh.setType("image/*");
                hinhanh.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(hinhanh,"Chọn hình"),REQUEST_CODE_HINHANH);
                // start picker to get image for cropping and then use the image in cropping activity
               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingAcountActivity.this);*/


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AA", "onActivityResult");
        if(requestCode==REQUEST_CODE_HINHANH&&resultCode==RESULT_OK) {
            Uri Urimg = data.getData();
            CropImage.activity(Urimg)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
            Log.d("AA", "onActivityResult 2");
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d("AA", "onActivityResult 3");
                if (resultCode == RESULT_OK) {
                    Uri file = result.getUri();
                    Log.d("AA", "onActivityResult 4");

                    File file1= new File(file.getPath());
                    Bitmap thumb_file = new Compressor(SettingAcountActivity.this)
                            .setMaxWidth(100)
                            .setMaxHeight(100)
                            .setQuality(75)
                            .compressToBitmap(file1);
                    // lấy trên firebase
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    thumb_file.compress(Bitmap.CompressFormat.JPEG,75,out);
                     final byte [] byt= out.toByteArray();
                    final StorageReference filepath =mStorageRef.child("profile_picture").child("thumbs").child(curent_user+".jpg");
                    StorageReference riversRef = mStorageRef.child("profile_picture").child(random()+".jpg");
                    riversRef.putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         Log.d("AA", "onActivityResult 5");

                         if(task.isSuccessful()){
                             final String downloadUrl= task.getResult().getDownloadUrl().toString();
                             UploadTask uploadTask=filepath.putBytes(byt);
                             uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                 @Override
                                 public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbtask) {
                                     if(thumbtask.isSuccessful()){
                                         // ddowwnload thành công
                                        String downloadthumb =thumbtask.getResult().getDownloadUrl().toString();
                                         Map hashMap =new HashMap <>();
                                         hashMap.put("Picture",downloadUrl);
                                         hashMap.put("ThumbPicture",downloadthumb);
                                         databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()){
                                                     Toast.makeText(SettingAcountActivity.this, "Thanh cong 1", Toast.LENGTH_SHORT).show();
                                                 }
                                             }
                                         });

                                         Toast.makeText(SettingAcountActivity.this, "Thanh cong 2", Toast.LENGTH_SHORT).show();
                                     }else {
                                         Toast.makeText(SettingAcountActivity.this, "that bai", Toast.LENGTH_SHORT).show();
                                     }

                                 }
                             });

                         }else {
                             Toast.makeText(SettingAcountActivity.this, "that bai thumb", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(SettingAcountActivity.this, "Lỗi rồi"+error, Toast.LENGTH_SHORT).show();
                }
            }
           // Toast.makeText(this, "Uri ="+Urimg, Toast.LENGTH_SHORT).show();

        }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
