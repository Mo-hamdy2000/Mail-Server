package GUI;

import Classes.DoublyLinkedList;
import Classes.App;
import Classes.Contact;
import Classes.User;
import Misc.Utils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class Contacts implements Initializable {

	private final String sep = System.getProperty("file.separator");
    private App app;
    private User user;
    
    @FXML
    private Button sortBtn;
    @FXML
    private Hyperlink back;
    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Contact> tv;
    @FXML
    private TableColumn<String, Contact> tcName, tcAddress;
    @FXML
    private ComboBox<String> sortCombo, searchCombo;
    @FXML
    private TextField searchField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAddress.setCellValueFactory(new PropertyValueFactory<>("mainAddress"));
        tv.setRowFactory(tv -> {
            TableRow<Contact> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Contact rowData = row.getItem();
                    edit(rowData);
                }
            });
            return row;
        });

    }

    protected void setUp() {
        DoublyLinkedList list = user.getContacts();
        Iterator<Object> iter = list.iterator(true);
        tv.getItems().clear();
        while (iter.hasNext()) {
            tv.getItems().add((Contact) iter.next());
        }
    }

    protected void setApp(App app) {
        this.app = app;
        user = app.getLoggedinUser();
        setUp();
    }

    @FXML
    private void add() {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addContact.fxml"));
            root = loader.load();
            AddContact controller = loader.getController();
            controller.setApp(app, this);
        } catch (IOException e) {
            Utils.fileNotFound();
        }
        Stage stage = new Stage();
        stage.setTitle("Add Contact");
        //stage.getIcons().add(new Image("/icon.png"));
        stage.sizeToScene();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.getScene().getWindow().centerOnScreen();
        try {
            stage.getIcons().add(new Image("icon.png"));
        } catch (Exception e) {
            stage.getIcons().add(new Image("GUI" + sep + "icon.png"));
        }
        stage.show();
    }

    @FXML
    private void del() {
        ObservableList<Contact> selected = tv.getSelectionModel().getSelectedItems();
        for (Contact c : selected) {
            c.delete();
        }
        setUp();
    }

    @FXML
    private void sort() {
        String val = (String) sortCombo.getValue();
        if (val == null) return;
        if (val.equals("by name")) {
            user.sortContactsByName();
            setUp();
        } else if (val.equals("by date added")) {
            user.sortContactsByIndex();
            setUp();
        }
    }

    @FXML
    private void search() {
        String text = searchField.getText();
        String val = (String) searchCombo.getValue();
        DoublyLinkedList res;
        if (text.isEmpty() || val == null) {
            setUp();
            return;
        } else if (val.equals("by name")) {
            res = user.getContactByName(text);
        } else {
            res = user.getContactByAddress(text);
        }
        tv.getItems().clear();
        Iterator<Object> iter = res.iterator(true);
        while (iter.hasNext()) {
            tv.getItems().add((Contact) iter.next());
        }
    }

    @FXML
    private void back() {
    	AnchorPane pane = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        try {
        	pane = loader.load();
        } catch (IOException e) {
            Utils.fileNotFound();
        }
        HomeController home = loader.getController();
        home.initialize(app);
        this.pane.getChildren().setAll(pane);
        this.pane.getScene().getWindow().setHeight(722);
        this.pane.getScene().getWindow().setWidth(1175);
        this.pane.getScene().getWindow().centerOnScreen();
    }

    private void edit(Contact contact) {

        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
            root = loader.load();
            Edit controller = loader.getController();
            controller.setApp(app, this, contact);
        } catch (IOException e) {
            Utils.fileNotFound();
        }
        Stage stage = new Stage();
        stage.setTitle("Edit Contact");
        try {
            stage.getIcons().add(new Image("icon.png"));
        } catch (Exception e) {
            stage.getIcons().add(new Image("GUI" + sep + "icon.png"));
        }
        stage.sizeToScene();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.getScene().getWindow().centerOnScreen();
        stage.show();


    }
}