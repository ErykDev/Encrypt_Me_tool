package org.encryptTool.controllers.tabController;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;

public abstract class TabController {
    @FXML
    VBox fileDropBox;

    @FXML
    Button startButton;

    ListView<File> fileListView;

    ObservableList<File> list;

    protected abstract void initEventHandler();
    protected abstract void initEventAction();

    static class XCell extends ListCell<File> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("X");

        public XCell() {
            super();

            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(event -> getListView().getItems().remove(getItem()));
        }

        @Override
        protected void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {

                if (item.getName().length() > 20){
                    label.setText("/..."+item.getName().substring(item.getName().length() - 17));
                } else
                    label.setText("/"+item.getName());

                setGraphic(hbox);
            }
        }
    }
}
