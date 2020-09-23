package com.example.android2project.model;

public class Conversation {
    private String chatId;
    private String senderEmail;
    private String receiverEmail;

    public Conversation() {}

    public Conversation(String senderEmail, String receiverEmail) {
        this.chatId = senderEmail + "&" + receiverEmail;
        if (receiverEmail.compareTo(senderEmail) < 0) {
            this.chatId = receiverEmail + "&" + senderEmail;
        }
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "chatId='" + chatId + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", receiverEmail='" + receiverEmail + '\'' +
                '}';
    }
}
