package application.controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import application.service.Main;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import utils.Track;

	public class DbController implements Initializable{
		
		public VBox DatabaseView;
		public TextField search;
		@FXML
		private TableView<Track> tracks;
		
	
		
		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			Main.dbController = this;

			//TableView tracks
			tracks.setPlaceholder(new Label("Nothing to display :("));
			tracks.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
			tracks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			initTableColumns();
			
		}
		
		public void updateTable() { //TODO updates the table when songs have been added/removed
			//tracks which are in tracklist are displayed in bold
			
		}
		
		@FXML
		private void searchPressed(KeyEvent ke){
			if (ke.getCode().equals(KeyCode.ENTER) && !search.getText().isEmpty())
	        {
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
				for(Track t:selected){
					Main.tracklist.addTrack(t);
					Main.tlController.updateTracklist();
				}
			}
		}
		
		
		private void initTableColumns() {
			//Callback
			Callback<TableColumn<Track, String>, TableCell<Track, String>> cellFactory =
			        new Callback<TableColumn<Track, String>, TableCell<Track, String>>() {
			            public TableCell call(TableColumn p) {
			                TableCell cell = new TableCell<Track, String>() { //not sure what this all is doing (thx stackoverflow :D)
			                    @Override
			                    public void updateItem(String item, boolean empty) {
			                        super.updateItem(item, empty);
			                        if(item==null){
			                        	setText(null);
			                        	setContextMenu(null);
			                        }else{
			                        	setText(item);
			                        	setContextMenu(Main.contextMenu);
			                        }
			                        setGraphic(null);
			                    }
			                };

			                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { //add to Tracklist
			                    @Override
			                    public void handle(MouseEvent event) {
			                        if (event.getClickCount() > 1) {
			                            TableCell c = (TableCell) event.getSource();
			                            Main.tracklist.addTrack((Track) c.getTableRow().getItem());
			                            Main.tlController.updateTracklist();
			                        }
			                    }
			                });

			                return cell;
			            }
			        };
			
			Callback<TableColumn<Track, Integer>, TableCell<Track, Integer>> cellFactoryInteger =
			        new Callback<TableColumn<Track, Integer>, TableCell<Track, Integer>>() {
			            public TableCell call(TableColumn p) {
			                TableCell cell = new TableCell<Track, Integer>() { //not sure what this all is doing (thx stackoverflow :D)
			                    @Override
			                    public void updateItem(Integer item, boolean empty) {
			                        super.updateItem(item, empty);
			                        if(item==null){
			                        	setText(null);
			                        	setContextMenu(null);
			                        }else{
			                        	setText(item.toString());
			                        	setContextMenu(Main.contextMenu);
			                        }
			                        setGraphic(null);
			                    }
			
			                };
			
			                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { //add to Tracklist
			                    @Override
			                    public void handle(MouseEvent event) {
			                        if (event.getClickCount() > 1) {
			                            TableCell c = (TableCell) event.getSource();;
			                            Main.tracklist.addTrack((Track) c.getTableRow().getItem());
			                            Main.tlController.updateTracklist();
			                        }
			                    }
			                });
			
			                return cell;
			            }
			        };
			        
	        Callback<TableColumn<Track, Double>, TableCell<Track, Double>> cellFactoryDouble =
			        new Callback<TableColumn<Track, Double>, TableCell<Track, Double>>() {
			            public TableCell call(TableColumn p) {
			                TableCell cell = new TableCell<Track, Double>() { //not sure what this all is doing (thx stackoverflow :D)
			                    @Override
			                    public void updateItem(Double item, boolean empty) {
			                        super.updateItem(item, empty);
			                        if(item==null){
			                        	setText(null);
			                        	setContextMenu(null);
			                        }else{
			                        	setText(item.toString());
			                        	setContextMenu(Main.contextMenu);
			                        }
			                        setGraphic(null);
			                    }
			                };
			
			                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { //add to Tracklist
			                    @Override
			                    public void handle(MouseEvent event) {
			                        if (event.getClickCount() > 1) {
			                            TableCell c = (TableCell) event.getSource();;
			                            Main.tracklist.addTrack((Track) c.getTableRow().getItem());
			                            Main.tlController.updateTracklist();
			                        }
			                    }
			                });
			
			                return cell;
			            }
			        };
			//TableColumns
			TableColumn<Track, String> path = new TableColumn<Track, String>("PATH");
			path.setCellFactory(cellFactory);
			path.setCellValueFactory(
					new PropertyValueFactory<Track,String>("path")
			);
			TableColumn<Track, String> title = new TableColumn<Track, String>("TITLE");
			title.setCellFactory(cellFactory);
			title.setCellValueFactory(
					new PropertyValueFactory<Track,String>("title")
			);
			TableColumn<Track, String> albumartist = new TableColumn<Track, String>("ALBUMARTIST");
			albumartist.setCellFactory(cellFactory);
			albumartist.setCellValueFactory(
					new PropertyValueFactory<Track,String>("albumartist")
			);
			TableColumn<Track, String> artist = new TableColumn<Track, String>("ARTIST");
			artist.setCellFactory(cellFactory);
			artist.setCellValueFactory(
					new PropertyValueFactory<Track,String>("artist")
			);
			TableColumn<Track, String> album = new TableColumn<Track, String>("ALBUM");
			album.setCellFactory(cellFactory);
			album.setCellValueFactory(
					new PropertyValueFactory<Track,String>("album")
			);
			TableColumn<Track, Integer> trackNr = new TableColumn<Track, Integer>("#");
			trackNr.setCellFactory(cellFactoryInteger);
			trackNr.setCellValueFactory(
					new PropertyValueFactory<Track,Integer>("trackNr")
			);
			TableColumn<Track, Integer> year = new TableColumn<Track, Integer>("YEAR");
			year.setCellFactory(cellFactoryInteger);
			year.setCellValueFactory(
					new PropertyValueFactory<Track,Integer>("year")
			);
			TableColumn<Track, Double> bpm = new TableColumn<Track, Double>("BPM");
			bpm.setCellFactory(cellFactoryDouble);
			bpm.setCellValueFactory(
					new PropertyValueFactory<Track,Double>("bpm")
			);
			TableColumn<Track, Integer> rating = new TableColumn<Track, Integer>("RATING");
			rating.setCellFactory(cellFactoryInteger);
			rating.setCellValueFactory(
					new PropertyValueFactory<Track,Integer>("rating")
			);
			tracks.getColumns().setAll(Arrays.asList(title, albumartist, artist, album, trackNr, year, bpm, rating, path)); //Arrays.asList to ensure type safety
		}

		

}
