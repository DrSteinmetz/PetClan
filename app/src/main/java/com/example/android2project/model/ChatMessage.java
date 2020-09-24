package com.example.android2project.model;

import java.util.Date;

public class ChatMessage implements Comparable<Object> {
    private String mContent;
    private String mRecipient;
    private Date mTime;

    public ChatMessage() {}

    public ChatMessage(String content, String recipient) {
        this.mContent = content;
        this.mRecipient = recipient;
        this.mTime = new Date();
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getRecipient() {
        return mRecipient;
    }

    public void setRecipient(String recipient) {
        this.mRecipient = recipient;
    }

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date mTime) {
        this.mTime = mTime;
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof ChatMessage) {
            ChatMessage otherMessage = (ChatMessage) object;
            return this.mTime.compareTo(otherMessage.getTime());
        }
        return 0;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "mContent='" + mContent + '\'' +
                ", mRecipient=" + mRecipient +
                ", mTime=" + mTime +
                '}';
    }
}


