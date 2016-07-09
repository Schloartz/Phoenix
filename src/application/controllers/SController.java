package application.controllers;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import application.service.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SController implements Initializable{
	@FXML
	private VBox SettingsView; 
	@FXML
	private CheckBox trackinfo, controlinfo;
	@FXML
	private StackPane menuProgressContainer;
	@FXML
	private Label menuInfo;
	@FXML
	private ProgressBar menuProgress;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Main.sController = this;
		
		trackinfo.selectedProperty().addListener((ov, old_val, new_val) ->{
			Main.mainController.showTrackInfo = new_val;
		});
		controlinfo.selectedProperty().addListener((ov, old_val, new_val) ->{
			Main.mainController.showFlash = new_val;
		});
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
	
	@FXML
	private void rebuildDBPressed(){
		//Show progress in menu and rebuild the DB
		menuProgressContainer.setOpacity(1);
		Main.database.rebuild();
	}
	
	public VBox getView(){
		return SettingsView;
	}

	public void hideProgress() {
		menuProgressContainer.setOpacity(0);
	}

}
