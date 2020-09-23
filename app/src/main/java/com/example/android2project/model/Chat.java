package com.example.android2project.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private String chatId;
    private String senderEmail;
    private String receiverEmail;
    private List<ChatMessage> messageList;

    public Chat() {
    }

    public Chat(String senderEmail, String reciverEmail) {
        this.senderEmail = senderEmail;
        this.receiverEmail = reciverEmail;
        messageList = new ArrayList<>();
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

    public List<ChatMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }
}
