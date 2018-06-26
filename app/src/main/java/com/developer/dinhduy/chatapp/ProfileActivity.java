package com.developer.dinhduy.chatapp;

import android.graphics.Color;
import android.icu.text.TimeZoneFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    private TextView profile_name,profile_about,profile_countfriends;
    private ImageView imageView;
    private Button button,buttondeline;
    private DatabaseReference mUserdatabase;
    private DatabaseReference mfriendsdatabse;
    private DatabaseReference mrootRef;
    private FirebaseUser mUsercurrent;
    private String mcurrent_State;
    private DatabaseReference mfriends;
    private DatabaseReference mNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_name=(TextView) findViewById(R.id.Profile_name);
        profile_about=(TextView) findViewById(R.id.Profile_About);
        profile_countfriends=(TextView) findViewById(R.id.Profile_CountFriends);
        imageView=(ImageView) findViewById(R.id.Profle_picture);
        button=(Button) findViewById(R.id.btnadd);
        buttondeline=(Button) findViewById(R.id.btndeline);


        // lấy dữ liệu này
        final String Getid=getIntent().getExtras().getString("profile_id");
        profile_name.setText(Getid);

        mcurrent_State="Not_Friend";
        mfriendsdatabse=FirebaseDatabase.getInstance().getReference().child("Friends_reg");
        mNotification=FirebaseDatabase.getInstance().getReference().child("notifications");
        mrootRef=FirebaseDatabase.getInstance().getReference();

        mfriends=FirebaseDatabase.getInstance().getReference().child("Friends");
        //--------------------NOT FRIENDS----------------------//
         button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mcurrent_State.equals("Not_Friend")){
                   final HashMap<String,String> NotificationHash=new HashMap<>();
                    NotificationHash.put("from",mUsercurrent.getUid());
                    NotificationHash.put("type","request");



                    DatabaseReference newNotification =mrootRef.child("notifications").child(Getid).push();
                    String newNotificationId=newNotification.getKey();
                    Map Reqestmap= new HashMap();
                    Reqestmap.put("Friends_reg/"+mUsercurrent.getUid()+"/"+Getid+"/request_type","send");
                    Reqestmap.put("Friends_reg/"+Getid+'/'+mUsercurrent.getUid()+"/request_type","received");
                    Reqestmap.put("notifications/"+Getid+'/'+newNotificationId,NotificationHash);
                    mrootRef.updateChildren(Reqestmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null){
                       Toast.makeText(ProfileActivity.this, "Lỗi rồi", Toast.LENGTH_SHORT).show();
                             }


                        }
                    });


                }

        //--------------------Trạng thái CANCEL FRIENDS----------------------//

                  if (mcurrent_State.equals("reg_send")){
                    mfriendsdatabse.child(mUsercurrent.getUid()).child(Getid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "Đang Hủy.", Toast.LENGTH_SHORT).show();
                                mfriendsdatabse.child(Getid).child(mUsercurrent.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ProfileActivity.this, "Tạm Biệt Bro ", Toast.LENGTH_SHORT).show();
                                        button.setText("Bro !");
                                        button.setEnabled(true);
                                        mcurrent_State="Not_Friend";
                                    }
                                });
                            }else{
                                Toast.makeText(ProfileActivity.this, " Bro Lỗi Rồi huhu! ", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                //--------------------Trạng thái Đã là Bro của nhau----------------------//
                if(mcurrent_State.equals("Friends")){
                    button.setText("Cancel Bro");
                    button.setBackgroundColor(Color.parseColor("FFFF00EE"));
                }

                //--------------------Trạng thái Reg Received----------------------//

                if(mcurrent_State.equals("reg_received")){
                    final  String Datenow= DateFormat.getDateTimeInstance().format(new Date());

                    Map Friendsmap=new HashMap();
                    Friendsmap.put("Friends/"+mUsercurrent.getUid()+"/"+Getid+"/date",Datenow);
                    Friendsmap.put("Friends/"+Getid+"/"+mUsercurrent.getUid()+"/date",Datenow);

                    Friendsmap.put("Friends_reg/"+mUsercurrent.getUid()+"/"+Getid,null);
                    Friendsmap.put("Friends_reg/"+Getid+'/'+mUsercurrent.getUid(),null);
                    mrootRef.updateChildren(Friendsmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError==null){
                                Toast.makeText(ProfileActivity.this, "Lỗi Khi cố trở thành Bro của nhau", Toast.LENGTH_SHORT).show();
                                button.setEnabled(true);
                                mcurrent_State="friends";
                                button.setText("Un Bro !");
                                buttondeline.setVisibility(View.INVISIBLE);
                                buttondeline.setEnabled(true);
                            }else {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }

            }
         });

         //------Hủy Kết Bạn------//

        if (mcurrent_State.equals("friends")){

            Map MapUnfriends= new HashMap();

            MapUnfriends.put("Friends/"+mUsercurrent.getUid()+"/"+Getid,null);
            MapUnfriends.put("Friends/"+Getid+"/"+mUsercurrent.getUid(),null);

            mrootRef.updateChildren(MapUnfriends, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError==null){
                        Toast.makeText(ProfileActivity.this, "Hủy Bro Thành công", Toast.LENGTH_SHORT).show();
                        mcurrent_State="Not_Friend";
                        button.setText("Bro !");

                        buttondeline.setVisibility(View.INVISIBLE);
                        buttondeline.setEnabled(true);
                    }else {
                        String error=databaseError.getMessage();
                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            button.setEnabled(true);

        }


        // Lấy dữ liệu ở databse ra nè
        mUserdatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(Getid);
        mUsercurrent= FirebaseAuth.getInstance().getCurrentUser();
        mUserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String About=dataSnapshot.child("About").getValue().toString();
                final String Picture=dataSnapshot.child("Picture").getValue().toString();
                profile_about.setText(About);
                profile_name.setText(name);
              //  Picasso.get().load(Picture).placeholder(R.drawable.avatar_default).into(imageView);
                Picasso.get().load(Picture).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_default).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Picture).placeholder(R.drawable.avatar_default).into(imageView);
                    }
                });


                // --------------------------Đồng Ý Kết Bạn ,Hủy Kết Bạn-------------------------//
                mfriendsdatabse.child(mUsercurrent.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(Getid)) {
                            String reg_type = dataSnapshot.child(Getid).child("request_type").getValue().toString();
                            if (reg_type.equals("received")) {
                                mcurrent_State = "reg_received";
                                button.setText("Đồng ý Bro! này");
                            }
                           else if (reg_type.equals("send")) {
                                mcurrent_State = "reg_send";
                                button.setText("hủy Bro");
                                buttondeline.setVisibility(View.INVISIBLE);
                                buttondeline.setEnabled(false);
                            }
                            else {
                                mfriends.child(mUsercurrent.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(Getid)){

                                            button.setText("Hủy Liên Kết Bro !");
                                            button.setEnabled(true);
                                            mcurrent_State="Friends";
                                            buttondeline.setVisibility(View.INVISIBLE);
                                            buttondeline.setEnabled(false);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
