package utils;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Flash extends Popup { //short flash to show change of settings
	private static final double SEC = 0.5;
	
	public Flash(Image i) {
		//Position
		setX(C.SCREEN_WIDTH - 40 - 15);
		setY(15);
		//Content
		ImageView img = new ImageView(i);
		Circle background = new Circle(20, Color.WHITE);
		StackPane p = new StackPane();
		p.getChildren().addAll(background,img);
		getContent().addAll(p);

		//Timeline
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(SEC), e -> hide()));
		addEventHandler(WindowEvent.WINDOW_SHOWN, e -> timeline.play());
	}
	

}
