package com.nash.phoenix.views.splash;


import com.nash.phoenix.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashPresenter implements Initializable {
    @FXML
    private ProgressBar splashProgress;
    @FXML
    private Label splashLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App.splashPresenter = this;
    }

    public ProgressBar getSplashProgress(){
        return splashProgress;
    }

    public Label getSplashLabel() {
        return splashLabel;
    }
}
