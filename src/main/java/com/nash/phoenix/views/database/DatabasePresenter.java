package com.nash.phoenix.views.database;

import com.nash.phoenix.App;
import com.nash.phoenix.utils.C;
import com.nash.phoenix.utils.Track;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

	public class DatabasePresenter implements Initializable{
		@FXML
		private TextField search;
		@FXML
		private TableView<Track> tracks;
		@FXML
		private ImageView search_close;
		
		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			App.databasePresenter = this;

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
				tracks.setItems(App.database.getRequestHandler().search(search.getText()));
	            tracks.requestFocus(); //sets Focus to tableview
	            tracks.getSelectionModel().selectFirst(); //selects first row
	        }
		}
		
		@FXML
		private void tracksPressed(){
			App.mainPresenter.lastSelected = tracks.getSelectionModel().getSelectedItem();
		}
		
		@FXML
		private void tracksEnter(KeyEvent ke){
			if(ke.getCode()==KeyCode.ENTER){
				List<Track> selected = tracks.getSelectionModel().getSelectedItems();
				App.tracklist.addTracks(selected);
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

		public TextField getSearch(){
			return search;
		}

	}
