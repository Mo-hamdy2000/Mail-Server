package GUI;

import Classes.App;
import Misc.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Signin implements Initializable  {

    private App app;

    protected void setApp(App app) {
        this.app = app;
    }

    @FXML
    private TextField addressField, passwordField;
    @FXML
    private Label invalidSigninLabel;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Hyperlink loadSignup;
    @FXML
    private Button signinButton;
    
    @FXML
    private void signin() {
        if (!app.signin(addressField.getText().toLowerCase(), passwordField.getText())) {
            invalidSigninLabel.setText("Invalid email address or password");
            passwordField.setText("");
        } else {
        	invalidSigninLabel.setText("");
            passwordField.setText("");
            AnchorPane pane = null;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
            try {
				pane = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
				Utils.fileNotFound();
			}
            HomeController home = loader.getController();
            home.initialize(app);
            rootPane.getChildren().setAll(pane);
            rootPane.getScene().getWindow().setHeight(722);
            rootPane.getScene().getWindow().setWidth(1175);
            rootPane.getScene().getWindow().centerOnScreen();
        }
    }

    @FXML
    private void loadSignup() {
        AnchorPane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
            pane = loader.load();
            Signup signupController = loader.getController();
            signupController.setApp(app);
        } catch (IOException e) {
            Utils.fileNotFound();
        }
        rootPane.getChildren().setAll(pane);
        rootPane.getScene().getWindow().setHeight(723);
        rootPane.getScene().getWindow().setWidth(629);
        rootPane.getScene().getWindow().centerOnScreen();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		signinButton.setDefaultButton(true);
		
	}

}