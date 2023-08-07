package com.tareq.chatapp.server;
/**
 * Server Controller Class
 */

import com.tareq.chatapp.model.*;
import com.tareq.chatapp.util.FileUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ServerController2 implements Initializable {
    @FXML
    private Label labelInfo;
    @FXML
    private Pane titlePane;
    @FXML
    private ImageView btnClose;
    @FXML
    private ImageView btnMinimize;
    @FXML
    private Button btnLoadRegisteredUser;
    @FXML
    private Button btnLoadActiveUser;
    @FXML
    private Button btnDeleteAllUser;
    @FXML
    private Button btnLoadGroupInfo;
    @FXML
    private VBox activeClientsBox;
    @FXML
    private VBox usersBox;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private ScrollPane spUserList;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        loadUsers();

        try {
            serverSocket = new ServerSocket(1234);
            labelInfo.setText("Server successfully created.");
            System.out.println("Server successfully created.");

            Thread t = new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        System.out.println("Server is waiting for client...");
                        socket = serverSocket.accept();
                        System.out.println("A new client is joined: " + socket.toString());
                        //registerClient(socket);  //todo: update the active client list in server side
                        //sendActiveClientList(activeClientList); //todo: give command to all clients to update their activeClientListView
                        communicateWithClient(socket);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        System.out.println("Server-side message: Client is failed to join.");
                        closeSocket(socket);
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
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
        Platform.runLater(() -> {
                btnLoadRegisteredUser.fire();
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
        try {
            if (socket != null) {
                socket.close();
                System.out.println(socket + "Socket is closed successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void communicateWithClient(Socket skt) {
        Thread communicateWithClientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String clientId = null;
                try {
                    while (skt != null && skt.isConnected()) {
                        ObjectInputStream objectInputStream = new ObjectInputStream(skt.getInputStream());
//                        ObjectOutputStream objectOutputStream;// = new ObjectOutputStream(skt.getOutputStream()); // this should not be done here
                        Message message = (Message) objectInputStream.readObject();
                        if (message.getMessageType().equals(MessageType.CONNECTION)) {
                            //Receive Initial message & store this new client to active user list.
                            clientId = message.getSenderId();
                            ProgramDummyDB.getActiveClientList().put(message.getSenderId(), skt);
                            ProgramDummyDB.getActiveClientIds().add(message.getSenderId());
                            System.out.println("Connection message-server: " + message);
                            System.out.println("Active Client Number: " + ProgramDummyDB.getActiveClientList().size());
                            HBox hBox = new HBox(20);
                            Text text = new Text(message.getSenderId());
                            Button button = new Button();
                            ImageView iv = new ImageView(new Image(this.getClass().getResourceAsStream("/com/tareq/chatapp/Images/logout-box-r-fill.png")));
                            iv.setFitHeight(15);
                            iv.setFitWidth(15);
                            button.setGraphic(iv);
                            hBox.getChildren().addAll(text, button);

                            //Updating active client list in server UI
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    labelInfo.setText("A new client is joined: " + skt.toString());
                                    activeClientsBox.getChildren().add(hBox);
                                    // TODO: need to shutdown the client properly. throw a specific message based on that client program close the socket.
                                    button.setOnAction(actionEvent -> {
                                        String deletedId = message.getSenderId();
                                        try {
                                            //1. send message to client to logout itself
                                            Message clientLogoutMsg = new Message();
                                            clientLogoutMsg.setMessageType(MessageType.LOGOUT_CLIENT);
                                            clientLogoutMsg.setSenderId("SERVER");
                                            clientLogoutMsg.setReceiverId(deletedId);
                                            objectOutputStream = new ObjectOutputStream(skt.getOutputStream());
                                            objectOutputStream.writeObject(clientLogoutMsg);
                                            objectOutputStream.flush();
                                            System.out.println("Message is sent to a active client to logout itself.");

                                            //2. Remove from program database
                                            ProgramDummyDB.getActiveClientList().remove(deletedId);
                                            ProgramDummyDB.getActiveClientIds().remove(deletedId);

                                            //3. dispatch remove client message to all clients.
                                            updateActiveClientListAfterRemovingClient(deletedId);

                                            //4. close this socket
                                            skt.close();
                                            activeClientsBox.getChildren().remove(hBox);
                                            System.out.println("Shutdown:: " + skt.toString());
                                            labelInfo.setText("Shutdown:: " + skt.toString());
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
                            activeClientMessage.setDataObject(ProgramDummyDB.getActiveClientIds());
                            dispatchMessageToAllClients(activeClientMessage);
                        } else if (message.getMessageType().equals(MessageType.PLAIN) && isValidMessage(message)) {
                            System.out.println(message.toString());
                            // send this message to sender socket client.
                            if (ProgramDummyDB.getActiveClientList().containsKey(message.getReceiverId())) {
                                //send message to specific client immediately
                                Socket skt = ProgramDummyDB.getActiveClientList().get(message.getReceiverId());
                                objectOutputStream = new ObjectOutputStream(skt.getOutputStream()); //(ObjectOutputStream) skt.getOutputStream(); -- this process does not work
                                objectOutputStream.writeObject(message);
                                objectOutputStream.flush();
                                System.out.println("Message is sent to active client: " + message);
                            } else if (isFoundInClientList(message.getReceiverId())) {
                                // store the message in db and send it later
                            }
                        } else if (message.getMessageType().equals(MessageType.GROUP_CREATION)) {
                            //New group created message is received and send this info to group members.
                            System.out.println("Server: New group created.\n" + message.toString());
                            ProgramDummyDB.getGroupList().add((MessageGroup) message.getDataObject());
                            sendToGroupMember(message);
                            System.out.println("Server: Group creation message successfully sent to group member.");
                        } else if (message.getMessageType().equals(MessageType.GROUP_MESSAGE) && isValidMessage(message)) {
                            System.out.println("Server: Received group message.\n" + message.toString());
                            sendToGroupMember(message);
                            System.out.println("Server: Group message successfully sent to group member.");
                        }
//                        String messageFromClient = bufferedReader.readLine();
//                        ServerController.addLabel(messageFromClient, vBox);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    closeSocket(skt);
                    if (e instanceof SocketException || e instanceof EOFException){
                        System.out.println("Some how client is disconnected.");
                        if (clientId != null){
                            System.out.println("Update server with active client list & also UI");
                            ProgramDummyDB.getActiveClientList().remove(clientId);
                            ProgramDummyDB.getActiveClientIds().remove(clientId);
                            for (Node n : activeClientsBox.getChildren()) {
                                HBox hBox = (HBox) n;
                                Text text = (Text) hBox.getChildren().get(0);
                                if (text.getText().equals(clientId)){
                                    Platform.runLater(() -> {
                                        activeClientsBox.getChildren().remove(n);
                                    });
                                    break;
                                }
                            }
                            //Send command to all clients to update their active client list.
                            updateActiveClientListAfterRemovingClient(clientId);
                        }
                    }
                    e.printStackTrace();
                    System.out.println("Error receiving message from the Client!");
                }
            }
        });
        communicateWithClientThread.setDaemon(true);
        communicateWithClientThread.start();
    }

    private void updateActiveClientListAfterRemovingClient(String removedClientId) {
        Message removeClientMessage = new Message();
        removeClientMessage.setMessageType(MessageType.REMOVE_CLIENT);
        removeClientMessage.setSenderId("SERVER");
        removeClientMessage.setReceiverId("");
        removeClientMessage.setDataObject(removedClientId);
        dispatchMessageToAllClients(removeClientMessage);
    }

    private void dispatchMessageToAllClients(Message message) {
        try {
            System.out.println("Server: Given message is sending(all clients).......");
            for (var activeClient : ProgramDummyDB.getActiveClientList().entrySet()) {
                Socket s = activeClient.getValue();
                objectOutputStream = new ObjectOutputStream(s.getOutputStream());
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            }
            System.out.println("Server: Successfully given message is sent to all clients.");
        }catch (IOException ex){
            System.out.println("Server: Failed to send given message to all clients!!!");
            ex.printStackTrace();
        }
    }

    private boolean isValidMessage(Message message) {
        if (message.getSenderId() != null && !message.getSenderId().isEmpty()
                && message.getReceiverId() != null && !message.getReceiverId().isEmpty()
                && message.getMsg() != null && !message.getMsg().isEmpty()
                && message.getSentDateTime() != null && !message.getSenderId().equals(message.getReceiverId())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isFoundInClientList(String receiverId) {
        return false;
    }

    private void sendToGroupMember(Message message) throws IOException {
        MessageGroup messageGroupDetails = (MessageGroup) message.getDataObject();
        List<String> participantIdList = messageGroupDetails.getParticipantIdList();
        for (String id : participantIdList) {
            if (!id.equals(message.getSenderId()) && ProgramDummyDB.getActiveClientList().containsKey(id)){
                Socket skt = ProgramDummyDB.getActiveClientList().get(id);
                objectOutputStream = new ObjectOutputStream(skt.getOutputStream());
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            }
        }
    }

    private void loadUsers() {
        List<User> userList = UserController.generateDummyUsers(10);
        for (User u : userList) {
            Text t = new Text(u.getUserId() + " :: " + u.getUsername());
            usersBox.getChildren().add(t);
        }
    }

    public static void addLabel(String messageFromClient, VBox vBox) {
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

    private void activateTitleDragFunctionality(Stage stage){
        titlePane.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });
        titlePane.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - x);
            stage.setY(mouseEvent.getScreenY() - y);
        });
        btnClose.setOnMouseClicked(mouseEvent -> {
            Platform.exit();
        });
        btnMinimize.setOnMouseClicked(mouseEvent -> stage.setIconified(true));
    }
    @FXML
    void loadActiveUser(ActionEvent event) {
        System.out.println("Loading active users");
        usersBox.getChildren().clear();
        usersBox.setAlignment(Pos.CENTER_LEFT);
        Text t = new Text("Active Users");
        t.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 13));
        usersBox.getChildren().add(t);
        int i = 1;
        for (String activeClientId : ProgramDummyDB.getActiveClientIds()) {
            t = new Text(i++ +". " + activeClientId);
            t.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 12));
            usersBox.getChildren().add(t);
        }
    }
    @FXML
    void loadRegisteredUser(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        activateTitleDragFunctionality(stage);
        System.out.println("Loading registered users");
        usersBox.getChildren().clear();
        usersBox.setAlignment(Pos.CENTER_LEFT);
        Text t = new Text("Registered Users");
        t.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 13));
        usersBox.getChildren().add(t);
        int i = 1;
        for (User user : ProgramDummyDB.getUserList()) {
            t = new Text(i++ +". " + user.getUsername());
            t.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 12));
            usersBox.getChildren().add(t);
        }
    }
    @FXML
    void deleteAllUser(ActionEvent event) {
        System.out.println("Deleting all registered users");
        ProgramDummyDB.deleteAllRegisteredUser();
    }
    @FXML
    void loadGroupInfo(ActionEvent event) {
        System.out.println("Loading Group info");
        usersBox.getChildren().clear();
        usersBox.setAlignment(Pos.CENTER_LEFT);
        Text t = new Text("Group info");
        t.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 13));
        usersBox.getChildren().add(t);
        int i = 1;
        for (MessageGroup group : ProgramDummyDB.getGroupList()) {
            t = new Text(i++ +". " + group.getGroupName() + " - (" + group.getParticipantIdList().size() + " user)");
            t.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 12));
            usersBox.getChildren().add(t);
        }
    }
}