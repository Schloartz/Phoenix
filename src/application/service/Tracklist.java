package application.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.Track;

public class Tracklist {
	private ObservableList<Track> list;
	
	Tracklist(){
		list = FXCollections.observableArrayList();
	}
	
	public String getCurrentIds(){ //returns the ids of the tracks in the tracklist
		String ids = "(";
		for(Track t:list){
			ids += " " + t.getId() + ",";
		}
		ids = ids.substring(0,ids.length()-1); //cuts of the last undesired ","
		ids += ")";
		return ids;
	}
	
	public Track getCurrentTrack(){
		int cr = Main.mediaplayer.getStatus().getCurrTrack();
		return list.get(cr);
	}
	
	public void deleteTracks(int start, int end){ //deletes all Tracks from index a to index b
		int c_org = Main.mediaplayer.getStatus().getCurrTrack();
		int deleted = end-start+1; //deleted Tracks
		//Delete from tracklist
		if(start!=end){
			list.remove(start, end+1);
		}else{
			list.remove(start);
		}
		int size_now = getSize();
		//update currTrack
		///case 1: tracks below cr are deleted (no change)
		///case 2: tracks above cr are deleted (recalc)
		///case 3: tracks including cr are deleted (recalc and new cr)
		///   -    new cr-preference: select tracks below/above/none
		if(start<=c_org){
			if(c_org<=end){ //case 3
				if(size_now>end){ //tracks below available
					Main.mediaplayer.getStatus().setCurrTrack(start);
				}else if(start>0){ //tracks above available
					Main.mediaplayer.getStatus().setCurrTrack(start-1);
				}else{ //none available
					Main.mediaplayer.getStatus().setCurrTrack(-1);
				}
				Main.mediaplayer.disposeOldPlayer();
			}else{
				Main.mediaplayer.getStatus().setCurrTrack(c_org-deleted); //case 2
			}
		}
		
		Main.tlController.updateTracklist();
		Main.cvController.updateCoverView(false);
	}
	
	public void addTrack(String path) {
		if(new File(path).exists()){
			list.add(new Track(path));
			Main.tlController.updateTracklist();
			Main.cvController.updateCoverView(false);
		}else{
			System.out.println("ERROR track ("+path+") not existing");
		}
	}
	public void addTrack(Track track) {
		if(new File(track.getPath()).exists()){
			list.add(track);
			Main.tlController.updateTracklist();
			Main.cvController.updateCoverView(false);
		}else{
			System.out.println("ERROR track ("+track.getPath()+") not existing");
		}
	}
	
	public boolean isSubsequentTrack(){ //returns if there is a subsequent track in the tracklist 
		if(list.size()>0 && list.size()>Main.mediaplayer.getStatus().getCurrTrack()+1){
			return true;
		}else{
			return false;
		}
	}
	public boolean isPreviousTrack(int i) {//returns if there is a previous track (1:previous, 2:preprevious) in the tracklist 
		if(Main.mediaplayer.getStatus().getCurrTrack()>=i){
			return true;
		}else{
			return false;
		}
	}
	
	public void setActive(int index, boolean value){ //sets the track with index an active of value
		list.get(index).setActive(value);
	}
	public int getRating(int index){ //returns rating of current song
		Track m = new Track(getPath(index));
		return m.getRating();
	}
	public ObservableList<Track> getList(){ //returns current tracklist
		return list;
	}
	public String getPath(int index){ //gets the path of song at index
		return list.get(index).getPath();
	}
	public boolean isActive(int index){ //if the song is active i.e. being paused or player right now
		return list.get(index).isActive();
	}
	public int getSize(){
		return list.size();
	}
	public BufferedImage getCover(int index){
		if(new File(getPath(index)).exists()){
			Track m = new Track(getPath(index));
			return m.getCover();
		}else{
			return null;
		}
	}

	public void shuffle() {
		ArrayList<Track> tempList = new ArrayList<Track>();
		for(int i = Main.mediaplayer.getStatus().getCurrTrack()+1;i<getSize();i++){ //cycle through all upcoming tracks 
			tempList.add(list.get(i));
		}
		list.subList(Main.mediaplayer.getStatus().getCurrTrack()+1, getSize()).clear();
		Collections.shuffle(tempList); //shuffle them
		list.addAll(tempList);
	}

	public void addAutodjTrack(int autodj) {
		addTrack(Main.database.getAutoDJTrack(getCurrentTrack(), autodj));
	}
	
}