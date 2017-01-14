package com.nash.phoenix.views.settings;

import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import com.nash.phoenix.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsPresenter implements Initializable{
	@FXML
	private VBox SettingsView; 
	@FXML
	private CheckBox controlinfo, numinput;
	@FXML
	private StackPane menuProgressContainer;
	@FXML
	private Label menuInfo, update, complete;
	@FXML
	private ProgressBar menuProgress;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		App.settingsPresenter = this;
		updateUpdate();
		updateComplete();
		controlinfo.selectedProperty().addListener((ov, old_val, new_val) -> App.mainPresenter.showFlash = new_val);
		numinput.selectedProperty().addListener((ov, old_val, new_val) -> App.toggleNumInput(new_val));
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
		App.database.rebuild();
	}
	
	public VBox getView(){
		return SettingsView;
	}

	public void hideProgress() {
		menuProgressContainer.setOpacity(0);
	}
	public void updateUpdate(){ //updates the label for the last time the database was incrementally built
		Platform.runLater(()-> update.setText(DateFormat.getDateInstance().format(App.getUpdated())+" "+DateFormat.getTimeInstance().format(App.getUpdated())));
	}
	public void updateComplete(){ //updates the label for the last time the database was fully built
		Platform.runLater(()-> complete.setText(DateFormat.getDateInstance().format(App.getComplete())+" "+DateFormat.getTimeInstance().format(App.getComplete())));
	}

	public void toggleNumInput() {
		if(numinput.selectedProperty().get()) {
			numinput.setSelected(false);
		}else{
			numinput.setSelected(true);
		}
	}
	public boolean numInputEnabled(){
		return numinput.isSelected();
	}
}
