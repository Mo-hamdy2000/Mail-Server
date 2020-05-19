package GUI;

import Classes.App;
import Classes.DoublyLinkedList;
import Classes.Folder;
import Classes.User;
import Misc.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

public class FolderChooser {
    private final String sep = System.getProperty("file.separator");
    private DoublyLinkedList mails;
    private App app;
    private User user;
    private Stage currentStage;
    private HomeController homeController;
    
    @FXML
    private ListView<String> list;
    @FXML
    private Button moveBtn;
    
    protected void setParameters(App app, DoublyLinkedList mails, Stage stage, HomeController homeController) {
    	this.mails = mails;
        this.app = app;
    	user = app.getLoggedinUser();
    	this.currentStage = stage;
    	this.homeController = homeController;
        setUp();
    }

    protected void setUp() {
        DoublyLinkedList dll = new DoublyLinkedList();
        dll.add("..");
        try {
            Scanner sc = new Scanner(new File(user.getFilePath() + sep + "inbox" + sep + "folders.txt"));
            while (sc.hasNext()) {
                dll.add(sc.nextLine());
            }
            sc.close();
        } catch (FileNotFoundException e) {
            Utils.fileNotFound();
        }
        Iterator<Object> iter = dll.iterator(true);
        list.getItems().clear();
        while (iter.hasNext()) {
            list.getItems().add(iter.next().toString());
        }
    }

    @FXML
    protected void move() {
        String selected = list.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        else if (selected == "..") {
        	app.moveEmails(mails, new Folder(user.getFilePath() + sep + "inbox"));
        }
        else {
        	app.moveEmails(mails, new Folder(user.getFilePath() + sep + "inbox" + sep + selected));
        }
        this.homeController.refresh();
        this.currentStage.close();
    }
}