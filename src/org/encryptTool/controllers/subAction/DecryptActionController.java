package org.encryptTool.controllers.subAction;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.encryptTool.crypt.FileCipher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DecryptActionController implements Initializable {
    private ObservableList<File> list;

    private final String hashKey;
    private final String algorithm;
    private ExecutorService pool;
    private boolean delete_files;

    @FXML
    private AnchorPane panel;

    public DecryptActionController(ObservableList<File> list, String hashKey, String algorithm, int thread_pool, boolean delete_files){
        this.list = list;
        this.hashKey = hashKey;
        this.algorithm = algorithm;
        this.pool = Executors.newFixedThreadPool(thread_pool, runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        this.delete_files = delete_files;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initList();
    }

    void initList() {
        TableView<DecryptTask> table = new TableView<DecryptTask>();

        for (File file : list)
            table.getItems().add(new DecryptTask(file, delete_files,hashKey,algorithm));

        TableColumn<DecryptTask, String> statusCol = new TableColumn("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>(
                "message"));
        statusCol.setPrefWidth(75);

        TableColumn<DecryptTask, Double> progressCol = new TableColumn("Progress");
        progressCol.setCellValueFactory(new PropertyValueFactory<>(
                "progress"));

        progressCol.setCellFactory(ProgressBarTableCell.<DecryptTask> forTableColumn());

        TableColumn<DecryptTask, String> fileNameCol = new TableColumn("File");
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>(
                "title"));

        table.getColumns().addAll(fileNameCol, statusCol, progressCol);

        table.setPrefWidth(370);
        table.setPrefHeight(420);

        panel.getChildren().add(table);

        for (DecryptTask task : table.getItems())
            pool.execute(task);
    }

    static class DecryptTask extends Task<Void> {

        private File in;
        private boolean delete;
        private final String hashKey;
        private final String algorithm;

        DecryptTask(File in, boolean delete, String hashKey, String algorithm) {
            this.in = in;
            this.delete = delete;
            this.hashKey = hashKey;
            this.algorithm = algorithm;

            this.updateMessage("Waiting...");
            this.updateTitle(in.getName());


            if (in.getName().length() > 20)
                this.updateTitle("/..."+in.getName().substring(in.getName().length() - 17));
            else
                this.updateTitle(in.getName());

            this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
            this.updateProgress(0, 1);
        }

        @Override
        protected Void call() throws Exception {
            this.updateMessage("Running...");

            try {
                FileCipher.decrypt(in, new File(in.toPath().toString().replace(".bin", "")),hashKey,algorithm,
                        status ->  updateProgress(status, 100));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (delete) {
                this.updateMessage("Deleting File");
                in.delete();
            }

            this.updateMessage("Done");
            this.updateProgress(1, 1);
            return null;
        }
    }
}
