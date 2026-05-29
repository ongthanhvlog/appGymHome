package com.example.gymhome.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymhome.R;
import com.example.gymhome.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 1;
    private static final int TYPE_AI = 2;

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).isUser()) {
            return TYPE_USER;
        } else {
            return TYPE_AI;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_ai, parent, false);
            return new AIViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else if (holder instanceof AIViewHolder) {
            ((AIViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        ImageView imgMessage;
        View cardImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            imgMessage = itemView.findViewById(R.id.imgMessage);
            cardImage = itemView.findViewById(R.id.cardImage);
        }

        void bind(ChatMessage message) {
            if (message.getText() != null && !message.getText().isEmpty()) {
                txtMessage.setVisibility(View.VISIBLE);
                txtMessage.setText(message.getText());
            } else {
                txtMessage.setVisibility(View.GONE);
            }

            if (message.getImage() != null) {
                cardImage.setVisibility(View.VISIBLE);
                imgMessage.setImageBitmap(message.getImage());
            } else if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                cardImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(message.getImageUrl())
                        .into(imgMessage);
            } else {
                cardImage.setVisibility(View.GONE);
            }
        }
    }

    static class AIViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;

        public AIViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }

        void bind(ChatMessage message) {
            if (message.isThinking()) {
                txtMessage.setText("AI đang suy nghĩ...");
                return;
            }

            if (!message.isAnimated() && message.getText() != null) {
                animateText(message.getText(), message);
            } else {
                txtMessage.setText(message.getText());
            }
        }

        private void animateText(String fullText, ChatMessage message) {
            message.setAnimated(true);
            final int[] i = {0};
            android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            txtMessage.setText("");

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (i[0] <= fullText.length()) {
                        txtMessage.setText(fullText.substring(0, i[0]++));
                        handler.postDelayed(this, 5);
                    }
                }
            };
            handler.post(runnable);
        }
    }
}
