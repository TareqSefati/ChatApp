package com.application.javafx_chat_messenger_application.client;

import com.application.javafx_chat_messenger_application.model.ProgramDummyDB;
import com.application.javafx_chat_messenger_application.model.User;
import com.application.javafx_chat_messenger_application.util.PasswordUtil;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {


    @FXML
    private TextField username;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private JFXButton btnSignUp;

    @FXML
    private Label labelMsg;
    private Parent fxml;

    @FXML
    void signUpProcess(ActionEvent event) {
    	System.out.println("Sign up process goes here...");
        //write validation code here to validate user registration
        if (!email.getText().isEmpty() && !username.getText().isEmpty() && !password.getText().isEmpty()){
            User user = new User();
            user.setUserId(email.getText());
            user.setEmail(email.getText());
            user.setUsername(username.getText());
            user.setPassword(PasswordUtil.hashPassword(password.getText()));
            ProgramDummyDB.getUserList().add(user);
            labelMsg.setStyle("-fx-text-fill: WHITE");
            labelMsg.setText("Registration Successful! Go to SignIn.");
            email.clear();
            username.clear();
            password.clear();
        }else {
            labelMsg.setStyle("-fx-text-fill: #F78C7B");
            labelMsg.setText("Registration Failed! Try again with valid data.");
        }
    }
}
