package com.application.javafx_chat_messenger_application.server;
/**
 * Server Controller Class
 */

import com.application.javafx_chat_messenger_application.model.Message;
import com.application.javafx_chat_messenger_application.model.MessageType;
import com.application.javafx_chat_messenger_application.model.User;
import com.application.javafx_chat_messenger_application.model.UserController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class ServerController2 implements Initializable {
    @FXML
    private Label labelInfo;
    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    VBox activeClientsBox;
    @FXML
    private VBox usersBox;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private ScrollPane spUserList;
    private Server server;
    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, Socket> activeClientList;
    private List<String> activeClientIds;

    private ObjectOutputStream objectOutputStream;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        try{
//            server = new Server(new ServerSocket(1234));
//            System.out.println("Connected to Client!");
//        }catch(IOException e){
//            e.printStackTrace();
//            System.out.println("Error creating Server ... ");
//        }
        activeClientList = new HashMap<>();
        activeClientIds = new ArrayList<>();
        loadUsers();
        try {
            serverSocket = new ServerSocket(1234);
            labelInfo.setText("Server successfully created.");
            System.out.println("Server successfully created.");

            new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        System.out.println("Server is waiting for client...");
                        socket = serverSocket.accept();
                        System.out.println("A new client is joined: " + socket.toString());
                        //registerClient(socket);  //update the active client list in server side
                        //sendActiveClientList(activeClientList); //give command to all clients to update their activeClientListView
                        communicateWithClient(socket);
                    } catch (IOException ex){
                        ex.printStackTrace();
                        System.out.println("Server-side message: Client is failed to join.");
                        closeSocket(socket);
                    }
                }
            }).start();
        } catch (IOException e){
            e.printStackTrace();
            labelInfo.setText("Error creating Server ... ");
            System.out.println("Error creating Server ... ");
        }

        activeClientsBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double) newValue);
            }
        });
//        usersBox.heightProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                spUserList.setVvalue((Double) newValue);
//            }
//        });

        //receiveMessageFromClient();  // name may be communicateWithClient and place it after serverSocket.accept();
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

    private void communicateWithClient(Socket skt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(skt != null && skt.isConnected()){
                    try{
                        ObjectInputStream objectInputStream = new ObjectInputStream(skt.getInputStream());
//                        ObjectOutputStream objectOutputStream;// = new ObjectOutputStream(skt.getOutputStream()); // this should not be done here
                        Message message = (Message) objectInputStream.readObject();
                        if(message.getMessageType().equals(MessageType.CONNECTION)){
                            //Receive Initial message & store this new client to active user list.
                            activeClientList.put(message.getSenderId(), skt);
                            activeClientIds.add(message.getSenderId());
                            System.out.println("Connection message-server: " + message);
                            System.out.println("Active Client Number: " + activeClientList.size());
                            HBox hBox = new HBox(20);
                            TextFlow textFlow = new TextFlow(new Text(message.getSenderId()));
                            Button button = new Button("X");
                            hBox.getChildren().addAll(textFlow,button);

                            //Updating active client list in server UI
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    labelInfo.setText("A new client is joined: " + socket.toString());
                                    activeClientsBox.getChildren().add(hBox);
                                    // TODO: need to shutdown the client properly. throw a specific message based on that client program close the socket.
                                    button.setOnAction(actionEvent -> {
                                        try {
                                            socket.close();
                                            activeClientsBox.getChildren().remove(hBox);
                                            System.out.println("Shutdown:: " + socket.toString());
                                            labelInfo.setText("Shutdown:: " + socket.toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            labelInfo.setText("Failed: Shutdown instruction sent to client!");
                                        }
                                    });
                                }
                            });
                            //Send the updated active client list to new client and all other clients too.
                            System.out.println("Server: updated active client list sent to all clients.");
                            Message activeClientMessage = new Message();
                            activeClientMessage.setMessageType(MessageType.ACTIVE_CLIENTS);
                            activeClientMessage.setSenderId("SERVER");
                            activeClientMessage.setReceiverId(message.getSenderId());
                            activeClientMessage.setDataObject(activeClientIds);
                            dispatchMessageToAllClients(message);
                        } else if (message.getMessageType().equals(MessageType.PLAIN) && isValidMessage(message)){
                            System.out.println(message.toString());
                            // send this message to sender socket client.
                            if(activeClientList.containsKey(message.getReceiverId())){
                                //send message to specific client immediately
                                Socket skt = activeClientList.get(message.getReceiverId());
                                objectOutputStream = new ObjectOutputStream(skt.getOutputStream());
                                objectOutputStream.writeObject(message);
                                objectOutputStream.flush();
                                System.out.println("Message is sent to active client: " + message);
                            } else if (isFoundInClientList(message.getReceiverId())) {
                                // store the message in db and send it later
                            }
                        }
//                        String messageFromClient = bufferedReader.readLine();
//                        ServerController.addLabel(messageFromClient, vBox);
                    }catch (IOException | ClassNotFoundException e){
                        e.printStackTrace();
                        System.out.println("Error receiving message from the Client!");
                        closeSocket(skt);
                        break;
                    }
                }
            }
            private void dispatchMessageToAllClients(Message message) throws IOException {
                //ObjectOutputStream objectOutputStream;
                System.out.println("Server: updated active client list sent to all clients.");
                Message activeClientMessage = new Message();
                activeClientMessage.setMessageType(MessageType.ACTIVE_CLIENTS);
                activeClientMessage.setSenderId("SERVER");
                activeClientMessage.setReceiverId(message.getSenderId());
                activeClientMessage.setDataObject(activeClientIds);
                for (var activeClient : activeClientList.entrySet()) {
                    Socket s = activeClient.getValue();
                    objectOutputStream = new ObjectOutputStream(s.getOutputStream());
                    objectOutputStream.writeObject(activeClientMessage);
                    objectOutputStream.flush();
                }
            }
            private boolean isFoundInClientList(String receiverId) {
                return false;
            }

            private boolean isValidMessage(Message message) {
                if(message.getSenderId() != null && !message.getSenderId().isEmpty()
                    && message.getReceiverId() != null && !message.getReceiverId().isEmpty()
                        && message.getMsg() != null && !message.getMsg().isEmpty()
                            && message.getSentDateTime() != null && !message.getSenderId().equals(message.getReceiverId())) {
                    return true;
                }else {
                    return false;
                }
            }

        }).start();
    }

    private void loadUsers() {
        List<User> userList = UserController.generateDummyUsers(10);
        for(User u : userList){
            Text t = new Text(u.getUserId() + " :: " +u.getUsername());
            usersBox.getChildren().add(t);
        }
    }

    public static void addLabel(String messageFromClient, VBox vBox){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle(
                        "-fx-background-color: rgb(233, 233, 235);" +
                        "-fx-background-radius: 20px;");

        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }

}