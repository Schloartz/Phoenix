package application.service;
	
import application.controllers.*;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.C;
import utils.Flash;
import utils.TrackInfo;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;


public class Main extends Application implements IntellitypeListener, HotkeyListener{
	public static Stage stage;
	@FXML
	private static BorderPane root;
	public static ContextMenu contextMenu;
	private static TrackInfo trackInfo;
	private static Preferences prefs = Preferences.userRoot().node(Main.class.getName()); //includes all user settings for database
	//PlayerController & Database
	public static Mediaplayer mediaplayer;
	public static Database database;
	public static Tracklist tracklist;
	//Controllers
	public static MainController mainController;
	public static SplashController splashController;
	public static DatabaseController databaseController;
	public static MenuController menuController;
	public static CoverviewController coverviewController;
	public static TracklistController tracklistController;
	public static ControlsController controlsController;
	public static SettingsController settingsController;
	
	static double starttime;


	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		starttime = System.currentTimeMillis();

		final Task init = new Task() {

			@Override
			protected Object call() throws Exception {
				updateProgress(0.5,3.5);
				updateMessage("Starting database...");
				mediaplayer = new Mediaplayer();
				tracklist = new Tracklist();
				database = new Database();
				updateProgress(2.5,3.5);
				updateMessage("Loading GUI...");
				Main.stage = stage;
				root = null;
				//Load FXML
				try {
					root = FXMLLoader.load(getClass().getResource("/resources/fxml/MainWindowView.fxml"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				C.printTime("Load Main root", starttime); //~500ms
				updateProgress(3.0,3.5);
				updateMessage("Showing GUI...");
				//init ContextMenu
				initContextMenu();
				trackInfo = new TrackInfo();
				return null;
			}
		};
		stage.initStyle(StageStyle.TRANSPARENT);
		//show splash screen
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icons/icon_phoenix_medium.png"))); //icon on taskbar
		showSplash(stage,init);
		C.printTime("Show Splash", starttime); //~500ms

		//Global Hotkeys
		HotkeyListener hotkeyListener = this;
		JIntellitype.getInstance();
		JIntellitype.getInstance().addIntellitypeListener(this); //intellitype for playpause mediabutton
		JIntellitype.getInstance().addHotKeyListener(hotkeyListener); //hotkeylistener for num inputs
		toggleNumInput(true);
	}



	private void showSplash(Stage stage, Task<?> task) {
		//Scene + Stage
		StackPane r = null;
		try {
			r = FXMLLoader.load(getClass().getResource("/resources/fxml/SplashView.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene splashScene = new Scene(r, 185,343);
		splashScene.setFill(Color.TRANSPARENT);
		stage.setScene(splashScene);
		//binding progressproperties
		splashController.getSplashProgress().progressProperty().bind(task.progressProperty());
		splashController.getSplashLabel().textProperty().bind(task.messageProperty());


		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				splashController.getSplashProgress().progressProperty().unbind();
				splashController.getSplashLabel().textProperty().unbind();
				splashController.getSplashProgress().setProgress(1);
				stage.hide();
				//Stage
				Scene scene = new Scene(root,800,600);
				scene.setOnKeyReleased(e -> {
					if(e.getCode()==KeyCode.ALT){
						menuController.toggleView();
					}
				});
				stage.setScene(scene);
				stage.setTitle("Phoenix");
				stage.show();
				databaseController.getSearch().requestFocus();
				C.printTime("Show Main", starttime); //~500ms
				System.out.println("Application has been started ("+((System.currentTimeMillis()-starttime)/1000)+" s)");
			}
		});

		stage.centerOnScreen();
		stage.show();
		new Thread(task).start();
	}
	
	private void initContextMenu(){
		contextMenu = new ContextMenu();
		MenuItem openFolder = new MenuItem("Open folder");
		openFolder.setOnAction(e -> {
			if (Desktop.isDesktopSupported()) {
			    try {
					Desktop.getDesktop().open(new File(mainController.lastSelected.getPath()).getParentFile());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		MenuItem updateFolder = new MenuItem("Update folder");
		updateFolder.setOnAction(event -> { //updates tags for song
			ArrayList<File> file = new ArrayList<>();
			file.add(new File(mainController.lastSelected.getPath()).getParentFile());
			database.updateEntries(file);
		});
		contextMenu.getItems().addAll(updateFolder, openFolder);
	}
	
	public static void shutdown() { //shuts down all services and frees ressources
		JIntellitype.getInstance().cleanUp();
		if(database.isRunning()){ //close database if it is running
			database.shutdown();
		}
		mediaplayer.disposeOldPlayer();
    	Platform.exit();
        System.exit(0);
	}
	@Override
	public void onIntellitype(int key){
    	if(key==JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE){
			Platform.runLater(()-> mediaplayer.playPausePressed());
    	}
	}
	
	@Override
	public void onHotKey(int key) {
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	switch(key){
	        	case C.KEY_NUMTOGGLE:
	        		Main.settingsController.toggleNumInput();
					break;
				case C.KEY_SHUFFLE:
					if(mediaplayer.shufflePressed() && mainController.showFlash){ //if shuffle-input is valid and flash is enabled
						new Flash(new Image(getClass().getResourceAsStream("/resources/icons/icon_shuffle.png"))).show(stage);
					}
					break;
				case C.KEY_BACKWARD: mediaplayer.backwardPressed();
					break;
				case C.KEY_PLAYPAUSE: mediaplayer.playPausePressed();
					break;
				case C.KEY_FORWARD: mediaplayer.forwardPressed();
					break;
				case C.KEY_AUTODJ:
					if(mainController.showFlash) {
						new Flash(controlsController.returnNextAutodjIcon(mediaplayer.getStatus().getAutodj())).show(stage);
					}
					Platform.runLater(() -> controlsController.autodjPressed());
					break;
					case C.KEY_TRACKINFO:
						//Show trackInfo if track is present
						if(mediaplayer.trackLoaded()){
							trackInfo.updateCoverTextRating(Main.tracklist.getCurrentTrack(), Main.coverviewController.getMidCoverImage());
							trackInfo.show(stage);
						}else{
							System.out.println("ERROR cannot display trackinfo if no track is playing right now");
						}
						break;
				}
	        }
	   });
		
		
	}


	public static long getUpdated(){ //returns the time of the last incremental database update
		return prefs.getLong("updated",0);
	}
	public static long getComplete(){ //returns the time of the last full database update
		return prefs.getLong("complete", 0);
	}
	static void setComplete(long t){ //sets the time of the last full database update
		prefs.putLong("complete", t);
		settingsController.updateComplete();
	}
	static void setUpdated(long t){ //sets the time of the last incremental database update
		prefs.putLong("updated", t);
		if(settingsController!=null){
			settingsController.updateUpdate();
		}
	}

	public static void toggleNumInput(boolean bool){ //enables/disables input via numpad
		if(bool){
			//JIntellitype.getInstance().addHotKeyListener(hotkeyListener);
			JIntellitype.getInstance().registerHotKey(C.KEY_NUMTOGGLE, 0, C.KEY_PAUSE);
			JIntellitype.getInstance().registerHotKey(C.KEY_SHUFFLE, 0, C.KEY_NUM2);
			JIntellitype.getInstance().registerHotKey(C.KEY_BACKWARD, 0, C.KEY_NUM4); //0: no key associated
			JIntellitype.getInstance().registerHotKey(C.KEY_PLAYPAUSE, 0, C.KEY_NUM5);
			JIntellitype.getInstance().registerHotKey(C.KEY_FORWARD, 0, C.KEY_NUM6);
			JIntellitype.getInstance().registerHotKey(C.KEY_AUTODJ, 0, C.KEY_NUM8);
			JIntellitype.getInstance().registerHotKey(C.KEY_TRACKINFO, 0, C.KEY_NUM9);
		}else{
			//JIntellitype.getInstance().removeHotKeyListener(hotkeyListener);
			JIntellitype.getInstance().unregisterHotKey(C.KEY_SHUFFLE);
			JIntellitype.getInstance().unregisterHotKey(C.KEY_BACKWARD);
			JIntellitype.getInstance().unregisterHotKey(C.KEY_PLAYPAUSE);
			JIntellitype.getInstance().unregisterHotKey(C.KEY_FORWARD);
			JIntellitype.getInstance().unregisterHotKey(C.KEY_AUTODJ);
			JIntellitype.getInstance().unregisterHotKey(C.KEY_TRACKINFO);
		}
	}
	public static BorderPane getRoot(){ return root;}
}
