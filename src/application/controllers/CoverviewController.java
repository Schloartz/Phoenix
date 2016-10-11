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
	private ImageView transLeftToOut, transOutToRight, transRightToMid, transMidToLeft;
	@FXML
	private PerspectiveTransform pt_left, pt_right;
	@FXML
	private Label title, albumartist, artist, album, trackNr, year, bpm, rating;

	//Images of the covers
	private WritableImage oldCoverImage = null; //image of the image that gets driven out (left)
	private WritableImage leftCoverImage = null;
	private WritableImage midCoverImage = null;
	private WritableImage rightCoverImage = null;
	private WritableImage cover_missing;
	
	private PerspectiveTransform pt_leftToOut, pt_midToLeft, pt_rightToMid, pt_outToRight;  
	
	
	private static Timeline forwardAnimation;
	private static boolean MidinFront;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Main.coverviewController = this;
		
		pt_left = new PerspectiveTransform(0,-C.TRANSFORM ,C.SMALLCOVER,0,C.SMALLCOVER,C.SMALLCOVER,0,C.SMALLCOVER+C.TRANSFORM);
		pt_right = new PerspectiveTransform(0,0 ,C.SMALLCOVER,-C.TRANSFORM,C.SMALLCOVER,C.SMALLCOVER+C.TRANSFORM,0,C.SMALLCOVER);
		pt_leftToOut = new PerspectiveTransform(0,-C.TRANSFORM ,150,0,150,150,0,150+C.TRANSFORM);
		pt_midToLeft = new PerspectiveTransform(0,0 ,300,0,300,300,0,300);
		pt_rightToMid = new PerspectiveTransform(0,0 ,150,-C.TRANSFORM,150,150+C.TRANSFORM,0,150);
		pt_outToRight = new PerspectiveTransform(0,0 ,150,0,150,150,0,150);
		
	    coverLeft.setEffect(pt_left);
	    coverRight.setEffect(pt_right);
	    
	    //Images
	  	cover_missing = convertBufferedImageToWritableImage(SwingFXUtils.fromFXImage(new Image(getClass().getResourceAsStream("/resources/icons/cover_missing.png")), null));
	  		
		///Animation for the Forward Animation
	    forwardAnimation = new Timeline();
	    forwardAnimation.getKeyFrames().addAll(
				new KeyFrame(new Duration(1100), //first (faster) transition: moves left and mid cover out or to the left
						//Left to Out
						new KeyValue(Main.coverviewController.transLeftToOut.opacityProperty(), 0),
						new KeyValue(Main.coverviewController.transLeftToOut.translateXProperty(), -150),
						new KeyValue(pt_leftToOut.ulyProperty(), 0),
						new KeyValue(pt_leftToOut.llyProperty(), 150),
						//Mid to Left
						new KeyValue(Main.coverviewController.transMidToLeft.translateXProperty(), -75), //move to left; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(Main.coverviewController.transMidToLeft.scaleXProperty(), 0.5), //resize image to 150
						new KeyValue(Main.coverviewController.transMidToLeft.scaleYProperty(), 0.5)), //resize image to 150
						
				new KeyFrame(new Duration(2000), //second (slower) transition: moves the right cover mid and the outter cover right
						//Mid to Left
						new KeyValue(Main.coverviewController.transMidToLeft.opacityProperty(), 0.6), //reduce opacity from 1 to 0.6
						new KeyValue(pt_midToLeft.ulyProperty(), -65),
						new KeyValue(pt_midToLeft.llyProperty(), 300+65),
						//Right to Mid
						new KeyValue(Main.coverviewController.transRightToMid.opacityProperty(), 1), //reduce opacity from 1 to 0.6
						new KeyValue(Main.coverviewController.transRightToMid.translateXProperty(), 90), //move to left; no transition to bottom necessary bc node is resizing accordingly ;)
						new KeyValue(Main.coverviewController.transRightToMid.translateYProperty(), 0),
						new KeyValue(pt_rightToMid.urxProperty(), 300),
						new KeyValue(pt_rightToMid.uryProperty(), 0),
						new KeyValue(pt_rightToMid.lrxProperty(), 300),
						new KeyValue(pt_rightToMid.lryProperty(), 300),
						new KeyValue(pt_rightToMid.llyProperty(), 300),
						//Out to Right
						new KeyValue(Main.coverviewController.transOutToRight.opacityProperty(), 0.6),
						new KeyValue(Main.coverviewController.transOutToRight.translateXProperty(), 480-150),
						new KeyValue(pt_outToRight.uryProperty(), -32.5),
						new KeyValue(pt_outToRight.lryProperty(), 150+32.5))
						
		);
		forwardAnimation.setOnFinished(e -> Platform.runLater(() -> {
			Main.coverviewController.dynamicCovers.setVisible(false);
			Main.coverviewController.staticCovers.setVisible(true);
		}));
		///Move right cover (now in mid) to front at a specific point
		Main.coverviewController.transRightToMid.translateXProperty().addListener(new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(!MidinFront && newValue.doubleValue()<155){ //moves right cover that has transitioned to mid to the top of all covers
				Main.coverviewController.transRightToMid.toFront();
				MidinFront = true;
			}
		}});


	}
	
	
	
	public void updateCoverView(boolean dynamic) { //coordinates the change in coverview (static or dynamic)
		loadCovers();
		if(dynamic && Main.mainController.view.equals("mediaplayer")){
			prepareCoverTransition();
			Main.coverviewController.staticCovers.setVisible(false);
			Main.coverviewController.dynamicCovers.setVisible(true);
			//BorderPane doesn't clip its children
			Main.root.setRight(null);
			Main.root.setRight(Main.tracklistController.tracklist);
			forwardAnimation.play();
		}
		Main.coverviewController.coverLeft.setImage(leftCoverImage);
    	Main.coverviewController.coverMid.setImage(midCoverImage);
    	Main.coverviewController.coverRight.setImage(rightCoverImage);
	}

	private void loadCovers(){ //loads new covers into the slots
		int current = Main.mediaplayer.getStatus().getCurrTrack();
		leftCoverImage = null;
		midCoverImage = null;
		rightCoverImage = null;
		oldCoverImage = null;
		//oldCover
		if(Main.tracklist.isPreviousTrack(2)){
			oldCoverImage = convertBufferedImageToWritableImage(Main.tracklist.getCover(current-2));
		}
		//left
		if(Main.tracklist.isPreviousTrack(1)){
			leftCoverImage = convertBufferedImageToWritableImage(Main.tracklist.getCover(current-1));
		}
		//mid
		if(current!=-1){
			midCoverImage = convertBufferedImageToWritableImage(Main.tracklist.getCover(current));
		}
		//right
		if(Main.tracklist.isSubsequentTrack()){
			rightCoverImage = convertBufferedImageToWritableImage(Main.tracklist.getCover(current+1));
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
	
	private void prepareCoverTransition(){ //prepares the transition covers for the upcoming transition
    	Main.coverviewController.transLeftToOut.setImage(oldCoverImage);
		Main.coverviewController.transMidToLeft.setImage(leftCoverImage);
		Main.coverviewController.transRightToMid.setImage(midCoverImage);
		Main.coverviewController.transOutToRight.setImage(rightCoverImage);
    	///Set Opacity base-levels
		Main.coverviewController.transLeftToOut.setOpacity(0.6);
		Main.coverviewController.transMidToLeft.setOpacity(1);
		Main.coverviewController.transRightToMid.setOpacity(0.6);
		Main.coverviewController.transOutToRight.setOpacity(0);
		//Set Scaling
		Main.coverviewController.transMidToLeft.setScaleX(1);
		Main.coverviewController.transMidToLeft.setScaleY(1);
		///Set Transformation starting points
		setPerspectiveTransform(pt_leftToOut, 0,-C.TRANSFORM ,150,0,150,150,0,150+C.TRANSFORM);
		setPerspectiveTransform(pt_midToLeft, 0,0 ,300,0,300,300,0,300);
		setPerspectiveTransform(pt_rightToMid, 0,0 ,150,-C.TRANSFORM,150,150+C.TRANSFORM,0,150);
		setPerspectiveTransform(pt_outToRight, 0,0 ,150,0,150,150,0,150);
		
		Main.coverviewController.transLeftToOut.setEffect(pt_leftToOut);
	    Main.coverviewController.transMidToLeft.setEffect(pt_midToLeft);
	    Main.coverviewController.transRightToMid.setEffect(pt_rightToMid);
	    Main.coverviewController.transOutToRight.setEffect(pt_outToRight);
		///set Translations starting values
		Main.coverviewController.transLeftToOut.setTranslateY(75);
		Main.coverviewController.transLeftToOut.setTranslateX(0);
		Main.coverviewController.transMidToLeft.setTranslateX(90);
		Main.coverviewController.transRightToMid.setTranslateX(330);
		Main.coverviewController.transRightToMid.setTranslateY(75);
		Main.coverviewController.transOutToRight.setTranslateX(480);
		Main.coverviewController.transOutToRight.setTranslateY(75);
		MidinFront = false;
		
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
		title.setText(Main.tracklist.getCurrentTrack().getTitle());
		albumartist.setText(Main.tracklist.getCurrentTrack().getAlbumartist());
		artist.setText(Main.tracklist.getCurrentTrack().getArtist());
		album.setText(Main.tracklist.getCurrentTrack().getAlbum());
		trackNr.setText(String.valueOf(Main.tracklist.getCurrentTrack().getTrackNr()));
		year.setText(String.valueOf(Main.tracklist.getCurrentTrack().getYear()));
		bpm.setText(String.valueOf(Main.tracklist.getCurrentTrack().getBpm()));
		rating.setText(String.valueOf(Main.tracklist.getCurrentTrack().getRating()));
		trackoverlay.setVisible(true);
		trackoverlay.toFront();
	}
	@FXML
	private void hideTrackoverlay(){
		trackoverlay.setVisible(false);
	}
}
