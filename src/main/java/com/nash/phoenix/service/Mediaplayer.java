package com.nash.phoenix.service;

import java.io.File;
import java.util.ArrayList;

import com.nash.phoenix.App;
import javafx.beans.value.ChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class Mediaplayer { //Deals mostly with userinput and transmits to GuiUpdate
	
	private com.nash.phoenix.utils.Status status;
	public  ArrayList<MediaPlayer> players = new ArrayList<>(); //contains only one element, the current player
	
	private  ChangeListener<Status> playPauseListener;
	private  ChangeListener<Duration> playtime;
	
	
	public Mediaplayer(){
		//Status
		status = new com.nash.phoenix.utils.Status(-1,0);
		
		//Changelistener playpause
		playPauseListener = (arg0, arg1, arg2) -> App.controlsPresenter.updateControls();
		    
	    playtime = (observable, oldValue, newValue) -> {
            double percentage_played = (double)newValue.toSeconds()/players.get(0).getMedia().getDuration().toSeconds();
            App.controlsPresenter.trackProgress.setProgress(percentage_played);
        };
	}
	public void setVolume(double val){
		if(players.size()!=0){
    		players.get(0).setVolume(val);
    	}
	}
	public void toggleMute(){
		if(players.size()!=0){
			double vol = players.get(0).getVolume();
			if(vol>0){
				getStatus().setOldVolume(vol);
				setVolume(0);
				App.controlsPresenter.volumeControl.setValue(0);
			}else{
				setVolume(getStatus().getOldVolume());
				App.controlsPresenter.volumeControl.setValue(getStatus().getOldVolume());
			}
		}
	}
	public void playPausePressed() {
		/*Cases: 1) No Songs loaded or all Songs played -> Do nothing
		 * 		 2) Songs loaded/Pause current
		 * 		 3) Songs loaded/Play current
		 */
		if(App.tracklist.getSize()!=0){ //no songs in tracklistView
			if(getStatus().getCurrTrack()!=-1){ //no players started yet
				if(players.size()>0){
					if(isPlaying()){ //PlayerController is playing -> pause
						players.get(0).pause();
					}else{ // PlayerController is paused -> play
						players.get(0).play();
					}
				}else{ //track not loaded into player
					players.add(createPlayer(new File(App.tracklist.getPath(status.getCurrTrack())).toURI().toString())); //creates new player
					players.get(0).play();
				}
			}else{ //start new player
				playNextSongInTracklist();
			}
		}else{
			System.out.println("ERROR no songs in tracklistView");
		}
	}

	public void backwardPressed(){
		/* Cases: 1) No Songs loaded OR no last song -> do nothing
		 * 		  2) In the first 10s of song -> switch to last
		 * 		  3) After the first 10s of song -> rewind
		 */
		if(App.tracklist.getSize()!=0 && status.getCurrTrack()!=-1){
			if(players.get(0).getCurrentTime().toSeconds()>10){ //rewind current song
				setPlayTime(0);
			}else if(App.tracklist.isPreviousTrack(1)){ //choose last song
				disposeOldPlayer();
				playPreviousSongInTracklist();
			}
		}
	}

	private void playPreviousSongInTracklist(){ //launches new with next media
		//checks if Media is available --> adds new player
		File mp3 = new File(App.tracklist.getPath(status.getCurrTrack()-1));
		//check if file exists
		if(mp3.exists()){
			//creates new player
			players.add(createPlayer(new File(App.tracklist.getPath(status.getCurrTrack()-1)).toURI().toString())); //creates new player
			players.get(0).play();//play
			if(status.getCurrTrack()!=-1){ //set last song inactive
				App.tracklist.setActive(status.getCurrTrack(), false);
			}
			App.tracklist.setActive(status.getCurrTrack()-1, true);
			status.setCurrTrack(status.getCurrTrack() - 1);
			//GUI
			App.controlsPresenter.showOrgRating();
			App.coverPresenter.updateCoverView(true, "backward");
		}else{
			System.out.println("ERROR with loading Media: " + App.tracklist.getPath(status.getCurrTrack()+1));
		}
	}

	public void forwardPressed() {
		/* Cases: 1) No Songs loaded OR no next song OR first start-> do nothing 
		 * 		  2) Playing/Paused current song -> switch to next
		 */
		if(App.tracklist.getSize()!=0 && App.tracklist.isSubsequentTrack(1) && status.getCurrTrack()!=-1){//there is a next track
			disposeOldPlayer();
			playNextSongInTracklist();
		}else if(App.tracklist.getSize()!=0 && status.getAutodj()!=0 && status.getCurrTrack()!=-1){ //one track loaded and autodj on
			disposeOldPlayer();
			App.tracklist.addAutodjTrack(getStatus().getAutodj());
			playNextSongInTracklist();
		}else{
			System.out.println("ERROR no songs in tracklistView OR no next song to play");
		}
	}

	
	
	private void playNextSongInTracklist(){ //launches new with next media
		//checks if Media is available --> adds new player
		File mp3; //mp3 to be played
		if(App.tracklist.isSubsequentTrack(1)){ //checks if there is another entry in tracklistView
			mp3 = new File(App.tracklist.getPath(status.getCurrTrack()+1));
			//check if file exists
			if(mp3!=null && mp3.exists()){
				//creates new player
				players.add(createPlayer(new File(App.tracklist.getPath(status.getCurrTrack()+1)).toURI().toString())); //creates new player
				players.get(0).play();//play
				if(status.getCurrTrack()!=-1){ //set last song inactive
					App.tracklist.setActive(status.getCurrTrack(), false);
				}
				App.tracklist.setActive(status.getCurrTrack()+1, true);
				status.setCurrTrack(status.getCurrTrack() + 1);
				//GUI
				App.controlsPresenter.showOrgRating();
				App.coverPresenter.updateCoverView(true, "forward");
			}else{
				System.out.println("ERROR with loading Media: " + App.tracklist.getPath(status.getCurrTrack()+1));
			}
		}else{
			System.out.println("ERROR while trying to play next Song in Tracklist: No entry in tracklistView (current: " + (status.getCurrTrack()+1) + " from " + App.tracklist.getSize() + " Songs)");
		}
		
	}
	
	public void disposeOldPlayer(){
		//halt old player if it exists
		if(players.size()!=0){
			players.get(0).stop();
			players.get(0).statusProperty().removeListener(playPauseListener);
			players.get(0).currentTimeProperty().removeListener(playtime);
			players.get(0).dispose();
			players.clear(); //deletes the last player off the list
		}
	}
	
	private MediaPlayer createPlayer(String mediaSource) {
		try{ //start new player and try to load media
			 final Media media = new Media(mediaSource);
			    final MediaPlayer player = new MediaPlayer(media);
			    player.setVolume(App.controlsPresenter.volumeControl.valueProperty().doubleValue()); //initial volume
			    player.statusProperty().addListener(playPauseListener);
			    player.currentTimeProperty().addListener(playtime);
			    player.setOnEndOfMedia(this::forwardPressed);
			    return player;
			    
		}catch(Exception e){
			System.out.println("Error loading song: " + mediaSource + " (wrong path?) ");
			e.printStackTrace();
			return null;
		}
		
	   
	  }

	

	public boolean isPlaying() { //checks if PlayerController is currently playing
		boolean answer = false;
		if(trackLoaded()){ //PROBLEM emerges when player is not yet initialized far enough to display status
			if(players.get(0).getStatus().equals(MediaPlayer.Status.PAUSED) || players.get(0).getStatus().equals(MediaPlayer.Status.READY)){ //
				answer = false;
			}else if(players.get(0).getStatus().equals(MediaPlayer.Status.PLAYING)){
				answer = true;
			}
		}
		return answer;
	}
	public boolean trackLoaded(){ //returns true if a track is loaded right now
		//PROBLEM emerges when player is not yet initialized far enough to display status
		return players.size() != 0 && players.get(0).getStatus() != null;
	}

	public boolean shufflePressed() { //shuffles upcoming songs, returns true if valid input
		if(App.tracklist.isSubsequentTrack(1)){
			App.tracklist.shuffle();
			App.coverPresenter.updateCoverView(false, "nothing");
			return true;
		}else{
			System.out.println("ERROR while trying to shuffle upcoming songs. No upcoming songs found!");
			return false;
		}
		
	}

	public void setPlayTime(double percent) { //sets the playtime of the player to a percentage
		players.get(0).seek(Duration.seconds(percent*players.get(0).getStopTime().toSeconds()));
	}


	public  void autodjPressed() { //changes mode of autodj
		status.rotateAutodj();
	}
	public com.nash.phoenix.utils.Status getStatus() {
		return status;
	}


	
}
