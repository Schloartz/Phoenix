package utils;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Flash extends Stage{ //short flash to show change of settings
	double sec = 0.5;
	
	public Flash(Node i){
		//Init GUI
		setAlwaysOnTop(false);
		setX(C.SCREEN_WIDTH-40-15);
		setY(15);
		initStyle(StageStyle.TRANSPARENT);
		
		StackPane p = new StackPane();
		p.setStyle("-fx-background-radius: 30;");
		

		StackPane.setAlignment(i, Pos.CENTER);
		p.getChildren().add(i);
		
		Scene scene = new Scene(p,40,40);
		scene.setFill(Paint.valueOf("transparent"));
		setScene(scene);
		toFront();
		
		//Timeline
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(sec),e -> hide()));
		addEventHandler(WindowEvent.WINDOW_SHOWN, e -> timeline.play());
	}
	

}
