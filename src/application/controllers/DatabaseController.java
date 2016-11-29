package application.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.service.Main;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import utils.C;
import utils.Track;

	public class DatabaseController implements Initializable{
		@FXML
		private VBox DatabaseView;
		@FXML
		private TextField search;
		@FXML
		private TableView<Track> tracks;
		@FXML
		private ImageView search_close;
		
		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			Main.databaseController = this;

			//TableView tracks
			tracks.setPlaceholder(new Label("Nothing to display :("));
			tracks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			tracks.setRowFactory(tv -> new TableRow<>());
			tracks.setOnDragDetected(event -> {
				Dragboard db = tracks.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent cc = new ClipboardContent();
				ObservableList<Track> selected = tracks.getSelectionModel().getSelectedItems();
				ArrayList<Integer> dragged = new ArrayList<>();
				for(Track t:selected){
					dragged.add(t.getId());
				}
				cc.put(C.trackDataFormat, dragged);
				db.setContent(cc);
				event.consume();
			});
			//give search focus
			search.requestFocus();
			
		}
		
		@FXML
		private void searchPressed(KeyEvent ke){ //whenever search gets input
			if(search.getText().isEmpty()){
				showSearchclose(false);
			}else{
				showSearchclose(true);
			}
			if(ke.getCode().equals(KeyCode.ENTER) && !search.getText().isEmpty()){
				tracks.setItems(Main.database.search(search.getText()));
	            tracks.requestFocus(); //sets Focus to tableview
	            tracks.getSelectionModel().selectFirst(); //selects first row
	        }
		}
		
		@FXML
		private void tracksPressed(){
			Main.mainController.lastSelected = tracks.getSelectionModel().getSelectedItem();
		}
		
		@FXML
		private void tracksEnter(KeyEvent ke){
			if(ke.getCode()==KeyCode.ENTER){
				List<Track> selected = tracks.getSelectionModel().getSelectedItems();
				Main.tracklist.addTracks(selected);
			}
		}
		private void showSearchclose(boolean b){
			search_close.setVisible(b);
		}
		@FXML
		private void deleteSearch(){
			search.clear();
			showSearchclose(false);
		}
		
		VBox getView(){
			return DatabaseView;
		}
		public TextField getSearch(){
			return search;
		}

	}
