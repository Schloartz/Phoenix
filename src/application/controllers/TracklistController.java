package application.controllers;

import application.service.Main;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import utils.Track;
import utils.TracklistCell;

import java.net.URL;
import java.util.ResourceBundle;

public class TracklistController implements Initializable{

	@FXML
	private VBox tracklistRoot;
	@FXML
	private Button arrow;
	@FXML
	private StackPane foldContainer;
	@FXML
	private HBox horizLabel;
	@FXML
	private ListView<Track> tracklist;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Main.tracklistController = this;

		tracklist.setItems(Main.tracklist.getList());
		tracklist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    tracklist.setCellFactory(p -> new TracklistCell());
	    tracklist.setOnKeyReleased(ke -> {
			if(ke.getCode()==KeyCode.DELETE) {
				ObservableList<Integer> i = tracklist.getSelectionModel().getSelectedIndices();
				if (i.get(0) != -1) { //empty tracklist
					Main.tracklist.deleteTracks(i.get(0), i.get(i.size() - 1));
				}
			}
	    });
		//start: tracklist folded
		foldTracklist();
	}
	
	@FXML
	private void setLastSelected(){
		Main.mainController.lastSelected = tracklist.getSelectionModel().getSelectedItem();
	}
	
	public void updateTracklist(){
		//force update tracklistview
		System.out.println("updatetl");
//		tracklist.setItems(null);
//		tracklist.setItems(Main.tracklist.getList());
    	//Scroll to current track
    	tracklist.scrollTo(Main.mediaplayer.getStatus().getCurrTrack());
    	//update tracksTable
    	Main.databaseController.updateTable();
	}
	@FXML
	private void foldTracklist(){
		if(arrow.getGraphic().getRotate()==180){ //open->fold it
			tracklistRoot.getChildren().remove(tracklist);
			foldContainer.getChildren().remove(horizLabel);
			arrow.getGraphic().setRotate(0);
		}else{ //closed->open it
			tracklistRoot.getChildren().add(tracklist);
			foldContainer.getChildren().add(0,horizLabel);
			arrow.getGraphic().setRotate(180);
			updateTracklist();
		}
	}

}
