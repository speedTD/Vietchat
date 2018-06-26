package com.developer.dinhduy.chatapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
     private DatabaseReference Friends_database;
     private  DatabaseReference User_database;
    private FirebaseAuth firebaseAuth;
    private  String Cutrernt_Id_User;
    private View mview;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mview=inflater.inflate(R.layout.fragment_friends,container,false);
        recyclerView=(RecyclerView) mview.findViewById(R.id.friends_view);
        firebaseAuth=FirebaseAuth.getInstance();
        Cutrernt_Id_User=firebaseAuth.getCurrentUser().getUid();

         Friends_database=FirebaseDatabase.getInstance().getReference().child("Friends").child(Cutrernt_Id_User);
         Friends_database.keepSynced(true);
         User_database=FirebaseDatabase.getInstance().getReference().child("Users");
         User_database.keepSynced(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return mview;

    }

    @Override
    public void onStart() {
        super.onStart();
      startListening();

    }

   public void startListening(){
       Query query = Friends_database;
       FirebaseRecyclerOptions<Friends> options =
               new FirebaseRecyclerOptions.Builder<Friends>()
                       .setQuery(query, Friends.class)
                       .build();
       FirebaseRecyclerAdapter <Friends,FriendViewHoder> Adapter=new FirebaseRecyclerAdapter<Friends, FriendViewHoder>(options) {
           @Override

           protected void onBindViewHolder(@NonNull final FriendViewHoder holder, int position, @NonNull Friends model) {

               holder.setdate(model.getdate());
              final String list_Friends=getRef(position).getKey();
               //get User
               User_database.child(list_Friends).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       final String name=dataSnapshot.child("name").getValue().toString();
                       String thumb=dataSnapshot.child("ThumbPicture").getValue().toString();
                       Boolean Online= Boolean.valueOf(dataSnapshot.child("online").getValue().toString());
                       holder.Setname(name);
                       holder.SetPicture(thumb);
                       holder.SetOnline(Online);
                       holder.Mview.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               CharSequence  chon []= new CharSequence[]{"Trang Cá Nhân","Gửi Tin Nhắn"};
                               AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                               builder.setTitle("Chọn");
                               builder.setItems(chon, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {

                                       if(i==0){
                                           Intent intent =new Intent(getContext(),ProfileActivity.class);

                                           intent.putExtra("profile_id",list_Friends);

                                           startActivity(intent);
                                       }
                                       if(i==1){
                                           Intent intent =new Intent(getContext(),ChatActivity.class);

                                           intent.putExtra("chat_id",list_Friends);
                                           intent.putExtra("chat_name",name);

                                           startActivity(intent);
                                       }

                                   }
                               });

                               builder.show();
                           }
                       });

                       }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });


           }
// bug rồi :((( lại sắp sml r :v
           @NonNull
           @Override
           public FriendViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.user_single_layout, parent, false);

               return new FriendsFragment.FriendViewHoder(view);

           }
       };
       recyclerView.setAdapter(Adapter);
       Adapter.startListening();
   }
    public static class FriendViewHoder extends RecyclerView.ViewHolder{

        View Mview;
        public FriendViewHoder(View itemView) {
            super(itemView);
            Mview=itemView;
        }
        public void setdate(String date){
            TextView dateufriends=(TextView) Mview.findViewById(R.id.id_date_friends);

            System.out.print(date);
            dateufriends.setText(date);
        }
        public  void Setname(String name){
            TextView Nametxt=(TextView) Mview.findViewById(R.id.id_name_friends);
            Nametxt.setText(name);
        }
        public  void SetPicture(String pic){
            CircleImageView cycle=(CircleImageView) Mview.findViewById(R.id.img_friends);
            Picasso.get()
                    .load(pic)
                    .into(cycle);
        }
        public  void SetOnline(Boolean online){
            ImageView imageView=Mview.findViewById(R.id.imageView);
            if(online.equals(true)){
                imageView.setVisibility(View.VISIBLE);
            }else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }

}
