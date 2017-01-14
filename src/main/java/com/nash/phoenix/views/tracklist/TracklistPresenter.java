package com.nash.phoenix.views.tracklist;

import com.nash.phoenix.App;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.nash.phoenix.utils.C;
import com.nash.phoenix.utils.Track;
import com.nash.phoenix.utils.TracklistCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TracklistPresenter implements Initializable{

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
		App.tracklistPresenter = this;

		tracklist.setItems(App.tracklist.getList());
		tracklist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    tracklist.setCellFactory(p -> new TracklistCell());
	    tracklist.setOnKeyReleased(ke -> {
			if(ke.getCode()==KeyCode.DELETE) {
				ObservableList<Integer> i = tracklist.getSelectionModel().getSelectedIndices();
				if (i.get(0) != -1) { //empty tracklist
					App.tracklist.deleteTracks(i.get(0), i.get(i.size() - 1));
				}
			}
	    });
		//Drag and drop
		tracklist.setOnDragOver(event -> {
			Dragboard db = event.getDragboard();
			if (db.hasContent(C.trackDataFormat)) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					event.consume();
			}
		});

		tracklist.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			if (db.hasContent(C.trackDataFormat)) {
				App.tracklist.addTracks((ArrayList<Integer>) db.getContent(C.trackDataFormat));
				event.setDropCompleted(true);
				event.consume();
			}
		});
		//start: tracklist folded
		foldTracklist();
	}
	
	@FXML
	private void setLastSelected(){
		App.mainPresenter.lastSelected = tracklist.getSelectionModel().getSelectedItem();
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
		}
	}

}
