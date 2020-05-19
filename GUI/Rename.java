package GUI;

import Classes.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;


public class Rename {

	private final String sep = System.getProperty("file.separator");
    private App app;
    private Folders foldersController;
    private String dir;

    @FXML
    private Button cancelBtn;
    @FXML
    private Button renameBtn;
    @FXML
    private TextField nameField;
    @FXML
    private Label invalid;

    protected void setApp(App app, Folders controller, String dir) {
        this.app = app;
        foldersController = controller;
        this.dir = dir;
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void rename() {
        File old = new File(app.getLoggedinUser().getFilePath() + sep + "inbox" + sep + dir);
        File f = new File(app.getLoggedinUser().getFilePath() + sep + "inbox" + sep + nameField.getText());
        if (!old.renameTo(f)) invalid.setText("invalid folder name");
        else {
            invalid.setText("");
            foldersController.setUp();
            app.getLoggedinUser().editFolders();
            cancel();
        }
    }

}