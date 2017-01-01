package utils;

import javafx.scene.input.DataFormat;

public final class C { //CONSTANT VARIABLES: contains all final variables
	//Display
	public static double SCREEN_WIDTH = 1920;
	public static double SCREEN_HEIGHT = 1080;
	//GUI
	public static final double WIDTH = 800; //width of the scene
	public static final double HEIGHT= 600; //height of the scene

	//public static final double BIGCOVER = 300; //size of big cover in the center - for documentation only
	//public static final double SMALLCOVER = 150; //size of small covers at the side - for documentation only
	public static final double SMALLCOVER_VISIBLE = 90; //90 of 150 visible, 60 px hidden

	public static final double MENU_HEIGHT = 25; //height of menubar (observed)
	public static final double CONTROLS_HEIGHT = 78; //height of controls (observed)

	public static final double TRANSFORM = 32.5; //extent of transform of left and right covers
	public static final double X_MARGIN = (WIDTH*3/4 - 90 - 90 - 300) /2; //60; Margin left and right
	public static final double Y_MARGIN = (HEIGHT - MENU_HEIGHT - CONTROLS_HEIGHT - 300) /2; //98.5; Margin top and bottom
	
	//Hotkey support for jintellitype (Numpad)
	///Internal KeyCodes (arbitrary): correspond to numpad keys
	public static final int KEY_SHUFFLE = 2;
	public static final int KEY_BACKWARD = 4;
	public static final int KEY_PLAYPAUSE = 5;
	public static final int KEY_FORWARD = 6;
	public static final int KEY_AUTODJ = 8;
	public static final int KEY_TRACKINFO = 9;
	public static final int KEY_NUMTOGGLE = 0;

	///KeyCodes from System (fixed)
	public static final int KEY_NUM2 = 98;
	public static final int KEY_NUM4 = 100;
	public static final int KEY_NUM5 = 101;
	public static final int KEY_NUM6 = 102;
	public static final int KEY_NUM8 = 104;
	public static final int KEY_NUM9 = 105;
	public static final int KEY_PAUSE = 19;

	//Clipboard functionality
	public static final DataFormat trackDataFormat = new DataFormat("com.application.utils.Track");

	//Time output
	private static final boolean printTime = true;


	public static void printTime(String par, double start){ //prints the time with a parameter
		if(printTime)
			System.out.println(par + ": "+(System.currentTimeMillis()-start));
	}
}
