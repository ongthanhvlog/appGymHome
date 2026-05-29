package com.example.gymhome.model;

import android.graphics.Bitmap;

public class ChatMessage {
    private String text;
    private Bitmap image;
    private String imageUrl;
    private boolean isUser;
    private boolean isThinking;
    private boolean isAnimated = false;

    public ChatMessage(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }

    public ChatMessage(String text, boolean isUser, boolean isThinking) {
        this.text = text;
        this.isUser = isUser;
        this.isThinking = isThinking;
    }

    public ChatMessage(String text, Bitmap image, boolean isUser) {
        this.text = text;
        this.image = image;
        this.isUser = isUser;
    }

    public ChatMessage(String text, String imageUrl, boolean isUser) {
        this.text = text;
        this.imageUrl = imageUrl;
        this.isUser = isUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public boolean isThinking() {
        return isThinking;
    }

    public void setThinking(boolean thinking) {
        isThinking = thinking;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }
}
