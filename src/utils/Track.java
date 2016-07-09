package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import application.service.Main;

public class Track extends MP3File{ //represents a physical mp3 file with tags and properties of current player
	
	int id = -1; //id in database (also shows if track is listed in database (!=-1)
	MP3File mp3; //representation of mp3
	String title, albumartist, artist, album, path;
	int trackNr, year, rating;
	double bpm;
	BufferedImage cover;
	int order = -1; //order of the track in tracklist
	boolean active = false; //if track is currently being played/paused
	
	public Track(String _path){ //initializes track with a path, rest is read-in from file system
		try {
			mp3 = (MP3File)AudioFileIO.read(new File(_path));
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
			System.out.println("ERROR reading mp3 ("+_path+")");
			e.printStackTrace();
			return;
		}
		path = _path;
		//Tags
		title = readTitle();
		albumartist = readAlbumArtist();
		artist = readArtist();
		album = readAlbum();
		trackNr = readTrackNr();
		year = readYear();
		bpm = readBPM();
		rating = readRating();
		///cover excluded due to performance issues
	}
	
	public Track(int _id, String _path, String _title, String _albumartist, String _artist, String _album, int _trackNr, int _year, double _bpm, int _rating){ //initalizes track with (almost) all variables
		id = _id;
		if(_id==-1){
			System.out.println(_path);
		}
		path = _path;
		title = _title;
		albumartist = _albumartist;
		artist = _artist;
		album = _album;
		trackNr = _trackNr;
		year = _year;
		bpm = _bpm;
		rating = _rating;
		///cover excluded due to performance issues
		
	}
	private MP3File readMP3(){
		try {
			MP3File m = (MP3File)AudioFileIO.read(new File(path));
			return m;
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
			System.out.println("ERROR reading mp3 ("+path+")");
			e.printStackTrace();
			return null;
		}
	}
	
	public int readTrackNr() { //reads Track number of mp3 file, returns 0 if no tag was found
		if(mp3.getTag()!=null && mp3.getTag().getFirst(FieldKey.TRACK)!=""){
			int t = Integer.parseInt(mp3.getTag().getFirst(FieldKey.TRACK));
			return t;
		}else{
			return 0;
		}
	}

	public BufferedImage readCover() { //returns cover as bufferedimage OR null if there is no cover
		BufferedImage bi = null;
		if(mp3.getTag()!=null && mp3.getTag().getFirstArtwork()!=null){
			Artwork artwork = mp3.getTag().getFirstArtwork();
			try {
				bi = (BufferedImage) artwork.getImage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bi;
	}
	public int readRating(){ //returns rating of this mp3 from tag(0-255 -> 0-5) or 0 if not found;
		if(mp3.getTag()!=null && mp3.getTag().getFirst(FieldKey.RATING)!=null){
			int raw_rating = 0; //raw value
			int conv_rating = 0; //converted rating
			String str = null;
			try{
				str = mp3.getTag().getFirst(FieldKey.RATING); //parse Rating from tag
				if(str.equals("")){ //no rating corresponds to "" in tag!
					raw_rating = 0;
				}else{
					raw_rating = Integer.valueOf(str);
				}
			}catch(NumberFormatException e){
				System.out.println("ERROR parsing rating (" + path + ")");
				e.printStackTrace();
			}
			//Normal formatting: 0=0; 1=1; 2=64; 3=128; 4=196; 5=255
			if(raw_rating==0){
				conv_rating = 0;
			}else if(raw_rating>=1 && raw_rating < 33){ //1
				conv_rating = 1;
			}else if(raw_rating>=33 && raw_rating < 96){//2
				conv_rating = 2;
			}else if(raw_rating>=96 && raw_rating < 160){//3
				conv_rating = 3;
			}else if(raw_rating>=160 && raw_rating < 228){//4
				conv_rating = 4;
			}else if(raw_rating>=228){//5
				conv_rating = 5;
			}
//			System.out.println("rating from "+raw_rating + " to " + conv_rating);
			return conv_rating;
		}else{
			return 0;
		}
	}

	

	
	public String readTitle(){ //returns title of track
		if(mp3.getTag()!=null && mp3.getTag().getFirst(FieldKey.TITLE)!=""){
			String s = mp3.getTag().getFirst(FieldKey.TITLE);
			return s;
		}else{
			return null;
		}
	}
	public String readArtist(){ //returns artist of track
		if(mp3.getTag()!=null && mp3.getTag().getFirst(FieldKey.ARTIST)!=""){
			String s = mp3.getTag().getFirst(FieldKey.ARTIST);
			return s;
		}else{
			return null;
		}
	}
	
	public String readAlbumArtist(){ //returns albumartist of track
		if(mp3.getTag()!=null && mp3.getTag().getFirst(FieldKey.ALBUM_ARTIST)!=""){
			String s = mp3.getTag().getFirst(FieldKey.ALBUM_ARTIST);
			return s;
		}else{
			return null;
		}
	}
	
	public String readAlbum(){ //returns album of track
		if(mp3.getTag()!=null && mp3.getTag().getFirst(FieldKey.ALBUM)!=""){
			String s = mp3.getTag().getFirst(FieldKey.ALBUM);
			return s;
		}else{
			return null;
		}
	}
	public int readYear(){ //returns year of track; tag normally contains one of the following ("   0";"";"2012-05-15";"2012")
		int y = 0; //int year to be determined
		if(mp3.getTag()!=null && !mp3.getTag().getFirst(FieldKey.YEAR).isEmpty() && !mp3.getTag().getFirst(FieldKey.YEAR).equals("   0")){
			String year = mp3.getTag().getFirst(FieldKey.YEAR);
			if(year.length()>4){
				year = year.substring(0, 4);
			}
			try{
				y = Integer.parseInt(year);
			}catch(NumberFormatException e){
				System.out.println("ERROR while trying to get the YEAR-Tag of track (" + path + "). Invalid YEAR-Tag ("+mp3.getTag().getFirst(FieldKey.YEAR)+")");
			}
			
		}
		return y;
	}
	public double readBPM(){ //returns BPM of track
		if(mp3.getTag()!=null && mp3.getTag().getFirst(FieldKey.BPM)!=""){
			Double t = Double.parseDouble(mp3.getTag().getFirst(FieldKey.BPM));
			return t;
		}else{
			return 0;
		}
	}
	public void setRating(int r){ //TODO does not work if mp3 has no tag at all
		int r_conv = 0; //converted rating
		switch(r){ //convert rating
		case 0: r_conv = 0; //0
			break;
		case 1: r_conv = 1; //1
			break;
		case 2: r_conv = 64; //2
			break;
		case 3: r_conv = 128; //3
			break;
		case 4: r_conv = 196; //4
			break;
		case 5: r_conv = 255; //5
			break;
		}
		rating = r_conv;
		//save rating in tag
		if(mp3==null){
			mp3 = readMP3();
		}
		try {
			mp3.getTag().setField(FieldKey.RATING,String.valueOf(r_conv));
			mp3.commit();
		} catch (KeyNotFoundException | FieldDataInvalidException | CannotWriteException e) {
			System.out.println("ERROR writing the rating to ("+path+")");
			e.printStackTrace();
		}
		//update rating in database
		if(id!=-1){ //if mp3 is listed in database
			Main.database.updateRating(id, r);
		}
	
	}
	public void setActive(boolean value) {
		active = value;
	}

	public String getPath() {
		return path;
	}

	public boolean isActive() {
		return active;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAlbumartist() {
		return albumartist;
	}
	public void setAlbumartist(String albumartist) {
		this.albumartist = albumartist;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public int getTrackNr() {
		return trackNr;
	}
	public void setTrackNr(int trackNr) {
		this.trackNr = trackNr;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getBpm() {
		return bpm;
	}
	public void setBpm(int bpm) {
		this.bpm = bpm;
	}
	public int getRating() {
		return rating;
	}

	public BufferedImage getCover() {
		if(cover==null){ //if cover has not been loaded, try to load it another time
			cover = readCover();
		}
		return cover;
	}
	public void setCover(BufferedImage cover) {
		this.cover = cover;
	}
	public int getId(){
		return id;
	}
}
