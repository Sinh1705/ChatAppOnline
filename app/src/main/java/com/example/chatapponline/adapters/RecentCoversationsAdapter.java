package com.example.chatapponline.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapponline.databinding.ItemContainerRecentConversionBinding;
import com.example.chatapponline.listener.ConversionListener;
import com.example.chatapponline.models.ChatMessage;
import com.example.chatapponline.models.User;

import java.util.List;

public class RecentCoversationsAdapter extends RecyclerView.Adapter<RecentCoversationsAdapter.ConverionViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentCoversationsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConverionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConverionViewHolder(ItemContainerRecentConversionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ConverionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {

        return chatMessages.size();
    }

    class ConverionViewHolder extends RecyclerView.ViewHolder{
        ItemContainerRecentConversionBinding binding;

        ConverionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding){
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            binding.textName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v->{
                User user = new User();
                user.id = chatMessage.converionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversionListener.onConversionClicked(user);
            });
        }
    }
    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes = android.util.Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
