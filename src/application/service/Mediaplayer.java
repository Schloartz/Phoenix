package application.service;

import java.io.File;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class Mediaplayer { //Deals mostly with userinput and transmits to GuiUpdate
	
	private utils.Status status;
	public  ArrayList<MediaPlayer> players = new ArrayList<MediaPlayer>(); //contains only one element, the current player
	
	private  ChangeListener<Status> playPauseListener;
	private  ChangeListener<Duration> playtime;
	
	
	public Mediaplayer(){
		//Status
		status = new utils.Status(-1,0);
		
		//Changelistener playpause
		playPauseListener = new ChangeListener<MediaPlayer.Status>(){
			@Override
			public void changed(ObservableValue<? extends Status> arg0,
					Status arg1, Status arg2) {
				Main.cController.updateControls();
			}
		};
		    
	    playtime = new ChangeListener<Duration>(){
	    	@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
	    		double percentage_played = (double)newValue.toSeconds()/players.get(0).getMedia().getDuration().toSeconds();
	    		Main.cController.progressKnob.setTranslateX(percentage_played*Main.cController.trackProgress.getWidth() - 7);
				Main.cController.trackProgress.setProgress(percentage_played);
			}
	    };
	}
	public void setVolume(double val){
		if(players.size()!=0){
    		players.get(0).setVolume(val);
    	}
	}
	public void playPausePressed() {
		/*Cases: 1) No Songs loaded or all Songs played -> Do nothing
		 * 		 2) Songs loaded/Pause current
		 * 		 3) Songs loaded/Play current
		 */
		if(Main.tracklist.getSize()!=0){ //no songs in tracklist
			if(getStatus().getCurrTrack()!=-1){ //no players started yet
				if(players.size()>0){
					if(isPlaying()){ //PlayerController is playing -> pause
						players.get(0).pause();
					}else{ // PlayerController is paused -> play
						players.get(0).play();
					}
				}else{ //track not loaded into player
					players.add(createPlayer(new File(Main.tracklist.getPath(status.getCurrTrack())).toURI().toString())); //creates new player
					players.get(0).play();
				}
			}else{ //start new player
				playNextSongInTracklist();
			}
		}else{
			System.out.println("ERROR no songs in tracklist");
		}
		
	}

	public void forwardPressed() {
		/* Cases: 1) No Songs loaded OR no next song OR first start-> do nothing 
		 * 		  2) Playing/Paused current song -> switch to next
		 */
		if(Main.tracklist.getSize()!=0 && Main.tracklist.isSubsequentTrack() && status.getCurrTrack()!=-1){//there is a next track
			disposeOldPlayer();
			playNextSongInTracklist();
		}else if(Main.tracklist.getSize()!=0 && status.getAutodj()!=0 && status.getCurrTrack()!=-1){ //one track loaded and autodj on
			disposeOldPlayer();
			Main.tracklist.addAutodjTrack(getStatus().getAutodj());
			playNextSongInTracklist();
		}else{
			System.out.println("ERROR no songs in tracklist OR no next song to play");
		}
	}
	
	
	
	public void playNextSongInTracklist(){ //launches new with next media
		//checks if Media is available --> adds new player
		File mp3; //mp3 to be played
		if(Main.tracklist.isSubsequentTrack()){ //checks if there is another entry in tracklist
			mp3 = new File(Main.tracklist.getPath(status.getCurrTrack()+1));
			//check if file exists
			if(mp3!=null && mp3.exists()){
				//creates new player
				players.add(createPlayer(new File(Main.tracklist.getPath(status.getCurrTrack()+1)).toURI().toString())); //creates new player
				players.get(0).play();//play
				if(status.getCurrTrack()!=-1){ //set last song inactive
					Main.tracklist.setActive(status.getCurrTrack(), false);
				}
				Main.tracklist.setActive(status.getCurrTrack()+1, true);
				status.setCurrTrack(status.getCurrTrack() + 1);
				//GUI
				Main.cController.showOrgRating();
				Main.cvController.updateCoverView(true);
				Main.tlController.updateTracklist();
				//Show trackInfo
				Main.trackInfo.updateCoverTextRating(Main.tracklist.getCurrentTrack(), Main.cvController.getMidCoverImage());
				Main.trackInfo.show();
			}else{
				System.out.println("ERROR with loading Media: " + Main.tracklist.getPath(status.getCurrTrack()+1));
			}
		}else{
			mp3 = null;
			System.out.println("ERROR while trying to play next Song in Tracklist: No entry in tracklist (current: " + (status.getCurrTrack()+1) + " from " + Main.tracklist.getSize() + " Songs)");
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
			    player.setVolume(Main.cController.volumeControl.valueProperty().doubleValue()/100.0); //initial volume
			    player.statusProperty().addListener(playPauseListener);
			    player.currentTimeProperty().addListener(playtime);
			    player.setOnEndOfMedia(new Runnable(){
					@Override
					public void run() {
						disposeOldPlayer();
						playNextSongInTracklist();
					}
			    });

			    return player;
			    
		}catch(Exception e){
			System.out.println("Error loading song: " + mediaSource + " (wrong path?) ");
			e.printStackTrace();
			return null;
		}
		
	   
	  }

	

	public boolean isPlaying() { //checks if PlayerController is currently playing
		boolean answer = false;
		if(players.size()!=0 && players.get(0).getStatus()!=null){ //PROBLEM ermges when player is not yet initialized far enough to display status
			if(players.get(0).getStatus().equals(MediaPlayer.Status.PAUSED) || players.get(0).getStatus().equals(MediaPlayer.Status.READY)){ //
				answer = false;
			}else if(players.get(0).getStatus().equals(MediaPlayer.Status.PLAYING)){
				answer = true;
			}
		}
		return answer;
	}


	public boolean shufflePressed() { //shuffles upcoming songs, returns true if valid input
		if(Main.tracklist.isSubsequentTrack()){
			Main.tracklist.shuffle();
			Main.tlController.updateTracklist();
			Main.cvController.updateCoverView(false);
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
	public utils.Status getStatus() {
		return status;
	}
	public void setStatus(utils.Status status) {
		this.status = status;
	}

	
}