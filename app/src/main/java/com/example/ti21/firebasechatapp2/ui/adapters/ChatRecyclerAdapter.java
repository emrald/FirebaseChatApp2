package com.example.ti21.firebasechatapp2.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ti21.firebasechatapp2.R;
import com.example.ti21.firebasechatapp2.models.Chat;
import com.example.ti21.firebasechatapp2.ui.fragments.ChatFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 10/16/2016 , 10:36 AM
 * Project: FirebaseChat
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private List<Chat> mChats;
    Context context;

    public ChatRecyclerAdapter(List<Chat> chats) {
        mChats = chats;
    }

    public ChatRecyclerAdapter(Context chatFragment, ArrayList<Chat> chats) {

        mChats = chats;
        this.context = chatFragment;
    }

    public void add(Chat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.sender.substring(0, 1);

        Log.e("VISISBle...", chat.message + ".." + chat.downloadUrl + "...");

        if (chat.message.equals("")) {

            myChatViewHolder.txtChatMessage.setVisibility(View.GONE);
            myChatViewHolder.img_chat_message.setVisibility(View.VISIBLE);

            if (!chat.downloadUrl.equals("")) {
                Log.e("URLLLLLLLLLLLLLL", chat.downloadUrl + "");
                Picasso.with(context).load(chat.downloadUrl).placeholder(R.mipmap.ic_launcher).into(myChatViewHolder.img_chat_message);
            }

        } else {
            myChatViewHolder.txtChatMessage.setVisibility(View.VISIBLE);
            myChatViewHolder.img_chat_message.setVisibility(View.GONE);

            myChatViewHolder.txtChatMessage.setText(chat.message);
        }


        myChatViewHolder.txtUserAlphabet.setText(alphabet);


    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.sender.substring(0, 1);

        if (chat.message.equals("")) {

            otherChatViewHolder.txtChatMessage.setVisibility(View.GONE);
            otherChatViewHolder.img_chat_message.setVisibility(View.VISIBLE);

            Picasso.with(context).load(chat.downloadUrl).placeholder(R.mipmap.ic_launcher).into(otherChatViewHolder.img_chat_message);

        } else {
            otherChatViewHolder.txtChatMessage.setVisibility(View.VISIBLE);
            otherChatViewHolder.img_chat_message.setVisibility(View.GONE);

            otherChatViewHolder.txtChatMessage.setText(chat.message);
        }

        otherChatViewHolder.txtUserAlphabet.setText(alphabet);

    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;
        private ImageView img_chat_message;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            img_chat_message = (ImageView) itemView.findViewById(R.id.img_chat_message);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;
        private ImageView img_chat_message;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            img_chat_message = (ImageView) itemView.findViewById(R.id.img_chat_message);
        }
    }
}
