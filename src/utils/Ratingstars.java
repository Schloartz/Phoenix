package utils;

import java.util.ArrayList;

import application.service.Main;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


public class Ratingstars extends HBox{
	ArrayList<ImageView> stars = new ArrayList<ImageView>();
	static ImageView star1 = new ImageView();
	static ImageView star2 = new ImageView();
	static ImageView star3 = new ImageView();
	static ImageView star4 = new ImageView();
	static ImageView star5 = new ImageView();
	static Image star_empty = new Image(Ratingstars.class.getResourceAsStream("/resources/icons/ratingstar_empty.png"));
	static Image star_full = new Image(Ratingstars.class.getResourceAsStream("/resources/icons/ratingstar_full.png"));
	static Image star_mouseover = new Image(Ratingstars.class.getResourceAsStream("/resources/icons/ratingstar_mouseover.png"));
	
	public Ratingstars(){
		stars.add(star1);
		stars.add(star2);
		stars.add(star3);
		stars.add(star4);
		stars.add(star5);
		
		for(ImageView s:stars){
			//add images
			s.setImage(star_empty);
			//add EventHandlers
			s.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					//fill all stars till this one
					int rating = stars.indexOf(event.getSource())+1;
					showMouseOverStarRating(rating);
				    event.consume();
				}
			});
			s.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					//reset to rating of current song
					if(Main.mediaplayer.getStatus().getCurrTrack()==-1){ //if there is no active track
						showStarRating(0);
					}else{
						showOrgRating();
					}
				    event.consume();
				}
			});
			s.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					//set new rating for current song
					if(Main.mediaplayer.getStatus().getCurrTrack()!=-1){ //if there is an active track
						int r = 0 ;
						if(event.getSource()==star1){
							r = 1;	
						}else if(event.getSource()==star2){
							r = 2;	
						}else if(event.getSource()==star3){
							r = 3;	
						}else if(event.getSource()==star4){
							r = 4;	
						}else if(event.getSource()==star5){
							r = 5;	
						}
						Main.tracklist.getList().get(Main.mediaplayer.getStatus().getCurrTrack()).setRating(r);
						showStarRating(r);
					}
				    event.consume();
				}
			});
		}
		
		this.getChildren().addAll(stars);
	}
	
	public static void showStarRating(int rating){ //shows full stars for each number of rating e.g. 3 rating = XXX00
		switch(rating){
		case 0:
			star1.setImage(star_empty);
			star2.setImage(star_empty);
			star3.setImage(star_empty);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 1:
			star1.setImage(star_full);
			star2.setImage(star_empty);
			star3.setImage(star_empty);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 2:
			star1.setImage(star_full);
			star2.setImage(star_full);
			star3.setImage(star_empty);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 3:
			star1.setImage(star_full);
			star2.setImage(star_full);
			star3.setImage(star_full);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 4:
			star1.setImage(star_full);
			star2.setImage(star_full);
			star3.setImage(star_full);
			star4.setImage(star_full);
			star5.setImage(star_empty);
	        break;
		case 5:
			star1.setImage(star_full);
			star2.setImage(star_full);
			star3.setImage(star_full);
			star4.setImage(star_full);
			star5.setImage(star_full);
	        break;
	}
	}
	public static void showMouseOverStarRating(int rating){ //shows grey stars for each number of rating e.g. 3 rating = XXX00
		switch(rating){
		case 0:
			star1.setImage(star_empty);
			star2.setImage(star_empty);
			star3.setImage(star_empty);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 1:
			star1.setImage(star_mouseover);
			star2.setImage(star_empty);
			star3.setImage(star_empty);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 2:
			star1.setImage(star_mouseover);
			star2.setImage(star_mouseover);
			star3.setImage(star_empty);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 3:
			star1.setImage(star_mouseover);
			star2.setImage(star_mouseover);
			star3.setImage(star_mouseover);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 4:
			star1.setImage(star_mouseover);
			star2.setImage(star_mouseover);
			star3.setImage(star_mouseover);
			star4.setImage(star_mouseover);
			star5.setImage(star_empty);
	        break;
		case 5:
			star1.setImage(star_mouseover);
			star2.setImage(star_mouseover);
			star3.setImage(star_mouseover);
			star4.setImage(star_mouseover);
			star5.setImage(star_mouseover);
	        break;
	}
	}
	
	public void showOrgRating(){ //determines rating of current track and displays it in starratings
		int r = Main.tracklist.getRating(Main.mediaplayer.getStatus().getCurrTrack());
		showStarRating(r);
	}
}