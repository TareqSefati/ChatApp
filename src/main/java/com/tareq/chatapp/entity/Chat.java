package com.tareq.chatapp.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * Created by Tareq Sefati on 16-Jun-23
 */
//@Entity
//@Table(name = "chat")
public class Chat implements Serializable{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "ID", nullable = false)
//    private Long id;
//    @Column(name = "sender_id", nullable = false)
//    private Long senderId;
//    @Column(name = "receiver_id", nullable = false)
//    private Long receiverId;
//    @Column(name = "message")
//    private String message;
//    @Column(name = "file_url")
//    private String fileUrl;
//    @Column(name = "conversation_hash", nullable = false)
//    private String conversationHash;    //This will be treated as groupHash for group conversation.
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "sent_datetime", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    private Date sentDateTime;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Long getSenderId() {
//        return senderId;
//    }
//
//    public void setSenderId(Long senderId) {
//        this.senderId = senderId;
//    }
//
//    public Long getReceiverId() {
//        return receiverId;
//    }
//
//    public void setReceiverId(Long receiverId) {
//        this.receiverId = receiverId;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public String getConversationHash() {
//        return conversationHash;
//    }
//
//    public void setConversationHash(String conversationHash) {
//        this.conversationHash = conversationHash;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public Date getSentDateTime() {
//        return sentDateTime;
//    }
//
//    public void setSentDateTime(Date sentDateTime) {
//        this.sentDateTime = sentDateTime;
//    }
//
//    public String getFileUrl() {
//        return fileUrl;
//    }
//
//    public void setFileUrl(String fileUrl) {
//        this.fileUrl = fileUrl;
//    }
//
//    @Override
//    public String toString() {
//        return "Chat{" +
//                "id=" + id +
//                ", senderId=" + senderId +
//                ", receiverId=" + receiverId +
//                ", message='" + message + '\'' +
//                ", fileUrl='" + fileUrl + '\'' +
//                ", conversationHash='" + conversationHash + '\'' +
//                ", sentDateTime=" + sentDateTime +
//                '}';
//    }

    private String id;
    private String msg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                '}';
    }
}
