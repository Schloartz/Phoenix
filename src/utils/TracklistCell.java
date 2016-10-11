package utils;

import application.service.Main;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TracklistCell extends ListCell<Track>{ //Cell for tracklist (listcell!)
 
    @Override
    protected void updateItem(Track t, boolean bln) {
        super.updateItem(t, bln);
        if (t != null) {
        	//text label
        	setText(t.getTitle() + " - " +t.getArtist());

            if(Main.tracklist.getList().indexOf(t)<Main.mediaplayer.getStatus().getCurrTrack()){
            	getStyleClass().remove("current_song");
            }else if(getIndex()==Main.mediaplayer.getStatus().getCurrTrack()){ //if track is being played right now
                getStyleClass().add("current_song");
            }else{
                getStyleClass().remove("current_song");
            }
          
            setContextMenu(Main.contextMenu);
        }else{
        	setText(null);
        	setContextMenu(null);
            getStyleClass().remove("current_song");
        }
    }
    
    

}
