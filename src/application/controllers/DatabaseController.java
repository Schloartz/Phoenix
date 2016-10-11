package application.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.service.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
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
			tracks.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
			tracks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			//give search focus
			search.requestFocus();
			
		}
		
		void updateTable() { //forces update of table
	    	tracks.setItems(null);
	    	tracks.layout();
	    	tracks.setItems(Main.database.getResults());
	    	//restore focus
	    	tracks.requestFocus();
	    	tracks.getSelectionModel().select(Main.mainController.lastSelected);
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
