package GUI;

import Classes.App;
import Classes.User;
import Misc.AES;
import Misc.Birthday;
import Misc.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Signup implements Initializable {

    private App app;

    protected void setApp(App app) {
        this.app = app;
    }

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField nameField, passwordField, confirmField, addressField;
    @FXML
    private Label invalidNameLabel, invalidAddressLabel, invalidAddressLabel2, invalidAddressLabel3,
    		invalidPasswordLabel, invalidPasswordLabel2, invalidConfirmLabel, invalidLabel, invalidBd;
    @FXML
    private ToggleGroup t1;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button signupBtn;

    @FXML
    private boolean checkName() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            invalidNameLabel.setText("");
            return false;
        }
        if (Utils.validString(name)) {
            invalidNameLabel.setTextFill(Color.web("#006600"));
            invalidNameLabel.setText("valid name");
            return true;
        }
        invalidNameLabel.setTextFill(Color.web("#ff0000"));
        invalidNameLabel.setText("invalid name");
        return false;

    }

    @FXML
    private boolean checkAddress() {
        String text = addressField.getText().toLowerCase();
        if (text.isEmpty()) {
            invalidAddressLabel.setText("");
            invalidAddressLabel2.setText("");
            invalidAddressLabel3.setText("");
            return false;
        }
        if (app.addressExists(text)) {
            invalidAddressLabel.setTextFill(Color.web("#ff0000"));
            invalidAddressLabel.setText("email address already exists!");
            invalidAddressLabel2.setText("try signing-in instead.");
            invalidAddressLabel3.setText("");
            return false;
        }
        if (Utils.validAddress(addressField.getText())) {
            invalidAddressLabel.setTextFill(Color.web("#006600"));
            invalidAddressLabel.setText("valid address");
            invalidAddressLabel2.setText("");
            invalidAddressLabel3.setText("");
            return true;
        }

        invalidAddressLabel.setTextFill(Color.web("#ff0000"));
        invalidAddressLabel.setText("invalid address");
        invalidAddressLabel2.setText("can only contain numbers, letters, periods or underscore");
        invalidAddressLabel3.setText("make sure to include \"@trio.com\"");
        return false;

    }

    @FXML
    private boolean checkPassword() {
        String password = passwordField.getText();
        if (password.isEmpty()) {
            invalidPasswordLabel.setText("");
            invalidPasswordLabel2.setText("");
            invalidConfirmLabel.setText("");
            return false;
        }
        if (!Utils.validPassword(password)) {
            invalidPasswordLabel.setText("invalid password");
            invalidPasswordLabel2.setText("must be between 6 and 30 characters with no spaces");
            invalidConfirmLabel.setText("");
            return false;
        }
        //valid password
        invalidPasswordLabel.setText("");
        invalidPasswordLabel2.setText("");
        return checkConfirm();

    }

    @FXML
    private boolean checkConfirm() {
        String password = confirmField.getText();
        if (password.isEmpty()) {
            invalidConfirmLabel.setText("");
            return false;
        }
        if (password.equals(passwordField.getText())) {
            invalidConfirmLabel.setTextFill(Color.web("#006600"));
            invalidConfirmLabel.setText("passwords match");
            return true;
        }
        //password not matching
        invalidConfirmLabel.setTextFill(Color.web("#ff0000"));
        invalidConfirmLabel.setText("passwords are not matching");
        return false;

    }
    
    @FXML
    private boolean checkBd() {
        LocalDate ld = datePicker.getValue();
        if (ld == null) {
            invalidBd.setText("");
            return false;
        }
        Birthday bd = new Birthday(ld.getDayOfMonth(), ld.getMonthValue(), ld.getYear());
        if (!bd.valid()) {
            invalidBd.setText("invalid birthday");
            return false;
        }
        invalidBd.setText("");
        return true;
    }
    
    @FXML
    private void callSignup() {

        if (!allValid()) {
            invalidLabel.setText("invalid information");
        } else {
            LocalDate ld = datePicker.getValue();
            String password = passwordField.getText();
            User user = new User(addressField.getText().toLowerCase(), AES.encrypt(password, password));
            user.setName(nameField.getText());
            user.setGender(((RadioButton) t1.getSelectedToggle()).getText());
            user.setBirthday(new Birthday(ld.getDayOfMonth(), ld.getMonthValue(), ld.getYear()));
            if (!app.signup(user)) Utils.fileNotFound();
            invalidLabel.setText("");
            AnchorPane pane = null;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
            try {
				pane = loader.load();
			} catch (IOException e) {
				Utils.fileNotFound();
			}
            HomeController home = loader.getController();
            home.initialize(app);
            rootPane.getChildren().setAll(pane);
            rootPane.getScene().getWindow().setHeight(722);
            rootPane.getScene().getWindow().setWidth(1175);
            rootPane.getScene().getWindow().centerOnScreen();
        }
        passwordField.setText("");
        confirmField.setText("");
        invalidPasswordLabel.setText("");
        invalidPasswordLabel2.setText("");
        invalidConfirmLabel.setText("");
    }

    @FXML
    private void loadSignin() {
        AnchorPane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signin.fxml"));
            pane = loader.load();
            Signin controller = loader.getController();
            controller.setApp(app);
        } catch (IOException e) {
            Utils.fileNotFound();
        }
        rootPane.getChildren().setAll(pane);
        rootPane.getScene().getWindow().setHeight(437);
        rootPane.getScene().getWindow().setWidth(629);
        rootPane.getScene().getWindow().centerOnScreen();
    }

    private boolean allValid() {
    	return checkName() && checkAddress() && checkPassword() && checkConfirm() && checkBd() && datePicker.getValue() != null;
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		signupBtn.setDefaultButton(true);
	}


}