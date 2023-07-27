package com.application.javafx_chat_messenger_application.client;

import com.application.javafx_chat_messenger_application.model.ProgramDummyDB;
import com.application.javafx_chat_messenger_application.model.User;
import com.application.javafx_chat_messenger_application.util.PasswordUtil;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController {
	

    @FXML
    private TextField userIdOrEmail;

    @FXML
    private PasswordField userPassword;

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
        String email = userIdOrEmail.getText();
        String password = PasswordUtil.hashPassword(userPassword.getText());
        User user = getUser(email, password);
        if (user != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("clientUI1.fxml"));
            root = loader.load();
            ClientController2 controller2 = (ClientController2) loader.getController();
            controller2.initializeCurrentUser(user);
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

    private User getUser(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()){
            return ProgramDummyDB.findUser(email, password);
        }
        return null;
    }

}
