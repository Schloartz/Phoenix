package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.service.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import utils.Ratingstars;

public class CController implements Initializable{
	
	@FXML
	private StackPane progressContainer;
	public ProgressBar trackProgress;
	@FXML
	public Circle progressKnob;
	public ImageView autodj;
	public ImageView playpause;
	public Slider volumeControl;
	public Label volumeLabel;
	public Ratingstars ratingstars;
	
	@FXML
	private static Image autodj_off, autodj_on1, autodj_on2, autodj_on3;
	public Image playIcon;
	public Image pauseIcon;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Main.cController = this;
		
		playIcon = new Image(getClass().getResourceAsStream("/resources/icons/icon_play.png"));
		Image playIcon_mouseover = new Image(getClass().getResourceAsStream("/resources/icons/icon_play_mouseover.png"));
		Image playIcon_activated = new Image(getClass().getResourceAsStream("/resources/icons/icon_play_activated.png"));
		pauseIcon = new Image(getClass().getResourceAsStream("/resources/icons/icon_pause.png"));
		Image pauseIcon_mouseover = new Image(getClass().getResourceAsStream("/resources/icons/icon_pause_mouseover.png"));
		Image pauseIcon_activated = new Image(getClass().getResourceAsStream("/resources/icons/icon_pause_activated.png"));
		autodj_off = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_off.png"));
		autodj_on1 = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_on1.png"));
		autodj_on2 = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_on2.png"));
		autodj_on3 = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_on3.png"));
		
		//Bottom Component: Controls
	    progressContainer.setOnMouseEntered(e -> {
	    	trackProgress.setStyle("-fx-accent: #878787");
	    	trackProgress.setPrefHeight(7);
	    	progressKnob.setVisible(true);
	    });
	    progressContainer.setOnMouseExited(e -> {
	    	trackProgress.setStyle("-fx-accent: black");
	    	progressKnob.setFill(Color.web("#878787"));
	    	trackProgress.setPrefHeight(5);
	    	progressKnob.setVisible(false);
	    });
	    progressContainer.setOnMousePressed(e -> { //set track to new playtime
	    	trackProgress.setStyle("-fx-accent: #7CAFC2");
	    	progressKnob.setFill(Color.web("#7CAFC2"));
	    	if(Main.mediaplayer.players.size()!=0){ //if there is a song currently  being played/paused
	    		progressKnob.setTranslateX(e.getX()-7);
	    		Main.mediaplayer.setPlayTime(e.getX()/800);
	    	}
	    });
	    progressContainer.setOnMouseReleased(e -> {
	    	trackProgress.setStyle("-fx-accent: #878787");
	    	progressKnob.setFill(Color.web("#878787"));
	    });
	    
	    playpause.setOnMouseEntered(e -> {
	    	if(!Main.mediaplayer.isPlaying()){
	    		playpause.setImage(playIcon_mouseover);
	    	}else{
	    		playpause.setImage(pauseIcon_mouseover);
	    	}
	    });
	    playpause.setOnMousePressed(e -> {
	    	if(!Main.mediaplayer.isPlaying() || Main.mediaplayer.players.size()==0){
	    		playpause.setImage(playIcon_activated);
        	}else{
        		playpause.setImage(pauseIcon_activated);
        	}
	    });
	    playpause.setOnMouseExited(e -> {
	    	if(!Main.mediaplayer.isPlaying() || Main.mediaplayer.players.size()==0){
	    		playpause.setImage(playIcon);
        	}else{
        		playpause.setImage(pauseIcon);
        	}
	    });
	    volumeControl.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
            		//change Text & update volume in player
            		volumeLabel.setText(new_val.intValue()+" %");
            		Main.mediaplayer.setVolume(new_val.doubleValue()/100.0);     
                }
            });
	    initPlusOne();
	}
	
	@FXML
	private void forwardPressed(){
		Main.mediaplayer.forwardPressed();
	}
	@FXML
	private void shufflePressed(){
		Main.mediaplayer.shufflePressed();
	}
	@FXML
	private void playPausePressed(){
		Main.mediaplayer.playPausePressed();
	}

	public void autodjPressed(){ //has to be public due to Intellij-access
		switch(Main.mediaplayer.getStatus().getAutodj()){
    	case 0: autodj.setImage(autodj_on1);
    		break;
    	case 1: autodj.setImage(autodj_on2);
    		break;
    	case 2: autodj.setImage(autodj_on3);
    		break;
    	case 3: autodj.setImage(autodj_off);
    		break;
    	}
		Main.mediaplayer.autodjPressed();
	}
	
	public void showOrgRating() {
		ratingstars.showOrgRating();
	}
	
	public void updateControls() {		
    	if(!Main.mediaplayer.isPlaying()){
    		Main.cController.playpause.setImage(Main.cController.playIcon);
    	}else{
    		Main.cController.playpause.setImage(Main.cController.pauseIcon);
    	}
	}
	
	private void initPlusOne() { //inits the plusOne button
		//Icons
		///normal: global variables
		
		///mouseover
		Image autodj_off_mouseover = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_off_mouseover.png"));
		Image autodj_on1_mouseover = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_on1_mouseover.png"));
		Image autodj_on2_mouseover = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_on2_mouseover.png"));
		Image autodj_on3_mouseover = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_on3_mouseover.png"));
		///pressed
		Image autodj_off_activated = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_off_activated.png"));
		Image autodj_on1_activated = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_on1_activated.png"));
		Image autodj_on2_activated = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_on2_activated.png"));
		Image autodj_on3_activated = new Image(getClass().getResourceAsStream("/resources/icons/icon_autodj_on3_activated.png"));
		
		//Tooltip
		final Tooltip tooltip = new Tooltip();
		tooltip.setText("AutoDJ (What happens when tracklist is depleted):\n1) Disabled\n2) Adds one random song\n3) Adds one similar song with good rating\n4) Adds one track from artist/albumartist");
		Tooltip.install(autodj, tooltip);
		//Interactions: Pressed, Hover, exited
	    //MouseOver
	    autodj.setOnMouseEntered(e ->{
	    	switch(Main.mediaplayer.getStatus().getAutodj()){
	    	case 0: autodj.setImage(autodj_off_mouseover);
	    		break;
	    	case 1: autodj.setImage(autodj_on1_mouseover);
	    		break;
	    	case 2: autodj.setImage(autodj_on2_mouseover);
	    		break;
	    	case 3: autodj.setImage(autodj_on3_mouseover);
	    		break;	
	    	}
	    });
	  //Pressed
	    autodj.setOnMousePressed(e ->{
	    	switch(Main.mediaplayer.getStatus().getAutodj()){
	    	case 0: autodj.setImage(autodj_off_activated);
	    		break;
	    	case 1: autodj.setImage(autodj_on1_activated);
	    		break;
	    	case 2: autodj.setImage(autodj_on2_activated);
	    		break;
	    	case 3: autodj.setImage(autodj_on3_activated);
    			break;
	    	}
	    });
	  //Exited
	    autodj.setOnMouseExited(e ->{
	    	switch(Main.mediaplayer.getStatus().getAutodj()){
	    	case 0: autodj.setImage(autodj_off);
	    		break;
	    	case 1: autodj.setImage(autodj_on1);
	    		break;
	    	case 2: autodj.setImage(autodj_on2);
	    		break;
	    	case 3: autodj.setImage(autodj_on3);
    			break;
	    	}
	    });
		
	}
}
