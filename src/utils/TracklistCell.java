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

            if(!t.getActive()){
            	getStyleClass().remove("current_song");
            }else{ //if track is being played right now
                getStyleClass().add("current_song");
                System.out.println("cs:"+t.getTitle());
            }
          
            setContextMenu(Main.contextMenu);
        }else{
        	setText(null);
        	setContextMenu(null);
            getStyleClass().remove("current_song");
        }
    }
    
    

}
