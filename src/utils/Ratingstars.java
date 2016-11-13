package utils;

import java.util.ArrayList;

import application.service.Main;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


public class Ratingstars extends HBox{
	private ArrayList<ImageView> stars = new ArrayList<>();
	private static ImageView star1 = new ImageView();
	private static ImageView star2 = new ImageView();
	private static ImageView star3 = new ImageView();
	private static ImageView star4 = new ImageView();
	private static ImageView star5 = new ImageView();
	private static Image star_empty = new Image(Ratingstars.class.getResourceAsStream("/resources/icons/ratingstar_empty.png"));
	private static Image star_full = new Image(Ratingstars.class.getResourceAsStream("/resources/icons/ratingstar_full.png"));
	private static Image star_mouseover = new Image(Ratingstars.class.getResourceAsStream("/resources/icons/ratingstar_mouseover.png"));
	
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
			s.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
                //fill all stars till this one
                int rating = stars.indexOf(e.getSource())+1;
                showStarRating(rating, "mouseover");
                e.consume();
			});
			s.addEventHandler(MouseEvent.MOUSE_EXITED, e-> {
                //reset to rating of current song
                if(Main.mediaplayer.getStatus().getCurrTrack()==-1){ //if there is no active track
                    showStarRating(0, "full");
                }else{
                    showOrgRating();
                }
                e.consume();
			});
			s.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                //set new rating for current song
                if(Main.mediaplayer.getStatus().getCurrTrack()!=-1){ //if there is an active track
                    int r = 0 ;
                    if(e.getSource()==star1){
                        r = 1;
                    }else if(e.getSource()==star2){
                        r = 2;
                    }else if(e.getSource()==star3){
                        r = 3;
                    }else if(e.getSource()==star4){
                        r = 4;
                    }else if(e.getSource()==star5){
                        r = 5;
                    }
                    Main.tracklist.getList().get(Main.mediaplayer.getStatus().getCurrTrack()).setRating(r);
                    showStarRating(r, "full");
                }
                e.consume();
			});
		}
		
		this.getChildren().addAll(stars);
	}
	
	private static void showStarRating(int rating, String mode){ //shows full stars for each number of rating e.g. 3 rating = XXX00 in corresponding mode: mouseover or full
        Image star_filled = null;
        if(mode.equals("full")){
            star_filled=star_full;
        }else if(mode.equals("mouseover")){
            star_filled=star_mouseover;
        }
		switch(rating){
		case 0:
			star1.setImage(star_empty);
			star2.setImage(star_empty);
			star3.setImage(star_empty);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 1:
			star1.setImage(star_filled);
			star2.setImage(star_empty);
			star3.setImage(star_empty);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 2:
			star1.setImage(star_filled);
			star2.setImage(star_filled);
			star3.setImage(star_empty);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 3:
			star1.setImage(star_filled);
			star2.setImage(star_filled);
			star3.setImage(star_filled);
			star4.setImage(star_empty);
			star5.setImage(star_empty);
	        break;
		case 4:
			star1.setImage(star_filled);
			star2.setImage(star_filled);
			star3.setImage(star_filled);
			star4.setImage(star_filled);
			star5.setImage(star_empty);
	        break;
		case 5:
			star1.setImage(star_filled);
			star2.setImage(star_filled);
			star3.setImage(star_filled);
			star4.setImage(star_filled);
			star5.setImage(star_filled);
	        break;
	}
	}
	
	public void showOrgRating(){ //determines rating of current track and displays it in starratings
		int r = Main.tracklist.getRating(Main.mediaplayer.getStatus().getCurrTrack());
		showStarRating(r, "full");
	}
}