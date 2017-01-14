package com.nash.phoenix.utils;

import com.nash.phoenix.App;
import javafx.scene.control.ListCell;

public class TracklistCell extends ListCell<Track>{ //Cell for tracklist (listcell!)

    @Override
    protected void updateItem(Track t, boolean bln) {
        super.updateItem(t, bln);
        if (t != null) {
        	//text label
        	setText(t.getTitle() + " - " +t.getArtist());
            if(t.getActive()){ //if track is being played right now
            	getStyleClass().add("current_song");
            }else{
                getStyleClass().remove("current_song");
            }
          
            setContextMenu(App.contextMenu);
        }else{
        	setText(null);
        	setContextMenu(null);
            getStyleClass().remove("current_song");
        }
    }
    
    

}
