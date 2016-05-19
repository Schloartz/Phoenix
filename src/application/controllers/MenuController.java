package application.controllers;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import application.service.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class MenuController implements Initializable{
	private double xOffset, yOffset;
	
	public StackPane menuProgressContainer;
	public Label menuInfo;
	public ProgressBar menuProgress;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Main.menuController = this;
		
	}
	
	@FXML
	private void rebuildDBPressed(){
		//Show progress in menu and rebuild the DB
		menuProgressContainer.setOpacity(1);
		Main.database.rebuild();
	}
	@FXML
	private void dev(){ //developer option
		
	}

	public void switchView(){
    	if(Main.mainController.mediaplayerView){ //Current View: Mediaplayer
			Main.mainController.mediaplayerView = false;
    		Main.root.setRight(null);
			Main.root.setCenter(Main.dbController.DatabaseView);
			Main.dbController.search.requestFocus();
		}else{ //Current View: Database
    		Main.mainController.mediaplayerView = true;
    		Main.root.setRight(Main.tlController.tracklist);
    		Main.root.setCenter(Main.cvController.getCoverView());
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
	
	public void updateRebuildingProgress(int searched, int max) { //updates the rebuildingProgressbar
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	menuProgress.setProgress((double)searched/max);
		    	String per = new DecimalFormat("#.#").format((double)searched/max*100); //formats double to sth like 23.2%
		    	menuInfo.setText("Rebuilding... "+ per + " %");
		    }
		});
    	
	}
	
}
