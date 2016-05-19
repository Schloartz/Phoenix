package utils;

import application.service.Main;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TracklistCell extends ListCell<Track>{
	StackPane imageContainer;
	ImageView removeTrack;
	
	public TracklistCell(){
		
	}
		 
    @Override
    protected void updateItem(Track t, boolean bln) {
        super.updateItem(t, bln);
        if (t != null) {
        	//text label
        	setFont(Font.font(null, FontWeight.NORMAL, 12));
        	setTextFill(Color.BLACK);
        	setText(t.getTitle() + " - " +t.getArtist());
            if(Main.tracklist.getList().indexOf(t)<Main.mediaplayer.getStatus().getCurrTrack()){
            	setFont(Font.font(null, FontWeight.NORMAL, 12));
            	setTextFill(Color.GREY);
            }else if(getIndex()==Main.mediaplayer.getStatus().getCurrTrack()){ //if track is being played right now
            	setFont(Font.font(null, FontWeight.BOLD, 12));
            }
          
            setContextMenu(Main.contextMenu);
        }else{
        	setText(null);
        	setContextMenu(null);
        }
    }
    
    

}
