package com.tareq.chatapp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Tareq Sefati on 02-Jul-23
 */
public class MessageGroup implements Serializable {
    private String groupId;
    private String groupName;
    private String groupHash;
    private Date createdOn;
    private List<String> participantIdList;
    private List<String> adminIdList;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupHash() {
        return groupHash;
    }

    public void setGroupHash(String groupHash) {
        this.groupHash = groupHash;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public List<String> getParticipantIdList() {
        return participantIdList;
    }

    public void setParticipantIdList(List<String> participantIdList) {
        this.participantIdList = participantIdList;
    }

    public List<String> getAdminIdList() {
        return adminIdList;
    }

    public void setAdminIdList(List<String> adminIdList) {
        this.adminIdList = adminIdList;
    }

    public MessageGroup(String groupId, String groupName, String groupHash, Date createdOn, List<String> participantIdList, List<String> adminIdList) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupHash = groupHash;
        this.createdOn = createdOn;
        this.participantIdList = participantIdList;
        this.adminIdList = adminIdList;
    }

    public MessageGroup() {
    }

    @Override
    public String toString() {
        return "MessageGroup{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupHash='" + groupHash + '\'' +
                ", createdOn=" + createdOn +
                ", participantIdList=" + participantIdList +
                ", adminIdList=" + adminIdList +
                '}';
    }
}
