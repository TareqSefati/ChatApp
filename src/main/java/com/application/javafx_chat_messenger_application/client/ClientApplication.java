package com.application.javafx_chat_messenger_application.client;
/**
 * Client Main Class
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ClientApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        //Parent root = FXMLLoader.load(getClass().getResource("clientUI.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        //primaryStage.setTitle("Client!");
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/com/application/javafx_chat_messenger_application/Images/Chatting-icon.png")));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
