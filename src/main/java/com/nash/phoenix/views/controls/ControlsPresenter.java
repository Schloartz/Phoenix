package com.nash.phoenix.views.controls;

import java.net.URL;
import java.util.ResourceBundle;

import com.nash.phoenix.App;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.nash.phoenix.utils.RatingObjects;

public class ControlsPresenter implements Initializable{

	public ProgressBar trackProgress;
	public Button autodj, playpause;
	public Slider volumeControl;
	public RatingObjects ratingObjects;
	@FXML
	private Button volumeIcon;
	@FXML
	private static ImageView autodj_off, autodj_on1, autodj_on2, autodj_on3;
	@FXML
	private ImageView playIcon, pauseIcon;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		App.controlsPresenter = this;
		
		playIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_play.png")));
		pauseIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_pause.png")));
		autodj_off = new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_autodj_off.png")));
		autodj_on1 = new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_autodj_on1.png")));
		autodj_on2 = new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_autodj_on2.png")));
		autodj_on3 = new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_autodj_on3.png")));
		
		//Bottom Component: Controls
		trackProgress.setOnMousePressed(e -> { //set track to new playtime
	    	if(App.mediaplayer.players.size()!=0){ //if there is a song currently  being played/paused
	    		App.mediaplayer.setPlayTime(e.getX()/800);
	    	}
	    });
	    playpause.setOnMouseExited(e -> {
	    	if(!App.mediaplayer.isPlaying() || App.mediaplayer.players.size()==0){
	    		playpause.setGraphic(playIcon);
        	}else{
        		playpause.setGraphic(pauseIcon);
        	}
	    });
	    volumeControl.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
            		//change Icon & update volume in player
            		App.mediaplayer.setVolume(new_val.doubleValue());
            		if(new_val.doubleValue()==0.0){
            			volumeIcon.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_volume_off.png"))));
            		}else if(new_val.doubleValue()<=0.5){
            			volumeIcon.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_volume_half.png"))));
            		}else{
            			volumeIcon.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_volume.png"))));
            		}
                }
            });
	    initPlusOne();
	}
	@FXML
	private void backwardPressed(){ App.mediaplayer.backwardPressed(); }
	@FXML
	private void forwardPressed(){
		App.mediaplayer.forwardPressed();
	}
	@FXML
	private void volumePressed() {
		App.mediaplayer.toggleMute();}
	@FXML
	private void shufflePressed(){
		App.mediaplayer.shufflePressed();
	}
	@FXML
	private void playPausePressed(){
		App.mediaplayer.playPausePressed();
	}

	public void autodjPressed(){ //has to be public due to Intellij-access
		App.mediaplayer.autodjPressed();
		switch(App.mediaplayer.getStatus().getAutodj()){
			case 0: autodj.setGraphic(autodj_off);
				break;
			case 1: autodj.setGraphic(autodj_on1);
				break;
			case 2: autodj.setGraphic(autodj_on2);
				break;
			case 3: autodj.setGraphic(autodj_on3);
				break;
		}
	}
	
	public void showOrgRating() {
		ratingObjects.showOrgRating();
	}
	
	public void updateControls() {		
    	if(!App.mediaplayer.isPlaying()){
    		App.controlsPresenter.playpause.setGraphic(App.controlsPresenter.playIcon);
    	}else{
    		App.controlsPresenter.playpause.setGraphic(App.controlsPresenter.pauseIcon);
    	}
	}
	
	private void initPlusOne() { //inits the plusOne button
		//Icons
		///normal: global variables
		//Tooltip
		final Tooltip tooltip = new Tooltip();
		tooltip.setText("AutoDJ (What happens when tracklistView is depleted):\n1) Disabled\n2) Adds one random song\n3) Adds one similar song with good rating\n4) Adds one track from artist/albumartist");
		Tooltip.install(autodj, tooltip);
		
	}

	public Image returnNextAutodjIcon(int i){ //returns the autodj-icon for a given number
		switch(i){
			case 0: return autodj_on1.getImage();
			case 1: return autodj_on2.getImage();
			case 2: return autodj_on3.getImage();
			case 3: return autodj_off.getImage();
			default: return null;
		}
	}

}
