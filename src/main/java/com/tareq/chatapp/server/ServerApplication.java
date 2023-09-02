package com.tareq.chatapp.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by Tareq Sefati on 19-Jun-23
 */
public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("loading server ...");
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("serverUIRaw.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Server!");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}