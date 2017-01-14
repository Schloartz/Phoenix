package com.nash.phoenix.views.main;

import com.nash.phoenix.App;
import com.nash.phoenix.utils.Track;
import com.nash.phoenix.views.controls.ControlsView;
import com.nash.phoenix.views.cover.CoverView;
import com.nash.phoenix.views.database.DatabaseView;
import com.nash.phoenix.views.menu.MenuView;
import com.nash.phoenix.views.settings.SettingsView;
import com.nash.phoenix.views.tracklist.TracklistView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPresenter implements Initializable{ //contains methods that can ONLY be user-generated
	@FXML
	BorderPane root;
	//Settings
	public boolean showFlash = false;
	public Track lastSelected = null; //stores the last selected listcell/tableviewcell for open folder-contextmenu
	public String view = "database"; //current view: mediaplayer/musiccollection/settings
	//FXMLViews
	private static ControlsView controlsView;
	private static CoverView coverView;
	private static DatabaseView databaseView;
	private static MenuView menuView;
	private static SettingsView settingsView;
	private static TracklistView tracklistView;


	@Override
	public void initialize(URL location, ResourceBundle resources) { //called when view shows
		App.mainPresenter = this;
		//Load FXMLViews
		///Top
		menuView = new MenuView();
		//Mid
		coverView = new CoverView();
		coverView.getView(); //init view, so it can be referenced
		databaseView = new DatabaseView();
		settingsView = new SettingsView();
		settingsView.getView(); //init view, so it can be referenced
		//Bottom
		controlsView = new ControlsView();
		//Right
		tracklistView = new TracklistView();

		//Set Top/Mid/Bottom/Right
		root.setTop(menuView.getView());
		root.setCenter(databaseView.getView());
		root.setBottom(controlsView.getView());
		root.setRight(tracklistView.getView());



	}

	
	public void setCenter(String s){ //sets the Center of the mainView to a new parent
		switch(s){
			case "database": root.setCenter(databaseView.getView());
				break;
			case "cover": root.setCenter(coverView.getView());
				break;
			case "settings": root.setCenter(settingsView.getView());
				break;
		}
	}
	
	
	
}
