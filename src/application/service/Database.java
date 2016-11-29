package application.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.Track;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

/*Examples of Queries:
 * s.executeUpdate("ALTER TABLE " + tableName + " ADD filename VARCHAR(100) ");
 * s.executeUpdate("DELETE FROM " + tableName + " WHERE NUM in (1956)");
 * s.executeUpdate("ALTER TABLE " + tableName + " ADD path varchar(500)");
 * s.executeUpdate("ALTER TABLE " + tableName + " DROP COLUMN NAME");
 * s.executeUpdate("ALTER TABLE " + tableName + " ADD ID INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)");
 * s.executeUpdate("RENAME COLUMN "+tableName + ".folderpath TO path");
 */

public class Database{
	private String physicalDb = "E:/Musik/";
	private Connection con = null;
	private String tableName = "Tracks";
	private boolean running = false; //if Database-Services have been started yet
	
	private PreparedStatement psInsert;
	private int searchedFiles = 0;
	private int maxFiles = 0;
	private ObservableList<Track> results; //results of last search
  
  	public Database(){
  		results = FXCollections.observableArrayList();
  		//Start apache server
  		try{
        	Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            //Get a connection
            con = DriverManager.getConnection("jdbc:derby:MusicDatabase;create=true");
            running = true;
            //Print last build date
            System.out.println("Database has been started.");
        }catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException se){
        	se.printStackTrace();
			return;
        }
        if(new File(physicalDb).exists()){
            updateEntries(findNewFolders());
            deleteOldEntries();
        }else{
            System.out.println("ERROR while trying to read your music files at <"+physicalDb+">. Is your hard drive connected?");
        }
  	}

  	void updateEntries(ArrayList<File> folders){ //updates entries in the database for the respecting folders
		if(!folders.isEmpty()) {
			for(File f:folders) {
				//optional: delete old files from database
				try {
					Statement s = con.createStatement();
					s.executeUpdate("DELETE FROM " + tableName + " WHERE path LIKE '" + getSQLSafeVersion(f.getPath()) + "%'");
				} catch (SQLException e2) {
					printSQLException(e2);
				}

				try { //prepare Statment
					psInsert = con.prepareStatement("INSERT INTO " + tableName + "(path, title, albumartist, artist, album, trackNr, yea, bpm, rating) VALUES (?,?,?,?,?,?,?,?,?)");
				} catch (SQLException e1) {
					printSQLException(e1);
				}
				//iterate through folders and add to DB
				searchedFiles = 0;
				searchAllSubfolders(f, false);
				try {
					psInsert.close();
				} catch (SQLException e) {
					printSQLException(e);
				}
				System.out.println("Updated records in database for folder <" + f.getName() + ">");
			}
			Main.setUpdated(System.currentTimeMillis());
		}
	}


	private ArrayList<File> findNewFolders(){ //returns all new folders in the database-folder
		ArrayList<File> folders = new ArrayList<>();

		for(File f:new File(physicalDb).listFiles()){
			if(f.isDirectory() && f.lastModified()>Main.getUpdated()){ //folder is "unknown", bc last build did take place before his last modification
				folders.add(f);
			}
		}
		return folders;
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
			printSQLException(e);
		}
  	}
  	
	private String getSQLSafeVersion(String str){ //returns a SQL safe version of a string (e.g. escaped '->'')
		if(str.contains("'")){
			str = str.replace("'", "''");
		}
		return str;
	}

	ArrayList<Track> getTracks(ArrayList<Integer> ids){
		ArrayList<Track> tracks = new ArrayList<>();
		ResultSet resultSet;

		String query="";
		for(int i:ids){
			if(query.equals("")){
				query = String.valueOf(i);
			}else{
				query = query +", "+i;
			}
		}
		try {
			Statement s = con.createStatement();
			resultSet = s.executeQuery("SELECT * FROM "+tableName+" WHERE ID IN (" + query + ")");
			while(resultSet.next()){
				tracks.add(new Track(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getInt(7), resultSet.getInt(8), resultSet.getDouble(9),resultSet.getInt(10)));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}

		return tracks;
	}

    public ObservableList<Track> search(String str_org){ //search the database for String <str> and return the results
    	results.clear();
		//escape quotes (')
		String str = getSQLSafeVersion(str_org);
		str = str.toLowerCase();

    	try{
    		ResultSet resultset;
    		Statement s = con.createStatement();
    		long dur = System.currentTimeMillis();
    		if(str.startsWith("\"") && str.endsWith("\"")){ //exact search with " "
				str = str.substring(1, str.length()-1);
    			resultset = s.executeQuery("SELECT * FROM "+tableName+" WHERE ("
        				+ "(LOWER(title) = '"+ str +"') OR "
        				+ "(LOWER(albumartist) = '"+ str +"') OR "
        				+ "(LOWER(artist) = '"+ str +"') OR "
        				+ "(LOWER(album) = '"+ str +"'))");
    		}else{
    			resultset = s.executeQuery("SELECT * FROM "+tableName+" WHERE ("
        				+ "(LOWER(title) LIKE '"+ str +"%') OR "
        				+ "(LOWER(albumartist) LIKE '"+ str +"%') OR "
        				+ "(LOWER(artist) LIKE '"+ str +"%') OR "
        				+ "(LOWER(album) LIKE '"+ str +"%'))");
    		}
    		if (resultset.next()) { //transfer results into array
    		    do {
    		    	results.add(new Track(resultset.getInt(1), resultset.getString(2), resultset.getString(3), resultset.getString(4), resultset.getString(5), resultset.getString(6), resultset.getInt(7), resultset.getInt(8), resultset.getDouble(9),resultset.getInt(10)));
    		    } while(resultset.next());
    		} else { //if no results, do deeper search
    			resultset = s.executeQuery("SELECT * FROM "+tableName+" WHERE LOWER(path) LIKE '%"+ str +"%'");
    			while(resultset.next()){
    		    	results.add(new Track(resultset.getInt(1), resultset.getString(2), resultset.getString(3), resultset.getString(4), resultset.getString(5), resultset.getString(6), resultset.getInt(7), resultset.getInt(8), resultset.getDouble(9),resultset.getInt(10)));
    		    }
    		}
    		resultset.close();
    		s.close();
    		System.out.println("Search finished: \""+str_org+"\", "+(System.currentTimeMillis()-dur)+" ms, "+results.size()+" results");
    	}catch (SQLException se){
			printSQLException(se);
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
		Thread rebuild = new Thread(() -> {
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
            countAllFiles(new File(physicalDb));
            maxFiles = searchedFiles;
            //iterate through folders and add to DB
            searchedFiles = 0;
            searchAllSubfolders(new File(physicalDb), true);
            try {
                psInsert.close();
            } catch (SQLException e) {
                printSQLException(e);
            }
            //save build time in preferences
            long lastBuild = System.currentTimeMillis();
            Main.setComplete(lastBuild);
            System.out.println("Set last complete build to "+lastBuild);

            long duration = (System.currentTimeMillis() - startingTime)/1000; //duration in s
            System.out.println("Finished rebuilding Database ("+searchedFiles+" Files in "+(duration-duration%60)/60+"m "+duration%60+"s)");
            Main.settingsController.hideProgress();
        });
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
				Main.settingsController.updateRebuildingProgress(searchedFiles, maxFiles);
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

	Track requestAutoDJTrack(Track t, int mode) { //returns a track that is similar to the current track in tracklist
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
    		int size;
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

	boolean isRunning() {
		return running;
	}
}