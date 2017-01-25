package com.nash.phoenix.views.settings;

import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import com.nash.phoenix.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsPresenter implements Initializable{
	@FXML
	private VBox SettingsView; 
	@FXML
	private CheckBox controlinfo, numinput;
	@FXML
	private StackPane progressContainer;
	@FXML
	private Button rebuildButton;
	@FXML
	private Label rebuildInfo, update, complete;
	@FXML
	private ProgressBar rebuildProgress;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		App.settingsPresenter = this;
		rebuildButton.managedProperty().bind(rebuildButton.visibleProperty()); //so a hidden button does not affect the layout anymore
		updateUpdate();
		updateComplete();
		controlinfo.selectedProperty().addListener((ov, old_val, new_val) -> App.mainPresenter.showFlash = new_val);
		numinput.selectedProperty().addListener((ov, old_val, new_val) -> App.toggleNumInput(new_val));
	}
	
	public void updateRebuildingProgress(int searched, int max) { //updates the rebuildingProgressbar
		Platform.runLater(()-> {
			rebuildProgress.setProgress((double)searched/max);
			String per = new DecimalFormat("#.#").format((double)searched/max*100); //formats double to sth like 23.2%
			rebuildInfo.setText("Rebuilding... "+ per + " %");
		});
    	
	}

	/**
	 * Shows the progressbar, hides the rebuildbutton in the settings and starts the rebuilding of the database
	 */
	@FXML
	private void rebuildDBPressed(){
		progressContainer.setVisible(true);
		rebuildButton.setVisible(false);
		App.database.rebuild();
	}
	
	public VBox getView(){
		return SettingsView;
	}

	/**
	 * Hides the progressbar and shows the button again
	 */
	public void hideProgress() {
		progressContainer.setVisible(false);
		rebuildButton.setVisible(true);
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
