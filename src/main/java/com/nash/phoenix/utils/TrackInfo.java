package com.nash.phoenix.utils;

import com.nash.phoenix.App;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import javafx.util.Duration;

import java.util.ArrayList;

public class TrackInfo extends Popup {
	private ImageView coverview;
	private Label trackInfoText;
	private Timeline trackInfoTimeline;
	private static final double SEC = 4;
	private ArrayList<Star> stars = new ArrayList<>();

	
	
	public TrackInfo(){ //initializes the components of the trackinfo-flash
		//Position
		setX(C.SCREEN_WIDTH-220-15);
		setY(15);

		addEventHandler(WindowEvent.WINDOW_SHOWN, e -> trackInfoTimeline.play());
		
		HBox trackInfoContainer = new HBox(10);
		trackInfoContainer.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
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
		for(int i=1;i<=5;i++){
			Star star = new Star(8,3.6);
			star.setStrokeWidth(0.5);
			star.setStroke(Color.BLACK);
			star.setFill(Color.WHITE);
			stars.add(star);
			rating.getChildren().add(star);
		}
		rightSide.getChildren().addAll(trackInfoText,rating);
		
		trackInfoContainer.getChildren().addAll(coverview, rightSide);
		trackInfoContainer.setMaxSize(220,110);
		getContent().add(trackInfoContainer);
		
		//Timeline to hide trackInfo
		trackInfoTimeline = new Timeline();
		trackInfoTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(SEC), e-> hide()));
	}
	
	public void updateCoverTextRating(Track t, WritableImage wi) {
		//Set Cover, Text
		coverview.setImage(wi);
		trackInfoText.setText(t.getTitle()+"\n"+t.getArtist()+"\n"+t.getAlbum()+"\n"+t.getYear());
		//Rating
		int rating = t.getRating();
		int i = 0;
		for(Star s:stars){
			if(i<rating){
				s.setFill(Color.BLACK);
			}else{
				s.setFill(Color.TRANSPARENT);
			}
			i++;
		}
		
		sizeToScene();
	}
	
	
}

