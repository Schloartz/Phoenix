package utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import application.service.Main;

/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied list of entries.
 * @author Caleb Brinkman (modified by Nash)
 */
public class AutoCompleteTextField extends TextField
{
  /** The popup used to select an entry. */
  private ContextMenu entriesPopup;

  /** Construct a new AutoCompleteTextField. */
  public AutoCompleteTextField() {
    super();
    entriesPopup = new ContextMenu();
    textProperty().addListener(new ChangeListener<String>()
    {
      @Override
      public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
        if (getText().length() == 0)
        {
          entriesPopup.hide();
        } else
        {
          ArrayList<String> searchResult = null;
          Main.database.autocompleteSearch(getText());
//          if (searchResult.size() > 0)
//          {
//            populatePopup(searchResult);
//            if (!entriesPopup.isShowing())
//            {
//              entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
//            }
//          } else
//          {
//            entriesPopup.hide();
//          }
        }
      }
    });

    focusedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
        entriesPopup.hide();
      }
    });
  }

  /**
   * Populate the entry set with the given search results.  Display is limited to 10 entries, for performance.
   * @param searchResult The set of matching strings.
   */
  private void populatePopup(ArrayList<String> searchResult) {
    List<CustomMenuItem> menuItems = new LinkedList<>();
    // If you'd like more entries, modify this line.
    for (String str:searchResult)
    {
      Label entryLabel = new Label(str);
      CustomMenuItem item = new CustomMenuItem(entryLabel, true);
      item.setOnAction(new EventHandler<ActionEvent>()
      {
        @Override
        public void handle(ActionEvent actionEvent) {
          setText(str);
          entriesPopup.hide();
        }
      });
      menuItems.add(item);
    }
    entriesPopup.getItems().clear();
    entriesPopup.getItems().addAll(menuItems);

  }
}
