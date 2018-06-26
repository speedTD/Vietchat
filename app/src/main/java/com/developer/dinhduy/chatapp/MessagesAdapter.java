package com.developer.dinhduy.chatapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHoder>{

    private static final int VIEW_TYPE_MESSAGE_THIS_USER = 0;
    private static final int VIEW_TYPE_MESSAGE_OTHER_USERS = 1;
    private List<Messages> msgList;
    FirebaseAuth mauth;

    public MessagesAdapter(List<Messages> msgList) {
        this.msgList = msgList;
    }

    @NonNull
    @Override
    public MessagesViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // chỗ này cần gettype này
        RecyclerView.ViewHolder holder = null;
           switch (viewType){
               case VIEW_TYPE_MESSAGE_THIS_USER:
                   View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_list, parent, false);
                   holder=new MessagesViewHoder(v);
                   break;

               case VIEW_TYPE_MESSAGE_OTHER_USERS:
                   View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_listchat_2, parent, false);
                   holder = new MessagesViewHoder(v2);
                   break;
           }

        return (MessagesViewHoder) holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHoder holder, int position) {
        // Bind the Chat object to the ChatHolder
        Messages m=msgList.get(position);
      /*  if(From_user.equals(current_uid)){
           dem=1;
           holder.txtchat.setBackgroundColor(Color.WHITE);


        }else{

            holder.txtchat.setBackgroundResource(R.drawable.ve_khung_chat);
            holder.txtchat.setTextColor(Color.BLACK);
            dem=2;
        }*/

        switch (holder.getItemViewType()) {

            case 0:
                holder.txtchat.setText(m.getMessage());
                break;

            case 1:
                holder.txtchat2.setText(m.getMessage());
                break;
        }
    }


    public  class MessagesViewHoder extends RecyclerView.ViewHolder{

        public  TextView txtchat,txtchat2;

        public MessagesViewHoder(View itemView) {
            super(itemView);
            txtchat=(TextView) itemView.findViewById(R.id.text_chat);
            txtchat2=(TextView) itemView.findViewById(R.id.txtchat2);
        }

    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        mauth=FirebaseAuth.getInstance();
        String current_uid=mauth.getCurrentUser().getUid();
        Messages m=msgList.get(position);
        String From_user=m.getFrom();
       //Người Gửi
        if (From_user.equals(current_uid)) {

            return VIEW_TYPE_MESSAGE_THIS_USER;
        } else {
         //   người nhận

            return  VIEW_TYPE_MESSAGE_OTHER_USERS;
        }
    }


}
