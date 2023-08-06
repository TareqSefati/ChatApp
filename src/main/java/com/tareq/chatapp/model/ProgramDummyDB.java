package com.tareq.chatapp.model;

import com.tareq.chatapp.util.FileUtil;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tareq Sefati on 08-Jul-23
 */
public class ProgramDummyDB {

    //SERVER SIDE DATA:
    private static List<String> activeClientIds = new ArrayList<>();
    private static Map<String, Socket> activeClientList = new HashMap<>();

    public static List<String> getActiveClientIds(){
        return activeClientIds;
    }

    public static Map<String, Socket> getActiveClientList(){
        return activeClientList;
    }

    //GENERATING DUMMY USERS DATA:
    private static List<User> userList = new ArrayList<>(); //UserController.generateDummyUsers(10);
    public static List<User> getUserList() {
        userList = FileUtil.getRegisteredUserFromFile();
        return userList;
    }

    public static void addUserInFile(User user){
        FileUtil.appendUserToFile(user);
        userList = FileUtil.getRegisteredUserFromFile();
    }

    public static void deleteAllRegisteredUser(){
        FileUtil.deleteAllUserRecords();
        userList = FileUtil.getRegisteredUserFromFile();
    }

    public static boolean isIdenticalUser(User targetUser){
        userList = FileUtil.getRegisteredUserFromFile();
        for (User user : userList) {
            if (targetUser.getUsername().equals(user.getUsername()) && targetUser.getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public static User findUser(String email, String password) {
        userList = FileUtil.getRegisteredUserFromFile();
        for (User user : userList) {
            if (email.equals(user.getEmail()) && password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    //CLIENT SIDE DATA:
    private static Map<String, List<MessageGroup>> userWiseGroupMap = new HashMap<>();
    private static Map<Pair<String, String>, VBox> userWiseConversationMap = new HashMap<>();

    public static void checkUserAndAddGroup(String userId, MessageGroup messageGroup) {
        if (userWiseGroupMap.containsKey(userId)){
            userWiseGroupMap.get(userId).add(messageGroup);
        }else {
            List<MessageGroup> list = new ArrayList<>();
            list.add(messageGroup);
            userWiseGroupMap.put(userId, list);
        }
    }

    public static Map<String, List<MessageGroup>> getUserWiseGroupMap(){
        return userWiseGroupMap;
    }

    public static List<MessageGroup> getGroupListByUserId(String userId){
        if (userWiseGroupMap.containsKey(userId)){
            return userWiseGroupMap.get(userId);
        }
        return null;
    }

    public static Map<Pair<String, String>, VBox> getUserWiseConversationMap() {
        return userWiseConversationMap;
    }
}
