package com.application.javafx_chat_messenger_application.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Tareq Sefati on 18-Jun-23
 */
public class Message implements Serializable {
    private String senderId;
    private String receiverId;
    private String msg;
    private Date sentDateTime;
    private MessageType messageType;
    private Object dataObject;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getSentDateTime() {
        return sentDateTime;
    }

    public void setSentDateTime(Date sentDateTime) {
        this.sentDateTime = sentDateTime;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Object getDataObject() {
        return dataObject;
    }

    public void setDataObject(Object dataObject) {
        this.dataObject = dataObject;
    }

    public Message(String senderId, String receiverId, String msg, Date sentDateTime, MessageType messageType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.msg = msg;
        this.sentDateTime = sentDateTime;
        this.messageType = messageType;
    }

    public Message() {
    }

    public Message(String senderId, String receiverId, String msg, Date sentDateTime, MessageType messageType, Object dataObject) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.msg = msg;
        this.sentDateTime = sentDateTime;
        this.messageType = messageType;
        this.dataObject = dataObject;
    }
}
