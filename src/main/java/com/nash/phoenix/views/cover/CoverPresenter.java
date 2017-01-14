package com.nash.phoenix.views.cover;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import com.nash.phoenix.App;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.nash.phoenix.utils.C;

public class CoverPresenter implements Initializable{
	@FXML
	private Pane staticCovers, dynamicCovers;
	@FXML
	private VBox trackoverlay;
	@FXML
	private ImageView coverLeft, coverMid, coverRight;
	@FXML
	private ImageView transOutLeft, transLeft, transMid, transRight, transOutRight; //ImageViews for transition, specifies their starting point
	@FXML
	private PerspectiveTransform pt_left, pt_right;
	@FXML
	private Label title, albumartist, artist, album, trackNr, year, bpm, rating;

	//Images of the covers
	private WritableImage outLeftCoverImage = null;
	private WritableImage leftCoverImage = null;
	private WritableImage midCoverImage = null;
	private WritableImage rightCoverImage = null;
	private WritableImage outRightCoverImage = null;
	private WritableImage cover_missing;
	
	private PerspectiveTransform transPtOutLeft, transPtLeft, transPtMid, transPtRight, transPtOutRight;
	
	
	private static Timeline forwardAnimation, backwardAnimation, rightinAnimation, rightoutAnimation, leftinAnimation, leftoutAnimation;
	private static boolean MidinFront;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		App.coverPresenter = this;
		
		pt_left = new PerspectiveTransform(0,-C.TRANSFORM ,150,0,150,150,0,150+C.TRANSFORM);
		pt_right = new PerspectiveTransform(0,0 ,150,-C.TRANSFORM,150,150+C.TRANSFORM,0,150);
		transPtLeft = new PerspectiveTransform(0,-C.TRANSFORM ,150,0,150,150,0,150+C.TRANSFORM);
		transPtMid = new PerspectiveTransform(0,0 ,300,0,300,300,0,300);
		transPtRight = new PerspectiveTransform(0,0 ,150,-C.TRANSFORM,150,150+C.TRANSFORM,0,150);
		transPtOutRight = new PerspectiveTransform(0,0 ,150,0,150,150,0,150);
		transPtOutLeft = new PerspectiveTransform(0,0 ,150,0,150,150,0,150);
		
	    coverLeft.setEffect(pt_left);
	    coverRight.setEffect(pt_right);
	    
	    //Images
	  	cover_missing = convertBufferedImageToWritableImage(SwingFXUtils.fromFXImage(new Image(getClass().getResourceAsStream("/icons/cover_missing.png")), null));
	  		
		///Animation for the Forward Animation
	    forwardAnimation = new Timeline();
	    forwardAnimation.getKeyFrames().addAll(
				new KeyFrame(new Duration(1100), //first (faster) transition: moves left and mid cover out or to the left
						//Left to Out
						new KeyValue(App.coverPresenter.transLeft.opacityProperty(), 0),
						new KeyValue(App.coverPresenter.transLeft.translateXProperty(), -150),
						new KeyValue(transPtLeft.ulyProperty(), 0),
						new KeyValue(transPtLeft.llyProperty(), 150),
						//Mid to Left
						new KeyValue(App.coverPresenter.transMid.translateXProperty(), -75), //move to left; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(App.coverPresenter.transMid.scaleXProperty(), 0.5), //resize image to 150
						new KeyValue(App.coverPresenter.transMid.scaleYProperty(), 0.5)), //resize image to 150
						
				new KeyFrame(new Duration(2000), //second (slower) transition: moves the right cover mid and the outter cover right
						//Mid to Left
						new KeyValue(App.coverPresenter.transMid.opacityProperty(), 0.6), //reduce opacity from 1 to 0.6
						new KeyValue(transPtMid.ulyProperty(), -65),
						new KeyValue(transPtMid.llyProperty(), 300+65),
						//Right to Mid
						new KeyValue(App.coverPresenter.transRight.opacityProperty(), 1), //reduce opacity from 1 to 0.6
						new KeyValue(App.coverPresenter.transRight.translateXProperty(), 90), //move to left; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(App.coverPresenter.transRight.translateYProperty(), 0),
						new KeyValue(transPtRight.urxProperty(), 300),
						new KeyValue(transPtRight.uryProperty(), 0),
						new KeyValue(transPtRight.lrxProperty(), 300),
						new KeyValue(transPtRight.lryProperty(), 300),
						new KeyValue(transPtRight.llyProperty(), 300),
						//Out to Right
						new KeyValue(App.coverPresenter.transOutRight.opacityProperty(), 0.6),
						new KeyValue(App.coverPresenter.transOutRight.translateXProperty(), 480-150),
						new KeyValue(transPtOutRight.uryProperty(), -32.5),
						new KeyValue(transPtOutRight.lryProperty(), 150+32.5))
						
		);
		forwardAnimation.setOnFinished(e -> Platform.runLater(() -> {
			App.coverPresenter.dynamicCovers.setVisible(false);
			App.coverPresenter.staticCovers.setVisible(true);
		}));
		///Move right cover (now in mid) to front at a specific point
		App.coverPresenter.transRight.translateXProperty().addListener(new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(!MidinFront && newValue.doubleValue()<155){ //moves right cover that has transitioned to mid to the top of all covers
				App.coverPresenter.transRight.toFront();
				MidinFront = true;
			}
		}});

		///Animation for the Backward Animation TODO
		backwardAnimation = new Timeline();
		backwardAnimation.getKeyFrames().addAll(
				new KeyFrame(new Duration(1100), //first (faster) transition: moves right to out-right and mid cover to the left
						//Right to Out-right
						new KeyValue(App.coverPresenter.transRight.opacityProperty(), 0),
						new KeyValue(App.coverPresenter.transRight.translateXProperty(), 480+150),
						new KeyValue(transPtRight.uryProperty(), 0),
						new KeyValue(transPtRight.lryProperty(), 150),
						//Mid to Right
						new KeyValue(App.coverPresenter.transMid.translateXProperty(), 90+165), //move to right; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(App.coverPresenter.transMid.scaleXProperty(), 0.5), //resize image to 150
						new KeyValue(App.coverPresenter.transMid.scaleYProperty(), 0.5)), //resize image to 150

				new KeyFrame(new Duration(2000), //second (slower) transition: moves the left cover mid and the out-left cover left
						//Mid to Left
						new KeyValue(App.coverPresenter.transMid.opacityProperty(), 0.6), //reduce opacity from 1 to 0.6
						new KeyValue(transPtMid.uryProperty(), -65),
						new KeyValue(transPtMid.lryProperty(), 300+65),
						//Left to Mid
						new KeyValue(App.coverPresenter.transLeft.opacityProperty(), 1), //reduce opacity from 1 to 0.6
						new KeyValue(App.coverPresenter.transLeft.translateXProperty(), 90), //move to right; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(App.coverPresenter.transLeft.translateYProperty(), 0),
						new KeyValue(transPtLeft.ulyProperty(), 0),
						new KeyValue(transPtLeft.urxProperty(), 300),
						new KeyValue(transPtLeft.lrxProperty(), 300),
						new KeyValue(transPtLeft.lryProperty(), 300),
						new KeyValue(transPtLeft.llyProperty(), 300),
						//Out-left to left
						new KeyValue(App.coverPresenter.transOutLeft.opacityProperty(), 0.6),
						new KeyValue(App.coverPresenter.transOutLeft.translateXProperty(), 0),
						new KeyValue(transPtOutLeft.ulyProperty(), -32.5),
						new KeyValue(transPtOutLeft.llyProperty(), 150+32.5))

		);
		backwardAnimation.setOnFinished(e -> Platform.runLater(() -> {
			App.coverPresenter.dynamicCovers.setVisible(false);
			App.coverPresenter.staticCovers.setVisible(true);
		}));
		///Move left cover (now in mid) to front at a specific point
		App.coverPresenter.transLeft.translateXProperty().addListener((observable, oldValue, newValue) -> {
            if(!MidinFront && newValue.doubleValue()>30){ //moves left cover that has transitioned to mid to the top of all covers
                App.coverPresenter.transLeft.toFront();
                MidinFront = true;
            }
        });
	}
	
	
	
	public void updateCoverView(boolean dynamic, String mode) { //coordinates the change in coverview (static or dynamic)
		loadCovers();
		if(dynamic && App.mainPresenter.view.equals("cover")){
			prepareAnimation(mode);
			App.coverPresenter.staticCovers.setVisible(false);
			App.coverPresenter.dynamicCovers.setVisible(true);
			if(mode.equals("forward")){
				forwardAnimation.play();
			}else if(mode.equals("backward")){
				backwardAnimation.play();
			}
		}
		App.coverPresenter.coverLeft.setImage(leftCoverImage);
    	App.coverPresenter.coverMid.setImage(midCoverImage);
    	App.coverPresenter.coverRight.setImage(rightCoverImage);
	}

	private void loadCovers(){ //loads new covers into the slots
		int current = App.mediaplayer.getStatus().getCurrTrack();
		//outleft
		if(App.tracklist.isPreviousTrack(2)){
			outLeftCoverImage = convertBufferedImageToWritableImage(App.tracklist.getCover(current-2));
		}else{
			outLeftCoverImage = null;
		}
		//left
		if(App.tracklist.isPreviousTrack(1)){
			leftCoverImage = convertBufferedImageToWritableImage(App.tracklist.getCover(current-1));
		}else{
			leftCoverImage = null;
		}
		//mid
		if(current!=-1){
			midCoverImage = convertBufferedImageToWritableImage(App.tracklist.getCover(current));
		}
		//right
		if(App.tracklist.isSubsequentTrack(1)){
			rightCoverImage = convertBufferedImageToWritableImage(App.tracklist.getCover(current+1));
		}else{
			rightCoverImage = null;
		}
		//outright
		if(App.tracklist.isSubsequentTrack(2)){
			outRightCoverImage = convertBufferedImageToWritableImage(App.tracklist.getCover(current+2));
		}else{
			outRightCoverImage = null;
		}
	}
	
	
	
	
	private WritableImage convertBufferedImageToWritableImage(BufferedImage bf){ //converts a bufferedImage into a writableImage 
		if(bf!=null){ //read cover of current track
			WritableImage wr = new WritableImage(bf.getWidth(), bf.getHeight());
			PixelWriter pw = wr.getPixelWriter();
			for (int x = 0; x < bf.getWidth(); x++) {
				for (int y = 0; y < bf.getHeight(); y++) {
					pw.setArgb(x, y, bf.getRGB(x, y));
				}
			}
			return wr;
		}else{ //no cover available...use placeholder
			return cover_missing;
		}
	}
	
	private void prepareAnimation(String mode){ //prepares the transition covers for the upcoming transition depending on the mode
		//set Covers to starting value before animation, coverimages represent the new values
		switch(mode){
			case "forward":
				App.coverPresenter.transOutLeft.setImage(null); //not moved, not seen
				App.coverPresenter.transLeft.setImage(outLeftCoverImage);
				App.coverPresenter.transMid.setImage(leftCoverImage);
				App.coverPresenter.transRight.setImage(midCoverImage);
				App.coverPresenter.transOutRight.setImage(rightCoverImage);
				MidinFront = false;
				break;
			case "backward":
				App.coverPresenter.transOutLeft.setImage(leftCoverImage);
				App.coverPresenter.transLeft.setImage(midCoverImage);
				App.coverPresenter.transMid.setImage(rightCoverImage);
				App.coverPresenter.transRight.setImage(outRightCoverImage);
				App.coverPresenter.transOutRight.setImage(null); //not moved, not seen
				MidinFront = false;
				break;
		}
    	///Set Opacity base-levels
		App.coverPresenter.transOutLeft.setOpacity(0);
		App.coverPresenter.transLeft.setOpacity(0.6);
		App.coverPresenter.transMid.setOpacity(1);
		App.coverPresenter.transRight.setOpacity(0.6);
		App.coverPresenter.transOutRight.setOpacity(0);
		//Set Scaling
		App.coverPresenter.transMid.setScaleX(1);
		App.coverPresenter.transMid.setScaleY(1);
		///Set Transformation starting points
		setPerspectiveTransform(transPtOutLeft, 0,0 ,150,0,150,150,0,150);
		setPerspectiveTransform(transPtLeft, 0,-C.TRANSFORM ,150,0,150,150,0,150+C.TRANSFORM);
		setPerspectiveTransform(transPtMid, 0,0 ,300,0,300,300,0,300);
		setPerspectiveTransform(transPtRight, 0,0 ,150,-C.TRANSFORM,150,150+C.TRANSFORM,0,150);
		setPerspectiveTransform(transPtOutRight, 0,0 ,150,0,150,150,0,150);

		App.coverPresenter.transOutLeft.setEffect(transPtOutLeft);
		App.coverPresenter.transLeft.setEffect(transPtLeft);
	    App.coverPresenter.transMid.setEffect(transPtMid);
	    App.coverPresenter.transRight.setEffect(transPtRight);
	    App.coverPresenter.transOutRight.setEffect(transPtOutRight);
		///set Translations starting values
		App.coverPresenter.transOutLeft.setTranslateX(-150);
		App.coverPresenter.transOutLeft.setTranslateY(75);
		App.coverPresenter.transLeft.setTranslateY(75);
		App.coverPresenter.transLeft.setTranslateX(0);
		App.coverPresenter.transMid.setTranslateX(90);
		App.coverPresenter.transRight.setTranslateX(330);
		App.coverPresenter.transRight.setTranslateY(75);
		App.coverPresenter.transOutRight.setTranslateX(480);
		App.coverPresenter.transOutRight.setTranslateY(75);
	}
	
	//sets all values of a PerspectiveTransform
	private void setPerspectiveTransform(PerspectiveTransform pt, double ulx, double uly, double urx, double ury, double lrx, double lry, double llx, double lly){
		pt.setUlx(ulx);
		pt.setUly(uly);
		pt.setUrx(urx);
		pt.setUry(ury);
		pt.setLrx(lrx);
		pt.setLry(lry);
		pt.setLlx(llx);
		pt.setLly(lly);
	}
	
	public WritableImage getMidCoverImage(){
		return midCoverImage;
	}

	@FXML
	private void showTrackoverlay(){
		//fill information in
		title.setText(App.tracklist.getCurrentTrack().getTitle());
		albumartist.setText(App.tracklist.getCurrentTrack().getAlbumartist());
		artist.setText(App.tracklist.getCurrentTrack().getArtist());
		album.setText(App.tracklist.getCurrentTrack().getAlbum());
		trackNr.setText(String.valueOf(App.tracklist.getCurrentTrack().getTrackNr()));
		year.setText(String.valueOf(App.tracklist.getCurrentTrack().getYear()));
		bpm.setText(String.valueOf(App.tracklist.getCurrentTrack().getBpm()));
		rating.setText(String.valueOf(App.tracklist.getCurrentTrack().getRating()));

		//make visible
		trackoverlay.setVisible(true);
		trackoverlay.toFront();
	}
	@FXML
	private void hideTrackoverlay(){
		trackoverlay.setVisible(false);
	}
}
