package com.application.javafx_chat_messenger_application.server;
/**
 * Server Main Class
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("loading server ...");
//        Parent root = FXMLLoader.load(getClass().getResource("serverUI-basic-design.fxml"));
//        primaryStage.setTitle("Server!");
//        primaryStage.setScene(new Scene(root, 480, 400));
//        primaryStage.show();

        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("serverUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Server!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}