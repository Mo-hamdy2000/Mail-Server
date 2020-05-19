package GUI;

import Classes.App;
import Classes.Contact;
import Misc.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddContact {

    private App app;
    private Contacts controller;
    
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label invalidName, invalidAddresses;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea addressesField;

    @FXML
    private void save() {
        String name = nameField.getText(), addresses = addressesField.getText().toLowerCase();
        boolean ok = true;
        if (name.isEmpty() || !Utils.validString(name)) {
            invalidName.setText("Invalid Name");
            ok = false;
        } else {
            invalidName.setText("");
        }
        String[] arr = null;
        if (!addresses.isEmpty()) {
            arr = addresses.split(",");
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i].trim();
            }
            int num = app.addressesExist(arr);
            if (num != arr.length) {
                invalidAddresses.setText("Address " + (num + 1) + " does not exist.");
                ok = false;
            } else {
                invalidAddresses.setText("");
            }
        }
        if (ok) {
            Contact contact = new Contact(name);
            contact.addAddresses(arr);
            app.getLoggedinUser().addContact(contact);
            controller.setUp();
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    public void setApp(App app, Contacts controller) {
        this.app = app;
        this.controller = controller;
    }

}