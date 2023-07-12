package com.application.javafx_chat_messenger_application.client;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SignUpController {


    @FXML
    private TextField username;

    @FXML
    private TextField email;

    @FXML
    private TextField password;

    @FXML
    private JFXButton btnSignUp;

    @FXML
    void signUpProcess(ActionEvent event) {
    	System.out.println("Sign up process goes here...");
    }
}
