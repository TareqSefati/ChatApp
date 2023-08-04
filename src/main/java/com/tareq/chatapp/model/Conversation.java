package com.tareq.chatapp.model;

/**
 * Created by Tareq Sefati on 28-Jun-23
 */
public class Conversation {
    private String participantId;
    private String adjacentId;
    private String lastMsg;
    private String conversationHash;

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getAdjacentId() {
        return adjacentId;
    }

    public void setAdjacentId(String adjacentId) {
        this.adjacentId = adjacentId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getConversationHash() {
        return conversationHash;
    }

    public void setConversationHash(String conversationHash) {
        this.conversationHash = conversationHash;
    }

    public Conversation(String participantId, String adjacentId, String lastMsg, String conversationHash) {
        this.participantId = participantId;
        this.adjacentId = adjacentId;
        this.lastMsg = lastMsg;
        this.conversationHash = conversationHash;
    }

    public Conversation() {
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "participantId='" + participantId + '\'' +
                ", adjacentId='" + adjacentId + '\'' +
                ", lastMsg='" + lastMsg + '\'' +
                ", conversationHash='" + conversationHash + '\'' +
                '}';
    }
}
