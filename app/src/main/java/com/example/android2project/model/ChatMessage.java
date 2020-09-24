package com.example.android2project.model;

import java.util.Date;

public class ChatMessage implements Comparable<Object> {
    private String mContent;
    private String mRecipientEmail;
    private Date mTime;

    public ChatMessage() {}

    public ChatMessage(String content, String recipientEmail) {
        this.mContent = content;
        this.mRecipientEmail = recipientEmail;
        this.mTime = new Date();
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getRecipientEmail() {
        return mRecipientEmail;
    }

    public void setRecipientEmail(String recipient) {
        this.mRecipientEmail = recipient;
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
                ", mRecipient=" + mRecipientEmail +
                ", mTime=" + mTime +
                '}';
    }
}


