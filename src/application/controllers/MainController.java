package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.service.Main;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import utils.Track;

public class MainController implements Initializable{ //contains methods that can ONLY be user-generated
	
	BorderPane root;
	
	public Track lastSelected = null; //stores the last selected listcell/tableviewcell
	public boolean mediaplayerView = true; //if MusicPlayer is showing DatabaseView or CoverView
	
	@Override
	public void initialize(URL location, ResourceBundle resources) { //called when view shows
		Main.mainController = this;
		//Load Database FXML
		try {
			//Mid Component: DatabaseView
			FXMLLoader.load(getClass().getResource("/resources/fxml/DatabaseView.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	
}
