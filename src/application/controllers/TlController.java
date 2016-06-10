package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.service.Main;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import utils.Track;
import utils.TracklistCell;

public class TlController implements Initializable{

	public ListView<Track> tracklist;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Main.tlController = this;
		
		tracklist.setItems(Main.tracklist.getList());
		tracklist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    tracklist.setPlaceholder(new ImageView(new Image(getClass().getResourceAsStream("/resources/icons/tracklist_placeholder.png"))));
	    tracklist.setCellFactory(new Callback<ListView<Track>, ListCell<Track>>(){
 
            @Override
            public ListCell<Track> call(ListView<Track> p) {
                 return new TracklistCell();
            }
        });
	    tracklist.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if(ke.getCode()==KeyCode.DELETE){
					ObservableList<Integer> i = tracklist.getSelectionModel().getSelectedIndices();
					if(i.get(0)!=-1){ //empty tracklist
						Main.tracklist.deleteTracks(i.get(0), i.get(i.size()-1));
					}
				}
			}
	    });
		
	}
	
	@FXML
	private void setLastSelected(){
		Main.mainController.lastSelected = tracklist.getSelectionModel().getSelectedItem();
	}
	
	public void updateTracklist(){
    	//force update tracklistview
    	tracklist.setItems(null);
    	tracklist.setItems(Main.tracklist.getList());
    	//Scroll to current track
    	tracklist.scrollTo(Main.mediaplayer.getStatus().getCurrTrack());
	}
	
}
