package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.service.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

public class MenuController implements Initializable{
	private double xOffset, yOffset;
	@FXML
	private ToggleButton musiccollection, mediaplayer, settings;
	@FXML
	private ToggleGroup view;
	
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Main.menuController = this;
		
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
    	if(Main.mainController.view.equals("musiccollection") || Main.mainController.view.equals("settings")){ //Current View: musiccollection or settigns
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
		if(!Main.mainController.view.equals("musiccollection")){
			Main.mainController.view = "musiccollection";
			Main.getRoot().setCenter(Main.databaseController.getView());
			Main.databaseController.getSearch().requestFocus();
		}
	}
	@FXML
	private void switchToMediaplayer(){
		if(!Main.mainController.view.equals("mediaplayer")){
			Main.mainController.view = "mediaplayer";
    		Main.getRoot().setCenter(Main.coverviewController.getCoverView());
		}
	}
	@FXML
	private void switchToSettings(){
		if(!Main.mainController.view.equals("settings")){
			Main.mainController.view = "settings";
    		Main.getRoot().setCenter(Main.settingsController.getView());
		}
	} 
	
	@FXML
	private void minimize(){
		Main.stage.setIconified(true);
	}
	@FXML
	private void shutdown(){
		Main.shutdown();
	}
	@FXML
	private void dragStarted(MouseEvent e){
		xOffset = Main.stage.getX() - e.getScreenX();
		yOffset = Main.stage.getY() - e.getScreenY();
	}
	@FXML
	private void dragExecuted(MouseEvent e){
		Main.stage.setX(e.getScreenX() + xOffset);
        Main.stage.setY(e.getScreenY() + yOffset);
	}
	
	
	
}
