package com.tareq.chatapp.server;
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
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("serverUIRaw.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Server!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}