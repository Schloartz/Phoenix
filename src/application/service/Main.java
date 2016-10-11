package application.service;
	
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;

import application.controllers.ControlsController;
import application.controllers.CoverviewController;
import application.controllers.DatabaseController;
import application.controllers.MenuController;
import application.controllers.SettingsController;
import application.controllers.TracklistController;
import application.controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.C;
import utils.Flash;
import utils.TrackInfo;


public class Main extends Application implements IntellitypeListener, HotkeyListener{
	public static Stage stage;
	public static BorderPane root;
	public static ContextMenu contextMenu;
	static TrackInfo trackInfo;
	private static Preferences prefs = Preferences.userRoot().node(Main.class.getName()); //includes all user settings for database
	//PlayerController & Database
	public static Mediaplayer mediaplayer;
	public static Database database;
	public static Tracklist tracklist;
	//Controllers
	public static MainController mainController;
	public static DatabaseController databaseController;
	public static MenuController menuController;
	public static CoverviewController coverviewController;
	public static TracklistController tracklistController;
	public static ControlsController controlsController;
	public static SettingsController settingsController;
	
	private static double starttime;
	
	public static void main(String[] args){
		starttime = System.currentTimeMillis();
		
		mediaplayer = new Mediaplayer();
		tracklist = new Tracklist();
		database = new Database();
		
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		setStage(primaryStage);
		root = null;
		//Load FXML
		try {
			root = FXMLLoader.load(getClass().getResource("/resources/fxml/MainWindowView.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Stage
		Scene scene = new Scene(root,800,600);
		scene.setOnKeyReleased(e -> {
        	if(e.getCode()==KeyCode.ALT){
        		menuController.toggleView();
        	}
        });
		//Global Hotkeys
		JIntellitype.getInstance();
		JIntellitype.getInstance().addIntellitypeListener(this);
		JIntellitype.getInstance().addHotKeyListener(this);
		JIntellitype.getInstance().registerHotKey(C.KEY_BACKWARD, 0, C.KEY_NUM4); //0: no key associated
		JIntellitype.getInstance().registerHotKey(C.KEY_PLAYPAUSE, 0, C.KEY_NUM5);
		JIntellitype.getInstance().registerHotKey(C.KEY_FORWARD, 0, C.KEY_NUM6);
		JIntellitype.getInstance().registerHotKey(C.KEY_AUTODJ, 0, C.KEY_NUM8);
		JIntellitype.getInstance().registerHotKey(C.KEY_SHUFFLE, 0, C.KEY_NUM2);
		//init ContextMenu
		initContextMenu();
		trackInfo = new TrackInfo(); 
		//Stage
		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle("Phoenix");
		primaryStage.centerOnScreen();
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icons/icon_phoenix_large.png")));
		primaryStage.show();
		databaseController.getSearch().requestFocus();
		System.out.println("Application has been started ("+((System.currentTimeMillis()-starttime)/1000)+" s)");

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
		MenuItem updateFolder = new MenuItem("Update song");
		updateFolder.setOnAction(event -> { //updates tags for song
			ArrayList<File> file = new ArrayList<>();
			file.add(new File(mainController.lastSelected.getPath()));
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
				case C.KEY_BACKWARD: //PlayerController.backwardPressed();
					break;
				case C.KEY_PLAYPAUSE: mediaplayer.playPausePressed();
					break;
				case C.KEY_FORWARD: mediaplayer.forwardPressed();
					break;
				case C.KEY_AUTODJ: 
					controlsController.autodjPressed();
					if(mainController.showFlash)
						new Flash(Main.controlsController.autodj.getImage()).show();
					break;
				case C.KEY_SHUFFLE:
					if(mediaplayer.shufflePressed() && mainController.showFlash){ //if shuffle-input is valid and flash is enabled
						new Flash(new Image(getClass().getResourceAsStream("/resources/icons/icon_shuffle.png"))).show();
					}
					break;
				}
	        }
	   });
		
		
	}

	private static void setStage(Stage _stage) {
		stage = _stage;
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
}
