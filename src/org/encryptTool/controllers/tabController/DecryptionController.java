package org.encryptTool.controllers.tabController;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import org.encryptTool.controllers.MainController;
import org.encryptTool.controllers.subAction.DecryptActionController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public final class DecryptionController extends TabController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list = FXCollections.observableArrayList();
        fileListView = new ListView<>(list);

        initEventHandler();
        initEventAction();

        fileListView.setCellFactory(param -> new XCell());
        fileListView.setStyle("-fx-background-insets: 0;");
        fileDropBox.getChildren().add(fileListView);
    }

    @Override
    protected void initEventHandler() {
        fileDropBox.setOnDragOver(event -> {
            if (event.getGestureSource() != fileDropBox
                    && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
    }

    @Override
    protected void initEventAction(){
        fileDropBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                for (File f: db.getFiles()) {
                    if (f.isDirectory())
                        try {
                            List<File> files = Files.walk(Paths.get(f.getPath())).map(Path::toFile)
                                    .filter(file -> file.isFile() && file.canRead() && file.getName().endsWith(".bin"))
                                    .collect(Collectors.toList());

                            for (File sub_file: files)
                                if (!list.contains(sub_file))
                                    list.add(sub_file);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    else
                    if (!list.contains(f) && f.getName().contains(".bin"))
                        list.add(f);
                }
                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);
            event.consume();
        });

        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if(MainController.HASH_KEY.isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.NONE, "Encryption key is Empty", ButtonType.OK);
                    alert.show();
                    return;
                }

                Stage stage = new Stage();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/display_form.fxml"));
                fxmlLoader.setController(new DecryptActionController(list, MainController.HASH_KEY, MainController.ALGORITHM, MainController.NUM_OF_TREADS, MainController.DEL_Files));
                Parent root = null;
                try {
                    root = fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Scene scene = new Scene(root, 360, 410);
                stage.setScene(scene);
                stage.show();
                stage.setResizable(false);

                list.clear();
            }
        });
    }
}