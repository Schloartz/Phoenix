package utils;

import application.service.Main;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class CustomTableColumn<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
	
    public CustomTableColumn() {

    }

	@Override
	public TableCell<S, T> call(TableColumn<S, T> arg0) {

		return new TableCell<S, T>() {
           @Override
           protected void updateItem(T item, boolean empty) {
               super.updateItem(item, empty);
               if(item==null){
                   setText(null);
                   setContextMenu(null);
                   setGraphic(null);
               }else{
                   if(getTableColumn().getText().equals("RATING")){ //for rating cells
                       setGraphic(getRatingstars(Integer.parseInt(item.toString())));
                   }else{ //for text cells
                       //Text
                       setGraphic(null);
                       setText(item.toString());
                       //Font
                       if(getTableRow()!=null && getTableRow().getItem()!=null){ //throws nullpointerexception on empty cells (Warum-auch-immer?)
                           if(Main.tracklist.isInTracklist((Track) getTableRow().getItem())){
                               setStyle("-fx-font-weight: bold;");
                           }else{
                               setStyle("-fx-font-weight: normal;");
                           }
                       }
                   }
                   //for all cells
                   setContextMenu(Main.contextMenu);
                   addEventFilter(MouseEvent.MOUSE_CLICKED, e -> { //add to Tracklist
                       if (e.getClickCount() > 1) {
                           Main.tracklist.addTrack(Main.mainController.lastSelected);
                       }
                   });
               }
           }
        };
	    }
	
		private HBox getRatingstars(int rating){ //return an HBox containing 5 images corresponding to the rating of the song, e.g. 3 = XXX00
			HBox hb = new HBox();
            for(int i=1;i<=5;i++){
                Star star = new Star(8,3.6);
                star.setStrokeWidth(0.5);
                star.setStroke(Color.BLACK);
                if(i<rating){
                    star.setFill(Color.BLACK);
                }else{
                    star.setFill(Color.TRANSPARENT);
                }
                hb.getChildren().add(star);
            }
			return hb;
		}
	}
