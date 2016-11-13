package application.controllers;

import java.net.URL;
import java.text.DateFormat;
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

public class SettingsController implements Initializable{
	@FXML
	private VBox SettingsView; 
	@FXML
	private CheckBox trackinfo, controlinfo;
	@FXML
	private StackPane menuProgressContainer;
	@FXML
	private Label menuInfo, update, complete;
	@FXML
	private ProgressBar menuProgress;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Main.settingsController = this;
		updateUpdate();
		updateComplete();
		trackinfo.selectedProperty().addListener((ov, old_val, new_val) -> Main.mainController.showTrackInfo = new_val);
		controlinfo.selectedProperty().addListener((ov, old_val, new_val) -> Main.mainController.showFlash = new_val);
	}
	
	public void updateRebuildingProgress(int searched, int max) { //updates the rebuildingProgressbar
		Platform.runLater(()-> {
			menuProgress.setProgress((double)searched/max);
			String per = new DecimalFormat("#.#").format((double)searched/max*100); //formats double to sth like 23.2%
			menuInfo.setText("Rebuilding... "+ per + " %");
		});
    	
	}
	
	@FXML
	private void rebuildDBPressed(){
		//Show progress in menu and rebuild the DB
		menuProgressContainer.setOpacity(1);
		Main.database.rebuild();
	}
	
	VBox getView(){
		return SettingsView;
	}

	public void hideProgress() {
		menuProgressContainer.setOpacity(0);
	}
	public void updateUpdate(){ //updates the label for the last time the database was incrementally built
		Platform.runLater(()-> update.setText(DateFormat.getDateInstance().format(Main.getUpdated())+" "+DateFormat.getTimeInstance().format(Main.getUpdated())));
	}
	public void updateComplete(){ //updates the label for the last time the database was fully built
		Platform.runLater(()-> complete.setText(DateFormat.getDateInstance().format(Main.getComplete())+" "+DateFormat.getTimeInstance().format(Main.getComplete())));
	}
}
