package com.application.javafx_chat_messenger_application.client;

import com.application.javafx_chat_messenger_application.model.Message;
import com.application.javafx_chat_messenger_application.model.MessageGroup;
import com.application.javafx_chat_messenger_application.model.MessageType;
import com.application.javafx_chat_messenger_application.model.ProgramDummyDB;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;

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
    private ListView<MessageGroup> groupListView;

    @FXML
    private TextField tf_message;

    @FXML
    private Button button_send;

    @FXML
    private Button btnCreateGroup;

    @FXML
    private Button btnDeleteGroup;

    @FXML
    private Label labelClientName;

    @FXML
    private Label labelInfo;
    private Socket socket;
    private static String userId;
    private ObjectOutputStream objectOutputStream;
    private Map<Pair<String, String>, VBox> userConversationMap = new HashMap<>();
    private List<MessageGroup> messageGroupList = new ArrayList<>();
    private Map<MessageGroup, VBox> groupConversationMap = new HashMap<>();
    private boolean isDataBackedUp = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket("localhost", 1234);
            System.out.println("Connected to Server");
            labelInfo.setText("Connected to Server");
            userId = "Id-"+ LocalTime.now();
            labelClientName.setText(labelClientName.getText() + " - " + userId);
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
        activeClientListView.getSelectionModel().selectedItemProperty().addListener( (observable, oldVal, newVal) -> {
            if (newVal != null){
                labelInfo.setText("Connected to Server  -  Chat with: " + activeClientListView.getSelectionModel().getSelectedItem());
            }else {
                labelInfo.setText("Connected to Server.");
            }
            if (oldVal != null){
                //Backup old conversation
                Pair<String, String> pairOld = new Pair<>(userId, oldVal);
                VBox oldMessages = new VBox();
                if (isDataBackedUp){
                    System.out.println(userId + ": Backup last selected individual conversation(empty data) - " + oldVal);
                    isDataBackedUp = false;
                }else {
                    oldMessages.getChildren().addAll(vBoxMessages.getChildren());
                    if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(pairOld)){
                        ProgramDummyDB.getUserWiseConversationMap().put(pairOld, oldMessages);
                    }else {
                        ProgramDummyDB.getUserWiseConversationMap().replace(pairOld, oldMessages);
                    }
                    System.out.println(userId + ": Backup last selected individual conversation(data) - " + oldVal);
                    vBoxMessages.getChildren().clear();
                }
            }
//            else {
//                //check into groupListView, is there anything to backup -> backup and clear selection
//                if (groupListView.getSelectionModel().getSelectedItem() != null){
//                    Pair<String, String> selectedGroupPair = new Pair<>(userId, groupListView.getSelectionModel().getSelectedItem().getGroupHash());
//                    VBox vBox = new VBox();
//                    vBox.getChildren().addAll(vBoxMessages.getChildren());
//                    if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(selectedGroupPair)){
//                        ProgramDummyDB.getUserWiseConversationMap().put(selectedGroupPair, vBox);
//                    }else {
//                        ProgramDummyDB.getUserWiseConversationMap().replace(selectedGroupPair, vBox);
//                    }
//                    System.out.println(userId + ": Backup last selected group conversation(activeClientListView) - " + groupListView.getSelectionModel().getSelectedItem().getGroupName());
//                    vBoxMessages.getChildren().clear();
//                }
//                groupListView.getSelectionModel().clearSelection();
//            }
            //Restore new conversation
            if (newVal != null) {
                //check into groupListView, is there anything to back up -> backup and clear selection
                if (groupListView.getSelectionModel().getSelectedItem() != null){
                    Pair<String, String> selectedGroupPair = new Pair<>(userId, groupListView.getSelectionModel().getSelectedItem().getGroupHash());
                    VBox vBox = new VBox();
                    vBox.getChildren().addAll(vBoxMessages.getChildren());
                    if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(selectedGroupPair)){
                        ProgramDummyDB.getUserWiseConversationMap().put(selectedGroupPair, vBox);
                    }else {
                        ProgramDummyDB.getUserWiseConversationMap().replace(selectedGroupPair, vBox);
                    }
                    System.out.println(userId + ": Backup last selected group conversation(activeClientListView) - " + groupListView.getSelectionModel().getSelectedItem().getGroupName());
                    vBoxMessages.getChildren().clear();
                    isDataBackedUp = true;
                }
                Pair<String, String> pairNew = new Pair<>(userId, newVal);
                if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(pairNew)) {
                    ProgramDummyDB.getUserWiseConversationMap().put(pairNew, new VBox());
                    System.out.println(userId + ": Restore selected individual conversation(empty data) - " + newVal);
                } else {
                    vBoxMessages.getChildren().addAll(ProgramDummyDB.getUserWiseConversationMap().get(pairNew));
                    System.out.println(userId + ": Restore selected individual conversation(data) - " + newVal);
                }

            }
            //For clear and focus message text field for every client selection.
            Platform.runLater(() -> {
                tf_message.clear();
                tf_message.requestFocus();
            });
        });

        groupListView.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null){
                labelInfo.setText("Connected to Server  -  Chat with group: " + groupListView.getSelectionModel().getSelectedItem().getGroupName());
            }else {
                labelInfo.setText("Connected to Server.");
            }
            //Backup old conversation
            if (oldVal != null){
                Pair<String, String> pairOld = new Pair<>(userId, oldVal.getGroupHash());
                VBox oldMessages = new VBox();
                if (isDataBackedUp){
                    System.out.println(userId + ": Backup last selected group conversation(empty data) - " + oldVal.getGroupName());
                    isDataBackedUp = false;
                }else {
                    oldMessages.getChildren().addAll(vBoxMessages.getChildren());
                    if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(pairOld)){
                        ProgramDummyDB.getUserWiseConversationMap().put(pairOld, oldMessages);
                    }else {
                        ProgramDummyDB.getUserWiseConversationMap().replace(pairOld, oldMessages);
                    }
                    System.out.println(userId + ": Backup last selected group conversation(data) - " + oldVal.getGroupName());
                    vBoxMessages.getChildren().clear();
                }
            }
//            else {
//                //check into activeClientListView, is there anything to back up -> backup and clear selection
//                if (activeClientListView.getSelectionModel().getSelectedItem() != null){
//                    Pair<String, String> selectedClientPair = new Pair<>(userId, activeClientListView.getSelectionModel().getSelectedItem());
//                    VBox vBox = new VBox();
//                    vBox.getChildren().addAll(vBoxMessages.getChildren());
//                    if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(selectedClientPair)){
//                        ProgramDummyDB.getUserWiseConversationMap().put(selectedClientPair, vBox);
//                    }else {
//                        ProgramDummyDB.getUserWiseConversationMap().replace(selectedClientPair, vBox);
//                    }
//                    System.out.println(userId + ": Backup last selected individual conversation(groupListView) - " + activeClientListView.getSelectionModel().getSelectedItem());
//                    vBoxMessages.getChildren().clear();
//                }
//                groupListView.getSelectionModel().clearSelection();
//            }
            //Restore new conversation
            if (newVal != null){
                //check into activeClientListView, is there anything to back up -> backup and clear selection
                if (activeClientListView.getSelectionModel().getSelectedItem() != null){
                    Pair<String, String> selectedClientPair = new Pair<>(userId, activeClientListView.getSelectionModel().getSelectedItem());
                    VBox vBox = new VBox();
                    vBox.getChildren().addAll(vBoxMessages.getChildren());
                    if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(selectedClientPair)){
                        ProgramDummyDB.getUserWiseConversationMap().put(selectedClientPair, vBox);
                    }else {
                        ProgramDummyDB.getUserWiseConversationMap().replace(selectedClientPair, vBox);
                    }
                    System.out.println(userId + ": Backup last selected individual conversation(groupListView) - " + activeClientListView.getSelectionModel().getSelectedItem());
                    vBoxMessages.getChildren().clear();
                    isDataBackedUp = true;
                }
                Pair<String, String> pairNew = new Pair<>(userId, newVal.getGroupHash());
                if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(pairNew)){
                    ProgramDummyDB.getUserWiseConversationMap().put(pairNew, new VBox());
                    System.out.println(userId + ": Restore selected group conversation(empty data) - " + newVal.getGroupName());
                }else {
                    vBoxMessages.getChildren().addAll(ProgramDummyDB.getUserWiseConversationMap().get(pairNew));
                    System.out.println(userId + ": Restore selected group conversation(data) - " + newVal.getGroupName());
                }
            }
            //For clear and focus message text field for every group selection.
            Platform.runLater(() -> {
                tf_message.clear();
                tf_message.requestFocus();
            });
        });

        groupListView.focusedProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal){
                activeClientListView.getSelectionModel().clearSelection();
            }
        });

        activeClientListView.focusedProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal){
                groupListView.getSelectionModel().clearSelection();
            }
        });

//        if (groupListView.getSelectionModel().getSelectedItem() != null){
//            System.out.println(userId + ": Group selected - " + groupListView.getSelectionModel().getSelectedItem().getGroupName());
//        }
//        if (activeClientListView.getSelectionModel().getSelectedItem() != null){
//            System.out.println(userId + ": Individual conversation selected - " + activeClientListView.getSelectionModel().getSelectedItem());
//        }

//        if (!activeClientListView.isFocused() && groupListView.getSelectionModel().getSelectedItem() != null){
//            if (activeClientListView.getSelectionModel().getSelectedItem() != null){
//                Pair<String, String> pair = new Pair<>(userId, activeClientListView.getSelectionModel().getSelectedItem());
//                VBox vBox = new VBox();
//                vBox.getChildren().addAll(vBoxMessages.getChildren());
//                ProgramDummyDB.getUserWiseConversationMap().replace(pair, vBox);
//                vBoxMessages.getChildren().clear();
//            }
//            //activeClientListView.getSelectionModel().clearSelection();
//        }
//        if (!groupListView.isFocused() && activeClientListView.getSelectionModel().getSelectedItem() != null){
//            if(groupListView.getSelectionModel().getSelectedItem() != null){
//                Pair<String, String> pair = new Pair<>(userId, groupListView.getSelectionModel().getSelectedItem().getGroupHash());
//                VBox vBox = new VBox();
//                vBox.getChildren().addAll(vBoxMessages.getChildren());
//                ProgramDummyDB.getUserWiseConversationMap().replace(pair, vBox);
//                vBoxMessages.getChildren().clear();
//            }
//            //groupListView.getSelectionModel().clearSelection();
//        }

        groupListView.setCellFactory(messageGroupListView -> new ListCell<MessageGroup>(){
            private InputStream imageStream = this.getClass().getResourceAsStream("/com/application/javafx_chat_messenger_application/Images/team-fill.png");
            private Image groupIconImage = new Image(imageStream);
            private ImageView imageView = new ImageView();
            private Label groupName = new Label();
            private HBox layout = new HBox(10); //todo: further UI development by adding layout in list view
            @Override
            protected void updateItem(MessageGroup messageGroup, boolean empty) {
                super.updateItem(messageGroup, empty);
//                layout.setAlignment(Pos.CENTER);
//                layout.setStyle("-fx-background-color: green;");
                if (empty || messageGroup == null){
                    setGraphic(null);
                } else {
                    setText(messageGroup.getGroupName());
//                    groupName.setText(messageGroup.getGroupName());
                    imageView.setFitHeight(15);
                    imageView.setFitWidth(15);
                    imageView.setPreserveRatio(true);
                    imageView.setImage(groupIconImage);
//                    layout.getChildren().addAll(imageView, groupName);
                    setGraphic(imageView);
                    //setStyle("-fx-background-color: green;");
                }
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
        } else if (message.getMessageType().equals(MessageType.GROUP_CREATION)) {
            System.out.println("Sending Group creation message to server.....");
        } else if (message.getMessageType().equals(MessageType.GROUP_MESSAGE)) {
            System.out.println("Sending Group message to server.....");
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
        message.setSenderId(userId);
        message.setMessageType(MessageType.CONNECTION);
        return message;
    }

    private void receiverMessageTread(Socket skt){
        System.out.println("Client: Starting receiver thread.");
        Thread t = new Thread(() -> {
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
                                activeClientListView.getItems().remove(activeClientListView.getItems().size() - 1);
                            }else {
                                //insert only last active client to list view - old client
                                System.out.println("Client: updating active clients view - old client");
                                activeClientListView.getItems().add(activeClientIds.get(activeClientIds.size() - 1));
                            }
                        });
                    } else if (message.getMessageType().equals(MessageType.REMOVE_CLIENT)) {
                        System.out.println("Client: Removing a client from active client list.");
                        Platform.runLater(() -> {
                            activeClientListView.getItems().remove((String) message.getDataObject());
                            activeClientListView.refresh();
                        });
                    } else if (message.getMessageType().equals(MessageType.PLAIN)) {
                        System.out.println("Client: Plain general message received successfully.\n" + message.toString());
                        Pair<String, String> msgReceivedConversationPair = new Pair<>(userId, message.getSenderId());
                        if (activeClientListView.getSelectionModel().getSelectedItem() != null &&
                                activeClientListView.getSelectionModel().getSelectedItem().equals(message.getSenderId())){
                            //Update the message box UI
                            updateMessagesUI(message, Pos.CENTER_LEFT, true);
                        }else {
                            //Store message in userWiseConversationMap
                            if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(msgReceivedConversationPair)){
                                ProgramDummyDB.getUserWiseConversationMap().put(msgReceivedConversationPair, new VBox()); //adding new user conversation map
                            }
                            updateMessagesUI(message, Pos.CENTER_LEFT, false);
                        }

                    } else if (message.getMessageType().equals(MessageType.GROUP_CREATION)) {
                        MessageGroup messageGroupDetails = (MessageGroup) message.getDataObject();
                        ProgramDummyDB.checkUserAndAddGroup(userId, messageGroupDetails);
                        ProgramDummyDB.getUserWiseConversationMap().put(new Pair<>(userId, messageGroupDetails.getGroupHash()), new VBox());
                        //groupConversationMap.put(messageGroupDetails, new VBox());
                        Platform.runLater(() -> {
                            groupListView.getItems().add(messageGroupDetails);
                            labelInfo.setText("New message group is created: " + messageGroupDetails.getGroupName());
                        });
                    } else if (message.getMessageType().equals(MessageType.GROUP_MESSAGE)) {
                        System.out.println("Client: Group message received successfully.\n" + message.toString());
                        Pair<String, String> groupMsgReceivedPair = new Pair<>(userId, message.getConversationHash());
                        MessageGroup msgGroup = groupListView.getSelectionModel().getSelectedItem();
                        if (msgGroup != null && message.getConversationHash().equals(msgGroup.getGroupHash())){
                            //Update the message box UI instantly
                            updateMessagesUI(message, Pos.CENTER_LEFT, true);
                        } else {
                            //Store message in userWiseConversationMap
                            if (!ProgramDummyDB.getUserWiseConversationMap().containsKey(groupMsgReceivedPair)){
                                ProgramDummyDB.getUserWiseConversationMap().put(groupMsgReceivedPair, new VBox());
                            }
                            updateMessagesUI(message, Pos.CENTER_LEFT, false);
                        }
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
        });
        t.setDaemon(true);
        t.start();
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

    private void updateMessagesUI(Message message, Pos alignmentPosition, boolean isMainMessageUI){
        HBox hBox = new HBox();
        hBox.setAlignment(alignmentPosition);
        Text text = new Text();
        TextFlow textFlow = new TextFlow(text);
        if(alignmentPosition.equals(Pos.CENTER_RIGHT)){
            text.setText(message.getMsg());
            hBox.setPadding(new Insets(5, 5, 5, 35));
            textFlow.setPadding(new Insets(5, 5, 5, 5));
            textFlow.setStyle(
                    "-fx-color: rgb(239, 242, 255);" +
                            "-fx-background-color: rgb(15, 125, 242);" +
                            "-fx-background-radius: 10px;");
        } else if (alignmentPosition.equals(Pos.CENTER_LEFT)) {
            text.setText(message.getSenderId()+ "->"+ message.getMsg());
            hBox.setPadding(new Insets(5, 35, 5, 5));
            textFlow.setPadding(new Insets(5, 5, 5, 5));
            textFlow.setStyle(
                    "-fx-background-color: rgb(233, 233, 235);" +
                            "-fx-background-radius: 10px;");
        }

        hBox.getChildren().add(textFlow);
        if (isMainMessageUI){
            Platform.runLater(() -> {
                vBoxMessages.getChildren().add(hBox);
            });
        }else {
            Pair<String, String> msgReceivedConversationPair;
            if (message.getMessageType().equals(MessageType.PLAIN)){
                msgReceivedConversationPair = new Pair<>(userId, message.getSenderId());
                ProgramDummyDB.getUserWiseConversationMap().get(msgReceivedConversationPair).getChildren().add(hBox);
            } else if (message.getMessageType().equals(MessageType.GROUP_MESSAGE)) {
                msgReceivedConversationPair = new Pair<>(userId, message.getConversationHash());
                ProgramDummyDB.getUserWiseConversationMap().get(msgReceivedConversationPair).getChildren().add(hBox);
            }
        }
    }

    @FXML
    void sendMessage(ActionEvent event) {
        //activeClientListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (activeClientListView.getSelectionModel().getSelectedItem() != null) {
            System.out.println("Client: Sending individual message");
            String receiverId = activeClientListView.getSelectionModel().getSelectedItem();
            if(!receiverId.equals(userId) && !tf_message.getText().isEmpty()){
                String msg = tf_message.getText();
                Message message = new Message(userId, receiverId, msg, new Date(), MessageType.PLAIN);
                System.out.println(message.toString());
                sendMessageToServer(message);
                updateMessagesUI(message, Pos.CENTER_RIGHT, true);
                tf_message.clear();
            }
        } else if (groupListView.getSelectionModel().getSelectedItem() != null) {
            System.out.println("Client: Sending group message");
            MessageGroup messageGroup = groupListView.getSelectionModel().getSelectedItem();
            if (!tf_message.getText().isEmpty()) {
                Message message = new Message(userId, "Group-Member", tf_message.getText(), new Date(), MessageType.GROUP_MESSAGE);
                message.setConversationHash(messageGroup.getGroupHash());
                message.setDataObject(messageGroup);
                sendMessageToServer(message);
                updateMessagesUI(message, Pos.CENTER_RIGHT, true);
                tf_message.clear();
            }
        }
    }

    @FXML
    void createGroup(ActionEvent event) {
        Node node = (Node) event.getSource();
//        Stage stage = (Stage) node.getScene().getWindow();
        Stage groupCreationStage = new Stage();
        groupCreationStage.initModality(Modality.WINDOW_MODAL);
        groupCreationStage.initOwner(node.getScene().getWindow());
        groupCreationStage.setTitle("Create new message group");

        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(350, 500);
        ListView<String> listView = new ListView<>(activeClientListView.getItems()); //Populating listview
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TextField groupNameTf = new TextField();
        groupNameTf.setAlignment(Pos.CENTER);
        //groupNameTf.setPrefSize(100, 40);
        Button btnCreateGroup = new Button("Create Group");
        Button btnCancel = new Button("Cancel");
        btnCancel.setOnAction(actionEvent -> {
            groupCreationStage.close();
        });
        btnCreateGroup.setOnAction(actionEvent -> {
            if (groupNameTf.getText() != null && listView.getSelectionModel().getSelectedItems().size() > 1){
                String gName = groupNameTf.getText();
                List<String> groupMemberList = new ArrayList<>(listView.getSelectionModel().getSelectedItems());
                groupMemberList.add(userId); //adding myself to group as member.
                List<String> groupAdminList = new ArrayList<>();
                groupAdminList.add(userId); //adding myself to group as admin.
                System.out.println(gName);
                System.out.println(groupMemberList);
                //groupListView.getItems().add(gName);
                groupCreationStage.close();
                System.out.println("New group is created.");
                MessageGroup messageGroupDetails = new MessageGroup("gId-"+gName, gName, "gHash-"+gName, new Date(),
                        groupMemberList, groupAdminList);
                ProgramDummyDB.checkUserAndAddGroup(userId, messageGroupDetails);
                ProgramDummyDB.getUserWiseConversationMap().put(new Pair<>(userId, messageGroupDetails.getGroupHash()), new VBox());
                //groupConversationMap.put(messageGroupDetails, new VBox());
                groupListView.getItems().add(messageGroupDetails);
                labelInfo.setText("New message group is created: " + gName);
                Message groupCreationMessage = new Message(userId, "SERVER", "New group creation",
                        new Date(), MessageType.GROUP_CREATION);
                groupCreationMessage.setDataObject(messageGroupDetails);
                System.out.println("Client: New group creating message is sent to server.\n" + groupCreationMessage);
                sendMessageToServer(groupCreationMessage);
            }
        });
        HBox hBox = new HBox(100, btnCreateGroup, btnCancel);
        hBox.setAlignment(Pos.CENTER);
        container.getChildren().addAll(listView, groupNameTf, hBox);
        groupCreationStage.setScene(new Scene(container));
        groupCreationStage.show();
    }

    @FXML
    void deleteGroup(ActionEvent event) {
//        String selectedGroup = groupListView.getSelectionModel().getSelectedItem();
//        if (selectedGroup != null){
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Delete");
//            alert.setHeaderText(null);
//            alert.setContentText("Are you sure to delete message group : " + selectedGroup + " ?");
//            Optional<ButtonType> buttonType = alert.showAndWait();
//            if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)){
//                groupListView.getItems().remove(selectedGroup);
//                System.out.println(selectedGroup + " is removed successfully.");
//                labelInfo.setText(selectedGroup + " is removed successfully.");
//                groupListView.getSelectionModel().clearSelection();
//            }else {
//                groupListView.getSelectionModel().clearSelection();
//            }
//
//        }
    }
}
