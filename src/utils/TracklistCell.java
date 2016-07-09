package utils;

import application.service.Main;
import javafx.scene.control.ListCell;
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
            	setFont(Font.font("Helvetica", FontWeight.NORMAL, 12));
            	setTextFill(Color.GREY);
            }else if(getIndex()==Main.mediaplayer.getStatus().getCurrTrack()){ //if track is being played right now
            	setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
            	setTextFill(Color.BLACK);
            }else{
            	setFont(Font.font("Helvetica", FontWeight.NORMAL, 12));
            	setTextFill(Color.BLACK);
            }
          
            setContextMenu(Main.contextMenu);
        }else{
        	setText(null);
        	setContextMenu(null);
        }
    }
    
    

}
