package utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class TrackInfo extends Stage{
	Scene trackInfoScene;
	ImageView coverview;
	Label trackInfoText;
	Timeline trackInfoTimeline;
	//Ratingstars
	ImageView star1,star2,star3,star4,star5;
	Image star_empty = new Image(TrackInfo.class.getResourceAsStream("/resources/icons/ratingstar_empty.png"));
	Image star_full = new Image(TrackInfo.class.getResourceAsStream("/resources/icons/ratingstar_full.png"));
	Image star_mouseover = new Image(TrackInfo.class.getResourceAsStream("/resources/icons/ratingstar_mouseover.png"));
	
	
	public TrackInfo(){ //initalizes the components of the trackinfo-flash
		
		setAlwaysOnTop(true);
		setX(C.SCREEN_WIDTH-220-15);
		setY(15);
		initStyle(StageStyle.TRANSPARENT);
		initModality(Modality.NONE);
		addEventHandler(WindowEvent.WINDOW_SHOWN, e -> trackInfoTimeline.play());
		
		HBox trackInfoContainer = new HBox(10);
		trackInfoContainer.getStylesheets().add(TrackInfo.class.getResource("/resources/css/StyleClasses.css").toExternalForm());
		trackInfoContainer.getStyleClass().add("root");
		trackInfoContainer.setPadding(new Insets(5));
		
		//Left side: Cover
		coverview = new ImageView();
		coverview.setFitHeight(100);
		coverview.setFitWidth(100);

				
		//Right side: TrackInfo
		VBox rightSide = new VBox();
		trackInfoText = new Label();
		trackInfoText.setFont(Font.font(null,FontWeight.NORMAL,14));
		
		HBox rating = new HBox();
		star1 = new ImageView();
		star1.setFitHeight(15);
		star1.setFitWidth(15);
		star2 = new ImageView();
		star2.setFitHeight(15);
		star2.setFitWidth(15);
		star3 = new ImageView();
		star3.setFitHeight(15);
		star3.setFitWidth(15);
		star4 = new ImageView();
		star4.setFitHeight(15);
		star4.setFitWidth(15);
		star5 = new ImageView();
		star5.setFitHeight(15);
		star5.setFitWidth(15);
		rating.getChildren().addAll(star1,star2,star3,star4,star5);
		rightSide.getChildren().addAll(trackInfoText,rating);
		
		trackInfoContainer.getChildren().addAll(coverview, rightSide);
		trackInfoScene = new Scene(trackInfoContainer,220,110); //220
		
		setScene(trackInfoScene);
		
		//Timeline to hide trackInfo
		trackInfoTimeline = new Timeline();
		trackInfoTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(4),
		    new EventHandler<ActionEvent>() {
		
		        @Override
		        public void handle(ActionEvent event) {
		            hide();
		        }
		}));
	}
	
	public void updateCoverTextRating(Track t, WritableImage wi) {
		//Set Cover, Text
		coverview.setImage(wi);
		trackInfoText.setText(t.getTitle()+"\n"+t.getArtist()+"\n"+t.getAlbum()+"\n"+t.getYear());
		//Rating
		switch(t.getRating()){
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
		
		sizeToScene();
	}
	
	
}

