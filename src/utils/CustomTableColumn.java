package utils;

import application.service.Main;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class CustomTableColumn<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
	
	private Image star_full, star_empty;
	
    public CustomTableColumn() {
    	star_full = new Image(getClass().getResourceAsStream("/resources/icons/ratingstar_full.png"));
		star_empty = new Image(getClass().getResourceAsStream("/resources/icons/ratingstar_empty.png"));
    }

	@Override
	public TableCell<S, T> call(TableColumn<S, T> arg0) {
		 TableCell<S, T> cell = new TableCell<S, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
            	super.updateItem((T) item, empty);
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
                }
            }
		 };
		 
    	cell.setContextMenu(Main.contextMenu);
    	cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> { //add to Tracklist
	        if (e.getClickCount() > 1) {
	            Main.tracklist.addTrack((Track) cell.getTableRow().getItem());
	        }
		});
    	
        return cell;
	    }
	
		private HBox getRatingstars(int stars){ //return an HBox containing 5 images corresponding to the rating of the song, e.g. 3 = XXX00
			HBox hb = new HBox();
			
			for(int i=0;i<stars;i++){
				hb.getChildren().add(new ImageView(star_full));
			}
			for(int l=stars;l<5;l++){
				hb.getChildren().add(new ImageView(star_empty));
			}
			for(Node i:hb.getChildren()){
				((ImageView) i).setFitHeight(15);
				((ImageView) i).setFitWidth(15);
			}
			return hb;
		}
	}
