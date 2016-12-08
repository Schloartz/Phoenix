package utils;

import application.service.Main;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;


public class RatingObjects extends HBox{
	private ArrayList<Star> ratingobjects = new ArrayList<>();

	
	public RatingObjects(){
		//create objects
		for(int i=1;i<=5;i++){
			Star star = new Star(15,6);
			star.setStrokeWidth(2);
			star.setStroke(Color.BLACK);
			star.setFill(Color.WHITE);
			ratingobjects.add(star);
			setEventHandling(star, i);
			this.getChildren().add(star);
		}
	}

	private void setEventHandling(Star c, int pos){ //sets the EventHandling for the single objects
		c.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
			//fill all stars till this one
			showStarRating(pos, "mouseover");
			e.consume();
		});
		c.addEventHandler(MouseEvent.MOUSE_EXITED, e-> {
			//reset to rating of current song
			if(Main.mediaplayer.getStatus().getCurrTrack()==-1){ //if there is no active track
				showStarRating(0, "full");
			}else{
				showOrgRating();
			}
			e.consume();
		});
		c.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			//set new rating for current song
			if(Main.mediaplayer.getStatus().getCurrTrack()!=-1){ //if there is an active track
				Main.tracklist.getList().get(Main.mediaplayer.getStatus().getCurrTrack()).setRating(pos);
				showStarRating(pos, "full");
			}
			e.consume();
		});
	}

	private void showStarRating(int rating, String mode){ //shows full stars for each number of rating e.g. 3 rating = XXX00 in corresponding mode: mouseover or full
		Color col = null;
        if(mode.equals("full")){
			col = Color.BLACK;
        }else if(mode.equals("mouseover")){
			col = Color.web("#C4C4C4");
        }
        int i = 0;
        for(Star c:ratingobjects){ //filled circles
			if(i<rating){
				c.setFill(col);
			}else{
				c.setFill(Color.TRANSPARENT);
			}
			i++;
		}

	}
	
	public void showOrgRating(){ //determines rating of current track and displays it in starratings
		int r = Main.tracklist.getRating(Main.mediaplayer.getStatus().getCurrTrack());
		showStarRating(r, "full");
	}
}

