package application.controllers;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import application.service.Main;
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
import utils.C;

public class CoverviewController implements Initializable{
	@FXML
	private StackPane coverView;
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
		Main.coverviewController = this;
		
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
	  	cover_missing = convertBufferedImageToWritableImage(SwingFXUtils.fromFXImage(new Image(getClass().getResourceAsStream("/resources/icons/cover_missing.png")), null));
	  		
		///Animation for the Forward Animation
	    forwardAnimation = new Timeline();
	    forwardAnimation.getKeyFrames().addAll(
				new KeyFrame(new Duration(1100), //first (faster) transition: moves left and mid cover out or to the left
						//Left to Out
						new KeyValue(Main.coverviewController.transLeft.opacityProperty(), 0),
						new KeyValue(Main.coverviewController.transLeft.translateXProperty(), -150),
						new KeyValue(transPtLeft.ulyProperty(), 0),
						new KeyValue(transPtLeft.llyProperty(), 150),
						//Mid to Left
						new KeyValue(Main.coverviewController.transMid.translateXProperty(), -75), //move to left; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(Main.coverviewController.transMid.scaleXProperty(), 0.5), //resize image to 150
						new KeyValue(Main.coverviewController.transMid.scaleYProperty(), 0.5)), //resize image to 150
						
				new KeyFrame(new Duration(2000), //second (slower) transition: moves the right cover mid and the outter cover right
						//Mid to Left
						new KeyValue(Main.coverviewController.transMid.opacityProperty(), 0.6), //reduce opacity from 1 to 0.6
						new KeyValue(transPtMid.ulyProperty(), -65),
						new KeyValue(transPtMid.llyProperty(), 300+65),
						//Right to Mid
						new KeyValue(Main.coverviewController.transRight.opacityProperty(), 1), //reduce opacity from 1 to 0.6
						new KeyValue(Main.coverviewController.transRight.translateXProperty(), 90), //move to left; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(Main.coverviewController.transRight.translateYProperty(), 0),
						new KeyValue(transPtRight.urxProperty(), 300),
						new KeyValue(transPtRight.uryProperty(), 0),
						new KeyValue(transPtRight.lrxProperty(), 300),
						new KeyValue(transPtRight.lryProperty(), 300),
						new KeyValue(transPtRight.llyProperty(), 300),
						//Out to Right
						new KeyValue(Main.coverviewController.transOutRight.opacityProperty(), 0.6),
						new KeyValue(Main.coverviewController.transOutRight.translateXProperty(), 480-150),
						new KeyValue(transPtOutRight.uryProperty(), -32.5),
						new KeyValue(transPtOutRight.lryProperty(), 150+32.5))
						
		);
		forwardAnimation.setOnFinished(e -> Platform.runLater(() -> {
			Main.coverviewController.dynamicCovers.setVisible(false);
			Main.coverviewController.staticCovers.setVisible(true);
		}));
		///Move right cover (now in mid) to front at a specific point
		Main.coverviewController.transRight.translateXProperty().addListener(new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(!MidinFront && newValue.doubleValue()<155){ //moves right cover that has transitioned to mid to the top of all covers
				Main.coverviewController.transRight.toFront();
				MidinFront = true;
			}
		}});

		///Animation for the Backward Animation TODO
		backwardAnimation = new Timeline();
		backwardAnimation.getKeyFrames().addAll(
				new KeyFrame(new Duration(1100), //first (faster) transition: moves right to out-right and mid cover to the left
						//Right to Out-right
						new KeyValue(Main.coverviewController.transRight.opacityProperty(), 0),
						new KeyValue(Main.coverviewController.transRight.translateXProperty(), 480+150),
						new KeyValue(transPtRight.uryProperty(), 0),
						new KeyValue(transPtRight.lryProperty(), 150),
						//Mid to Right
						new KeyValue(Main.coverviewController.transMid.translateXProperty(), 90+165), //move to right; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(Main.coverviewController.transMid.scaleXProperty(), 0.5), //resize image to 150
						new KeyValue(Main.coverviewController.transMid.scaleYProperty(), 0.5)), //resize image to 150

				new KeyFrame(new Duration(2000), //second (slower) transition: moves the left cover mid and the out-left cover left
						//Mid to Left
						new KeyValue(Main.coverviewController.transMid.opacityProperty(), 0.6), //reduce opacity from 1 to 0.6
						new KeyValue(transPtMid.uryProperty(), -65),
						new KeyValue(transPtMid.lryProperty(), 300+65),
						//Left to Mid
						new KeyValue(Main.coverviewController.transLeft.opacityProperty(), 1), //reduce opacity from 1 to 0.6
						new KeyValue(Main.coverviewController.transLeft.translateXProperty(), 90), //move to right; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(Main.coverviewController.transLeft.translateYProperty(), 0),
						new KeyValue(transPtLeft.ulyProperty(), 0),
						new KeyValue(transPtLeft.urxProperty(), 300),
						new KeyValue(transPtLeft.lrxProperty(), 300),
						new KeyValue(transPtLeft.lryProperty(), 300),
						new KeyValue(transPtLeft.llyProperty(), 300),
						//Out-left to left
						new KeyValue(Main.coverviewController.transOutLeft.opacityProperty(), 0.6),
						new KeyValue(Main.coverviewController.transOutLeft.translateXProperty(), 0),
						new KeyValue(transPtOutLeft.ulyProperty(), -32.5),
						new KeyValue(transPtOutLeft.llyProperty(), 150+32.5))

		);
		backwardAnimation.setOnFinished(e -> Platform.runLater(() -> {
			Main.coverviewController.dynamicCovers.setVisible(false);
			Main.coverviewController.staticCovers.setVisible(true);
		}));
		///Move left cover (now in mid) to front at a specific point
		Main.coverviewController.transLeft.translateXProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(!MidinFront && newValue.doubleValue()>30){ //moves left cover that has transitioned to mid to the top of all covers
					Main.coverviewController.transLeft.toFront();
					MidinFront = true;
				}
			}});
	}
	
	
	
	public void updateCoverView(boolean dynamic, String mode) { //coordinates the change in coverview (static or dynamic)
		loadCovers();
		if(dynamic && Main.mainController.view.equals("mediaplayer")){
			prepareAnimation(mode);
			Main.coverviewController.staticCovers.setVisible(false);
			Main.coverviewController.dynamicCovers.setVisible(true);
			//BorderPane doesn't clip its children
			Main.root.setRight(null);
			Main.root.setRight(Main.tracklistController.tracklist);
			if(mode.equals("forward")){
				forwardAnimation.play();
			}else if(mode.equals("backward")){
				backwardAnimation.play();
			}
		}
		Main.coverviewController.coverLeft.setImage(leftCoverImage);
    	Main.coverviewController.coverMid.setImage(midCoverImage);
    	Main.coverviewController.coverRight.setImage(rightCoverImage);
	}

	private void loadCovers(){ //loads new covers into the slots
		int current = Main.mediaplayer.getStatus().getCurrTrack();
		//outleft
		if(Main.tracklist.isPreviousTrack(2)){
			outLeftCoverImage = convertBufferedImageToWritableImage(Main.tracklist.getCover(current-2));
		}else{
			outLeftCoverImage = null;
		}
		//left
		if(Main.tracklist.isPreviousTrack(1)){
			leftCoverImage = convertBufferedImageToWritableImage(Main.tracklist.getCover(current-1));
		}else{
			leftCoverImage = null;
		}
		//mid
		if(current!=-1){
			midCoverImage = convertBufferedImageToWritableImage(Main.tracklist.getCover(current));
		}
		//right
		if(Main.tracklist.isSubsequentTrack(1)){
			rightCoverImage = convertBufferedImageToWritableImage(Main.tracklist.getCover(current+1));
		}else{
			rightCoverImage = null;
		}
		//outright
		if(Main.tracklist.isSubsequentTrack(2)){
			outRightCoverImage = convertBufferedImageToWritableImage(Main.tracklist.getCover(current+2));
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
				Main.coverviewController.transOutLeft.setImage(null); //not moved, not seen
				Main.coverviewController.transLeft.setImage(outLeftCoverImage);
				Main.coverviewController.transMid.setImage(leftCoverImage);
				Main.coverviewController.transRight.setImage(midCoverImage);
				Main.coverviewController.transOutRight.setImage(rightCoverImage);
				MidinFront = false;
				break;
			case "backward":
				Main.coverviewController.transOutLeft.setImage(leftCoverImage);
				Main.coverviewController.transLeft.setImage(midCoverImage);
				Main.coverviewController.transMid.setImage(rightCoverImage);
				Main.coverviewController.transRight.setImage(outRightCoverImage);
				Main.coverviewController.transOutRight.setImage(null); //not moved, not seen
				MidinFront = false;
				break;
		}
    	///Set Opacity base-levels
		Main.coverviewController.transOutLeft.setOpacity(0);
		Main.coverviewController.transLeft.setOpacity(0.6);
		Main.coverviewController.transMid.setOpacity(1);
		Main.coverviewController.transRight.setOpacity(0.6);
		Main.coverviewController.transOutRight.setOpacity(0);
		//Set Scaling
		Main.coverviewController.transMid.setScaleX(1);
		Main.coverviewController.transMid.setScaleY(1);
		///Set Transformation starting points
		setPerspectiveTransform(transPtOutLeft, 0,0 ,150,0,150,150,0,150);
		setPerspectiveTransform(transPtLeft, 0,-C.TRANSFORM ,150,0,150,150,0,150+C.TRANSFORM);
		setPerspectiveTransform(transPtMid, 0,0 ,300,0,300,300,0,300);
		setPerspectiveTransform(transPtRight, 0,0 ,150,-C.TRANSFORM,150,150+C.TRANSFORM,0,150);
		setPerspectiveTransform(transPtOutRight, 0,0 ,150,0,150,150,0,150);

		Main.coverviewController.transOutLeft.setEffect(transPtOutLeft);
		Main.coverviewController.transLeft.setEffect(transPtLeft);
	    Main.coverviewController.transMid.setEffect(transPtMid);
	    Main.coverviewController.transRight.setEffect(transPtRight);
	    Main.coverviewController.transOutRight.setEffect(transPtOutRight);
		///set Translations starting values
		Main.coverviewController.transOutLeft.setTranslateX(-150);
		Main.coverviewController.transOutLeft.setTranslateY(75);
		Main.coverviewController.transLeft.setTranslateY(75);
		Main.coverviewController.transLeft.setTranslateX(0);
		Main.coverviewController.transMid.setTranslateX(90);
		Main.coverviewController.transRight.setTranslateX(330);
		Main.coverviewController.transRight.setTranslateY(75);
		Main.coverviewController.transOutRight.setTranslateX(480);
		Main.coverviewController.transOutRight.setTranslateY(75);
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
	
	StackPane getCoverView(){
		return coverView;
	}

	@FXML
	private void showTrackoverlay(){
		//fill information in
		title.setText(Main.tracklist.getCurrentTrack().getTitle());
		albumartist.setText(Main.tracklist.getCurrentTrack().getAlbumartist());
		artist.setText(Main.tracklist.getCurrentTrack().getArtist());
		album.setText(Main.tracklist.getCurrentTrack().getAlbum());
		trackNr.setText(String.valueOf(Main.tracklist.getCurrentTrack().getTrackNr()));
		year.setText(String.valueOf(Main.tracklist.getCurrentTrack().getYear()));
		bpm.setText(String.valueOf(Main.tracklist.getCurrentTrack().getBpm()));
		rating.setText(String.valueOf(Main.tracklist.getCurrentTrack().getRating()));

		//make visible
		trackoverlay.setVisible(true);
		trackoverlay.toFront();
	}
	@FXML
	private void hideTrackoverlay(){
		trackoverlay.setVisible(false);
	}
}
