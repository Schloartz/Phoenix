package com.nash.phoenix.utils;


import com.nash.phoenix.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class DbRequesthandler { //handles search and similar requests to database
    private Connection con; //Connection to database
    private ObservableList<Track> results; //results of last search
    private String tableName;

    public DbRequesthandler(Connection con, String tableName){
        this.con = con;
        this.tableName = tableName;
        results = FXCollections.observableArrayList();
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

    public Track requestAutoDJTrack(Track t, int mode) { //returns a track that is similar to the current track in tracklistView
        long dur = System.currentTimeMillis();
        String currentIds = App.tracklist.getCurrentIds();

        try{
            Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = null;
            if(mode==1){
                //Random track considering bpm (dif<=30) and rating (!=1 OR 2)
                rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE (bpm BETWEEN "+(t.getBpm()-30)+" AND "+(t.getBpm()+30)+") AND (rating NOT IN (1, 2)) AND (ID NOT IN "+currentIds+")");
            }else if(mode==2){
                //random track considering bpm (dif<=15) and rating (>3) -> !0
                rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE (bpm BETWEEN "+(t.getBpm()-15)+" AND "+(t.getBpm()+15)+") AND rating>=3 AND (ID NOT IN "+currentIds+")");
            }else if(mode==3){
                //random track from albumartist->artist->album considering rating(!=1 OR 2)
                String art = t.getArtist();
                String aart = t.getAlbumartist();
                String alb = t.getAlbum();

                if(aart!=null && !aart.startsWith("Various")){
                    rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE (LOWER(albumartist)='"+aart.toLowerCase()+"') AND (rating NOT IN (1, 2)) AND (ID NOT IN "+currentIds+")");
                }else if(art!=null && !art.startsWith("Various")){
                    rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE (LOWER(artist)='"+art.toLowerCase()+"') AND (rating NOT IN (1, 2)) AND (ID NOT IN "+currentIds+")");
                }else if(alb!=null) {
                    rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE (LOWER(album)='"+alb.toLowerCase()+"') AND (rating NOT IN (1, 2)) AND (ID NOT IN "+currentIds+")");
                }else{
                    System.out.println("ERROR while trying to get an AutoDJ Track: Current track <"+t.getPath()+"> does not have an artist/albumartist/album");
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

    private String getSQLSafeVersion(String str){ //returns a SQL safe version of a string (e.g. escaped '->'')
        if(str.contains("'")){
            str = str.replace("'", "''");
        }
        return str;
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

}
