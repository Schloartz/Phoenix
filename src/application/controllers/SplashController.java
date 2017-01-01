package application.controllers;


import application.service.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {
    @FXML
    private ProgressBar splashProgress;
    @FXML
    private Label splashLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.splashController = this;


    }

    public ProgressBar getSplashProgress(){
        return splashProgress;
    }

    public Label getSplashLabel() {
        return splashLabel;
    }
}
