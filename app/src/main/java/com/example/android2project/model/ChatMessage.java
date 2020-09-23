package com.example.android2project.model;

import java.util.Date;

public class ChatMessage {
    String mContent;
    User mSender;
    Date mTime;

    public ChatMessage() {
    }

    public ChatMessage(String mContent, User mSender, Date mTime) {
        this.mContent = mContent;
        this.mSender = mSender;
        this.mTime = mTime;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public User getSender() {
        return mSender;
    }

    public void setSender(User mSender) {
        this.mSender = mSender;
    }

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date mTime) {
        this.mTime = mTime;
    }
}


