package com.tareq.chatapp.util;

import com.tareq.chatapp.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareq Sefati on 05-Aug-23
 */
public class FileUtil {

    private static String commonDirectory = System.getProperty("user.home"); // Get the user's home directory
    private static String fileName = "chatUserRecord.txt";
    private static Path filePath = Paths.get(commonDirectory, fileName);
    static {
        try{
            Files.createFile(filePath);
            System.out.println("chatUserRecord.txt File created successfully at directory - " +commonDirectory);
        } catch (IOException e) {
            if (e instanceof FileAlreadyExistsException){
                System.out.println("File already exist.");
            }else {
                e.printStackTrace();
            }

        }
    }
    public static List<User> getRegisteredUserFromFile(){
        System.out.println("Retrieving data from file record...");
        List<User> userList = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(filePath);
            //data store pattern: username userid email password_hash
            for (String line : lines) {
                User user = new User();
                String[] words = line.split(" ");
                //user.setUsername(words[0] != null? words[0] : "");
                user.setUsername(words[0]);
                user.setUserId(words[1]);
                user.setEmail(words[2]);
                user.setPassword(words[3]);
//                for (String word : words) {
//                    System.out.println(word);
//                    user.setUsername(word);
//                }
                userList.add(user);
            }
        } catch (IOException e) {
            System.out.println("Failed to Retrieve data from file.");
            e.printStackTrace();
        }
        return userList;
    }
    public static void appendUserToFile(User user){
        System.out.println("Storing data to file...");
        //data store pattern: username userid email password_hash
        try {
            if(!user.getUsername().isEmpty() && !user.getUserId().isEmpty() && !user.getEmail().isEmpty()
                && !user.getPassword().isEmpty()){
                StringBuilder dataToWrite = new StringBuilder(user.getUsername()).append(" ");
                dataToWrite.append(user.getUserId()+" ").append(user.getEmail()+" ").append(user.getPassword()).append(System.lineSeparator());
                Files.write(filePath, dataToWrite.toString().getBytes(), StandardOpenOption.APPEND);
                System.out.println("File written successfully.");
            }

        } catch (IOException e) {
            System.out.println("Failed to write data in file.");
            e.printStackTrace();
        }
    }
    public static void deleteAllUserRecords(){
        try {
            // Overwrite the file with an empty content
            Files.write(filePath, new byte[0]);
            System.out.println("All data deleted from the file.");
        } catch (IOException e) {
            System.out.println("Failed to delete all data from the file.");
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        System.out.println("File code testing...");
//        System.out.println(getRegisteredUserFromFile());
//        //appendUserToFile(new User("usernam", "userid", "email", "password"));
//        //deleteAllUserRecords();
//    }
}
