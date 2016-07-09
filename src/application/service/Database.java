package application.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.Track;

/*Examples of Queries:
 * s.executeUpdate("ALTER TABLE " + tableName + " ADD filename VARCHAR(100) ");
 * s.executeUpdate("DELETE FROM " + tableName + " WHERE NUM in (1956)");
 * s.executeUpdate("ALTER TABLE " + tableName + " ADD path varchar(500)");
 * s.executeUpdate("ALTER TABLE " + tableName + " DROP COLUMN NAME");
 * s.executeUpdate("ALTER TABLE " + tableName + " ADD ID INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)");
 * s.executeUpdate("RENAME COLUMN "+tableName + ".folderpath TO path");
 */

public class Database{
	private String dbPath = "E:/Musik/";
	private Connection con = null;
	private String tableName = "Tracks";
	private boolean running = false; //if Database-Services have been started yet
	
	private PreparedStatement psInsert;
	private int searchedFiles = 0;
	private int maxFiles = 0;
	private ObservableList<Track> results; //results of last search
  
  	public Database(){
  		results = FXCollections.observableArrayList();
  		
  		try{
        	Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            //Get a connection
            con = DriverManager.getConnection("jdbc:derby:MusicDatabase;create=true");
            running = true;
            //Print last build date
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            String lastBuild = sdf.format(Main.prefs.getLong("lastBuild", 0));
            System.out.println("Database has been started. Last Build: "+lastBuild);
        }catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException se){
        	se.printStackTrace();
        }
  		
  		updateNewEntries();
  		deleteOldEntries();
  	}
    
  	private void deleteOldEntries(){ //deletes file corpses i.e. deleted files
  		int del = 0;
		try {	
			Statement s = con.createStatement();
			ResultSet res = s.executeQuery("SELECT ID, path FROM "+tableName);
			String str ="(";
			while(res.next()){
	  			if(!new File(res.getString(2)).exists()){
	  				str += res.getInt(1)+", "; //adding IDs to String
	  				del++;
	  			}
	  		}
			if(!str.equals("(")){ //not empty
				str = str.substring(0,str.length()-2) + ")";
				s.executeUpdate("DELETE FROM "+tableName+" WHERE ID IN "+str);
				System.out.println("Deleted "+del+" old entries");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
  	}
  	
  	private void updateNewEntries(){ //updates all new entries in the database-folder
  		File db = new File(dbPath);
  		if(db.exists()){
	  		boolean updated = false;
	  		
	  		for(File f:db.listFiles()){
	  			if(f.isDirectory()){
	  				if(f.lastModified()>Main.prefs.getLong("lastBuild", 0)){ //folder is "unknown", bc last build did take place before his last modification
	  					//optional: delete entries from folder
						try {
							Statement s = con.createStatement();
							s.executeUpdate("DELETE FROM "+tableName+" WHERE path LIKE '"+f.getPath()+"%'");
						} catch (SQLException e2) {
							e2.printStackTrace();
						}
	  					
	  					try { //prepare Statment
	  						psInsert = con.prepareStatement("INSERT INTO "+tableName+"(path, title, albumartist, artist, album, trackNr, yea, bpm, rating) VALUES (?,?,?,?,?,?,?,?,?)");
	  					} catch (SQLException e1) {
	  						printSQLException(e1);
	  					}
	  					//iterate through folders and add to DB
	  					searchedFiles = 0;
	  					searchAllSubfolders(f,false);
	  					try {
	  						psInsert.close();
	  					} catch (SQLException e) {
	  						printSQLException(e);
	  					}
	  					System.out.println("Updated records in database for folder <"+f.getName()+">");
	  					updated = true;
	  				}
	  			}
	  		}
	  		if(updated){ //if one folder has been updated, update lastBuild
	  			long lastBuild = System.currentTimeMillis();
				Main.prefs.putLong("lastBuild", lastBuild);
	  		}
  		}else{
  			System.out.println("ERROR Skipped the updating of new entries because folder <"+dbPath+"> was not found.\nMake sure the hard drive is connected!");
  		}
  	}
  	
    public ObservableList<Track> search(String str){ //search the database for String <str> and return the results
    	results.clear();
    	try{
    		ResultSet resultset;
    		Statement s = con.createStatement();
    		long dur = System.currentTimeMillis();
    		if(str.startsWith("\"") && str.endsWith("\"")){ //exact search with " "
    			str = str.substring(1, str.length()-1);
    			resultset = s.executeQuery("SELECT * FROM "+tableName+" WHERE ("
        				+ "(LOWER(title) = '"+ str.toLowerCase() +"') OR "
        				+ "(LOWER(albumartist) = '"+ str.toLowerCase() +"') OR "
        				+ "(LOWER(artist) = '"+ str.toLowerCase() +"') OR "
        				+ "(LOWER(album) = '"+ str.toLowerCase() +"'))");
    		}else{
    			resultset = s.executeQuery("SELECT * FROM "+tableName+" WHERE ("
        				+ "(LOWER(title) LIKE '"+ str.toLowerCase() +"%') OR "
        				+ "(LOWER(albumartist) LIKE '"+ str.toLowerCase() +"%') OR "
        				+ "(LOWER(artist) LIKE '"+ str.toLowerCase() +"%') OR "
        				+ "(LOWER(album) LIKE '"+ str.toLowerCase() +"%'))");	
    		}
    		if (resultset.next()) { //transfer results into array
    		    do {
    		    	results.add(new Track(resultset.getInt(1), resultset.getString(2), resultset.getString(3), resultset.getString(4), resultset.getString(5), resultset.getString(6), resultset.getInt(7), resultset.getInt(8), resultset.getDouble(9),resultset.getInt(10)));
    		    } while(resultset.next());
    		} else { //if no results, do deeper search
    			resultset = s.executeQuery("SELECT * FROM "+tableName+" WHERE LOWER(path) LIKE '%"+ str.toLowerCase() +"%'");
    			while(resultset.next()){
    		    	results.add(new Track(resultset.getInt(1), resultset.getString(2), resultset.getString(3), resultset.getString(4), resultset.getString(5), resultset.getString(6), resultset.getInt(7), resultset.getInt(8), resultset.getDouble(9),resultset.getInt(10)));
    		    }
    		}
    		resultset.close();
    		s.close();
    		System.out.println("Search finished: \""+str+"\", "+(System.currentTimeMillis()-dur)+" ms, "+results.size()+" results");
    	}catch (SQLException se){
        	se.printStackTrace();
        }
    	return results;
    }
    
    
    void shutdown(){
    	try
        {
    		con.close();
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
            running = false;
        }
        catch (SQLException se)
        {
            if (( (se.getErrorCode() == 50000)
                    && ("XJ015".equals(se.getSQLState()) ))) {
                System.out.println("Derby shut down normally");
                // Note that for single database shutdown, the expected
                // SQL state is "08006", and the error code is 45000.
            } else {
                System.err.println("Derby did not shut down normally");
                printSQLException(se);
            }
        }
    }
    
    
    private void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
//            e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }



	public void rebuild() { //rebuilds the database from scratch
		Thread rebuild = new Thread(){
			public void run(){
				System.out.println("Starting to rebuild Database");
				long startingTime = System.currentTimeMillis();
				//Drop table from database
				try{
		    		Statement s = con.createStatement();
		    		s.executeUpdate("DROP TABLE "+tableName); 
		    		s.close();
		    	}catch(SQLException se){
		    		printSQLException(se);
		    	}
				//create table and columns
				try{
		    		Statement s = con.createStatement();
		    		s.close();
				}catch(SQLException se){
		    		printSQLException(se);
		    	}
				try{
		    		Statement s = con.createStatement();
		    		s.execute("CREATE TABLE "+tableName + "("
		    				+ "ID INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
		    				+ "path varchar(500),"
		    				+ "title varchar(500),"
		    				+ "albumartist varchar(500),"
		    				+ "artist varchar(500),"
		    				+ "album varchar(500),"
		    				+ "trackNr int,"
		    				+ "yea int,"
		    				+ "bpm double,"
		    				+ "rating int)");
		    		s.close();
		    	}catch(SQLException se){
		    		printSQLException(se);
		    	}
				//insert tracks
				try { //prepare Statment
					psInsert = con.prepareStatement("INSERT INTO "+tableName+"(path, title, albumartist, artist, album, trackNr, yea, bpm, rating) VALUES (?,?,?,?,?,?,?,?,?)");
				} catch (SQLException e1) {
					printSQLException(e1);
				}
				//count all folders in medialibrary
				searchedFiles = 0;
				countAllFiles(new File(dbPath));
				maxFiles = searchedFiles;
				//iterate through folders and add to DB
				searchedFiles = 0;
				searchAllSubfolders(new File(dbPath), true);
				try {
					psInsert.close();
				} catch (SQLException e) {
					printSQLException(e);
				}
				//save build time in preferences
				long lastBuild = System.currentTimeMillis();
				Main.prefs.putLong("lastBuild", lastBuild);
				System.out.println("Set lastBuild to "+lastBuild);
				
				long duration = System.currentTimeMillis() - startingTime;
				System.out.println("Finished rebuilding Database ("+searchedFiles+" Files in "+duration/1000+"s)");
				Main.sController.hideProgress();
			}
		};
		rebuild.start();
	}
	private void countAllFiles(File f){ //checks through all mp3-files in the folder and counts them
		 if(f.isDirectory()){ //if folder, look for files
			 for(File sub:f.listFiles()){
				 countAllFiles(sub);
			 }
		 }else if(f.isFile() && f.getName().endsWith(".mp3")){ //if mp3-file, add to database
			searchedFiles++;
		 }
	}
	private void searchAllSubfolders(File f, boolean update){ //checks through all mp3-files in the folder and adds them to Database; update: if progressbar needs to be updated in dialog
		 if(f.isDirectory()){ //if folder, look for files
			 for(File sub:f.listFiles()){
				 searchAllSubfolders(sub, update);
			 }
		 }else if(f.isFile() && f.getName().endsWith(".mp3")){ //if mp3-file, add to database
			 try {
				Track track = new Track(f.getPath());
				psInsert.setString(1, track.getPath());
				psInsert.setString(2, track.getTitle());
				psInsert.setString(3, track.getAlbumartist());
				psInsert.setString(4, track.getArtist());
				psInsert.setString(5, track.getAlbum());
				psInsert.setInt(6, track.getTrackNr());
				psInsert.setInt(7, track.getYear());
				psInsert.setDouble(8, track.getBpm());
				psInsert.setInt(9, track.getRating());
				psInsert.execute();
			} catch (SQLException e) {
				printSQLException(e);
			}
			searchedFiles++;
			if(update){
				Main.sController.updateRebuildingProgress(searchedFiles, maxFiles);
			}
		 }
	}



	public void updateRating(int id, int rating) { //updates the rating of the track in database 
		try{
    		Statement s = con.createStatement();
    		s.executeUpdate("UPDATE "+tableName+" SET rating="+rating+" WHERE id="+id);
    		s.close();
		}catch(SQLException se){
    		printSQLException(se);
    	}
	}

	public Track requestAutoDJTrack(Track t, int mode) { //returns a track that is similar to the current track in tracklist
		long dur = System.currentTimeMillis();
		String currentIds = Main.tracklist.getCurrentIds();
		
		try{
    		Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    		ResultSet rs = null;
    		if(mode==1){
    			//Random track considering bpm (dif<=15) and rating (!=1 OR 2)
    			rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE (bpm BETWEEN "+(t.getBpm()-15)+" AND "+(t.getBpm()+15)+") AND (rating NOT IN (1, 2)) AND (ID NOT IN "+currentIds+")");
    		}else if(mode==2){
    			//random track considering bpm (dif<=5) and rating (>3)
    			rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE (bpm BETWEEN "+(t.getBpm()-5)+" AND "+(t.getBpm()+5)+") AND rating>=3 AND (ID NOT IN "+currentIds+")");
    		}else if(mode==3){
    			//random track from artist/albumartist considering rating(!=1 OR 2)
    			if(t.getArtist()!=null && t.getAlbumartist()!=null){
    				rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE ((LOWER(artist)='"+t.getArtist().toLowerCase()+"') OR (LOWER(albumartist)='"+t.getAlbumartist().toLowerCase()+"')) AND (rating NOT IN (1, 2)) AND (ID NOT IN "+currentIds+")");
    			}else if(t.getArtist()==null){
    				rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE (LOWER(albumartist)='"+t.getAlbumartist().toLowerCase()+"') AND (rating NOT IN (1, 2)) AND (ID NOT IN "+currentIds+")");
    			}else if(t.getAlbumartist()==null){
    				rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE (LOWER(artist)='"+t.getArtist().toLowerCase()+"') AND (rating NOT IN (1, 2)) AND (ID NOT IN "+currentIds+")");
    			}else{
    				System.out.println("ERROR while trying to get an AutoDJ Track: Current track <"+t.getPath()+"> does not have an artist/albumartist");
    				return null;
    			}
    		}
    		
    		//count rows
    		int size= 0;
    		if (rs != null){  
    		  rs.beforeFirst();
    		  rs.last();
    		  size = rs.getRow();  
    		}else{
    			System.out.println("ERROR while trying to get an AutoDJ Track: No track found that matches the criteria");
    			return null;
    		}
    		if(size==0){
    			System.out.println("ERROR while trying to get an AutoDJ Track: No track found that matches the criteria");
    			return null;
    		}
    		Random rand = new Random();
    		int randomNum = rand.nextInt((size - 1) + 1) + 1;
    		rs.absolute(randomNum); //select a random row
    		Track tr = new Track(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getInt(8), rs.getDouble(9),rs.getInt(10));
    		System.out.println("Search (AutoDJ) finished ("+(System.currentTimeMillis()-dur)+" ms)");
    		return tr;
		}catch(SQLException se){
    		printSQLException(se);
    		return null;
    	}
	}

	public boolean isRunning() {
		return running;
	}
	
	public ObservableList<Track> getResults(){
		return results;
	}
}