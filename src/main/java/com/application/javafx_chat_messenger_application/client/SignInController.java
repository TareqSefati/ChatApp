package com.application.javafx_chat_messenger_application.client;

import com.application.javafx_chat_messenger_application.model.ProgramDummyDB;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController {
	

    @FXML
    private TextField userIdOrEmail;

    @FXML
    private TextField userPassword;

    @FXML
    private JFXButton btnForgetPassword;

    @FXML
    private JFXButton btnSignIn;

    @FXML
    private Label labelErrorMsg;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    void forgetPasswordProcess(ActionEvent event) {
    	System.out.println("Forget password process goes here...");
    }

    @FXML
    void signInProcess(ActionEvent event) throws IOException {
        labelErrorMsg.setText("");
    	System.out.println("Sign in process goes here...");
        String id = userIdOrEmail.getText();
        String password = userPassword.getText();
        if (validateUser(id, password)) {
            root = FXMLLoader.load(getClass().getResource("clientUI.fxml"));
            scene = new Scene(root);
            Stage loginStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage = new Stage();
            stage.setTitle("Client");
            stage.setResizable(false);
            loginStage.hide();
            stage.setScene(scene);
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/com/application/javafx_chat_messenger_application/Images/Chatting-icon.png")));
            stage.show();
            labelErrorMsg.setText("Login Successful!");
            System.out.println("Login Successful!");
        }else {
            labelErrorMsg.setText("Invalid UserId or Password! Try again.");
            System.out.println("Invalid UserId or Password! Try again.");
        }
    }

    private boolean validateUser(String id, String password) {
        if (!id.isEmpty() && !password.isEmpty()){
            return ProgramDummyDB.findUser(id, password);
        }
        return false;
    }

}
