package com.tareq.chatapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareq Sefati on 18-Jun-23
 */
public class UserController {

    public static List<User> generateDummyUsers(int userNumber){
        List<User> userList = new ArrayList<>();
        for (int i = 1; i <= userNumber; i++) {
            //(String username, String userId, String email, String password)
            User user = new User("username-"+i, "userid-"+i, "test@gmail.com", "password");
            userList.add(user);
        }
        return userList;
    }
}
