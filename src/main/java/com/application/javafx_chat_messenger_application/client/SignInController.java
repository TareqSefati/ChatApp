package com.application.javafx_chat_messenger_application.client;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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
    void forgetPasswordProcess(ActionEvent event) {
    	System.out.println("Forget password process goes here...");
    }

    @FXML
    void signInProcess(ActionEvent event) {
    	System.out.println("Sign in process goes here...");
    }

}
