package com.application.javafx_chat_messenger_application.model;

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


    //CLIENT SIDE DATA:
    private static Map<String, List<MessageGroup>> userWiseGroupMap = new HashMap<>();

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
}
