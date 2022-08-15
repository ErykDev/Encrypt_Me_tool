package org.encryptTool.controllers.tabController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import org.encryptTool.controllers.MainController;

import java.util.UUID;
import java.net.URL;
import java.util.ResourceBundle;

public  final class SettingsController implements Initializable {

    private final String STARTING_ALG = "AES";
    private final int STARTING_THREADS = 1;

    @FXML
    private TextArea keyArea;

    @FXML
    private ComboBox<String> algorithmChoiceBox;

    @FXML
    private ComboBox<String> threadsChoiceBox;

    @FXML
    private RadioButton del_files;

    @FXML
    private Button gen_key;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmChoiceBox.getItems().addAll("AES","RC2","RC4","Blowfish");

        algorithmChoiceBox.setValue(STARTING_ALG);
        MainController.ALGORITHM = STARTING_ALG;

        algorithmChoiceBox.setOnAction(event -> MainController.ALGORITHM = algorithmChoiceBox.getValue());

        threadsChoiceBox.getItems().addAll("1","2","4","8","16","32","64");

        threadsChoiceBox.setValue(String.valueOf(STARTING_THREADS));
        MainController.NUM_OF_TREADS = STARTING_THREADS;

        threadsChoiceBox.setOnAction(event -> MainController.NUM_OF_TREADS = Integer.parseInt(threadsChoiceBox.getValue()));

        gen_key.setOnMouseClicked(event -> {
            String key = UUID.randomUUID().toString();
            keyArea.setText(key);
            MainController.HASH_KEY = key;
        });

        keyArea.setWrapText(true);
        keyArea.textProperty().addListener((observable, oldValue, newValue) -> MainController.HASH_KEY = newValue);

        del_files.selectedProperty().addListener((observable, oldValue, newValue) -> MainController.DEL_Files = newValue);
    }
}
