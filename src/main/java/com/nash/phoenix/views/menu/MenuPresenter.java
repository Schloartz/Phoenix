package com.nash.phoenix.views.menu;

import java.net.URL;
import java.util.ResourceBundle;

import com.nash.phoenix.App;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

public class MenuPresenter implements Initializable{
	private double xOffset, yOffset;
	@FXML
	private ToggleButton musiccollection, mediaplayer, settings;
	@FXML
	private ToggleGroup view;
	
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		App.menuPresenter = this;
		
		//Changelistener for ToggleGroup
		view.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
		    public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
		       if(new_toggle==null){ //if not toggle is selected, select the last selected (equals "one toggle needs to be selected")
		    	   toggle.setSelected(true);
		       }
		    }
		});
	}
	
	

	public void toggleView(){
    	if(App.mainPresenter.view.equals("database") || App.mainPresenter.view.equals("settings")){ //Current View: musiccollection or settigns
    		switchToMediaplayer();
			settings.setSelected(false);
			mediaplayer.setSelected(true);
			musiccollection.setSelected(false);
		}else{ //Current View: mediaplayer
			switchToMusiccollection();
    		settings.setSelected(false);
    		mediaplayer.setSelected(false);
			musiccollection.setSelected(true);
		}
	}
	@FXML
	private void switchToMusiccollection(){
		if(!App.mainPresenter.view.equals("database")){
			App.mainPresenter.view = "database";
			App.getMainPresenter().setCenter("database");
			App.databasePresenter.getSearch().requestFocus();
		}
	}
	@FXML
	private void switchToMediaplayer(){
		if(!App.mainPresenter.view.equals("cover")){
			App.mainPresenter.view = "cover";
    		App.getMainPresenter().setCenter("cover");
		}
	}
	@FXML
	private void switchToSettings(){
		if(!App.mainPresenter.view.equals("settings")){
			App.mainPresenter.view = "settings";
    		App.getMainPresenter().setCenter("settings");
		}
	} 
	
	@FXML
	private void minimize(){
		App.stage.setIconified(true);
	}
	@FXML
	private void shutdown(){
		App.shutdown();
	}
	@FXML
	private void dragStarted(MouseEvent e){
		xOffset = App.stage.getX() - e.getScreenX();
		yOffset = App.stage.getY() - e.getScreenY();
	}
	@FXML
	private void dragExecuted(MouseEvent e){
		App.stage.setX(e.getScreenX() + xOffset);
        App.stage.setY(e.getScreenY() + yOffset);
	}
	
	
	
}
