package com.developer.dinhduy.chatapp;

import android.content.Context;
import android.os.Build;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    private  String id_chat;
    private Toolbar toolbar;
    private DatabaseReference mrootRef;
    TextView txtname,txtOnline;
    CircleImageView  imageView;
    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private ImageButton BtnSend;
    private EditText Inputtext;
    private RecyclerView ListMessage;
    private List<Messages> Listmsg= new ArrayList<>();
    private  MessagesAdapter adapter;


    private LinearLayoutManager mlinerlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar=(Toolbar) findViewById(R.id.layout_Bar_chat);
        BtnSend=(ImageButton) findViewById(R.id.btn_send) ;
        Inputtext=(EditText) findViewById(R.id.editText) ;
        ListMessage=(RecyclerView) findViewById(R.id.all_chat);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


      //

        id_chat=getIntent().getStringExtra("chat_id");
        mrootRef= FirebaseDatabase.getInstance().getReference();
        String namechat=getIntent().getStringExtra("chat_name");
        getSupportActionBar().setTitle(namechat);
        mAuth=FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();


        mlinerlayout= new LinearLayoutManager(this);
        ListMessage.setHasFixedSize(true);
        ListMessage.setLayoutManager(mlinerlayout);
        adapter=new MessagesAdapter(Listmsg);
        ListMessage.setAdapter(adapter);
        LoadMessage();
        // cái này cho Actionbar

        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Action_view= inflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(Action_view);

        txtname=(TextView) findViewById(R.id.txt_Displayname);
        txtOnline=(TextView) findViewById(R.id.txt_Last);
        imageView=(CircleImageView) findViewById(R.id.img_custom_bar);

        txtname.setText(namechat);

        mrootRef.child("Users").child(id_chat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Online =dataSnapshot.child("online").getValue().toString();
                String Image=dataSnapshot.child("ThumbPicture").getValue().toString();
                if(Online.equals("true")){
                    txtOnline.setText("Đang hoạt động");
                }else{
                    // set Time Không hoạt động
                    TimeAgo ClassTime= new TimeAgo();
                    long OnlineTime=Long.parseLong(Online);
                    String LastTime=ClassTime.getTimeAgo(OnlineTime,getApplicationContext());
                    txtOnline.setText(LastTime);
                }
                Picasso.get()
                        .load(Image)
                        .into(imageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Cây Chat
        mrootRef.child("chat").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(id_chat)){
                    Map chatApp = new HashMap();
                    chatApp.put("seen",false);
                    chatApp.put("timesamp", ServerValue.TIMESTAMP);


                    // chat user

                    Map ChatUser= new HashMap();

                    ChatUser.put("chat/"+CurrentUserID+"/"+id_chat,chatApp);

                    ChatUser.put("chat/"+id_chat+"/"+CurrentUserID,chatApp);



                    mrootRef.updateChildren(ChatUser, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // btn send
        BtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MessageSend();
            }
        });

    }

   private void LoadMessage() {
        mrootRef.child("messages").child(CurrentUserID).child(id_chat).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages mss=dataSnapshot.getValue(Messages.class);
                Listmsg.add(mss);
                adapter.notifyDataSetChanged();
                ListMessage.scrollToPosition(Listmsg.size()-1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    String Push_id;
    private void MessageSend() {
        String Ms= Inputtext.getText().toString().trim();

        if(!TextUtils.isEmpty(Ms)){
            String User_Send="messages/"+id_chat+"/"+CurrentUserID;
            String User_Recived="messages/"+CurrentUserID+"/"+id_chat;

            DatabaseReference User_message_Push=mrootRef.child("messages").child(CurrentUserID)
                    .child(id_chat).push();

             Push_id=User_message_Push.getKey();

            Map MessagesMap= new HashMap();

            MessagesMap.put("message",Ms);

            MessagesMap.put("seen",false);

            MessagesMap.put("type","text");

            MessagesMap.put("timesamp",ServerValue.TIMESTAMP);

            MessagesMap.put("from",CurrentUserID);



            Map  messagesUserMap=new HashMap();

            messagesUserMap.put(User_Send+"/"+Push_id,MessagesMap);

            messagesUserMap.put(User_Recived+"/"+Push_id,MessagesMap);

            mrootRef.updateChildren(messagesUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError!=null){
                        Log.d("ER",databaseError.getMessage().toString());
                    }else{
                        Inputtext.setText("");
                    }
                }
            });




        }
    }
}
