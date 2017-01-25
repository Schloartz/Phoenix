package com.nash.phoenix;
	
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.nash.phoenix.service.Database;
import com.nash.phoenix.service.Mediaplayer;
import com.nash.phoenix.service.Tracklist;
import com.nash.phoenix.utils.C;
import com.nash.phoenix.utils.Flash;
import com.nash.phoenix.utils.TrackInfo;
import com.nash.phoenix.views.controls.ControlsPresenter;
import com.nash.phoenix.views.cover.CoverPresenter;
import com.nash.phoenix.views.database.DatabasePresenter;
import com.nash.phoenix.views.main.MainPresenter;
import com.nash.phoenix.views.main.MainView;
import com.nash.phoenix.views.menu.MenuPresenter;
import com.nash.phoenix.views.settings.SettingsPresenter;
import com.nash.phoenix.views.tracklist.TracklistPresenter;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import static com.sun.jna.platform.win32.WinUser.GWL_STYLE;


public class App extends Application implements IntellitypeListener, HotkeyListener{
	public static Stage stage;
	public static ContextMenu contextMenu;
	private static TrackInfo trackInfo;
	private static Preferences prefs = Preferences.userRoot().node(App.class.getName()); //includes all user settings for database
	//Main service providers: mediaplayer, database, tracklistView
	public static Mediaplayer mediaplayer;
	public static Database database;
	public static Tracklist tracklist;
	//Views
	private static MainView mainView;
	//Presenters
	public static MainPresenter mainPresenter;
	public static DatabasePresenter databasePresenter;
	public static MenuPresenter menuPresenter;
	public static CoverPresenter coverPresenter;
	public static TracklistPresenter tracklistPresenter;
	public static ControlsPresenter controlsPresenter;
	public static SettingsPresenter settingsPresenter;

	public static double starttime;


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
				tracklist.restoreTracklist();
				updateProgress(2.5,3.5);
				updateMessage("Loading GUI...");
				App.stage = stage;
				mainView = new MainView(); //load MainView
				C.printTime("Load App root", starttime); //~500ms
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
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon_phoenix_medium.png"))); //icon on taskbar
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
		Stage splashView = new Stage();
		ImageView img = new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_phoenix_large.png")));
		Scene splashScene = new Scene(new StackPane(img), 185,343);
		splashScene.setFill(Color.TRANSPARENT);
		stage.setScene(splashScene);



		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				stage.hide();
				//Stage
				Scene scene = new Scene(mainView.getView(),800,600);
				scene.setOnKeyReleased(e -> {
					KeyCode key = e.getCode();
					if(key==KeyCode.ALT){
						menuPresenter.toggleView();
					}else if(key==KeyCode.CONTROL){
						tracklistPresenter.foldTracklist();
					}
				});
				stage.setScene(scene);
				stage.setTitle("Phoenix");
				stage.show();
				databasePresenter.getSearch().requestFocus();
				C.printTime("Show App", starttime); //~500ms

				//Stuff for minimizing by click on taskbaricon (thx to brian from stackoverflow https://stackoverflow.com/questions/26972683/javafx-minimizing-undecorated-stage)
				long lhwnd = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
				Pointer lpVoid = new Pointer(lhwnd);
				HWND hwnd = new HWND(lpVoid);
				final User32 user32 = User32.INSTANCE;
				int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
				//System.out.println(Integer.toBinaryString(oldStyle));
				int newStyle = oldStyle | 0x00020000;//WS_MINIMIZEBOX
				//System.out.println(Integer.toBinaryString(newStyle));
				user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);

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
					Desktop.getDesktop().open(new File(mainPresenter.lastSelected.getPath()).getParentFile());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		MenuItem updateFolder = new MenuItem("Update folder");
		updateFolder.setOnAction(event -> { //updates tags for song
			ArrayList<File> file = new ArrayList<>();
			file.add(new File(mainPresenter.lastSelected.getPath()).getParentFile());
			database.updateEntries(file);
		});
		contextMenu.getItems().addAll(updateFolder, openFolder);
	}

	/**
	 * Shuts down all services (JIntellitype, database, mediaplayer, tracklist) and frees ressources
	 */
	public static void shutdown() {
		JIntellitype.getInstance().cleanUp();
		if(database.isRunning()){ //close database if it is running
			database.shutdown();
		}
		mediaplayer.disposeOldPlayer();
		tracklist.saveTracklist();
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
	        		App.settingsPresenter.toggleNumInput();
	        		if(App.settingsPresenter.numInputEnabled()){
						new Flash(new Image(getClass().getResourceAsStream("/resources/icons/icon_numpad_on.png"))).show(stage);
					}else{
						new Flash(new Image(getClass().getResourceAsStream("/resources/icons/icon_numpad_off.png"))).show(stage);
					}
					break;
				case C.KEY_SHUFFLE:
					if(mediaplayer.shufflePressed() && mainPresenter.showFlash){ //if shuffle-input is valid and flash is enabled
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
					if(mainPresenter.showFlash) {
						new Flash(controlsPresenter.returnNextAutodjIcon(mediaplayer.getStatus().getAutodj())).show(stage);
					}
					Platform.runLater(() -> controlsPresenter.autodjPressed());
					break;
					case C.KEY_TRACKINFO:
						//Show trackInfo if track is present
						if(mediaplayer.trackLoaded()){
							trackInfo.updateCoverTextRating(App.tracklist.getCurrentTrack(), App.coverPresenter.getMidCoverImage());
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
	public static void setComplete(long t){ //sets the time of the last full database update
		prefs.putLong("complete", t);
		settingsPresenter.updateComplete();
	}
	public static void setUpdated(long t){ //sets the time of the last incremental database update
		prefs.putLong("updated", t);
		if(settingsPresenter !=null){
			settingsPresenter.updateUpdate();
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
	public static MainPresenter getMainPresenter(){
		return mainPresenter;
	}

}
