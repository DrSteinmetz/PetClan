package com.example.android2project.model;

public class Conversation {
    private String mChatId;
    private String mSenderEmail;
    private String mRecipientEmail;

    public Conversation() {}

    public Conversation(String senderEmail, String recipientEmail) {
        final String id1 = senderEmail.replace(".", "");
        final String id2 = recipientEmail.replace(".", "");

        this.mChatId = id1 + "&" + id2;
        if (id2.compareTo(id1) < 0) {
            this.mChatId = id2 + "&" + id1;
        }

        this.mSenderEmail = senderEmail;
        this.mRecipientEmail = recipientEmail;
    }

    public String getChatId() {
        return mChatId;
    }

    public void setChatId(String chatId) {
        this.mChatId = chatId;
    }

    public String getSenderEmail() {
        return mSenderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.mSenderEmail = senderEmail;
    }

    public String getRecipientEmail() {
        return mRecipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.mRecipientEmail = recipientEmail;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "chatId='" + mChatId + '\'' +
                ", senderEmail='" + mSenderEmail + '\'' +
                ", receiverEmail='" + mRecipientEmail + '\'' +
                '}';
    }
}
