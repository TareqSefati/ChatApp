package com.application.javafx_chat_messenger_application.client;

import com.application.javafx_chat_messenger_application.model.Message;
import com.application.javafx_chat_messenger_application.model.MessageType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Tareq Sefati on 19-Jun-23
 */
public class ClientController2 implements Initializable {
    @FXML
    private AnchorPane ap_main;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private VBox vBoxMessages;

    @FXML
    private ListView<String> activeClientListView;

    @FXML
    private TextField tf_message;

    @FXML
    private Button button_send;
    @FXML
    private Label labelInfo;
    private Socket socket;
    private static String userId;
    private ObjectOutputStream objectOutputStream;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket("localhost", 1234);
            System.out.println("Connected to Server");
            labelInfo.setText("Connected to Server");
            sendMessageToServer(getInitialMessage());
            communicateWithServer(socket);
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Error creating Client ... ");
            labelInfo.setText("Error creating Client ... ");
            closeSocket(socket);
        }

        vBoxMessages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double) newValue);
            }
        });

    }

    private void communicateWithServer(Socket socket) {
        receiverMessageTread(socket);
    }


    private void sendMessageToServer(Message message) {
        if (message.getMessageType().equals(MessageType.CONNECTION)){
            System.out.println("Sending Initial connection message to server.....");
        } else if (message.getMessageType().equals(MessageType.PLAIN)) {
            System.out.println("Sending general message to server.....");
        }
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            System.out.println("Client: Message sent to server successfully!");
            //objectOutputStream.close();  //don't use this. dangerous error
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Client: Message sent to server failed ... ");
            labelInfo.setText("Client: Message sent to server failed ... ");
            //closeSocket(socket); // could be implemented, i don't know - have to think
        }
    }

    private Message getInitialMessage(){
        Message message = new Message();
        userId = "Id-"+ LocalTime.now();
        message.setSenderId(userId);
        message.setMessageType(MessageType.CONNECTION);
        return message;
    }

    private void receiverMessageTread(Socket skt){
        System.out.println("Client: Starting receiver thread.");
        new Thread(() -> {
            while(skt != null && skt.isConnected()) {
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(skt.getInputStream());
                    Message message = (Message) objectInputStream.readObject();
                    System.out.println("Client: Message received: " + message.toString());
                    if (message.getMessageType().equals(MessageType.ACTIVE_CLIENTS)){
                        List<String> activeClientIds= (List<String>) message.getDataObject();
                        Platform.runLater(() -> {
                            if(message.getReceiverId().equals(userId)){
                                //insert all active clients to list view - new client
                                System.out.println("Client: updating active clients view - new client");
                                activeClientListView.getItems().addAll(activeClientIds);
                            }else {
                                //insert only last active client to list view - old client
                                System.out.println("Client: updating active clients view - old client");
                                activeClientListView.getItems().add(activeClientIds.get(activeClientIds.size() - 1));
                            }
                        });
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Client side: Message receiving failed from server.");
                    Platform.runLater(() -> {
                        labelInfo.setText("Client side: Message receiving failed from server.");
                    });
                    closeSocket(skt);
                    break;
                }
            }
        }).start();
    }

    private void closeSocket(Socket socket) {
        try{
            if (socket != null) {
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    void sendMessage(ActionEvent event) {
        //activeClientListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (activeClientListView.getSelectionModel().getSelectedItem() != null) {
            String receiverId = activeClientListView.getSelectionModel().getSelectedItem();
            if(!receiverId.equals(userId)){
                String msg = userId + " -> " + receiverId + " : " + tf_message.getText();
                Message message = new Message(userId, receiverId, msg, new Date(), MessageType.PLAIN);
                System.out.println(message.toString());
                sendMessageToServer(message);
            }

        }
    }
}
